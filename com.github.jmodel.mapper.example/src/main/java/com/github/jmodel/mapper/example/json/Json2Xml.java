package com.github.jmodel.mapper.example.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jmodel.mapper.ModelMapper;

public class Json2Xml {

	public static void main(String[] args) {

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode data = objectMapper.readTree("{\"Content\": {\"Name\": \"hello\"}}");
			String r = ModelMapper.convert(data, "com.github.jmodel.mapper.example.Json2Xml", String.class);
			System.out.println(r);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("yy");
		}
	}

}
