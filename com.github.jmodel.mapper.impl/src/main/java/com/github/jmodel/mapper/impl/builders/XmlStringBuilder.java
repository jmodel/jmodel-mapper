package com.github.jmodel.mapper.impl.builders;

import java.io.StringWriter;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.github.jmodel.api.Array;
import com.github.jmodel.api.Entity;
import com.github.jmodel.api.Field;
import com.github.jmodel.api.Model;
import com.github.jmodel.mapper.api.Builder;

public class XmlStringBuilder implements Builder<String> {

	public String process(Model targetModel) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = null;
		try {
			documentBuilder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Document xmlDoc = documentBuilder.newDocument();
		generateXml(targetModel, xmlDoc, null);
		StringWriter stringWriter = new StringWriter();
		try {
			Source source = new DOMSource(xmlDoc);
			StreamResult result = new StreamResult(stringWriter);
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			xformer.transform(source, result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringWriter.toString();
	}

	private void generateXml(Model model, Document document, Element parent) {
		// TODO Auto-generated method stub
		Element element = document.createElement(model.getName());
		if (parent != null) {
			parent.appendChild(element);
		} else {
			document.appendChild(element);
		}
		if (model instanceof Entity) {
			List<Field> fields = ((Entity) model).getFields();
			if (fields != null) {
				for (Field field : fields) {
					Element fieldElement = document.createElement(field.getName());
					fieldElement.appendChild(document.createTextNode(field.getValue()));
					element.appendChild(fieldElement);
				}
			}
		}
		List<Model> subModel = model.getSubModels();
		if (subModel != null) {
			for (Model subModels : subModel) {
				if (subModels instanceof Entity) {
					generateXml(subModels, document, element);
				} else if (subModels instanceof Array) {
					List<Model> sub = subModels.getSubModels();
					if (sub != null) {
						for (Model s : sub) {
							generateXml(s, document, element);
						}
					}
				}
			}
		}
	}

}
