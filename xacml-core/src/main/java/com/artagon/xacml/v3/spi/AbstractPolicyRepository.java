package com.artagon.xacml.v3.spi;

import java.util.Collection;

import com.artagon.xacml.v3.EvaluationContext;
import com.artagon.xacml.v3.Policy;
import com.artagon.xacml.v3.PolicyIDReference;
import com.artagon.xacml.v3.PolicyResolutionException;
import com.artagon.xacml.v3.PolicySet;
import com.artagon.xacml.v3.PolicySetIDReference;
import com.artagon.xacml.v3.VersionMatch;
import com.google.common.collect.Iterables;

/**
 * A base class for {@link PolicyRepository} implementations
 * 
 * @author Giedrius Trumpickas
 */
public abstract class AbstractPolicyRepository implements PolicyRepository
{
	@Override
	public final Policy resolve(EvaluationContext context, PolicyIDReference ref)
			throws PolicyResolutionException {
		Collection<Policy> found = getPolicies(ref.getId(), ref.getVersionMatch(), 
				ref.getEarliestVersion(), ref.getLatestVersion());
		return found.isEmpty()?null:Iterables.getLast(found);
	}

	@Override
	public final PolicySet resolve(EvaluationContext context, PolicySetIDReference ref)
			throws PolicyResolutionException {
		Collection<PolicySet> found = getPolicySets(ref.getId(), ref.getVersionMatch(), 
				ref.getEarliestVersion(), ref.getLatestVersion());
		return found.isEmpty()?null:Iterables.getLast(found);
	}
	
	@Override
	public final Collection<Policy> getPolicies(String id, VersionMatch version){
		return getPolicies(id, version, null, null);
	}
	
	@Override
	public final Collection<PolicySet> getPolicySets(String id, VersionMatch version){
		return getPolicySets(id, version, null, null);
	}
	
}
