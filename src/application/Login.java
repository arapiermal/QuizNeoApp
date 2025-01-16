package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Login {
	private static Login loginData;

	private List<User> users;

	public Login() {
		this.users = loadUsers();

	}

	public Login(List<User> users) {
		this.users = users;
	}

	public User loginUser(String email) {
		for (User u : users) {
			if (email.equalsIgnoreCase(u.getEmail()))
				return u;
		}
		return null;
	}

	public User registerUser(String firstName, String lastName, String email) {
		if (!existsUser(email)) {
			User u = new User(firstName, lastName, email);
			users.add(u);
			saveUsers(); // This overwrites, possible to append
			return u;
		}
		return null;
	}

	public boolean existsUser(String email) {
		for (User u : users) {
			if (email.equalsIgnoreCase(u.getEmail()))
				return true;
		}
		return false;
	}

	// Singleton
	public static Login getLoginData() {
		if (loginData == null)
			loginData = new Login(); // or load
		return loginData;
	}

	public static List<User> loadUsers() {
		List<User> list = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader("resources/users.txt"))) {
			String line;
			while ((line = br.readLine()) != null) {
				if (line.isBlank())
					continue;
				String[] params = line.trim().split("\\s*,\\s*");
				if (params.length >= 3) {
					list.add(new User(params[0], params[1], params[2]));
				}
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return list;
	}

	public void saveUsers() {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter("resources/users.txt"))) {
			for (User user : users) {
				bw.write(user.getFirstName() + "," + user.getLastName() + "," + user.getEmail());
				bw.newLine();
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

}
