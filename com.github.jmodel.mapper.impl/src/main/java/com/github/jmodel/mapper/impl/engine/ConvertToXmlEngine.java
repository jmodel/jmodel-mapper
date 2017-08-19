package com.github.jmodel.mapper.impl.engine;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;

import com.github.jmodel.FormatEnum;
import com.github.jmodel.mapper.api.MappingEngine;
import com.github.jmodel.mapper.api.builder.Builder;
import com.github.jmodel.mapper.api.builder.BuilderFactoryService;
import com.github.jmodel.mapper.impl.AbstractMappingEngine;

public class ConvertToXmlEngine extends AbstractMappingEngine implements MappingEngine {

	private Map<Class<?>, Builder> builderMap;

	public ConvertToXmlEngine() {
		builderMap = new HashMap<Class<?>, Builder>();
		builderMap.put(Element.class, BuilderFactoryService.getInstance().getBuilder(FormatEnum.XML, Element.class));
		builderMap.put(String.class, BuilderFactoryService.getInstance().getBuilder(FormatEnum.XML, String.class));
	}

	@Override
	public <T> Builder getBuilder(Class<T> valueType) {
		return builderMap.get(valueType);
	}
}
