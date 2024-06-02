package org.example.proyectofinal;

import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.example.proyectofinal.Constants.DataConstants;
import org.example.proyectofinal.VideoCall.VideoObj;
import org.example.proyectofinal.VideoCall.ChatObj;
import org.example.proyectofinal.VideoCall.audio.AudioService;
import org.example.proyectofinal.FIle.File;
import org.example.proyectofinal.Message.MessageCli;
import org.example.proyectofinal.Message.MessageSend;
import org.example.proyectofinal.VideoCall.VideoCallService;

import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Map;

public class AppController {

    @FXML
    private ListView<String> contactListView;

    private Map<String, ObservableList<String>> chatMessages;

    private ObservableSet<String> contacts;
    @FXML
    private String activeChat;

    @FXML
    private VBox chatPanel;
    private ChatObj chatStatus;
    @FXML
    private ListView<String> messageListView;

    @FXML
    private TextField messageInput;
    @FXML
    private Button sendMessageButton;
    @FXML
    private Button endCallButton;

    private MessageCli messageReceiver;

    // Add Contact
    @FXML
    private TextField newContactInput;
    @FXML
    private Button addContactButton;

    // File Transfer
    @FXML
    private Button fileUploadButton;
    @FXML
    private FileChooser fileChooser;

    private final File fileService = new File();

    // Video Call
    @FXML
    private VBox videoCallPanel;
    private VideoObj videoStatus;
    @FXML
    private ImageView senderVideoView;
    @FXML
    private ImageView receiverVideoView;
    private VideoCallService videoCallService;
    private AudioService audioService;

    @FXML
    private Label activeContactLabel;

    public void initialize() {
        configureContactList();
        chatMessages = new HashMap<>();
        initializeMessageReceiver();
        chatStatus = new ChatObj();
        videoStatus = new VideoObj();
        chatPanel.setVisible(true);
        videoCallPanel.visibleProperty().bind(videoStatus.isAbleToSeeProperty());
        endCallButton.setVisible(false);
    }

    public void initializeMessageReceiver() {
        try {
            DatagramSocket socket = new DatagramSocket(DataConstants.IN_MESSAGE_PORT);
            messageReceiver = new MessageCli(socket, chatMessages, contacts);
            messageReceiver.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initiateVideoCall() {
        if (activeChat == null) {
            return;
        }
        terminateVideoCall();
        videoStatus.setIsAbleToSee(true);
        videoCallService = new VideoCallService(activeChat, receiverVideoView, senderVideoView);
        videoCallService.start();
        audioService = new AudioService(activeChat);
        audioService.start();
        endCallButton.setVisible(true);
    }

    @FXML
    private void terminateVideoCall() {
        videoStatus.setIsAbleToSee(false);
        if (videoCallService != null) {
            System.out.println("Stopping video call");
            videoCallService.stop();
            videoCallService = null;
            receiverVideoView.setImage(null);
            senderVideoView.setImage(null);
        }
        if (audioService != null) {
            System.out.println("Stopping audio call");
            audioService.stop();
            audioService = null;
        }
        endCallButton.setVisible(false);
    }

    public void sendMessage() throws Exception {
        String message = messageInput.getText();
        if (message.isEmpty() || activeChat == null) {
            return;
        }
        String formattedMessage = "You: " + message;
        chatMessages.get(activeChat).add(formattedMessage);
        messageInput.clear();
        DatagramSocket socket = new DatagramSocket();
        MessageSend messageSender = new MessageSend(socket, activeChat, DataConstants.IN_MESSAGE_PORT);
        messageSender.sendMessage(message);
    }

    @FXML
    public void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            try {
                sendMessage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void addContact() {
        String newContact = newContactInput.getText();
        if (newContact.isEmpty()) {
            return;
        }
        System.out.println("Adding contact: " + newContact);
        contacts.add(newContact);
        chatMessages.put(newContact, FXCollections.observableArrayList());
        newContactInput.clear();
    }

    protected void configureFileChooser() {
        fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
    }

    public void uploadFile() {
        configureFileChooser();
        java.io.File file = fileChooser.showOpenDialog(null);
        if (file == null || activeChat == null || file.isDirectory()) {
            return;
        }
        fileService.sendFile(file.getAbsolutePath(), activeChat);
    }

    protected void configureContactList() {
        contacts = FXCollections.observableSet();
        contactListView.setItems(FXCollections.observableArrayList(contacts));

        contacts.addListener((SetChangeListener<String>) change -> {
            if (change.wasAdded()) {
                contactListView.getItems().add(change.getElementAdded());
            } else if (change.wasRemoved()) {
                contactListView.getItems().remove(change.getElementRemoved());
            }
        });

        contactListView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            String selectedContact = contactListView.getSelectionModel().getSelectedItem();
            if (selectedContact == null) {
                return;
            }
            if (activeChat != null && activeChat.equals(selectedContact)) {
                return;
            }
            activeChat = selectedContact;
            chatStatus.setIsAbleToSee(true);
            videoStatus.setIsAbleToSee(false);
            messageListView.setItems(chatMessages.get(selectedContact));
            activeContactLabel.setText("Messaging: " + selectedContact);
        });
    }
}
