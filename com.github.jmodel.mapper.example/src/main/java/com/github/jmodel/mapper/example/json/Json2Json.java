package com.github.jmodel.mapper.example.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jmodel.mapper.ModelMapper;

public class Json2Json {

	public static void main(String[] args) {

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode data = objectMapper.readTree("{\"Content\": {\"Name\": \"hello\"}}");
			JsonNode r = ModelMapper.convert(data, "com.github.jmodel.mapper.example.Json2Json", JsonNode.class);
			System.out.println("xx");
			System.out.println(r.toString());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("yy");
		}
	}

}
