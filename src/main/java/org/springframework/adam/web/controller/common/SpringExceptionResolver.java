/**
 * 
 */
package org.springframework.adam.web.controller.common;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.adam.common.bean.contants.BaseReslutCodeConstants;
import org.springframework.adam.common.utils.AdamExceptionUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

/**
 * @author user
 *
 */
public class SpringExceptionResolver extends SimpleMappingExceptionResolver {

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
		return getErrorModelAndView(ex);
	}

	private ModelAndView getErrorModelAndView(Exception ex) {
		MappingJackson2JsonView view = new MappingJackson2JsonView();
		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("resultCode", BaseReslutCodeConstants.CODE_SYSTEM_ERROR);
		attributes.put("resultMsg", AdamExceptionUtils.getStackTrace(ex));
		attributes.put("data", null);
		view.setAttributesMap(attributes);
		ModelAndView mav = new ModelAndView(view);
		return mav;
	}
}
