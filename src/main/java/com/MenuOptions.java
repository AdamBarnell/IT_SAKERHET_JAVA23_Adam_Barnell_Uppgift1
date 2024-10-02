package com;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Scanner;

public class MenuOptions {
    private Connection con;

    public MenuOptions(Connection con) {
        this.con = con;
    }
    //lets the user input a new user to the database
    public void insertUser() {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter user name: ");
            String userName = scanner.nextLine();
            System.out.println("Enter user email: ");
            String email = scanner.nextLine();
            System.out.println("Enter user age: ");
            int age = scanner.nextInt();
            System.out.println("Enter password: ");
            String losenord = scanner.next();
            String password = password(losenord);

            String query = "INSERT INTO users (name, email, age, password) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, userName);
            pstmt.setString(2, email);
            pstmt.setInt(3, age);
            pstmt.setString(4, password);
            int rowsInserted = pstmt.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("A new user was inserted successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //Lets the user view a specific user through name query
    public void viewUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the user name to search for: ");
        String userName = scanner.nextLine();

        try {
            String query = "SELECT * FROM users WHERE name = ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, userName);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("User found:");
                System.out.println( "id: " +rs.getInt("id") + " Name:" + rs.getString("name") + " Email:" + rs.getString("email") + " Age:" + rs.getInt("age"));
            } else {
                System.out.println("No user found with the name " + userName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Deletes the user from the database based on the name
    public void deleteAllUsers() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the user name to delete: ");
        String name = scanner.next();
        try {
            String query = "DELETE FROM users WHERE name = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, name);
            int rowsDeleted = stmt.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Account is deleted with the name: " + name);
            } else {
                System.out.println("Username not found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //Updates the user by request from the user (name,email,age)
    public void updateUser() {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("Enter the username to update: ");
            String name = scanner.nextLine();
            scanner.nextLine();

            PreparedStatement checkUserStmt = con.prepareStatement("SELECT * FROM users WHERE name = ?");
            checkUserStmt.setString(1, name);
            ResultSet rs = checkUserStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("User with username " + name + " does not exist.");
                return;
            }

            System.out.println("Current user information: ");
            System.out.println("Name: " + rs.getString("name"));
            System.out.println("Email: " + rs.getString("email"));
            System.out.println("Age: " + rs.getInt("age"));

            System.out.println("What do you want to update?");
            System.out.println("1 = Name");
            System.out.println("2 = Email");
            System.out.println("3 = Age");
            System.out.print("Choose option: ");
            int updateOption = scanner.nextInt();
            scanner.nextLine();

            String updateQuery = "";
            PreparedStatement pstmt = null;
            switch (updateOption) {
                case 1:
                    System.out.print("Enter new name: ");
                    String newName = scanner.nextLine();
                    updateQuery = "UPDATE users SET name = ? WHERE name = ?";
                    pstmt = con.prepareStatement(updateQuery);
                    pstmt.setString(1, newName);
                    pstmt.setString(2, name);
                    break;
                case 2:
                    System.out.print("Enter new email: ");
                    String newEmail = scanner.nextLine();
                    updateQuery = "UPDATE users SET email = ? WHERE name = ?";
                    pstmt = con.prepareStatement(updateQuery);
                    pstmt.setString(1, newEmail);
                    pstmt.setString(2, name);
                    break;
                case 3:
                    System.out.print("Enter new age: ");
                    int newAge = scanner.nextInt();
                    updateQuery = "UPDATE users SET age = ? WHERE name = ?";
                    pstmt = con.prepareStatement(updateQuery);
                    pstmt.setInt(1, newAge);
                    pstmt.setString(2, name);
                    break;
                default:
                    System.out.println("Invalid option selected.");
                    return;
            }

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("User information updated successfully.");
            } else {
                System.out.println("Failed to update user information.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //Hashes the password.
    private String password(String losenord) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(losenord.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
