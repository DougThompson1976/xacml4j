package org.xacml4j.v30.pdp;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xacml4j.v30.Advice;
import org.xacml4j.v30.AttributeCategory;
import org.xacml4j.v30.AttributeDesignatorKey;
import org.xacml4j.v30.AttributeSelectorKey;
import org.xacml4j.v30.BagOfAttributeExp;
import org.xacml4j.v30.CompositeDecisionRule;
import org.xacml4j.v30.CompositeDecisionRuleIDReference;
import org.xacml4j.v30.Decision;
import org.xacml4j.v30.DecisionRule;
import org.xacml4j.v30.EvaluationContext;
import org.xacml4j.v30.EvaluationException;
import org.xacml4j.v30.Obligation;
import org.xacml4j.v30.PolicyResolutionException;
import org.xacml4j.v30.StatusCode;
import org.xacml4j.v30.ValueExpression;
import org.xacml4j.v30.XPathVersion;
import org.xacml4j.v30.spi.repository.PolicyReferenceResolver;
import org.xacml4j.v30.spi.xpath.XPathProvider;
import org.xacml4j.v30.types.XacmlTypes;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Ticker;


public final class RootEvaluationContext implements EvaluationContext {

	protected final Logger log = LoggerFactory.getLogger(RootEvaluationContext.class);

	private final XPathVersion defaultXPathVersion;
	private final EvaluationContextHandler contextHandler;
	private final PolicyReferenceResolver resolver;
	private final Map<String, Advice> denyAdvices;
	private final Map<String, Obligation> denyObligations;
	private final Map<String, Advice> permitAdvices;
	private final Map<String, Obligation> permitObligations;
	private final List<CompositeDecisionRuleIDReference> evaluatedPolicies;
	private final TimeZone timezone;
	private final Calendar currentDateTime;
	private final Map<AttributeDesignatorKey, BagOfAttributeExp> designCache;
	private final Map<AttributeSelectorKey, BagOfAttributeExp> selectCache;
	private final Map<AttributeDesignatorKey, BagOfAttributeExp> resolvedDesignators;
	private final Ticker ticker = Ticker.systemTicker();
	private boolean validateFuncParamsAtRuntime = false;
	private StatusCode evaluationStatus;
	private Integer combinedDecisionCacheTTL = null;
	private final boolean extendedIndeterminateEval = false;

	public RootEvaluationContext(
			boolean validateFuncParamsAtRuntime,
			int defaultDecisionCacheTTL,
			XPathVersion defaultXPathVersion,
			PolicyReferenceResolver referenceResolver,
			EvaluationContextHandler contextHandler) {
		Preconditions.checkNotNull(contextHandler);
		Preconditions.checkNotNull(referenceResolver);
		this.denyAdvices = new LinkedHashMap<String, Advice>();
		this.denyObligations = new LinkedHashMap<String, Obligation>();
		this.permitAdvices = new LinkedHashMap<String, Advice>();
		this.permitObligations = new LinkedHashMap<String, Obligation>();
		this.contextHandler = contextHandler;
		this.resolver = referenceResolver;
		this.timezone = TimeZone.getTimeZone("UTC");
		this.currentDateTime = Calendar.getInstance(timezone);
		this.evaluatedPolicies = new LinkedList<CompositeDecisionRuleIDReference>();
		this.designCache = new HashMap<AttributeDesignatorKey, BagOfAttributeExp>(128);
		this.selectCache = new HashMap<AttributeSelectorKey, BagOfAttributeExp>(128);
		this.resolvedDesignators = new HashMap<AttributeDesignatorKey, BagOfAttributeExp>();
		this.combinedDecisionCacheTTL = (defaultDecisionCacheTTL > 0)?defaultDecisionCacheTTL:null;
		this.defaultXPathVersion = defaultXPathVersion;
	}

	public RootEvaluationContext(
			boolean validateFuncParamsAtRuntime,
			int defaultDecisionCacheTTL,
			PolicyReferenceResolver referenceResolver,
			EvaluationContextHandler handler){
		this(validateFuncParamsAtRuntime,
				defaultDecisionCacheTTL,
				XPathVersion.XPATH1,
				referenceResolver,
				handler);
	}

	@Override
	public XPathVersion getXPathVersion() {
		return defaultXPathVersion;
	}


	@Override
	public Ticker getTicker(){
		return ticker;
	}

	@Override
	public boolean isExtendedIndeterminateEval() {
		return extendedIndeterminateEval;
	}
	
	@Override
	public EvaluationContext createExtIndeterminateEvalContext() {
		return new DelegatingEvaluationContext(this){
			@Override
			public EvaluationContext createExtIndeterminateEvalContext() {
				return this;
			}

			@Override
			public boolean isExtendedIndeterminateEval() {
				return true;
			}
		};
	}

	@Override
	public StatusCode getEvaluationStatus() {
		return evaluationStatus;
	}

	@Override
	public void setEvaluationStatus(StatusCode status){
		this.evaluationStatus = status;
	}

	@Override
	public int getDecisionCacheTTL() {
		return Objects.firstNonNull(combinedDecisionCacheTTL, 0);
	}

	@Override
	public void setDecisionCacheTTL(int ttl) {
		if(combinedDecisionCacheTTL == null){
			this.combinedDecisionCacheTTL = ttl;
			return;
		}
		this.combinedDecisionCacheTTL = (ttl > 0)?Math.min(this.combinedDecisionCacheTTL, ttl):0;
	}

	@Override
	public TimeZone getTimeZone(){
		Preconditions.checkState(timezone != null);
		return timezone;
	}

	@Override
	public final Calendar getCurrentDateTime() {
		return currentDateTime;
	}

	@Override
	public final void addEvaluationResult(CompositeDecisionRule policy, Decision result) {
		this.evaluatedPolicies.add(policy.getReference());
	}

	@Override
	public boolean isValidateFuncParamsAtRuntime() {
		return validateFuncParamsAtRuntime;
	}

	@Override
	public void setValidateFuncParamsAtRuntime(boolean validate){
		this.validateFuncParamsAtRuntime = validate;
	}

	@Override
	public void addAdvices(Decision d, Iterable<Advice> advices)
	{
		Preconditions.checkNotNull(d);
		if(d.isIndeterminate() ||
				d == Decision.NOT_APPLICABLE){
			return;
		}
		for(Advice a : advices){
			addAndMergeAdvice(d, a);
		}
	}

	@Override
	public void addObligations(Decision d, Iterable<Obligation> obligations)
	{
		Preconditions.checkNotNull(d);
		if(d.isIndeterminate() ||
				d == Decision.NOT_APPLICABLE){
			return;
		}
		for(Obligation a : obligations){
			addAndMergeObligation(d, a);
		}
	}

	private void addAndMergeAdvice(Decision d, Advice a)
	{
		Preconditions.checkArgument(d == Decision.PERMIT || d == Decision.DENY);
		Map<String, Advice> advices = (d == Decision.PERMIT)?permitAdvices:denyAdvices;
		Advice other = advices.get(a.getId());
		if(other != null){
			advices.put(a.getId(), other.merge(a));
		}else{
			advices.put(a.getId(), a);
		}
	}

	private void addAndMergeObligation(Decision d, Obligation a)
	{
		Preconditions.checkArgument(d == Decision.PERMIT || d == Decision.DENY);
		Map<String, Obligation> obligations = (d == Decision.PERMIT)?permitObligations:denyObligations;
		Obligation other = obligations.get(a.getId());
		if(other != null){
			obligations.put(a.getId(), other.merge(a));
		}else{
			obligations.put(a.getId(), a);
		}
	}

	/**
	 * Implementation always return {@code null}
	 */
	@Override
	public EvaluationContext getParentContext() {
		return null;
	}

	/**
	 * Implementation always returns {@code null}
	 */
	@Override
	public Policy getCurrentPolicy() {
		return null;
	}

	@Override
	public DecisionRule getCurrentRule() {
		return null;
	}

	/**
	 * Implementation always returns {@code null}
	 */
	@Override
	public PolicyIDReference getCurrentPolicyIDReference() {
		return null;
	}

	/**
	 * Implementation always returns {@code null}
	 */
	@Override
	public PolicySetIDReference getCurrentPolicySetIDReference() {
		return null;
	}

	/**
	 * Implementation always returns {@code null}
	 */
	@Override
	public PolicySet getCurrentPolicySet() {
		return null;
	}

	@Override
	public final ValueExpression getVariableEvaluationResult(
			String variableId){
		return null;
	}

	@Override
	public final void setVariableEvaluationResult(String variableId, ValueExpression value) {
	}

	@Override
	public final CompositeDecisionRule resolve(CompositeDecisionRuleIDReference ref)
			throws PolicyResolutionException
	{
		if(ref instanceof PolicyIDReference){
			return resolve((PolicyIDReference)ref);
		}
		if(ref instanceof PolicySetIDReference){
			return resolve((PolicySetIDReference)ref);
		}
		throw new PolicyResolutionException(this,
				"Failed to resolve reference");
	}

	private Policy resolve(PolicyIDReference ref)
		throws PolicyResolutionException {
		Policy p =	resolver.resolve(ref);
		if(log.isDebugEnabled()){
			log.debug("Trying to resolve " +
					"Policy reference=\"{}\"", ref);
		}
		if(p == null){
			throw new PolicyResolutionException(this,
					"Failed to resolve PolicySet reference");
		}
		return p;
	}

	private PolicySet resolve(PolicySetIDReference ref)
			throws PolicyResolutionException {
		PolicySet p = resolver.resolve(ref);
		if(log.isDebugEnabled()){
			log.debug("Trying to resolve " +
					"PolicySet reference=\"{}\"", ref);
		}
		if(p == null){
			throw new PolicyResolutionException(this, 
					"Failed to resolve PolicySet reference");
		}
		return p;
	}

	@Override
	public final Node evaluateToNode(String path, AttributeCategory categoryId)
			throws EvaluationException
	{
		return contextHandler.evaluateToNode(this, path, categoryId);
	}

	@Override
	public final NodeList evaluateToNodeSet(String path,
			AttributeCategory categoryId)
			throws EvaluationException
	{
		return 
	}

	@Override
	public final Number evaluateToNumber(String path, AttributeCategory categoryId)
			throws EvaluationException {
		return contextHandler.evaluateToNumber(this, path, categoryId);
	}

	@Override
	public final String evaluateToString(String path, AttributeCategory categoryId)
			throws EvaluationException
	{
		return contextHandler.evaluateToString(this, path, categoryId);
	}

	@Override
	public final BagOfAttributeExp resolve(
			AttributeDesignatorKey ref)
		throws EvaluationException
	{
		BagOfAttributeExp v = designCache.get(ref);
		if(v != null){
			if(log.isDebugEnabled()){
				log.debug("Found designator=\"{}\" " +
						"value=\"{}\" in cache", ref, v);
			}
			return v;
		}
		v = contextHandler.resolve(this, ref);
		v = (v == null)?ref.getDataType().emptyBag():v;
		if(log.isDebugEnabled()){
			log.debug("Resolved " +
					"designator=\"{}\" to value=\"{}\"", ref, v);
		}
		this.designCache.put(ref, (v == null)?ref.getDataType().emptyBag():v);
		return v;
	}

	@Override
	public final BagOfAttributeExp resolve(
			AttributeSelectorKey ref)
			throws EvaluationException
	{
		BagOfAttributeExp v = selectCache.get(ref);
		if(v != null){
			if(log.isDebugEnabled()){
				log.debug("Found selector=\"{}\" " +
						"value=\"{}\" in cache", ref, v);
			}
			return v;
		}
		v = contextHandler.resolve(this, ref);
		v = (v == null)?ref.getDataType().emptyBag():v;
		if(log.isDebugEnabled()){
			log.debug("Resolved " +
					"selector=\"{}\" to value=\"{}\"", ref, v);
		}
		this.selectCache.put(ref, (v == null)?ref.getDataType().emptyBag():v);
		return v;
	}

	@Override
	public void setResolvedDesignatorValue(
			AttributeDesignatorKey key,
			BagOfAttributeExp v){
		Preconditions.checkNotNull(key);
		this.resolvedDesignators.put(key, (v == null) ? key.getDataType().emptyBag() : v);
		this.designCache.put(key, (v == null) ? key.getDataType().emptyBag() : v);
	}

	@Override
	public Collection<CompositeDecisionRuleIDReference> getEvaluatedPolicies() {
		return Collections.unmodifiableList(evaluatedPolicies);
	}

	@Override
	public Map<AttributeDesignatorKey, BagOfAttributeExp> getResolvedDesignators() {
		return Collections.unmodifiableMap(resolvedDesignators);
	}

	@Override
	public Collection<Obligation> getMatchingObligations(final Decision decision) {
		return (decision == Decision.PERMIT)?permitObligations.values():denyObligations.values();
	}

	@Override
	public Collection<Advice> getMatchingAdvices(final Decision decision) {
		return (decision == Decision.PERMIT)?permitAdvices.values():denyAdvices.values();
	}

	@Override
	public String toString() {
		return Objects
				.toStringHelper(this)
				.add("defaultXPathVersion", defaultXPathVersion)
				.add("contextHandler", contextHandler)
				.add("resolver", resolver)
				.add("denyAdvices", denyAdvices)
				.add("denyObligations", denyObligations)
				.add("permitAdvices", permitAdvices)
				.add("permitObligations", permitObligations)
				.add("evaluatedPolicies", evaluatedPolicies)
				.add("timezone", timezone)
				.add("currentDateTime", currentDateTime)
				.add("designCache", designCache)
				.add("selectCache", selectCache)
				.add("resolvedDesignators", resolvedDesignators)
				.add("ticker", ticker)
				.add("validateFuncParamsAtRuntime", validateFuncParamsAtRuntime)
				.add("evaluationStatus", evaluationStatus)
				.add("combinedDecisionCacheTTL", combinedDecisionCacheTTL)
				.add("extendedIndeterminateEval", extendedIndeterminateEval)
				.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(defaultXPathVersion,
				contextHandler,
				resolver,
				denyAdvices,
				denyObligations,
				permitAdvices,
				permitObligations,
				evaluatedPolicies,
				timezone,
				currentDateTime,
				designCache,
				selectCache,
				resolvedDesignators,
				validateFuncParamsAtRuntime,
				evaluationStatus,
				combinedDecisionCacheTTL,
				extendedIndeterminateEval);
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof RootEvaluationContext)) {
			return false;
		}
		final RootEvaluationContext c = (RootEvaluationContext) o;
		return Objects.equal(defaultXPathVersion, c.defaultXPathVersion)
			&& Objects.equal(contextHandler, c.contextHandler)
			&& Objects.equal(resolver, c.resolver)
			&& Objects.equal(denyAdvices, c.denyAdvices)
			&& Objects.equal(denyObligations, c.denyObligations)
			&& Objects.equal(permitAdvices, c.permitAdvices)
			&& Objects.equal(permitObligations, c.permitObligations)
			&& Objects.equal(evaluatedPolicies, c.evaluatedPolicies)
			&& Objects.equal(timezone, c.timezone)
			&& Objects.equal(currentDateTime, c.currentDateTime)
			&& Objects.equal(designCache, c.designCache)
			&& Objects.equal(selectCache, c.selectCache)
			&& Objects.equal(resolvedDesignators, c.resolvedDesignators)
			&& Objects.equal(validateFuncParamsAtRuntime, c.validateFuncParamsAtRuntime)
			&& Objects.equal(evaluationStatus, c.evaluationStatus)
			&& Objects.equal(combinedDecisionCacheTTL, c.combinedDecisionCacheTTL)
			&& Objects.equal(extendedIndeterminateEval, c.extendedIndeterminateEval);
	}

	/**
	 * Clears context state
	 */
	public void clear() {
		this.combinedDecisionCacheTTL = null;
		this.designCache.clear();
		this.selectCache.clear();
		this.evaluatedPolicies.clear();
	}
}


