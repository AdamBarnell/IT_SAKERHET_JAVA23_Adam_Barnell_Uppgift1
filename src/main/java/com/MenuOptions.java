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

    // Lets the user input a new user to the database
    public void insertUser() {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter user name: ");
            String userName = scanner.nextLine();
            System.out.println("Enter user email: ");
            String email = scanner.nextLine();
            System.out.println("Enter user address: ");
            String address = scanner.nextLine();
            System.out.println("Do you consent to providing your age for database storage (Y/N)?");
            String consent = scanner.nextLine();
            Integer age = null;

            if (consent.equalsIgnoreCase("Y")) {
                System.out.println("Enter user age: ");
                age = scanner.nextInt();
                scanner.nextLine();
            } else {
                System.out.println("Age will not be stored.");
            }

            System.out.println("Enter password: ");
            String losenord = scanner.next();
            String password = password(losenord);

            String query = "INSERT INTO users (name, email, address, age, password) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, userName);
            pstmt.setString(2, email);
            pstmt.setString(3, address);

            if (age != null) {
                pstmt.setInt(4, age);
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }

            pstmt.setString(5, password);

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new user was inserted successfully!");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Lets the user view a specific user by name
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
                System.out.println("ID: " + rs.getInt("id"));
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Email: " + rs.getString("email"));
                System.out.println("Address: " + rs.getString("address"));
                Integer age = rs.getInt("age");
                if (!rs.wasNull()) {
                    System.out.println("Age: " + age);
                } else {
                    System.out.println("Age: Not provided");
                }
            } else {
                System.out.println("No user found with the name " + userName);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Deletes the user from the database based on the name
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
            System.out.println(e.getMessage());
        }
    }

    // Updates the user by request from the user (name, email, age, address, and age if they consent)
    public void updateUser() {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("Enter the username to update: ");
            String name = scanner.nextLine();

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
            System.out.println("Address: " + rs.getString("address"));
            Integer currentAge = rs.getInt("age");
            if (!rs.wasNull()) {
                System.out.println("Age: " + currentAge);
            } else {
                System.out.println("Age: No age in database");
            }

            System.out.println("What do you want to update?");
            System.out.println("1 = Name");
            System.out.println("2 = Email");
            System.out.println("3 = Address");
            System.out.println("4 = Age (optional)");
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
                    System.out.print("Enter new address: ");
                    String newAddress = scanner.nextLine();
                    updateQuery = "UPDATE users SET address = ? WHERE name = ?";
                    pstmt = con.prepareStatement(updateQuery);
                    pstmt.setString(1, newAddress);
                    pstmt.setString(2, name);
                    break;
                case 4:
                    System.out.println("Do you consent to providing your age for database storage (Y/N)?");
                    String ageConsent = scanner.nextLine();
                    if (ageConsent.equalsIgnoreCase("Y")) {
                        System.out.print("Enter new age: ");
                        int newAge = scanner.nextInt();
                        updateQuery = "UPDATE users SET age = ? WHERE name = ?";
                        pstmt = con.prepareStatement(updateQuery);
                        pstmt.setInt(1, newAge);
                        pstmt.setString(2, name);
                    } else {
                        System.out.println("Age will not be updated.");
                        return;
                    }
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
            System.out.println(e.getMessage());
        }
    }

    // Hashes the password.
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
            System.out.println(e.getMessage());
        }
        return null;
    }
}
