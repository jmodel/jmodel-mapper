package com.github.jmodel.mapper.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.jmodel.FormatEnum;
import com.github.jmodel.ModelBuilder;
import com.github.jmodel.ModelException;
import com.github.jmodel.api.entity.Array;
import com.github.jmodel.api.entity.Entity;
import com.github.jmodel.api.entity.Field;
import com.github.jmodel.api.entity.Model;
import com.github.jmodel.mapper.api.MappingEngine;
import com.github.jmodel.mapper.api.builder.Builder;
import com.github.jmodel.mapper.api.domain.Mapping;
import com.github.jmodel.mapper.spi.MappingEngineFactory;
import com.github.jmodel.utils.ModelHelper;

/**
 * Abstract mapping engine.
 * 
 * @author jianni@hotmail.com
 *
 */
public abstract class AbstractMappingEngine implements MappingEngine {

	protected static String DEFAULT_PACKAGE_NAME = "com.github.jmodel.mapper";

	private MappingEngineFactory mappingEngineFactory;

	public <T> T convert(Object sourceObj, Mapping mapping, Class<T> valueType) throws ModelException {
		return convert(sourceObj, mapping, null, valueType);
	}

	public <T> T convert(Object sourceObj, Mapping mapping, Map<String, Object> argsMap, Class<T> valueType)
			throws ModelException {

		if (argsMap != null) {
			checkVariables(mapping, argsMap);
		}

		Model sourceTemplateModel = mapping.getSourceTemplateModel();
		Model targetTemplateModel = mapping.getTargetTemplateModel();

		if (!mapping.isTemplateReady()) {
			ModelHelper.populateModel(sourceTemplateModel, mapping.getRawSourceFieldPaths(),
					mapping.getSourceModelRecursiveMap());
			ModelHelper.populateModel(targetTemplateModel, mapping.getRawTargetFieldPaths(),
					mapping.getTargetModelRecursiveMap());
			mapping.setTemplateReady(true);
		}

		Model sourceModel = sourceTemplateModel.clone();
		ModelBuilder.update(mapping.getFromFormat(), sourceModel, sourceObj);

		Model targetModel = targetTemplateModel.clone();
		fillTargetInstance(targetModel, new HashMap<String, Field>(), new HashMap<String, Model>());

		mapping.execute(sourceModel, targetModel, argsMap);
		cleanUnusedField(targetModel);

		Builder builder = getBuilder(valueType);
		return builder.process(targetModel);

	}

	public <T> T convert(Object sourceObj, FormatEnum fromFormat, FormatEnum toFormat, Class<T> valueType)
			throws ModelException {
		Model sourceModel = ModelBuilder.build(fromFormat, sourceObj, "hello");

		Builder builder = getBuilder(valueType);
		return builder.process(sourceModel);
	}

	public <T> T convert(Model sourceModel, FormatEnum toFormat, Class<T> valueType) throws ModelException {
		Builder builder = getBuilder(valueType);
		return builder.process(sourceModel);
	}

	/**
	 * Set value to fields of target model
	 * 
	 * @param targetInstanceModel
	 * @param targetFieldPathMap
	 * @param targetModelPathMap
	 */
	private void fillTargetInstance(Model targetInstanceModel, Map<String, Field> targetFieldPathMap,
			Map<String, Model> targetModelPathMap) {
		if (targetInstanceModel != null) {
			targetInstanceModel.setFieldPathMap(targetFieldPathMap);
			targetInstanceModel.setModelPathMap(targetModelPathMap);

			if (targetInstanceModel.getModelPath() == null) {
				targetInstanceModel.setModelPath(targetInstanceModel.getName());
			}

			targetModelPathMap.put(targetInstanceModel.getModelPath(), targetInstanceModel);

			if (targetInstanceModel instanceof Entity) {
				List<Field> fieldList = ((Entity) targetInstanceModel).getFields();
				if (fieldList != null) {
					for (Field field : fieldList) {
						field.setParentEntity((Entity) targetInstanceModel);
						targetFieldPathMap.put(targetInstanceModel.getModelPath() + "." + field.getName(), field);
					}
				}
			}

			List<Model> subModelList = targetInstanceModel.getSubModels();
			if (subModelList != null) {
				for (Model subModel : subModelList) {
					if (subModel instanceof Entity) {
						subModel.setModelPath(subModel.getParentModel().getModelPath() + "." + subModel.getName());
						fillTargetInstance(subModel, targetFieldPathMap, targetModelPathMap);
					}

					if (subModel instanceof Array) {
						subModel.setModelPath(targetInstanceModel.getModelPath() + "." + subModel.getName() + "[]");
						subModel.setModelPathMap(targetModelPathMap);
						subModel.setFieldPathMap(targetFieldPathMap);
						targetModelPathMap.put(subModel.getModelPath(), subModel);
						List<Model> subSubModelList = subModel.getSubModels();
						for (int i = 0; i < subSubModelList.size(); i++) {
							Model entityInArray = subSubModelList.get(i);
							entityInArray.setModelPath(
									targetInstanceModel.getModelPath() + "." + subModel.getName() + "[" + i + "]");
							fillTargetInstance(entityInArray, targetFieldPathMap, targetModelPathMap);
						}
					}
				}
			}
		}
	}

	/**
	 * Clean unused fields
	 * 
	 * @param targetModel
	 */
	private void cleanUnusedField(Model targetModel) {
		if (targetModel instanceof Entity) {
			Entity currentEntity = (Entity) targetModel;
			List<Field> newFieldList = new ArrayList<Field>();
			List<Field> fieldList = currentEntity.getFields();
			if (fieldList != null) {
				for (Field field : fieldList) {
					if (field.isUsed()) {
						newFieldList.add(field);
					}
				}
			}
			currentEntity.setFields(newFieldList);
		}

		List<Model> subModelList = targetModel.getSubModels();
		if (subModelList != null) {
			for (Model subModel : subModelList) {
				cleanUnusedField(subModel);
			}
		}
	}

	private void checkVariables(final Mapping mapping, Map<String, Object> argsMap) throws ModelException {
		
		if (mapping.getRawVariables().size() > 0) {
			if (argsMap == null || argsMap.size() == 0) {
				throw new ModelException("Variable is not found");
			}
			if (argsMap.keySet().parallelStream().filter(s -> mapping.getRawVariables().contains(s)).count() == 0) {
				throw new ModelException("Variable is not found");
			}
		}
	}

	@Override
	public void setMappingEngineFactory(MappingEngineFactory mappingEngineFactory) throws ModelException {
		this.mappingEngineFactory = mappingEngineFactory;
	}

	@Override
	public MappingEngineFactory getMappingEngineFactory() throws ModelException {
		return mappingEngineFactory;
	}

}
