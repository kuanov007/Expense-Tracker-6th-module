package service;

import constant.Tools;
import entity.User;

import java.sql.SQLException;
import java.util.Optional;

public class AuthService {


    public static Optional<User> auth() throws SQLException {
        while (true) {
            System.out.print("""
                    [Welcome to Auth Service]
                    1 -> Register
                    2 -> Login
                    
                    0 -> exit
                    >>>""");

            switch (Tools.intScanner.nextInt()) {
                case 1 -> {
                    System.out.println("Please enter your username: ");
                    String username = Tools.strScanner.nextLine();
                    System.out.println("Please enter your phone number: ");
                    String phoneNumber = Tools.strScanner.nextLine();
                    System.out.println("Please enter your password: ");
                    String password = Tools.strScanner.nextLine();

                    Optional<User> user = Tools.dbHelper.register(username, phoneNumber, password);
                    if (user.isPresent()) {
                        System.out.println("User registered successfully!");
                        return user;
                    }
                    System.err.println("This phone number is already in use!");
                }
                case 2 -> {
                    System.out.println("Please enter your phone number: ");
                    String phoneNumber = Tools.strScanner.nextLine();
                    System.out.println("Please enter your password: ");
                    String password = Tools.strScanner.nextLine();

                    Optional<User> user = Tools.dbHelper.login(phoneNumber, password);
                    if (user.isPresent()) {
                        System.out.println("You have successfully logged in!");
                        return user;
                    }
                    System.err.println("Invalid phone number or password!");
                }
                case 0 -> {
                    return Optional.empty();
                }
                default -> {
                    System.out.println("Invalid input!");
                }
            }
        }
    }
}
