/**
 * 
 */
package org.springframework.adam.service.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.adam.client.ILogService;
import org.springframework.adam.common.bean.ResultVo;
import org.springframework.adam.service.AbsCallbacker;
import org.springframework.adam.service.AbsTasker;

/**
 * @author USER
 *
 */
public class DoFinalTasker extends AbsTasker<Object, Object> {

	private static final Log log = LogFactory.getLog(DoFinalTasker.class);

	public static final String TYPE = "final";

	public DoFinalTasker(ILogService logService) {
		super(null, logService, null, TYPE);
	}

	@Override
	public AbsCallbacker doTask(Object income, ResultVo<Object> output) throws Exception {
		if (null != output.getScc()) {
			output.getScc().onSuccess(null);
		}
		return null;
	}

}
