package com.artagon.xacml.spring;

import org.junit.Ignore;

import com.artagon.xacml.v3.sdk.XacmlFuncParam;
import com.artagon.xacml.v3.sdk.XacmlFuncReturnType;
import com.artagon.xacml.v3.sdk.XacmlFuncSpec;
import com.artagon.xacml.v3.sdk.XacmlFunctionProvider;
import com.artagon.xacml.v3.types.BooleanType;
import com.artagon.xacml.v3.types.BooleanValue;
import com.artagon.xacml.v3.types.IntegerValue;

@Ignore
@XacmlFunctionProvider(description="Provider2")
public class TestStaticFunctions 
{
	@XacmlFuncSpec(id="test1_provider2")
	@XacmlFuncReturnType(typeId="http://www.w3.org/2001/XMLSchema#boolean")
	public static BooleanValue test1(
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#integer")IntegerValue a, 
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#integer")IntegerValue b) 
	{
		return BooleanType.BOOLEAN.create(a.equals(b));
	}
	
	
}
