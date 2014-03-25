package org.xacml4j.v30.policy.function;

import org.xacml4j.v30.EvaluationContext;
import org.xacml4j.v30.Time;
import org.xacml4j.v30.spi.function.XacmlFuncParam;
import org.xacml4j.v30.spi.function.XacmlFuncParamEvaluationContext;
import org.xacml4j.v30.spi.function.XacmlFuncReturnType;
import org.xacml4j.v30.spi.function.XacmlFuncSpec;
import org.xacml4j.v30.spi.function.XacmlFunctionProvider;
import org.xacml4j.v30.types.BooleanExp;
import org.xacml4j.v30.types.DateExp;
import org.xacml4j.v30.types.DateTimeExp;
import org.xacml4j.v30.types.StringExp;
import org.xacml4j.v30.types.TimeExp;

import com.google.common.base.Preconditions;

@XacmlFunctionProvider(description="XACML non-numeric comparision functions")
public class NonNumericComparisionFunctions
{
	@XacmlFuncSpec(id="urn:oasis:names:tc:xacml:1.0:function:string-greater-than")
	@XacmlFuncReturnType(typeId="http://www.w3.org/2001/XMLSchema#boolean")
	public static BooleanExp greatherThan(
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#string")StringExp a,
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#string")StringExp b)
	{
		return BooleanExp.valueOf(a.getValue().compareTo(b.getValue()) > 0);
	}

	@XacmlFuncSpec(id="urn:oasis:names:tc:xacml:1.0:function:string-greater-than-or-equal")
	@XacmlFuncReturnType(typeId="http://www.w3.org/2001/XMLSchema#boolean")
	public static BooleanExp greatherThanOrEqual(
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#string")StringExp a,
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#string")StringExp b)
	{
		int r = a.getValue().compareTo(b.getValue());
		return BooleanExp.valueOf(r > 0 || r == 0);
	}

	@XacmlFuncSpec(id="urn:oasis:names:tc:xacml:1.0:function:string-less-than")
	@XacmlFuncReturnType(typeId="http://www.w3.org/2001/XMLSchema#boolean")
	public static BooleanExp lessThan(
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#string")StringExp a,
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#string")StringExp b)
	{
		int r = a.getValue().compareTo(b.getValue());
		return BooleanExp.valueOf(r < 0);
	}

	@XacmlFuncSpec(id="urn:oasis:names:tc:xacml:1.0:function:string-less-than-or-equal")
	@XacmlFuncReturnType(typeId="http://www.w3.org/2001/XMLSchema#boolean")
	public static BooleanExp lessThanOrEqual(
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#string")StringExp a,
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#string")StringExp b)
	{
		int r = a.getValue().compareTo(b.getValue());
		return BooleanExp.valueOf(r < 0 || r == 0);
	}

	@XacmlFuncSpec(id="urn:oasis:names:tc:xacml:1.0:function:time-greater-than")
	@XacmlFuncReturnType(typeId="http://www.w3.org/2001/XMLSchema#boolean")
	public static BooleanExp greatherThan(
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#time")TimeExp a,
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#time")TimeExp b)
	{
		return BooleanExp.valueOf(a.compareTo(b) > 0);
	}

	@XacmlFuncSpec(id="urn:oasis:names:tc:xacml:1.0:function:time-greater-than-or-equal")
	@XacmlFuncReturnType(typeId="http://www.w3.org/2001/XMLSchema#boolean")
	public static BooleanExp greatherThanOrEquals(
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#time")TimeExp a,
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#time")TimeExp b)
	{
		int r = a.compareTo(b);
		return BooleanExp.valueOf(r  > 0 || r == 0);
	}

	@XacmlFuncSpec(id="urn:oasis:names:tc:xacml:1.0:function:time-less-than")
	@XacmlFuncReturnType(typeId="http://www.w3.org/2001/XMLSchema#boolean")
	public static BooleanExp lessThan(
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#time")TimeExp a,
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#time")TimeExp b)
	{
		return BooleanExp.valueOf(a.compareTo(b) < 0);
	}

	@XacmlFuncSpec(id="urn:oasis:names:tc:xacml:1.0:function:time-less-than-or-equal")
	@XacmlFuncReturnType(typeId="http://www.w3.org/2001/XMLSchema#boolean")
	public static BooleanExp lessThanOrEquals(
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#time")TimeExp a,
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#time")TimeExp b)
	{
		int r = a.compareTo(b);
		return BooleanExp.valueOf(r  < 0 || r == 0);
	}

	@XacmlFuncSpec(id="urn:oasis:names:tc:xacml:1.0:function:time-in-range")
	@XacmlFuncReturnType(typeId="http://www.w3.org/2001/XMLSchema#boolean")
	public static BooleanExp timeInRange(
			@XacmlFuncParamEvaluationContext EvaluationContext context,
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#time")TimeExp a,
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#time")TimeExp b,
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#time")TimeExp c)
	{
		Time ac = a.getValue();
		Time bc = b.getValue();
		Time cc = c.getValue();
		Preconditions.checkArgument(b.compareTo(c) <= 0);
		return BooleanExp.valueOf(ac.compareTo(bc) >= 0 && ac.compareTo(cc) <= 0);
	}

	@XacmlFuncSpec(id="urn:oasis:names:tc:xacml:1.0:function:date-greater-than")
	@XacmlFuncReturnType(typeId="http://www.w3.org/2001/XMLSchema#boolean")
	public static BooleanExp greatherThan(
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#date")DateExp a,
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#date")DateExp b)
	{
		return BooleanExp.valueOf(a.compareTo(b) > 0);
	}

	@XacmlFuncSpec(id="urn:oasis:names:tc:xacml:1.0:function:date-greater-than-or-equal")
	@XacmlFuncReturnType(typeId="http://www.w3.org/2001/XMLSchema#boolean")
	public static BooleanExp greatherThanOrEquals(
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#date")DateExp a,
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#date")DateExp b)
	{
		int r = a.compareTo(b);
		return BooleanExp.valueOf(r  > 0 || r == 0);
	}

	@XacmlFuncSpec(id="urn:oasis:names:tc:xacml:1.0:function:date-less-than")
	@XacmlFuncReturnType(typeId="http://www.w3.org/2001/XMLSchema#boolean")
	public static BooleanExp lessThan(
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#date")DateExp a,
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#date")DateExp b)
	{
		return BooleanExp.valueOf(a.compareTo(b) < 0);
	}

	@XacmlFuncSpec(id="urn:oasis:names:tc:xacml:1.0:function:date-less-than-or-equal")
	@XacmlFuncReturnType(typeId="http://www.w3.org/2001/XMLSchema#boolean")
	public static BooleanExp lessThanOrEquals(
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#date")DateExp a,
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#date")DateExp b)
	{
		int r = a.compareTo(b);
		return BooleanExp.valueOf(r  < 0 || r == 0);
	}

	@XacmlFuncSpec(id="urn:oasis:names:tc:xacml:1.0:function:dateTime-greater-than")
	@XacmlFuncReturnType(typeId="http://www.w3.org/2001/XMLSchema#boolean")
	public static BooleanExp greatherThan(
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#dateTime")DateTimeExp a,
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#dateTime")DateTimeExp b)
	{
		return BooleanExp.valueOf(a.compareTo(b) > 0);
	}

	@XacmlFuncSpec(id="urn:oasis:names:tc:xacml:1.0:function:dateTime-greater-than-or-equal")
	@XacmlFuncReturnType(typeId="http://www.w3.org/2001/XMLSchema#boolean")
	public static BooleanExp greatherThanOrEquals(
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#dateTime")DateTimeExp a,
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#dateTime")DateTimeExp b)
	{
		int r = a.compareTo(b);
		return BooleanExp.valueOf(r  > 0 || r == 0);
	}

	@XacmlFuncSpec(id="urn:oasis:names:tc:xacml:1.0:function:dateTime-less-than")
	@XacmlFuncReturnType(typeId="http://www.w3.org/2001/XMLSchema#boolean")
	public static BooleanExp lessThan(
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#dateTime")DateTimeExp a,
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#dateTime")DateTimeExp b)
	{
		return BooleanExp.valueOf(a.compareTo(b) < 0);
	}

	@XacmlFuncSpec(id="urn:oasis:names:tc:xacml:1.0:function:dateTime-less-than-or-equal")
	@XacmlFuncReturnType(typeId="http://www.w3.org/2001/XMLSchema#boolean")
	public static BooleanExp lessThanOrEquals(
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#dateTime")DateTimeExp a,
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#dateTime")DateTimeExp b)
	{
		int r = a.compareTo(b);
		return BooleanExp.valueOf(r  < 0 || r == 0);
	}
}
