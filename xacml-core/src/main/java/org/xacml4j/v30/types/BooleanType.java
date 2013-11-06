package org.xacml4j.v30.types;

import java.util.Collection;

import org.xacml4j.v30.AttributeExp;
import org.xacml4j.v30.AttributeExpType;
import org.xacml4j.v30.BagOfAttributeExp;
import org.xacml4j.v30.BagOfAttributeExpType;

import com.google.common.base.Preconditions;

public enum BooleanType implements AttributeExpType
{
	BOOLEAN("http://www.w3.org/2001/XMLSchema#boolean");

	private final BooleanExp FALSE;
	private final BooleanExp TRUE;

	private final String typeId;
	private final BagOfAttributeExpType bagType;

	private BooleanType(String typeId){
		this.typeId = typeId;
		this.bagType = new BagOfAttributeExpType(this);
		this.FALSE = new BooleanExp(this, Boolean.FALSE);
		this.TRUE = new BooleanExp(this, Boolean.TRUE);
	}

	public boolean isConvertibleFrom(Object any) {
		return Boolean.class.isInstance(any) || String.class.isInstance(any);
	}

	@Override
	public BooleanExp create(Object any, Object ...parameters){
		Preconditions.checkNotNull(any);
		Preconditions.checkArgument(isConvertibleFrom(any),
				"Value=\"%s\" of type=\"%s\" can't be converted to XACML \"%s\" type",
				any, any.getClass(), typeId);
		if(String.class.isInstance(any)){
			return fromXacmlString((String)any);
		}
		return ((Boolean)any)?TRUE:FALSE;
	}

	@Override
	public BooleanExp fromXacmlString(String v, Object ...parameters) {
		Preconditions.checkNotNull(v);
		return Boolean.parseBoolean(v)?TRUE:FALSE;
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
	public BagOfAttributeExp.Builder bag(){
		return new BagOfAttributeExp.Builder(this);
	}

	@Override
	public BagOfAttributeExp bagOf(Collection<AttributeExp> values) {
		return bagType.create(values);
	}

	@Override
	public BagOfAttributeExp bagOf(Object... values) {
		return bagType.bagOf(values);
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
