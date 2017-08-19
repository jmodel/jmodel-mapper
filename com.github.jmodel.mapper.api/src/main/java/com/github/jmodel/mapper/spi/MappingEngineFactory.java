package com.github.jmodel.mapper.spi;

import org.apache.commons.pool2.KeyedPooledObjectFactory;

import com.github.jmodel.FormatEnum;
import com.github.jmodel.mapper.api.MappingEngine;

/**
 * Model mapping engine factory interface.
 * 
 * @author jianni@hotmail.com
 *
 */
public interface MappingEngineFactory extends KeyedPooledObjectFactory<FormatEnum, MappingEngine> {

}
