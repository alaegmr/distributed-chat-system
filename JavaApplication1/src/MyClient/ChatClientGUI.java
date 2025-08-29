package MyClient;

import java.awt.event.KeyEvent;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

public class ChatClientGUI extends javax.swing.JFrame {
    private static final String SERVER_HOST = "127.0.0.1";
    private static final int SERVER_PORT = 5555;

    private PrintWriter out;
    private Scanner in;
    private DefaultListModel<String> userListModel;

    private javax.swing.JButton broadcastButton;
    private javax.swing.JButton multicastButton;
    private javax.swing.JButton unicastButton; // Nouveau bouton pour les messages unicast
    private javax.swing.JTextArea chatArea;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField messageField;
    private javax.swing.JList<String> userList;
    private javax.swing.JLabel userListLabel;
    private javax.swing.JScrollPane userListScrollPane;

    public ChatClientGUI() {
        initComponents();
        userListModel = new DefaultListModel<>();
        userList.setModel(userListModel);

        // Prompt for username
        String username = JOptionPane.showInputDialog(this, "Enter your username:");
        if (username != null && !username.isEmpty()) {
            connectToServer(username);
            receiveMessages();
            setFocusOnMessageField();
        } else {
            // Handle case where the user cancels or provides an empty username
            JOptionPane.showMessageDialog(this, "Username cannot be empty. Exiting.");
            System.exit(0);
        }
    }

    private void connectToServer(String username) {
        try {
            Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);

            // Send the username to the server
            out.println(username);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateUserList(String userListString) {
        String[] users = userListString.split(",");
        userListModel.clear();
        for (String user : users) {
            userListModel.addElement(user);
        }
    }

    private void receiveMessages() {
        new Thread(() -> {
            while (in.hasNextLine()) {
                String message = in.nextLine();
                System.out.println("Received message: " + message);

                if (message.startsWith("[UserList]")) {
                    String userListString = message.substring("[UserList]".length());
                    System.out.println("Received user list: " + userListString);
                    updateUserList(userListString);
                } else {
                    chatArea.append(message + "\n");
                }
            }
        }).start();
    }

    private void handleIncomingMessage(String message) {
        chatArea.append(message + "\n");
    }

    private void sendMessage(String recipient) {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            out.println(recipient + "|" + message);
            messageField.setText("");
        }
    }

    private void sendMulticast() {
        List<String> selectedUsersList = userList.getSelectedValuesList();

        if (!selectedUsersList.isEmpty()) {
            String selectedUsers = String.join(",", selectedUsersList);
            out.println("multicast|" + selectedUsers + "|" + messageField.getText());
            messageField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Select users for multicast.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sendBroadcast() {
        out.println("broadcast|" + messageField.getText());
        messageField.setText("");
    }

    // Nouvelle méthode pour envoyer des messages unicast
    private void sendUnicast() {
        String recipient = JOptionPane.showInputDialog(this, "Enter recipient username:");
        if (recipient != null && !recipient.isEmpty()) {
            sendMessage(recipient);
        } else {
            JOptionPane.showMessageDialog(this, "Recipient username cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initComponents() {
        jScrollPane1 = new javax.swing.JScrollPane();
        chatArea = new javax.swing.JTextArea();
        messageField = new javax.swing.JTextField();
        userListLabel = new javax.swing.JLabel();
        userListScrollPane = new javax.swing.JScrollPane();
        userList = new javax.swing.JList<>();
        multicastButton = new javax.swing.JButton();
        broadcastButton = new javax.swing.JButton();
        unicastButton = new javax.swing.JButton(); // Nouveau bouton pour les messages unicast

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Chat Client");

        chatArea.setEditable(false);
        chatArea.setColumns(20);
        chatArea.setRows(5);
        jScrollPane1.setViewportView(chatArea);

        messageField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendMessage("");
            }
        });

        userListLabel.setText("Connected Users:");

        userListScrollPane.setViewportView(userList);

        multicastButton.setText("Multicast");
        multicastButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                multicastButtonActionPerformed(evt);
            }
        });

        broadcastButton.setText("Broadcast");
        broadcastButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                broadcastButtonActionPerformed(evt);
            }
        });

        unicastButton.setText("Unicast"); // Nouveau bouton pour les messages unicast
        unicastButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                unicastButtonActionPerformed(evt);
            }
        });

        messageField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                messageFieldKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                            .addComponent(messageField))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(multicastButton, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(broadcastButton, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(unicastButton, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))) // Nouveau bouton pour les messages unicast
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(userListLabel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(userListScrollPane))
                .addContainerGap())
        );

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(userListLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(userListScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(messageField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(unicastButton)) // Nouveau bouton pour les messages unicast
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(multicastButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(broadcastButton)
                .addContainerGap())
        );

        pack();
    }

    private void multicastButtonActionPerformed(java.awt.event.ActionEvent evt) {
        sendMulticast();
    }

    private void broadcastButtonActionPerformed(java.awt.event.ActionEvent evt) {
        sendBroadcast();
    }

    private void unicastButtonActionPerformed(java.awt.event.ActionEvent evt) {
        sendUnicast();
    }

    private void messageFieldKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            sendMessage("");
        }
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            new ChatClientGUI().setVisible(true);
        });
    }

    public void setFocusOnMessageField() {
        messageField.requestFocusInWindow();
    }
}
