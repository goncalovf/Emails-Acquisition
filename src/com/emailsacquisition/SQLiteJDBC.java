package com.emailsacquisition;

import java.sql.*;

public class SQLiteJDBC {

    public static Connection connectToDB() {
        Connection c = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:ExplicasmeTutors.db");
            System.out.println("Opened database successfully");
            return c;
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
            return null;
        }
    }

    public static void insertToDB( String recordType, String[] recordData) {
        Connection c = null;
        Statement stmt = null;
        String rowToBeInserted;
        if (recordType.equals("tutors")) {
            String tutorType    = recordData[0];
            String fullName     = recordData[1];
            String firstName    = recordData[2];
            String lastName     = recordData[3];
            String profileUrl   = recordData[4];
            String cvUrl        = recordData[5];
            String email        = recordData[6];
            rowToBeInserted = String.format("('%s', '%s', '%s', '%s', '%s', '%s', '%s');", tutorType, fullName, firstName, lastName, profileUrl, cvUrl, email);
        } else {
            String location     = recordData[0];
            String subject      = recordData[1];
            rowToBeInserted = String.format("('%s', '%s');", location, subject);
        }
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:C:\\Program Files\\sqlite\\ExplicasmeTutors.db");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            String sql = "INSERT INTO " + recordType + " VALUES " + rowToBeInserted;
            stmt.executeUpdate(sql);

            stmt.close();
            c.commit();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Records created successfully");
    }
}
