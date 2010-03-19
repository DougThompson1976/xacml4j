package com.artagon.xacml.v3;


public class XacmlException extends Exception
{
	private static final long serialVersionUID = -546790992581476011L;

	protected XacmlException(Throwable cause){
		super(cause);
	}
	
	/**
	 * Constructs exception with a given message.
	 * 
	 * @param template a template {@see String#format(String, Object...)}
	 * @param arguments an arguments for template
	 */
	protected XacmlException(
			String template, Object ... arguments){
		super(String.format(template, arguments));
	}
	
	/**
	 * Constructs exception with a given status and message.
	 * 
	 * @param cause a root cause of this exception
	 * @param template a template {@see String#format(String, Object...)}
	 * @param arguments an arguments for template
	 */
	protected XacmlException(Throwable cause, String message, Object ... arguments){
		super(String.format(message, arguments), cause);
	}

	
}

