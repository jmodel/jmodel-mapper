package com.github.jmodel.mapper.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.github.jmodel.api.Analyzer;
import com.github.jmodel.api.FormatEnum;
import com.github.jmodel.api.Model;

public abstract class Mapping {

	private FormatEnum fromFormat;

	private FormatEnum toFormat;

	private Analyzer analyzer;

	private Builder<?> builder;

	private Model sourceTemplateModel;

	private Model targetTemplateModel;

	private boolean isTemplateReady;

	private List<String> rawVariables = new ArrayList<String>();

	private List<String> rawSourceFieldPaths = new ArrayList<String>();

	private Map<String, Boolean> sourceModelRecursiveMap = new HashMap<String, Boolean>();

	private List<String> rawTargetFieldPaths = new ArrayList<String>();

	private Map<String, Boolean> targetModelRecursiveMap = new HashMap<String, Boolean>();

	public FormatEnum getFromFormat() {
		return fromFormat;
	}

	public void setFromFormat(FormatEnum fromFormat) {
		this.fromFormat = fromFormat;
	}

	public FormatEnum getToFormat() {
		return toFormat;
	}

	public void setToFormat(FormatEnum toFormat) {
		this.toFormat = toFormat;
	}

	public Analyzer getAnalyzer() {
		return analyzer;
	}

	public void setAnalyzer(Analyzer analyzer) {
		this.analyzer = analyzer;
	}

	public Builder<?> getBuilder() {
		return builder;
	}

	public void setBuilder(Builder<?> builder) {
		this.builder = builder;
	}

	public Model getSourceTemplateModel() {
		return sourceTemplateModel;
	}

	public void setSourceTemplateModel(Model sourceTemplateModel) {
		this.sourceTemplateModel = sourceTemplateModel;
	}

	public Model getTargetTemplateModel() {
		return targetTemplateModel;
	}

	public void setTargetTemplateModel(Model targetTemplateModel) {
		this.targetTemplateModel = targetTemplateModel;
	}

	public boolean isTemplateReady() {
		return isTemplateReady;
	}

	public void setTemplateReady(boolean isTemplateReady) {
		this.isTemplateReady = isTemplateReady;
	}

	public List<String> getRawVariables() {
		return rawVariables;
	}

	public void setRawVariables(List<String> rawVariables) {
		this.rawVariables = rawVariables;
	}

	public List<String> getRawSourceFieldPaths() {
		return rawSourceFieldPaths;
	}

	public void setRawSourceFieldPaths(List<String> rawSourceFieldPaths) {
		this.rawSourceFieldPaths = rawSourceFieldPaths;
	}

	public Map<String, Boolean> getSourceModelRecursiveMap() {
		return sourceModelRecursiveMap;
	}

	public void setSourceModelRecursiveMap(Map<String, Boolean> sourceModelRecursiveMap) {
		this.sourceModelRecursiveMap = sourceModelRecursiveMap;
	}

	public List<String> getRawTargetFieldPaths() {
		return rawTargetFieldPaths;
	}

	public void setRawTargetFieldPaths(List<String> rawTargetFieldPaths) {
		this.rawTargetFieldPaths = rawTargetFieldPaths;
	}

	public Map<String, Boolean> getTargetModelRecursiveMap() {
		return targetModelRecursiveMap;
	}

	public void setTargetModelRecursiveMap(Map<String, Boolean> targetModelRecursiveMap) {
		this.targetModelRecursiveMap = targetModelRecursiveMap;
	}

	public void execute(final Model mySourceModel, final Model myTargetModel, final Map<String, Object> myVariablesMap,
			final Locale currentLocale) {

	}

	public void init(final Mapping myInstance) {

	}
}
