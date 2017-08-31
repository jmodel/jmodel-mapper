package com.github.jmodel.mapper.example;

import com.github.jmodel.ModelException;
import com.github.jmodel.api.domain.Model;
import com.github.jmodel.mapper.api.domain.Mapping;
import java.util.Map;

@SuppressWarnings("all")
public class Json2Json extends Mapping {
  private static Mapping instance;
  
  public static synchronized Mapping getInstance() {
    if (instance == null) {
    	instance = new com.github.jmodel.mapper.example.Json2Json();
    	
    	instance.init(instance);
    }	
    
    return instance;
  }
  
  @Override
  public void init(final Mapping myInstance) {
    super.init(myInstance);
    com.github.jmodel.api.domain.Entity sourceRootModel = new com.github.jmodel.api.domain.Entity();
    myInstance.setSourceTemplateModel(sourceRootModel);
    com.github.jmodel.api.domain.Entity targetRootModel = new com.github.jmodel.api.domain.Entity();
    myInstance.setTargetTemplateModel(targetRootModel); 
    		
    myInstance.setFromFormat(com.github.jmodel.FormatEnum.JSON);														
    
    myInstance.setToFormat(com.github.jmodel.FormatEnum.JSON);														
    
    	
    			
    	
    				
    	myInstance.getRawSourceFieldPaths().add("Source._");
    	myInstance.getRawTargetFieldPaths().add("Target._");
    	
    	myInstance.getRawSourceFieldPaths().add("Source.Content._");
    	myInstance.getRawTargetFieldPaths().add("Target._");
    	
    
        myInstance.getRawSourceFieldPaths().add("Source.Content.Name");
    	
    	myInstance.getRawTargetFieldPaths().add("Target.MyName");												
  }
  
  @Override
  public void execute(final Model mySourceModel, final Model myTargetModel, final Map myVariablesMap) throws ModelException {
    super.execute(mySourceModel, myTargetModel, myVariablesMap);
    {
    {
    {
    String fieldValue = null;
    fieldValue = String.valueOf(com.github.jmodel.api.utils.ModelHelper.getFieldValue(mySourceModel.getFieldPathMap().get("Source.Content.Name")));
    myTargetModel.getFieldPathMap().get("Target.MyName").setValue(fieldValue); 
    
    myTargetModel.getFieldPathMap().get("Target.MyName").setDataType(com.github.jmodel.api.domain.DataTypeEnum.STRING);   
    
    }
    }
    }
  }
}
