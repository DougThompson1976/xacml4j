package org.xacml4j.v30;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

public class RequestContext
{
	private boolean returnPolicyIdList;
	private boolean combinedDecision;
	private Multimap<AttributeCategory, Attributes> attributes;
	private Map<String, Attributes> attributesByXmlId;
	private Collection<RequestReference> requestReferences;
	private RequestDefaults requestDefaults;

	private transient int cachedHashCode;

	/**
	 * Constructs request with a given arguments
	 *
	 * @param returnPolicyIdList a flag indicating
	 * that response should contains applicable
	 * evaluated policy or policy set identifiers
	 * list
	 * @param attributes a collection of request attributes
	 * @param requestReferences a request references
	 * @param requestDefaults a request defaults
	 */
	public RequestContext(
			boolean returnPolicyIdList,
			boolean combinedDecision,
			Collection<Attributes> attributes,
			Collection<RequestReference> requestReferences,
			RequestDefaults requestDefaults)
	{
		this.returnPolicyIdList = returnPolicyIdList;
		this.attributes = LinkedListMultimap.create();
		this.requestReferences = (requestReferences == null)?
				Collections.<RequestReference>emptyList():new ArrayList<RequestReference>(requestReferences);
		this.attributesByXmlId = new HashMap<String, Attributes>();
		this.requestDefaults = requestDefaults;
		this.combinedDecision = combinedDecision;
		if(attributes != null){
			for(Attributes attr : attributes){
				// index attributes by category
				this.attributes.put(attr.getCategory(), attr);
				// index attributes
				// by id for fast lookup
				if(attr.getId() != null){
					this.attributesByXmlId.put(attr.getId(), attr);
				}
			}
		}
		this.cachedHashCode = Objects.hashCode(
				this.returnPolicyIdList,
				this.combinedDecision,
				this.attributes,
				this.requestReferences,
				this.requestDefaults);
	}

	private RequestContext(Builder b)
	{
		this.returnPolicyIdList = b.returnPolicyIdList;
		this.attributes = LinkedListMultimap.create();
		this.requestReferences = b.reqRefs.build();
		this.attributesByXmlId = new HashMap<String, Attributes>();
		this.requestDefaults = b.reqDefaults;
		this.combinedDecision = b.combinedDecision;
		this.attributes = b.attrBuilder.build();
		for(Attributes attr : attributes.values()){
				if(attr.getId() != null){
					this.attributesByXmlId.put(attr.getId(), attr);
				}
		}
		this.cachedHashCode = Objects.hashCode(
				this.returnPolicyIdList,
				this.combinedDecision,
				this.attributes,
				this.requestReferences,
				this.requestDefaults);
	}

	/**
	 * Constructs a request with a given attributes
	 *
	 * @param attributes a collection of {@link Attributes}
	 * instances
	 */
	public RequestContext(boolean returnPolicyIdList,
			boolean combinedDecision,
			Collection<Attributes> attributes,
			Collection<RequestReference> requestReferences)
	{
		this(returnPolicyIdList, combinedDecision, attributes,
				requestReferences, new RequestDefaults());
	}

	public RequestContext(boolean returnPolicyIdList,
			boolean combinedDecision,
			Collection<Attributes> attributes)
	{
		this(returnPolicyIdList, combinedDecision, attributes,
				Collections.<RequestReference>emptyList());
	}

	/**
	 * Constructs a request with a given attributes
	 *
	 * @param attributes a collection of {@link Attributes}
	 * instances
	 */
	public RequestContext(boolean returnPolicyIdList,
			Collection<Attributes> attributes)
	{
		this(returnPolicyIdList, false, attributes,
				Collections.<RequestReference>emptyList());
	}

	/**
	 * Constructs a request with a given attributes
	 *
	 * @param attributes a collection of {@link Attributes}
	 * instances
	 */
	public RequestContext(boolean returnPolicyIdList,
			Collection<Attributes> attributes,
			RequestDefaults requestDefaults)
	{
		this(returnPolicyIdList, false, attributes,
				Collections.<RequestReference>emptyList(), requestDefaults);
	}

	public static Builder builder(){
		return new Builder();
	}

	/**
	 * If the function returns {@code true}, a PDP that implements this optional
	 * feature MUST return a list of all policies which were
	 * found to be fully applicable. That is, all policies
	 * where both the {@link org.xacml4j.v30.pdp.Target} matched and the {@link org.xacml4j.v30.pdp.Condition}
	 * evaluated to {@code true}, whether or not the {@link Effect}
	 *  was the same or different from the {@link Decision}}
	 *
	 * @return boolean value
	 */
	public boolean isReturnPolicyIdList(){
		return returnPolicyIdList;
	}

	/**
	 * Gets a flag used to request that the PDP combines multiple
	 * decisions into a single decision. The use of this attribute
	 * is specified in [Multi]. If the PDP does not implement the relevant
	 * functionality in [Multi], then the PDP must return an Indeterminate
	 * with a status code "Processing Error @{link {@link StatusCode#isProcessingError()} returns
	 * {@code true} if it receives a request with this attribute
	 * set to {@code true}
	 *
	 * @return {@code true} if the decision is combined; returns {@code false} otherwise
	 */
	public boolean isCombinedDecision(){
		return combinedDecision;
	}

	/**
	 * Gets request defaults
	 *
	 * @return an instance of {@link RequestDefaults}
	 */
	public RequestDefaults getRequestDefaults(){
		return requestDefaults;
	}

	/**
	 * Gets occurrence of the given category attributes
	 * in this request
	 *
	 * @param category a category
	 * @return a non-negative number indicating attributes of given
	 * category occurrence in this request
	 */
	public int getCategoryOccuriences(AttributeCategory category){
		Collection<Attributes> attr = attributes.get(category);
		return (attr == null)?0:attr.size();
	}

	/**
	 * Gets request references contained
	 * in this request context
	 *
	 * @return a collection of {@link RequestReference}
	 * instances
	 */
	public Collection<RequestReference> getRequestReferences(){
		return Collections.unmodifiableCollection(requestReferences);
	}

	/**
	 * Gets all {@link Attributes} instances contained
	 * in this request
	 * @return a collection of {@link Attributes}
	 * instances
	 */
	public Collection<Attributes> getAttributes(){
		return Collections.unmodifiableCollection(attributes.values());
	}

	/**
	 * Tests if this request has multiple
	 * individual XACML requests
	 *
	 * @return {@code true} if this
	 * request has multiple XACML individual
	 * requests
	 */
	public boolean containsRequestReferences(){
		return !requestReferences.isEmpty();
	}

	/**
	 * Gets all attribute categories contained
	 * in this request
	 *
	 * @return a set of all attribute categories in
	 * this request
	 */
	public Set<AttributeCategory> getCategories(){
		return Collections.unmodifiableSet(attributes.keySet());
	}

	/**
	 * Resolves attribute reference to {@link Attributes}
	 *
	 * @param reference an attributes reference
	 * @return {@link Attributes} or {@code null} if
	 * reference can not be resolved
	 */
	public Attributes getReferencedAttributes(AttributesReference reference){
		Preconditions.checkNotNull(reference);
		return attributesByXmlId.get(reference.getReferenceId());
	}

	/**
	 * Gets all {@link Attributes} instances
	 * from request of a given category
	 *
	 * @param categoryId an attribute category
	 * @return a collection of {@link Attributes}, if
	 * a request does not have attributes of a specified
	 * category an empty collection is returned
	 */
	public Collection<Attributes> getAttributes(AttributeCategory categoryId){
		Preconditions.checkNotNull(categoryId);
		return attributes.get(categoryId);
	}

	public Collection<Entity> getEntities(AttributeCategory c){	
		ImmutableList.Builder<Entity> b = ImmutableList.builder();
		for(Attributes a : getAttributes(c)){
			b.add(a.getEntity());
		}
		return b.build();
	}
	
	/**
	 * Gets only one attribute of the given category
	 *
	 * @param category a category identifier
	 * @return {@link Attributes} or {@code null}
	 * if request does not have attributes of given category
	 * @exception IllegalArgumentException if a request
	 * has more than one instance of {@link Attributes}
	 * of the requested category
	 */
	public Attributes getOnlyAttributes(AttributeCategory category){
		Collection<Attributes> attributes = getAttributes(category);
		return Iterables.getOnlyElement(attributes, null);
	}
	
	public Entity getOnlyEntity(AttributeCategory category){
		Collection<Entity> attributes = getEntities(category);
		return Iterables.getOnlyElement(attributes, null);
	}


	/**
	 * Tests if this request has an multiple
	 * {@link Attributes} instances with the
	 * same {@link Attributes#getCategory()} value
	 *
	 * @return {@code true} if this request
	 * has multiple attributes of same category
	 */
	public boolean containsRepeatingCategories(){
		for(AttributeCategory category : getCategories()){
			if(getCategoryOccuriences(category) > 1){
				return true;
			}
		}
		return false;
	}

	public boolean containsAttributeValues(
			String attributeId, String issuer, AttributeExpType type)
	{
		for(Attributes a : getAttributes()){
			Entity e = a.getEntity();
			Collection<AttributeExp> values =  e.getAttributeValues(attributeId, issuer, type);
			if(!values.isEmpty()){
				return true;
			}
		}
		return false;
	}

	/**
	 * Tests if a given request context contains attributes with a given
	 * identifier of the given type for any category
	 *
	 * @param attributeId an attribute identifier
	 * @param type an attribute type
	 * @return {@code true} if this request contains an at least
	 * one attribute with a given identifier and the given type
	 */
	public boolean containsAttributeValues(String attributeId,
			AttributeExpType type)
	{
		return containsAttributeValues(attributeId, null, type);
	}

	/**
	 * Gets attribute values for given category, issuer, attribute id and data type
	 *
	 * @param categoryId an category
	 * @param attributeId an attribute identifier
	 * @param dataType an attribute data type
	 * @param issuer an attribute issuer
	 * @return a collection of {@link AttributeExp} instances
	 */
	public Collection<AttributeExp> getAttributeValues(AttributeCategory categoryId,
			String attributeId, AttributeExpType dataType, String issuer)
	{
		ImmutableList.Builder<AttributeExp> found = ImmutableList.builder();
		for(Attributes a : attributes.get(categoryId)){
			Entity e = a.getEntity();
			found.addAll(e.getAttributeValues(attributeId, issuer, dataType));
		}
		return found.build();
	}

	/**
	 * Gets all attribute values of the given category with the
	 * given identifier and data type
	 *
	 * @param categoryId an attribute category
	 * @param attributeId an attribute identifier
	 * @param dataType an attribute data type
	 * @return a collection of {@link AttributeExp} instances
	 */
	public Collection<AttributeExp> getAttributeValues(
			AttributeCategory categoryId,
			String attributeId,
			AttributeExpType dataType)
	{
		return getAttributeValues(categoryId, attributeId, dataType, null);
	}

	/**
	 * Gets a single {@link AttributeExp} from this request
	 *
	 * @param categoryId an attribute category identifier
	 * @param attributeId an attribute identifier
	 * @param dataType an attribute data type
	 * @return {@link AttributeExp} or {@code null}
	 */
	public AttributeExp getAttributeValue(AttributeCategory categoryId,
			String attributeId,
			AttributeExpType dataType){
		return Iterables.getOnlyElement(
				getAttributeValues(categoryId, attributeId, dataType), null);
	}

	/**
	 * Gets all {@link Attributes} instances
	 * containing an attributes with {@link Attribute#isIncludeInResult()}
	 * returning {@code true}
	 *
	 * @return a collection of {@link Attributes} instances
	 * containing only {@link Attribute} with
	 * {@link Attribute#isIncludeInResult()} returning {@code true}
	 */
	public Collection<Attributes> getIncludeInResultAttributes()
	{
		ImmutableList.Builder<Attributes> resultAttr = ImmutableList.builder();
		for(Attributes a : attributes.values()){
			Entity e = a.getEntity();
			Collection<Attribute> includeInResult = e.getIncludeInResultAttributes();
			if(!includeInResult.isEmpty()){
				resultAttr.add(Attributes
						.builder(a.getCategory())
						.entity(Entity.builder().attributes(e.getIncludeInResultAttributes()).build())
						.build());
			}
		}
		return resultAttr.build();
	}

	@Override
	public String toString(){
		return Objects.toStringHelper(this)
		.add("ReturnPolicyIDList", returnPolicyIdList)
		.add("CombineDecision", combinedDecision)
		.addValue(attributes.values())
		.add("RequestReferences", requestReferences)
		.add("RequestDefaults", requestDefaults).toString();
	}

	@Override
	public int hashCode(){
		return cachedHashCode;
	}

	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(o == null){
			return false;
		}
		if(!(o instanceof RequestContext)){
			return false;
		}
		RequestContext r = (RequestContext)o;
		return !(returnPolicyIdList ^ r.returnPolicyIdList) &&
			!(combinedDecision ^ r.combinedDecision) && attributes.equals(attributes) &&
			requestReferences.equals(r.requestReferences) && Objects.equal(requestDefaults, r.requestDefaults);
	}

	public static class Builder
	{
		private boolean returnPolicyIdList;
		private boolean combinedDecision;
		private RequestDefaults reqDefaults = new RequestDefaults();
		private ImmutableListMultimap.Builder<AttributeCategory, Attributes> attrBuilder = ImmutableListMultimap.builder();
		private ImmutableList.Builder<RequestReference> reqRefs = ImmutableList.builder();

		public Builder returnPolicyIdList(){
			this.returnPolicyIdList = true;
			return this;
		}

		public Builder reqDefaults(RequestDefaults defaults){
			this.reqDefaults = defaults;
			return this;
		}

		public Builder combineDecision(boolean combine){
			this.combinedDecision = combine;
			return this;
		}

		public Builder returnPolicyIdList(boolean returnPolicyIdList){
			this.returnPolicyIdList = returnPolicyIdList;
			return this;
		}

		public Builder reference(RequestReference ...refs){
			this.reqRefs.add(refs);
			return this;
		}

		public Builder reference(Iterable<RequestReference> refs){
			this.reqRefs.addAll(refs);
			return this;
		}

		/**
		 * Copies all state to this builder from
		 * a given request context
		 *
		 * @param req a request context
		 * @return {@link Builder}
		 */
		public Builder copyOf(RequestContext req)
		{
			combineDecision(req.isCombinedDecision());
			returnPolicyIdList(req.isReturnPolicyIdList());
			reqDefaults(req.getRequestDefaults());
			attributes(req.getAttributes());
			reference(req.getRequestReferences());
			return this;
		}

		/**
		 * Copies all state to this builder from
		 * a given request context except attributes
		 *
		 * @param req a request context
		 * @return {@link Builder}
		 */
		public Builder copyOf(RequestContext req, Iterable<Attributes> attributes)
		{
			combineDecision(req.isCombinedDecision());
			returnPolicyIdList(req.isReturnPolicyIdList());
			reqDefaults(req.getRequestDefaults());
			attributes(attributes);
			return this;
		}

		public Builder noAttributes(){
			attrBuilder = ImmutableListMultimap.builder();
			return this;
		}

		public Builder attributes(Attributes ... attrs)
		{
			Preconditions.checkNotNull(attrs);
			for(Attributes a : attrs){
				this.attrBuilder.put(a.getCategory(), a);
			}
			return this;
		}

		public Builder attributes(Iterable<Attributes> attrs)
		{
			Preconditions.checkNotNull(attrs);
			for(Attributes a : attrs){
				this.attrBuilder.put(a.getCategory(), a);
			}
			return this;
		}

		public RequestContext build(){
			return new RequestContext(this);
		}

	}
}
