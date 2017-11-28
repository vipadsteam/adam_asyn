package org.springframework.adam.common.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.aop.support.AopUtils;

public class AdamClassUtils {

	protected static Logger logger = Logger.getLogger(AdamClassUtils.class);

	private static final String STR_FINAL = " final ";

	/**
	 * Attempts to create a class from a String.
	 * 
	 * @param className
	 *            the name of the class to create.
	 * @return the class. CANNOT be NULL.
	 * @throws IllegalArgumentException
	 *             if the className does not exist.
	 */
	@SuppressWarnings("unchecked")
	public static <T> Class<T> loadClass(final String className) throws IllegalArgumentException {
		try {
			return (Class<T>) Class.forName(className);
		} catch (final ClassNotFoundException e) {
			throw new IllegalArgumentException(className + " class not found.");
		}
	}

	/**
	 * Creates a new instance of the given class by passing the given arguments
	 * to the constructor.
	 * 
	 * @param className
	 *            Name of class to be created.
	 * @param args
	 *            Constructor arguments.
	 * @return New instance of given class.
	 */
	public static <T> T newInstance(final String className, final Object... args) {
		return newInstance(AdamClassUtils.<T> loadClass(className), args);
	}

	/**
	 * Creates a new instance of the given class by passing the given arguments
	 * to the constructor.
	 * 
	 * @param clazz
	 *            Class of instance to be created.
	 * @param args
	 *            Constructor arguments.
	 * @return New instance of given class.
	 */
	public static <T> T newInstance(final Class<T> clazz, final Object... args) {
		final Class<?>[] argClasses = new Class[args.length];
		for (int i = 0; i < args.length; i++) {
			argClasses[i] = args[i].getClass();
		}
		try {
			return clazz.getConstructor(argClasses).newInstance(args);
		} catch (final Exception e) {
			throw new IllegalArgumentException("Error creating new instance of " + clazz, e);
		}
	}

	/**
	 * Gets the property descriptor for the named property on the given class.
	 * 
	 * @param clazz
	 *            Class to which property belongs.
	 * @param propertyName
	 *            Name of property.
	 * @return Property descriptor for given property or null if no property
	 *         with given name exists in given class.
	 */
	public static PropertyDescriptor getPropertyDescriptor(final Class<?> clazz, final String propertyName) {
		try {
			return getPropertyDescriptor(Introspector.getBeanInfo(clazz), propertyName);
		} catch (final IntrospectionException e) {
			throw new RuntimeException("Failed getting bean info for " + clazz, e);
		}
	}

	/**
	 * Gets the property descriptor for the named property from the bean info
	 * describing a particular class to which property belongs.
	 * 
	 * @param info
	 *            Bean info describing class to which property belongs.
	 * @param propertyName
	 *            Name of property.
	 * @return Property descriptor for given property or null if no property
	 *         with given name exists.
	 */
	public static PropertyDescriptor getPropertyDescriptor(final BeanInfo info, final String propertyName) {
		for (int i = 0; i < info.getPropertyDescriptors().length; i++) {
			final PropertyDescriptor pd = info.getPropertyDescriptors()[i];
			if (pd.getName().equals(propertyName)) {
				return pd;
			}
		}
		return null;
	}

	// public static Map<String,PropertyDescriptor>

	/**
	 * Sets the given property on the target JavaBean using bean instrospection.
	 * 
	 * @param propertyName
	 *            Property to set.
	 * @param value
	 *            Property value to set.
	 * @param target
	 *            Target java bean on which to set property.
	 */
	public static void setProperty(final String propertyName, final Object value, final Object target) {
		try {
			setProperty(propertyName, value, target, Introspector.getBeanInfo(target.getClass()));
		} catch (final IntrospectionException e) {
			throw new RuntimeException("Failed getting bean info on target JavaBean " + target, e);
		}
	}

	/**
	 * Sets the given property on the target JavaBean using bean instrospection.
	 * 
	 * @param propertyName
	 *            Property to set.
	 * @param value
	 *            Property value to set.
	 * @param target
	 *            Target JavaBean on which to set property.
	 * @param info
	 *            BeanInfo describing the target JavaBean.
	 */
	public static void setProperty(final String propertyName, final Object value, final Object target, final BeanInfo info) {
		try {
			final PropertyDescriptor pd = getPropertyDescriptor(info, propertyName);
			pd.getWriteMethod().invoke(target, value);
		} catch (final InvocationTargetException e) {
			throw new RuntimeException("Error setting property " + propertyName, e.getCause());
		} catch (final Exception e) {
			throw new RuntimeException("Error setting property " + propertyName, e);
		}
	}

	public static Object getBeanPropertyValue(Object beanObject, String propertyName) {
		PropertyDescriptor pd = getPropertyDescriptor(beanObject.getClass(), propertyName);
		try {
			Method m = pd.getReadMethod();
			return m != null ? m.invoke(beanObject, new Object[] {}) : null;
		} catch (Exception e) {
			logger.error("bean实体中没有获取到属性" + propertyName + "值," + e.getMessage());
		}
		return null;
	}

	public static Object getBeanDeepProperty(Object beanObject, String propertyName) {
		if (StringUtils.isEmpty(propertyName)) {
			return null;
		}
		Object obj = beanObject;
		String[] propertys = StringUtils.split(propertyName, ".");
		for (String property : propertys) {
			obj = getBeanPropertyValue(obj, property);
			if (obj == null) {
				return null;
			}
		}
		return obj;
	}

	public static List<Field> getBeanAllFields(final Class<?> clazz) {
		List<Field> fs = new ArrayList<Field>();
		Class<?> cl = clazz;
		while (cl != null) {
			for (Field f : cl.getDeclaredFields()) {
				if (!f.getName().equalsIgnoreCase("serialVersionUID"))
					fs.add(f);
			}
			cl = cl.getSuperclass();
		}
		return fs;
	}

	public static Map<String, PropertyDescriptor> getAllDescriptorMap(final Class<?> clazz) throws Exception {
		Map<String, PropertyDescriptor> fs = new HashMap<String, PropertyDescriptor>();
		BeanInfo info = Introspector.getBeanInfo(clazz);
		for (int i = 0; i < info.getPropertyDescriptors().length; i++) {
			final PropertyDescriptor pd = info.getPropertyDescriptors()[i];
			if (!"class".equals(pd.getName())) {
				fs.put(pd.getName(), pd);
			}
		}

		return fs;
	}

	/**
	 * 对象内属性对应的属性值
	 * 
	 * @param bean
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> describe(Object bean) throws Exception {
		Map<String, Object> fs = new HashMap<String, Object>();
		BeanInfo info = Introspector.getBeanInfo(bean.getClass());
		for (int i = 0; i < info.getPropertyDescriptors().length; i++) {
			final PropertyDescriptor pd = info.getPropertyDescriptors()[i];
			if (!"class".equals(pd.getName())) {
				fs.put(pd.getName(), pd.getReadMethod().invoke(bean, new Object[] {}));
			}
		}
		return fs;
	}

	/**
	 * 
	 * 尽量确保对list里面的对象正确执行obj里面的方法mthod 每次方法执行错误，重新支持，如果错误尝试executeCount次
	 * 
	 * @author Giant
	 * @param list
	 * @param obj
	 * @param method
	 * @param executeCount
	 *            错误执行后尝试次数
	 * @param args
	 *            method参数 （除了第一个为集合里面的对象，第二个以后自己补 如list的对象为 a
	 *            那么method的第一个参数为a的类型，method(a.class,args)
	 */
	public static void invokeMethods(List list, Object obj, Method method, int executeCount, Object... args) {
		int errorCount = 0;
		Object[] params = new Object[args == null ? 1 : args.length + 1];
		int k = 1;
		if (args != null) {
			for (Object param : args) {
				params[k] = param;
				k++;
			}
		}
		for (int i = 0; i < list.size(); i++) {
			Object row = list.get(i);
			params[0] = row;
			try {
				method.invoke(obj, params);
			} catch (Exception e) {
				logger.error(e);
				if (errorCount >= executeCount) {
					continue;
				} else {
					i--;
				}
				errorCount++;
			}
		}
	}

	/**
	 * 
	 * 分threadCount对集合list执行obj里面的method方法
	 * 
	 * @author Giant
	 * @param obj
	 * @param method
	 * @param list
	 *            处理集合
	 * @param threadCount
	 *            线程数目 ，如果list.size()%threadCount!=0 那么threadCount线程数目+1
	 * @param threadTimeout
	 *            线程处理时间超出时间
	 * @param Object...args
	 *            constructor的参数
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List openThreads(final List list, final Object obj, final Method method, Integer threadCount, Long threadTimeout, final Object... args) throws Exception {
		int threadMaxCellCount = 0;// 每个线程处理对象的最大数目
		if (list == null || list.size() == 0) {
			return null;
		} else if (list.size() <= threadCount) {
			threadCount = list.size();
			threadMaxCellCount = 1;
		} else if (list.size() % threadCount == 0) {
			threadMaxCellCount = list.size() / threadCount;
		} else {
			threadMaxCellCount = list.size() / threadCount;
			threadCount = threadCount + 1;// 多出一个线程处理余数
		}

		// if(threadCount != 0){
		// threadCount =
		// list.size()%threadMaxCellCount!=0?threadCount+1:threadCount;
		// }else{
		// threadCount = 1;
		// }
		ExecutorService es = Executors.newFixedThreadPool(threadCount);
		try {
			List<Future> futureList = new ArrayList<Future>();
			for (int i = 0; i < threadCount; i++) {
				List tempList = null;
				if (i + 1 == threadCount) {// 最后一次
					tempList = new Vector(list.subList(i * threadMaxCellCount, list.size()));
				} else {
					tempList = new Vector(list.subList(i * threadMaxCellCount, i * threadMaxCellCount + threadMaxCellCount));
				}
				// Object []params = new Object[args==null?1:args.length+1];
				// params[0] = tempList;
				// int k=1;
				// if(args!=null){
				// for(Object obj :args){
				// params[k]=obj;
				// k++;
				// }
				// }
				final List callList = tempList;
				Callable runer = new Callable() {
					public Object call() throws Exception {
						Object[] params = new Object[args == null ? 1 : args.length + 1];
						params[0] = callList;
						int k = 1;
						if (args != null) {
							for (Object param : args) {
								params[k] = param;
								k++;
							}
						}
						return method.invoke(obj, params);
					}
				};
				Future f = es.submit(runer);
				futureList.add(f);
			}
			List result = new ArrayList();
			// 判断线程是否都执行完
			int j = 0;
			for (Future f : futureList) {
				try {
					Object o = f.get(threadTimeout, TimeUnit.MILLISECONDS);// 设置超时时间
					result.add(o);
				} catch (Exception e) {
					logger.error(e);
					result.add(false);
				}
				j++;
				// System.out.println("线程完成："+j);
			}
			return result;
		} catch (Exception e) {
			throw e;
		} finally {
			es.shutdown();
		}
		// System.out.println("执行完一次操作");
	}

	/**
	 * 获取set里面的对象，是含有属性propertyName 值为 value的对象
	 * 
	 * @param set
	 * @param propertyName
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static Object getExistValue(Collection set, String propertyName, String value) throws Exception {
		if (set == null) {
			return null;
		}
		for (Object obj : set) {
			String beanValue = BeanUtils.getProperty(obj, propertyName);
			if (StringUtils.equals(value, beanValue)) {
				return obj;
			}
		}
		return null;
	}

	public static Map getKeyMapByList(List list, String[] keyFields) throws Exception {
		Map result = new HashMap();
		if (list == null || list.size() == 0) {
			return result;
		}
		for (Object obj : list) {
			StringBuilder key = new StringBuilder();
			for (String keyField : keyFields) {
				key.append(BeanUtils.getProperty(obj, keyField));
				key.append("_");
			}
			if (StringUtils.isNotBlank(key.toString())) {
				result.put(key.substring(0, key.length() - 1), obj);
			}
		}
		return result;
	}

	public static Map getKeyMapByList(List list, String keyField) throws Exception {
		String[] keyFields = { keyField };
		return getKeyMapByList(list, keyFields);
	}

	public static Map getKeyMapByList(List list, Integer[] keyFields) throws Exception {
		Map result = new HashMap();
		if (list == null || list.size() == 0) {
			return result;
		}
		for (Object obj : list) {
			List cell = (List) obj;
			StringBuilder key = new StringBuilder();
			for (Integer keyField : keyFields) {
				key.append(cell.get(keyField));
				key.append("_");
			}
			if (StringUtils.isNotBlank(key.toString())) {
				result.put(key.substring(0, key.length() - 1), obj);
			}
		}
		return result;
	}

	public static Map getKeyMapByList(List list, Integer keyField) throws Exception {
		return getKeyMapByList(list, new Integer[] { keyField });
	}

	/**
	 * 将keyField 值为主键，分类放入集合里
	 * 
	 * @param <T>
	 * @param list
	 * @param keyField
	 * @param val
	 * @return
	 * @throws Exception
	 */
	public static <T> Map<Object, List<T>> assortListByField(List<T> list, String keyField) throws Exception {
		Map<Object, List<T>> result = new HashMap<Object, List<T>>();
		if (list == null || list.size() == 0) {
			return result;
		}
		for (T obj : list) {
			Object objVal = BeanUtils.getProperty(obj, keyField);
			List<T> objList = null;
			if (result.get(objVal) == null) {
				objList = new ArrayList<T>();
				result.put(objVal, objList);
			} else {
				objList = result.get(objVal);
			}
			objList.add(obj);
		}
		return result;
	}

	/**
	 * @author Giant
	 * @param dest
	 * @param orig
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static void copyProperties(Object dest, Object orig) throws Exception {
		Class oclz = orig.getClass();
		Class dclz = dest.getClass();
		Map<String, PropertyDescriptor> omap = getAllDescriptorMap(oclz);
		Map<String, PropertyDescriptor> dmap = getAllDescriptorMap(dclz);
		Set<String> dset = dmap.keySet();
		for (String fieldName : dset) {
			PropertyDescriptor ofield = omap.get(fieldName);
			if (ofield != null) {
				Object value = ofield.getReadMethod().invoke(orig, new Object[] {});
				PropertyDescriptor dfield = dmap.get(fieldName);
				if (value != null) {
					dfield.getWriteMethod().invoke(dest, value);
				}
			}
		}
	}

	public static String getSplitStr(List beans, String propertyName, String separator) {
		if (beans == null || beans.size() == 0) {
			return "";
		}
		StringBuilder buf = new StringBuilder();
		for (Object bean : beans) {
			Object value = getBeanDeepProperty(bean, propertyName);
			if (value != null) {
				if (value instanceof String) {
					value = "'" + value + "'";
				}
				buf.append(value).append(separator);
			}
		}
		return StringUtils.removeEnd(buf.toString(), separator);
	}

	public static Class getTargetClass(Object service) {
		Class clazz = service.getClass();
		if (clazz.getCanonicalName().contains("$Proxy")) {
			clazz = AopUtils.getTargetClass(service);
		}
		return clazz;
	}
}
