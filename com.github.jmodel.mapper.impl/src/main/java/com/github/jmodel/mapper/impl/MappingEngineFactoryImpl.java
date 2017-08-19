package com.github.jmodel.mapper.impl;

import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import com.github.jmodel.FormatEnum;
import com.github.jmodel.mapper.api.MappingEngine;
import com.github.jmodel.mapper.impl.engine.ConvertToJsonEngine;
import com.github.jmodel.mapper.impl.engine.ConvertToXmlEngine;
import com.github.jmodel.mapper.spi.MappingEngineFactory;

/**
 * Model mapping engine factory implementation.
 * 
 * @author jianni@hotmail.com
 *
 */
public class MappingEngineFactoryImpl extends BaseKeyedPooledObjectFactory<FormatEnum, MappingEngine>
		implements MappingEngineFactory {

	@Override
	public MappingEngine create(FormatEnum toFormat) throws Exception {
		switch (toFormat) {
		case JSON:
			return new ConvertToJsonEngine();
		case XML:
			return new ConvertToXmlEngine();
		default:
			return null;
		}
	}

	@Override
	public PooledObject<MappingEngine> wrap(MappingEngine value) {
		return new DefaultPooledObject<MappingEngine>(value);
	}

}
