package com.github.jmodel.mapper.api;

import java.util.Map;

import com.github.jmodel.FormatEnum;
import com.github.jmodel.ModelException;
import com.github.jmodel.api.entity.Model;
import com.github.jmodel.mapper.api.builder.Builder;
import com.github.jmodel.mapper.api.domain.Mapping;
import com.github.jmodel.mapper.spi.MappingEngineFactory;

/**
 * Mapping engine is used to convert a source object to a specified target
 * object by mapping configuration. Mapping configuration is written in mapping
 * DSL language.
 * 
 * @author jianni@hotmail.com
 *
 */
public interface MappingEngine {

	/**
	 * convert source object to target object by mapping configuration
	 * 
	 * @param sourceObj
	 *            source object
	 * @param mapping
	 *            mapping configuration
	 * @param valueType
	 *            type of return
	 * @param <T>
	 *            type of return
	 * @return target object
	 * @throws ModelException
	 *             model exception
	 */
	public <T> T convert(Object sourceObj, Mapping mapping, Class<T> valueType) throws ModelException;

	/**
	 * convert source object to target object by mapping configuration with
	 * variables.
	 * 
	 * @param sourceObj
	 *            source object
	 * @param mapping
	 *            mapping configuration
	 * @param argsMap
	 *            variables in mapping configuration
	 * @param valueType
	 *            type of return
	 * @param <T>
	 *            type of return
	 * @return target object
	 * @throws ModelException
	 *             model exception
	 */
	public <T> T convert(Object sourceObj, Mapping mapping, Map<String, Object> argsMap, Class<T> valueType)
			throws ModelException;

	/**
	 * convert source object to target object with a different format. e.g. convert
	 * json to xml.
	 * 
	 * @param sourceObj
	 *            source object
	 * @param fromFormat
	 *            the format of source object
	 * @param toFormat
	 *            the format of target object
	 * @param valueType
	 *            type of return
	 * @param <T>
	 *            type of return
	 * @return target object
	 * @throws ModelException
	 *             model exception
	 */
	public <T> T convert(Object sourceObj, FormatEnum fromFormat, FormatEnum toFormat, Class<T> valueType)
			throws ModelException;

	/**
	 * convert source generic model instance to target object.
	 * 
	 * @param sourceModel
	 *            source generic model instance
	 * @param toFormat
	 *            the format of target object
	 * @param valueType
	 *            type of return
	 * @param <T>
	 *            type of return
	 * @return target object
	 * @throws ModelException
	 *             model exception
	 */
	public <T> T convert(Model sourceModel, FormatEnum toFormat, Class<T> valueType) throws ModelException;

	/**
	 * mapping engine has a set of builders.
	 * 
	 * @param valueType
	 *            type of return
	 * @param <T>
	 *            type of return
	 * @return builder
	 */
	public <T> Builder getBuilder(Class<T> valueType);

	/**
	 * set mapping engine factory
	 * 
	 * @param mappingEngineFactory
	 *            mapping engine factory
	 * @throws ModelException
	 *             model exception
	 */
	public void setMappingEngineFactory(MappingEngineFactory mappingEngineFactory) throws ModelException;

	/**
	 * get mapping engine factory
	 * 
	 * @return MappingEngineFactory mapping engine factory implementation
	 * @throws ModelException
	 *             model exception
	 */
	public MappingEngineFactory getMappingEngineFactory() throws ModelException;

}
