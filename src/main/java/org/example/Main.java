package org.example;

import org.example.Dao.UserDao;
import org.example.Dao.UserDaoImplements;
import org.example.entity.User;
import org.example.util.HibernateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final UserDao userDao = new UserDaoImplements();
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

            User user = new User(name, email, age);
            userDao.save(user);
            System.out.println("User created");
        } catch (NumberFormatException e) {
            System.out.println("Age must be a number");
        }
    }

    private static void showAllUsers() {
        List<User> users = userDao.findAll();
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

            userDao.findById(id).ifPresentOrElse(
                    user -> System.out.println("Found: " + user),
                    () -> System.out.println("User with ID " + id + " not found")
            );
        } catch (NumberFormatException e) {
            System.out.println("ID must be a number");
        }
    }

    private static void updateUser() {
        try {
            System.out.print("User ID for update: ");
            Long id = Long.parseLong(scanner.nextLine());

            userDao.findById(id).ifPresentOrElse(user -> {
                System.out.println("Old user info: " + user);

                System.out.print("New name (Enter — skip): ");
                String newName = scanner.nextLine();
                if (!newName.isEmpty()) user.setName(newName);

                System.out.print("New email (Enter — skip): ");
                String newEmail = scanner.nextLine();
                if (!newEmail.isEmpty()) user.setEmail(newEmail);

                System.out.print("New возраст (Enter — skip): ");
                String newAge = scanner.nextLine();
                if (!newAge.isEmpty()) user.setAge(Integer.parseInt(newAge));

                userDao.update(user);
                System.out.println("Updated");
            }, () -> System.out.println("User not found"));
        } catch (NumberFormatException e) {
            System.out.println("Incorrect enter");
        }
    }

    private static void deleteUser() {
        try {
            System.out.print("ID for delete: ");
            Long id = Long.parseLong(scanner.nextLine());
            userDao.delete(id);
            System.out.println("User deleted");
        } catch (NumberFormatException e) {
            System.out.println("ID must be a number");
        }
    }
}