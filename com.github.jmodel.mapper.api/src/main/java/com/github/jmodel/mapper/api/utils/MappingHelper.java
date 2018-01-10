package com.github.jmodel.mapper.api.utils;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.github.jmodel.api.entity.Array;
import com.github.jmodel.api.entity.Entity;
import com.github.jmodel.api.entity.Field;
import com.github.jmodel.api.entity.Model;

/**
 * Mapping helper.
 * 
 * @author jianni@hotmail.com
 *
 */
public class MappingHelper {

	public static void buildRelationForSubModel(Model parentModel, Model subModel, int index) {

		subModel.setParentModel(parentModel);
		subModel.setFieldPathMap(parentModel.getFieldPathMap());
		subModel.setModelPathMap(parentModel.getModelPathMap());

		String parentModelPath = parentModel.getModelPath();
		if (subModel instanceof Entity) {
			if (parentModel instanceof Array) {
				subModel.setModelPath(
						parentModelPath.substring(0, parentModelPath.lastIndexOf("[")) + "[" + index + "]");
			} else {
				subModel.setModelPath(parentModelPath + "." + subModel.getName());
			}
			for (Field field : ((Entity) subModel).getFields()) {
				subModel.getFieldPathMap().put(subModel.getModelPath() + "." + field.getName(), field);
			}
		} else {
			subModel.setModelPath(parentModelPath + "." + subModel.getName() + "[]");
		}

		subModel.getModelPathMap().put(subModel.getModelPath(), subModel);

		List<Model> subSubModels = subModel.getSubModels();
		if (subSubModels != null) {
			for (int i = 0; i < subSubModels.size(); i++) {
				Model subSubModel = subSubModels.get(i);
				buildRelationForSubModel(subModel, subSubModel, i);
			}
		}
	}

	public static <T> void arrayMapping(final Model rootSourceModel, final Model rootTargetModel,
			final Model mySourceModel, Model myTargetModel, final String sourceModelPath, final String targetModelPath,
			Integer targetIndex, final Predicate<String> p, final Consumer<T> c) {
		if (mySourceModel == null) {
			return;
		}
		arrayMapping(rootSourceModel, rootTargetModel, mySourceModel, myTargetModel, sourceModelPath, targetModelPath,
				targetIndex, false, p, c);
	}

	public static <T> void arrayMapping(final Model rootSourceModel, final Model rootTargetModel,
			final Model mySourceModel, Model myTargetModel, final String sourceModelPath, final String targetModelPath,
			Integer targetIndex, final boolean isAppend, final Predicate<String> p, final Consumer<T> c) {

		String[] modelPath = new String[3];
		modelPath[0] = sourceModelPath;
		modelPath[1] = targetModelPath;
		modelPath[2] = targetIndex + "";

		doIt(rootSourceModel, rootTargetModel, modelPath, mySourceModel, myTargetModel, sourceModelPath,
				targetModelPath, isAppend, p, c);
	}

	@SuppressWarnings("unchecked")
	private static <T> void doIt(final Model rootSourceModel, final Model rootTargetModel, final String[] modelPath,
			final Model mySourceModel, final Model myTargetModel, final String sourceModelPath,
			final String targetModelPath, final boolean isAppend, final Predicate<String> p, final Consumer<T> c) {

		/*
		 * source model is entity
		 */
		if (mySourceModel instanceof Entity) {
			modelPath[0] = sourceModelPath;

			/*
			 * target model is in a array path
			 */
			if (p != null && !p.test(modelPath[0])) {
				// not pass the condition
				return;
			}

			if (myTargetModel instanceof Array) {

				/*
				 * target model is array
				 */
				Entity targetEntityModel = (Entity) myTargetModel.getSubModels().get(0);
				if (!targetEntityModel.isUsed()) {
					modelPath[1] = myTargetModel.getSubModels().get(0).getModelPath();
				} else {
					if (isAppend) {
						targetEntityModel = (Entity) targetEntityModel.clone();
						myTargetModel.getSubModels().add(targetEntityModel);
						MappingHelper.buildRelationForSubModel(myTargetModel, targetEntityModel, 0);
						modelPath[1] = targetEntityModel.getModelPath();
						modelPath[2] = (myTargetModel.getSubModels().size() - 1) + "";
					} else {
						modelPath[1] = myTargetModel.getSubModels().get(Integer.valueOf(modelPath[2])).getModelPath();
					}
				}
				targetEntityModel.setUsed(true);
			} else {
				/*
				 * target model is entity, but it is in a array path
				 */
				modelPath[1] = targetModelPath;
			}

			c.accept((T) modelPath);
			return;
		}

		/*
		 * source model is array
		 */
		List<Model> subSourceEntities = mySourceModel.getSubModels();
		for (int i = 0; i < subSourceEntities.size(); i++) {
			Model subSourceEntity = subSourceEntities.get(i);
			modelPath[0] = subSourceEntity.getModelPath();
			if (modelPath[0] == null) { // no data in source model
				// myTargetModel.getParentModel().getSubModels().remove(myTargetModel);
				break;
			}
			if (p != null && !p.test(modelPath[0])) {
				// not pass the condition
				continue;
			}
			if (myTargetModel instanceof Entity) {
				/*
				 * target model is entity
				 */
				modelPath[1] = myTargetModel.getModelPath();
				c.accept((T) modelPath);
			} else {
				/*
				 * target model is array
				 */
				Entity targetEntityModel = (Entity) myTargetModel.getSubModels().get(0);
				if (!targetEntityModel.isUsed()) {
					// modelPath[1] = myTargetModel.getSubModels().get(0).getModelPath();
					String temp = myTargetModel.getSubModels().get(0).getModelPath();
					modelPath[1] = temp.substring(0, temp.lastIndexOf("[")) + "[0]";

				} else {
					if (isAppend || (!isAppend
							&& myTargetModel.getSubModels().size() < mySourceModel.getSubModels().size())) {
						targetEntityModel = (Entity) targetEntityModel.clone();
						myTargetModel.getSubModels().add(targetEntityModel);
						MappingHelper.buildRelationForSubModel(myTargetModel, targetEntityModel, i);
						String temp = targetEntityModel.getModelPath();
						modelPath[1] = temp.substring(0, temp.lastIndexOf("[")) + "[" + i + "]";
					} else {
						String temp = myTargetModel.getSubModels().get(i).getModelPath();
						modelPath[1] = temp.substring(0, temp.lastIndexOf("[")) + "[" + i + "]";
					}
				}
				targetEntityModel.setUsed(true);
				c.accept((T) modelPath);

				/*
				 * handle recursive
				 * 
				 * S[](*) => T[]
				 * 
				 * mySourceModel is array
				 */
				if (mySourceModel.isRecursive()) {
					for (Model subSubSourceArray : subSourceEntity.getSubModels()) {
						if (subSubSourceArray instanceof Array
								&& subSubSourceArray.getName().equals(mySourceModel.getName())) {
							// targetEntityModel = (Entity) targetEntityModel.clone();
							// myTargetModel.getSubModels().add(targetEntityModel);
							// MappingHelper.buildRelationForSubModel(myTargetModel, targetEntityModel);
							arrayMapping(rootSourceModel, rootTargetModel, subSubSourceArray, myTargetModel,
									subSubSourceArray.getModelPath(), myTargetModel.getModelPath(), 0, true, p, c);
							break;
						}
					}
				}
			}
		}
	}

}
