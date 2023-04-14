package com.forms;

public class EndOfInputException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EndOfInputException(FormsArray form) {
		super();
		fa = new FormsArray();
		fa = form;
	}

	FormsArray fa = null;

	public FormsArray getForm() {
		return fa;
	}
}
