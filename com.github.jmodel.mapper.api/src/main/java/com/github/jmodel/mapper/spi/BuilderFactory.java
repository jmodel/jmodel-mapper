package com.github.jmodel.mapper.spi;

import com.github.jmodel.FormatEnum;
import com.github.jmodel.mapper.api.builder.Builder;

/**
 * Builder factory interface.
 * 
 * @author jianni@hotmail.com
 *
 */
public interface BuilderFactory {

	public <T> Builder getBuilder(FormatEnum toFormat, Class<T> valueType);
}
