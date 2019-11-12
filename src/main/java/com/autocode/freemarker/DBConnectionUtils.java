package com.autocode.freemarker;



import java.sql.Connection;  
import java.sql.DriverManager;  
import java.sql.SQLException;  
import java.util.Properties;
  
/** 
 * 数据库连接类
 *  
 * 
 *  
 */  
public class DBConnectionUtils {  
    private static Connection conn = null;  
   
    public static Connection getJDBCConnection() {  
        if (null == conn) {  
            try {  
            	Properties props =new Properties();
            	props.put("user", "example");
            	props.put("password", "example");
            	props.put("useInformationSchema","true"); //表注释
                Class.forName("com.mysql.jdbc.Driver"); //   
                String url = "jdbc:mysql://127.0.0.1:3306/example?useUnicode=true&characterEncoding=utf-8";//
                conn = DriverManager.getConnection(url, props);  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
        return conn;  
    }  
  
    public static void close(){  
        if(null!=conn){  
            try {  
                conn.close();  
            } catch (SQLException e) {  
                e.printStackTrace();  
            }  
        }  
    }  
      
}  
