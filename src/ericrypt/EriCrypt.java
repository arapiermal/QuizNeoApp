package ericrypt;

import java.util.Base64;

public class EriCrypt {
	// Encrypt using XOR, then encode with Base64
    public static String encrypt(String text, String key) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            char k = key.charAt(i % key.length()); // Repeat key if needed
            sb.append((char) (c ^ k)); // XOR encryption
        }
        return Base64.getEncoder().encodeToString(sb.toString().getBytes()); // Convert to Base64
    }

    // Decrypt by decoding Base64 first, then applying XOR
    public static String decrypt(String text, String key) {
        byte[] decodedBytes = Base64.getDecoder().decode(text); // Decode Base64
        String decodedText = new String(decodedBytes);
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < decodedText.length(); i++) {
            char c = decodedText.charAt(i);
            char k = key.charAt(i % key.length());
            sb.append((char) (c ^ k)); // Reverse XOR
        }
        return sb.toString();
    }

	public static void main(String[] args) {
		String key = "simpleKey"; // Key for encryption
		String original = "Matematike ckemi si je";

		String encrypted = encrypt(original, key);
		System.out.println("Encrypted: " + encrypted);

		String decrypted = decrypt(encrypted, key);
		System.out.println("Decrypted: " + decrypted);

		FileCrypt.encryptFile("resources/quizzes/math.txt");

		FileCrypt.decryptFile("resources/quizzes/math.txt.erixam");
	}

}
