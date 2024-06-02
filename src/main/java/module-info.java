module org.example.proyectofinal {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires opencv;
    requires java.desktop;

    opens org.example.proyectofinal to javafx.fxml;
    exports org.example.proyectofinal;
    exports org.example.proyectofinal.VideoCall;
    opens org.example.proyectofinal.VideoCall to javafx.fxml;
}