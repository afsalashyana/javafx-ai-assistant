package com.genuinecoder.aiassistant.gui;

import com.genuinecoder.aiassistant.util.ContextUtil;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.InputStreamResource;
import org.springframework.util.MimeTypeUtils;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainApplicationController implements Initializable {

    private final Logger log = Logger.getLogger(MainApplicationController.class.getName());

    @FXML
    public TextArea textAreaAiResponse;
    @FXML
    public TextArea textAreaInput;
    @FXML
    public ProgressBar progressBar;
    @FXML
    public ImageView imageView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupGui();
    }

    private void setupGui() {
        log.log(Level.INFO, "GenuineCoder Desktop AI Assistant Loaded!");
    }

    @FXML
    public void handleButtonSendAction(ActionEvent actionEvent) {
        var textPrompt = textAreaInput.getText();
        var image = imageView.getImage();
        if (textPrompt == null || textPrompt.isEmpty()) {
            return;
        }
        imageView.setVisible(false);
        imageView.setImage(null);
        textAreaAiResponse.setText("");
        progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        if (image == null) {
            processTextOnlyPrompt(textPrompt);
        } else {
            processTextWithImagePrompt(textPrompt, image);
        }
    }

    private void processTextWithImagePrompt(String textPrompt, Image image) {
        log.log(Level.INFO, "Processing LLM input with text and image");
        var chatClient = ContextUtil.getApplicationContext().getBean(ChatClient.class);
        var byteArrayOutputStream = convertJavaFxImageIntoPng(image);

        chatClient.prompt()
                .user(promptUserSpec -> {
                    promptUserSpec
                            .text(textPrompt)
                            .media(MimeTypeUtils.IMAGE_PNG, new InputStreamResource(
                                    new ByteArrayInputStream(byteArrayOutputStream.toByteArray())
                            ));
                })
                .stream()
                .content()
                .doOnComplete(() -> Platform.runLater(() -> progressBar.setProgress(0)))
                .subscribe(token -> {
                    Platform.runLater(() -> textAreaAiResponse.appendText(token));
                });
    }

    private ByteArrayOutputStream convertJavaFxImageIntoPng(Image image) {
        try {
            var bufferedImage = SwingFXUtils.fromFXImage(image, null);
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", bao);
            return bao;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void processTextOnlyPrompt(String textPrompt) {
        log.log(Level.INFO, "Processing LLM input with text only");
        var chatClient = ContextUtil.getApplicationContext().getBean(ChatClient.class);
        chatClient.prompt()
                .user(textPrompt)
                .stream()
                .content()
                .doOnComplete(() -> Platform.runLater(() -> progressBar.setProgress(0)))
                .subscribe(token -> {
                    Platform.runLater(() -> textAreaAiResponse.appendText(token));
                });
    }

    @FXML
    public void handleButtonImagePickerAction(ActionEvent actionEvent) {
        var clipboard = Clipboard.getSystemClipboard();
        if (clipboard == null) {
            log.log(Level.INFO, "Clipboard is null!");
            return;
        }
        if (!clipboard.hasImage()) {
            log.log(Level.INFO, "Clipboard has no image!");
            return;
        }
        var image = clipboard.getImage();
        imageView.setImage(image);
        log.log(Level.INFO, "Image loaded successfully!");
        imageView.setVisible(true);
    }
}
