package org.example;

import javax.swing.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        // Read stored messages from JSON (static method)
        Message.readStoredMessagesFromJSON();

        System.out.println("Welcome, please create an account");
        Scanner scanner = new Scanner(System.in);
        Login login = new Login();
        int choice;
        String firstName = "";
        String lastName = "";

        while (true) {
            choice = displayMainMenu(scanner);

            switch (choice) {
                case 1:
                    firstName = prompt(scanner, "Please enter your first name:");
                    lastName = prompt(scanner, "Please enter your last name:");

                    handleRegistration(scanner, login);
                    break;

                case 2:
                    boolean success = handleLogin(scanner, login);
                    if (success) {
                        System.out.println("\n\nWelcome " + firstName + " " + lastName + ", it is great to see you again");
                        chatMenu(login);
                    } else {
                        System.out.println("Username or password incorrect, please try again");
                    }
                    break;

                case 0:
                    System.out.println("Good Bye");
                    return;

                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    // Main menu
    private static int displayMainMenu(Scanner scanner) {
        System.out.println("-------------------------");
        System.out.println("Login & Register");
        System.out.println("-------------------------");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("0. Exit");
        System.out.print("choice: ");
        return scanner.nextInt();
    }

    // Registration
    private static void handleRegistration(Scanner scanner, Login login) {
        System.out.println("Enter a username (maximum 5 characters, must contain an underscore):");
        login.setUsername(scanner.next());

        System.out.println("Enter a password (minimum 8 characters, must contain uppercase, digit, and special character):");
        login.setPassword(scanner.next());

        System.out.println("Enter a cellphone number that contains an international country code:");
        login.setCellphone(scanner.next());

        System.out.println(login.registerUser());
    }

    // Login
    private static boolean handleLogin(Scanner scanner, Login login) {
        System.out.println("Please provide your username for login:");
        String loginUsername = scanner.next();
        System.out.println("Please provide your password for login:");
        String loginPassword = scanner.next();

        return login.login(loginUsername, loginPassword);
    }

    // Prompt helper
    private static String prompt(Scanner scanner, String message) {
        System.out.println(message);
        return scanner.next();
    }

    // Chat menu
    public static void chatMenu(Login login) {
        JFrame frame = createChatFrame();
        String[] options = {"Send messages", "Show recently sent messages", "Manage Message Arrays", "Quit"};

        int choice = JOptionPane.showOptionDialog(
                frame,
                "Welcome to QuickChat",
                "QuickChat Menu",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        switch (choice) {
            case 0 -> sendMessages(frame, login);
            case 1 -> JOptionPane.showMessageDialog(frame, "Coming Soon");
            case 2 -> manageMessageArrays();
            case 3 -> System.exit(0);
        }
    }

    // Send messages
    private static void sendMessages(JFrame frame, Login login) {

        String messageCountStr = JOptionPane.showInputDialog(frame, "Enter number of messages to send (e.g., 5):");
        int messagesToSend = Integer.parseInt(messageCountStr);

        for (int i = 0; i < messagesToSend; i++) {
            Message messageRecp = new Message();
            String recipient = JOptionPane.showInputDialog(frame, "Enter recipient number (start with '+'):");

            if (!messageRecp.checkRecipientCell(recipient)) {
                JOptionPane.showMessageDialog(frame, "Invalid recipient, please try again.");
                i--;
                continue;
            }

            String messageText = JOptionPane.showInputDialog(frame, "Enter message #" + (i + 1) + ":");

            if (messageText == null || messageText.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Message cannot be empty.");
                i--;
                continue;
            }

            if (messageText.length() >= 250) {
                JOptionPane.showMessageDialog(frame, "Message too long, should be less than 250 characters.");
                i--;
                continue;
            }

            // Create Message object
            Message message = new Message(recipient, login.getCellphone(), messageText, i + 1);

            // Ask user what to do with message
            String[] msgOptions = {"send", "discard", "store"};
            int userChoice = JOptionPane.showOptionDialog(
                    frame,
                    "What do you wanna do?",
                    "Send Messages Menu",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    msgOptions,
                    msgOptions[0]
            );

            // Handle message (instance method)
            JOptionPane.showMessageDialog(frame, message.handleMessage(msgOptions[userChoice]));

            // Show total messages sent (static method)
            JOptionPane.showMessageDialog(frame, "You have sent: " + Message.returnTotalMessages() + " messages");
        }

        // Show all sent messages
        JOptionPane.showMessageDialog(frame, "All messages:\n" + Message.printMessages());
    }

    // Create JFrame for chat
    private static JFrame createChatFrame() {
        JFrame frame = new JFrame();
        frame.setAlwaysOnTop(true);
        frame.setUndecorated(true);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        return frame;
    }

    // Manage message arrays
    public static void manageMessageArrays() {
        JFrame frame = createManagementFrame();
        boolean system = true;

        while (system) {
            String[] options = {
                    "Display senders & recipients",
                    "Display longest message",
                    "Search message by ID",
                    "Search messages by recipient",
                    "Delete message by hash",
                    "Display full report",
                    "Read stored messages (JSON)",
                    "Exit"
            };

            int choice = JOptionPane.showOptionDialog(
                    frame,
                    "Select an option:",
                    "Message Array Management",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            switch (choice) {
                case 0 -> Message.displaySendersAndRecipients();
                case 1 -> Message.displayLongestMessage();
                case 2 -> {
                    String id = JOptionPane.showInputDialog(frame, "Enter Message ID:");
                    if (id != null) Message.searchByMessageId(id);
                }
                case 3 -> {
                    String recipient = JOptionPane.showInputDialog(frame, "Enter Recipient Number:");
                    if (recipient != null) Message.searchByRecipient(recipient);
                }
                case 4 -> {
                    String hash = JOptionPane.showInputDialog(frame, "Enter Message Hash:");
                    if (hash != null) Message.deleteByHash(hash);
                }
                case 5 -> Message.displayReport();
                case 6 -> Message.readStoredMessagesFromJSON();
                case 7 -> system = false;
                default -> JOptionPane.showMessageDialog(frame, "Invalid choice. Try again.");
            }
        }
    }

    // Create JFrame for management menu
    private static JFrame createManagementFrame() {
        JFrame frame = new JFrame();
        frame.setAlwaysOnTop(true);
        frame.setUndecorated(true);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        return frame;
    }
}
