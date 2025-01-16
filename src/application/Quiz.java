package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Quiz {
	private static int QUIZ_IDS = 0;
	private final int quizId;
	private String name;
	private int totalPoints;
	private List<Question> questions;

	public Quiz(String name) {
		quizId = QUIZ_IDS++;
		this.name = name;
		this.questions = new ArrayList<>();
	}

	public Quiz(String name, List<Question> questions) {
		quizId = QUIZ_IDS++;
		this.name = name;
		this.questions = questions;
		calcTotalPoints();
	}

	@Override
	public String toString() {
		return name;
	}

	public List<Question> getQuestions() {
		return questions;
	}

	public void calcTotalPoints() {
		totalPoints = 0;
		for (Question q : questions) {
			totalPoints += q.getPoints();
		}
	}

	public void addQuestion(Question q) {
		questions.add(q);
		totalPoints += q.getPoints();
	}

	public void removeQuestion(Question q) {
		if (questions.remove(q))
			totalPoints -= q.getPoints();
	}

	public void removeQuestion(int i) {
		if (i < 0 || i >= questions.size())
			return;
		Question q = questions.remove(i);
		totalPoints -= q.getPoints();
	}

	public int getQuestionCount() {
		return questions.size();
	}

	public static int getQUIZ_IDS() {
		return QUIZ_IDS;
	}

	public static void setQUIZ_IDS(int val) {
		QUIZ_IDS = val;
	}

	public int getQuizId() {
		return quizId;
	}

	public int getTotalPoints() {
		return totalPoints;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static Quiz loadFromFile(File selectedFile) {
		if(selectedFile == null || !selectedFile.canRead()) {
			return null;
		}
		try (BufferedReader br = new BufferedReader(new FileReader(selectedFile))) {
			String name = br.readLine();
			if (name == null) {
				return null;
			}
			Quiz quiz = new Quiz(name.trim());
			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				// Comment (for now only works outside)
				if (line.startsWith("//")) {
					continue;
				}
				String[] parts = line.split("\\s*:\\s*", 2);

				String[] qargs = parts[0].split("\\s*,\\s*");
				int points = 0;
				int secTimer = 0;
				if (qargs.length > 1) {
					try {
						points = Integer.valueOf(qargs[1]);
					} catch (NumberFormatException nfe) {
						Main.displayAlert("ERROR", "Parsing points of question", "Wrong Number Format: " + qargs[1]);
					}
				}
				if (qargs.length > 2) {
					try {
						secTimer = Integer.valueOf(qargs[2]);
					} catch (NumberFormatException nfe) {
						Main.displayAlert("ERROR", "Parsing timer of question", "Wrong Number Format: " + qargs[2]);
					}
				}
				Question quest = null;
				switch (qargs[0].toUpperCase()) {
				case "QA":
					String ansQ = br.readLine();
					if (ansQ != null)
						quest = new AnswerQuestion(parts[1], points, secTimer, ansQ);
					break;
				case "TF":
					String tfQ = br.readLine();
					if (tfQ != null) {
						boolean tfBool = Character.toUpperCase(tfQ.trim().charAt(0)) == 'T';
						quest = new TrueFalseQuestion(parts[1], points, secTimer, tfBool);
					}
					break;
				case "MC":
					List<Choice> choices = new ArrayList<>();
					int choiceOrder = 0;
					String choiceLine;
					while ((choiceLine = br.readLine()) != null && !(choiceLine = choiceLine.trim()).startsWith(".")) {
						String[] splitChoice = choiceLine.split("\\s*:\\s*", 2);
						String choiceName;
						boolean choiceBool = false;
						if (splitChoice.length == 1) {
							choiceName = splitChoice[0];
						} else {
							choiceName = splitChoice[1];
							char firstChar = Character.toUpperCase(splitChoice[0].charAt(0));
							choiceBool = firstChar == 'T' || firstChar == 'C'; // True/Correct
						}
						choices.add(new Choice(choiceOrder++, choiceName, choiceBool));
					}
					quest = new MultipleChoiceQuestion(parts[1], points, secTimer, choices);
					break;
				default:
					String defAnsQ = br.readLine();
					if (defAnsQ != null)
						quest = new AnswerQuestion(parts[0], points, secTimer, defAnsQ);
					break;
				}
				if (quest != null)
					quiz.addQuestion(quest);
				else
					Main.displayAlert("ERROR", "Question failed parsing", "Last line read: " + line);
			}

			return quiz;
		} catch (IOException ioe) {
			return null;
		}
	}

	public static String getTextTemplate(String separator) {
		StringBuilder sb = new StringBuilder();
		sb.append("Quiz Name").append(separator);
		sb.append("//Comment, MC-> type, 10->points, 10->timer, '.'->end").append(separator);
		sb.append("MC,10,10:Multiple Choice Question").append(separator);
		sb.append("Choice one").append(separator);
		sb.append("True:Correct choice two").append(separator);
		sb.append("Choice three").append(separator);
		sb.append(".").append(separator);
		sb.append("TF,20:True False Question").append(separator);
		sb.append("True").append(separator);
		sb.append("AQ,10:Answer Question").append(separator);
		sb.append("CORRECTANSWER").append(separator);
		sb.append(".").append(separator);
		return sb.toString();
	}
	// BufferedReader br

	public Question getQuestion(int i) {
		if (i < 0 || i >= questions.size())
			return null;
		return questions.get(i);
	}

}
