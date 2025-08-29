package MyClient;

public class Main {

public static void main(String args[]) {
    java.awt.EventQueue.invokeLater(() -> {
        ChatClientGUI chatClientGUI = new ChatClientGUI();
        chatClientGUI.setVisible(true);
        chatClientGUI.setFocusOnMessageField();
    });
}

}
