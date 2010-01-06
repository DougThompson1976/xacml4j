package com.artagon.xacml.policy.type;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.artagon.xacml.policy.Attribute;
import com.artagon.xacml.policy.type.IPAddressType;
import com.artagon.xacml.policy.type.IPAddressTypeImpl;
import com.artagon.xacml.policy.type.PortRange;
import com.artagon.xacml.util.IPAddressUtils;

public class IPAddressTypeTest 
{
	private IPAddressType t;
	
	@Before
	public void test(){
		this.t = new IPAddressTypeImpl();
	}
	
	@Test
	public void testToXacmlStringIPV4()
	{
		Attribute a0 = t.create(IPAddressUtils.parseAddress("127.0.0.1"));
		assertEquals("127.0.0.1", a0.toString());
		Attribute a1 = t.create(IPAddressUtils.parseAddress("127.0.0.1"), 
				IPAddressUtils.parseAddress("255.255.255.0"));
		assertEquals("127.0.0.1/255.255.255.0", a1.toString());
		Attribute a2 = t.create(IPAddressUtils.parseAddress("127.0.0.1"), 
				IPAddressUtils.parseAddress("255.255.255.0"), PortRange.getRange(1024, 2048));
		assertEquals("127.0.0.1/255.255.255.0:1024-2048", a2.toString());
	}
	
	@Test
	public void testToXacmlStringIPV6()
	{
		Attribute a0 = t.create(IPAddressUtils.parseAddress("2001:0db8:85a3:0000:0000:8a2e:0370:7334"));
		assertEquals("[2001:db8:85a3:0:0:8a2e:370:7334]", a0.toString());
		Attribute a1 = t.create(IPAddressUtils.parseAddress("2001:0db8:85a3:0000:0000:8a2e:0370:7334"), 
				IPAddressUtils.parseAddress("255.255.255.0"));
		assertEquals("127.0.0.1/255.255.255.0", a1.toString());
	}
}
