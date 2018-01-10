package com.github.jmodel.mapper.impl.builder;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.jmodel.api.entity.Array;
import com.github.jmodel.api.entity.Entity;
import com.github.jmodel.api.entity.Field;
import com.github.jmodel.api.entity.Model;

public class JsonBuilderHelper {

	private final static JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;

	public final static JsonNode buildJsonNode(final Model targetModel) {
		ObjectNode rootNode = jsonNodeFactory.objectNode();
		createJsonNode(jsonNodeFactory, rootNode, targetModel);
		return rootNode;
	}

	private final static JsonNode createJsonNode(final JsonNodeFactory factory, ObjectNode node, Model model) {

		if (model != null) {
			List<Field> fields = ((Entity) model).getFields();
			if (fields != null) {
				for (Field field : fields) {
					node.put(field.getName(), field.getValue());
				}
			}

			List<Model> subModels = model.getSubModels();
			if (subModels != null) {
				for (Model subModel : subModels) {
					if (subModel instanceof Entity) {
						ObjectNode subNode = factory.objectNode();
						node.set(subModel.getName(), createJsonNode(factory, subNode, subModel));
					} else if (subModel instanceof Array) {
						List<Model> sub = subModel.getSubModels();
						ArrayNode sNode1 = factory.arrayNode();
						for (Model s : sub) {
							ObjectNode sNode2 = factory.objectNode();
							sNode1.add(createJsonNode(factory, sNode2, s));
						}
						node.set(subModel.getName(), sNode1);
					}
				}
			}
		}
		return node;
	}

}
