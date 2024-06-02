package org.example.proyectofinal.VideoCall;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class VideoCallController {

    private Stage stage;
    @FXML
    private ImageView consumerImageView;
    @FXML
    private ImageView senderImageView;

    private VideoCallService videoCallService;

    //TODO
    public void initialize() {
        consumerImageView = new ImageView();
        senderImageView = new ImageView();

        this.stage.setOnCloseRequest(event -> {
            //videoCallService.stop();
        });
    }
}
