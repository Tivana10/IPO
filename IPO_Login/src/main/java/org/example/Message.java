package org.example;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import org.json.JSONObject;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Message {

    private String messageId;
    private String recipient;
    private String sender;
    private String messageText;
    private String messageHash;
    private int messageNumber;

    private static int totalMessagesSent = 0;

    public static final ArrayList<Message> sentMessages = new ArrayList<>();
    public static final ArrayList<Message> storedMessagesList = new ArrayList<>();
    public static final ArrayList<String> messageHashes = new ArrayList<>();
    public static final ArrayList<String> messageIDs = new ArrayList<>();

    // ================= CONSTRUCTORS =================

    public Message(String recipient, String sender, String messageText, int messageNumber) {
        this.recipient = recipient;
        this.sender = sender;
        this.messageText = messageText;
        this.messageNumber = messageNumber;
        this.messageId = generateMessageId();
        this.messageHash = createMessageHash();
    }

    public Message() {}

    // ================= GETTERS & SETTERS =================

    public String getRecipient() {
        return recipient;
    }

    public String setRecipient(String recipient) {
        if (recipient == null) return " ";
        if (checkRecipientCell(recipient)) {
            this.recipient = recipient;
            return "Cell number successfully captured";
        }
        return "Cell number is incorrectly formatted or does not contain an international code.";
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessage(String messageText) {
        this.messageText = messageText;
    }

    public String setMessageText(String messageText) {
        if (checkMessageLength(messageText)) {
            this.messageText = messageText;
            return "Message ready to send";
        }
        return "Message exceeds 250 characters by " + (messageText.length() - 250);
    }

    public String getMessageHash() {
        return messageHash;
    }

    public int getMessageNumber() {
        return messageNumber;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    // ================= VALIDATION =================

    public boolean checkRecipientCell(String recipient) {
        return recipient.startsWith("+") && recipient.length() == 12;
    }

    public boolean checkMessageLength(String text) {
        return text.length() <= 250;
    }

    // ================= ID & HASH =================

    public String generateMessageId() {
        String id = String.format("%010d", (long) (Math.random() * 1_000_000_0000L));
        return id;
    }

    public String createMessageHash() {
        String[] words = messageText.split(" ");
        String first = words[0].toUpperCase();
        String last = words[words.length - 1].toUpperCase();
        return messageId.substring(0, 2) + ":" + messageNumber + ":" + first + last;
    }

    // ================= MESSAGE ACTIONS =================

    public String handleMessage(String option) {

        messageIDs.add(messageId);
        messageHashes.add(messageHash);

        switch (option.toLowerCase()) {
            case "send":
                sentMessages.add(this);
                totalMessagesSent++;
                return "Message sent";

            case "store":
                storeMessage(this);
                return "Message successfully stored";

            case "discard":
                discard();
                return "Message discarded";

            default:
                return "Invalid option";
        }
    }

    private void discard() {
        messageId = null;
        messageHash = null;
        messageText = null;
        recipient = null;
        messageNumber = 0;
    }

    public static int returnTotalMessages() {
        return totalMessagesSent;
    }

    // Add these to your existing Message class

// ================= DISPLAY METHODS =================

    // Display all senders and recipients
    public static void displaySendersAndRecipients() {
        if (sentMessages.isEmpty()) {
            System.out.println("No sent messages.");
            return;
        }
        for (Message m : sentMessages) {
            System.out.println("Sender: " + m.sender + " -> Recipient: " + m.recipient);
        }
    }

    // Display the longest message
    public static String displayLongestMessage() {
        if (sentMessages.isEmpty()) {
            System.out.println("No sent messages.");
            return "No messages sent";
        }
        Message longest = sentMessages.get(0);
        for (Message m : sentMessages) {
            if (m.messageText.length() > longest.messageText.length()) {
                longest = m;
            }
        }
        System.out.println("Longest message: " + longest.messageText);
        return longest.messageText;
    }

    // Search messages by recipient
    public static void searchByRecipient(String recipient) {
        boolean found = false;
        for (Message m : sentMessages) {
            if (m.recipient.equalsIgnoreCase(recipient)) {
                System.out.println(m);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No messages found for recipient: " + recipient);
        }
    }

    // Display all messages as a report
    public static void displayReport() {
        if (sentMessages.isEmpty()) {
            System.out.println("No sent messages.");
            return;
        }
        System.out.println("===== FULL MESSAGE REPORT =====");
        for (Message m : sentMessages) {
            System.out.println(m);
        }
    }

    // Print all messages in a simple list (used in Main chat)
    public static String printMessages() {
        if (sentMessages.isEmpty()) return "No messages sent";
        StringBuilder sb = new StringBuilder();
        int counter = 1;
        for (Message m : sentMessages) {
            sb.append("Message ").append(counter).append(": ").append(m.messageText).append("\n");
            counter++;
        }
        return sb.toString();
    }


    // ================= JSON =================

    private JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.put("Message_ID", messageId);
        obj.put("Recipient", recipient);
        obj.put("Sender", sender);
        obj.put("Message", messageText);
        obj.put("Message_Hash", messageHash);
        obj.put("Message_Number", messageNumber);
        return obj;
    }

    public boolean storeMessage(Message message) {

        storedMessagesList.add(message);
        JsonArray array = new JsonArray();

        for (Message m : storedMessagesList) {
            array.add(m.toJson());
        }

        try (FileWriter writer = new FileWriter("messages.json")) {
            writer.write(array.toString());
            return true;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static void readStoredMessagesFromJSON() {

        try {
            String content = new String(Files.readAllBytes(Paths.get("messages.json")));
            JsonArray array = (JsonArray) Jsoner.deserialize(content);

            System.out.println("===== MESSAGES FROM JSON =====");
            for (Object obj : array) {
                JSONObject json = (JSONObject) obj;
                Message m = fromJson(json);
                System.out.println(m);
            }

        } catch (Exception e) {
            System.out.println("Error reading JSON: " + e.getMessage());
        }
    }

    private static Message fromJson(JSONObject json) {
        Message m = new Message();
        m.messageId = (String) json.get("Message_ID");
        m.recipient = (String) json.get("Recipient");
        m.sender = (String) json.get("Sender");
        m.messageText = (String) json.get("Message");
        m.messageHash = (String) json.get("Message_Hash");
        m.messageNumber = ((Long) json.get("Message_Number")).intValue();
        return m;
    }

    // ================= SEARCH & REPORT =================

    public static Message searchByMessageId(String id) {
        for (Message m : sentMessages) {
            if (m.messageId.equals(id)) return m;
        }
        return null;
    }

    public static String deleteByHash(String hash) {
        Iterator<Message> it = sentMessages.iterator();
        while (it.hasNext()) {
            Message m = it.next();
            if (m.messageHash.equals(hash)) {
                it.remove();
                return "Message deleted";
            }
        }
        return "Message not found";
    }

    @Override
    public String toString() {
        return "Message{" +
                "ID='" + messageId + '\'' +
                ", Recipient='" + recipient + '\'' +
                ", Sender='" + sender + '\'' +
                ", Text='" + messageText + '\'' +
                ", Hash='" + messageHash + '\'' +
                ", Number=" + messageNumber +
                '}';
    }
}
