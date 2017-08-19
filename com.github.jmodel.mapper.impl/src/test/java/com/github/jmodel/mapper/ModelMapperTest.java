package com.github.jmodel.mapper;

import static org.junit.Assert.fail;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jmodel.FormatEnum;

public class ModelMapperTest {

	private ObjectMapper mapper = new ObjectMapper();

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testConvertObjectFormatEnumFormatEnumClassOfT() {

		InputStream sourceObj = null;

		try {

			sourceObj = getClass().getResourceAsStream("data.json");
			String a = ModelMapper.convert(mapper.readTree(sourceObj), FormatEnum.JSON, FormatEnum.XML, String.class);

			System.out.println(a);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sourceObj != null) {
				try {
					sourceObj.close();
				} catch (Exception e) {

				}
			}
		}

	}

}
