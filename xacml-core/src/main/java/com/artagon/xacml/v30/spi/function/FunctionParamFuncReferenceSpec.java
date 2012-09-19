package com.artagon.xacml.v30.spi.function;

import java.util.ListIterator;

import com.artagon.xacml.v30.Expression;
import com.artagon.xacml.v30.ValueType;
import com.artagon.xacml.v30.pdp.FunctionParamSpec;
import com.artagon.xacml.v30.pdp.FunctionReference;

final class FunctionParamFuncReferenceSpec implements FunctionParamSpec
{
	@Override
	public boolean isValidParamType(ValueType type) {
		return false;
	}

	public boolean isVariadic() {
		return false;
	}

	@Override
	public boolean validate(ListIterator<Expression> it) {
		Expression exp = it.next();
		return (exp instanceof FunctionReference);
	}
}
