package com.github.jmodel.mapper.spi;

import com.github.jmodel.api.FormatEnum;
import com.github.jmodel.mapper.api.Builder;

public interface BuilderFactory {

	public <R> Builder<R> getBuilder(FormatEnum toFormat, Class<R> returnType);
}
