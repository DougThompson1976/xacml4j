package org.xacml4j.v30.types;

import javax.xml.namespace.QName;

import org.oasis.xacml.v30.jaxb.AttributeValueType;
import org.xacml4j.util.Xacml20XPathTo30Transformer;
import org.xacml4j.v30.AttributeCategories;
import org.xacml4j.v30.AttributeCategory;
import org.xacml4j.v30.AttributeExp;
import org.xacml4j.v30.AttributeExpType;
import org.xacml4j.v30.BagOfAttributeExp;
import org.xacml4j.v30.BagOfAttributeExpType;
import org.xacml4j.v30.XacmlSyntaxException;

import com.google.common.base.Preconditions;

public enum XPathExpType implements AttributeExpType, TypeToXacml30
{
	XPATHEXPRESSION("urn:oasis:names:tc:xacml:3.0:data-type:xpathExpression");

	private final String typeId;
	private final BagOfAttributeExpType bagType;
	
	public final static QName XPATH_CATEGORY_ATTR_NAME = new QName("XPathCategory");
	
	private XPathExpType(String typeId){
		this.typeId = typeId;
		this.bagType = new BagOfAttributeExpType(this);
	}

	public boolean isConvertibleFrom(Object any) {
		return (any instanceof String);
	}

	public XPathExp create(String xpath, AttributeCategory category) {
		return new XPathExp(xpath, category);
	}

	@Override
	public AttributeValueType toXacml30(Types types, AttributeExp v) {
		Preconditions.checkArgument(v.getType().equals(this));
		AttributeValueType xacml30 = new AttributeValueType();
		XPathExp xpath = (XPathExp)v;
		xacml30.setDataType(getDataTypeId());
		xacml30.getContent().add(xpath.getPath());
		xacml30.getOtherAttributes().put(XPATH_CATEGORY_ATTR_NAME, xpath.getCategory().getId());
		return xacml30;
	}
	
	@Override
	public AttributeExp fromXacml30(Types types, AttributeValueType v) {
		AttributeCategory categoryId = AttributeCategories.parse(v.getOtherAttributes().get(XPATH_CATEGORY_ATTR_NAME));
		if(v.getContent().size() > 0){
			if(categoryId == null){
				throw new XacmlSyntaxException(
						"XPath category can not be null");
			}
			return new XPathExp((String)v.getContent().get(0), categoryId);
		}
		throw new XacmlSyntaxException(
				"No content found for the attribute value"); 
	}
	
	@Override
	public String getDataTypeId() {
		return typeId;
	}

	@Override
	public BagOfAttributeExp.Builder bag(){
		return new BagOfAttributeExp.Builder(this);
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
	public BagOfAttributeExp emptyBag() {
		return bagType.createEmpty();
	}

	@Override
	public String toString(){
		return typeId;
	}
}
