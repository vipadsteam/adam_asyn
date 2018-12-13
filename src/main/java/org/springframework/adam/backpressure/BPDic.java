package org.springframework.adam.backpressure;

public class BPDic {

	private static boolean autoRateFlag = false;

	private static String minRate = "";

	private static String maxRate = "";

	private static String errorStep = "";

	private static String fixStep = "";

	public static void update(String key, String value) {
		if ("bp_arf".equals(key)) {
			if ("TRUE".equals(value)) {
				autoRateFlag = true;
			} else {
				autoRateFlag = false;
			}
		} else if ("bp_minRate".equals(key)) {
			minRate = value;
		} else if ("bp_maxRate".equals(key)) {
			maxRate = value;
		} else if ("bp_errorStep".equals(key)) {
			errorStep = value;
		} else if ("bp_fixStep".equals(key)) {
			fixStep = value;
		}
	}

	/**
	 * @return the minRate
	 */
	public static String getMinRate() {
		return minRate;
	}

	/**
	 * @param minRate
	 *            the minRate to set
	 */
	public static void setMinRate(String minRate) {
		BPDic.minRate = minRate;
	}

	/**
	 * @return the maxRate
	 */
	public static String getMaxRate() {
		return maxRate;
	}

	/**
	 * @param maxRate
	 *            the maxRate to set
	 */
	public static void setMaxRate(String maxRate) {
		BPDic.maxRate = maxRate;
	}

	/**
	 * @return the errorStep
	 */
	public static String getErrorStep() {
		return errorStep;
	}

	/**
	 * @param errorStep
	 *            the errorStep to set
	 */
	public static void setErrorStep(String errorStep) {
		BPDic.errorStep = errorStep;
	}

	/**
	 * @return the fixStep
	 */
	public static String getFixStep() {
		return fixStep;
	}

	/**
	 * @param fixStep
	 *            the fixStep to set
	 */
	public static void setFixStep(String fixStep) {
		BPDic.fixStep = fixStep;
	}

	/**
	 * @return the autoRateFlag
	 */
	public static boolean isAutoRateFlag() {
		return autoRateFlag;
	}

	/**
	 * @param autoRateFlag
	 *            the autoRateFlag to set
	 */
	public static void setAutoRateFlag(boolean autoRateFlag) {
		BPDic.autoRateFlag = autoRateFlag;
	}

}
