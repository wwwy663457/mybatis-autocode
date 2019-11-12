package ${controllerPackage};
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ${servicePackage}.${beanName}Service;
import ${modelPackage}.${beanName};

import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
@Controller
@RequestMapping("/${controllerurl}")
public class ${beanName}Controller  {


    private static Logger logger = LoggerFactory.getLogger(${beanName}Controller.class.getName());
	@Autowired
	private ${beanName}Service ${instanceName}Service;
	
	/**分页查询**/
	@RequestMapping(value = "/page", method = RequestMethod.POST)
	@ResponseBody
	public Object page(@RequestBody RequestPage page) {
		PageInfo<${beanName}> ${instanceName}Page = ${instanceName}Service.page(page);
		ResponseStr responseStr = new ResponseStr(
				${instanceName}Page,
				MessageProperty.SUCCESS_CODE,
				true,
				MessageProperty.getInstance().getProperty(MessageProperty.SUCCESS_CODE));
		return responseStr;
	}
	
	/**根据主键查询对象**/
    @RequestMapping(value = "/findbyid", method = RequestMethod.GET)
	@ResponseBody
	public Object findById(@RequestParam(value="${primKey.attributeName}",required = true) Long ${primKey.attributeName}) {
		${beanName} ${instanceName}= ${instanceName}Service.selectByPrimaryKey(${primKey.attributeName});
			if(null == ${instanceName}){
			throw new BusinessException(MessageProperty.ERROR_OBJECT_NOT_FOUND_CODE,
					MessageProperty.getInstance().getProperty(MessageProperty.ERROR_OBJECT_NOT_FOUND_CODE));
		}
		ResponseStr responseStr = new ResponseStr(
				${instanceName},
				MessageProperty.SUCCESS_CODE,
				true,
				MessageProperty.getInstance().getProperty(MessageProperty.SUCCESS_CODE));
		return responseStr;
	}
	
	
	/*新增对象*/
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public ResponseStr insert(@RequestBody @Valid ${beanName} ${instanceName}, BindingResult result){
		//校验确认
		checkValidator(result);
		int num = ${instanceName}Service.insertSelective(${instanceName});
		ResponseStr responseStr = new ResponseStr(
				${instanceName},
				MessageProperty.SUCCESS_CODE,
				true,
				MessageProperty.getInstance().getProperty(MessageProperty.SUCCESS_CODE));
		return responseStr;

	}
	
	
	/*删除对象*/
	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	@ResponseBody
	public ResponseStr delete(@RequestParam(value="${primKey.attributeName}",required = true) Long ${primKey.attributeName}){
	   int num = ${instanceName}Service.delete(${primKey.attributeName});
		if(num == 0) {
			throw new BusinessException(MessageProperty.ERROR_OBJECT_NOT_FOUND_CODE,
					MessageProperty.getInstance().getProperty(MessageProperty.ERROR_OBJECT_NOT_FOUND_CODE));
		}
		ResponseStr responseStr = new ResponseStr(
				num,
				MessageProperty.SUCCESS_CODE,
				true,
				MessageProperty.getInstance().getProperty(MessageProperty.SUCCESS_CODE));
		return responseStr;

	}
	
	
	
    /***修改对象*/
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	@ResponseBody
	public ResponseStr update(@RequestBody @Valid ${beanName} ${instanceName}, BindingResult result){
		//校验确认
		checkValidator(result);
		int num = ${instanceName}Service.updateByPrimaryKeySelective(${instanceName});
		if(num == 0) {
			throw new BusinessException(MessageProperty.ERROR_OBJECT_NOT_FOUND_CODE,
					MessageProperty.getInstance().getProperty(MessageProperty.ERROR_OBJECT_NOT_FOUND_CODE));
		}
		ResponseStr responseStr = new ResponseStr(
				${instanceName},
				MessageProperty.SUCCESS_CODE,
				true,
				MessageProperty.getInstance().getProperty(MessageProperty.SUCCESS_CODE));
		return responseStr;

	}
	
	

	
	
	


}