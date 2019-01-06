package org.jeecf.manager.interceptor;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.jeecf.common.exception.BusinessException;
import org.jeecf.manager.annotation.TargetDataSource;
import org.jeecf.manager.common.enums.BusinessErrorEnum;
import org.jeecf.manager.config.DynamicDataSourceContextHolder;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
/**
 * 数据源动态切换 拦截
 * @author jianyiming
 *
 */
@Aspect
@Order(-10) 
@Component
public class DynamicDataSourceAspect {

	@Before("@annotation(targetDataSource)")
    public void changeDataSource(JoinPoint point, TargetDataSource targetDataSource) throws Throwable {
		if(!DynamicDataSourceContextHolder.getCurrentDataSourceUsable()) {
			throw new BusinessException(BusinessErrorEnum.DB_CONNECT_NOT_USABLE);
		}
       //获取当前的指定的数据源;
        DynamicDataSourceContextHolder.setDataSourceType(DynamicDataSourceContextHolder.getCurrentDataSourceValue());
    }

	@After("@annotation(targetDataSource)")
    public void restoreDataSource(JoinPoint point, TargetDataSource targetDataSource) {
       //方法执行完毕之后，销毁当前数据源信息，进行垃圾回收。
        DynamicDataSourceContextHolder.clearDataSourceType();
    }

}
