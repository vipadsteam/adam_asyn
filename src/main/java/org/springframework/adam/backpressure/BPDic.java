package org.springframework.adam.backpressure;

public class BPDic {

	private boolean autoRateFlag = false;

	private String minRate = "";

	private String maxRate = "";

	private String errorStep = "";

	private String fixStep = "";

	/**
	 * @param key
	 * @param value
	 * @return is changed
	 */
	public boolean update(String key, String value) {
		boolean result = false;
		if ("arf".equals(key)) {
			boolean autoRateFlagTmp = "Y".equals(value);
			result = (autoRateFlagTmp != autoRateFlag);
			autoRateFlag = autoRateFlagTmp;
		} else if ("minRate".equals(key)) {
			result = (minRate != value);
			minRate = value;
		} else if ("maxRate".equals(key)) {
			result = (maxRate != value);
			maxRate = value;
		} else if ("errorStep".equals(key)) {
			result = (errorStep != value);
			errorStep = value;
		} else if ("fixStep".equals(key)) {
			result = (fixStep != value);
			fixStep = value;
		}
		return result;
	}

	/**
	 * @return
	 */
	public boolean isAutoRateFlag() {
		return autoRateFlag;
	}

	/**
	 * @param autoRateFlag
	 */
	public void setAutoRateFlag(boolean autoRateFlag) {
		this.autoRateFlag = autoRateFlag;
	}

	/**
	 * @return the minRate
	 */
	public String getMinRate() {
		return this.minRate;
	}

	/**
	 * @param minRate
	 *            the minRate to set
	 */
	public void setMinRate(String minRate) {
		this.minRate = minRate;
	}

	/**
	 * @return the maxRate
	 */
	public String getMaxRate() {
		return this.maxRate;
	}

	/**
	 * @param maxRate
	 *            the maxRate to set
	 */
	public void setMaxRate(String maxRate) {
		this.maxRate = maxRate;
	}

	/**
	 * @return the errorStep
	 */
	public String getErrorStep() {
		return this.errorStep;
	}

	/**
	 * @param errorStep
	 *            the errorStep to set
	 */
	public void setErrorStep(String errorStep) {
		this.errorStep = errorStep;
	}

	/**
	 * @return the fixStep
	 */
	public String getFixStep() {
		return this.fixStep;
	}

	/**
	 * @param fixStep
	 *            the fixStep to set
	 */
	public void setFixStep(String fixStep) {
		this.fixStep = fixStep;
	}

}
