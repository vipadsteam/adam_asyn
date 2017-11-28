/*
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.web.method.support;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.adam.client.ILogService;
import org.springframework.adam.common.bean.RequestLogEntity;
import org.springframework.adam.common.bean.ResultVo;
import org.springframework.adam.common.bean.annotation.service.ServiceErrorCode;
import org.springframework.adam.common.bean.contants.AdamSysConstants;
import org.springframework.adam.common.bean.contants.BaseReslutCodeConstants;
import org.springframework.adam.common.utils.AdamClassUtils;
import org.springframework.adam.common.utils.AdamExceptionUtils;
import org.springframework.adam.common.utils.ThreadLocalHolder;
import org.springframework.adam.common.utils.context.SpringContextUtils;
import org.springframework.adam.service.IRequestHook;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;

import com.alibaba.fastjson.JSON;

/**
 * Provides a method for invoking the handler method for a given request after
 * resolving its method argument values through registered
 * {@link HandlerMethodArgumentResolver}s.
 *
 * <p>
 * Argument resolution often requires a {@link WebDataBinder} for data binding
 * or for type conversion. Use the
 * {@link #setDataBinderFactory(WebDataBinderFactory)} property to supply a
 * binder factory to pass to argument resolvers.
 *
 * <p>
 * Use
 * {@link #setHandlerMethodArgumentResolvers(HandlerMethodArgumentResolverComposite)}
 * to customize the list of argument resolvers.
 *
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 * @since 3.1
 */
@ServiceErrorCode(BaseReslutCodeConstants.CODE_SYSTEM_ERROR)
public class InvocableHandlerMethod extends HandlerMethod {

	private WebDataBinderFactory dataBinderFactory;

	private HandlerMethodArgumentResolverComposite argumentResolvers = new HandlerMethodArgumentResolverComposite();

	private ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

	/**
	 * Create an instance from the given handler and method.
	 */
	public InvocableHandlerMethod(Object bean, Method method) {
		super(bean, method);
	}

	/**
	 * Create an instance from a {@code HandlerMethod}.
	 */
	public InvocableHandlerMethod(HandlerMethod handlerMethod) {
		super(handlerMethod);
	}

	/**
	 * Construct a new handler method with the given bean instance, method name
	 * and parameters.
	 * 
	 * @param bean
	 *            the object bean
	 * @param methodName
	 *            the method name
	 * @param parameterTypes
	 *            the method parameter types
	 * @throws NoSuchMethodException
	 *             when the method cannot be found
	 */
	public InvocableHandlerMethod(Object bean, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {

		super(bean, methodName, parameterTypes);
	}

	/**
	 * Set the {@link WebDataBinderFactory} to be passed to argument resolvers
	 * allowing them to create a {@link WebDataBinder} for data binding and type
	 * conversion purposes.
	 * 
	 * @param dataBinderFactory
	 *            the data binder factory.
	 */
	public void setDataBinderFactory(WebDataBinderFactory dataBinderFactory) {
		this.dataBinderFactory = dataBinderFactory;
	}

	/**
	 * Set {@link HandlerMethodArgumentResolver}s to use to use for resolving
	 * method argument values.
	 */
	public void setHandlerMethodArgumentResolvers(HandlerMethodArgumentResolverComposite argumentResolvers) {
		this.argumentResolvers = argumentResolvers;
	}

	/**
	 * Set the ParameterNameDiscoverer for resolving parameter names when needed
	 * (e.g. default request attribute name).
	 * <p>
	 * Default is a
	 * {@link org.springframework.core.DefaultParameterNameDiscoverer}.
	 */
	public void setParameterNameDiscoverer(ParameterNameDiscoverer parameterNameDiscoverer) {
		this.parameterNameDiscoverer = parameterNameDiscoverer;
	}

	/**
	 * Invoke the method after resolving its argument values in the context of
	 * the given request.
	 * <p>
	 * Argument values are commonly resolved through
	 * {@link HandlerMethodArgumentResolver}s. The {@code provideArgs} parameter
	 * however may supply argument values to be used directly, i.e. without
	 * argument resolution. Examples of provided argument values include a
	 * {@link WebDataBinder}, a {@link SessionStatus}, or a thrown exception
	 * instance. Provided argument values are checked before argument resolvers.
	 * 
	 * @param request
	 *            the current request
	 * @param mavContainer
	 *            the ModelAndViewContainer for this request
	 * @param providedArgs
	 *            "given" arguments matched by type, not resolved
	 * @return the raw value returned by the invoked method
	 * @exception Exception
	 *                raised if no suitable argument resolver can be found, or
	 *                if the method raised an exception
	 */
	public Object invokeForRequest(NativeWebRequest request, ModelAndViewContainer mavContainer, Object... providedArgs) throws Exception {
		if (!SpringContextUtils.isContextInjected()) {
			return invokeForRequest1(request, mavContainer, providedArgs);
		}

		ILogService logService = SpringContextUtils.getBean(ILogService.class);
		Object[] args = getMethodArgumentValues(request, mavContainer, providedArgs);
		StringBuilder sb = new StringBuilder();

		String fullPath = "";
		StringBuilder headerSB = new StringBuilder();
		Map<String, String> headersMap = new HashMap<String, String>();
		if (request instanceof ServletWebRequest) {
			HttpServletRequest servletWebRequest = ((ServletWebRequest) request).getRequest();
			// path
			fullPath = servletWebRequest.getRequestURI();
			sb.append("path:" + fullPath);
			sb.append(AdamSysConstants.LINE_SEPARATOR);
			sb.append("from:" + servletWebRequest.getRemoteAddr());
			sb.append(AdamSysConstants.LINE_SEPARATOR);

			// header
			sb.append("header:");
			sb.append(AdamSysConstants.LINE_SEPARATOR);
			Enumeration headerNames = servletWebRequest.getHeaderNames();
			while (headerNames.hasMoreElements()) {
				String key = headerNames.nextElement().toString();
				String value = request.getHeader(key);
				headersMap.put(key, value);
				headerSB.append(key + ":");
				headerSB.append(value);
				headerSB.append(AdamSysConstants.LINE_SEPARATOR);
			}
		}
		sb.append(headerSB);
		sb.append("Invoking [");
		sb.append(getBeanType().getSimpleName()).append(".");
		sb.append(getMethod().getName()).append("] method with arguments ");
		sb.append(AdamSysConstants.LINE_SEPARATOR);
		for (Object arg : args) {
			sb.append(arg + ":");
			sb.append(JSON.toJSONString(arg));
			sb.append(AdamSysConstants.LINE_SEPARATOR);
		}
		ThreadLocalHolder.initRunningAccount();
		String runningAccount = ThreadLocalHolder.getRunningAccount();
		logger.info("RA:" + runningAccount + " " + sb.toString());
		long beginTime = System.currentTimeMillis();
		Object returnValue = null;
		String methodName = AdamClassUtils.getTargetClass(this).getSimpleName() + ".invokeForRequest";
		ThreadLocalHolder.setRunningAccountFlag(1);
		try {
			returnValue = doBefore(fullPath, headersMap, args, returnValue);
			if (returnValue == null) {
				returnValue = doInvoke(args);
			}
		} catch (Throwable t) {
			logger.error("returnValue:[" + JSON.toJSONString(returnValue) + "]" + t, t);
			ResultVo<String> resultVo = new ResultVo<String>();
			resultVo.setResultCode(this.getClass(), BaseReslutCodeConstants.CODE_SYSTEM_ERROR);
			resultVo.setResultMsg(AdamExceptionUtils.getStackTrace(t));
			returnValue = resultVo;
		} finally {
			returnValue = doAfter(fullPath, headersMap, args, returnValue);
		}

		if (returnValue instanceof ResultVo) {
			ResultVo resultVo = (ResultVo) returnValue;
			resultVo.setResultMsg("ra:" + runningAccount);
			if (resultVo.getResultCode().startsWith(BaseReslutCodeConstants.CODE_SYSTEM_ERROR)) {
				logService.sendTechnologyErrorAccountLog(args, returnValue, methodName, "系统异常");
			}
		}
		long endTime = System.currentTimeMillis();

		RequestLogEntity orderRequestLogEntity = new RequestLogEntity();
		orderRequestLogEntity.setUrl(fullPath);
		orderRequestLogEntity.setHeader(headerSB.toString());
		orderRequestLogEntity.setRequest(sb.toString());
		orderRequestLogEntity.setResponse(JSON.toJSONString(returnValue));
		orderRequestLogEntity.setUseTime(endTime - beginTime);
		logService.sendRequestLog(orderRequestLogEntity);
		logger.info("RA:" + runningAccount + " " + "Method [" + getMethod().getName() + "] " + AdamSysConstants.LINE_SEPARATOR + "returned [" + JSON.toJSONString(returnValue) + "]" + "useTime:" + (endTime - beginTime));
		return returnValue;
	}

	/**
	 * Invoke the method after resolving its argument values in the context of
	 * the given request.
	 * <p>
	 * Argument values are commonly resolved through
	 * {@link HandlerMethodArgumentResolver}s. The {@code provideArgs} parameter
	 * however may supply argument values to be used directly, i.e. without
	 * argument resolution. Examples of provided argument values include a
	 * {@link WebDataBinder}, a {@link SessionStatus}, or a thrown exception
	 * instance. Provided argument values are checked before argument resolvers.
	 * 
	 * @param request
	 *            the current request
	 * @param mavContainer
	 *            the ModelAndViewContainer for this request
	 * @param providedArgs
	 *            "given" arguments matched by type, not resolved
	 * @return the raw value returned by the invoked method
	 * @exception Exception
	 *                raised if no suitable argument resolver can be found, or
	 *                if the method raised an exception
	 */
	public Object invokeForRequest1(NativeWebRequest request, ModelAndViewContainer mavContainer, Object... providedArgs) throws Exception {

		Object[] args = getMethodArgumentValues(request, mavContainer, providedArgs);
		if (logger.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder("Invoking [");
			sb.append(getBeanType().getSimpleName()).append(".");
			sb.append(getMethod().getName()).append("] method with arguments ");
			sb.append(Arrays.asList(args));
			logger.trace(sb.toString());
		}
		Object returnValue = doInvoke(args);
		if (logger.isTraceEnabled()) {
			logger.trace("Method [" + getMethod().getName() + "] returned [" + returnValue + "]");
		}
		return returnValue;
	}

	private Object doBefore(String url, Map<String, String> headersMap, Object[] income, Object output) throws Exception {
		IRequestHook requestHook = SpringContextUtils.getSpringBeanByType(IRequestHook.class);
		if (null != requestHook) {
			return requestHook.doBefore(url, headersMap, income, output);
		}
		return null;
	}

	private Object doAfter(String url, Map<String, String> headersMap, Object[] income, Object output) throws Exception {
		IRequestHook requestHook = SpringContextUtils.getSpringBeanByType(IRequestHook.class);
		if (null != requestHook) {
			return requestHook.doAfter(url, headersMap, income, output);
		}
		return null;
	}

	private static final String TYPE_NAME_PREFIX = "class ";

	private static String getClassName(Type type) {
		if (type == null) {
			return "";
		}
		String className = type.toString();
		if (className.startsWith(TYPE_NAME_PREFIX)) {
			className = className.substring(TYPE_NAME_PREFIX.length());
		}
		return className;
	}

	private static Class<?> getClass(Type type) throws ClassNotFoundException {
		String className = getClassName(type);
		if (className == null || className.isEmpty()) {
			return null;
		}
		return Class.forName(className);
	}

	/**
	 * Get the method argument values for the current request.
	 */
	private Object[] getMethodArgumentValues(NativeWebRequest request, ModelAndViewContainer mavContainer, Object... providedArgs) throws Exception {

		String paramStr = request.getParameter("json");
		MethodParameter[] parameters = getMethodParameters();
		if (!StringUtils.isBlank(paramStr) && parameters.length == 1) {
			Object[] args = new Object[1];
			args[0] = JSON.parseObject(paramStr, getClass(parameters[0].getGenericParameterType()));
			return args;
		}

		Object[] args = new Object[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			MethodParameter parameter = parameters[i];
			parameter.initParameterNameDiscovery(this.parameterNameDiscoverer);
			GenericTypeResolver.resolveParameterType(parameter, getBean().getClass());
			args[i] = resolveProvidedArgument(parameter, providedArgs);
			if (args[i] != null) {
				continue;
			}
			if (this.argumentResolvers.supportsParameter(parameter)) {
				try {
					args[i] = this.argumentResolvers.resolveArgument(parameter, mavContainer, request, this.dataBinderFactory);
					continue;
				} catch (Exception ex) {
					if (logger.isDebugEnabled()) {
						logger.debug(getArgumentResolutionErrorMessage("Error resolving argument", i), ex);
					}
					throw ex;
				}
			}
			if (args[i] == null) {
				String msg = getArgumentResolutionErrorMessage("No suitable resolver for argument", i);
				throw new IllegalStateException(msg);
			}
		}
		return args;
	}

	private String getArgumentResolutionErrorMessage(String message, int index) {
		MethodParameter param = getMethodParameters()[index];
		message += " [" + index + "] [type=" + param.getParameterType().getName() + "]";
		return getDetailedErrorMessage(message);
	}

	/**
	 * Adds HandlerMethod details such as the controller type and method
	 * signature to the given error message.
	 * 
	 * @param message
	 *            error message to append the HandlerMethod details to
	 */
	protected String getDetailedErrorMessage(String message) {
		StringBuilder sb = new StringBuilder(message).append("\n");
		sb.append("HandlerMethod details: \n");
		sb.append("Controller [").append(getBeanType().getName()).append("]\n");
		sb.append("Method [").append(getBridgedMethod().toGenericString()).append("]\n");
		return sb.toString();
	}

	/**
	 * Attempt to resolve a method parameter from the list of provided argument
	 * values.
	 */
	private Object resolveProvidedArgument(MethodParameter parameter, Object... providedArgs) {
		if (providedArgs == null) {
			return null;
		}
		for (Object providedArg : providedArgs) {
			if (parameter.getParameterType().isInstance(providedArg)) {
				return providedArg;
			}
		}
		return null;
	}

	/**
	 * Invoke the handler method with the given argument values.
	 */
	protected Object doInvoke(Object... args) throws Exception {
		ReflectionUtils.makeAccessible(getBridgedMethod());
		try {
			return getBridgedMethod().invoke(getBean(), args);
		} catch (IllegalArgumentException ex) {
			assertTargetBean(getBridgedMethod(), getBean(), args);
			throw new IllegalStateException(getInvocationErrorMessage(ex.getMessage(), args), ex);
		} catch (InvocationTargetException ex) {
			// Unwrap for HandlerExceptionResolvers ...
			Throwable targetException = ex.getTargetException();
			if (targetException instanceof RuntimeException) {
				throw (RuntimeException) targetException;
			} else if (targetException instanceof Error) {
				throw (Error) targetException;
			} else if (targetException instanceof Exception) {
				throw (Exception) targetException;
			} else {
				String msg = getInvocationErrorMessage("Failed to invoke controller method", args);
				throw new IllegalStateException(msg, targetException);
			}
		}
	}

	/**
	 * Assert that the target bean class is an instance of the class where the
	 * given method is declared. In some cases the actual controller instance at
	 * request- processing time may be a JDK dynamic proxy (lazy initialization,
	 * prototype beans, and others). {@code @Controller}'s that require proxying
	 * should prefer class-based proxy mechanisms.
	 */
	private void assertTargetBean(Method method, Object targetBean, Object[] args) {
		Class<?> methodDeclaringClass = method.getDeclaringClass();
		Class<?> targetBeanClass = targetBean.getClass();
		if (!methodDeclaringClass.isAssignableFrom(targetBeanClass)) {
			String msg = "The mapped controller method class '" + methodDeclaringClass.getName() + "' is not an instance of the actual controller bean instance '" + targetBeanClass.getName() + "'. If the controller requires proxying " + "(e.g. due to @Transactional), please use class-based proxying.";
			throw new IllegalStateException(getInvocationErrorMessage(msg, args));
		}
	}

	private String getInvocationErrorMessage(String message, Object[] resolvedArgs) {
		StringBuilder sb = new StringBuilder(getDetailedErrorMessage(message));
		sb.append("Resolved arguments: \n");
		for (int i = 0; i < resolvedArgs.length; i++) {
			sb.append("[").append(i).append("] ");
			if (resolvedArgs[i] == null) {
				sb.append("[null] \n");
			} else {
				sb.append("[type=").append(resolvedArgs[i].getClass().getName()).append("] ");
				sb.append("[value=").append(resolvedArgs[i]).append("]\n");
			}
		}
		return sb.toString();
	}

}
