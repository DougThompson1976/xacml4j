package com.artagon.xacml.v3.policy;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;

import java.util.Collection;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import com.artagon.xacml.v3.policy.type.DataTypes;
import static com.artagon.xacml.v3.policy.type.DataTypes.INTEGER;
import com.artagon.xacml.v3.policy.type.IntegerType.IntegerValue;
import com.artagon.xacml.v3.policy.type.StringType;

public class BagOfAttributesTest extends XacmlPolicyTestCase
{
	private StringType stringType;
	
	@Before
	public void init(){
		this.stringType = DataTypes.STRING.getType();
	}
	
	@Test
	public void testContains() throws Exception
	{
		BagOfAttributeValuesType<StringType.StringValue> bagType = stringType.bagOf();
		Collection<AttributeValue> content = new LinkedList<AttributeValue>();
		content.add(stringType.create("1"));
		content.add(stringType.create("2"));
		content.add(stringType.create("3"));	
		BagOfAttributeValues<StringType.StringValue> bag = bagType.create(content);
		assertTrue(bag.contains(stringType.create("1")));
		assertTrue(bag.contains(stringType.create("2")));
		assertTrue(bag.contains(stringType.create("3")));
		assertFalse(bag.contains(stringType.create("4")));
	}
	
	@Test
	public void testEqualsEmptyBags()
	{
		assertEquals(DataTypes.STRING.emptyBag(), DataTypes.STRING.emptyBag());
		assertEquals(DataTypes.STRING.emptyBag(), DataTypes.STRING.emptyBag());
	}
	
	@Test
	public void testContainsAll() throws Exception
	{
		Collection<AttributeValue> content = new LinkedList<AttributeValue>();
		content.add(INTEGER.create(1l));
		content.add(INTEGER.create(2l));
		content.add(INTEGER.create(1l));	
		BagOfAttributeValues<?> bag = INTEGER.bag(content);
		Collection<AttributeValue> test = new LinkedList<AttributeValue>();
		test.add(INTEGER.create(1l));
		test.add(INTEGER.create(2l));
		assertTrue(bag.containsAll(INTEGER.bag(test)));		
		test = new LinkedList<AttributeValue>();
		test.add(INTEGER.create(1l));
		test.add(INTEGER.create(3l));
		assertFalse(bag.containsAll(INTEGER.bag(test)));
	}
	
	@Test
	public void testEquals()
	{
		Collection<AttributeValue> content1 = new LinkedList<AttributeValue>();
		content1.add(INTEGER.create(1l));
		content1.add(INTEGER.create(2l));
		content1.add(INTEGER.create(3l));
		BagOfAttributeValues<?> bag1 = INTEGER.bag(content1);
		BagOfAttributeValues<?> bag2 = INTEGER.bag(content1);
		assertEquals(bag1, bag2);
	}
	
	@Test
	public void testJoin() throws Exception
	{
		Collection<AttributeValue> content1 = new LinkedList<AttributeValue>();
		content1.add(INTEGER.create(1l));
		content1.add(INTEGER.create(2l));
		content1.add(INTEGER.create(3l));
		BagOfAttributeValues<?> bag1 = INTEGER.bag(content1);
		Collection<AttributeValue> content2 = new LinkedList<AttributeValue>();
		content2.add(INTEGER.create(3l));
		content2.add(INTEGER.create(4l));
		content2.add(INTEGER.create(5l));
		BagOfAttributeValues<?> bag2 = INTEGER.bag(content2);
		BagOfAttributeValues<?> bag3 = bag1.join(bag2);
		BagOfAttributeValues<?> bag4 = bag2.join(bag1);
		assertEquals(bag3, bag4);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testCreateWithDifferentAttributeTypes()
	{
		Collection<AttributeValue> attr = new LinkedList<AttributeValue>();
		attr.add(INTEGER.create(1l));
		attr.add(stringType.create("aaa"));
		INTEGER.bag(attr);
	}
	
	@Test
	public void testEvaluateBag() throws EvaluationException
	{
		Collection<AttributeValue> content2 = new LinkedList<AttributeValue>();
		content2.add(INTEGER.create(3l));
		content2.add(INTEGER.create(4l));
		content2.add(INTEGER.create(5l));
		BagOfAttributeValues<?> bag2 = INTEGER.bag(content2);
		assertSame(bag2, bag2.evaluate(context));	
	}
	
	@Test
	public void testUnion()
	{
		BagOfAttributeValues<IntegerValue> bag0 = INTEGER.bag(
				INTEGER.create(1),
				INTEGER.create(2),
				INTEGER.create(3),
				INTEGER.create(6));
		
		BagOfAttributeValues<IntegerValue> bag1 = INTEGER.bag(
				INTEGER.create(2),
				INTEGER.create(2),
				INTEGER.create(7),
				INTEGER.create(6));
		
		BagOfAttributeValues<IntegerValue> bag3 = bag0.union(bag1);
		
		assertTrue(bag3.contains(INTEGER.create(2)));
		assertTrue(bag3.contains(INTEGER.create(7)));
		assertTrue(bag3.contains(INTEGER.create(6)));
		assertTrue(bag3.contains(INTEGER.create(1)));
		assertTrue(bag3.contains(INTEGER.create(3)));
		assertEquals(5, bag3.size());
		
	}
	
	@Test
	public void testIntersection()
	{
		BagOfAttributeValues<IntegerValue> bag0 = INTEGER.bag(
				DataTypes.INTEGER.create(1),
				DataTypes.INTEGER.create(2),
				DataTypes.INTEGER.create(3),
				DataTypes.INTEGER.create(6));
		
		BagOfAttributeValues<IntegerValue> bag1 = INTEGER.bag(
				INTEGER.create(2),
				INTEGER.create(2),
				INTEGER.create(7),
				INTEGER.create(6));
		
		BagOfAttributeValues<IntegerValue> bag3 = bag0.intersection(bag1);
		
		assertTrue(bag3.contains(INTEGER.create(2)));
		assertTrue(bag3.contains(INTEGER.create(6)));
		assertEquals(2, bag3.size());
		
	}
}
