package com.github.jmodel.mapper.api;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.apache.commons.pool2.impl.GenericKeyedObjectPool;

import com.github.jmodel.FormatEnum;
import com.github.jmodel.ModelException;
import com.github.jmodel.mapper.spi.MappingEngineFactory;

/**
 * Model mapping engine factory service.
 * 
 * @author jianni@hotmail.com
 *
 */
public class MappingEngineFactoryService {

	private static MappingEngineFactoryService service;

	private ServiceLoader<MappingEngineFactory> loader;

	private Map<MappingEngineFactory, GenericKeyedObjectPool<FormatEnum, MappingEngine>> poolMap;

	private MappingEngineFactoryService() {
		loader = ServiceLoader.load(MappingEngineFactory.class);
		poolMap = new HashMap<MappingEngineFactory, GenericKeyedObjectPool<FormatEnum, MappingEngine>>();
	}

	public static synchronized MappingEngineFactoryService getInstance() {
		if (service == null) {
			service = new MappingEngineFactoryService();
		}
		return service;
	}

	public MappingEngine getMappingEngine(FormatEnum format) {

		MappingEngine mappingEngine = null;

		try {
			Iterator<MappingEngineFactory> engineFactorys = loader.iterator();
			while (mappingEngine == null && engineFactorys.hasNext()) {
				MappingEngineFactory mappingEngineFactory = engineFactorys.next();
				GenericKeyedObjectPool<FormatEnum, MappingEngine> pool = poolMap.get(mappingEngineFactory);
				if (pool == null) {
					pool = new GenericKeyedObjectPool<FormatEnum, MappingEngine>(mappingEngineFactory);
					poolMap.put(mappingEngineFactory, pool);
				}
				mappingEngine = pool.borrowObject(format);
				mappingEngine.setMappingEngineFactory(mappingEngineFactory);
			}
		} catch (ServiceConfigurationError serviceError) {
			mappingEngine = null;
			serviceError.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mappingEngine;
	}

	public void releaseMappingEngine(FormatEnum format, MappingEngine mappingEngine) throws ModelException {
		try {
			poolMap.get(mappingEngine.getMappingEngineFactory()).returnObject(format, mappingEngine);
		} catch (Exception e) {
			throw new ModelException("fff");
		}
	}
}
