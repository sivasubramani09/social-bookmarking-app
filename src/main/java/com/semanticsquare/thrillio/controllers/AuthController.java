package com.semanticsquare.thrillio.controllers;

import java.io.IOException;

import com.semanticsquare.thrillio.managers.UserManager;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet implementation class AuthController
 */
@WebServlet(urlPatterns = { "/auth", "/auth/logout" })
public class AuthController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public AuthController() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		System.out.println("Servlet path: " + request.getServletPath());

		if (!request.getServletPath().contains("logout")) {
			String email = request.getParameter("email");
			String password = request.getParameter("password");

			long userId = UserManager.getInstance().authenticate(email, password);

			if (userId != -1) {

				HttpSession session = request.getSession();
				session.setAttribute("userId", userId);

				request.getRequestDispatcher("bookmark/mybooks").forward(request, response);
			} else {
				request.getRequestDispatcher("/login.jsp").forward(request, response);
			}

		} else {
			request.getSession().invalidate();
			request.getRequestDispatcher("/login.jsp").forward(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
