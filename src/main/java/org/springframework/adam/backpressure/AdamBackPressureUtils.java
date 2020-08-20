/**
 * 
 */
package org.springframework.adam.backpressure;

import org.apache.commons.lang3.StringUtils;
import org.springframework.adam.common.utils.ThreadLocalHolder;

/**
 * @author nixiaorui
 *
 */
public class AdamBackPressureUtils {

	public static void errIncrease() {
		errIncrease(ThreadLocalHolder.getName());
	}

	public static void errIncrease(String interfaceName) {
		if (StringUtils.isBlank(interfaceName)) {
			return;
		}
		BPDic bpDic = AdamBPDicHolder.getDic(interfaceName);
		if (null == bpDic || !bpDic.isAutoRateFlag()) {
			return;
		}
		AdamBackPressureHolder.get(interfaceName).errIncrease();
	}

}
