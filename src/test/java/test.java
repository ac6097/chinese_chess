import cd.User;
import cd.DBUtil;
import java.sql.SQLException;

public class test {
    public static void main(String[] args) {
        User user = new User();
        user.setAccount("test_user");
        user.setPassword("test_password");
        user.setRanking("黄金段位");
        user.setWinningRate(0.75);
        try {
            DBUtil.saveUser(user);
            System.out.println("用户数据保存成功！");
        } catch (SQLException e) {
            System.err.println("保存用户数据时发生错误: " + e.getMessage());
        }
    }
}