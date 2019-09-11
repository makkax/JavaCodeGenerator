package com.cc.jcg.jdbc.generator;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.cc.jcg.MClass;
import com.cc.jcg.MField;
import com.cc.jcg.MField.AccessorMethods;
import com.cc.jcg.MFunctions;
import com.cc.jcg.MMethod;
import com.cc.jcg.MPackage;
import com.cc.jcg.jdbc.JdbcCriteria;
import com.cc.jcg.jdbc.JdbcCriteriaBase;
import com.cc.jcg.jdbc.SQLTypeMap;

public class MJdbcCriteriaGenerator {

    public static final Predicate<String> ALL_TABLES = new Predicate<String>() {

	@Override
	public boolean test(String t) {
	    return true;
	}
    };
    public static final Predicate<MJdbcColumnDef> ALL_COLUMNS = new Predicate<MJdbcColumnDef>() {

	@Override
	public boolean test(MJdbcColumnDef t) {
	    return true;
	}
    };
    private final MPackage pckg;
    private String types[] = { "TABLE", "VIEW" };
    private final Consumer<MJdbcColumnDef> visitor;

    public MJdbcCriteriaGenerator(MPackage pckg, String[] types, Consumer<MJdbcColumnDef> visitor) {
	super();
	this.pckg = pckg;
	this.types = types;
	this.visitor = visitor;
    }

    public MJdbcCriteriaGenerator(MPackage pckg, Consumer<MJdbcColumnDef> visitor) {
	super();
	this.pckg = pckg;
	this.visitor = visitor;
    }

    public MJdbcCriteriaGenerator(MPackage pckg) {
	super();
	this.pckg = pckg;
	this.visitor = null;
    }

    public void generateJdbcCriterias(Connection connection) throws SQLException, ClassNotFoundException {
	generateJdbcCriterias(connection, ALL_TABLES, ALL_COLUMNS);
    }

    public void generateJdbcCriterias(Connection connection, Predicate<String> tablesFilter) throws SQLException, ClassNotFoundException {
	generateJdbcCriterias(connection, tablesFilter, ALL_COLUMNS);
    }

    public void generateJdbcCriterias(Connection connection, Predicate<String> tablesFilter, Predicate<MJdbcColumnDef> columnsFilter) throws SQLException, ClassNotFoundException {
	DatabaseMetaData metaData = connection.getMetaData();
	ResultSet rs = metaData.getTables(null, null, null, types);
	while (rs.next()) {
	    // -----------------------------------------------------------------------
	    // for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
	    // System.out.println(rs.getMetaData().getColumnName(i));
	    // }
	    // -----------------------------------------------------------------------
	    // TABLE_CAT
	    // TABLE_SCHEM
	    // TABLE_NAME
	    // TABLE_TYPE
	    // REMARKS
	    // -----------------------------------------------------------------------
	    StringBuffer name = new StringBuffer();
	    String catalog = rs.getString("TABLE_CAT");
	    if (catalog != null) {
		name.append(catalog);
	    }
	    String schema = rs.getString("TABLE_SCHEM");
	    if (schema != null) {
		name.append(name.toString().isEmpty() ? "" : ".");
		name.append(schema);
	    }
	    String tableName = rs.getString("TABLE_NAME");
	    name.append(name.toString().isEmpty() ? "" : ".");
	    name.append(tableName);
	    if (tablesFilter.test(name.toString())) {
		generateJdbcCriteria(tableName, connection, columnsFilter);
	    }
	}
    }

    public void generateJdbcCriteria(String tableName, Connection connection) throws SQLException, ClassNotFoundException {
	generateJdbcCriteria(tableName, connection, ALL_COLUMNS);
    }

    public void generateJdbcCriteria(String tableName, Connection connection, Predicate<MJdbcColumnDef> columnsFilter) throws SQLException, ClassNotFoundException {
	Collection<MJdbcColumnDef> columnDefs = new ArrayList<>();
	DatabaseMetaData metaData = connection.getMetaData();
	ResultSet rs = metaData.getColumns(null, null, tableName, null);
	while (rs.next()) {
	    // -----------------------------------------------------------------------
	    // for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
	    // System.out.println(rs.getMetaData().getColumnName(i));
	    // }
	    // -----------------------------------------------------------------------
	    // TABLE_CAT
	    // TABLE_SCHEM
	    // TABLE_NAME
	    // COLUMN_NAME
	    // DATA_TYPE
	    // TYPE_NAME
	    // COLUMN_SIZE
	    // BUFFER_LENGTH
	    // DECIMAL_DIGITS
	    // NUM_PREC_RADIX
	    // NULLABLE
	    // REMARKS
	    // COLUMN_DEF
	    // SQL_DATA_TYPE
	    // SQL_DATETIME_SUB
	    // CHAR_OCTET_LENGTH
	    // ORDINAL_POSITION
	    // IS_NULLABLE
	    // -----------------------------------------------------------------------
	    // System.out.println(rs.getString("TABLE_NAME") + "." + rs.getString("COLUMN_NAME") + "," + rs.getString("DATA_TYPE") + "," + rs.getString("TYPE_NAME") + "," + rs.getString("COLUMN_DEF") + "," + rs.getString("SQL_DATA_TYPE"));
	    String columnName = rs.getString("COLUMN_NAME");
	    Class<?> javaType = SQLTypeMap.toClass(rs.getInt("DATA_TYPE"));
	    MJdbcColumnDefImpl col = new MJdbcColumnDefImpl(columnName, javaType);
	    if (columnsFilter.test(col)) {
		columnDefs.add(col);
	    }
	}
	generateJdbcCriteria(tableName, columnDefs);
    }

    public void generateJdbcCriteria(String tableName, Collection<MJdbcColumnDef> columnDefs) throws ClassNotFoundException {
	if (visitor != null) {
	    columnDefs.forEach(cd -> visitor.accept(cd));
	}
	MClass cls = pckg.newClass(JdbcCriteria.class, MFunctions.camelize(MFunctions.labelize(tableName).replace(" ", "")));
	cls.setSuperclass(JdbcCriteriaBase.class);
	cls.overrideConstructors();
	MMethod addColumns = cls.addMethod("addColumns", void.class).makeProtected().overrides();
	StringBuffer addColumnsCode = new StringBuffer();
	for (MJdbcColumnDef cdef : columnDefs) {
	    if (!cdef.isMultipleValues().get()) {
		String fieldName = MFunctions.fieldname(MFunctions.labelize(cdef.getColumnName()).replace(" ", ""));
		MField fld = cls.addField(cdef.getValueType(), fieldName);
		AccessorMethods accessors = fld.addAccessorMethods();
		addColumnsCode.append("addJdbcColumnValue(\"" + cdef.getColumnName() + "\", " + cdef.getValueType().getName() + ".class, this::" + accessors.getter().getName() + ", this::" + accessors.setter().getName() + ");\n");
	    } else {
		String fieldName = MFunctions.fieldname(MFunctions.labelize(cdef.getColumnName()).replace(" ", ""));
		MField fld = cls.addField(List.class, fieldName).setGeneric(cdef.getValueType());
		AccessorMethods accessors = fld.addAccessorMethods();
		addColumnsCode.append("addJdbcColumnValues(\"" + cdef.getColumnName() + "\", " + cdef.getValueType().getName() + ".class, this::" + accessors.getter().getName() + ", this::" + accessors.setter().getName() + ");\n");
	    }
	}
	addColumns.setBlockContent(addColumnsCode);
    }
}
