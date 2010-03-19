package com.artagon.xacml.v3;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import com.artagon.xacml.util.Preconditions;
import com.artagon.xacml.v3.policy.AttributeValue;
import com.artagon.xacml.v3.policy.AttributeValueType;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

public class Attribute extends XacmlObject
{
	private String attributeId;
	private Multiset<AttributeValue> values;
	private boolean includeInResult;
	private String issuer;
	
	public Attribute(String attributeId,
			String issuer, 
			boolean includeInResult, 
			Collection<AttributeValue> values){
		Preconditions.checkNotNull(attributeId);
		Preconditions.checkNotNull(values);
		this.attributeId = attributeId;
		this.values = HashMultiset.create(values.size());
		this.values.addAll(values);
		this.includeInResult = includeInResult;
	}
	
	public Attribute(String attributeId, 
			Collection<AttributeValue> values){
		this(attributeId, null, false, values);
	}
	
	public Attribute(String attributeId, 
			AttributeValue ...values){
		this(attributeId, null, false, Arrays.asList(values));
	}
	
	/**
	 * Gets attribute identifier.
	 * 
	 * @return attribute identifier
	 */
	public String getAttributeId(){
		return attributeId;
	}
	
	/**
	 * Gets attribute values as collection of
	 * {@link AttributeValue} instances
	 * 
	 * @return collection of {@link AttributeValue} 
	 * instances
	 */
	public Collection<AttributeValue> getValues(){
		return Collections.unmodifiableCollection(values);
	}
	
	public String getIssuer(){
		return issuer;
	}
	
	public boolean isIncludeInResult(){
		return includeInResult;
	}
	
	public Collection<AttributeValue> getValuesByType(final AttributeValueType type){
		return Collections2.filter(values, new Predicate<AttributeValue>() {
			@Override
			public boolean apply(AttributeValue v) {
				return v.getType().equals(type);
			}
		});
	}
}
