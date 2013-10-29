package org.xacml4j.v30.pdp;

import org.xacml4j.v30.EvaluationContext;
import org.xacml4j.v30.EvaluationException;
import org.xacml4j.v30.StatusCode;




public class XPathEvaluationException extends EvaluationException
{
	private static final long serialVersionUID = 1511624494955246280L;

	private String xpathExpression;

	public XPathEvaluationException(String xpathExpression,
			EvaluationContext context,
			String template, Object... arguments) {
		super(StatusCode.createProcessingError(),
				context, template, arguments);
		this.xpathExpression = xpathExpression;
	}

	public XPathEvaluationException(String xpathExpression,
			EvaluationContext context,
			Throwable cause, String message, Object... arguments) {
		super(StatusCode.createProcessingError(),
				context, cause, message, arguments);
		this.xpathExpression = xpathExpression;
	}

	public XPathEvaluationException(String xpathExpression,
			EvaluationContext context,
			Throwable cause) {
		super(StatusCode.createProcessingError(),
				context, cause);
		this.xpathExpression = xpathExpression;
	}

	public String getXPathExpression(){
		return xpathExpression;
	}
}
