package com.github.jmodel.mapper.api.builder;

import com.github.jmodel.ModelException;
import com.github.jmodel.api.domain.Model;

/**
 * A builder of mapping target objects.
 * 
 * @author jianni@hotmail.com
 *
 */
public interface Builder {

	public <T> T process(Model model) throws ModelException;
}
