package cd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserGameUpdater {

    public void updateUserGameRecord(String account, int totalGames, int winGames) throws SQLException {
        String selectSql = "SELECT id FROM users WHERE account = ?";
        String updateSql = "UPDATE users SET total_games = ?, win_games = ?, winningrate = ? WHERE id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectSql);
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
            
            // 获取用户ID
            selectStmt.setString(1, account);
            ResultSet rs = selectStmt.executeQuery();
            
            if (!rs.next()) {
                throw new SQLException("未找到指定账号的用户。");
            }
            
            int userId = rs.getInt("id");
            
            // 计算胜率
            double winningrate = 0.0;
            if (totalGames > 0) {
                winningrate = (double) winGames / totalGames;
            }
            
            // 设置SQL参数
            updateStmt.setInt(1, totalGames);
            updateStmt.setInt(2, winGames);
            updateStmt.setDouble(3, winningrate);
            updateStmt.setInt(4, userId);
            
            // 执行更新
            int rowsAffected = updateStmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("用户游戏记录和胜率更新成功。");
            } else {
                System.out.println("未找到指定ID的用户。");
            }
        }
    }

    public UserGameUpdater() {

    }

}