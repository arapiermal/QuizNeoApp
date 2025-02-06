package tonichat;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.List;

import application.LoginDB;
import application.Main;

public class MessagesStage extends Stage {
	private String currentUsername;
	private String chatUsername;
	private long currentUserId;
	private long chatUserId;
	private VBox messagesBox;
	private TextArea messageInput;
	private Button sendButton;

	public MessagesStage(long currentUserId, long chatUserId, String currentUsername, String chatUsername) {
		this.currentUserId = currentUserId;
		this.chatUserId = chatUserId;
		this.currentUsername = currentUsername;
		this.chatUsername = chatUsername;
		setTitle("User " + currentUserId + ": " + currentUsername + " chat with User " + chatUserId + ": "
				+ chatUsername);
		setMinWidth(400);
		setMinHeight(500);

		BorderPane root = new BorderPane();

		// Chat messages display
		ScrollPane scrollPane = new ScrollPane();
		messagesBox = new VBox(5);
		messagesBox.setPadding(new Insets(10));
		scrollPane.setContent(messagesBox);
		scrollPane.setFitToWidth(true);
		root.setCenter(scrollPane);

		// Message input area
		HBox inputBox = new HBox(5);
		messageInput = new TextArea();
		messageInput.setPromptText("Type a message...");
		messageInput.setWrapText(true);
		messageInput.setPrefRowCount(2);

		sendButton = new Button("Send");
		sendButton.setPrefWidth(128);
		sendButton.setOnAction(e -> sendMessage());

		inputBox.getChildren().addAll(messageInput, sendButton);
		HBox.setHgrow(messageInput, Priority.ALWAYS);
		root.setBottom(inputBox);

		// Load existing messages
		loadMessages();

		Scene scene = new Scene(root, 500, 600);
		setScene(scene);
	}

	public static MessagesStage getMessagesWith(String otherUsername) {
		if (Main.getCurrUser() != null) {
			String senderUsername = Main.getCurrUser().getUsername();
			otherUsername = otherUsername.toLowerCase();
			long senderId = Main.getCurrUser().getUserId();
			long otherId = LoginDB.getUserId(otherUsername);
			if (otherId < 0) {
				return null;
			} else if (senderId == otherId) {
				Main.displayAlert("CANNOT SEND MESSAGE TO SELF", "senderId cannot be the same as receiverId", "");
				return null;
			}

			return new MessagesStage(senderId, otherId, senderUsername, otherUsername);
		} else {
			return null;
		}
	}

	// Load chat history
	private void loadMessages() {
		List<String> messages = MessagesDB.getMessagesBetweenUsers(currentUserId, chatUserId);
		messagesBox.getChildren().clear();

		for (String msg : messages) {
			msg = msg.replace("User " + currentUserId + ":", currentUsername + ":");
			msg = msg.replace("User " + chatUserId + ":", chatUsername + ":");
			Label messageLabel = new Label(msg);
			messageLabel.setWrapText(true);
			messagesBox.getChildren().add(messageLabel);
		}
	}

	// Send a new message
	private void sendMessage() {
		String message = messageInput.getText().trim();
		if (!message.isEmpty()) {
			MessagesDB.sendMessage(currentUserId, chatUserId, message);
			messageInput.clear();
			loadMessages(); // Refresh chat
		}
	}
}
