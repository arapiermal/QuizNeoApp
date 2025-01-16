package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class Leaderboard {
	private static Leaderboard leaderboard;

	private Map<Integer, PriorityQueue<Results>> allResults; // Integer -> QuizID (?)

	public Leaderboard() {
		allResults = load();
	}

	public void addResults(Results r) {
		int qId = r.getQuizId();
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
			leaderboard = new Leaderboard();
		return leaderboard;
	}

	public static Map<Integer, PriorityQueue<Results>> load() {
		Map<Integer, PriorityQueue<Results>> map = new HashMap<>();
		try (BufferedReader br = new BufferedReader(new FileReader("resources/leaderboard.txt"))) {
			String line;
			while ((line = br.readLine()) != null) {
				if ((line = line.trim()).isBlank())
					continue;
				try {
					int qId = Integer.parseInt(line);
					PriorityQueue<Results> res = loadSingle(br);
					map.put(qId, res);
				} catch (NumberFormatException nfe) {

				}
			}
		} catch (FileNotFoundException fnfe) {
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return map;
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

	public static void save(Map<Integer, PriorityQueue<Results>> map) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("resources/leaderboard.txt"))) {
			for (Map.Entry<Integer, PriorityQueue<Results>> entry : map.entrySet()) {
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

	public Map<Integer, PriorityQueue<Results>> getAllResults() {
		return allResults;
	}

	public PriorityQueue<Results> getResults(int qId) {
		return allResults.get(qId);
	}

}
