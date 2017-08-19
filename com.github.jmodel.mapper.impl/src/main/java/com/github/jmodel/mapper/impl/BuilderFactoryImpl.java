package com.github.jmodel.mapper.impl;

import com.github.jmodel.FormatEnum;
import com.github.jmodel.mapper.api.builder.Builder;
import com.github.jmodel.mapper.impl.builder.BeanBuilder;
import com.github.jmodel.mapper.impl.builder.JsonNodeBuilder;
import com.github.jmodel.mapper.impl.builder.JsonStringBuilder;
import com.github.jmodel.mapper.impl.builder.XmlStringBuilder;
import com.github.jmodel.mapper.spi.BuilderFactory;

/**
 * Builder factory implementation.
 * 
 * @author jianni@hotmail.com
 *
 */
public class BuilderFactoryImpl implements BuilderFactory {

	@Override
	public <T> Builder getBuilder(FormatEnum toFormat, Class<T> valueType) {

		switch (toFormat) {
		case JSON:
			if (valueType.getName().equals("com.fasterxml.jackson.databind.JsonNode")) {
				return new JsonNodeBuilder();
			} else if (valueType.getName().equals("java.lang.String")) {
				return new JsonStringBuilder();
			}
		case XML:
			if (valueType.getName().equals("java.lang.String")) {
				return new XmlStringBuilder();
			}

		default:
			return null;
		}

	}
}
