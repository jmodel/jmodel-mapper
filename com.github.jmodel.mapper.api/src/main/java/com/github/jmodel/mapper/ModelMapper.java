package com.github.jmodel.mapper;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.github.jmodel.FormatEnum;
import com.github.jmodel.ModelException;
import com.github.jmodel.api.domain.Model;
import com.github.jmodel.mapper.api.MappingEngine;
import com.github.jmodel.mapper.api.MappingEngineFactoryService;
import com.github.jmodel.mapper.api.domain.Mapping;

/**
 * Public API for model mapper.
 * 
 * @author jianni@hotmail.com
 *
 */
public class ModelMapper {

	/**
	 * JDK Logger
	 */
	private final static Logger logger = Logger.getLogger(ModelMapper.class.getName());

	/**
	 * The pattern of mapping URI.
	 */
	private static String NAME_PATTERN = "([a-zA-Z_][a-zA-Z\\d_]*\\.)*[a-zA-Z_][a-zA-Z\\d_]*";

	public static <T> T convert(Object sourceObj, String mappingURI, Class<T> valueType) throws ModelException {

		return convert(sourceObj, mappingURI, null, valueType);
	}

	public static <T> T convert(Object sourceObj, String mappingURI, Map<String, Object> argsMap, Class<T> valueType)
			throws ModelException {

		MappingEngine mappingEngine = null;
		FormatEnum toFormat = null;

		try {

			final Mapping mapping = getMapping(mappingURI);
			logger.info(() -> "The mapping is constructed : " + mappingURI);

			toFormat = mapping.getToFormat();
			logger.info(() -> "The mapping : " + mappingURI + "will get result in " + mapping.getToFormat());

			mappingEngine = MappingEngineFactoryService.getInstance().getMappingEngine(toFormat);
			return mappingEngine.convert(sourceObj, mapping, argsMap, valueType);

		} catch (Exception e) {
			throw new ModelException("Failed to convert model", e);
		} finally {

			try {
				MappingEngineFactoryService.getInstance().releaseMappingEngine(toFormat, mappingEngine);
			} catch (Exception e) {
			}
		}
	}

	public static <T> T convert(Object sourceObj, FormatEnum fromFormat, FormatEnum toFormat, Class<T> valueType)
			throws ModelException {

		MappingEngine mappingEngine = null;

		try {

			mappingEngine = MappingEngineFactoryService.getInstance().getMappingEngine(toFormat);
			return mappingEngine.convert(sourceObj, fromFormat, toFormat, valueType);

		} catch (Exception e) {
			throw new ModelException("Failed to convert model", e);
		} finally {

			try {
				MappingEngineFactoryService.getInstance().releaseMappingEngine(toFormat, mappingEngine);
			} catch (Exception e) {
			}
		}
	}

	public static <T> T convert(Model sourceModel, FormatEnum toFormat, Class<T> valueType) throws ModelException {

		MappingEngine mappingEngine = null;

		try {

			mappingEngine = MappingEngineFactoryService.getInstance().getMappingEngine(toFormat);
			return mappingEngine.convert(sourceModel, toFormat, valueType);

		} catch (Exception e) {
			throw new ModelException("Failed to convert model", e);
		} finally {

			try {
				MappingEngineFactoryService.getInstance().releaseMappingEngine(toFormat, mappingEngine);
			} catch (Exception e) {
			}
		}
	}

	private static Mapping getMapping(String mappingURI) throws ModelException {

		if (mappingURI == null || !Pattern.matches(NAME_PATTERN, mappingURI)) {
			throw new ModelException("MappingURI is wrong");
		}

		// TODO consider more loading mechanism later, local or remote
		Class<?> mappingClz;
		try {
			mappingClz = Class.forName(mappingURI);
		} catch (ClassNotFoundException e) {
			throw new ModelException("Mapping is not found", e);
		}

		Mapping mapping = null;
		try {
			Method method = mappingClz.getMethod("getInstance");
			mapping = (Mapping) (method.invoke(null));
			return mapping;
		} catch (Exception e) {
			throw new ModelException("Could not get instance of Mapping", e);
		}

	}

}
