package com.github.jmodel.mapper.api;

import java.util.Locale;
import java.util.Map;

import com.github.jmodel.api.FormatEnum;
import com.github.jmodel.api.Model;

public interface MappingEngine<R> {

	public <T> R convert(T sourceObj, String mappingURI);

	public <T> R convert(T sourceObj, String mappingURI, Map<String, Object> argsMap);

	public <T> R convert(T sourceObj, String mappingURI, Locale currentLocale);

	public <T> R convert(T sourceObj, String mappingURI, Map<String, Object> argsMap, Locale currentLocale);

	public <T> R convert(T sourceObj, FormatEnum fromFormat, FormatEnum toFormat);

	public <T> R convert(T sourceObj, FormatEnum fromFormat, FormatEnum toFormat, Locale currentLocale);

	public R convert(Model sourceModel, FormatEnum toFormat);

	public R convert(Model sourceModel, FormatEnum toFormat, Locale currentLocale);

}
