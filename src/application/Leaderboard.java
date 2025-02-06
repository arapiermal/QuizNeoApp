package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class Leaderboard {
	private static Leaderboard leaderboard;

	private Map<Long, PriorityQueue<Results>> allResults; // Integer -> QuizID (?)

	private Leaderboard(boolean sync) {
		allResults = new HashMap<>();
		if (sync) {
			loadSync();
		} else {
			load();
		}
	}

	public void addResults(Results r) {
		long qId = r.getQuizId();
		allResults.putIfAbsent(qId, new PriorityQueue<>());
		PriorityQueue<Results> pq = allResults.get(qId);
		if (r.isFinished())
			pq.add(r);
		else
			throw new IllegalArgumentException("Can only add finished results");
		save(allResults);
	}

	public static Leaderboard getLeaderboard() {
		if (leaderboard == null)
			leaderboard = new Leaderboard(true);
		return leaderboard;
	}

	public void load() {
		try (BufferedReader br = new BufferedReader(new FileReader("resources/leaderboard.txt"))) {
			String line;
			while ((line = br.readLine()) != null) {
				if ((line = line.trim()).isBlank())
					continue;
				try {
					long qId = Long.parseLong(line);
					PriorityQueue<Results> res = loadSingle(br);
					allResults.put(qId, res);
				} catch (NumberFormatException nfe) {

				}
			}
		} catch (FileNotFoundException fnfe) {

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public void loadSync() {
		Set<Long> quizIds = LeaderboardDB.getQuizIds();
		for (long quizId : quizIds) {
			PriorityQueue<Results> res = LeaderboardDB.getResultsByQuiz(quizId);
			allResults.put(quizId, res);
		}
	}

	public static PriorityQueue<Results> loadSingle(BufferedReader br) throws IOException {
		PriorityQueue<Results> pq = new PriorityQueue<>();
		String line;
		while ((line = br.readLine()) != null && !(line = line.trim()).startsWith(".")) {
			if (line.isBlank())
				continue;
			String[] params = line.split("\\s*,\\s*");
			if (params.length >= 5) {
				pq.add(new Results(params[0], params[1], params[2], params[3], params[4]));
			}

		}
		return pq;
	}

	public static void save() {
		save(getLeaderboard().allResults);
	}

	public static void save(Map<Long, PriorityQueue<Results>> map) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("resources/leaderboard.txt"))) {
			for (Map.Entry<Long, PriorityQueue<Results>> entry : map.entrySet()) {
				writer.write(String.valueOf(entry.getKey()));
				writer.newLine();
				for (Results result : entry.getValue()) {
					writer.write(result.getFullInfo());
					writer.newLine();
				}
				writer.write(".");
				writer.newLine();
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public Map<Long, PriorityQueue<Results>> getAllResults() {
		return allResults;
	}

	public PriorityQueue<Results> getResults(long qId) {
		return allResults.get(qId);
	}

}
