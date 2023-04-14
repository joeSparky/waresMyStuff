package com.forms;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet ("/sure")
public class TestServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2558683951650634951L;
	
	@Override
	public void init() {
		
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doBoth(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doBoth(request, response);
	}
	
	void doBoth(HttpServletRequest request, HttpServletResponse response) {
		
	}

}
