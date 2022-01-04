package com.semanticsquare.thrillio.controllers;

import java.io.IOException;
import java.util.Collection;

import com.semanticsquare.thrillio.constants.KidFriendlyStatus;
import com.semanticsquare.thrillio.entities.Bookmark;
import com.semanticsquare.thrillio.entities.User;
import com.semanticsquare.thrillio.managers.BookmarkManager;
import com.semanticsquare.thrillio.managers.UserManager;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = { "/", "/bookmark", "/bookmark/save", "/bookmark/mybooks" })
public class BookmarkController extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 314868454387879997L;

	public BookmarkController() {

	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		RequestDispatcher dispatcher = null;

		System.out.println("Servlet path: " + request.getServletPath());

		if (request.getSession().getAttribute("userId") != null) {

			long userId = (long) request.getSession().getAttribute("userId");

			if (request.getServletPath().contains("save")) {
				// save
				dispatcher = request.getRequestDispatcher("/mybooks.jsp");

				String bid = request.getParameter("bid");

				User user = UserManager.getInstance().getUser(userId);

				Bookmark book = BookmarkManager.getInstance().getBook(Long.parseLong(bid));

				BookmarkManager.getInstance().saveUserBookmark(user, book);

				Collection<Bookmark> list = BookmarkManager.getInstance().getBooks(true, userId);

				request.setAttribute("books", list);

			} else if (request.getServletPath().contains("mybooks")) {
				// mybooks

				dispatcher = request.getRequestDispatcher("/mybooks.jsp");

				Collection<Bookmark> list = BookmarkManager.getInstance().getBooks(true, userId);

				request.setAttribute("books", list);

			} else {
				// browse
				dispatcher = request.getRequestDispatcher("/browse.jsp");

				Collection<Bookmark> list = BookmarkManager.getInstance().getBooks(false, userId);

				request.setAttribute("books", list);
			}

		} else {

			dispatcher = request.getRequestDispatcher("/login.jsp");
		}

		dispatcher.forward(request, response);

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);

	}

	public void saveUserBookmark(User user, Bookmark bookmark) {
		BookmarkManager.getInstance().saveUserBookmark(user, bookmark);
	}

	public void setKidFriendlyStatusDecision(User user, KidFriendlyStatus kidFriendlyStatus, Bookmark bookmark) {
		BookmarkManager.getInstance().setKidFriendlyStatusDecision(user, kidFriendlyStatus, bookmark);
	}

	public void share(User user, Bookmark bookmark) {
		BookmarkManager.getInstance().share(user, bookmark);

	}
}
