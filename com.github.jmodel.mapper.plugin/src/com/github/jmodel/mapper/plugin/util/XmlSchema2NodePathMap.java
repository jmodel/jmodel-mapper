package com.github.jmodel.mapper.plugin.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.github.jmodel.api.IllegalException;
import org.apache.xerces.impl.Constants;
import org.apache.xerces.impl.xs.XMLSchemaLoader;
//import org.apache.xerces.utils.XMLSecurityManager;
//import org.apache.xerces.utils.XMLSecurityPropertyManager;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.grammars.Grammar;
import org.apache.xerces.xni.grammars.XSGrammar;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSTerm;
import org.apache.xerces.xs.XSTypeDefinition;

/**
 * A subset of XML schema is supported.
 * <ul>
 * <li>one and only one global element as root element. <a href=
 * "http://stackoverflow.com/questions/8854144/xml-schema-root-element">refer</a></li>
 * <li>attribute is unsupported</li>
 * </ul>
 * 
 * 
 * 
 * @author jianni
 *
 */
public class XmlSchema2NodePathMap {

	public static Map<String, TreeNode<String>> convert(String file) {

		Map<String, TreeNode<String>> nodePathMap = new HashMap<String, TreeNode<String>>();
		Map<XSObject, TreeNode<String>> xsObjMap = new HashMap<XSObject, TreeNode<String>>();
		XMLSchemaLoader xmlSchemaLoader = new XMLSchemaLoader();
//		xmlSchemaLoader.setParameter(Constants.XML_SECURITY_PROPERTY_MANAGER, new XMLSecurityPropertyManager());
//		xmlSchemaLoader.setParameter(Constants.SECURITY_MANAGER, new XMLSecurityManager());
		xmlSchemaLoader.setParameter(Constants.XERCES_FEATURE_PREFIX + Constants.SCHEMA_FULL_CHECKING, false);
		XMLInputSource input = new XMLInputSource(null, file, null);
		input.setSystemId(file);
		Grammar g = null;
		try {
			g = xmlSchemaLoader.loadGrammar(input);
			XSModel xsModel = ((XSGrammar) g).toXSModel();

			XSNamedMap map = xsModel.getComponents(XSConstants.ELEMENT_DECLARATION);
			if (map.getLength() != 1) {
				throw new IllegalException("one and only one global element can be support");
			}

			XSElementDeclaration eDec = (XSElementDeclaration) map.item(0);
			XSTypeDefinition tDef = eDec.getTypeDefinition();
			// for root element, no parent node path, and always be non multiple
			convertElement(tDef, eDec.getName(), null, false, xsObjMap, nodePathMap);

			return nodePathMap;

		} catch (XNIException e) {
			throw new IllegalException(e.getMessage());
		} catch (IOException e) {
			throw new IllegalException(e.getMessage());
		}
	}

	private static void convertElement(XSTypeDefinition currentTDef, String currentElementName, String parentNodePath,
			boolean isMultiple, Map<XSObject, TreeNode<String>> xsObjMap, Map<String, TreeNode<String>> nodePathMap) {

		TreeNode<String> currentNode = null;
		if (xsObjMap.containsKey(currentTDef)) {
			currentNode = xsObjMap.get(currentTDef);
		} else {
			currentNode = new TreeNode<String>(currentElementName);
			xsObjMap.put(currentTDef, currentNode);
		}

		String currentNodePath = null;
		if (parentNodePath != null) {
			NodeMeta meta = new NodeMeta();
			meta.setMultiple(isMultiple);
			if (currentTDef.getTypeCategory() == XSTypeDefinition.SIMPLE_TYPE) {
				meta.setLeaf(true);
			} else {
				meta.setLeaf(false);
			}
			nodePathMap.get(parentNodePath).addChild(currentNode, meta);
			if (isMultiple) {
				currentNodePath = parentNodePath + "." + currentElementName + "[]";
			} else {
				currentNodePath = parentNodePath + "." + currentElementName;
			}
		} else {
			currentNodePath = currentElementName;
		}
		nodePathMap.put(currentNodePath, currentNode);

		if (currentTDef.getTypeCategory() == XSTypeDefinition.COMPLEX_TYPE) {
			XSComplexTypeDefinition ctDef = (XSComplexTypeDefinition) currentTDef;
			convertComplexTypeDef(ctDef, currentNodePath, xsObjMap, nodePathMap);
		}
	}

	private static void convertComplexTypeDef(XSComplexTypeDefinition ctDef, String parentNodePath,
			Map<XSObject, TreeNode<String>> xsObjMap, Map<String, TreeNode<String>> nodePathMap) {

		if (ctDef.getContentType() == XSComplexTypeDefinition.CONTENTTYPE_ELEMENT
				|| ctDef.getContentType() == XSComplexTypeDefinition.CONTENTTYPE_MIXED) {
			XSParticle p = ctDef.getParticle();
			convertParticle(p, parentNodePath, xsObjMap, nodePathMap);
		} else {
			throw new IllegalException("Unrecognized content type");
		}
	}

	private static void convertParticle(XSParticle particle, String parentNodePath,
			Map<XSObject, TreeNode<String>> xsObjMap, Map<String, TreeNode<String>> nodePathMap) {

		XSTerm term = particle.getTerm();

		boolean multiple = false;
		if (particle.getMaxOccurs() > 1 || particle.getMaxOccursUnbounded()) {
			multiple = true;
		}

		if (term.getType() == XSConstants.ELEMENT_DECLARATION) {
			XSElementDeclaration eDec = (XSElementDeclaration) term;
			XSTypeDefinition tDef = eDec.getTypeDefinition();

			convertElement(tDef, eDec.getName(), parentNodePath, multiple, xsObjMap, nodePathMap);
		} else if (term.getType() == XSConstants.MODEL_GROUP) {
			XSModelGroup mg = (XSModelGroup) term;
			XSObjectList list = mg.getParticles();
			for (int i = 0; i < list.getLength(); i++) {
				XSParticle pp = (XSParticle) list.item(i);
				convertParticle(pp, parentNodePath, xsObjMap, nodePathMap);
			}
		}
	}

}
