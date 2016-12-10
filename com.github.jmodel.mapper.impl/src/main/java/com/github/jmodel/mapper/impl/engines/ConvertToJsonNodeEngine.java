package com.github.jmodel.mapper.impl.engines;

import java.util.Locale;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.jmodel.api.FormatEnum;
import com.github.jmodel.api.Model;
import com.github.jmodel.mapper.api.Builder;
import com.github.jmodel.mapper.api.BuilderFactoryService;
import com.github.jmodel.mapper.api.MappingEngine;
import com.github.jmodel.mapper.impl.AbstractConvertEngine;

public class ConvertToJsonNodeEngine extends AbstractConvertEngine implements MappingEngine<JsonNode> {

	public <T> JsonNode convert(T sourceObj, String mappingURI) {
		return convert(sourceObj, mappingURI, null, Locale.getDefault());
	}

	public <T> JsonNode convert(T sourceObj, String mappingURI, Locale currentLocale) {
		return convert(sourceObj, mappingURI, null, currentLocale);
	}

	@Override
	public <T> JsonNode convert(T sourceObj, String mappingURI, Map<String, Object> argsMap) {
		return convert(sourceObj, mappingURI, argsMap, Locale.getDefault());
	}

	@Override
	public <T> JsonNode convert(T sourceObj, String mappingURI, Map<String, Object> argsMap, Locale currentLocale) {
		return (JsonNode) super.getResult(sourceObj, mappingURI, argsMap, currentLocale);
	}

	@Override
	public <T> JsonNode convert(T sourceObj, FormatEnum fromFormat, FormatEnum toFormat) {
		return convert(sourceObj, fromFormat, toFormat, Locale.getDefault());
	}

	@Override
	public <T> JsonNode convert(T sourceObj, FormatEnum fromFormat, FormatEnum toFormat, Locale currentLocale) {
		return (JsonNode) super.getResult(sourceObj, fromFormat, toFormat, currentLocale);
	}

	@Override
	public JsonNode convert(Model sourceModel, FormatEnum toFormat) {
		return convert(sourceModel, toFormat, Locale.getDefault());
	}

	@Override
	public JsonNode convert(Model sourceModel, FormatEnum toFormat, Locale currentLocale) {
		return (JsonNode) super.getResult(sourceModel, toFormat, currentLocale);
	}

	@Override
	protected Builder<JsonNode> getBuilder(FormatEnum toFormat) {
		return BuilderFactoryService.getInstance().getBuilder(toFormat, JsonNode.class);
	}

}
