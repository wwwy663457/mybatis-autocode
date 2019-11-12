package com.autocode.freemarker;


/**
 * 数据库字段实体
 * @author coder
 *
 */
public class Column {  
	  
	private String columnName;  
    private String columnType;  
    private String attributeName;  
    private String attributeType;
    private String jdbcType;
    
    
    private String fullAttributeType;
    
	public String getFullAttributeType() {
		return fullAttributeType;
	}
	public void setFullAttributeType(String fullAttributeType) {
		this.fullAttributeType = fullAttributeType;
	}
	public String getJdbcType() {
		return jdbcType;
	}
	public void setJdbcType(String jdbcType) {
		this.jdbcType = jdbcType;
	}
	/**
	 * ע��
	 */
	private String comment;

	private boolean primaryKey;
	private boolean isAutoIncrement = false;;
 
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public boolean isPrimaryKey() {
		return primaryKey;
	}
	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}
	public boolean isAutoIncrement() {
		return isAutoIncrement;
	}
	public void setAutoIncrement(boolean isAutoIncrement) {
		this.isAutoIncrement = isAutoIncrement;
	}
	public String getColumnName() {  
        return columnName;  
    }  
    public void setColumnName(String columnName) {  
        this.columnName = columnName;  
    }  
    public String getColumnType() {  
        return columnType;  
    }  
    public void setColumnType(String columnType) {  
        this.columnType = columnType;  
    }  
    public String getAttributeName() {  
        return attributeName;  
    }  
    public void setAttributeName(String attributeName) {  
        this.attributeName = attributeName;  
    }  
    public String getAttributeType() {  
        return attributeType;  
    }  
    public void setAttributeType(String attributeType) {  
        this.attributeType = attributeType;  
    }  
  
}  