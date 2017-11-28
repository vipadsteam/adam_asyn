/**
 * 
 */
package org.springframework.adam.service.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.adam.client.ILogService;
import org.springframework.adam.common.bean.ResultVo;
import org.springframework.adam.common.bean.contants.BaseReslutCodeConstants;
import org.springframework.adam.common.utils.AdamExceptionUtils;
import org.springframework.adam.service.AbsCallbacker;
import org.springframework.adam.service.AbsTasker;
import org.springframework.adam.service.IService;
import org.springframework.adam.service.IServiceBefore;

/**
 * @author USER
 *
 */
public class DoServiceTasker<T1, T2> extends AbsTasker<T1, T2> {

	private static final Log log = LogFactory.getLog(DoServiceTasker.class);

	public static final String TYPE = "service";

	public DoServiceTasker(IService<T1, T2> service, ILogService logService, IServiceBefore<T1, T2> serviceBefore) {
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
			result = excSyn(income, output, true);
		} catch (Throwable t) {
			log.error(this.getServiceInfo().getSimpleClassName() + "." + TYPE + " error occor:", t);
			// doservice的任务并且任务内部都是成功的才设置成框架的error
			output.setResultCode(this.getClass(), BaseReslutCodeConstants.CODE_900000);
			output.setResultMsg("adam system error occor:" + AdamExceptionUtils.getStackTrace(t));
		}finally{
			output.setLatestServiceName(this.serviceInfo.getClassName());
		}
		return result;
	}

}
