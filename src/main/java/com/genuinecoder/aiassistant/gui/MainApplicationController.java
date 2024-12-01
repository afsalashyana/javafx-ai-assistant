package com.genuinecoder.aiassistant.gui;

import com.genuinecoder.aiassistant.util.ContextUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import org.springframework.ai.chat.client.ChatClient;

import java.net.URL;
import java.util.ResourceBundle;

public class MainApplicationController implements Initializable {

    @FXML
    public TextArea textAreaAiResponse;
    @FXML
    public TextArea textAreaInput;
    @FXML
    public ProgressBar progressBar;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupGui();
    }

    private void setupGui() {

    }

    @FXML
    public void handleButtonSendAction(ActionEvent actionEvent) {
        var llmInput = textAreaInput.getText();
        if (llmInput == null || llmInput.isEmpty()) {
            return;
        }
        progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        textAreaAiResponse.setText("");
        var chatClient = ContextUtil.getApplicationContext().getBean(ChatClient.class);
        chatClient.prompt()
                .user(llmInput)
                .stream()
                .content()
                .doOnComplete(() -> Platform.runLater(() -> progressBar.setProgress(0)))
                .subscribe(token -> {
                    Platform.runLater(() -> textAreaAiResponse.appendText(token));
                });
    }
}
