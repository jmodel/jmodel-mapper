package com.github.jmodel.mapper.impl.builders;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.jmodel.api.Model;
import com.github.jmodel.mapper.api.Builder;

public class JsonNodeBuilder implements Builder<JsonNode> {

	public JsonNode process(Model targetModel) {
		return JsonBuilderHelper.buildJsonNode(targetModel);
	}

}
