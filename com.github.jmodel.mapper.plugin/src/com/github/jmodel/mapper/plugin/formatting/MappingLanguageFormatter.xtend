package com.github.jmodel.mapper.plugin.formatting

import org.eclipse.xtext.formatting.impl.AbstractDeclarativeFormatter
import org.eclipse.xtext.formatting.impl.FormattingConfig

import com.google.inject.Inject;
import com.github.jmodel.mapper.plugin.services.MappingLanguageGrammarAccess

/**
 * This class contains custom formatting description.
 * 
 * see : http://www.eclipse.org/Xtext/documentation.html#formatting
 * on how and when to use it 
 * 
 * Also see {@link org.eclipse.xtext.xtext.XtextFormattingTokenSerializer} as an example
 */
class MappingLanguageFormatter extends AbstractDeclarativeFormatter {

	@Inject extension MappingLanguageGrammarAccess g

	override protected void configureFormatting(FormattingConfig c) {
		// mapping
		val mapping = g.getMappingAccess
		c.setIndentation(mapping.getLeftCurlyBracketKeyword_3(), mapping.getRightCurlyBracketKeyword_7())
		c.setLinewrap(2).after(mapping.getLeftCurlyBracketKeyword_3())

		// from
		val from = g.getFromAccess
		c.setLinewrap().before(from.getFromKeyword_0())

		// to
		val to = g.getToAccess
		c.setLinewrap().before(to.getToKeyword_0())

		// block
		val block = g.getBlockAccess
		c.setIndentation(block.getLeftCurlyBracketKeyword_8(), block.getRightCurlyBracketSemicolonKeyword_11())
		c.setLinewrap().before(block.getRule)
		c.setLinewrap().after(block.getLeftCurlyBracketKeyword_8)
		c.setLinewrap(2).after(block.getRightCurlyBracketSemicolonKeyword_11)
		c.setNoSpace().between(block.getAbsolutePathAssignment_1, block.getSourceModelPathAssignment_2)

		// fieldMapping
		val singleTargetFieldMapping = g.getSingleTargetFieldMappingAccess
		c.setLinewrap().after(singleTargetFieldMapping.getSemicolonKeyword_4)
		c.setNoSpace().before(singleTargetFieldMapping.getSemicolonKeyword_4)

		// singleSourceFieldPath
		val singleSourceFieldPath = g.getSingleSourceFieldPathAccess
		c.setNoSpace().between(singleSourceFieldPath.getAbsolutePathAssignment_1,
			singleSourceFieldPath.getContentAssignment_2)

		//
		val sourceFieldPathIf = g.getSourceFieldPathIfAccess
		c.setLinewrap().after(sourceFieldPathIf.getTHENKeyword_3)
		c.setIndentationIncrement().after(sourceFieldPathIf.getTHENKeyword_3)
		c.setIndentationDecrement().before(sourceFieldPathIf.getELSEKeyword_5_1_0)
		c.setLinewrap().after(sourceFieldPathIf.getELSEKeyword_5_1_0)
		c.setIndentationIncrement().after(sourceFieldPathIf.getELSEKeyword_5_1_0)
		c.setIndentationDecrement().before(sourceFieldPathIf.getENDIFKeyword_6)

		val sourceFieldPathElseIf = g.getSourceFieldPathElseIfAccess
		c.setIndentationDecrement().before(sourceFieldPathElseIf.getELSEIFKeyword_1)
		c.setLinewrap().after(sourceFieldPathElseIf.getTHENKeyword_3)
		c.setIndentationIncrement().after(sourceFieldPathElseIf.getTHENKeyword_3)
		c.setIndentationDecrement().before(sourceFieldPathElseIf.getELSEKeyword_5_1_0)
		c.setLinewrap().after(sourceFieldPathElseIf.getELSEKeyword_5_1_0)
		c.setIndentationIncrement().after(sourceFieldPathElseIf.getELSEKeyword_5_1_0)

		// filter
		val filter = g.getFilterAccess
		c.setLinewrap().after(filter.getRule)
		c.setNoSpace().after(filter.getColonKeyword_1)

		// xParenthesizedExpression
		val xParenthesizedExpression = g.getXParenthesizedExpressionAccess
		c.setNoSpace().after(xParenthesizedExpression.getLeftParenthesisKeyword_0)
		c.setNoSpace().before(xParenthesizedExpression.getRightParenthesisKeyword_2)

		// comments
		c.setLinewrap(0, 1, 2).before(SL_COMMENTRule)
		c.setLinewrap(0, 1, 2).before(ML_COMMENTRule)
		c.setLinewrap(0, 1, 1).after(ML_COMMENTRule)
	}
}
