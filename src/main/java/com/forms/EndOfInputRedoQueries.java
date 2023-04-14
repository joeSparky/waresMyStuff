package com.forms;

public class EndOfInputRedoQueries extends Exception {
	private static final long serialVersionUID = 1L;

	public EndOfInputRedoQueries(FormsArray ret) {
		this.ret = ret;
	}
	public FormsArray ret = null;
}
