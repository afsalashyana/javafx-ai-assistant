package com.genuinecoder.aiassistant.gui;

import com.genuinecoder.aiassistant.util.ContextUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;

import java.util.Objects;

public class MainApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent parent = FXMLLoader.load(
                Objects.requireNonNull(MainApplication.class.getResource("/fxml/mainwindow.fxml"))
        );
        Scene scene = new Scene(parent);
        stage.setTitle("GenuineAssistant | Genuine Coder AI Assistant");
        stage.setScene(scene);
        stage.getIcons().add(new Image("/icon/icon.png"));
        stage.setOnCloseRequest(event -> {
            SpringApplication.exit(ContextUtil.getApplicationContext());
        });
        stage.show();
    }

    public static void launchApplication() {
        MainApplication.launch(MainApplication.class, "");
    }

}
