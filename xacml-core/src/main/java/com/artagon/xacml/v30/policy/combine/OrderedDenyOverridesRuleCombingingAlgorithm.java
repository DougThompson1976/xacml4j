package com.artagon.xacml.v30.policy.combine;

import com.artagon.xacml.v30.Rule;

public final class OrderedDenyOverridesRuleCombingingAlgorithm extends DenyOverrides<Rule>
{
	private final static String ID = "urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:ordered-deny-overrides";
	
	public OrderedDenyOverridesRuleCombingingAlgorithm(){
		super(ID);
	}
}
