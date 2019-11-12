package ${servicePackage};


import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ${modelPackage}.${beanName};
import ${daoPackage}.${beanName}Mapper;



@Service
public class ${beanName}Service extends BaseSQLServiceImpl<${beanName}, ${priKeyType},${beanName}Mapper> {

}
