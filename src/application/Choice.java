package application;

public class Choice {
	private int order;
	private String name;
	private boolean correct;

	public Choice(String name, boolean correct) {
		this.name = name;
		this.correct = correct;
	}

	public Choice(int order, String name, boolean correct) {
		this.order = order;
		this.name = name;
		this.correct = correct;
	}

	@Override
	public String toString() {
		return name;
	}

	public boolean isCorrect() {
		return correct;
	}

	public boolean checkAnswer(boolean checked) {
		return checked == correct;
	}

	public int getOrder() {
		return order;
	}
	
	public String getInfo() {
		return correct ? "True" : "False" + ":" + name;
	}
}
