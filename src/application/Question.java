package application;

public abstract class Question {
	private String question;
	private int points;
	private int timer;

	// cannot be initialized
	public Question(String question, int points) {
		this.question = question;
		setPoints(points);
		this.timer = 0;
	}

	public Question(String question, int points, int timer) {
		this.question = question;
		setPoints(points);
		setTimer(timer);
	}

	@Override
	public String toString() {
		return question;
	}

	public abstract String getInfo();

	public abstract String getFullInfo(String separator);

	public String getFullInfo() {
		return getFullInfo("\n");
	}

	public abstract String getCorrectAnswers();

	public String getFullInfoForFile() {
		return getFullInfo(System.lineSeparator());
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		if (points >= 0)
			this.points = points;
	}

	public int getTimer() {
		return timer;
	}

	public void setTimer(int timer) {
		if (timer >= 0)
			this.timer = timer;
	}

	public boolean hasTimer() {
		return timer > 0;
	}

	public boolean hasPoints() {
		return points > 0;
	}
}
