package com.semanticsquare.thrillio.util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBUtil {
	private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	private static final String DB_URL = "jdbc:mysql://localhost:3306/jid_thrillio?useSSL=false";

	private static final String USER = "root";
	private static final String PASS = "root998";

	public static Connection getDBConnection() {
		Connection conn = null;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}
}
