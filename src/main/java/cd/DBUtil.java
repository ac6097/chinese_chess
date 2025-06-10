package cd;


import java.sql.*;

/**
 * 数据库连接工具类
 */
public class DBUtil {
    // 数据库连接URL（默认端口3306，数据库名为 chess）
    private static final String URL = "jdbc:mysql://localhost:3306/chess?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&authenticationPlugin=com.mysql.cj.protocol.a.authentication.MysqlNativePasswordPlugin";
    private static final String USER = "root"; // 默认用户名为 root
    private static final String PASSWORD = "Ykc240607"; // 用户提供的密码

    /**
     * 获取数据库连接
     * @return Connection 数据库连接对象
     * @throws SQLException 如果连接失败则抛出异常
     */
    public static Connection getConnection() throws SQLException {
        System.out.println("正在尝试连接到数据库...");
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        System.out.println("数据库连接成功。");
        return conn;
    }

    /**
     * 保存用户信息到数据库
     * @param user 要保存的用户对象
     * @throws SQLException 如果保存失败则抛出异常
     */
    public static void saveUser(User user) throws SQLException {
        String sql = "INSERT INTO users (`account`, `password`, `ranking`, `total_games`, `win_games`) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getAccount());
            stmt.setString(2, user.getPassword());
            // 增加null检查以避免NullPointerException
            stmt.setString(3, user.getRanking() == null ? "" : user.getRanking().replaceAll("\u3000", " "));
            stmt.setInt(4, user.getTotalGames());
            stmt.setInt(5, user.getWinGames());
            stmt.executeUpdate();
            
            // 获取生成的自增id并设置到user对象中
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                user.setId(generatedKeys.getInt(1));
            }
        }
    }

    /**
     * 根据用户名加载用户信息
     * @param account 用户名
     * @return User 对象，如果未找到返回 null
     * @throws SQLException 如果查询过程中发生错误
     */
    public static User loadUser(String account) throws SQLException {
        String sql = "SELECT * FROM users WHERE account = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, account);
            ResultSet rs = stmt.executeQuery();
            
            System.out.println("[DEBUG] 正在查询账户: " + account);
            
            if (rs.next()) {
                System.out.println("[DEBUG] 成功找到用户: " + account);
                User user = new User();
                user.setAccount(rs.getString("account"));
                user.setPassword(rs.getString("password"));
                user.setRanking(rs.getString("ranking"));
                user.setTotalGames(rs.getInt("total_games"));
                user.setWinGames(rs.getInt("win_games"));
                user.setWinningRate(rs.getDouble("winningrate"));
                return user;
            } else {
                System.out.println("[DEBUG] 未找到用户: " + account);
            }
        }
        return null;
    }
}