            // 创建用户表
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "account TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL, " +
                "rank TEXT, " +
                "winning_rate REAL)";
            stmt.execute(createUsersTable);