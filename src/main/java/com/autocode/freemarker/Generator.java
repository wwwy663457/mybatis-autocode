package com.autocode.freemarker;



import java.io.File;  
import java.io.FileWriter;  
import java.io.IOException;  
import java.io.Writer;  
import java.sql.Connection;  
import java.sql.DatabaseMetaData;  
import java.sql.ResultSet;  
import java.sql.Types;
import java.util.ArrayList;  
import java.util.HashMap;  
import java.util.List;  
import java.util.Map;  
  







import org.apache.commons.lang.StringUtils;

import freemarker.template.Configuration;  
import freemarker.template.DefaultObjectWrapper;  
import freemarker.template.Template;  
import freemarker.template.TemplateException;
  
/**
 * 生成入口
 * @author coder
 *
 */
public class Generator {  
    private Configuration cfg;  
    private Connection connection;  
    private void initial() {  
        try {  
            if (null == cfg) {  
                cfg = new Configuration();  
            }  
            cfg.setDirectoryForTemplateLoading(new File(  
                    "src/main/java/com/autocode/freemarker"));
           
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        cfg.setObjectWrapper(new DefaultObjectWrapper());  
    }  
    public void generator() throws Exception {  
        initial();  
        cfg.setDefaultEncoding("utf-8");  
        Template entityTemplate = cfg.getTemplate("entity.ftl");  
        Template sqlTemplate = cfg.getTemplate("xml.ftl");  
        Template daoTemplate = cfg.getTemplate("dao.ftl");  
        Template serviceTemplate = cfg.getTemplate("service.ftl"); 
        Template controllerTemplate = cfg.getTemplate("controller.ftl"); 
        List<Map<String, Object>> templates=new ArrayList<Map<String,Object>>();
        if(Constants.tables!=null&&Constants.tables.length>0){
        	int i=0;
        	for(String table:Constants.tables){
        		templates.addAll(generatorTemplateData(table,Constants.ENTITY_NAME[i]));
        		i++;
        	}
        }else{
        	templates = generatorTemplateData("","");
        } 
        
        connection.close(); 
        
        
        //handlerTableName();
        for (Map<String, Object> o : templates) {
        	boolean packFlag=Constants.packageFlag;
        	String pageName="";
        	if(packFlag) {
        		pageName="."+o.get("beanName").toString().toLowerCase();
        	}
        	buildFile(Constants.srcFile + (Constants.packagePath+pageName+".entity").replaceAll("\\.", "\\/") + "/",o.get("beanName") + ".java",entityTemplate,o);
        	buildFile(Constants.srcFile + (Constants.packagePath+pageName+".dao").replaceAll("\\.", "\\/") + "/",o.get("beanName") + "Mapper.java",daoTemplate,o);
        	buildFile(Constants.srcFile + (Constants.packagePath+pageName+".service").replaceAll("\\.", "\\/") + "/",o.get("beanName") + "Service.java",serviceTemplate,o);
        	buildFile(Constants.srcFile + (Constants.packagePath+pageName+".controller").replaceAll("\\.", "\\/") + "/",o.get("beanName") + "Controller.java",controllerTemplate,o);
        	/**
        	 * map  放到entity的文件目录里面
        	 */
            buildFile(Constants.srcFile + (Constants.packagePath+pageName+".entity").replaceAll("\\.", "\\/") + "/",o.get("beanName") + "Mapper.xml",sqlTemplate,o);
        }  
    }  
     
    
    

  
    private List<Map<String, Object>> generatorTemplateData(String table,String entityName) throws Exception { 
    	connection = DBConnectionUtils.getJDBCConnection(); 
    
        DatabaseMetaData dbmd = connection.getMetaData();  
        List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();  
        String[] tables = { "Table" };  
        if(!StringUtils.isNotBlank(table)){
        	table="%";
        }
        ResultSet tableSet = dbmd.getTables(null, null, table, tables);// 
        String priKeyName="";
        Column columnPri = null;  
        while (tableSet.next()) {  
        	
            Map<String, Object> map = new HashMap<String, Object>();  
            map.put("priKeyType", "String");
            String tableName = tableSet.getString("TABLE_NAME");
            System.out.println("TRUNCATE "+ tableName + ";");
            String beanName="";
            if(StringUtils.isNotBlank(entityName)) {
            	beanName=entityName; 
            }else {
            	beanName=handlerTableName(tableName);  
            }
            
        	boolean packFlag=Constants.packageFlag;
        	String pageName="";
        	if(packFlag) {
        		pageName="."+beanName.toLowerCase();
        	}
            String tableRemark=tableSet.getString("REMARKS"); 
            ResultSet columnSet = dbmd.getColumns(null, "%", tableName, "%");  
            ResultSet primaryKeySet=dbmd.getPrimaryKeys(null, null, tableName);
            if (primaryKeySet.next()) {
            	columnPri=new Column();
            	priKeyName = primaryKeySet.getString("COLUMN_NAME");
            	columnPri.setColumnName(priKeyName);
    			String attributeName = handlerColumnName(priKeyName);
    			columnPri.setAttributeName(attributeName);
    		}
            List<Column> columns = new ArrayList<Column>();//   
            while (columnSet.next()) {  
                String columnName = columnSet.getString("COLUMN_NAME");  
                String attributeName = handlerColumnName(columnSet.getString("COLUMN_NAME"));  
                String columnType = columnSet.getString("TYPE_NAME");  
               // System.out.println(columnType);
                int columnDataType=columnSet.getInt("DATA_TYPE");
                String attributeType = getDataType(columnDataType);;
                String columnRemark=columnSet.getString("REMARKS");
                String autoIncrement=columnSet.getString("IS_AUTOINCREMENT");
                Column column = new Column();  
                column.setJdbcType(getJDBCType(columnDataType));
                column.setComment(columnRemark);
                if(priKeyName.equals(columnName)){
                	columnPri.setColumnType(columnType);
                	columnPri.setAttributeType(attributeType);
                	columnPri.setJdbcType(getJDBCType(columnDataType));
                	columnPri.setFullAttributeType(getFullDataType(columnDataType));
                	column.setPrimaryKey(true);
                    column.setAutoIncrement(true);
   	             	map.put("priKeyType", attributeType);  
                }else{
                	column.setPrimaryKey(false);
                    column.setAutoIncrement(false);
                }
                
                column.setColumnName(columnName);  
                column.setColumnType(columnType);  
                column.setAttributeName(attributeName);  
                column.setAttributeType(attributeType); 
                column.setFullAttributeType(getFullDataType(columnDataType));
                columns.add(column);  
            } 
            map.put("basePackage", Constants.packagePath+pageName);
            map.put("modelPackage", Constants.packagePath+pageName+".entity");
            map.put("daoPackage", Constants.packagePath+pageName+".dao");
            map.put("servicePackage", Constants.packagePath+pageName+".service");
            map.put("controllerPackage", Constants.packagePath+pageName+".controller");
            map.put("tableName", tableName);  
            map.put("tableRemark", tableRemark);
         
            map.put("beanName", beanName);
            map.put("columns", columns);
            if(columnPri!=null)
            map.put("primKey", columnPri);
            map.put("instanceName", beanName.toLowerCase());
            map.put("objectPrimkey", handlerTableName(columnPri.getColumnName()));
            map.put("deleteFlag", Constants.deleteFlag);
            map.put("controllerurl", beanName.toLowerCase());
            map.put("newRowTag", "&#x000A;");
            map.put("deleteColumn", Constants.deleteColumn);
            map.put("autoID", Constants.autoID);
            lists.add(map);  
        }  
       // connection.close(); 
        
        return lists;  
    }  
  
    public static String handlerColumnName(String oldName) {  
        String[] arrays = oldName.split("_");  
        String newName = "";  
        if (arrays.length > 0) {  
            newName = arrays[0];  
        }  
        for (int i = 1; i < arrays.length; i++) {  
            newName += (arrays[i].substring(0, 1).toUpperCase() + arrays[i]  
                    .substring(1, arrays[i].length()));  
        }  
        return newName;  
    }  
  
    public static String handlerTableName(String oldName) {  
    	if(oldName.startsWith("t_")) {
    		oldName = oldName.substring(2);
    	}
        String[] arrays = oldName.split("_");  
        String newName = "";  
        for (int i = 0; i < arrays.length; i++) {  
            newName += (arrays[i].substring(0, 1).toUpperCase() + arrays[i]  
                    .substring(1, arrays[i].length()));  
        }  
        return newName;  
    }  
  
    /*public static String handlerColumnType(String oldType) {  
        if (oldType.toUpperCase().startsWith("VARCHAR")) {  
            return "String";  
        }  
        if (oldType.toUpperCase().startsWith("INT")) {  
            return "int";  
        }  
        if (oldType.toUpperCase().startsWith("DATETIME")) {  
            return "Date";  
        }if (oldType.toUpperCase().startsWith("CHAR")) {  
            return "String";  
        }  
        if (oldType.toUpperCase().startsWith("TINYINT")) {  
            return "int";  
        }  
        if (oldType.toUpperCase().startsWith("BIT")) {  
            return "int";  
        }  
        if (oldType.toUpperCase().startsWith("BIGINT")) {  
            return "Long";  
        }  if(oldType.toUpperCase().startsWith("DATE")) {
        	return "Date";
        }
        return oldType;  
    }  */
    
    
	public static String getDataType(int iDataType) {
		String dataType = "";
		if (iDataType == Types.VARCHAR || iDataType == Types.CHAR || iDataType == Types.LONGVARCHAR || iDataType == Types.CLOB) {
			dataType = "String";
		} else if (iDataType == Types.INTEGER || iDataType == Types.BIT || iDataType == Types.TINYINT || iDataType == Types.SMALLINT
				|| iDataType == Types.SMALLINT) {
			dataType = "Integer";
		} else if (iDataType == Types.BIGINT) {
			dataType = "Long";
		} else if (iDataType == Types.DOUBLE || iDataType == Types.FLOAT || iDataType == Types.REAL) {
			dataType = "Double";
		} else if (iDataType == Types.DECIMAL || iDataType == Types.NUMERIC) {
			dataType = "BigDecimal";
		} else if (iDataType == Types.DATE || iDataType == Types.TIMESTAMP || iDataType == Types.TIME) {
			dataType = "Date";
		} else if (iDataType == Types.BLOB || iDataType == Types.BINARY || iDataType == Types.VARBINARY || iDataType == Types.LONGVARBINARY) {
			dataType = "byte[]";
		}
		return dataType;
	}
	
	
	/**
	 * 获取java全类型
	 */
	public static String getFullDataType(int iDataType) {
		String dataType = "";
		if (iDataType == Types.VARCHAR || iDataType == Types.NVARCHAR || iDataType == Types.CHAR || iDataType == Types.LONGVARCHAR || iDataType == Types.CLOB) {
			dataType = "java.lang.String";
		} else if (iDataType == Types.INTEGER || iDataType == Types.BIT || iDataType == Types.TINYINT || iDataType == Types.SMALLINT
				|| iDataType == Types.SMALLINT) {
			dataType = "java.lang.Integer";
		} else if (iDataType == Types.BIGINT) {
			dataType = "java.lang.Long";
		} else if (iDataType == Types.DOUBLE || iDataType == Types.FLOAT || iDataType == Types.REAL) {
			dataType = "java.lang.Double";
		} else if (iDataType == Types.DECIMAL || iDataType == Types.NUMERIC) {
			dataType = "java.math.BigDecimal";
		} else if (iDataType == Types.DATE || iDataType == Types.TIMESTAMP || iDataType == Types.TIME) {
			dataType = "java.util.Date";
		} else if (iDataType == Types.BLOB || iDataType == Types.BINARY || iDataType == Types.VARBINARY || iDataType == Types.LONGVARBINARY) {
			dataType = "java.lang.Byte";
		}
		return dataType;
	}

	/**
	 * 获取jdbc类型
	 */
	public static String getJDBCType(int iDataType) {
		String jdbcType = "";
		if (iDataType == Types.VARCHAR) {
			jdbcType = "VARCHAR";
		} else if (iDataType == Types.NVARCHAR) {
			jdbcType = "NVARCHAR";
		} else if (iDataType == Types.CHAR) {
			jdbcType = "CHAR";
		} else if (iDataType == Types.LONGVARCHAR) {
			jdbcType = "LONGVARCHAR";
		} else if (iDataType == Types.CLOB) {
			jdbcType = "CLOB";
		} else if (iDataType == Types.INTEGER) {
			jdbcType = "INTEGER";
		} else if (iDataType == Types.BIT) {
			jdbcType = "BIT";
		} else if (iDataType == Types.TINYINT) {
			jdbcType = "TINYINT";
		} else if (iDataType == Types.SMALLINT) {
			jdbcType = "SMALLINT";
		} else if (iDataType == Types.BIGINT) {
			jdbcType = "BIGINT";
		} else if (iDataType == Types.DOUBLE) {
			jdbcType = "DOUBLE";
		} else if (iDataType == Types.DECIMAL) {
			jdbcType = "DECIMAL";
		} else if (iDataType == Types.FLOAT) {
			jdbcType = "FLOAT";
		} else if (iDataType == Types.REAL) {
			jdbcType = "REAL";
		} else if (iDataType == Types.NUMERIC) {
			jdbcType = "NUMERIC";
		} else if (iDataType == Types.DATE) {
			jdbcType = "DATE";
		} else if (iDataType == Types.TIMESTAMP) {
			jdbcType = "TIMESTAMP";
		} else if (iDataType == Types.TIME) {
			jdbcType = "TIME";
		} else if (iDataType == Types.BLOB) {
			jdbcType = "BLOB";
		} else if (iDataType == Types.BINARY) {
			jdbcType = "BINARY";
		} else if (iDataType == Types.VARBINARY) {
			jdbcType = "VARBINARY";
		} else if (iDataType == Types.LONGVARBINARY) {
			jdbcType = "LONGVARBINARY";
		}
		return jdbcType;
	}
	
	
	
    private void  buildFile(String dir,String path,Template t,Object o){
 	   File dirPath = new File(dir);  
        if (!dirPath.exists())  
        {  
     	   dirPath.mkdirs();  
        } 
        File filePath=new File(dir+path);
        System.out.println("生成文件:"+dir+path);
        try {
			Writer sqlWriter = new FileWriter(filePath);
			try {
				t.process(o, sqlWriter);
				   sqlWriter.close(); 
			} catch (TemplateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    
	     
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
 }
}  
