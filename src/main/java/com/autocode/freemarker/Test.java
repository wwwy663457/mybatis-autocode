package com.autocode.freemarker;


/**
 * 生成service,xml,dao,controller
 * @author coder
 *
 */
public class Test {
    
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 Generator gen = new Generator();  
	     try {
			gen.generator();  
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
}
