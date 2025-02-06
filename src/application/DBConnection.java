package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
	private static final String URL = "jdbc:mysql://localhost:3306/erixam";
	private static final String USER = "root";
	private static final String PASSWORD = "rrenje";

	public static Connection getConnection() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			return DriverManager.getConnection(URL, USER, PASSWORD);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("MySQL driver not found");
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Error connecting to the database");
		}
	}
}
