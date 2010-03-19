package com.artagon.xacml.v3.profiles;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import com.artagon.xacml.v3.Attributes;
import com.artagon.xacml.v3.AttributesReference;
import com.artagon.xacml.v3.RequestContext;
import com.artagon.xacml.v3.RequestContextException;
import com.artagon.xacml.v3.RequestReference;
import com.artagon.xacml.v3.StatusCode;

public class MultipleRequestsProfile implements RequestContextProfile
{
	@Override
	public Collection<RequestContext> process(RequestContext context) throws RequestContextException 
	{
		if(!context.hasMultipleRequests()){
			return Collections.singleton(context);
		}
		Collection<RequestReference> references = context.getMultipleRequests();
		Collection<RequestContext> resolved = new ArrayList<RequestContext>(references.size());
		for(RequestReference ref : context.getMultipleRequests()){
			resolved.add(resolveAttributes(context, ref));
		}
		return resolved;
	}
	
	private RequestContext resolveAttributes(RequestContext context, 
			RequestReference reqRef) throws RequestContextException
	{
		Collection<Attributes> resolved = new LinkedList<Attributes>();
		for(AttributesReference ref : reqRef.getReferencedAttributes()){
			Attributes attributes = context.getReferencedAttributes(ref);
			if(attributes == null){
				throw new RequestContextException(
						StatusCode.createSyntaxError(), 
						"Failed to resolve attribute reference with id=\"%s\"", 
						ref.getReferenceId());
			}
			resolved.add(attributes);
		}
		return new RequestContext(context.isReturnPolicyIdList(), resolved);
	}
	
}
