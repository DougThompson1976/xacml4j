package com.artagon.xacml.v3;

import java.util.Calendar;
import java.util.Collection;
import java.util.TimeZone;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public interface EvaluationContext 
{	
	/**
	 * Gets time zone used in PDP time
	 * calculations
	 * 
	 * @return {@link TimeZone}
	 */
	TimeZone getTimeZone();
	
	Calendar getCurrentDateTime();

	/**
	 * Tests if function parameters
	 * need to be validate at runtime
	 * every time function is invoked
	 * 
	 * @return <code>true</code> if parameters
	 * need to be validated at runtime
	 */
	boolean isValidateFuncParamsAtRuntime();
	
	/**
	 * Enables/Disables function parameters validation
	 * at runtime
	 * 
	 * @param validate a flag to validate
	 */
	void setValidateFuncParamsAtRuntime(boolean validate);
		
	/**
	 * Gets XACML policy evaluation failure 
	 * status
	 * 
	 * @return a failure {@link Status}
	 */
	StatusCode getEvaluationStatus();
	void setEvaluationStatus(StatusCode code);
	
	/**
	 * Gets current attribute reference resolution 
	 * scope. For example scope while evaluating
	 * {@link Match} is {@link AttributeResolutionScope#REQUEST}
	 * and {@link AttributeResolutionScope#REQUEST_EXTERNAL} while
	 * evaluating policy rules
	 * 
	 * @return {@link AttributeResolutionScope}
	 */
	AttributeResolutionScope getAttributeResolutionScope();
	
	/**
	 * Gets parent evaluation context
	 * 
	 * @return parent evaluation context or
	 * <code>null</code>
	 */
	EvaluationContext getParentContext();
	
	/**
	 * Returns a list of all policies which were found 
	 * to be fully applicable during evaluation.
	 * 
	 * @return a collection of {@link PolicyIdentifier}
	 */
	Collection<CompositeDecisionRuleIDReference> getEvaluatedPolicies();
	
	/**
	 * Adds evaluated policy and policy 
	 * evaluation result to the context
	 * 
	 * @param policy an evaluated policy
	 * @param result a policy evaluaton result
	 */
	void addEvaluatedPolicy(Policy policy, Decision result);
	
	/**
	 * Adds evaluated policy set and policy set
	 * evaluation result to the context
	 * 
	 * @param policy an evaluated policy set
	 * @param result a policy set evaluation result
	 */
	void addEvaluatedPolicySet(PolicySet policySet, Decision result);
	
	/**
	 * Gets currently evaluated policy
	 * 
	 * @return {@link Policy} or <code>null</code>
	 */
	Policy getCurrentPolicy();
	
	/**
	 * Gets currently evaluated policy set
	 * 
	 * @return {@link PolicySet} or <code>null</code>
	 */
	PolicySet getCurrentPolicySet();
	
	/**
	 * Gets current {@link PolicyIDReference}
	 * 
	 * @return current {@link PolicyIDReference} or
	 * <code>null</code>
	 */
	PolicyIDReference getCurrentPolicyIDReference();
	
	/**
	 * Gets currently evaluated {@link PolicySetIDReference}
	 * 
	 * @return {@link PolicySetIDReference} or <code>null</code>
	 */
	PolicySetIDReference getCurrentPolicySetIDReference();
	
	/**
	 * Gets all advice instances from
	 * decision evaluated in this context
	 * 
	 * @return collection of {@link Advice}
	 * instances
	 */
	Collection<Advice> getAdvices();
	
	/**
	 * Gets all obligations from decision
	 * evaluated in this context
	 * 
	 * @return collection of {@link Obligation}
	 * instances
	 */
	Collection<Obligation> getObligations();
	
	/**
	 * Gets XPath version
	 * 
	 * @return {@link XPathVersion}
	 */
	XPathVersion getXPathVersion();
	
	/**
	 * Adds evaluated obligation from
	 * decision evaluated in this context 
	 * 
	 * @param obligations a collection of {@link Obligation}
	 */
	void addObligations(Collection<Obligation> obligations);
	
	/**
	 * Adds evaluated advice instances to this context
	 * from decision evaluated in this context
	 * 
	 * @param advices
	 */
	void addAdvices(Collection<Advice> advices);
	
	/**
	 * Gets variable evaluation result for given
	 * variable identifier.
	 * 
	 * @param variableId a variable identifier
	 * @return {@link ValueExpression} instance or {@code null}
	 */
	 ValueExpression getVariableEvaluationResult(String variableId);
	
	/**
	 * Caches current policy variable evaluation result.
	 * 
	 * @param variableId a variable identifier
	 * @param value a variable value
	 */
	void setVariableEvaluationResult(String variableId, ValueExpression value);
	
	
	BagOfAttributeValues resolve(AttributeDesignator ref) 
		throws EvaluationException;
	
	BagOfAttributeValues resolve(AttributeSelector ref) 
		throws EvaluationException;
	

	NodeList evaluateToNodeSet(
			String xpath, 
			AttributeCategories categoryId) 
		throws EvaluationException;
	
	
	String evaluateToString(
			String path, 
			AttributeCategories categoryId) 
		throws EvaluationException;
	
	Node evaluateToNode(
			String path, 
			AttributeCategories categoryId) 
		throws EvaluationException;
	
	Number evaluateToNumber(
			String path, 
			AttributeCategories categoryId) 
		throws EvaluationException;
	
	/**
	 * Resolves given {@link PolicyIDReference}
	 * reference
	 * 
	 * @param ref a policy reference
	 * @return resolved {@link Policy} instance
	 * @throws PolicyResolutionException if
	 * policy reference can not be resolved
	 */
	Policy resolve(PolicyIDReference ref) 
		throws PolicyResolutionException;
	
	/**
	 * Resolves given {@link PolicySetIDReference}
	 * reference
	 * 
	 * @param ref a policy reference
	 * @return resolved {@link PolicySet} instance
	 * @throws PolicyResolutionException if
	 * policy set reference can not be resolved
	 */
	PolicySet resolve(PolicySetIDReference ref) 
		throws PolicyResolutionException;
	
	/**
	 * Gets value of a given category from this context
	 * 
	 * @param categoryId an attribute category
	 * @param key an key used to identify value
	 * @return {@link Object} an value or <code>null</code>
	 * if no value exist for given category and key
	 */
	Object getValue(AttributeCategory categoryId, 
			Object key);
	
	Object setValue(AttributeCategory categoryId, 
			Object key, Object v);
}