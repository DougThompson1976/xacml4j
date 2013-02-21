package org.xacml4j.v30.pdp;

import java.util.Collection;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xacml4j.v30.Advice;
import org.xacml4j.v30.Decision;
import org.xacml4j.v30.DecisionRule;
import org.xacml4j.v30.EvaluationContext;
import org.xacml4j.v30.EvaluationException;
import org.xacml4j.v30.Expression;
import org.xacml4j.v30.MatchResult;
import org.xacml4j.v30.Obligation;
import org.xacml4j.v30.StatusCode;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

abstract class BaseDecisionRule implements DecisionRule
{
	protected static final Logger log = LoggerFactory.getLogger(BaseDecisionRule.class);

	protected String id;
	private String description;
	private Target target;
	protected Condition condition;
	protected Collection<AdviceExpression> adviceExpressions;
	protected Collection<ObligationExpression> obligationExpressions;


	protected BaseDecisionRule(
			BaseDecisionRuleBuilder<?> b){
		Preconditions.checkNotNull(b.id,
				"Decision rule identifier can not be null");
		this.id = b.id;
		this.description = b.description;
		this.target = b.target;
		this.condition = b.condition;
		this.adviceExpressions = b.adviceExpressions.build();
		this.obligationExpressions = b.obligationExpressions.build();
	}

	@Override
	public String getId(){
		return id;
	}

	/**
	 * Gets decision rule description
	 *
	 * @return decision rule description
	 */
	public String getDescription(){
		return description;
	}

	/**
	 * Gets decision rule target
	 *
	 * @return {@link Target} or <code>null</code>
	 * if rule is applicable to any request
	 */
	public Target getTarget(){
		return target;
	}

	/**
	 * Gets decision rule condition
	 *
	 * @return {@link Condition} or <code>null</code>
	 * implying always <code>true</code> condition
	 */
	public Condition getCondition(){
		return condition;
	}
	
	public Collection<AdviceExpression> getAdviceExpressions(){
		return adviceExpressions;
	}
	
	public Collection<ObligationExpression> getObligationExpressions(){
		return obligationExpressions;
	}

	/**
	 * Testing if this decision rule is applicable to
	 * the current evaluation context
	 *
	 * @param context an evaluation context
	 * @return {@link MatchResult} a match result
	 */
	@Override
	public MatchResult isMatch(EvaluationContext context){
		Preconditions.checkArgument(
				isEvaluationContextValid(context));
		return (target == null)?MatchResult.MATCH:target.match(context);
	}

	/**
	 * Evaluates all matching to the given
	 * decision rule advice and obligations
	 *
	 * @param context an evaluation context
	 * @param result a rule evaluation result
	 * @return a rule evaluation result or {@link Decision#INDETERMINATE}
	 * if any of the advice or obligation evaluation fails
	 */
	protected final Decision evaluateAdvicesAndObligations(
			EvaluationContext context, Decision result)
	{
		try
		{
			if(result.isIndeterminate() ||
					result == Decision.NOT_APPLICABLE){
				return result;
			}
			Collection<Advice> advices = evaluateAdvices(context, result);
			Collection<Obligation> obligations = evaluateObligations(context, result);
			context.addAdvices(result, advices);
			context.addObligations(result, obligations);
			return result;
		}catch(Exception e){
			return Decision.INDETERMINATE;
		}
	}

	/**
	 * Evaluates advice expressions matching given decision
	 * {@link Decision} result
	 *
	 * @param context an evaluation context
	 * @param result a decision evaluation result
	 * @return collection of {@link Advice} instances
	 * @throws EvaluationException if an evaluation error occurs
	 */
	private Collection<Advice> evaluateAdvices(EvaluationContext context,
			Decision result)
		throws EvaluationException
	{
		if(log.isDebugEnabled()){
			log.debug("Evaluating advices " +
					"for descision rule id=\"{}\"", getId());
		}
		Collection<Advice> advices = new LinkedList<Advice>();
		try{
			for(AdviceExpression adviceExp : adviceExpressions){
				if(adviceExp.isApplicable(result)){
					if(log.isDebugEnabled()){
						log.debug("Evaluating advice id=\"{}\"",
								adviceExp.getId());
					}
					Advice a = adviceExp.evaluate(context);
					Preconditions.checkState(a != null);
					advices.add(a);
				}
			}
			if(log.isDebugEnabled()){
				log.debug("Evaluated=\"{}\" applicable advices",
						advices.size());
			}
			return advices;
		}catch(EvaluationException e){
			if(log.isDebugEnabled()){
				log.debug("Failed to evaluate " +
						"decision rule advices", e);
			}
			throw e;
		}catch(Exception e){
			if(log.isDebugEnabled()){
				log.debug("Failed to evaluate " +
						"decision rule advices", e);
			}
			throw new EvaluationException(
					StatusCode.createProcessingError(),
					context, e);
		}
	}

	/**
	 * Evaluates obligation matching given decision
	 * {@link Decision} result
	 *
	 * @param context an evaluation context
	 * @param result an decision result
	 * @return collection of {@link Obligation} instances
	 * @throws EvaluationException if an evaluation error occurs
	 */
	private Collection<Obligation> evaluateObligations(EvaluationContext context, Decision result)
		throws EvaluationException
	{
		if(log.isDebugEnabled()){
			log.debug("Evaluating obligations " +
					"for descision rule id=\"{}\"", getId());
		}
		Collection<Obligation> obligations = new LinkedList<Obligation>();
		try{
			for(ObligationExpression obligationExp : obligationExpressions){
				if(obligationExp.isApplicable(result)){
					if(log.isDebugEnabled()){
						log.debug("Evaluating obligation id=\"{}\"",
								obligationExp.getId());
					}
					Obligation o  = obligationExp.evaluate(context);
					Preconditions.checkState(o != null);
					obligations.add(o);
				}
			}
			if(log.isDebugEnabled()){
				log.debug("Evaluated=\"{}\" applicable obligations",
						obligations.size());
			}
			return obligations;
		}catch(EvaluationException e){
			if(log.isDebugEnabled()){
				log.debug("Failed to evaluate " +
						"decision rule obligations", e);
			}
			throw e;
		}catch(Exception e){
			if(log.isDebugEnabled()){
				log.debug("Failed to evaluate " +
						"decision rule obligations", e);
			}
			throw new EvaluationException(
					StatusCode.createProcessingError(),
					context, e);
		}
	}

	protected ToStringHelper toStringBuilder(Objects.ToStringHelper b){
		return b.add("id", id)
				.add("description", description)
				.add("target", target)
				.add("condition", condition)
				.add("adviceExp", adviceExpressions)
				.add("obligationExp", obligationExpressions);

	}

	protected abstract boolean isEvaluationContextValid(EvaluationContext context);


	protected abstract static class BaseDecisionRuleBuilder<T extends BaseDecisionRuleBuilder<?>>
	{
		protected String id;
		protected String description;
		protected Target target;
		protected Condition condition;
		protected ImmutableList.Builder<AdviceExpression> adviceExpressions = ImmutableList.builder();
		protected ImmutableList.Builder<ObligationExpression> obligationExpressions = ImmutableList.builder();

		protected BaseDecisionRuleBuilder(String ruleId){
			Preconditions.checkNotNull(ruleId,
					"Decision rule identifier can't be null");
			this.id = ruleId;
		}

		public T id(String id){
			Preconditions.checkNotNull(id,
					"Decision rule identifier can't be null");
			this.id = id;
			return getThis();
		}

		public T description(String desc){
			this.description = desc;
			return getThis();
		}

		public T target(Target target){
			this.target = target;
			return getThis();
		}

		public T condition(Expression predicate){
			Preconditions.checkNotNull(predicate);
			this.condition = new Condition(predicate);
			return getThis();
		}

		public T condition(Condition condition){
			this.condition = condition;
			return getThis();
		}

		public T withoutCondition(){
			this.condition = null;
			return getThis();
		}

		public T target(Target.Builder b){
			Preconditions.checkNotNull(b);
			this.target = b.build();
			return getThis();
		}

		public T withoutTarget(){
			this.target = null;
			return getThis();
		}



		public T withoutAdvices(){
			this.adviceExpressions = ImmutableList.builder();
			return getThis();
		}

		public T withoutObligations(){
			this.obligationExpressions = ImmutableList.builder();
			return getThis();
		}

		public T advice(AdviceExpression.Builder b){
			Preconditions.checkNotNull(b);
			this.adviceExpressions.add(b.build());
			return getThis();
		}

		public T advice(Iterable<AdviceExpression> advices){
			this.adviceExpressions.addAll(advices);
			return getThis();
		}

		public T advice(AdviceExpression ... advices){
			this.adviceExpressions.add(advices);
			return getThis();
		}

		public T obligation(ObligationExpression ... obligations){
			this.obligationExpressions.add(obligations);
			return getThis();
		}

		public T obligation(Iterable<ObligationExpression> obligations){
			Preconditions.checkNotNull(obligations);
			this.obligationExpressions.addAll(obligations);
			return getThis();
		}

		public T obligation(ObligationExpression.Builder b){
			Preconditions.checkNotNull(b);
			this.obligationExpressions.add(b.build());
			return getThis();
		}

		protected abstract T getThis();
	}
}
