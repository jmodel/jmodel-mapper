package com.github.jmodel.mapper.spi;

import com.github.jmodel.mapper.api.MappingEngine;

public interface MappingEngineFactory {

	public <R> MappingEngine<R> getEngine(Class<R> returnType);
}
