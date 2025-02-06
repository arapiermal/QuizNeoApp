package application;

import java.util.List;

public class MultipleChoiceQuestion extends Question {
	private List<Choice> choices;
	private int totalCorrect;

	public MultipleChoiceQuestion(String question, int points, List<Choice> choices) {
		super(question, points);
		this.choices = choices;
		calcTotalCorrect();
	}

	public MultipleChoiceQuestion(String question, int points, int timer, List<Choice> choices) {
		super(question, points, timer);
		this.choices = choices;
		calcTotalCorrect();
	}

	public List<Choice> getChoices() {
		return choices;
	}

	public void calcTotalCorrect() {
		totalCorrect = 0;
		for (Choice c : choices) {
			if (c.isCorrect())
				totalCorrect++;
		}
	}

	public boolean isSingleCorrect() {
		return totalCorrect == 1;
	}

	public boolean isMultiCorrect() {
		return totalCorrect > 1;
	}
	public boolean isNoneCorrect() {
		return totalCorrect == 0;
	}

	public boolean checkAnswer(boolean... checked) {
		if (checked.length == choices.size()) {
			int total = 0;
			for (int i = 0; i < choices.size(); i++) {
				if (choices.get(i).checkAnswer(checked[i]))
					total++;
			}
			return total == choices.size();
		}
		return false;
	}

	public String getInfo() {
		StringBuilder sb = new StringBuilder("MC");
		sb.append(",").append(getPoints());
		if (hasTimer()) {
			sb.append(",").append(getTimer());
		}
		sb.append(":").append(getQuestion());
		return sb.toString();
	}

	public String getFullInfo(String separator) {
		StringBuilder sb = new StringBuilder(getInfo());
		sb.append(separator);
		for (Choice c : choices) {
			sb.append(c.getInfo()).append(separator);
		}
		sb.append(".");
		return sb.toString();
	}

	@Override
	public String getCorrectAnswers() {
		StringBuilder sb = new StringBuilder();
		for (Choice c : choices) {
			if(c.isCorrect())
				sb.append(c.toString()).append("\n");
		}
		return sb.toString();
	}
}
