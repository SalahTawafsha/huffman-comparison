package com.example.huffmancomparison.controller;

import com.example.huffmancomparison.model.HuffmanCompress;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;

import java.io.File;

public class CompressController {
    private final File originalFile;
    private final Button selectFileButton;
    private final VBox box;
    private final Alert error = new Alert(Alert.AlertType.ERROR);


    public CompressController(File originalFile, Button selectFileButton, VBox box) {
        this.originalFile = originalFile;
        this.selectFileButton = selectFileButton;
        this.box = box;
    }

    public void compress() {
        if (originalFile == null) {
            error.setContentText("Please select file");
            error.show();
            return;
        }

        HuffmanCompress huffmanCompress = new HuffmanCompress(originalFile);
        Thread t = new Thread(() -> {
            Platform.runLater(() -> selectFileButton.setDisable(true));

            huffmanCompress.compress();

            Platform.runLater(() -> {
                ListView<String> listView = new ListView<>(huffmanCompress.getListView());
                if (box.getChildren().size() == 2)
                    box.getChildren().add(listView);
                else
                    box.getChildren().set(2, listView);

                selectFileButton.setDisable(false);
            });
        });

        Thread t1 = new Thread(() -> {
            ProgressBar bar = new ProgressBar(0);
            Platform.runLater(() -> {
                if (box.getChildren().size() == 1)
                    box.getChildren().add(bar);
                else
                    box.getChildren().set(1, bar);

            });

            while (huffmanCompress.isRunning())
                bar.setProgress(huffmanCompress.getProgress());

        });


        t.start();
        t1.start();


    }
}
