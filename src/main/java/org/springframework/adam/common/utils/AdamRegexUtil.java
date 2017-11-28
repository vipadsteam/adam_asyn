package org.springframework.adam.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式工具类
 * 
 * @author longshaota
 */
public class AdamRegexUtil {

	public static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern.compile("^\\d*$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	public static boolean isIp(String ipAddress) {
		String ip = "([0-9]*\\.){3}[0-9]*";
		Pattern pattern = Pattern.compile(ip);
		Matcher matcher = pattern.matcher(ipAddress);
		return matcher.matches();
	}
}
