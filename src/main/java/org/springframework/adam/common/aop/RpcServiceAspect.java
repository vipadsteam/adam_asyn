/**
 * 
 */
package org.springframework.adam.common.aop;

import java.util.Map;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.adam.client.ILogService;
import org.springframework.adam.common.bean.RequestLogEntity;
import org.springframework.adam.common.bean.ResultVo;
import org.springframework.adam.common.bean.annotation.service.ServiceErrorCode;
import org.springframework.adam.common.bean.contants.AdamSysConstants;
import org.springframework.adam.common.bean.contants.BaseReslutCodeConstants;
import org.springframework.adam.common.utils.AdamExceptionUtils;
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

	@Autowired
	private ILogService logService;

	@Autowired
	private IRequestHook requestHook;

	@Around("@annotation(org.springframework.adam.common.bean.annotation.service.RpcService)")
	public Object aroundMethod(ProceedingJoinPoint pjp) throws Throwable {
		return doinvoke(pjp);
	}

	private Object doinvoke(ProceedingJoinPoint pjp) throws Throwable {
		// 获取信息
		long beginTime = System.currentTimeMillis();
		Object[] args = pjp.getArgs();
		Signature method = pjp.getSignature();

		// 新建request对象
		RequestLogEntity requestLogEntity = null;
		Object returnValue = null;

		returnValue = doBefore(method.toString(), null, args, returnValue);
		String runningAccount = "";
		if (logService.isNeedLog()) {
			// init running account
			ThreadLocalHolder.initRunningAccount();
			runningAccount = ThreadLocalHolder.getRunningAccount();
		}
		try {
			// 看看是不是要日志
			if (logService.isNeedLog()) {
				// 获取参数
				requestLogEntity = new RequestLogEntity();
				requestLogEntity.setUrl(method.toString());
				requestLogEntity.setHeader("header:RPCServiceAspect");
				StringBuilder argSB = new StringBuilder(2048);
				for (Object arg : args) {
					argSB.append(arg + ":");
					argSB.append(JSON.toJSONString(arg));
					argSB.append(AdamSysConstants.LINE_SEPARATOR);
				}
				requestLogEntity.setRequest(argSB.toString());
				logService.sendBeginRequestLog(requestLogEntity);
			}
			if (returnValue == null) {
				returnValue = pjp.proceed();
			}
			returnValue = doAfter(method.toString(), null, args, returnValue);
		} catch (Throwable t) {
			long endTime = System.currentTimeMillis();
			logger.error("RA:" + runningAccount + " " + "Method [" + method.toString() + "] " + AdamSysConstants.LINE_SEPARATOR + "returned [" + JSON.toJSONString(returnValue) + "]" + "useTime:" + (endTime - beginTime) + t, t);
			ResultVo<String> resultVo = new ResultVo<String>();
			resultVo.setResultCode(this.getClass(), BaseReslutCodeConstants.CODE_SYSTEM_ERROR);
			resultVo.setResultMsg(AdamExceptionUtils.getStackTrace(t));
			returnValue = resultVo;
			throw t;
		} finally {
			if (returnValue instanceof ResultVo) {
				ResultVo resultVo = (ResultVo) returnValue;
				resultVo.setResultMsg("ra:" + runningAccount);
				if (resultVo.getResultCode().startsWith(BaseReslutCodeConstants.CODE_SYSTEM_ERROR)) {
					logService.sendTechnologyErrorAccountLog(args, returnValue, method.toString(), "系统异常");
				}
			}
			long endTime = System.currentTimeMillis();
			if (logService.isNeedLog() && null != requestLogEntity) {
				requestLogEntity.setResponse(JSON.toJSONString(returnValue));
				requestLogEntity.setUseTime(endTime - beginTime);
				logService.sendEndRequestLog(requestLogEntity);
			}
		}
		return returnValue;
	}

	private Object doBefore(String url, Map<String, String> headersMap, Object[] income, Object output) throws Exception {
		return requestHook.doBefore(url, headersMap, income, output);
	}

	private Object doAfter(String url, Map<String, String> headersMap, Object[] income, Object output) throws Exception {
		return requestHook.doAfter(url, headersMap, income, output);
	}
}
