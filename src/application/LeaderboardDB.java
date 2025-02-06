package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class LeaderboardDB {

	public static void saveResult(Results result) {
		String sql = "INSERT INTO results (quiz_id, user_id, points, total_points) VALUES (?, ?, ?, ?)";
		long resultId = -1;

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			stmt.setLong(1, result.getQuizId());
			stmt.setLong(2, result.getUserId());
			stmt.setInt(3, result.getPoints());
			stmt.setInt(4, result.getTotalPoints());

			int affectedRows = stmt.executeUpdate();
			if (affectedRows > 0) {
				try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						resultId = generatedKeys.getLong(1);
						result.setResultId(resultId);
					}
				}
			}
			System.out.println("Result saved successfully with Result ID: " + resultId);

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public static PriorityQueue<Results> getResultsByQuiz(long quizId) {
		return getResultsBySth(quizId, "quiz_id");
	}

	public static PriorityQueue<Results> getResultsByUser(long userId) {
		return getResultsBySth(userId, "user_id");
	}

	public static PriorityQueue<Results> getResultsBySth(long sthId, String basedOn) {
		String sql = "SELECT result_id, quiz_id, user_id, points, total_points FROM results WHERE " + basedOn
				+ " = ? ORDER BY attempt_date DESC";
		PriorityQueue<Results> resultsList = new PriorityQueue<Results>();
		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setLong(1, sthId);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				Results result = new Results(rs.getLong("result_id"), rs.getLong("quiz_id"), rs.getLong("user_id"),
						rs.getInt("points"), rs.getInt("total_points"));
				resultsList.add(result);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return resultsList;
	}

	public static Set<Long> getQuizIds() {
		String sql = "SELECT quiz_id FROM results";
		Set<Long> quizIds = new HashSet<>();
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				quizIds.add(rs.getLong("quiz_id"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return quizIds;
	}
}
