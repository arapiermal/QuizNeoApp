package application;

public class AnswerQuestion extends Question {
	private String correctAnswer;

	public AnswerQuestion(String question, int points, double secTimer) {
		super(question, points);
	}

	public AnswerQuestion(String question, int points, double secTimer, String correctAnswer) {
		super(question, points);
		setCorrectAnswer(correctAnswer);
	}

	public boolean checkAnswer(String answer) {
		return correctAnswer.equalsIgnoreCase(answer);
	}

	public String getCorrectAnswer() {
		return correctAnswer;
	}

	public void setCorrectAnswer(String correctAnswer) {
		this.correctAnswer = correctAnswer;
	}

	public String getInfo() {
		StringBuilder sb = new StringBuilder("QA");
		sb.append(",").append(getPoints());
		if(hasTimer()) {
			sb.append(",").append(getTimer());
		}
		sb.append(":").append(getQuestion());
		return sb.toString();
	}

	public String getFullInfo(String separator) {
		return getInfo() + separator + correctAnswer;
	}

	@Override
	public String getCorrectAnswers() {
		return correctAnswer;
	}
}
