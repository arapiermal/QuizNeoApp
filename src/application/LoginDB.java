package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LoginDB {

	// Insert a new user
	public static User registerUser(String username, String firstName, String lastName, String email, String password, int userRole) {
		long userId = -1;
		String sql = "INSERT INTO users (username, first_name, last_name, email, password, user_role) VALUES (?, ?, ?, ?, ?, ?)";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			stmt.setString(1, username);
			stmt.setString(2, firstName);
			stmt.setString(3, lastName);
			stmt.setString(4, email);
			stmt.setString(5, password);
			stmt.setInt(6, userRole);

			int affectedRows = stmt.executeUpdate();
			if (affectedRows > 0) {
				try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						userId = generatedKeys.getLong(1);
					}
				}
			}

			Main.displayAlert("Successfully registered user", "username: " + username, "");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return new User(userId, username, firstName, lastName, email, password);
	}

	// Retrieve a user by username if password correct
	public static User loginUser(String username, String password) {
		username = username.toLowerCase();
		String sql = "SELECT * FROM users WHERE username = ?";
		User user = null;

		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				String rsPassword = rs.getString("password");
				if (!rsPassword.equals(password)) {
					Main.displayAlert("ERROR LOGGING IN", "Invalid Password", "The password you entered is wrong.");
					return null;
				}

				user = new User(rs.getLong("user_id"), rs.getInt("user_role"), rs.getString("username"),
						rs.getString("first_name"), rs.getString("last_name"), rs.getString("email"),
						rs.getString("password"));
			} else {
				Main.displayAlert("ERROR LOGGING IN", "Invalid Username", "The username you entered does not exist.");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return user;
	}

	// Update user details
	public static void updateUser(User user) {
		String sql = "UPDATE users SET first_name = ?, last_name = ?, email = ?, password = ? WHERE username = ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, user.getFirstName());
			stmt.setString(2, user.getLastName());
			stmt.setString(3, user.getEmail());
			stmt.setString(4, user.getPassword());
			stmt.setString(5, user.getUsername());
			stmt.executeUpdate();
			Main.displayAlert("User updated successfully!", "username: " + user.getUsername(), "");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Delete user by username
	public static void deleteUser(String username) {
		String sql = "DELETE FROM users WHERE username = ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, username);
			stmt.executeUpdate();
			Main.displayAlert("User deleted successfully!", "username: " + username, "");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static long getUserId(String username) {
		username = username.toLowerCase();
		String sql = "SELECT user_id FROM users WHERE username = ?";
		long userId = -1;
		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				userId = rs.getLong("user_id");
			} else {
				Main.displayAlert("user_id not found", "Invalid Username", "The username you entered does not exist.");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return userId;
	}
}
