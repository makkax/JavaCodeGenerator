package com.cc.jcg.jdbc;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Properties;

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
	MBundle.EXCLUDE_GENERATED_ANNOTATION.set(false);
	MBundle.GENERATE_READONLY.set(true);
	// ----------------------------------------------------------------------------------------------------------------
	MBundle bundle = new MBundle(new File("src-generated"));
	MPackage pckg = bundle.newPackage("com.cc.jcg.jdbc");
	// ----------------------------------------------------------------------------------------------------------------
	MJdbcCriteriaGenerator generator = new MJdbcCriteriaGenerator(pckg);
	Properties properties = new Properties();
	// jdbc.url, jdbc.username, jdbc.password
	properties.load(new FileInputStream("database-connection.properties"));
	Connection connection = new JDBCConnectionProviderOracle(properties).getNewConnection();
	generator.generateJdbcCriterias(connection, name -> {
	    return name.equals(properties.getProperty("table.name"));
	});
	// ----------------------------------------------------------------------------------------------------------------
	bundle.generateCode(false);
	// ----------------------------------------------------------------------------------------------------------------
    }
}
