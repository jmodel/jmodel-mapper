package com.github.jmodel.mapper.impl.engines;

import java.util.Locale;
import java.util.Map;

import com.github.jmodel.api.FormatEnum;
import com.github.jmodel.api.Model;
import com.github.jmodel.mapper.api.Builder;
import com.github.jmodel.mapper.api.BuilderFactoryService;
import com.github.jmodel.mapper.api.MappingEngine;
import com.github.jmodel.mapper.impl.AbstractConvertEngine;

public class ConvertToStringEngine extends AbstractConvertEngine implements MappingEngine<String> {

	public <T> String convert(T sourceObj, String mappingURI) {
		return convert(sourceObj, mappingURI, null, Locale.getDefault());
	}

	public <T> String convert(T sourceObj, String mappingURI, Locale currentLocale) {
		return convert(sourceObj, mappingURI, null, currentLocale);
	}

	@Override
	public <T> String convert(T sourceObj, String mappingURI, Map<String, Object> argsMap) {
		return convert(sourceObj, mappingURI, argsMap, Locale.getDefault());
	}

	@Override
	public <T> String convert(T sourceObj, String mappingURI, Map<String, Object> argsMap, Locale currentLocale) {
		return (String) super.getResult(sourceObj, mappingURI, argsMap, currentLocale);
	}

	@Override
	public <T> String convert(T sourceObj, FormatEnum fromFormat, FormatEnum toFormat) {
		return convert(sourceObj, fromFormat, toFormat, Locale.getDefault());
	}

	@Override
	public <T> String convert(T sourceObj, FormatEnum fromFormat, FormatEnum toFormat, Locale currentLocale) {
		return (String) super.getResult(sourceObj, fromFormat, toFormat, currentLocale);
	}

	@Override
	public String convert(Model sourceModel, FormatEnum toFormat) {
		return convert(sourceModel, toFormat, Locale.getDefault());
	}

	@Override
	public String convert(Model sourceModel, FormatEnum toFormat, Locale currentLocale) {
		return (String) super.getResult(sourceModel, toFormat, currentLocale);
	}

	@Override
	protected Builder<String> getBuilder(FormatEnum toFormat) {
		return BuilderFactoryService.getInstance().getBuilder(toFormat, String.class);
	}
}
