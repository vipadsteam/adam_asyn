/**
 * 
 */
package org.springframework.adam.common.bean.map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

/**
 * @author USER
 *
 */
@Service
public class AdamBeanMapper implements InitializingBean {

	private MapperFacade mapper;

	@Override
	public void afterPropertiesSet() throws Exception {
		init();
	}

	protected synchronized void refresh() {
		if (null != mapper) {
			return;
		}
		init();
	}

	protected synchronized void init() {
		DefaultMapperFactory.Builder factoryBuilder = new DefaultMapperFactory.Builder();
		factoryBuilder.mapNulls(true);
		MapperFactory factory = factoryBuilder.build();
		mapper = factory.getMapperFacade();
	}

	public MapperFacade getMapper() {
		if (null == mapper) {
			refresh();
		}
		return mapper;
	}

	public void setMapper(MapperFacade mapper) {
		this.mapper = mapper;
	}
}
