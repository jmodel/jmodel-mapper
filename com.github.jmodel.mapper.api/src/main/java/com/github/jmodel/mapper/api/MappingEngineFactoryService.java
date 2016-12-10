package com.github.jmodel.mapper.api;

import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import com.github.jmodel.mapper.spi.MappingEngineFactory;

public class MappingEngineFactoryService {

	private static MappingEngineFactoryService service;
	private ServiceLoader<MappingEngineFactory> loader;

	private MappingEngineFactoryService() {
		loader = ServiceLoader.load(MappingEngineFactory.class);
	}

	public static synchronized MappingEngineFactoryService getInstance() {
		if (service == null) {
			service = new MappingEngineFactoryService();
		}
		return service;
	}

	public <R> MappingEngine<R> getEngine(Class<R> returnType) {
		MappingEngine<R> engine = null;

		try {
			Iterator<MappingEngineFactory> engineFactorys = loader.iterator();
			while (engine == null && engineFactorys.hasNext()) {
				MappingEngineFactory engineFactory = engineFactorys.next();
				engine = engineFactory.getEngine(returnType);
			}
		} catch (ServiceConfigurationError serviceError) {
			engine = null;
			serviceError.printStackTrace();

		}
		return engine;
	}
}
