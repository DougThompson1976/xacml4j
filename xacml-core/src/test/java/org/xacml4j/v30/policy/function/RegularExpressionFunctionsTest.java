package org.xacml4j.v30.policy.function;

import static org.easymock.EasyMock.createControl;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;
import org.xacml4j.v30.EvaluationContext;
import org.xacml4j.v30.EvaluationException;
import org.xacml4j.v30.spi.function.AnnotiationBasedFunctionProvider;
import org.xacml4j.v30.spi.function.FunctionProvider;
import org.xacml4j.v30.types.AnyURIExp;
import org.xacml4j.v30.types.BooleanExp;
import org.xacml4j.v30.types.RFC822NameExp;
import org.xacml4j.v30.types.StringExp;


public class RegularExpressionFunctionsTest
{
	private IMocksControl c;
	private EvaluationContext context;
	
	@Before
	public void init(){
		c = createControl();
		this.context = c.createMock(EvaluationContext.class);
	}
	
	@Test
	public void testFunctionIfImplemented() throws Exception
	{	
		FunctionProvider f = new AnnotiationBasedFunctionProvider(
				RegularExpressionFunctions.class);
		assertNotNull(f.getFunction("urn:oasis:names:tc:xacml:1.0:function:string-regexp-match"));
		assertNotNull(f.getFunction("urn:oasis:names:tc:xacml:1.0:function:anyURI-regexp-match"));
		assertNotNull(f.getFunction("urn:oasis:names:tc:xacml:1.0:function:ipAddress-regexp-match"));
		assertNotNull(f.getFunction("urn:oasis:names:tc:xacml:1.0:function:dnsName-regexp-match"));
		assertNotNull(f.getFunction("urn:oasis:names:tc:xacml:1.0:function:rfc822Name-regexp-match"));
		assertNotNull(f.getFunction("urn:oasis:names:tc:xacml:1.0:function:x500Name-regexp-match"));

	}

	@Test
	public void testXacmlRegExptoJERegExpWithCharacterSubstraction()
	{
		assertEquals(".*[0-9]{3}-[0-9]{3}-[0-9]{4}.*", RegularExpressionFunctions.covertXacmlToJavaSyntax("[0-9]{3}-[0-9]{3}-[0-9]{4}"));
	}

	@Test
	public void testXacmlRegExpWithSpaceBugTrimming()
	{
		StringExp regexp1 = StringExp.valueOf("   This  is n*o*t* *IT!  ");
		StringExp regexp2 = StringExp.valueOf("  *This .*is not IT! *");
		StringExp input1 = StringExp.valueOf("   This  is not IT!  ");
		c.replay();
		assertEquals(BooleanExp.valueOf(true), RegularExpressionFunctions.stringRegexpMatch(context, regexp1, input1));
		c.verify();
		c.reset();
		c.replay();
		assertEquals(BooleanExp.valueOf(true), RegularExpressionFunctions.stringRegexpMatch(context, regexp2, input1));
		c.verify();
	}

	@Test
	public void testRegExpMatchFromIIC168ConformanceTest()
	{
		StringExp regexp1 = StringExp.valueOf("   This  is n*o*t* *IT!  ");
		StringExp input1 = StringExp.valueOf("   This  is IT!  ");
		StringExp input2 = StringExp.valueOf("   This  is not IT!  ");
		c.replay();
		assertEquals(BooleanExp.valueOf(true), RegularExpressionFunctions.stringRegexpMatch(context, regexp1, input1));
		c.verify();
		c.reset();
		c.replay();
		assertEquals(BooleanExp.valueOf(true), RegularExpressionFunctions.stringRegexpMatch(context, regexp1, input2));
		c.verify();
	}

	@Test
	public void testStringRegExpMatch() throws EvaluationException
	{
		StringExp regexp = StringExp.valueOf("G*,Trumpickas");
		StringExp input = StringExp.valueOf("Giedrius,Trumpickas");
		c.replay();
		assertEquals(BooleanExp.valueOf(true), RegularExpressionFunctions.stringRegexpMatch(context, regexp, input));
		c.verify();
	}

	@Test
	public void testAnyURIRegExpMatch() throws EvaluationException
	{
		StringExp regexp = StringExp.valueOf("http://www.test.org/public/*");
		AnyURIExp input = AnyURIExp.valueOf("http://www.test.org/public/test/a");
		c.replay();
		assertEquals(BooleanExp.valueOf(true), RegularExpressionFunctions.anyURIRegexpMatch(context, regexp, input));
		c.verify();
		
	}

	@Test
	public void testrfc822NameRegExpMatch() throws EvaluationException
	{
		StringExp regexp = StringExp.valueOf(".*@comcast.net");
		RFC822NameExp input = RFC822NameExp.valueOf("trumpyla@comcast.net");
		c.replay();
		assertEquals(BooleanExp.valueOf(true), RegularExpressionFunctions.rfc822NameRegexpMatch(context, regexp, input));
		c.verify();
		
	}



}
