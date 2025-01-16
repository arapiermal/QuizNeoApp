package application;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

public class QuizBox extends VBox {
	private Quiz currQuiz;
	private int currIndex;
	private Timer timer;
	private ResultBox resultBox; // Reference
	private Results results;
	private Label timerLabel;
	private Label quizName;
	private Label quizPoints;
	private Label currQuestionLabel;
	private Label currQuestionPointsLabel;
	private VBox choicesVBox;
	private TextField answerQuestionField;
	private ToggleButton trueFalseToggleButton;

	private Button nextButton;

	public QuizBox(ResultBox resultBox) {
		super(16);
		this.resultBox = resultBox;
		quizName = new Label("No Quiz Selected");
		quizPoints = new Label();
		timerLabel = new Label();
		Region reg1 = new Region();
		HBox quizTopHBox = new HBox(16, quizName, reg1, timerLabel, quizPoints);
		HBox.setHgrow(reg1, Priority.ALWAYS);
		getChildren().add(quizTopHBox);
		currQuestionLabel = new Label();
		currQuestionPointsLabel = new Label();
		Region reg2 = new Region();
		HBox quizMidHBox = new HBox(16, currQuestionLabel, reg2, currQuestionPointsLabel);
		HBox.setHgrow(reg2, Priority.ALWAYS);
		getChildren().add(quizMidHBox);
		answerQuestionField = new TextField();
		answerQuestionField.setVisible(false);
		getChildren().add(answerQuestionField);
		trueFalseToggleButton = new ToggleButton("False");
		trueFalseToggleButton.setVisible(false);
		getChildren().add(trueFalseToggleButton);
		trueFalseToggleButton.setOnAction(e -> {
			if (trueFalseToggleButton.isSelected()) {
				trueFalseToggleButton.setText("True");
			} else {
				trueFalseToggleButton.setText("False");
			}
		});
		choicesVBox = new VBox(16); // for multiple choices
		getChildren().add(choicesVBox);
		nextButton = new Button("Next");
		nextButton.setOnAction(e -> nextQuestion());
		nextButton.setAlignment(Pos.BOTTOM_RIGHT);
		nextButton.setDisable(true);
		getChildren().add(nextButton);

		setAlignment(Pos.TOP_CENTER);
		setPadding(new Insets(16));
	}

	private void nextQuestion() {
		if (currQuiz != null) {
			checkAnswer();
			currIndex++;
			if (currIndex == currQuiz.getQuestionCount()) {
				stopTimer();
				// Finish
				finishQuiz();
				return;
			} else if (currIndex + 1 == currQuiz.getQuestionCount()) {
				nextButton.setText("Finish");
			}
			Question q = currQuiz.getQuestion(currIndex);
			loadQuestion(q);
		}
	}

	private void setTimer(int questionTimer) {
		stopTimer();
		if (questionTimer <= 0) {
			return;
		}
		int[] totalSeconds = { questionTimer };
		this.timer = new Timer();
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						if (totalSeconds[0] <= 0) {
							stopTimer();
							nextQuestion();
							
						} else {
							totalSeconds[0]--;
							timerLabel.setText(totalSeconds[0] + "s");
						}
					}
				});
			}
		};

		timer.schedule(timerTask, 0, 1000);

	}

	public void stopTimer() {
		if (timer != null) {
			timer.cancel();
			timer.purge();
			timerLabel.setText("");
		}
	}

	public void checkAnswer() {
		Question q = currQuiz.getQuestion(currIndex);
		if (q == null)
			return;
		String stringifiedAnswer = "";
		boolean corr = false;
		if (q instanceof AnswerQuestion qa) {
			String ansQA = answerQuestionField.getText();
			corr = qa.checkAnswer(ansQA);

			stringifiedAnswer = ansQA;
		} else if (q instanceof TrueFalseQuestion tf) {
			boolean ansTF = trueFalseToggleButton.isSelected();
			corr = tf.checkAnswer(ansTF);

			stringifiedAnswer = String.valueOf(ansTF);
		} else if (q instanceof MultipleChoiceQuestion mc) {
			boolean[] ansMC = getSelectedAnswers();
			corr = mc.checkAnswer(ansMC);

			stringifiedAnswer = "";
		}
		results.addAnswerResult(corr);
		if (corr) {
			results.addPoints(q.getPoints());
			resultBox.updateCurrPoints(results.getPoints());
			resultBox.setSVGCorrectIncorrect(1);
			// Show that you were correct
		} else {
			// Show the correct answer
			resultBox.setSVGCorrectIncorrect(-1);

		}
		resultBox.setFromLastQuestion(q.toString(), stringifiedAnswer, q.getCorrectAnswers());
	}

	public void finishQuiz() {
		results.finishQuiz();
		nextButton.setDisable(true);
		Leaderboard.getLeaderboard().addResults(results);
	}

	public void loadQuiz(Quiz quiz) {
		if (quiz == null) {
			Main.displayAlert("ERROR", "Failed to load Quiz", "");
			return;
		}
		if (quiz.getQuestionCount() < 1) {
			Main.displayAlert("ERROR", "Quiz has no questions", "");
			return;
		}
		if (currQuiz != null) {
			// remove quiz logic
		}
		currIndex = 0;
		currQuiz = quiz;
		quizName.setText(currQuiz.toString());
		quizPoints.setText(currQuiz.getTotalPoints() + " total points");
		loadQuestion(currQuiz.getQuestion(currIndex));
		nextButton.setDisable(false);
		if (currIndex + 1 == currQuiz.getQuestionCount()) {
			nextButton.setText("Finish");
		} else {
			nextButton.setText("Next");
		}

		results = new Results(currQuiz.getQuizId(), Main.getCurrUser().getUserId(), 0, currQuiz.getTotalPoints());
	}

	public void loadQuestion(Question q) {
		if (q != null) {
			currQuestionLabel.setText(q.toString());
			currQuestionPointsLabel.setText(q.getPoints() + " points");
			if (q instanceof AnswerQuestion) {
				answerQuestionField.setText("");
				answerQuestionField.setVisible(true);
				trueFalseToggleButton.setVisible(false);
				choicesVBox.setVisible(false);
			} else if (q instanceof TrueFalseQuestion) {
				answerQuestionField.setVisible(false);
				trueFalseToggleButton.setSelected(false);
				trueFalseToggleButton.setVisible(true);
				choicesVBox.setVisible(false);
			} else if (q instanceof MultipleChoiceQuestion) {
				answerQuestionField.setVisible(false);
				trueFalseToggleButton.setVisible(false);
				MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) q;
				if (mcq.isSingleCorrect()) {
					loadSingleCorrect(mcq);
				} else {
					loadMultiCorrect(mcq);
				}
				choicesVBox.setVisible(true);

			}
			setTimer(q.getTimer());
		}
	}

	private void loadSingleCorrect(MultipleChoiceQuestion mcq) {
		choicesVBox.getChildren().clear();
		ToggleGroup group = new ToggleGroup();
		for (Choice choice : mcq.getChoices()) {
			RadioButton rb = new RadioButton(choice.toString());
			rb.setToggleGroup(group);
			choicesVBox.getChildren().add(rb);
		}
	}

	private void loadMultiCorrect(MultipleChoiceQuestion mcq) {
		choicesVBox.getChildren().clear();
		for (Choice choice : mcq.getChoices()) {
			CheckBox cb = new CheckBox(choice.toString());
			choicesVBox.getChildren().add(cb);
		}
	}

	public boolean[] getSelectedAnswers() {
		boolean[] selectedAnswers = new boolean[choicesVBox.getChildren().size()];
		Arrays.fill(selectedAnswers, false);
		int i = 0;
		for (Node node : choicesVBox.getChildren()) {
			if (node instanceof RadioButton) {
				RadioButton rb = (RadioButton) node;
				if (rb.isSelected()) {
					selectedAnswers[i] = true;
				}
			} else if (node instanceof CheckBox) {
				CheckBox cb = (CheckBox) node;
				if (cb.isSelected()) {
					selectedAnswers[i] = true;
					;
				}
			}
			i++;
		}
		return selectedAnswers;
	}

	public void forceOut() {

	}

}
