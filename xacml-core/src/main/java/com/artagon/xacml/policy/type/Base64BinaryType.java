package com.artagon.xacml.policy.type;

import com.artagon.xacml.policy.AttributeDataType;
import com.artagon.xacml.policy.BagOfAttributesType;
import com.artagon.xacml.policy.BaseAttributeValue;
import com.artagon.xacml.policy.type.AnyURIType.AnyURIValue;

public interface Base64BinaryType extends AttributeDataType
{	
	Base64BinaryValue create(Object any);
	Base64BinaryValue fromXacmlString(String v);
	BagOfAttributesType<Base64BinaryValue> bagOf();
	
	final class Base64BinaryValue extends BaseAttributeValue<BinaryValue>
	{
		public Base64BinaryValue(Base64BinaryType type, BinaryValue value) {
			super(type, value);
		}
	
		@Override
		public String toXacmlString() {
			return getValue().toBase64();
		}
	}
}
