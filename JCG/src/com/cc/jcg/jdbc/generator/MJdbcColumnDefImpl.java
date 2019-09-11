package com.cc.jcg.jdbc.generator;

import java.util.concurrent.atomic.AtomicBoolean;

import com.cc.jcg.jdbc.JdbcCriteriaBase;

public class MJdbcColumnDefImpl
	implements MJdbcColumnDef {

    private Class<?> valueType;
    private Integer sortOrder;
    private String columnName;
    private String label;
    private final AtomicBoolean multipleValues = new AtomicBoolean(false);

    public MJdbcColumnDefImpl(String columnName, Class<?> valueType, Integer sortOrder, String label) {
	super();
	this.valueType = valueType;
	this.sortOrder = sortOrder;
	this.columnName = columnName;
	this.label = label;
    }

    public MJdbcColumnDefImpl(String columnName, Class<?> valueType, Integer sortOrder) {
	super();
	this.valueType = valueType;
	this.sortOrder = sortOrder;
	this.columnName = columnName;
	this.label = JdbcCriteriaBase.labelize(columnName);
    }

    public MJdbcColumnDefImpl(String columnName, Class<?> valueType, String label) {
	super();
	this.valueType = valueType;
	this.sortOrder = null;
	this.columnName = columnName;
	this.label = label;
    }

    public MJdbcColumnDefImpl(String columnName, Class<?> valueType) {
	super();
	this.valueType = valueType;
	this.sortOrder = null;
	this.columnName = columnName;
	this.label = JdbcCriteriaBase.labelize(columnName);
    }

    public MJdbcColumnDefImpl(String columnName, Class<?> valueType, boolean multipleValues) {
	super();
	this.valueType = valueType;
	this.sortOrder = null;
	this.columnName = columnName;
	this.label = JdbcCriteriaBase.labelize(columnName);
	this.multipleValues.set(multipleValues);
    }

    @Override
    public Class<?> getValueType() {
	return valueType;
    }

    @Override
    public Integer getSortOrder() {
	return sortOrder;
    }

    @Override
    public String getColumnName() {
	return columnName;
    }

    @Override
    public String getLabel() {
	return label;
    }

    @Override
    public AtomicBoolean isMultipleValues() {
	return multipleValues;
    }
}
