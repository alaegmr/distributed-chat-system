package MyServer;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ServerT {
    private static final int PORT = 5555;
    private static Map<String, PrintWriter> clients = new HashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected");

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                Scanner in = new Scanner(clientSocket.getInputStream());
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                out.println("Enter your username:");
                String username = in.nextLine();
                System.out.println("New user connected: " + username);

                clients.put(username, out);

                sendUserListUpdate();

                while (true) {
                    String message = in.nextLine();
                    String[] parts = message.split("\\|", 2);

                    if (parts.length == 2) {
                        String recipient = parts[0];
                        String content = parts[1];

                        if (recipient.equals("multicast")) {
                            handleMulticastMessage(username, content);
                        } else if (recipient.equals("broadcast")) {
                            // Broadcast message handling
                            broadcastMessage("[Broadcast][" + username + "]: " + content);
                        } else if (clients.containsKey(recipient)) {
                            // One-to-one message handling
                            PrintWriter recipientWriter = clients.get(recipient);
                            recipientWriter.println(username + ": " + content);
                        } else {
                            out.println("[Server] Recipient not found.");
                        }

                        sendUserListUpdate();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (out != null) {
                    clients.values().remove(out);
                    sendUserListUpdate();
                }
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void broadcastMessage(String message) {
            for (PrintWriter clientWriter : clients.values()) {
                clientWriter.println(message);
            }
        }

        private void sendUserListUpdate() {
            StringBuilder userListStringBuilder = new StringBuilder("[UserList]");
            for (String user : clients.keySet()) {
                userListStringBuilder.append(user).append(",");
            }
            String userListString = userListStringBuilder.toString();
            broadcastMessage(userListString);
        }

private void handleMulticastMessage(String sender, String message) {
    String[] multicastParts = message.split("\\|", 2);

    if (multicastParts.length == 2) {
        String[] recipients = multicastParts[0].split(",");
        String multicastContent = multicastParts[1];

        for (String recipientUser : recipients) {
            String trimmedRecipient = recipientUser.trim();  // Trim to remove leading/trailing spaces
            if (clients.containsKey(trimmedRecipient) && !trimmedRecipient.equals(sender)) {
                PrintWriter recipientWriter = clients.get(trimmedRecipient);
                recipientWriter.println("[Multicast][" + sender + "]: " + multicastContent);
            } else {
                out.println("[Server] Recipient " + trimmedRecipient + " not found or cannot multicast to self.");
            }
        }
    } else {
        out.println("[Server] Invalid multicast message format.");
    }
}

    }
}