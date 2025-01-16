package application;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Results implements Comparable<Results> {
	private int quizId;
	private int userId;
	private int points;
	private int totalPoints;
	private double percentageCorrect;
	private List<Boolean> answerResults;
	private boolean finished;

	public Results(int quizId, int userId, int points, int totalPoints) {
		this.quizId = quizId;
		this.userId = userId;
		this.points = points;
		this.totalPoints = totalPoints;
		this.answerResults = new LinkedList<>();
	}

	public Results(String quizId, String userId, String points, String totalPoints, String answerResults) {
		this.quizId = Integer.parseInt(quizId);
		this.userId = Integer.parseInt(userId);
		this.points = Integer.parseInt(points);
		this.totalPoints = Integer.parseInt(totalPoints);
		this.answerResults = new LinkedList<>();
		for (char c : answerResults.toCharArray()) {
			if (c == 'T' || c == 't' || c == 'C' || c == 'c') {
				this.answerResults.add(true);
			} else {
				this.answerResults.add(false);
			}
		}
		finishQuiz();
	}

	public void finishQuiz() {
		finished = true;
		percentageCorrect = (double) points / totalPoints * 100.0;
	}

	public int getQuizId() {
		return quizId;
	}

	public int getUserId() {
		return userId;
	}

	public int getPoints() {
		return points;
	}

	public int getTotalPoints() {
		return totalPoints;
	}

	public double getPercentageCorrect() {
		return percentageCorrect;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Quiz ").append(quizId).append(",User ").append(userId).append(",").append(points).append("/")
				.append(totalPoints);
		return sb.toString();
	}

	public String getInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append(quizId).append(",").append(userId).append(",").append(points).append(",").append(totalPoints);
		return sb.toString();
	}

	public String getFullInfo() {
		StringBuilder sb = new StringBuilder(getInfo());
		sb.append(",");
		for (boolean b : answerResults) {
			sb.append(b ? 'T' : 'F');
		}
		return sb.toString();
	}

	public void addAnswerResult(boolean corr) {
		if (finished)
			return;
		answerResults.add(corr);
	}

	public void addPoints(int points) {
		if (finished)
			return;
		this.points += points;
	}

	public boolean isFinished() {
		return finished;
	}

	@Override
	public int compareTo(Results o) {
		return Double.compare(o.percentageCorrect, this.percentageCorrect);
	}
}
