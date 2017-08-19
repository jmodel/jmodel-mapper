package com.github.jmodel.mapper.impl.engine;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.jmodel.FormatEnum;
import com.github.jmodel.mapper.api.MappingEngine;
import com.github.jmodel.mapper.api.builder.Builder;
import com.github.jmodel.mapper.api.builder.BuilderFactoryService;
import com.github.jmodel.mapper.impl.AbstractMappingEngine;

public class ConvertToJsonEngine extends AbstractMappingEngine implements MappingEngine {

	private Map<Class<?>, Builder> builderMap;

	public ConvertToJsonEngine() {
		builderMap = new HashMap<Class<?>, Builder>();
		builderMap.put(JsonNode.class, BuilderFactoryService.getInstance().getBuilder(FormatEnum.JSON, JsonNode.class));
		builderMap.put(String.class, BuilderFactoryService.getInstance().getBuilder(FormatEnum.JSON, String.class));
	}

	@Override
	public <T> Builder getBuilder(Class<T> valueType) {
		return builderMap.get(valueType);
	}

}
