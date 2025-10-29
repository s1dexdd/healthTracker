package com.healthtracker.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConfig {
    private static final String DB_URL ="jdbc:h2:~/health_tracker_db";
    private static final String DB_USER ="sa";
    private static final String DB_PASSWORD="sa";
    public static Connection getConnection() throws SQLException{
        return DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWORD);
    }
}
