package com.github.jmodel.mapper.plugin.util

import org.eclipse.emf.ecore.EObject
import org.eclipse.xtext.xbase.XAbstractFeatureCall
import org.eclipse.xtext.xbase.XFeatureCall
import org.eclipse.xtext.xbase.XMemberFeatureCall
import java.util.Stack
import org.eclipse.xtext.xbase.XIfExpression
import com.github.jmodel.mapper.plugin.mappingLanguage.Filter
import com.github.jmodel.mapper.plugin.mappingLanguage.Block
import com.github.jmodel.mapper.plugin.mappingLanguage.SingleSourceFieldPath
import com.github.jmodel.mapper.plugin.mappingLanguage.Body
import com.github.jmodel.mapper.plugin.mappingLanguage.Mapping
import com.github.jmodel.mapper.plugin.mappingLanguage.DataType
import com.github.jmodel.mapper.plugin.mappingLanguage.BlockContent

class Util {

	def static boolean isPredict(String oper) {
		if (oper.equals("==") || oper.equals("!=") || oper.equals(">") || oper.equals(">=") || oper.equals("<") ||
			oper.equals("<=") || oper.equals("in") || oper.equals("!in") || oper.equals("||") || oper.equals("&&")) {
			return true
		} else {
			return false
		}
	}

	def static String operEnum(String oper) {
		switch oper {
			case "==": "com.github.jmodel.api.OperationEnum.EQUALS"
			case "!=": "com.github.jmodel.api.OperationEnum.NOTEQUALS"
			case ">": "com.github.jmodel.api.OperationEnum.GT"
			case ">=": "com.github.jmodel.api.OperationEnum.GTE"
			case "<": "com.github.jmodel.api.OperationEnum.LT"
			case "<=": "com.github.jmodel.api.OperationEnum.LTE"
			case "in": "com.github.jmodel.api.OperationEnum.IN"
			case "!in": "com.github.jmodel.api.OperationEnum.NOTIN"
			case "||": "com.github.jmodel.api.OperationEnum.OR"
			case "&&": "com.github.jmodel.api.OperationEnum.AND"
			case "+": "com.github.jmodel.api.OperationEnum.PLUS"
			case "*": "com.github.jmodel.api.OperationEnum.MULTIPLY"
			default: "The operation is not supported"
		}
	}

	def static String getFromSchema(EObject x) {
		if (x instanceof Mapping) {
			return x.from.schema
		} else {
			getFromSchema(x.eContainer)
		}
	}

	def static String getVariableName(String variableDeclaration) {
		variableDeclaration.substring(2, variableDeclaration.length - 1)
	}

	def static String getDataType(DataType dataType) {
		switch dataType {
			case DataType.STR: "com.github.jmodel.api.DataTypeEnum.STRING"
			case DataType.INT: "com.github.jmodel.api.DataTypeEnum.INTEGER"
			case DataType.LONG: "com.github.jmodel.api.DataTypeEnum.LONG"
			case DataType.BOOL: "com.github.jmodel.api.DataTypeEnum.BOOLEAN"
			case DataType.DEC: "com.github.jmodel.api.DataTypeEnum.BIGDECIMAL"
			case DataType.DATE: "com.github.jmodel.api.DataTypeEnum.DATE"
			default: "com.github.jmodel.api.DataTypeEnum.STRING"
		}
	}

	def static boolean isDot(String x) {
		if (x.equals(".")) {
			true
		} else {
			false
		}
	}

	def static boolean isInFilter(EObject x) {
		if (x instanceof Filter) {
			return true;
		} else if (x instanceof Block) {
			return false;
		} else {
			return isInFilter(x.eContainer);
		}
	}

	def static boolean isInAppendPath(EObject x) {
		if (x instanceof Body) {
			return false;
		} else if (x instanceof Block) {
			if (x.isAppend != null) {
				return true;
			}
		}
		return isInAppendPath(x.eContainer);
	}

	def static boolean isInXIfExpression(EObject x) {
		if (x instanceof XIfExpression) {
			return true;
		} else if (x instanceof Block) {
			return false;
		} else {
			return isInFilter(x.eContainer);
		}
	}

//	/**
//	 * If the EObject is in a array path, include self, then return true 
//	 */
//	def static boolean isArrayPath(EObject eObj) {
//		val sourceModelPath = getFullModelPath(eObj, true)
//		val targetModelPath = getFullModelPath(eObj, false)
//		sourceModelPath.contains("[") || targetModelPath.contains("[")
//	}
	/**
	 * If the EObject is in a array path, include self, then return true 
	 */
	def static boolean isArrayPath(EObject eObj, boolean isSourceModel) {
		if (isSourceModel) {
			val sourceModelPath = getFullModelPath(eObj, true)
			return sourceModelPath.contains("[")
		} else {
			val targetModelPath = getFullModelPath(eObj, false)
			return targetModelPath.contains("[")
		}
	}

	/**
	 * If the EObject is in a source array path, include self, then return true 
	 */
	def static boolean isSourceArrayPath(EObject eObj) {
		val sourceModelPath = getFullModelPath(eObj, true)
		sourceModelPath.contains("[")
	}

//	/**
//	 * If the EObject is in a array path, not include self, then return true 
//	 */
//	def static boolean isInArrayPath(EObject eObj) {
//		isSourceModelInArrayPath(eObj) || isTargetModelInArrayPath(eObj)
//	}
	/**
	 * If the EObject is in a array path, not include self, then return true 
	 */
	def static boolean isInArrayPath(EObject eObj, boolean isSourceModel) {
		if (isSourceModel) {
			return isSourceModelInArrayPath(eObj)
		} else {
			return isTargetModelInArrayPath(eObj)
		}
	}

	def private static boolean isSourceModelInArrayPath(EObject eObj) {
		if (eObj instanceof Body) {
			return false
		}

		val lastBlock = getLastArrayBlock(eObj, true)
		if (lastBlock == null) {
			return false
		} else {
			return true
		}
	}

	def private static boolean isTargetModelInArrayPath(EObject eObj) {
		if (eObj instanceof Body) {
			return false
		}

		val lastBlock = getLastArrayBlock(eObj, false)
		if (lastBlock == null) {
			return false
		} else {
			return true
		}
	}

	/**
	 * If the EObject is in a array 
	 */
	def static boolean isArray(EObject eObj) {
		isSourceModelArray(eObj) || isTargetModelArray(eObj)
	}

	/**
	 * If the EObject is in a array 
	 */
	def static boolean isArray(EObject eObj, boolean isSourceModel) {
		if (isSourceModel) {
			return isSourceModelArray(eObj)
		} else {
			return isTargetModelArray(eObj)
		}
	}

	def private static boolean isSourceModelArray(EObject eObj) {
		val sourceModelPath = getFullModelPath(eObj, true)
		val lastDotPosition = sourceModelPath.lastIndexOf(".");
		if (lastDotPosition < 0) {
			sourceModelPath.contains("[")
		} else {
			sourceModelPath.substring(lastDotPosition).contains("[")
		}
	}

	def private static boolean isTargetModelArray(EObject eObj) {
		val targetModelPath = getFullModelPath(eObj, false)
		val lastDotPosition = targetModelPath.lastIndexOf(".");
		if (lastDotPosition < 0) {
			targetModelPath.contains("[")
		} else {
			targetModelPath.substring(lastDotPosition).contains("[")
		}
	}

	def static void buildBlockStack(Stack<Block> blockStack, EObject eObj) {
		if (eObj instanceof Block) {
			blockStack.push(eObj)
			if (eObj.eContainer instanceof Body) {
				return
			}
		}
		buildBlockStack(blockStack, eObj.eContainer)
	}

	def static String getSourceModelPathByPath(EObject eObj) {
		return getFullModelPath(getBlockByPath(eObj), true)
	}

	def static String getTargetModelPathByPath(EObject eObj) {
		return getFullModelPath(getBlockByPath(eObj), false)
	}

	def static Block getBlockByPath(EObject eObj) {

		val Stack<Block> blockStack = new Stack<Block>()
		buildBlockStack(blockStack, eObj)
		if (eObj instanceof Block) {
			return getBlockByPath0(blockStack, eObj.absolutePath.length)
		} else if (eObj instanceof SingleSourceFieldPath) {
			return getBlockByPath0(blockStack, eObj.absolutePath.length)
		} else if (eObj instanceof Body) {
			throw new RuntimeException("please contact system administrator");
		} else {
			return getBlockByPath(eObj.eContainer);
		}
	}

	def static Block getBlockByPath0(Stack<Block> blockStack, int index) {
		if (index == 1) {
			blockStack.pop
		} else {
			blockStack.pop
			return getBlockByPath0(blockStack, index - 1)
		}
	}

	def static Block getCurrentBlock(EObject eObj) {

		if (eObj instanceof SingleSourceFieldPath) {
			if (eObj.absolutePath != null) {
				return getBlockByPath(eObj)
			}
		}

		if (eObj instanceof Block) {
			return eObj
		} else {
			return getCurrentBlock(eObj.eContainer)
		}
	}

	def static Block getCurrentAliasedBlockForTargetModelPath(EObject eObj) {
		if (eObj instanceof Block && !isDot((eObj as Block).targetModelPath)) {
			return eObj as Block
		} else {
			return getCurrentAliasedBlockForTargetModelPath(eObj.eContainer)
		}
	}

	def static Block getAppendedBlock(Body body, String fullTargetModelPath) {
		for (block : body.blocks) {
			val appendedBlock = getAppendedBlock0(block as Block, fullTargetModelPath)
			if (appendedBlock != null) {
				return appendedBlock
			}
		}
		return null
	}

	def private static Block getAppendedBlock0(Block block, String fullTargetModelPath) {
		if (!(block.targetModelPath.equals(".")) && block.isAppend == null &&
			fullTargetModelPath.equals(getFullModelPath(block, false))) {
			return block
		} else {
			for (subBlockContent : block.blockContents) {
				val subContent = subBlockContent as BlockContent
				if (subContent.content != null && subContent.content instanceof Block) {
					return getAppendedBlock0(subContent.content as Block, fullTargetModelPath)
				}
			}
			return null
		}
	}

	def static Block getLastArrayBlock(EObject eObj, boolean isOfSourceModel) {
		var currentBlock = getCurrentBlock(eObj);
		/*
		 * handle specific source model path : #
		 */
		if (isOfSourceModel && currentBlock.absolutePath != null) {
			return getBlockByPath(eObj); // just for source model
		}

		/*
		 * handle specific target model path : .+
		 */
		if (!isOfSourceModel && currentBlock.targetModelPath.equals(".") && currentBlock.isAppend != null) {
			currentBlock = getCurrentAliasedBlockForTargetModelPath(currentBlock)
		}

		return getLastArrayBlock0(currentBlock.eContainer, isOfSourceModel);
	}

	def private static Block getLastArrayBlock0(EObject eObj, boolean isOfSourceModel) {
		if (eObj instanceof Body) {
			return null;
		} else if (eObj instanceof Block) {
			if (isOfSourceModel && eObj.sourceModelPath.endsWith("]")) {
				return eObj

			} else if (!isOfSourceModel && eObj.targetModelPath.endsWith("]")) {
				return eObj
			}
		}
		return getLastArrayBlock0(eObj.eContainer, isOfSourceModel)

	}

	def static Body getBody(EObject eObj) {
		if (eObj instanceof Body) {
			return eObj
		} else {
			return getBody(eObj.eContainer)
		}
	}

	def static String getCurrentSourceModelPath(EObject eObj) {
		if (eObj instanceof Block) {
			if (!(eObj.sourceModelPath.equals("."))) {
				return eObj.sourceModelPath
			}
		}
		return getCurrentSourceModelPath(eObj.eContainer)
	}

	def static String getCurrentTargetModelPath(EObject eObj) {
		if (eObj instanceof Block) {
			if (!(eObj.targetModelPath.equals("."))) {
				return eObj.targetModelPath
			}
		}
		return getCurrentTargetModelPath(eObj.eContainer)
	}

	// get source model path of last array, if no array, return null
	def static String[] getLastArrayModelPath(EObject eObj, boolean isSourceModelPath) {
		var String[] modelPaths = newArrayOfSize(2)
		var String modelPath = null
		if (isSourceModelPath) {
			modelPath = getFullModelPath(eObj, true)
		} else {
			modelPath = getFullModelPath(eObj, false)
		}

		val lastDotPosition = modelPath.lastIndexOf(".");
		if (lastDotPosition < 0) {
			return null
		} else {
			val parentModelPath = modelPath.substring(0, lastDotPosition)
			val lastArrayPosition = parentModelPath.lastIndexOf("[")
			if (lastArrayPosition < 0) {
				return null
			} else {
				modelPaths.set(0, parentModelPath.substring(0, lastArrayPosition) + "[]")
				modelPaths.set(1, modelPath.substring(lastArrayPosition + 2))
				return modelPaths
			}
		}
	}

	/**
	 * 
	 */
	def static String getFullModelPath(EObject eObj, boolean isSourceModelPath) {
		val currentBlock = getCurrentBlock(eObj)
		if (currentBlock.eContainer instanceof Body) {
			if (isSourceModelPath) {
				return currentBlock.sourceModelPath
			} else {
				return currentBlock.targetModelPath
			}
		}
		return getFullModelPath0(currentBlock, "", isSourceModelPath)
	}

	def private static String getFullModelPath0(EObject eObj, String modelPath, boolean isSourceModelPath) {
		if (eObj instanceof Body) {
			return modelPath;
		}

		if (!(eObj instanceof Block)) {
			return getFullModelPath0(eObj.eContainer, modelPath, isSourceModelPath)
		}

		val Block block = (eObj as Block)

		if (isSourceModelPath) {
			if (block.absolutePath != null) {
				if (block.sourceModelPath.equals(".")) {
					return getSourceModelPathByPath(block)
				} else {
					return getSourceModelPathByPath(block) + '.' + block.sourceModelPath
				}
			} else {
				if (block.sourceModelPath.equals(".")) {
					return getFullModelPath0(eObj.eContainer, modelPath, isSourceModelPath)
				} else {
					if (modelPath.trim.length == 0) {
						return getFullModelPath0(eObj.eContainer, block.sourceModelPath, isSourceModelPath)
					} else {
						return getFullModelPath0(eObj.eContainer, block.sourceModelPath + '.' + modelPath,
							isSourceModelPath)
					}

				}
			}
		} else {
			if (block.targetModelPath.equals(".")) {
				return getFullModelPath0(eObj.eContainer, modelPath, isSourceModelPath)
			} else {
				if (modelPath.trim.length == 0) {
					return getFullModelPath0(eObj.eContainer, block.targetModelPath, isSourceModelPath)
				} else {
					return getFullModelPath0(eObj.eContainer, block.targetModelPath + '.' + modelPath,
						isSourceModelPath)
				}

			}
		}

	}

	def static String getXMemberFeatureCallPath(XAbstractFeatureCall featureCall, String path) {

		if (featureCall instanceof XFeatureCall) {
			return featureCall + '.' + path
		} else if (featureCall instanceof XMemberFeatureCall) {
			return getXMemberFeatureCallPath((featureCall.memberCallTarget as XAbstractFeatureCall),
				featureCall.concreteSyntaxFeatureName + '.' + path)
		}

	}

	def static boolean isNotInMemberFeatureCall(XFeatureCall featureCall) {
		if (featureCall.eContainer instanceof XMemberFeatureCall) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 
	 */
	def static boolean isTopMemberFeatureCall(XMemberFeatureCall memberFeatureCall) {
		if (memberFeatureCall.eContainer instanceof XMemberFeatureCall) {
			return false;
		} else {
			return true;
		}
	}

}
