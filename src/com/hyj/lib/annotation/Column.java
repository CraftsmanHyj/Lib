package com.hyj.lib.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据库字段的注解
 * 
 * @Author hyj
 * @Date 2016-2-15 下午10:03:55
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

	/**
	 * <pre>
	 * 定义注解里面的初始化值的属性
	 * 如：@Column("id")与@Column(value="id")等同
	 * 	当只有一个属性的时候必须用value()(默认规则)
	 * </pre>
	 * 
	 * @return
	 */
	String value();
}
