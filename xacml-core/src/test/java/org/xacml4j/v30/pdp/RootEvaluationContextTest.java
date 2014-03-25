package org.xacml4j.v30.pdp;

import static org.easymock.EasyMock.createControl;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.xacml4j.v30.types.StringType.STRING;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;
import org.xacml4j.v30.AttributeCategories;
import org.xacml4j.v30.AttributeDesignatorKey;
import org.xacml4j.v30.EvaluationException;
import org.xacml4j.v30.spi.repository.PolicyReferenceResolver;
import org.xacml4j.v30.types.StringType;
import org.xacml4j.v30.types.XacmlTypes;


public class RootEvaluationContextTest
{
	private EvaluationContextHandler handler;
	private PolicyReferenceResolver resolver;
	private IMocksControl c;

	@Before
	public void init(){
		this.c = createControl();
		this.handler = c.createMock(EvaluationContextHandler.class);
		this.resolver = c.createMock(PolicyReferenceResolver.class);
	}

	@Test
	public void testSetAndGetDecisionCacheTTLWithDefaultTTLZero()
	{
		RootEvaluationContext context = new RootEvaluationContext(XacmlTypes.builder().defaultTypes().create(), false, 0, resolver, handler);
		c.replay();
		assertEquals(0, context.getDecisionCacheTTL());
		context.setDecisionCacheTTL(20);
		assertEquals(20, context.getDecisionCacheTTL());
		context.setDecisionCacheTTL(10);
		assertEquals(10, context.getDecisionCacheTTL());
		context.setDecisionCacheTTL(-1);
		assertEquals(0, context.getDecisionCacheTTL());
		context.setDecisionCacheTTL(10);
		assertEquals(0, context.getDecisionCacheTTL());
		c.verify();
	}

	@Test
	public void testSetAndGetDecisionCacheTTLWithDefaultTTL()
	{
		RootEvaluationContext context = new RootEvaluationContext(XacmlTypes.builder().defaultTypes().create(), false, 10, resolver, handler);
		c.replay();
		assertEquals(10, context.getDecisionCacheTTL());
		context.setDecisionCacheTTL(20);
		assertEquals(10, context.getDecisionCacheTTL());
		context.setDecisionCacheTTL(5);
		assertEquals(5, context.getDecisionCacheTTL());
		context.setDecisionCacheTTL(-1);
		assertEquals(0, context.getDecisionCacheTTL());
		context.setDecisionCacheTTL(10);
		assertEquals(0, context.getDecisionCacheTTL());
		c.verify();
	}

	@Test
	public void testResolveDesignatorValueValueIsInContext() throws EvaluationException
	{
		RootEvaluationContext context = new RootEvaluationContext(XacmlTypes.builder().defaultTypes().create(), false, 0, resolver, handler);
		c.replay();
		AttributeDesignatorKey k = AttributeDesignatorKey
				.builder()
				.category(AttributeCategories.SUBJECT_ACCESS)
				.attributeId("testId")
				.dataType(StringType.STRING)
				.issuer("test")
				.build();
		context.setResolvedDesignatorValue(k, StringType.STRING.create("aaa").toBag());
		assertEquals(StringType.STRING.create("aaa").toBag(), context.resolve(k));
		c.verify();
	}

	@Test
	public void testResolveDesignatorValueValueIsNotInContext() throws EvaluationException
	{
		RootEvaluationContext context = new RootEvaluationContext(XacmlTypes.builder().defaultTypes().create(), false, 0, resolver, handler);
		AttributeDesignatorKey k = AttributeDesignatorKey
				.builder()
				.category(AttributeCategories.SUBJECT_ACCESS)
				.attributeId("testId")
				.dataType(StringType.STRING)
				.issuer("test")
				.build();
		expect(handler.resolve(context, k)).andReturn(StringType.STRING.bag().attribute(STRING.create("aaa"), STRING.create("ccc")).build());

		c.replay();
		assertEquals(StringType.STRING.bagOf(STRING.create("aaa"), STRING.create("ccc")), context.resolve(k));
		assertEquals(StringType.STRING.bagOf(STRING.create("aaa"), STRING.create("ccc")), context.resolve(k));
		c.verify();
	}
}
