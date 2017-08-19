package com.github.jmodel.mapper.impl.builder;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.github.jmodel.ModelException;
import com.github.jmodel.api.domain.Array;
import com.github.jmodel.api.domain.DataTypeEnum;
import com.github.jmodel.api.domain.Entity;
import com.github.jmodel.api.domain.Field;
import com.github.jmodel.api.domain.Model;
import com.github.jmodel.mapper.api.builder.Builder;

public class BeanBuilder implements Builder {

	@SuppressWarnings("unchecked")
	@Override
	public Object process(Model targetModel) throws ModelException {

		Object bean = targetModel.getTargetBean();
		if (bean == null) {
			throw new ModelException("Bean instance is not found");
		}
		buildBean(targetModel, bean);
		return bean;
	}

	private void buildBean(Model model, Object bean) throws ModelException {

		Method getM = null;
		Method setM = null;
		try {
			if (model instanceof Entity) {
				List<Field> fields = ((Entity) model).getFields();
				if (fields != null) {
					for (Field field : fields) {
						DataTypeEnum dataType = field.getDataType();
						if (dataType == null) {
							field.setDataType(DataTypeEnum.STRING);
						}
						switch (field.getDataType()) {
						case INTEGER:
							setM = bean.getClass().getMethod("set" + StringUtils.capitalize(field.getName()),
									Integer.class);
							setM.invoke(bean, Integer.valueOf(field.getValue()));
						case LONG:
							setM = bean.getClass().getMethod("set" + StringUtils.capitalize(field.getName()),
									Long.class);
							setM.invoke(bean, Long.valueOf(field.getValue()));
						case BOOLEAN:
							setM = bean.getClass().getMethod("set" + StringUtils.capitalize(field.getName()),
									Boolean.class);
							setM.invoke(bean, Boolean.valueOf(field.getValue()));
						case BIGDECIMAL:
							setM = bean.getClass().getMethod("set" + StringUtils.capitalize(field.getName()),
									BigDecimal.class);
							setM.invoke(bean, new BigDecimal(field.getValue()));
						case DATE:
							setM = bean.getClass().getMethod("set" + StringUtils.capitalize(field.getName()),
									Date.class);
							DateFormat format = new SimpleDateFormat("YYYY-MM-DD");
							Date date = format.parse(field.getValue());
							setM.invoke(bean, date);
						default:
							setM = bean.getClass().getMethod("set" + StringUtils.capitalize(field.getName()),
									String.class);
							setM.invoke(bean, field.getValue());
						}
					}
				}
			}
			List<Model> subModels = model.getSubModels();
			if (subModels != null) {
				for (Model subModel : subModels) {
					if (subModel instanceof Entity) {
						getM = bean.getClass().getMethod("get" + StringUtils.capitalize(subModel.getName()));
						setM = bean.getClass().getMethod("set" + StringUtils.capitalize(subModel.getName()),
								getM.getReturnType());
						Object obj = getM.getReturnType().newInstance();
						setM.invoke(bean, obj);
						buildBean(subModel, obj);
					} else if (subModel instanceof Array) {
						getM = bean.getClass().getMethod("get" + StringUtils.capitalize(subModel.getName()));
						setM = bean.getClass().getMethod("set" + StringUtils.capitalize(subModel.getName()),
								List.class);
						Type type = ((ParameterizedType) (getM.getGenericReturnType())).getActualTypeArguments()[0];
						List<Object> subBeans = new ArrayList<Object>();
						setM.invoke(bean, subBeans);

						List<Model> sub = subModel.getSubModels();
						if (sub != null) {
							for (Model subSubModel : sub) {
								Object subBean = Class.forName(type.getTypeName()).newInstance();
								subBeans.add(subBean);
								buildBean(subSubModel, subBean);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			throw new ModelException("Failed to construct bean instance", e);
		}
	}

}
