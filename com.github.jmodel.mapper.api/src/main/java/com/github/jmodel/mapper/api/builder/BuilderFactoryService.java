package com.github.jmodel.mapper.api.builder;

import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import com.github.jmodel.FormatEnum;
import com.github.jmodel.mapper.spi.BuilderFactory;

/**
 * Builder factory service.
 * 
 * @author jianni@hotmail.com
 *
 */
public class BuilderFactoryService {

	private static BuilderFactoryService service;
	
	private ServiceLoader<BuilderFactory> loader;

	private BuilderFactoryService() {
		loader = ServiceLoader.load(BuilderFactory.class);
	}

	public static synchronized BuilderFactoryService getInstance() {
		if (service == null) {
			service = new BuilderFactoryService();
		}
		return service;
	}

	public <T> Builder getBuilder(FormatEnum toFormat, Class<T> valueType) {

		Builder builder = null;

		try {
			Iterator<BuilderFactory> builderFactorys = loader.iterator();
			while (builder == null && builderFactorys.hasNext()) {
				BuilderFactory builderFactory = builderFactorys.next();
				builder = builderFactory.getBuilder(toFormat, valueType);
			}
		} catch (ServiceConfigurationError serviceError) {
			builder = null;
			serviceError.printStackTrace();

		}
		return builder;
	}
}
