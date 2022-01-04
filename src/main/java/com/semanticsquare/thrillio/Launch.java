package com.semanticsquare.thrillio;

import java.util.List;

import com.semanticsquare.thrillio.bgjobs.WebPageDownloaderTask;
import com.semanticsquare.thrillio.entities.Bookmark;
import com.semanticsquare.thrillio.entities.User;
import com.semanticsquare.thrillio.managers.BookmarkManager;
import com.semanticsquare.thrillio.managers.UserManager;

public class Launch {

	private static List<User> users;
	private static List<List<Bookmark>> bookmarks;

	private static void printUserData() {
		for (User user : users) {
			System.out.println(user);
		}
	}

	private static void loadData() {
		System.out.println("1. Loading data...");
		DataStore.loadData();
		users = UserManager.getInstance().getUsers();
		bookmarks = BookmarkManager.getInstance().getBookmarks();
		printUserData();
		printBookmarkData();

	}

	private static void printBookmarkData() {
		for (List<Bookmark> bookmarkList : bookmarks) {
			for (Bookmark bookmark : bookmarkList) {
				System.out.println(bookmark);
			}
		}
	}

	public static void main(String[] args) {
		loadData();
//		runDownloaderJob(); //background jobs
	}

	private static void runDownloaderJob() {
		WebPageDownloaderTask Task = new WebPageDownloaderTask(true);
		(new Thread(Task)).start();
	}

}
