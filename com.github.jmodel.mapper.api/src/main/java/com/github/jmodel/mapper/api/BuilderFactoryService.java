package com.github.jmodel.mapper.api;

import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import com.github.jmodel.api.FormatEnum;
import com.github.jmodel.mapper.spi.BuilderFactory;

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

	public <R> Builder<R> getBuilder(FormatEnum toFormat, Class<R> returnType) {
		Builder<R> builder = null;

		try {
			Iterator<BuilderFactory> builderFactorys = loader.iterator();
			while (builder == null && builderFactorys.hasNext()) {
				BuilderFactory builderFactory = builderFactorys.next();
				builder = builderFactory.getBuilder(toFormat, returnType);
			}
		} catch (ServiceConfigurationError serviceError) {
			builder = null;
			serviceError.printStackTrace();

		}
		return builder;
	}
}
