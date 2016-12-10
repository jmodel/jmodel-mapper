package com.github.jmodel.mapper.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.github.jmodel.api.Analyzer;
import com.github.jmodel.api.AnalyzerFactoryService;
import com.github.jmodel.api.Array;
import com.github.jmodel.api.Entity;
import com.github.jmodel.api.Field;
import com.github.jmodel.api.FormatEnum;
import com.github.jmodel.api.IllegalException;
import com.github.jmodel.api.Model;
import com.github.jmodel.impl.ArrayImpl;
import com.github.jmodel.impl.EntityImpl;
import com.github.jmodel.impl.FieldImpl;
import com.github.jmodel.mapper.api.Builder;
import com.github.jmodel.mapper.api.Mapping;

public abstract class AbstractConvertEngine {

	protected ResourceBundle messages;

	protected static String DEFAULT_PACKAGE_NAME = "com.github.jmodel.mapper";

	protected static String NAME_PATTERN = "([a-zA-Z_][a-zA-Z\\d_]*\\.)*[a-zA-Z_][a-zA-Z\\d_]*";

	/**
	 * For auto converting by source object
	 * 
	 * @param sourceObj
	 * @param fromFormat
	 * @param toFormat
	 * @param currentLocale
	 * @return
	 */
	protected <T> Object getResult(T sourceObj, FormatEnum fromFormat, FormatEnum toFormat, Locale currentLocale) {
		AnalyzerFactoryService analyzerFactoryService = AnalyzerFactoryService.getInstance();
		Analyzer analyzer = analyzerFactoryService.getAnalyzer(fromFormat, null);
		Builder<?> builder = getBuilder(toFormat);

		Model model = analyzer.process(new EntityImpl(), sourceObj, true);
		return builder.process(model);
	}

	/**
	 * For auto converting by model
	 * 
	 * @param model
	 * @param toFormat
	 * @param currentLocale
	 * @return
	 */
	protected Object getResult(Model model, FormatEnum toFormat, Locale currentLocale) {
		Builder<?> builder = getBuilder(toFormat);
		return builder.process(model);
	}

	/**
	 * For configured converting by source object
	 * 
	 * @param sourceObj
	 * @param mappingURI
	 * @param argsMap
	 * @param currentLocale
	 * @return
	 */
	protected <T> Object getResult(T sourceObj, String mappingURI, Map<String, Object> argsMap, Locale currentLocale) {

		messages = ResourceBundle.getBundle("com.github.jmodel.mapper.api.MessagesBundle", currentLocale);

		if (mappingURI == null || !Pattern.matches(NAME_PATTERN, mappingURI)) {
			throw new IllegalException(messages.getString("M_NAME_IS_ILLEGAL"));
		}

		// TODO consider more loading mechanism later, local or remote
		Class<?> mappingClz;
		try {
			mappingClz = Class.forName(mappingURI);
		} catch (ClassNotFoundException e) {
			throw new IllegalException(messages.getString("M_IS_MISSING"));
		}

		Mapping mapping;
		try {
			Method method = mappingClz.getMethod("getInstance");
			mapping = (Mapping) (method.invoke(null));
		} catch (Exception e) {
			throw new IllegalException(messages.getString("M_IS_ILLEGAL"));
		}

		AnalyzerFactoryService analyzerFactoryService = AnalyzerFactoryService.getInstance();
		Analyzer analyzer = analyzerFactoryService.getAnalyzer(mapping.getFromFormat(), null);
		Builder<?> builder = getBuilder(mapping.getToFormat());

		// check variables
		if (mapping.getRawVariables().size() > 0) {
			if (argsMap == null || argsMap.size() == 0) {
				throw new IllegalException(messages.getString("V_NOT_FOUND"));
			}
			if (argsMap.keySet().parallelStream().filter(s -> mapping.getRawVariables().contains(s)).count() == 0) {
				throw new IllegalException(messages.getString("V_NOT_FOUND"));
			}
		}

		Model sourceTemplateModel = mapping.getSourceTemplateModel();
		Model targetTemplateModel = mapping.getTargetTemplateModel();

		if (!mapping.isTemplateReady()) {
			populateModel(sourceTemplateModel, mapping.getRawSourceFieldPaths(), mapping.getSourceModelRecursiveMap());
			populateModel(targetTemplateModel, mapping.getRawTargetFieldPaths(), mapping.getTargetModelRecursiveMap());
			mapping.setTemplateReady(true);
		}

		Model sourceModel = analyzer.process(sourceTemplateModel.clone(), sourceObj, false);
		Model targetModel = targetTemplateModel.clone();
		fillTargetInstance(targetModel, new HashMap<String, Field>(), new HashMap<String, Model>());

		mapping.execute(sourceModel, targetModel, argsMap, currentLocale);
		cleanUnusedField(targetModel);
		return builder.process(targetModel);

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

	/**
	 * Populate template model (source and target) by configured field paths
	 * 
	 * @param root
	 * @param fieldPaths
	 */
	private void populateModel(final Model root, final List<String> fieldPaths,
			final Map<String, Boolean> modelRecursiveMap) {
		final Map<String, Object> modelOrFieldMap = new HashMap<String, Object>();

		for (String fieldPath : fieldPaths) {

			String[] paths = fieldPath.split("\\.");

			String currentPath = "";
			String parentPath = "";

			for (int i = 0; i < paths.length - 1; i++) {

				if (parentPath.equals("")) {
					currentPath = paths[i];
				} else {
					currentPath = parentPath.replace("[]", "[0]") + "." + paths[i];
				}
				Model currentModel = (Model) modelOrFieldMap.get(currentPath);
				if (currentModel == null) {
					// create model object
					if (parentPath.equals("")) {
						currentModel = root;
					} else {
						if (paths[i].indexOf("[]") != -1) {
							currentModel = new ArrayImpl();
							currentModel.setName(StringUtils.substringBefore(paths[i], "[]"));
						} else {
							currentModel = new EntityImpl();
							currentModel.setName(paths[i]);
						}
					}
					if (currentModel.getName() == null) {
						currentModel.setName(paths[i]);
					}
					Boolean recursive = modelRecursiveMap.get(currentPath.replaceAll("\\[0\\]", "\\[\\]"));
					if (recursive != null && recursive) {
						currentModel.setRecursive(true);
					}

					currentModel.setModelPath(currentPath);
					modelOrFieldMap.put(currentPath, currentModel);

					// if current Model is Array,create a existing entity
					if (currentModel instanceof Array) {
						Entity subEntity = new EntityImpl();
						String entityName = StringUtils.substringBefore(paths[i], "[]");
						subEntity.setName(entityName);
						currentPath = currentPath.replace("[]", "[0]");
						subEntity.setModelPath(currentPath);
						subEntity.setParentModel(currentModel);
						currentModel.getSubModels().add(subEntity);
						modelOrFieldMap.put(currentPath, subEntity);

					}

					// maintenence parent model relation
					Model parentModel = (Model) modelOrFieldMap.get(parentPath.replaceAll("\\[\\]", "\\[0\\]"));
					if (parentModel != null) {
						currentModel.setParentModel(parentModel);
						List<Model> subModelList = parentModel.getSubModels();
						if (subModelList == null) {
							subModelList = new ArrayList<Model>();
							parentModel.setSubModels(subModelList);
						}
						subModelList.add(currentModel);

					}

				}
				// set parentPath
				parentPath = currentPath;
			}

			// set field list
			String fieldName = paths[paths.length - 1];
			if (!fieldName.equals("_")) {
				currentPath = currentPath + "." + fieldName;
				Field currentField = (Field) modelOrFieldMap.get(currentPath);
				if (currentField == null) {
					currentField = new FieldImpl();
					currentField.setName(fieldName);
					modelOrFieldMap.put(currentPath, currentField);
					Entity currentModel = null;
					Object model = modelOrFieldMap.get(parentPath);
					if (model instanceof Entity) {
						currentModel = (Entity) model;
					} else if (model instanceof Array) {
						Array aModel = (Array) model;
						currentModel = (Entity) aModel.getSubModels().get(0);
					}
					List<Field> fieldList = currentModel.getFields();
					if (fieldList == null) {
						fieldList = new ArrayList<Field>();
						currentModel.setFields(fieldList);
					}
					fieldList.add(currentField);
				}
			}
		}
	}

	protected abstract Builder<?> getBuilder(FormatEnum toFormat);
}
