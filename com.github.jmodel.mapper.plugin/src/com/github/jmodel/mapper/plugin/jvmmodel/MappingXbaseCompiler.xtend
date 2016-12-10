package com.github.jmodel.mapper.plugin.jvmmodel

import com.github.jmodel.mapper.plugin.mappingLanguage.Body
import com.github.jmodel.mapper.plugin.mappingLanguage.FieldMapping
import com.github.jmodel.mapper.plugin.mappingLanguage.IfTargetFieldsMapping
import com.github.jmodel.mapper.plugin.mappingLanguage.Block
import com.github.jmodel.mapper.plugin.mappingLanguage.SingleTargetFieldMapping
import com.github.jmodel.mapper.plugin.mappingLanguage.SourceFieldPath
import com.github.jmodel.mapper.plugin.mappingLanguage.SourceFieldPathSetting
import com.github.jmodel.mapper.plugin.mappingLanguage.SingleSourceFieldPath
import com.github.jmodel.mapper.plugin.mappingLanguage.SourceFieldPathXLiteral
import com.github.jmodel.mapper.plugin.mappingLanguage.SourceFieldPathXParenthesizedExpression
import com.github.jmodel.mapper.plugin.mappingLanguage.TargetFieldPath
import org.eclipse.xtext.xbase.XBinaryOperation
import org.eclipse.xtext.xbase.XBooleanLiteral
import org.eclipse.xtext.xbase.XExpression
import org.eclipse.xtext.xbase.XIfExpression
import org.eclipse.xtext.xbase.XNullLiteral
import org.eclipse.xtext.xbase.XNumberLiteral
import org.eclipse.xtext.xbase.XStringLiteral
import org.eclipse.xtext.xbase.compiler.XbaseCompiler
import org.eclipse.xtext.xbase.compiler.output.ITreeAppendable
import com.github.jmodel.mapper.plugin.mappingLanguage.Variable
import com.github.jmodel.mapper.plugin.mappingLanguage.DataType
import com.github.jmodel.mapper.plugin.util.Util
import com.github.jmodel.mapper.plugin.mappingLanguage.Mapping
import com.github.jmodel.mapper.plugin.mappingLanguage.BlockContent

/**
 * The main procedure of compiling:
 * <ul>
 * <li>doInternalToJavaStatement</li> 
 * <li>_toJavaStatement</li>
 * <li>internalToJavaStatement</li>
 * <li>internalToJavaExpression</li>
 * <li>internalToConvertedExpression</li>
 * <li>_toJavaExpression</li>
 * </ul>
 */
class MappingXbaseCompiler extends XbaseCompiler {

	override protected doInternalToJavaStatement(XExpression expr, ITreeAppendable it, boolean isReferenced) {
		switch expr {
			Body: {
				newLine
				append('''super.execute(mySourceModel, myTargetModel, myVariablesMap, currentLocale);''')

				val mapping = expr.eContainer as Mapping
				if (mapping.to.bean != null) {
					newLine
					append('''myTargetModel.setTargetBean(new «mapping.to.bean»());''')
				}

				for (block : expr.blocks) {
					doInternalToJavaStatement(block, it, isReferenced)
				}
			}
			Block: {
				/*
				 * Find full model path for current block
				 */
				val fullSourceModelPath = Util.getFullModelPath(expr, true)
				val fullTargetModelPath = Util.getFullModelPath(expr, false)
				var String m = null 
				if(hasName(fullSourceModelPath + "_" + fullTargetModelPath + "_m")) {
					m = getName(fullSourceModelPath + "_" + fullTargetModelPath + "_m");
				}else{
					m = declareUniqueNameVariable(fullSourceModelPath + "_" + fullTargetModelPath + "_m", "m");
				}

				newLine
				append('''{''')

				var String strSourceModel
				var String strSourceModelPath
				var String strTargetModel
				var String strTargetModelPath
				var String strIndex = '''0'''
				var String strIsAppend = '''false'''

				/*
				 * not root block
				 */ 
				if (expr.eContainer instanceof BlockContent) {
					
					/*
					 * Analyze source model of block, if the source model is in a array path (not include self)
					 * Get last array block for source model, pay attention to these symbols, . # ##...  
					 */
					if (!Util.isInArrayPath(expr, true)) {
						strSourceModel = '''mySourceModel.getModelPathMap().get("«fullSourceModelPath»")'''
						strSourceModelPath = '''"«fullSourceModelPath»"'''
					} else {
						val lastSourceArrayBlock = Util.getLastArrayBlock(expr, true)
						val lastSourceArrayModelPath = Util.getFullModelPath(lastSourceArrayBlock, true)
						val lastTargetArrayModelPath = Util.getFullModelPath(lastSourceArrayBlock, false)
						val l_m = getName(lastSourceArrayModelPath + "_" + lastTargetArrayModelPath + "_m")
						val lastSourceArrayModelPathAfter = fullSourceModelPath.replace(lastSourceArrayModelPath, "")

						strSourceModel = '''mySourceModel.getModelPathMap().get(«l_m»[0] + "«lastSourceArrayModelPathAfter»")'''
						strSourceModelPath = '''«l_m»[0] + "«lastSourceArrayModelPathAfter»"'''
					}

					/*
					 * Analyze target model of block, if the target model is in a array path (not include self)
					 * Get last array block for target model, pay attention to these symbols, . +  
					 */					
					if (!Util.isInArrayPath(expr, false)) {
						/*
						 * A.B.C[] => A.B.C[]
						 * A.B.. => A.B.C[]
						 * A.B..+ => A.B.C[]+ 
						 */
						strTargetModel = '''myTargetModel.getModelPathMap().get("«fullTargetModelPath»")'''
						strTargetModelPath = '''"«fullTargetModelPath»"'''
					} else {
						/*
						 * A.B.C[].D[]
						 * A.B.C[].. => A.B.C[]
						 * A.B.C[]..+ => A.B.C[]
						 * A.B.C[]...E
						 * A.B.C[].... => A.B.C[]
						 * 
						 * A.B.C[].X
						 * A.B.C[].. => A.B.C[]
						 */
						val lastTargetArrayBlock = Util.getLastArrayBlock(expr, false)
						val lastSourceArrayModelPath = Util.getFullModelPath(lastTargetArrayBlock, true)
						val lastTargetArrayModelPath = Util.getFullModelPath(lastTargetArrayBlock, false)
						
						val l_m = getName(lastSourceArrayModelPath + "_" + lastTargetArrayModelPath + "_m")
						val targetArrayModelPathAfter = fullTargetModelPath.replace(lastTargetArrayModelPath, "")

						strTargetModel = '''myTargetModel.getModelPathMap().get(«l_m»[1] + "«targetArrayModelPathAfter»")'''
						strTargetModelPath = '''«l_m»[1] + "«targetArrayModelPathAfter»"'''
						strIndex = '''Integer.valueOf(«l_m»[2])'''			
					}
					
					/*
					 * only self is append
					 */
					if (expr.isAppend!=null) {
						strIsAppend = '''true'''
					}

				} else {
					// root model path
					strSourceModel = '''mySourceModel.getModelPathMap().get("«expr.sourceModelPath»")'''
					strSourceModelPath = '''"«expr.sourceModelPath»"'''
					strTargetModel = '''myTargetModel.getModelPathMap().get("«expr.targetModelPath»")'''
					strTargetModelPath = '''"«expr.targetModelPath»"'''
				}

				// self is array
				if (Util.isArray(expr)) {
					val p = declareUniqueNameVariable(fullSourceModelPath + "_" + fullTargetModelPath + "_p", "p")
					newLine
					append('''java.util.function.Predicate<String> «p» = null;''')

					if (expr.filter != null) {
						val f = declareUniqueNameVariable(fullSourceModelPath + "_" + fullTargetModelPath +
									"_f", "f")

						newLine
						append('''«p» = (String «f») -> (''')
						doInternalToJavaStatement(expr.filter.expression, it,
									isReferenced)
						append(''');''')
					}

					newLine
					append('''com.github.jmodel.mapper.api.MappingHelper.arrayMapping(mySourceModel, myTargetModel, «strSourceModel», «strTargetModel», «strSourceModelPath», «strTargetModelPath», «strIndex», «strIsAppend», «p»,''')

					newLine
					append('''(String[] «m») ->''')

					newLine
					append('''{''')

				}

				for (blockContent : expr.blockContents) {
					val content = blockContent.content
					if (content != null) {
						if (content instanceof FieldMapping) {
							doInternalToJavaStatement((content as FieldMapping).expression, it, isReferenced)
						} else if (content instanceof Block) {
							doInternalToJavaStatement(content, it, isReferenced)
						}
					}
				}

				// self is array
				if (Util.isArray(expr)) {
					newLine
					append('''});''')
				}

				newLine
				append('''}''')
			}
			SingleTargetFieldMapping: {

				val fullSourceModelPath = Util.getFullModelPath(expr, true)
				val fullTargetModelPath = Util.getFullModelPath(expr, false)

				newLine
				append('''{''')

				newLine
				append('''String fieldValue = null;''')

				doInternalToJavaStatement(expr.sourceFieldPath, it, isReferenced)

				val targetFieldPath = (expr.targetFieldPath as TargetFieldPath)

				/*
				 * If target model is in a array path (include self)
				 */
				if (Util.isArrayPath(expr, false)) {
					
					if(Util.isArray(expr, false)) {
						val m = getName(fullSourceModelPath + "_" + fullTargetModelPath + "_m")
						newLine
						append('''
							myTargetModel.getFieldPathMap().get(«m»[1] + ".«targetFieldPath.expression»").setValue(fieldValue); 
						''')
						if (targetFieldPath.dataType != null || targetFieldPath.dataType !=	DataType.STR) {
							newLine
							append('''
								myTargetModel.getFieldPathMap().get(«m»[1] + ".«targetFieldPath.expression»").setDataType(«Util.getDataType(targetFieldPath.dataType)»);  
							''')
						}
					}else{
						val lastTargetArrayBlock = Util.getLastArrayBlock(expr, false)
						if (lastTargetArrayBlock == null) {
							newLine
							append('''
								myTargetModel.getFieldPathMap().get("«fullTargetModelPath».«targetFieldPath.expression»").setValue(fieldValue); 
							''')
						} else {
							val lastSourceArrayModelPath = Util.getFullModelPath(lastTargetArrayBlock, true)
							val lastTargetArrayModelPath = Util.getFullModelPath(lastTargetArrayBlock, false)
							val l_m = getName(lastSourceArrayModelPath + "_" + lastTargetArrayModelPath + "_m")
						
							val lastTargetArrayModelPathAfter = fullTargetModelPath.replace(lastTargetArrayModelPath, "")
							newLine
							append('''
								myTargetModel.getFieldPathMap().get(«l_m»[1] + "«lastTargetArrayModelPathAfter»" + ".«targetFieldPath.expression»").setValue(fieldValue); 
							''')
						}
					}
				} else {
					newLine
					append('''
						myTargetModel.getFieldPathMap().get("«fullTargetModelPath».«targetFieldPath.expression»").setValue(fieldValue); 
					''')
					if (targetFieldPath.dataType != null || targetFieldPath.dataType !=	DataType.STR) {
						newLine
						append('''
							myTargetModel.getFieldPathMap().get("«fullTargetModelPath».«targetFieldPath.expression»").setDataType(«Util.getDataType(targetFieldPath.dataType)»);   
						''')
					}
				}

				newLine
				append('''}''')

			}
			IfTargetFieldsMapping: {
				newLine
				append('''{''')
				doInternalToJavaStatement(expr.sourceFieldPathIf, it, isReferenced)
				newLine
				append('''}''')

			}
			SourceFieldPath: {
				newLine
				append('''fieldValue = String.valueOf(''')
				doInternalToJavaStatement(expr.expression, it, isReferenced)
				append(''');''')
			}
			SourceFieldPathXLiteral: {
				switch (expr.content) {
					XStringLiteral:
						append('''"«(expr.content as XStringLiteral).value»"''')
					XNumberLiteral:
						append('''String.valueOf(«(expr.content as XNumberLiteral).value»)''')
					default:
					append('''''')
				}
			}
			SingleSourceFieldPath: {
				val fullSourceModelPath = Util.getFullModelPath(expr, true)
				val fullTargetModelPath = Util.getFullModelPath(expr, false)

				if (Util.isSourceArrayPath(expr)) {

					if (Util.isInFilter(expr)) {
						val f = getName(fullSourceModelPath + "_" + fullTargetModelPath + "_f")
						append('''com.github.jmodel.api.ModelHelper.getFieldValue(mySourceModel.getFieldPathMap().get(«f» + ".«expr.content»"))''')
					} else {
						var String m = null
						if (expr.absolutePath != null) {
							val sourceModelPathByPath = Util.getSourceModelPathByPath(expr)
							val targetModelPathByPath = Util.getTargetModelPathByPath(expr)
							m = getName(sourceModelPathByPath + "_" + targetModelPathByPath + "_m")
						} else {
							m = getName(fullSourceModelPath + "_" +	fullTargetModelPath + "_m")
						}
						append('''com.github.jmodel.api.ModelHelper.getFieldValue(mySourceModel.getFieldPathMap().get(«m»[0] + ".«expr.content»"))''')
					}

				} else {
					append('''com.github.jmodel.api.ModelHelper.getFieldValue(mySourceModel.getFieldPathMap().get("«fullSourceModelPath».«expr.content»"))''')
				}
			}
			SourceFieldPathXParenthesizedExpression: {
				doInternalToJavaStatement(expr.content, it, isReferenced)
			}
			SourceFieldPathSetting: {
				for (singleTargetFieldMapping : expr.set) {
					doInternalToJavaStatement(singleTargetFieldMapping, it, isReferenced)
				}
			}
			Variable: {
				append('''myVariablesMap.get("«Util.getVariableName(expr.expression)»")''')
			}
			XIfExpression: {
				newLine
				append("if (")
				doInternalToJavaStatement(expr.getIf(), it, isReferenced)
				append(") {").increaseIndentation()
				doInternalToJavaStatement(expr.getThen(), it, isReferenced)
				decreaseIndentation().newLine().append("}")
				if (expr.getElse() != null) {
					append(" else {").increaseIndentation()
					doInternalToJavaStatement(expr.getElse(), it, isReferenced)
					decreaseIndentation().newLine().append("}")
				}
			}
			XBinaryOperation: {
				val operation = expr.getConcreteSyntaxFeatureName()
				if (Util.isPredict(operation)) {
					append('''(com.github.jmodel.api.ModelHelper.predict(''')
				} else {
					append('''(com.github.jmodel.api.ModelHelper.calc(''')
				}
				doInternalToJavaStatement(expr.leftOperand, it, isReferenced)
				append(''',''')
				if (operation.equals("in")) {
					append('''(java.util.List)(''')
					doInternalToJavaStatement(expr.rightOperand, it, isReferenced)
					append(''')''')
				} else {
					doInternalToJavaStatement(expr.rightOperand, it, isReferenced)
				}
				append(''',''')
				append('''«Util.operEnum(operation)», currentLocale''')
				append('''))''')
			}
			XStringLiteral: {
				append('''"«expr.value»"''')
			}
			XNumberLiteral: {
				append(expr.value)
			}
			XNullLiteral: {
				// always be used as comparable value
				append("(Comparable)null")
			}
			XBooleanLiteral: {
				append('''"«expr.isIsTrue()»"''')
			}
			default:
				super.doInternalToJavaStatement(expr, it, isReferenced)
			}
		}
	}
																