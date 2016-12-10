package com.github.jmodel.mapper.api;

import com.github.jmodel.api.Model;

public interface Builder<R> {

	public R process(Model targetModel);
}
