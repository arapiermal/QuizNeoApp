package application;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.PriorityQueue;

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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Main extends Application {
	private static User currUser;
	private BorderPane root; // maybe left -> info, top -> toolbox, right select quiz?
	private Text title;
	private TextField emailField;
	private Button loginButton;
	private TextField firstNameField;
	private TextField lastNameField;
	private Button registerButton;
	private VBox userLoginVBox;
	private HBox userDataHBox;
	private Label labelFullName;
	private Button logOutButton;
	private ToolBar quizDataToolBar;
	private Button loadQuizButton;
	private Button openLeaderboardButton;
	private QuizBox quizBox; // Custom UI element
	private ResultBox resultBox;

	@Override
	public void start(Stage primaryStage) {
		try {
			root = new BorderPane();
			Scene scene = new Scene(root, 800, 600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setTitle("EQuiz");
			title = new Text("EQuiz");
			title.setFont(Font.font("Arial", 36));
			emailField = new TextField();
			emailField.setPromptText("Email");
			loginButton = new Button("Login");
			loginButton.setOnAction(e -> loginUser());
			firstNameField = new TextField();
			firstNameField.setPromptText("First Name");
			lastNameField = new TextField();
			lastNameField.setPromptText("Last Name");
			registerButton = new Button("Register");
			registerButton.setOnAction(e -> registerUser());
			userLoginVBox = new VBox(16, title, emailField, loginButton, firstNameField, lastNameField, registerButton);
			userLoginVBox.setAlignment(Pos.CENTER);
			userLoginVBox.setPadding(new Insets(16));
			root.setCenter(userLoginVBox);

			resultBox = new ResultBox();
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
			quizDataToolBar = new ToolBar(loadQuizButton, openLeaderboardButton);
			quizDataToolBar.setVisible(false);
			root.setBottom(quizDataToolBar);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void openLeaderboard() {
		Map<Integer, PriorityQueue<Results>> leaderboardData = Leaderboard.getLeaderboard().getAllResults();

		Stage stage = new Stage();
		stage.setTitle("Leaderboard");

		TableView<Results> tableView = new TableView<>();

		TableColumn<Results, Integer> quizIdColumn = new TableColumn<>("Quiz ID");
		quizIdColumn.setCellValueFactory(new PropertyValueFactory<>("quizId"));

		TableColumn<Results, Integer> userIdColumn = new TableColumn<>("User ID");
		userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));

		TableColumn<Results, Integer> pointsColumn = new TableColumn<>("Points");
		pointsColumn.setCellValueFactory(new PropertyValueFactory<>("points"));

		TableColumn<Results, Integer> totalPointsColumn = new TableColumn<>("Total Points");
		totalPointsColumn.setCellValueFactory(new PropertyValueFactory<>("totalPoints"));

		TableColumn<Results, Double> percentageColumn = new TableColumn<>("Percentage Correct");
		percentageColumn.setCellValueFactory(new PropertyValueFactory<>("percentageCorrect"));

		tableView.getColumns().addAll(quizIdColumn, userIdColumn, pointsColumn, totalPointsColumn, percentageColumn);

		ObservableList<Results> data = FXCollections.observableArrayList();
		for (Map.Entry<Integer, PriorityQueue<Results>> entry : leaderboardData.entrySet()) {
			data.addAll(entry.getValue());
		}
		tableView.setItems(data);

		Scene scene = new Scene(tableView, 600, 400);
		stage.setScene(scene);

		stage.show();
	}

	private void loginUser() {
		String email = emailField.getText().trim();
		if (!email.isBlank()) {
			currUser = Login.getLoginData().loginUser(email);
			if (currUser == null) {
				// ERROR
				displayAlert("ERROR", "Email not found", "The email does not exist in our database");
			} else {
				// Continue, show menu to select quiz
				root.setCenter(quizBox);
				labelFullName.setText(currUser.toString());
				userDataHBox.setVisible(true);
				resultBox.setVisible(true);
				quizDataToolBar.setVisible(true);
			}
		} else {
			displayAlert("ERROR", "Blank email field", "The email you entered is blank");
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
		String firstName = firstNameField.getText().trim();
		String lastName = lastNameField.getText().trim();
		String email = emailField.getText().trim();
		if (!email.isBlank() && !firstName.isBlank() && !lastName.isBlank()) {
			currUser = Login.getLoginData().registerUser(firstName, lastName, email);
			if (currUser == null) {
				// ERROR
				displayAlert("ERROR", "Failed to register user", "The email might already exist in our database");
			} else {
				// Continue, show menu to select quiz
				root.setCenter(quizBox);
				labelFullName.setText(currUser.toString());
				resultBox.setVisible(true);
				userDataHBox.setVisible(true);
				quizDataToolBar.setVisible(true);
			}
		} else {
			displayAlert("ERROR", "Blank text fields", "At least one of the fields might be blank");
		}
	}

	private void loadQuiz() {
		try {
			// File recordsDir = new File(System.getProperty("user.home"),"quiz");
			// if (!recordsDir.exists()) recordsDir.mkdirs();

			FileChooser fileChooser = new FileChooser();
			// fileChooser.setInitialDirectory(recordsDir);
			fileChooser.setTitle("Open the '.txt' file containing a quiz");
			fileChooser.getExtensionFilters().add(new ExtensionFilter("Text Files", "*.txt"));
			File selectedFile = fileChooser.showOpenDialog(new Stage());
			quizBox.loadQuiz(Quiz.loadFromFile(selectedFile));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void displayAlert(String title, String headerText, String contentText) {
		Alert alert = new Alert(AlertType.ERROR);
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
