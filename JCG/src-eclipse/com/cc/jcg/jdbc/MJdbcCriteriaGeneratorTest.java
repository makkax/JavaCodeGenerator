package com.cc.jcg.jdbc;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.cc.jcg.MBundle;
import com.cc.jcg.MPackage;
import com.cc.jcg.jdbc.generator.MJdbcColumnDef;
import com.cc.jcg.jdbc.generator.MJdbcColumnDefImpl;
import com.cc.jcg.jdbc.generator.MJdbcCriteriaGenerator;

class MJdbcCriteriaGeneratorTest {

    @Test
    void test1() throws Exception {
	// ----------------------------------------------------------------------------------------------------------------
	MBundle.EXCLUDE_GENERATED_ANNOTATION.set(true);
	MBundle.GENERATE_READONLY.set(false);
	// ----------------------------------------------------------------------------------------------------------------
	MBundle bundle = new MBundle(new File("src-generated"));
	MPackage pckg = bundle.newPackage("com.cc.jcg.jdbc");
	// ----------------------------------------------------------------------------------------------------------------
	MJdbcCriteriaGenerator generator = new MJdbcCriteriaGenerator(pckg);
	Collection<MJdbcColumnDef> columnDefs = new ArrayList<>();
	columnDefs.add(new MJdbcColumnDefImpl("FIRST_NAME", String.class));
	columnDefs.add(new MJdbcColumnDefImpl("LAST_NAME", String.class));
	columnDefs.add(new MJdbcColumnDefImpl("DATE", Date.class));
	columnDefs.add(new MJdbcColumnDefImpl("ACTIVE", boolean.class));
	columnDefs.add(new MJdbcColumnDefImpl("ITEMS", String.class, true));
	generator.generateJdbcCriteria("Simple", columnDefs);
	// ----------------------------------------------------------------------------------------------------------------
	bundle.generateCode(true);
	// ----------------------------------------------------------------------------------------------------------------
    }

    @Test
    void test2() throws Exception {
	// ----------------------------------------------------------------------------------------------------------------
	// gen.src.dir, gen.pckg
	// jdbc.url, jdbc.username, jdbc.password
	// table.names
	// ----------------------------------------------------------------------------------------------------------------
	ConnectionProperties properties = new ConnectionProperties("database-connection.properties");
	// ----------------------------------------------------------------------------------------------------------------
	MBundle.EXCLUDE_GENERATED_ANNOTATION.set(true);
	MBundle.GENERATE_READONLY.set(false);
	// ----------------------------------------------------------------------------------------------------------------
	MBundle bundle = new MBundle(new File(properties.getProperty("gen.src.dir")));
	MPackage pckg = bundle.newPackage(properties.getProperty("gen.pckg"));
	// ----------------------------------------------------------------------------------------------------------------
	MJdbcCriteriaGenerator generator = new MJdbcCriteriaGenerator(pckg).considerTablesOnly();
	Connection connection = new JDBCConnectionProviderOracle(properties).getNewConnection();
	List<String> tables = Arrays.asList(properties.getProperty("table.names").split(","));
	generator.generateJdbcCriterias(connection, name -> {
	    if (name.startsWith(properties.getJdbcUsername() + ".")) {
		for (String table : tables) {
		    if (table.equals(name)) {
			return true;
		    } else if (name.matches(table)) {
			return true;
		    }
		}
	    }
	    return false;
	});
	// ----------------------------------------------------------------------------------------------------------------
	bundle.generateCode(true);
	// ----------------------------------------------------------------------------------------------------------------
    }
}
