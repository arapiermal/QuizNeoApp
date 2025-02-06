package application;

import java.util.regex.Pattern;

public class User {
	public enum Role {
		REGULAR_USER(0, ""), STUDENT(1, "Student"), PROFESSOR(2, "Professor"), ADMIN(3, "Admin");

		private final int level;
		private final String title;

		Role(int level, String title) {
			this.level = level;
			this.title = title;
		}

		public int getLevel() {
			return level;
		}

		@Override
		public String toString() {
			return title;
		}
	}

	private final long userId;
	private final int userRole;
	private final String username;
	private String firstName;
	private String lastName;
	private String email;
	private String password;

	public User(long userId, String username, String firstName, String lastName, String email, String password) {
		this.userId = userId;
		this.userRole = 0;
		this.username = username.toLowerCase();
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
	}

	public User(long userId, int userRole, String username, String firstName, String lastName, String email,
			String password) {
		this.userId = userId;
		this.userRole = userRole;
		this.username = username.toLowerCase();
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
	}

	@Override
	public String toString() {
		return firstName + " " + lastName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		if (isEmailValid(email))
			this.email = email;
	}

	public long getUserId() {
		return userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public static boolean isEmailValid(String email) {
		final String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@"
				+ "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
		Pattern p = Pattern.compile(emailRegex);
		return email != null && p.matcher(email).matches();
	}

	public boolean hasEriCryptAccess() {
		return userRole > 1;
	}

	public Role getRole() {
		int n = Role.values().length;
		if (userRole <= 0)
			return Role.values()[0];
		else if (userRole < n)
			return Role.values()[userRole];
		else
			return Role.values()[n - 1];
	}

	public String toStringLong() {
		return getRole() + " " + toString();
	}

	public static boolean isEducationalEmail(String email) {
		return Pattern.compile("\\.edu(\\.[a-zA-Z]+)?$", Pattern.CASE_INSENSITIVE).matcher(email).find();
	}
}
