package ${modelPackage};
import org.springframework.format.annotation.DateTimeFormat; 
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;  
/**  
 * 对应表 ${tableName}
 * ${tableRemark}  
 *  
 */ 
  
public class ${beanName} implements java.io.Serializable{ 
	private static final long serialVersionUID = 1L;
 
	<#list columns as item>  
	
    /**
     * 对应表中${item.columnName}
     * ${item.comment}
     */  
	<#if item.columnType=="DATE">
	@DateTimeFormat(pattern="yyyy-MM-dd")
	<#elseif  item.columnType=="DATETIME" >
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	</#if>
	private ${item.attributeType} ${item.attributeName};
	</#list> 
	
    public ${beanName}(){
    } 
    <#list columns as item>    
    public ${item.attributeType} get${item.attributeName?cap_first}(){    
    	return ${item.attributeName};    
    }    
    public void set${item.attributeName?cap_first}(${item.attributeType} ${item.attributeName}){    
    	this.${item.attributeName} = ${item.attributeName};    
    }    
    </#list>
}   