package com.artagon.xacml.policy.function.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.artagon.xacml.policy.type.XacmlDataType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface XacmlFuncReturnType 
{
	XacmlDataType type();
	boolean isBag() default false;
}
