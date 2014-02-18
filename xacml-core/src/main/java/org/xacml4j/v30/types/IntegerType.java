package org.xacml4j.v30.types;

import org.oasis.xacml.v30.jaxb.AttributeValueType;
import org.xacml4j.v30.AttributeExp;
import org.xacml4j.v30.AttributeExpType;
import org.xacml4j.v30.BagOfAttributeExp;
import org.xacml4j.v30.BagOfAttributeExpType;
import org.xacml4j.v30.XacmlSyntaxException;

import com.google.common.base.Preconditions;

public enum IntegerType implements AttributeExpType, TypeToString, TypeToXacml30
{
	INTEGER("http://www.w3.org/2001/XMLSchema#integer");

	private final String typeId;
	private final BagOfAttributeExpType bagType;

	private IntegerType(String typeId){
		this.typeId = typeId;
		this.bagType = new BagOfAttributeExpType(this);
	}

	private boolean isConvertibleFrom(Object any) {
		return Long.class.isInstance(any) || Integer.class.isInstance(any) ||
		Short.class.isInstance(any) || Byte.class.isInstance(any) ||
		String.class.isInstance(any);
	}
	
	public IntegerExp create(Object any){
		Preconditions.checkNotNull(any);
		Preconditions.checkArgument(isConvertibleFrom(any),
				"Value=\"%s\" of type=\"%s\" can't be converted to XACML \"%s\" type",
				any, any.getClass(), typeId);
		if(String.class.isInstance(any)){
			return fromString((String)any);
		}
		if(Byte.class.isInstance(any)){
			return new IntegerExp(((Byte)any).longValue());
		}
		if(Short.class.isInstance(any)){
			return new IntegerExp(((Short)any).longValue());
		}
		if(Integer.class.isInstance(any)){
			return new IntegerExp(((Integer)any).longValue());
		}
		return new IntegerExp((Long)any);
	}

	@Override
	public AttributeValueType toXacml30(Types types, AttributeExp v) {
		AttributeValueType xacml = new AttributeValueType();
		xacml.setDataType(v.getType().getDataTypeId());
		xacml.getContent().add(toString(v));
		return xacml;
	}

	@Override
	public IntegerExp fromXacml30(Types types, AttributeValueType v) {
		if(v.getContent().size() > 0){
			return fromString((String)v.getContent().get(0));
		}
		throw new XacmlSyntaxException(
				"No content found for the attribute value");
	}

	@Override
	public String toString(AttributeExp exp) {
		IntegerExp v = (IntegerExp)exp;
		return v.getValue().toString();
	}

	@Override
	public IntegerExp fromString(String v) {
        Preconditions.checkNotNull(v);
		if ((v.length() >= 1) &&
        		(v.charAt(0) == '+')){
			v = v.substring(1);
		}
		return new IntegerExp(Long.valueOf(v));
	}

	@Override
	public String getDataTypeId() {
		return typeId;
	}

	@Override
	public BagOfAttributeExpType bagType() {
		return bagType;
	}

	@Override
	public BagOfAttributeExp bagOf(AttributeExp... values) {
		return bagType.create(values);
	}

	@Override
	public BagOfAttributeExp bagOf(Iterable<AttributeExp> values) {
		return bagType.create(values);
	}

	@Override
	public BagOfAttributeExp.Builder bag(){
		return new BagOfAttributeExp.Builder(this);
	}
	
	@Override
	public BagOfAttributeExp emptyBag() {
		return bagType.createEmpty();
	}

	@Override
	public String toString(){
		return typeId;
	}
}
