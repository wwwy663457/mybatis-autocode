package com.autocode.freemarker;
/**
 * 静态常量   配置路径
 * @author coder
 *
 */
public class Constants {
	

	static String srcFile="D:\\gencode\\";
	static String packagePath = "com.autocode.example";
	/**
	 *  true:物理删除   false:逻辑删除
	 */
    static boolean deleteFlag = false;
    static String deleteColumn="isDeleted";
    /**
     * 自增长ID
     */
    static boolean autoID = false ;
    
    /**
     * 是否需要按模块分包
     */
    static boolean packageFlag=false;
    /**
     * entityname 和tables  一一对应
     */
    static String[] tables={"user"};
    static String[] ENTITY_NAME = new String[] {"User"};
}
