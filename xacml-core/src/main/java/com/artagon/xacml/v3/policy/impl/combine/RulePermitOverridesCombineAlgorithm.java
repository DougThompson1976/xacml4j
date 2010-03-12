package com.artagon.xacml.v3.policy.impl.combine;

import com.artagon.xacml.v3.policy.Rule;

final class RulePermitOverridesCombineAlgorithm extends PermitOverrides<Rule> 
{
	public final static String ID = "urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:permit-overrides";
	
	public RulePermitOverridesCombineAlgorithm() {
		super(ID);
	}

}
