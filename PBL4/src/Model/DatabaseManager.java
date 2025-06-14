package Model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:mysql://localhost:4306/game";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    // Kết nối đến cơ sở dữ liệu
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // ---------- Phương thức cho bảng Player ----------
    // Thêm người chơi mới vào cơ sở dữ liệu
    public boolean addPlayer(Player player) {
        String sql = "INSERT INTO users (username, password, highScore, gamesPlayed, gamesWon) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, player.getUsername());
            stmt.setString(2, player.getPassword());
            stmt.setInt(3, player.getHighScore());
            stmt.setInt(4, player.getGamesPlayed());
            stmt.setInt(5, player.getGamesWon());
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm người chơi: " + e.getMessage());
            return false;
        }
    }

    public boolean updatePlayerStats(String playerName, int playerScore, boolean isWinner) {
        String updateQuery = "UPDATE users SET highScore = GREATEST(highScore, ?), gamesPlayed = gamesPlayed + 1, gamesWon = gamesWon + ? WHERE username = ?";
        
        try (Connection conn = getConnection(); PreparedStatement preparedStatement = conn.prepareStatement(updateQuery)) {
            preparedStatement.setInt(1, playerScore); // Cập nhật highScore nếu lớn hơn
            preparedStatement.setInt(2, isWinner ? 1 : 0); // Thêm 1 vào gamesWon nếu thắng
            preparedStatement.setString(3, playerName); // Lọc theo tên người chơi

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0; // Trả về true nếu có hàng được cập nhật
        } catch (SQLException e) {
            e.printStackTrace(); // In lỗi ra console để debug
            return false;
        }
    }
    
    public Player getUserByUsername(String username) {
        Player player = null;
        String query = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                player = new Player(rs.getInt("id"), rs.getString("username"), rs.getString("password"),
                        rs.getInt("highScore"), rs.getInt("gamesPlayed"), rs.getInt("gamesWon"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return player;
    }

    public List<Player> getAllPlayers() {
        List<Player> players = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                players.add(new Player(rs.getInt("id"), rs.getString("username"), rs.getString("password"),
                        rs.getInt("highScore"), rs.getInt("gamesPlayed"), rs.getInt("gamesWon")));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách người chơi: " + e.getMessage());
        }
        return players;
    }

    public boolean usernameExists(String username) {
        String query = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {    
            e.printStackTrace();
        }
        return false;
    }

    public boolean registerUser(String username, String password) {
        String query = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ---------- Phương thức cho bảng Room ----------
    public boolean addRoom(int idRoom, String nameRoom) {
        String query = "INSERT INTO room (idRoom, nameRoom, statusRoom) VALUES (?, ?, ?)";
        try (Connection conn = getConnection(); 
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idRoom);
            stmt.setString(2, nameRoom);
            stmt.setString(3, "open");
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Loi khi them phong: " + e.getMessage());
        }
        return false;
    }

    public boolean updateRoom(Room room) {
        String query = "UPDATE room SET roomName = ?, status = ? WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, room.getNameRoom());
            stmt.setString(2, room.getStatusRoom());
            stmt.setInt(3, room.getIdRoom());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật phòng: " + e.getMessage());
        }
        return false;
    }

    public Room getRoomStatus(int idRoom, String nameRoom) {
        String query = "SELECT * FROM room WHERE idRoom = ? AND nameRoom = ?";
        try (Connection conn = getConnection(); 
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idRoom);
            stmt.setString(2, nameRoom);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("idRoom");
                String name = rs.getString("nameRoom");
                String status = rs.getString("statusRoom");
                return new Room(id, name, status); // Trả về đối tượng Room
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy thông tin phòng: " + e.getMessage());
        }
        return null;
    }

    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        String query = "SELECT * FROM Room";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                rooms.add(new Room(
                    rs.getInt("id"),
                    rs.getString("roomName"),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách phòng: " + e.getMessage());
        }
        return rooms;
    }

    public boolean deleteRoom(int id) {
        String query = "DELETE FROM Room WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa phòng: " + e.getMessage());
        }
        return false;
    }

    // ---------- Phương thức cho bảng History ----------
    public boolean addHistory(History history) {
        String query = "INSERT INTO History (user_id, room_id, score, play_time) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, history.getUserId());
            stmt.setInt(2, history.getRoomId());
            stmt.setInt(3, history.getScore());
            stmt.setTimestamp(4, history.getPlayTime());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm lịch sử chơi: " + e.getMessage());
        }
        return false;
    }

    public List<History> getHistoryByUserId(int userId) {
        List<History> histories = new ArrayList<>();
        String query = "SELECT * FROM History WHERE user_id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                histories.add(new History(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getInt("room_id"),
                    rs.getInt("score"),
                    rs.getTimestamp("play_time")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Loi khi lay lich su theo user_id: " + e.getMessage());
        }
        return histories;
    }

    public List<History> getHistoryByRoomId(int roomId) {
        List<History> histories = new ArrayList<>();
        String query = "SELECT * FROM History WHERE room_id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, roomId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                histories.add(new History(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getInt("room_id"),
                    rs.getInt("score"),
                    rs.getTimestamp("play_time")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy lịch sử theo room_id: " + e.getMessage());
        }
        return histories;
    }

    public boolean deleteHistoryById(int id) {
        String query = "DELETE FROM History WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa lịch sử chơi: " + e.getMessage());
        }
        return false;
    }
}