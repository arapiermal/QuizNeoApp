package tonichat;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import application.DBConnection;

public class MessagesDB {

    // Save a message to the database
    public static void sendMessage(long senderId, long receiverId, String message) {
        String sql = "INSERT INTO usermessages (sender_id, receiver_id, message) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, senderId);
            stmt.setLong(2, receiverId);
            stmt.setString(3, message);
            stmt.executeUpdate();
            System.out.println("Message sent successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get messages between two users
    public static List<String> getMessagesBetweenUsers(long userId1, long userId2) {
        String sql = "SELECT sender_id, message, sent_at FROM usermessages " +
                     "WHERE (sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?) " +
                     "ORDER BY sent_at";

        List<String> messages = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId1);
            stmt.setLong(2, userId2);
            stmt.setLong(3, userId2);
            stmt.setLong(4, userId1);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                long sender = rs.getLong("sender_id");
                String text = rs.getString("message");
                Timestamp sentAt = rs.getTimestamp("sent_at");

                messages.add("[" + sentAt + "] User " + sender + ": " + text);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return messages;
    }

    // Delete a message by ID
    public static void deleteMessage(long messageId) {
        String sql = "DELETE FROM usermessages WHERE message_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, messageId);
            int rowsDeleted = stmt.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Message deleted successfully!");
            } else {
                System.out.println("Message not found!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

