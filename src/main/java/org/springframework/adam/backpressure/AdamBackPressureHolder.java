/**
 * 
 */
package org.springframework.adam.backpressure;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.adam.common.utils.ThreadLocalHolder;

/**
 * @author USER
 *
 */
public class AdamBackPressureHolder {

	private static Map<String, BackPressure> bpMap = new ConcurrentHashMap<>();

	public static BackPressure get() {
		String interfaceName = ThreadLocalHolder.getName();
		return bpMap.computeIfAbsent(interfaceName, k -> new BackPressure(interfaceName).updateDic(AdamBPDicHolder.getDic(k)));
	}
	
	public static BackPressure get(String interfaceName) {
		return bpMap.computeIfAbsent(interfaceName, k -> new BackPressure(interfaceName).updateDic(AdamBPDicHolder.getDic(k)));
	}

	public static void update(String interfaceName) {
		BackPressure abp = bpMap.get(interfaceName);
		if (null == abp) {
			return;
		}
		abp.updateDic(AdamBPDicHolder.getDic(interfaceName));
	}

}
