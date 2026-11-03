package com.vtu.examsystem.util;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

    private static final Properties CFG = new Properties();

    static {
        try (FileInputStream in = new FileInputStream("config/db.properties")) {
            CFG.load(in);
        } catch (Exception e) {
            throw new RuntimeException("Missing config/db.properties. Copy config/db.properties.example "
                    + "to config/db.properties and fill in your MySQL details.", e);
        }
    }

    public static Connection get() throws SQLException {
        String url = "jdbc:mysql://localhost:" + CFG.getProperty("port", "3306")
                + "/vtu_exam?useSSL=false&allowPublicKeyRetrieval=true";
        return DriverManager.getConnection(url, CFG.getProperty("user", "root"), CFG.getProperty("password", ""));
    }
}