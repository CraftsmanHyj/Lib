package com.hyj.lib.tree.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TreeNodeId {
	// @Target(ElementType.FIELD)声明注解作用于字段上面
	// @Retention(RetentionPolicy.RUNTIME)声明注解何时可见
}
