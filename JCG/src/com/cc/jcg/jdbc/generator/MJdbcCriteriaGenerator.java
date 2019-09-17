package com.cc.jcg.jdbc.generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.cc.jcg.MClass;
import com.cc.jcg.MField;
import com.cc.jcg.MField.AccessorMethods;
import com.cc.jcg.MFunctions;
import com.cc.jcg.MMethod;
import com.cc.jcg.MPackage;
import com.cc.jcg.MParameter;
import com.cc.jcg.jdbc.JdbcCriteria;
import com.cc.jcg.jdbc.JdbcCriteriaBase;
import com.cc.jcg.jdbc.QueryByCriteria;
import com.cc.jcg.jdbc.QueryExecutor;
import com.cc.jcg.jdbc.connection.ConnectionProperties;

public class MJdbcCriteriaGenerator {

    public static final Predicate<String> ALL_TABLES = t -> true;
    public static final Predicate<MJdbcColumnDef> ALL_COLUMNS = t -> true;
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

    public final MJdbcCriteriaGenerator considerTablesAndViews() {
	types = new String[] { "TABLE", "VIEW" };
	return this;
    }

    public final MJdbcCriteriaGenerator considerTablesOnly() {
	types = new String[] { "TABLE" };
	return this;
    }

    public final MJdbcCriteriaGenerator considerViewsOnly() {
	types = new String[] { "VIEW" };
	return this;
    }

    public Properties extractAllTableNames(Connection connection) throws SQLException {
	Properties props = new Properties();
	DatabaseMetaData metaData = connection.getMetaData();
	ResultSet rs = metaData.getTables(null, null, null, types);
	while (rs.next()) {
	    StringBuffer name = getLongTableName(rs);
	    props.put(name.toString(), "false");
	}
	return props;
    }

    private StringBuffer getLongTableName(ResultSet rs) throws SQLException {
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
	return name;
    }

    private static Predicate<String> toTablePredicate(Properties props) {
	return name -> {
	    if (props.containsKey(name)) {
		return props.getProperty(name).toLowerCase().equals("true");
	    }
	    return false;
	};
    }

    public ConnectionProperties updateAllTableNames(Connection connection, File propertiesFile) throws FileNotFoundException, IOException, SQLException {
	if (!propertiesFile.exists()) {
	    new Properties().store(new FileOutputStream(propertiesFile), null);
	}
	ConnectionProperties stored = new ConnectionProperties(propertiesFile, true);
	Properties current = extractAllTableNames(connection);
	stored.keySet().retainAll(current.keySet());
	current.keySet().removeAll(stored.keySet());
	stored.putAll(current);
	stored.save();
	return stored;
    }

    public void generateJdbcCriterias(Connection connection) throws SQLException, ClassNotFoundException {
	generateJdbcCriterias(connection, ALL_TABLES, ALL_COLUMNS);
    }

    public void generateJdbcCriterias(Connection connection, Predicate<String> tablesFilter) throws SQLException, ClassNotFoundException {
	generateJdbcCriterias(connection, tablesFilter, ALL_COLUMNS);
    }

    public void generateJdbcCriterias(Connection connection, Properties tables) throws SQLException, ClassNotFoundException {
	generateJdbcCriterias(connection, toTablePredicate(tables), ALL_COLUMNS);
    }

    public void generateJdbcCriterias(Connection connection, Predicate<String> tablesFilter, Predicate<MJdbcColumnDef> columnsFilter) throws SQLException, ClassNotFoundException {
	DatabaseMetaData metaData = connection.getMetaData();
	ResultSet rs = metaData.getTables(null, null, null, types);
	while (rs.next()) {
	    // -------------------------------------------------------------------------------------------
	    // for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
	    // System.out.println(rs.getMetaData().getColumnName(i));
	    // }
	    // -------------------------------------------------------------------------------------------
	    // TABLE_CAT
	    // TABLE_SCHEM
	    // TABLE_NAME
	    // TABLE_TYPE
	    // REMARKS
	    // -------------------------------------------------------------------------------------------
	    StringBuffer name = getLongTableName(rs);
	    if (tablesFilter.test(name.toString())) {
		System.out.println("x " + name);
		generateJdbcCriteria(rs.getString("TABLE_CAT"), rs.getString("TABLE_SCHEM"), rs.getString("TABLE_NAME"), connection, columnsFilter);
	    } else {
		System.out.println("- " + name);
	    }
	}
    }

    public void generateJdbcCriteria(String tableName, Connection connection) throws SQLException, ClassNotFoundException {
	generateJdbcCriteria(null, null, tableName, connection, ALL_COLUMNS);
    }

    public void generateJdbcCriteria(String catalog, String schema, String table, Connection connection, Predicate<MJdbcColumnDef> columnsFilter) throws SQLException, ClassNotFoundException {
	Collection<MJdbcColumnDef> columnDefs = new ArrayList<>();
	DatabaseMetaData metaData = connection.getMetaData();
	ResultSet rs = metaData.getColumns(catalog, schema, table, null);
	while (rs.next()) {
	    // -------------------------------------------------------------------------------------------
	    // for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
	    // System.out.println(rs.getMetaData().getColumnName(i));
	    // }
	    // -------------------------------------------------------------------------------------------
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
	    // -------------------------------------------------------------------------------------------
	    // System.out.println(rs.getString("TABLE_NAME") + "." + rs.getString("COLUMN_NAME") + ","
	    // + rs.getString("DATA_TYPE") + "," + rs.getString("TYPE_NAME") + ","
	    // + rs.getString("COLUMN_DEF") + "," + rs.getString("SQL_DATA_TYPE"));
	    // -------------------------------------------------------------------------------------------
	    String columnName = rs.getString("COLUMN_NAME");
	    Class<?> javaType = SQLTypeMap.toClass(rs.getInt("DATA_TYPE"));
	    MJdbcColumnDefImpl col = new MJdbcColumnDefImpl(columnName, javaType);
	    if (columnsFilter.test(col)) {
		columnDefs.add(col);
	    }
	}
	generateJdbcCriteria(table, columnDefs);
    }

    public synchronized void generateJdbcCriteria(String tableName, Collection<MJdbcColumnDef> columnDefs) throws ClassNotFoundException {
	// -------------------------------------------------------------------------------------------------------------------------
	if (visitor != null) {
	    columnDefs.forEach(cd -> visitor.accept(cd));
	}
	String clsName = MFunctions.camelize(MFunctions.labelize(MFunctions.constantize(tableName)).replace(" ", ""));
	MClass cls = pckg.newClass(JdbcCriteria.class, clsName.concat("Criteria"));
	cls.setSuperclass(JdbcCriteriaBase.class);
	cls.overrideConstructors().forEach(cstr -> {
	    if (cstr.getParameters().isEmpty()) {
		cstr.setBlockContent("this(\"" + tableName + "\");");
	    }
	});
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
		MMethod single = cls.addMethod(MFunctions.singular(accessors.setter().getName()), void.class, new MParameter(cdef.getValueType(), "value"));
		single.setFinal(true);
		single.setBlockContent(accessors.setter().getName() + "(Collections.singletonList(value));");
		single.addImport(Collections.class);
	    }
	}
	addColumns.setBlockContent(addColumnsCode);
	// -------------------------------------------------------------------------------------------------------------------------
	String entityName = MFunctions.camelize(MFunctions.labelize(MFunctions.constantize(tableName)).replace(" ", ""));
	MClass entity = pckg.newClass(entityName.concat("Entity"));
	StringBuffer filler = new StringBuffer();
	for (MJdbcColumnDef cdef : columnDefs) {
	    String fieldName = MFunctions.fieldname(MFunctions.labelize(cdef.getColumnName()).replace(" ", ""));
	    MField fld = entity.addField(cdef.getValueType(), fieldName);
	    AccessorMethods accessors = fld.addAccessorMethods();
	    // e.setActive(rs.getBoolean(columns.get("ACTIVE")));
	    String getRs = MFunctions.camelize(fld.getType().getSimpleName());
	    String getCol = cdef.getColumnName();
	    filler.append("e." + accessors.setter().getName() + "(rs.get" + getRs + "(columns.get(\"" + getCol + "\")));\n");
	}
	cls.addInterface(QueryByCriteria.class, entity);
	// -------------------------------------------------------------------------------------------------------------------------
	MClass executor = pckg.newClass(entityName.concat("QueryExecutor"));
	executor.setSuperclass(QueryExecutor.class, entity);
	executor.overrideConstructors();
	MMethod newEntity = executor.addMethod("newEntity", entity).overrides();
	newEntity.setBlockContent("return new " + entity.getName() + "();");
	// T e, ResultSet rs, Map<String, Integer> columns
	List<MParameter> parameters = new LinkedList<>();
	parameters.add(new MParameter(entity, "e"));
	parameters.add(new MParameter(ResultSet.class, "rs"));
	parameters.add(new MParameter(Map.class, "<String, Integer>", "columns"));
	MMethod fillEntity = executor.addMethod("fillEntity", void.class, parameters).overrides();
	fillEntity.throwsException(SQLException.class);
	fillEntity.setBlockContent(filler);
	cls.addField(executor, "executor").setFinal(true).setValue("new " + executor.getName() + "(this);");
	// -------------------------------------------------------------------------------------------------------------------------
	MMethod executeQuery = cls.addMethod("executeQuery", Collection.class, new MParameter(Connection.class, "connection"));
	executeQuery.setGenericReturnType("<" + entity.getName() + ">").overrides();
	executeQuery.throwsException(SQLException.class);
	executeQuery.setBlockContent("return executor.executeQuery(connection);");
	// -------------------------------------------------------------------------------------------------------------------------
	MMethod executeQuery2 = cls.addMethod("executeQuery", void.class, new MParameter(Connection.class, "connection"), new MParameter(Consumer.class, "<" + entity.getName() + ">", "consumer"));
	executeQuery2.throwsException(SQLException.class);
	executeQuery2.setBlockContent("executor.executeQuery(connection, consumer);");
	// -------------------------------------------------------------------------------------------------------------------------
	MMethod count = cls.addMethod("count", long.class, new MParameter(Connection.class, "connection"));
	count.overrides();
	count.throwsException(SQLException.class);
	count.setBlockContent("return executor.count(connection);");
	// -------------------------------------------------------------------------------------------------------------------------
    }
}
