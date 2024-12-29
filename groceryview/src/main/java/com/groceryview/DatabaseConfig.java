package com.groceryview;

import java.io.File;
import java.sql.*;

/* 
* Class containing the database configuration to manage two tables:
* the first table stores the receipt information and the second table stores the receipt items
*/
public class DatabaseConfig {
    public static final String DB_URL = "jdbc:sqlite:groceryview.db";
    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                // SQLite JDBC driver
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(DB_URL);
                
                // Enable foreign keys
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute("PRAGMA foreign_keys = ON");
                }
                
                System.out.println("SQLite database connected successfully");
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    // DAO (data access object) class for the receipts
    public static class ReceiptDAO {
        public void createTable() {
            String sql = """
                CREATE TABLE IF NOT EXISTS receipts (
                    receipt_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    date_added TEXT NOT NULL,
                    total REAL NOT NULL
                )
            """;
            
            try (Statement stmt = getConnection().createStatement()) {
                stmt.execute(sql);
                System.out.println("Receipts table created successfully");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public int insertReceipt(Receipt receipt) {
            String sql = "INSERT INTO receipts (date_added, total) VALUES (?, ?)";
            try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
                pstmt.setString(1, receipt.getReceiptDate());
                pstmt.setFloat(2, receipt.getTotalPaid());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            // Retrieve the generated receipt_id using LAST_INSERT_ROWID()
            String lastInsertIdSql = "SELECT LAST_INSERT_ROWID() AS receipt_id";
            int receiptId = -1;
            try (Statement lastInsertIdStmt = connection.createStatement()) {
                ResultSet resultSet = lastInsertIdStmt.executeQuery(lastInsertIdSql);
                if (resultSet.next()) {
                    receiptId = resultSet.getInt("receipt_id");
                    System.out.println("Generated receipt_id: " + receiptId);
                } else {
                    throw new SQLException("Failed to retrieve receipt_id.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return receiptId;
        }
    }
    
    // DAO (data access object) class for the receipt items
    public static class ReceiptItemDAO {
        public void createTable() {
            String sql = """
                CREATE TABLE IF NOT EXISTS receiptitems (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    receipt_id INTEGER NOT NULL,
                    name TEXT NOT NULL,
                    vat REAL NOT NULL,
                    price REAL NOT NULL
                )
            """;
            
            try (Statement stmt = getConnection().createStatement()) {
                stmt.execute(sql);
                System.out.println("ReceiptItems table created successfully");

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public void insertReceiptItem(Receipt.ReceiptItem receiptItem, int receiptId) {
            String sql = "INSERT INTO receiptitems (receipt_id, name, vat, price) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
                pstmt.setInt(1, receiptId);
                pstmt.setString(2, receiptItem.getName());
                pstmt.setString(3, receiptItem.getVat());
                pstmt.setFloat(4, receiptItem.getPrice());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
