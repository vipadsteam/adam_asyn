/**
 * 
 */
package org.springframework.adam.common.utils.holder;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.adam.common.utils.AdamRegexUtil;

/**
 * @author user
 *
 */
public class LocalIpHolder {

	private static final Log log = LogFactory.getLog(LocalIpHolder.class);

	private volatile static String localIp = null;

	public static String getIp() {
		if (StringUtils.isBlank(localIp)) {
			try {
				initLocalIp();
			} catch (Exception e) {
				log.error(e, e);
			}
		}

		return localIp;
	}

	private static void initLocalIp() throws Exception {
		Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
		String thisIp = "";
		while (e.hasMoreElements()) {
			Enumeration<InetAddress> ee = e.nextElement().getInetAddresses();
			while (ee.hasMoreElements()) {
				String ip = ee.nextElement().getHostAddress();
				if (!AdamRegexUtil.isIp(ip)) {
					continue;
				}
				if ("127.0.0.1".equals(ip)) {
					continue;
				}
				thisIp = ip;
			}
		}
		if (StringUtils.isBlank(thisIp)) {
			thisIp = InetAddress.getLocalHost().getHostAddress();
		}

		String pid = ManagementFactory.getRuntimeMXBean().getName();
		localIp = thisIp + "_" + pid;
	}

}
