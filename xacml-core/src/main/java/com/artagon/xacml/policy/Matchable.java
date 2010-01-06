package com.artagon.xacml.policy;

public interface Matchable extends PolicyElement
{
	/**
	 * Matches this matchable against given
	 * evaluation context.
	 * 
	 * @param context an evaluation context
	 * @return {@link MatchResult} instance
	 */
	MatchResult match(EvaluationContext context);
}
