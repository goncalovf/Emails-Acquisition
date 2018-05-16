package com.emailsacquisition;

import java.sql.*;

public class DBConnector {

    public DBConnector() {

    }

    public Connection connect() {

        Connection c = null;

        try {

            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:goncalo.db");

        } catch (Exception e) {

            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);

        }

        return c;

    }

    public boolean writeMail(String mail) {

        Connection c = connect();
        Statement stmt;

        try {

            stmt = c.createStatement();
            String sql = "INSERT INTO emails (email) " +
                    "VALUES (" + mail + ");";
            stmt.executeUpdate(sql);

            stmt.close();
            c.commit();
            c.close();

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }

        return true;
    }

}