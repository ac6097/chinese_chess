package cd;

import java.sql.SQLException;

public class Updater {
    public static void main(String[] args) throws SQLException {
        UserGameUpdater updater = new UserGameUpdater();
        updater.updateUserGameRecord("2024141460192",1,0);
    }
}
