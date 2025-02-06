package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

public class ResultBox extends VBox {
	private SVGPath correctPath;
	private SVGPath incorrectPath;
	private Label currResults;
	private Label currPoints;
	private Label lastQuestion;
	private Label lastUserAnswer;
	private Label lastCorrectAnswer;

	public ResultBox() {
		super(16);
		currResults = new Label("Results");
		currPoints = new Label("0");
		lastQuestion = new Label();
		lastUserAnswer = new Label();
		lastCorrectAnswer = new Label();
		initSVGPaths();
		setAlignment(Pos.CENTER);
		setPadding(new Insets(16));
		getChildren().addAll(currResults, currPoints, new Text("Last Question"), lastQuestion, new Text("Your answer"),
				lastUserAnswer, new Text("Correct answer"), lastCorrectAnswer, correctPath, incorrectPath);
	}

	public void updateCurrPoints(int val) {
		currPoints.setText(val + " points");
	}

	public void setSVGCorrectIncorrect(int val) {
		if (val > 0) {
			correctPath.setVisible(true);
			incorrectPath.setVisible(false);
		} else if (val < 0) {
			correctPath.setVisible(false);
			incorrectPath.setVisible(true);
		} else {
			correctPath.setVisible(false);
			incorrectPath.setVisible(false);
		}
	}

	public void setFromLastQuestion(String lastQ, String ans, String corr) {
		lastQuestion.setText(lastQ);
		lastUserAnswer.setText(ans);
		lastCorrectAnswer.setText(corr);
	}

	public void initSVGPaths() {
		correctPath = new SVGPath();
		correctPath.setContent("M 10 50 L 40 80 L 70 20");
		correctPath.setStroke(Color.GREEN);
		correctPath.setStrokeWidth(3);
		correctPath.setFill(Color.TRANSPARENT);
		correctPath.setVisible(false);

		incorrectPath = new SVGPath();
		incorrectPath.setContent("M 20 20 L 80 80 M 80 20 L 20 80");
		incorrectPath.setStroke(Color.RED);
		incorrectPath.setStrokeWidth(3);
		incorrectPath.setFill(Color.TRANSPARENT);
		incorrectPath.setVisible(false);
	}
	// store results

}
