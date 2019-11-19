/**
 * 
 */
package org.springframework.adam.common.aop;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.adam.common.bean.ResultVo;
import org.springframework.adam.common.bean.annotation.service.ParamDefault;
import org.springframework.adam.common.bean.annotation.service.ParamNotNull;
import org.springframework.adam.common.bean.annotation.service.ServiceErrorCode;
import org.springframework.adam.common.bean.contants.BaseReslutCodeConstants;
import org.springframework.adam.common.utils.AdamClassUtils;
import org.springframework.adam.common.utils.AdamTimeUtil;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

/**
 * @author user
 *
 */
@Component
@Aspect
@Order(0)
@ServiceErrorCode(BaseReslutCodeConstants.CODE_FIELD_NULL_ERROR)
public class ParamValidAspect {
	private static Log log = LogFactory.getLog(ParamValidAspect.class);

	@Around("@annotation(org.springframework.adam.common.bean.annotation.service.ParamValid)")
	public Object aroundMethod(ProceedingJoinPoint pjp) throws Throwable {
		return doinvoke(pjp);
	}

	private Object doinvoke(ProceedingJoinPoint pjp) throws Throwable {

		Object[] args = pjp.getArgs();

		ResultVo resultVo = new ResultVo();
		if (args != null && args.length != 0) {
			// 遍历所有argument
			for (Object argument : args) {
				if (null == argument) {
					continue;
				}
				List<Field> fields = AdamClassUtils.getBeanAllFields(argument.getClass());
				if (fields == null || fields.isEmpty()) {
					continue;
				}
				// 遍历所有field找NotNull的注解
				for (Field field : fields) {
					ParamDefault paramDefault = field.getAnnotation(ParamDefault.class);
					if (paramDefault != null) {
						handleDefault(argument, field, paramDefault, resultVo);
					}

					ParamNotNull paramNotNull = field.getAnnotation(ParamNotNull.class);
					if (paramNotNull != null) {
						if (paramIsNull(argument, field, paramNotNull, resultVo)) {
							return resultVo;
						}
					}
				}
			}
		}
		Object result = pjp.proceed();
		// 返回通知
		return result;

	}

	/**
	 * @param argument
	 * @param field
	 * @param paramDefault
	 * @param resultVo
	 * @throws Exception
	 */
	private void handleDefault(Object argument, Field field, ParamDefault paramDefault, ResultVo resultVo) throws Exception {
		field.setAccessible(true);
		Object fieldValue = field.get(argument);
		if (checkNull(fieldValue, true)) {
			String defaultVal = paramDefault.value();
			if (paramDefault.isJson()) {
				field.set(argument, JSON.parseObject(defaultVal, field.getGenericType()));
			} else {
				if (String.class.equals(field.getGenericType())) {
					field.set(argument, defaultVal);
				} else if (Integer.class.equals(field.getGenericType())) {
					field.set(argument, Integer.valueOf(defaultVal));
				} else if (Long.class.equals(field.getGenericType())) {
					field.set(argument, Long.valueOf(defaultVal));
				} else if (BigDecimal.class.equals(field.getGenericType())) {
					field.set(argument, new BigDecimal(defaultVal));
				} else if (Double.class.equals(field.getGenericType())) {
					field.set(argument, Double.valueOf(defaultVal));
				} else if (Date.class.equals(field.getGenericType())) {
					field.set(argument, AdamTimeUtil.stringToDate(defaultVal));
				} else if (Boolean.class.equals(field.getGenericType())) {
					if ("true".equals(defaultVal) || "Y".equals(defaultVal)) {
						field.set(argument, Boolean.TRUE);
					} else {
						field.set(argument, Boolean.FALSE);
					}
				} else {
					field.set(argument, defaultVal);
				}
			}
		}
	}

	/**
	 * @param argument
	 * @param field
	 * @param paramNotNull
	 * @param resultVo
	 * @return
	 * @throws Exception
	 */
	private boolean paramIsNull(Object argument, Field field, ParamNotNull paramNotNull, ResultVo resultVo) throws Exception {
		field.setAccessible(true);
		Object fieldValue = field.get(argument);
		if (checkNull(fieldValue, paramNotNull.allowBlank())) {
			resultVo.setResultCode(this.getClass(), paramNotNull.code());
			StringBuilder sb = new StringBuilder("arg:");
			sb.append(argument.getClass().getSimpleName());
			sb.append(" field:");
			sb.append(field.getName());
			sb.append(" needs value-");
			sb.append(paramNotNull.msg());
			log.info(sb.toString());
			resultVo.setResultMsg(sb.toString());
			return true;
		}
		return false;
	}

	/**
	 * @param fieldValue
	 * @param allowBlank
	 * @return
	 */
	private boolean checkNull(Object fieldValue, boolean allowBlank) {
		if (null == fieldValue) {
			return true;
		}

		if (allowBlank) {
			return false;
		}

		if (fieldValue instanceof String) {
			if (StringUtils.isBlank(fieldValue.toString())) {
				return true;
			}
		}

		if (fieldValue instanceof Collection) {
			if (CollectionUtils.isEmpty((Collection) fieldValue)) {
				return true;
			}
		}

		return false;
	}

}
