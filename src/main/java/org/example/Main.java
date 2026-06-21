package org.example;

import org.example.Dao.UserDao;
import org.example.Dao.UserDaoImplements;
import org.example.entity.User;
import org.example.service.UserService;
import org.example.service.UserServiceImplements;
import org.example.util.HibernateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final UserDao userDao = new UserDaoImplements();
    private static final UserService userService = new UserServiceImplements(userDao);
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        logger.info("Starting");
        boolean running = true;
        while (running) {
            System.out.println();
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": {
                    createUser();
                    break;
                }
                case "2": {
                    showAllUsers();
                    break;
                }
                case "3": {
                    findUserById();
                    break;
                }
                case "4": {
                    updateUser();
                    break;
                }
                case "5": {
                    deleteUser();
                    break;
                }
                case "6": {
                    running = false;
                    logger.info("Exiting");
                }
                default: System.out.println("Error. Enter a number from 1 to 6.");
            }
        }
        HibernateUtil.shutdown();
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("1. Create user");
        System.out.println("2. Show all users");
        System.out.println("3. Find by ID");
        System.out.println("4. Update user");
        System.out.println("5. Delete user");
        System.out.println("6. Exit");
        System.out.print("Choose number: ");
    }

    private static void createUser() {
        try {
            System.out.print("Name: ");
            String name = scanner.nextLine();
            System.out.print("Email: ");
            String email = scanner.nextLine();
            System.out.print("Age: ");
            int age = Integer.parseInt(scanner.nextLine());

            User user = userService.createUser(name, email, age);
            System.out.println("User created");
        } catch (NumberFormatException e) {
            System.out.println("Age must be a number");
        } catch (IllegalArgumentException e) {
            System.out.println("Validation error: " + e.getMessage());
        }
    }

    private static void showAllUsers() {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("User list is empty");
        } else {
            System.out.println("\nUsers:");
            users.forEach(System.out::println);
        }
    }

    private static void findUserById() {
        try {
            System.out.print("Enter ID: ");
            Long id = Long.parseLong(scanner.nextLine());

            userService.getUserById(id).ifPresentOrElse(
                    user -> System.out.println("Found: " + user),
                    () -> System.out.println("User with ID " + id + " not found")
            );
        } catch (NumberFormatException e) {
            System.out.println("ID must be a number");
        } catch (IllegalArgumentException e) {
            System.out.println("Error " + e.getMessage());
        }
    }

    private static void updateUser() {
        try {
            System.out.print("User ID for update: ");
            Long id = Long.parseLong(scanner.nextLine());

            System.out.print("New name (Enter — skip): ");
            String name = scanner.nextLine();
            System.out.print("New email (Enter — skip): ");
            String email = scanner.nextLine();
            System.out.print("New возраст (Enter — skip): ");
            String ageStr = scanner.nextLine();
            Integer age = ageStr.isBlank() ? null : Integer.parseInt(ageStr);
            User updated = userService.updateUser(id, name, email, age);
            System.out.println("Updated");
        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }
    }

    private static void deleteUser() {
        try {
            System.out.print("ID for delete: ");
            Long id = Long.parseLong(scanner.nextLine());
            if (userService.deleteUser(id)) {
            System.out.println("User deleted");
            } else {
                System.out.println("User not found");
            }
        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }
    }
}