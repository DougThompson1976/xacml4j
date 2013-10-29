package org.xacml4j.v30.pdp;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.xacml4j.v30.AttributeAssignment;
import org.xacml4j.v30.AttributeCategories;
import org.xacml4j.v30.types.IntegerType;


public class AttributeAssignmentTest
{
	@Test
	public void testCreateAndEquals()
	{
		AttributeAssignment a0 =  AttributeAssignment.builder()
				.id("testId")
				.category(AttributeCategories.ACTION)
				.value(IntegerType.INTEGER.create(10))
				.build();
		assertEquals("testId", a0.getAttributeId());
		assertEquals(AttributeCategories.ACTION, a0.getCategory());
		assertEquals(IntegerType.INTEGER.create(10), a0.getAttribute());
		AttributeAssignment a1 =  AttributeAssignment.builder()
				.id("testId")
				.category(AttributeCategories.ACTION)
				.value(IntegerType.INTEGER.create(10))
				.build();
		assertEquals(a0, a1);
	}
}
