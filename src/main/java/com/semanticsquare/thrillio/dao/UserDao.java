package com.semanticsquare.thrillio.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.semanticsquare.thrillio.DataStore;
import com.semanticsquare.thrillio.constants.Gender;
import com.semanticsquare.thrillio.constants.UserType;
import com.semanticsquare.thrillio.entities.User;
import com.semanticsquare.thrillio.managers.UserManager;
import com.semanticsquare.thrillio.util.DBUtil;

public class UserDao {
	public List<User> getUsers() {
		return DataStore.getUsers();
	}

	public User getUser(long userId) {
		User user = null;
		try (Connection conn = DBUtil.getDBConnection(); Statement stmt = conn.createStatement();) {
			String query = "Select * from User where id = " + userId;
			ResultSet rs = stmt.executeQuery(query);

			while (rs.next()) {
				long id = rs.getLong("id");
				String email = rs.getString("email");
				String password = rs.getString("password");
				String firstName = rs.getString("first_name");
				String lastName = rs.getString("last_name");
				int gender_id = rs.getInt("gender_id");
				Gender gender = Gender.values()[gender_id];
				int user_type_id = rs.getInt("user_type_id");
				UserType userType = UserType.values()[user_type_id];

				user = UserManager.getInstance().createUser(id, email, password, firstName, lastName, gender, userType);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return user;

	}

	public long authenticate(String email, String password) {

		try (Connection conn = DBUtil.getDBConnection(); Statement stmt = conn.createStatement();) {
			String query = "Select id from User where email = '" + email + "' and password = '" + password + "'";
			System.out.println("query: " + query);
			ResultSet rs = stmt.executeQuery(query);

			while (rs.next()) {
				return rs.getLong("id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return -1;
	}
}
