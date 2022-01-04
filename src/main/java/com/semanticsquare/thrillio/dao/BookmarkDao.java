package com.semanticsquare.thrillio.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.semanticsquare.thrillio.DataStore;
import com.semanticsquare.thrillio.constants.BookGenre;
import com.semanticsquare.thrillio.entities.Book;
import com.semanticsquare.thrillio.entities.Bookmark;
import com.semanticsquare.thrillio.entities.Movie;
import com.semanticsquare.thrillio.entities.UserBookmark;
import com.semanticsquare.thrillio.entities.WebLink;
import com.semanticsquare.thrillio.managers.BookmarkManager;
import com.semanticsquare.thrillio.util.DBUtil;

public class BookmarkDao {
	public List<List<Bookmark>> getBookmarks() {
		return DataStore.getBookmarks();
	}

	public void saveUserBookmark(UserBookmark userBookmark) {

		try (Connection conn = DBUtil.getDBConnection(); Statement stmt = conn.createStatement();) {
			if (userBookmark.getBookmark() instanceof Book) {
				saveUserBook(userBookmark, stmt);
			} else if (userBookmark.getBookmark() instanceof Movie) {
				saveUserMovie(userBookmark, stmt);
			} else {
				saveUserWebLink(userBookmark, stmt);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void saveUserWebLink(UserBookmark userBookmark, Statement stmt) throws SQLException {
		String query = "INSERT INTO user_weblink (user_id, weblink_id) values( " + userBookmark.getUser().getId() + ","
				+ userBookmark.getBookmark().getId() + ")";

		stmt.executeUpdate(query);
	}

	private void saveUserMovie(UserBookmark userBookmark, Statement stmt) throws SQLException {
		String query = "INSERT INTO user_movie (user_id, movie_id) values( " + userBookmark.getUser().getId() + ","
				+ userBookmark.getBookmark().getId() + ")";

		stmt.executeUpdate(query);
	}

	private void saveUserBook(UserBookmark userBookmark, Statement stmt) throws SQLException {
		String query = "INSERT INTO user_book (user_id, book_id) values( " + userBookmark.getUser().getId() + ","
				+ userBookmark.getBookmark().getId() + ")";

		stmt.executeUpdate(query);
	}

	public List<WebLink> getAllWebLinks() {
		List<WebLink> result = new ArrayList<>();
		List<List<Bookmark>> bookmarks = DataStore.getBookmarks();
		List<Bookmark> allWebLinks = bookmarks.get(0);
		for (Bookmark bookmark : allWebLinks) {
			result.add((WebLink) bookmark);
		}

		return result;
	}

	public List<WebLink> getWebLinks(WebLink.DownloadStatus downloadStatus) {
		List<WebLink> result = new ArrayList<>();

		List<WebLink> allWebLinks = getAllWebLinks();
		for (WebLink weblink : allWebLinks) {
			if (weblink.getDownloadStatus().equals(downloadStatus)) {
				result.add(weblink);
			}
		}

		return result;
	}

	public void updateKidFriendlyStatus(Bookmark bookmark) {
		int kidFriendlyStatus = bookmark.getKidFriendlyStatus().ordinal();
		long userID = bookmark.getKidFriendlyMarkedBy().getId();
		String tableToUpdate = "book";

		if (bookmark instanceof Movie) {
			tableToUpdate = "movie";
		} else if (bookmark instanceof WebLink) {
			tableToUpdate = "weblink";
		}
		try (Connection conn = DBUtil.getDBConnection(); Statement stmt = conn.createStatement();) {

			String query = "update " + tableToUpdate + " set kid_friendly_status = " + kidFriendlyStatus
					+ " ,kid_friendly_marked_by = " + userID + " WHERE id = " + bookmark.getId();

			System.out.println("query(updateKidFriendlyStatus): " + query);
			stmt.executeUpdate(query);

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void updateSharedByInfo(Bookmark bookmark) {
		long userID = bookmark.getSharedBy().getId();
		String tableToUpdate = "book";

		if (bookmark instanceof WebLink) {
			tableToUpdate = "weblink";
		}

		try (Connection conn = DBUtil.getDBConnection(); Statement stmt = conn.createStatement();) {
			String query = "update " + tableToUpdate + " set shared_by = " + userID + " WHERE id = " + bookmark.getId();
			System.out.println("query(updateSharedByInfo): " + query);
			stmt.executeUpdate(query);

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public Collection<Bookmark> getBooks(boolean isBookmarked, long userId) {

		Collection<Bookmark> result = new ArrayList<>();

		try (Connection conn = DBUtil.getDBConnection(); Statement stmt = conn.createStatement();) {

			String query = "";
			if (!isBookmarked) {
				query = "Select b.id, title, image_url, publication_year, GROUP_CONCAT(a.name SEPARATOR ',') AS authors, book_genre_id, "
						+ "amazon_rating from Book b, Author a, Book_Author ba where b.id = ba.book_id and ba.author_id = a.id and "
						+ "b.id NOT IN (select ub.book_id from User u, User_Book ub where u.id = " + userId
						+ " and u.id = ub.user_id) group by b.id";
			} else {
				query = "Select b.id, title, image_url, publication_year, GROUP_CONCAT(a.name SEPARATOR ',') AS authors, book_genre_id, "
						+ "amazon_rating from Book b, Author a, Book_Author ba where b.id = ba.book_id and ba.author_id = a.id and "
						+ "b.id IN (select ub.book_id from User u, User_Book ub where u.id = " + userId
						+ " and u.id = ub.user_id) group by b.id";
			}

			ResultSet rs = stmt.executeQuery(query);

			while (rs.next()) {
				long id = rs.getLong("id");
				String title = rs.getString("title");
				String imageUrl = rs.getString("image_url");
				int publicationYear = rs.getInt("publication_year");
				// String publisher = rs.getString("name");
				String[] authors = rs.getString("authors").split(",");
				int genre_id = rs.getInt("book_genre_id");
				BookGenre genre = BookGenre.values()[genre_id];
				double amazonRating = rs.getDouble("amazon_rating");

				System.out.println(
						"id: " + id + ", title: " + title + ", publication year: " + publicationYear + ", authors: "
								+ String.join(", ", authors) + ", genre: " + genre + ", amazonRating: " + amazonRating);

				Bookmark bookmark = BookmarkManager.getInstance().createBook(id, title, imageUrl, publicationYear, null,
						authors, genre, amazonRating);
				result.add(bookmark);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	public Bookmark getBook(long bookId) {
		Book book = null;

		try (Connection conn = DBUtil.getDBConnection(); Statement stmt = conn.createStatement();) {

			String query = "Select b.id, title, image_url, publication_year, p.name, GROUP_CONCAT(a.name SEPARATOR ',') AS authors, book_genre_id, amazon_rating, created_date"
					+ " from Book b, Publisher p, Author a, Book_Author ba " + "where b.id = " + bookId
					+ " and b.publisher_id = p.id and b.id = ba.book_id and ba.author_id = a.id group by b.id";
			ResultSet rs = stmt.executeQuery(query);

			while (rs.next()) {
				long id = rs.getLong("id");
				String title = rs.getString("title");
				String imageUrl = rs.getString("image_url");
				int publicationYear = rs.getInt("publication_year");
				String publisher = rs.getString("name");
				String[] authors = rs.getString("authors").split(",");
				int genre_id = rs.getInt("book_genre_id");
				BookGenre genre = BookGenre.values()[genre_id];
				double amazonRating = rs.getDouble("amazon_rating");

				System.out.println("id: " + id + ", title: " + title + ", publication year: " + publicationYear
						+ ", publisher: " + publisher + ", authors: " + String.join(", ", authors) + ", genre: " + genre
						+ ", amazonRating: " + amazonRating);

				book = BookmarkManager.getInstance().createBook(id, title, imageUrl, publicationYear, publisher,
						authors, genre, amazonRating);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return book;
	}
}
