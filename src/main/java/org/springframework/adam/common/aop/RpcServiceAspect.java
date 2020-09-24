/**
 * 
 */
package org.springframework.adam.common.aop;

import java.lang.reflect.Method;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.adam.client.ILogService;
import org.springframework.adam.common.bean.annotation.service.RpcService;
import org.springframework.adam.common.bean.annotation.service.ServiceErrorCode;
import org.springframework.adam.common.bean.contants.AdamSysConstants;
import org.springframework.adam.common.bean.contants.BaseReslutCodeConstants;
import org.springframework.adam.common.utils.AdamTimeUtil;
import org.springframework.adam.common.utils.ThreadLocalHolder;
import org.springframework.adam.service.IRequestHook;
import org.springframework.beans.factory.annotation.Autowired;
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
@ServiceErrorCode(BaseReslutCodeConstants.CODE_SYSTEM_ERROR)
public class RpcServiceAspect {

	private static Logger logger = Logger.getLogger(RpcServiceAspect.class);

	@Autowired(required = false)
	private ILogService logService;

	@Autowired(required = false)
	private IRequestHook requestHook;

	@Around("@annotation(org.springframework.adam.common.bean.annotation.service.RpcService)")
	public Object aroundMethod(ProceedingJoinPoint pjp) throws Throwable {
		return doinvoke(pjp);
	}

	private Object doinvoke(ProceedingJoinPoint pjp) throws Throwable {
		// 获取信息
		Object[] args = pjp.getArgs();
		MethodSignature signature = (MethodSignature) pjp.getSignature();
		String methodName = signature.toString();
		Method method = signature.getMethod();
		RpcService rpcService = method.getAnnotation(RpcService.class);

		// 新建request对象
		Object returnValue = null;

		ThreadLocalHolder.initRunningAccount();
		// 为每个接口设置个名
		String name = rpcService.name();
		if (StringUtils.isBlank(name)) {
			name = methodName;
		}
		ThreadLocalHolder.setName(name);

		returnValue = doBefore(methodName, null, args, returnValue);
		String runningAccount = ThreadLocalHolder.getRunningAccount();
		try {
			// 看看是不是要日志
			if (null != logService && logService.isNeedLog()) {
				// 获取参数
				StringBuilder argSB = new StringBuilder(2048);
				argSB.append("request_begin method:");
				argSB.append(methodName);
				argSB.append(AdamSysConstants.COLUMN_SPE);
				for (Object arg : args) {
					argSB.append(arg + ":");
					argSB.append(logService.objToStr(arg));
					argSB.append(AdamSysConstants.COLUMN_SPE);
				}
				logService.sendBeginRequestLog(argSB.toString());
			}
			if (returnValue == null) {
				returnValue = pjp.proceed();
			}
			returnValue = doAfter(methodName, null, args, returnValue);
		} finally {
			if (ThreadLocalHolder.getStatus() >= 0 && !rpcService.isAsyn() && null != logService
					&& logService.isNeedLog()) {
				StringBuilder argSB = new StringBuilder(2048);
				argSB.append("request_end used time:");
				argSB.append(AdamTimeUtil.getNow() - ThreadLocalHolder.getBegin());
				argSB.append(AdamSysConstants.COLUMN_SPE);
				argSB.append(logService.objToStr(returnValue));
				logService.sendEndRequestLog(argSB);
				ThreadLocalHolder.setStatus(-1);
			}
		}
		return returnValue;
	}

	private Object doBefore(String url, Map<String, String> headersMap, Object[] income, Object output)
			throws Exception {
		if (null != requestHook) {
			return requestHook.doBefore(url, headersMap, income, output);
		}
		return null;
	}

	private Object doAfter(String url, Map<String, String> headersMap, Object[] income, Object output)
			throws Exception {
		if (null != requestHook) {
			return requestHook.doAfter(url, headersMap, income, output);
		}
		return output;
	}
}
