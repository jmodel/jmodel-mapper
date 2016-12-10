package com.github.jmodel.mapper.impl;

import com.github.jmodel.mapper.api.MappingEngine;
import com.github.jmodel.mapper.spi.MappingEngineFactory;
import com.github.jmodel.mapper.impl.engines.ConvertToBeanEngine;
import com.github.jmodel.mapper.impl.engines.ConvertToJsonNodeEngine;
import com.github.jmodel.mapper.impl.engines.ConvertToStringEngine;

public class MappingEngineFactoryImpl implements MappingEngineFactory {

	@SuppressWarnings("unchecked")
	@Override
	public <R> MappingEngine<R> getEngine(Class<R> returnType) {
		if (returnType.getName().equals("java.lang.String")) {
			return (MappingEngine<R>) new ConvertToStringEngine();
		} else if (returnType.getName().equals("com.fasterxml.jackson.databind.JsonNode")) {
			return (MappingEngine<R>) new ConvertToJsonNodeEngine();
		} else if (returnType.getName().equals("java.lang.Object")) {
			return (MappingEngine<R>) new ConvertToBeanEngine();
		} else {
			return null;
		}
	}
}
