package application;

public class TrueFalseQuestion extends Question {
	private boolean truthful;

	public TrueFalseQuestion(String question, int points, boolean truthful) {
		super(question, points);
		this.truthful = truthful;
	}

	public TrueFalseQuestion(String question, int points, int timer, boolean truthful) {
		super(question, points, timer);
		this.truthful = truthful;
	}

	public boolean isTruthful() {
		return truthful;
	}

	public boolean checkAnswer(boolean checked) {
		return checked == truthful;
	}

	@Override
	public String getInfo() {
		StringBuilder sb = new StringBuilder("TF");
		sb.append(",").append(getPoints());
		if (hasTimer()) {
			sb.append(",").append(getTimer());
		}
		sb.append(":").append(getQuestion());
		return sb.toString();
	}

	@Override
	public String getFullInfo(String separator) {
		StringBuilder sb = new StringBuilder(getInfo());
		sb.append(separator).append(truthful ? "True" : "False");
		return sb.toString();
	}

	@Override
	public String getCorrectAnswers() {
		return String.valueOf(truthful);
	}

}
