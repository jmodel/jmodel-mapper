package com.github.jmodel.mapper;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.regex.Pattern;

import com.github.jmodel.FormatEnum;
import com.github.jmodel.ModelException;
import com.github.jmodel.api.entity.Model;
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
	 * The pattern of mapping URI.
	 */
	private static String NAME_PATTERN = "([a-zA-Z_][a-zA-Z\\d_]*\\.)*[a-zA-Z_][a-zA-Z\\d_]*";

	/**
	 * Convert sourceObj to a target object by mapping configuration. The type of
	 * target object is specified by valueType. The mapping engine and the value
	 * type is extendible.
	 * 
	 * @param sourceObj
	 *            the source object
	 * @param mappingURI
	 *            the mapping configuration written in mapping DSL
	 * @param valueType
	 *            the type of target object
	 * @return the target object
	 * @throws ModelException
	 *             the jmodel exception
	 */
	public static <T> T convert(Object sourceObj, String mappingURI, Class<T> valueType) throws ModelException {

		return convert(sourceObj, mappingURI, null, valueType);
	}

	/**
	 * Convert sourceObj to a target object by mapping configuration with arguments
	 * support. The type of target object is specified by valueType. The mapping
	 * engine and the value type is extendible.
	 * 
	 * @param sourceObj
	 *            the source object
	 * @param mappingURI
	 *            the mapping configuration written in mapping DSL
	 * @param argsMap
	 *            arguments used in mapping DSL
	 * @param valueType
	 *            the type of target object
	 * @return the target object
	 * @throws ModelException
	 *             the jmodel exception
	 */
	public static <T> T convert(Object sourceObj, String mappingURI, Map<String, Object> argsMap, Class<T> valueType)
			throws ModelException {

		MappingEngine mappingEngine = null;
		FormatEnum toFormat = null;

		try {
			Mapping mapping = getMapping(mappingURI);
			toFormat = mapping.getToFormat();
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

	/**
	 * Convert sourceObj to a target object with different format.The type of target
	 * object is specified by valueType. The mapping engine and the value type is
	 * extendible.
	 * 
	 * @param sourceObj
	 *            the source object
	 * @param fromFormat
	 *            the format of source object
	 * @param toFormat
	 *            the format of target object
	 * @param valueType
	 *            the type of target object
	 * @return the target object
	 * @throws ModelException
	 *             the jmodel exception
	 */
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

	/**
	 * Convert source model to a target object.The type of target object is
	 * specified by valueType. The mapping engine and the value type is extendible.
	 * 
	 * @param sourceModel
	 *            the source model
	 * @param toFormat
	 *            the format of target object
	 * @param valueType
	 *            the type of target object
	 * @return the target object
	 * @throws ModelException
	 *             the jmodel exception
	 */
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

	/**
	 * Get mapping configuration instance by mapping URI.
	 * 
	 * @param mappingURI
	 *            the mapping configuration URL
	 * @return the mapping instance
	 * @throws ModelException
	 *             the jmodel exception
	 */
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
