package com.example.huffmancomparison.view;

import com.example.huffmancomparison.controller.CompressController;
import com.example.huffmancomparison.controller.UncompressController;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        // create file chooser of original file to compress it
        FileChooser compressionFileChooser = new FileChooser();
        compressionFileChooser.setTitle("Select File to compression");
        compressionFileChooser.setInitialDirectory(new File("."));

        // button that opens file chooser
        Button compressionButton = new Button("Select file to compress");
        VBox compressionBox = new VBox(20, compressionButton);
        compressionBox.setAlignment(Pos.CENTER);

        // create file chooser of huffman file to uncompress it
        FileChooser uncompressionFileChooser = new FileChooser();
        uncompressionFileChooser.setTitle("select file to uncompress");
        uncompressionFileChooser.setInitialDirectory(new File("."));
        uncompressionFileChooser.getExtensionFilters()
                .add(new FileChooser
                        .ExtensionFilter("HUF Comparison files (*.huf)", "*.huf"));

        // button that opens file chooser
        Button uncompressionButton = new Button("Select File to uncompressed");
        VBox uncompressed = new VBox(20, uncompressionButton);
        uncompressed.setAlignment(Pos.CENTER);

        // HBox that have buttons of compress and uncompress
        HBox buttons = new HBox(150, compressionBox, uncompressed);
        buttons.setAlignment(Pos.CENTER);
        buttons.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, null, null)));

        // add buttons to a scene
        Scene s = new Scene(buttons, 1600, 900);
        s.getStylesheets().add("file:///C:/old_projects/huffman-comparison/src/main/resources/style.css");
        stage.setScene(s);

        // customize stage
        stage.getIcons().add(new Image("icon.png"));
        stage.setResizable(false);
        stage.setTitle("Huffman Compressor");
        stage.setMaximized(true);
        stage.show();

        compressionButton.setOnAction(e -> {
            CompressController compressController =
                    new CompressController(compressionFileChooser.showOpenDialog(stage), compressionButton, compressionBox);
            compressController.compress();
        });
        uncompressionButton.setOnAction(e -> {
            UncompressController uncompressController = new UncompressController(uncompressionFileChooser.showOpenDialog(stage), uncompressionButton, uncompressed);
            uncompressController.uncompress();
        });

    }

}