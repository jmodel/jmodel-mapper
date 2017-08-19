package com.github.jmodel.mapper.impl.builder;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.jmodel.api.domain.Model;
import com.github.jmodel.mapper.api.builder.Builder;

public class JsonNodeBuilder implements Builder {

	@SuppressWarnings("unchecked")
	@Override
	public JsonNode process(Model targetModel) {
		return JsonBuilderHelper.buildJsonNode(targetModel);
	}

}
