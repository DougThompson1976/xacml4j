package com.artagon.xacml.policy.combine;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.artagon.xacml.policy.CompositeDecision;
import com.artagon.xacml.policy.DecisionResult;
import com.artagon.xacml.policy.EvaluationContext;
import com.artagon.xacml.policy.MatchResult;

public class PolicyOnlyOneApplicable extends BaseDecisionCombiningAlgorithm<CompositeDecision> 
{
	private final static Logger log = LoggerFactory.getLogger(PolicyOnlyOneApplicable.class);
	
	public PolicyOnlyOneApplicable() {
		super("urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:only-one-applicable");
	}

	@Override
	public DecisionResult combine(List<CompositeDecision> decisions,
			EvaluationContext context) 
	{
		boolean atLeastOne = false;
		CompositeDecision found = null;
		EvaluationContext policyContext = null;
		for(CompositeDecision d : decisions)
		{
			policyContext = d.createContext(context);
			MatchResult r = d.isApplicable(policyContext);
			log.debug("Decision id=\"{}\" applicability=\"{}\"", d.getId(), r);
			if(r == MatchResult.INDETERMINATE){
				return DecisionResult.INDETERMINATE;
			}
			if(r == MatchResult.MATCH){
				if(atLeastOne){
					return DecisionResult.INDETERMINATE;
				}
				atLeastOne = true;
				found = d;
			}
			continue;
		}
		if(atLeastOne && log.isDebugEnabled()){
			log.debug("Found one applicable decision id=\"{}\"", found.getId());
		}
		return atLeastOne?found.evaluate(policyContext):DecisionResult.NOT_APPLICABLE;
	}
}
