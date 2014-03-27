package org.xacml4j.v30.marshal.json;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Node;
import org.xacml4j.v30.Advice;
import org.xacml4j.v30.Attribute;
import org.xacml4j.v30.AttributeAssignment;
import org.xacml4j.v30.AttributeCategories;
import org.xacml4j.v30.Attributes;
import org.xacml4j.v30.Decision;
import org.xacml4j.v30.Entity;
import org.xacml4j.v30.Obligation;
import org.xacml4j.v30.ResponseContext;
import org.xacml4j.v30.Result;
import org.xacml4j.v30.Status;
import org.xacml4j.v30.StatusCode;
import org.xacml4j.v30.SubjectAttributes;
import org.xacml4j.v30.marshal.Marshaller;
import org.xacml4j.v30.marshal.Unmarshaller;
import org.xacml4j.v30.pdp.PolicyIDReference;
import org.xacml4j.v30.pdp.PolicySetIDReference;
import org.xacml4j.v30.types.StringExp;
import org.xml.sax.InputSource;

import com.google.common.collect.ImmutableList;

public class JsonResponseContextMarshallerTest {

	private Unmarshaller<ResponseContext> unmarshaller;
	private Marshaller<ResponseContext> marshaller;

	@Before
	public void setUp() {
		marshaller = new JsonResponseContextMarshaller();
		unmarshaller = new JsonResponseContextUnmarshaller();
	}

	@Test
	public void testMarshal() throws Exception {
		ResponseContext reqIn = createTestResponse();
		Object o = marshaller.marshal(reqIn);
		ResponseContext reqOut = unmarshaller.unmarshal(o);
		assertThat(reqOut, is(equalTo(reqIn)));
	}

	private ResponseContext createTestResponse() throws Exception {
		Result.Builder resultBuilder = Result
				.builder(Decision.PERMIT, new Status(StatusCode.createOk(), "alles kaput"));
		resultBuilder
				.obligation(Obligation
						.builder("obligation1")
						.attributes(
								ImmutableList.<AttributeAssignment> of(
										AttributeAssignment
												.builder()
										        .id(SubjectAttributes.SUBJECT_ID.toString())
												.category(AttributeCategories.ACTION)
												.issuer("Vytenai")
												.value(StringExp.valueOf("obuolys"))
												.build(),
										AttributeAssignment
												.builder()
												.id(SubjectAttributes.KEY_INFO.toString())
												.category(AttributeCategories.ACTION)
												.issuer("ispanija")
												.value(StringExp.valueOf("apelsinas"))
												.build()))
						.build());
		resultBuilder.obligation(Obligation
				.builder("obligation2")
				.attributes(
						ImmutableList.<AttributeAssignment> of(
								AttributeAssignment
										.builder()
										.id("custom:attribute1")
										.category(AttributeCategories.parse("totaly:made:up:attribute-category1"))
										.value(StringExp.valueOf("same old apelsinas"))
						                .build()))
				.build());
		resultBuilder.advice(ImmutableList.of(
				Advice.builder("advice1")
						.attributes(
								ImmutableList.<AttributeAssignment> of(
										AttributeAssignment
												.builder()
												.id("test:advice1")
												.value(StringExp.valueOf("nespjauk i sulini"))
												.build()))
						.build(),
				Advice.builder("advice2").build()));

		Attributes subjectAttributes = Attributes
				.builder(AttributeCategories.SUBJECT_ACCESS)
				.id("SubjectAttributes")
				.entity(Entity
						.builder()
						.content(sampleContent1())
						.attributes(
						ImmutableList.<Attribute> of(
								Attribute
										.builder(SubjectAttributes.SUBJECT_ID.toString())
										.includeInResult(false)
										.issuer("testIssuer")
										.value(StringExp.valueOf(
												"VFZTAQEAABRcZ03t-NNkK__rcIbvgKcK6e5oHBD5fD0qkdPIuqviWHzzFVR6AAAAgFl8GkUGZQG8TPXg9T6cQCoMO3a_sV1FR8pJC4BPfXfXlOvWDPUt4pr0cBkGTeaSU9RjSvEiXF-kTq5GFPkBHXcYnBW7eNjhq2EB_RWHh7_0sWqY32yb4fxlPLOsh5cUR4WbYZJE-zNuVzudco5cOjHU6Zwlr2HACpHW5siAVKfW"))
										.build(),
								Attribute.builder(SubjectAttributes.SUBJECT_ID_QUALIFIER.toString())
										.includeInResult(false).issuer("testIssuer")
										.value(StringExp.valueOf("TestDomain")).build())).build())
						.build();
		resultBuilder.includeInResultAttr(ImmutableList.<Attributes> of(subjectAttributes));

		resultBuilder.evaluatedPolicies(ImmutableList.<PolicyIDReference> of(PolicyIDReference.builder("policy1")
				.versionAsString("1.0").earliest("0.5").latest("1.5").build(), PolicyIDReference.builder("policy2").build()));
		resultBuilder.evaluatedPolicies(ImmutableList.<PolicySetIDReference> of(
				PolicySetIDReference.builder("policySet3").versionAsString("1.1").earliest("1.0").latest("1.9").build(),
				PolicySetIDReference.builder("policySet4").versionAsString("2.0").build()));

		return ResponseContext.builder().result(resultBuilder.build()).build();
	}

	private Node sampleContent1() throws Exception {
		DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

		return documentBuilder.parse(new InputSource(new StringReader(
				"<security>\n<through obscurity=\"true\"></through></security>")));
	}

}
