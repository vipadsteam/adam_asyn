/**
 * 
 */
package org.springframework.adam.service.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.adam.client.ILogService;
import org.springframework.adam.common.bean.ResultVo;
import org.springframework.adam.common.utils.AdamExceptionUtils;
import org.springframework.adam.service.AbsCallbacker;
import org.springframework.adam.service.AbsTasker;
import org.springframework.adam.service.IService;
import org.springframework.adam.service.IServiceBefore;
import org.springframework.adam.service.chain.ServiceChain;

/**
 * @author USER
 *
 */
public class DoComplateTasker<T1, T2> extends AbsTasker<T1, T2> {

	private static final Log log = LogFactory.getLog(DoComplateTasker.class);

	public static final String TYPE = "complate";

	public DoComplateTasker(IService<T1, T2> service, ILogService logService, IServiceBefore<T1, T2> serviceBefore) {
		super(service, logService, serviceBefore, TYPE);
	}

	@Override
	public AbsCallbacker doTask(T1 income, ResultVo<T2> output) throws Exception {
		AbsCallbacker result = null;
		try {
			output.setNowTasker(this);
			if (null != this.serviceBefore && this.serviceBefore.dealServiceBefore(serviceInfo, income, output)) {
				return null;
			}
			result = exc(income, output, false);
		} catch (Throwable t) {
			log.error(this.getServiceInfo().getSimpleClassName() + "." + TYPE + " error occor:", t);
			output.setResultMsg("adam system error occur:" + AdamExceptionUtils.getStackTrace(t));
		}
		return result;
	}

}
