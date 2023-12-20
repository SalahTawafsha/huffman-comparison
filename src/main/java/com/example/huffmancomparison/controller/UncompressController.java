package com.example.huffmancomparison.controller;

import com.example.huffmancomparison.model.HuffmanUncompress;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;

import java.io.File;

public class UncompressController {
    private final File huffmanFile;
    private final Button selectFileButton;
    private final VBox box;
    private final Alert error = new Alert(Alert.AlertType.ERROR);

    public UncompressController(File huffmanFile, Button selectFileButton, VBox box) {
        this.huffmanFile = huffmanFile;
        this.selectFileButton = selectFileButton;
        this.box = box;
    }

    public void uncompress() {
        if (huffmanFile == null) {
            error.setContentText("Please select file");
            error.show();
            return;
        }

        HuffmanUncompress huffmanUncompress = new HuffmanUncompress(huffmanFile);
        Thread t = new Thread(() -> {
            Platform.runLater(() -> selectFileButton.setDisable(true));

            huffmanUncompress.uncompress();

            Platform.runLater(() -> selectFileButton.setDisable(false));
        });

        Thread t1 = new Thread(() -> {
            ProgressBar bar = new ProgressBar(0.0);
            Platform.runLater(() -> {
                if (box.getChildren().size() == 1)
                    box.getChildren().add(bar);
                else
                    box.getChildren().set(1, bar);
            });

            while (huffmanUncompress.isRunning()) {
                bar.setProgress(huffmanUncompress.getProgress());
            }
        });


        t.start();
        t1.start();

    }
}
