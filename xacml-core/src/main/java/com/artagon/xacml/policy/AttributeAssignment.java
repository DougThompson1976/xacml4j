package com.artagon.xacml.policy;

import org.oasis.xacml.azapi.constants.AzCategoryId;

import com.artagon.xacml.util.Preconditions;

public class AttributeAssignment
{
	private Attribute attribute;
	private AzCategoryId category;
	private String attributeId;
	private String issuer;
	
	public AttributeAssignment(String attributeId, 
			AzCategoryId category, String issuer, Attribute value){
		Preconditions.checkNotNull(attributeId);
		Preconditions.checkNotNull(category);
		Preconditions.checkNotNull(value);
		this.attributeId = attributeId;
		this.category = category;
		this.issuer = issuer;
		this.attribute = value;
	}
	
	
	/**
	 * Gets attribute identifier
	 * 
	 * @return attribute identifier
	 */
	public String getAttributeId(){
		return attributeId;
	}
	
	/**
	 * Gets attribute value
	 * 
	 * @return attribute value
	 */
	public Attribute getAttribute(){
		return attribute;
	}
	
	/**
	 * Gets attribute category
	 * 
	 * @return attribute category
	 */
	public AzCategoryId getCategory(){
		return category;
	}
	
	/**
	 * Gets attribute issuer identifier
	 * 
	 * @return attribute issuer
	 */
	public String getIssuer(){
		return issuer;
	}
}
