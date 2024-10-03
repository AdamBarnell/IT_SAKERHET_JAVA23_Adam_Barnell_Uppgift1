package com;

import java.sql.*;
import java.util.Scanner;
public class Main {
    public static Connection connect() {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/databassecurity", "adam", "adam");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return con;
    }

    public static void main(String[] args) throws SQLException {
        String choice;
        Scanner scanner = new Scanner(System.in);

        Connection con = connect();
        if (con == null) {
            System.out.println("Connection failed!");
            return;
        }

        MenuOptions menuOptions = new MenuOptions(con);

        while (true) {
            System.out.println("1 = add user: ");
            System.out.println("2 = check user:");
            System.out.println("3 = delete user: ");
            System.out.println("4 = change user information: ");
            System.out.println("5 = Exit the program: ");
            System.out.println("Select something: ");
            choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    menuOptions.insertUser();
                    scanner.nextLine();
                    break;
                case "2":
                    menuOptions.viewUser();
                    scanner.nextLine();
                    break;
                case "3":
                    menuOptions.deleteAllUsers();
                    scanner.nextLine();
                    break;
                case "4":
                    menuOptions.updateUser();
                    scanner.nextLine();
                    break;
                case "5":
                    System.out.println("Exiting...");
                    con.close();
                    return;
                default:
                    System.out.println("Invalid choice");
                    break;
            }
        }
    }
}
