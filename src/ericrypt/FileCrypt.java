package ericrypt;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class FileCrypt {

	public static void encryptFile(String filePath) {
		try {
			Path path = Paths.get(filePath);
			String key = path.getFileName().toString(); // First encryption key
			System.out.println(key);
			List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

			if (lines.isEmpty()) {
				System.out.println("File is empty!");
				return;
			}
			String quizName = lines.get(0); // Second encryption key
			// Encrypt the rest of the lines using the second key
			StringBuilder sb = new StringBuilder();
			sb.append(EriCrypt.encrypt(quizName, key)).append("\n");
			for (int i = 1; i < lines.size(); i++) {
				sb.append(EriCrypt.encrypt(lines.get(i), quizName)).append("\n");
			}

			Files.write(Paths.get(filePath + ".erixam"), sb.toString().getBytes(StandardCharsets.UTF_8));
			System.out.println("File encrypted as " + filePath + ".erixam");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void decryptFile(String filePath) {
		try {
			Path path = Paths.get(filePath);
			String tempFilename = path.getFileName().toString();
			String originalFilename;
			if (tempFilename.endsWith(".erixam")) {
				originalFilename = tempFilename.substring(0, tempFilename.length() - ".erixam".length());
			} else {
				originalFilename = tempFilename;
			}
			List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

			if (lines.isEmpty()) {
				System.out.println("File is empty!");
				return;
			}

			String firstKey = originalFilename;
			String secondKey = EriCrypt.decrypt(lines.get(0), firstKey);
			System.out.println(secondKey);
			StringBuilder sb = new StringBuilder(secondKey).append("\n");
			for (int i = 1; i < lines.size(); i++) {
				System.out.println(lines.get(i));
				sb.append(EriCrypt.decrypt(lines.get(i), secondKey)).append("\n");
			}

			String decryptedFilePath = path.getParent().toString() + "\\decrypted_" + originalFilename;
			Files.write(Paths.get(decryptedFilePath), sb.toString().getBytes(StandardCharsets.UTF_8));
			System.out.println("File decrypted as " + decryptedFilePath);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String decryptFileString(File file) {
		if(file == null || !file.exists() || !file.canRead())
			return "";
		try {
			String tempFilename = file.getName().toString();
			String originalFilename;
			if (tempFilename.endsWith(".erixam")) {
				originalFilename = tempFilename.substring(0, tempFilename.length() - ".erixam".length());
			} else {
				originalFilename = tempFilename;
			}
			List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);

			if (lines.isEmpty()) {
				System.out.println("File is empty!");
				return "";
			}
			String firstKey = originalFilename;
			String secondKey = EriCrypt.decrypt(lines.get(0), firstKey);
			System.out.println(secondKey);
			StringBuilder sb = new StringBuilder(secondKey).append("\n");
			for (int i = 1; i < lines.size(); i++) {
				System.out.println(lines.get(i));
				sb.append(EriCrypt.decrypt(lines.get(i), secondKey)).append("\n");
			}
			return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}
}
