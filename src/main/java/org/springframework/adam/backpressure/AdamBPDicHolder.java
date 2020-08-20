/**
 * 
 */
package org.springframework.adam.backpressure;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.adam.common.bean.contants.AdamSysConstants;

/**
 * @author nixiaorui
 *
 */
public class AdamBPDicHolder {

	private static Map<String, BPDic> dicMap = new ConcurrentHashMap<>();

	/**
	 * 更新系统字典表时候可以调用这个方法
	 * 
	 * key:bp@ + name(RpcService.name, if not set default name is MethodSignature.toString) + @ + {arf|minRate|maxRate|errorStep|fixStep}
	 * eg:bp@DeferredResult org.adam.asyn.web.controller.DeferredAdamController.request1(RequestMsg)@arf
	 * 
	 * value:Ref#org.springframework.adam.backpressure.BPDic.update
	 * eg:Y
	 * @param prop
	 */
	public static void update(Map<String, String> prop) {
		Set<String> changeinterfaceNameSet = new HashSet<>();
		for (Entry<String, String> entry : prop.entrySet()) {
			if (!entry.getKey().startsWith("bp@")) {
				continue;
			}
			String[] keyArray = entry.getKey().split(AdamSysConstants.COLUMN_ATE);
			if (keyArray.length != 3) {
				continue;
			}

			if (update(keyArray[1], keyArray[2], entry.getValue())) {
				changeinterfaceNameSet.add(keyArray[1]);
			}
		}

		for (String changeinterfaceName : changeinterfaceNameSet) {
			AdamBackPressureHolder.update(changeinterfaceName);
		}
	}

	/**
	 * @param key
	 * @param value
	 * @return is changed
	 */
	public static boolean update(String interfaceName, String key, String value) {
		BPDic bpDic = dicMap.computeIfAbsent(interfaceName, k -> new BPDic());
		return bpDic.update(key, value);
	}

	public static BPDic getDic(String interfaceName) {
		return dicMap.get(interfaceName);
	}

	public static Map<String, BPDic> getDicMap() {
		return dicMap;
	}

}
