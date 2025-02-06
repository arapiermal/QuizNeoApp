package application;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;

import ericrypt.FileCrypt;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import tonichat.MessagesStage;

public class Main extends Application {
	private static User currUser;
	private BorderPane root; // maybe left -> info, top -> toolbox, right select quiz?
	private Text title;
	private TextField usernameField;
	private PasswordField passwordField;
	private Button loginButton;
	private TextField firstNameField;
	private TextField lastNameField;
	private TextField emailField;
	private Button registerButton;
	private VBox userLoginVBox;
	private HBox userDataHBox;
	private Label labelFullName;
	private Button logOutButton;
	private ToolBar quizDataToolBar;
	private Button loadQuizButton;
	private Button openLeaderboardButton;
	private Button openMessagesButton;
	private Button encryptButton;
	private Button decryptButton;
	private QuizBox quizBox; // Custom UI element
	private ResultBox resultBox;

	@Override
	public void start(Stage primaryStage) {
		try {
			root = new BorderPane();
			Scene scene = new Scene(root, 1200, 700);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setTitle("EQuiz");
			title = new Text("EQuiz");
			title.setFont(Font.font("Arial", 36));
			usernameField = new TextField();
			usernameField.setPromptText("Username");
			usernameField.setMaxWidth(300);
			passwordField = new PasswordField();
			passwordField.setPromptText("Password");
			passwordField.setMaxWidth(300);

			loginButton = new Button("Login");
			loginButton.setOnAction(e -> loginUser());

			firstNameField = new TextField();
			firstNameField.setPromptText("First Name");
			firstNameField.setMaxWidth(300);
			lastNameField = new TextField();
			lastNameField.setPromptText("Last Name");
			lastNameField.setMaxWidth(300);
			emailField = new TextField();
			emailField.setPromptText("Email");
			emailField.setMaxWidth(300);

			registerButton = new Button("Register");
			registerButton.setOnAction(e -> registerUser());
			userLoginVBox = new VBox(16, title, new Text("Username"), usernameField, new Text("Password"),
					passwordField, loginButton, new Text("First Name"), firstNameField, new Text("Last Name"),
					lastNameField, new Text("Email"), emailField, registerButton);
			userLoginVBox.setAlignment(Pos.CENTER);
			userLoginVBox.setPadding(new Insets(16));
			root.setCenter(userLoginVBox);

			resultBox = new ResultBox();
			// Fix taking space while invisible
			resultBox.managedProperty().bind(resultBox.visibleProperty());
			//
			resultBox.setVisible(false);

			root.setRight(resultBox);

			quizBox = new QuizBox(resultBox);

			labelFullName = new Label();
			Region regUD = new Region();
			logOutButton = new Button("Log Out");
			logOutButton.setOnAction(e -> logOutUser());
			userDataHBox = new HBox(16, labelFullName, regUD, logOutButton);
			userDataHBox.setPadding(new Insets(16));
			HBox.setHgrow(regUD, Priority.ALWAYS);
			userDataHBox.setVisible(false);

			root.setTop(userDataHBox);
			loadQuizButton = new Button("Load Quiz");
			loadQuizButton.setOnAction(e -> loadQuiz());
			openLeaderboardButton = new Button("Leaderboard");
			openLeaderboardButton.setOnAction(e -> openLeaderboard());
			openMessagesButton = new Button("Send Messages");
			openMessagesButton.setOnAction(e -> openMessages());
			encryptButton = new Button("Encrypt");
			encryptButton.setOnAction(e -> encryptQuiz());
			decryptButton = new Button("Decrypt");
			decryptButton.setOnAction(e -> decryptQuiz());

			quizDataToolBar = new ToolBar(loadQuizButton, openLeaderboardButton, openMessagesButton, encryptButton,
					decryptButton);
			quizDataToolBar.setVisible(false);
			root.setBottom(quizDataToolBar);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void openMessages() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Open Chat");
		dialog.setHeaderText("Enter Username to Chat With:");
		dialog.setContentText("Username:");

		Optional<String> result = dialog.showAndWait();
		result.ifPresent(username -> {
			MessagesStage chat = MessagesStage.getMessagesWith(username);
			if (chat != null) {
				chat.show();
			} else {
				// Main.displayAlert("User Not Found", "The username you entered does not
				// exist.", "");
			}
		});
	}

	private void openLeaderboard() {
		Map<Long, PriorityQueue<Results>> leaderboardData = Leaderboard.getLeaderboard().getAllResults();

		Stage stage = new Stage();
		stage.setTitle("Leaderboard");

		TableView<Results> tableView = new TableView<>();

		TableColumn<Results, Long> quizIdColumn = new TableColumn<>("Quiz ID");
		quizIdColumn.setCellValueFactory(new PropertyValueFactory<>("quizId"));

		TableColumn<Results, Long> userIdColumn = new TableColumn<>("User ID");
		userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));

		TableColumn<Results, Integer> pointsColumn = new TableColumn<>("Points");
		pointsColumn.setCellValueFactory(new PropertyValueFactory<>("points"));

		TableColumn<Results, Integer> totalPointsColumn = new TableColumn<>("Total Points");
		totalPointsColumn.setCellValueFactory(new PropertyValueFactory<>("totalPoints"));

		TableColumn<Results, Double> percentageColumn = new TableColumn<>("Percentage Correct");
		percentageColumn.setCellValueFactory(new PropertyValueFactory<>("percentageCorrect"));

		tableView.getColumns().addAll(quizIdColumn, userIdColumn, pointsColumn, totalPointsColumn, percentageColumn);

		ObservableList<Results> data = FXCollections.observableArrayList();
		for (Map.Entry<Long, PriorityQueue<Results>> entry : leaderboardData.entrySet()) {
			data.addAll(entry.getValue());
		}
		tableView.setItems(data);

		Scene scene = new Scene(tableView, 600, 400);
		stage.setScene(scene);

		stage.show();
	}

	private void loginUser() {
		String username = usernameField.getText().trim();
		String password = passwordField.getText();
		if (!username.isBlank() && !password.isBlank()) {
			currUser = LoginDB.loginUser(username, password);
			if (currUser == null) {
				// ERROR already dealt with in LoginDB
			} else {

				// Continue, show menu to select quiz
				loggedIn();
			}
		} else {
			displayAlert("ERROR", "Empty username or password", "");
		}
	}

	private void logOutUser() {
		if (currUser != null) {
			root.setCenter(userLoginVBox);
			userDataHBox.setVisible(false);
			resultBox.setVisible(false);
			quizDataToolBar.setVisible(false);
			quizBox.forceOut();
			currUser = null;
			labelFullName.setText("");
		}
	}

	private void registerUser() {
		String username = usernameField.getText().trim();
		String password = passwordField.getText();
		String firstName = firstNameField.getText().trim();
		String lastName = lastNameField.getText().trim();
		String email = emailField.getText().trim();
		if (!username.isBlank() && !password.isBlank() && !firstName.isBlank() && !lastName.isBlank()
				&& User.isEmailValid(email)) {
			int userRole = User.Role.REGULAR_USER.getLevel();
			if(User.isEducationalEmail(email)) {
				userRole = User.Role.STUDENT.getLevel();
			}
			currUser = LoginDB.registerUser(username, firstName, lastName, email, password, userRole);

			if (currUser == null) {
				// ERROR
				displayAlert("ERROR", "Failed to register user", "");
			} else {
				// Continue, show menu to select quiz
				loggedIn();
			}
		} else {
			displayAlert("ERROR", "Blank text fields", "At least one of the fields might be blank");
		}
	}

	private void loggedIn() {
		root.setCenter(quizBox);
		labelFullName.setText(currUser.toStringLong());
		resultBox.setVisible(true);
		userDataHBox.setVisible(true);
		if (currUser.hasEriCryptAccess()) {
			encryptButton.setDisable(false);
			decryptButton.setDisable(false);
		} else {
			encryptButton.setDisable(true);
			decryptButton.setDisable(true);
		}

		quizDataToolBar.setVisible(true);

	}

	private void loadQuiz() {
		try {
			// File recordsDir = new File(System.getProperty("user.home"),"quiz");
			// if (!recordsDir.exists()) recordsDir.mkdirs();

			FileChooser fileChooser = new FileChooser();
			// fileChooser.setInitialDirectory(recordsDir);
			fileChooser.setTitle("Open the '.txt' file containing a quiz or '.erixam' encrypted quiz");
			fileChooser.getExtensionFilters().add(new ExtensionFilter("Text Files", "*.txt"));
			fileChooser.getExtensionFilters().add(new ExtensionFilter("Erixam Files", "*.erixam"));
			File selectedFile = fileChooser.showOpenDialog(new Stage());
			quizBox.loadQuiz(Quiz.loadFromFile(selectedFile));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void encryptQuiz() {
		try {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Encrypt the '.txt' file containing a quiz");
			fileChooser.getExtensionFilters().add(new ExtensionFilter("Text Files", "*.txt"));
			File selectedFile = fileChooser.showOpenDialog(new Stage());
			FileCrypt.encryptFile(selectedFile.getAbsolutePath().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void decryptQuiz() {
		try {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Decrypt the '.erixam' encrypted quiz");
			fileChooser.getExtensionFilters().add(new ExtensionFilter("Erixam Files", "*.erixam"));
			File selectedFile = fileChooser.showOpenDialog(new Stage());
			FileCrypt.decryptFile(selectedFile.getAbsolutePath().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void displayAlert(String title, String headerText, String contentText) {
		Alert alert = new Alert(AlertType.WARNING); // CHANGE BASED ON SUCCESS!!!!
		alert.setTitle(title);
		alert.setHeaderText(headerText);
		alert.setContentText(contentText);
		alert.showAndWait();
	}

	public static User getCurrUser() {
		return currUser;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
