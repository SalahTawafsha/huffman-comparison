package com.example.huffmancomparison;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class HelloApplication extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        FileChooser compressionFileChooser = new FileChooser();
        compressionFileChooser.setTitle("Select File to compression");
        compressionFileChooser.setInitialDirectory(new File("."));

        Button compressionButton = new Button("Select File to compression");

        VBox compression = new VBox(20, compressionButton);
        compression.setAlignment(Pos.CENTER);

        FileChooser uncompressionFileChooser = new FileChooser();
        uncompressionFileChooser.setTitle("Select File to uncompressed");
        uncompressionFileChooser.setInitialDirectory(new File("."));
        uncompressionFileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("HUF Comparison files (*.huf)", "*.huf"));

        Button uncompressionButton = new Button("Select File to uncompressed");
        VBox uncompressed = new VBox(20, uncompressionButton);
        uncompressed.setAlignment(Pos.CENTER);

        HBox input = new HBox(150, compression, uncompressed);
        input.setAlignment(Pos.CENTER);
        input.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, null, null)));
        Scene s = new Scene(input, 1600, 900);
        s.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());
        stage.setScene(s);
        stage.show();
        stage.getIcons().add(new Image("icon.png"));
        stage.setResizable(false);
        stage.setTitle("Huffman Compressor");
        stage.setMaximized(true);

        compressionButton.setOnAction(e -> compress(compressionFileChooser.showOpenDialog(stage), compression, compressionButton));
        uncompressionButton.setOnAction(e -> uncompress(uncompressionFileChooser.showOpenDialog(stage), uncompressed, uncompressionButton));

    }

    private void uncompress(File input, VBox box, Button uncompressionButton) {
        if (input == null)
            return;
        ProgressBar bar = new ProgressBar(0);

        if (box.getChildren().size() == 1)
            box.getChildren().add(bar);
        else
            box.getChildren().set(1, bar);

        StringBuilder fileExtension = new StringBuilder();
        Thread t = new Thread(() -> {
            uncompressionButton.setDisable(true);
            try {
                long l = Files.size(Path.of(input.getPath()));
                FileInputStream scan = new FileInputStream(input);
                byte[] buffer = new byte[8];
                int j = scan.read(buffer);
                if (j != -1)
                    for (int i = 0; i < j; i++)
                        if (buffer[i] != 0)
                            fileExtension.append((char) buffer[i]);
                        else
                            break;

                buffer = new byte[4];
                scan.read(buffer);

                ByteBuffer byteBuffer = ByteBuffer.allocate(Integer.BYTES);
                byteBuffer.put(buffer);
                byteBuffer.rewind();
                int value = byteBuffer.getInt();


                int read = 0;
                buffer = new byte[1];
                StringBuilder header = new StringBuilder();
                while (read < value / 8 + 1 && scan.read(buffer) != -1) {
                    read++;
                    header.append(toBinaryString(buffer[0]));
                }

                Stack<TNode> stack = new Stack<>();
                for (int i = 0; i < value; ) {
                    if (header.charAt(i) == '0') {
                        i++;
                        stack.push(new TNode(parseByte(header.substring(i, Math.min(i + 8, header.length()))), 0));
                        i += Math.min(8, header.length());
                    } else {
                        i++;
                        TNode node = new TNode(null);
                        node.setRight(stack.pop());
                        node.setLeft(stack.pop());

                        stack.push(node);
                    }
                }
                header = null;

                TNode tree = stack.peek();

                FileOutputStream out = new FileOutputStream(input.getName().substring(0, input.getName().lastIndexOf(".")) + " (1)." + fileExtension);

                buffer = new byte[8 * 1024];
                StringBuilder s = new StringBuilder();
                int lastBitIndex = 8, count = 0, bufferCounter = 0;

                byte[] bufferWriter = new byte[8 * 1024];
                while ((j = scan.read(buffer)) != -1) {
                    count += j;
                    bar.setProgress((1 - ((double) (l - count) / l)));
                    for (int i = 0; i < j; i++) {
                        if (i == j - 1)
                            lastBitIndex = buffer[i];
                        s.append(toBinaryString(buffer[i]));

                        int toDelete = 0;
                        TNode curr = tree;
                        while (curr != null && toDelete < s.length()) {
                            if (s.charAt(toDelete) == '0' && curr.hasLeft())
                                curr = curr.getLeft();
                            else if (curr.hasRight())
                                curr = curr.getRight();
                            else
                                break;
                            toDelete++;
                        }
                        if (toDelete > 0 && curr.getByteData() != null) {
                            s.delete(0, toDelete);
                            bufferWriter[bufferCounter++] = curr.getByteData();
                            if (bufferCounter == 8 * 1024) {
                                out.write(bufferWriter);
                                bufferCounter = 0;
                                bufferWriter = new byte[8 * 1024];
                            }
                        }
                    }
                }

                scan.close();

                lastBitIndex = 16 - lastBitIndex;

                while (s.length() > lastBitIndex) {
                    int toDelete = 0;
                    TNode curr = tree;
                    while (curr != null && toDelete < s.length()) {
                        if (s.charAt(toDelete) == '0' && curr.hasLeft())
                            curr = curr.getLeft();
                        else if (curr.hasRight())
                            curr = curr.getRight();
                        else
                            break;
                        toDelete++;
                    }
                    if (toDelete > 0 && curr.getByteData() != null) {
                        s.delete(0, toDelete);
                        bufferWriter[bufferCounter++] = curr.getByteData();
                        if (bufferCounter == 8 * 1024) {
                            out.write(bufferWriter);
                            bufferCounter = 0;
                            bufferWriter = new byte[8 * 1024];
                        }
                    }
                }


                if (bufferCounter != 0)
                    out.write(Arrays.copyOfRange(bufferWriter, 0, bufferCounter));

                bar.setProgress(1);

                Runtime.getRuntime().exec("explorer /select, " + input.getName().substring(0, input.getName().lastIndexOf(".")) + " (1)." + fileExtension);

                out.close();

            } catch (
                    IOException ignored) {

            }
            uncompressionButton.setDisable(false);
        });
        t.start();

    }

    public static byte parseByte(String substring) {
        byte i = 0;
        int pow = 0;
        for (int j = substring.length() - 1; j >= 0; j--, pow++) {
            if (substring.charAt(j) == '1')
                i += 1 << pow;
        }
        return i;
    }

    public static String toBinaryString(byte b) {
        StringBuilder s = new StringBuilder("00000000");
        if (b < 0) {
            s.replace(0, 1, "1");
            b += 128;
        }
        if (b - 64 >= 0) {
            s.replace(1, 2, "1");
            b -= 64;
        }
        if (b - 32 >= 0) {
            s.replace(2, 3, "1");
            b -= 32;
        }
        if (b - 16 >= 0) {
            s.replace(3, 4, "1");
            b -= 16;
        }
        if (b - 8 >= 0) {
            s.replace(4, 5, "1");
            b -= 8;
        }
        if (b - 4 >= 0) {
            s.replace(5, 6, "1");
            b -= 4;
        }
        if (b - 2 >= 0) {
            s.replace(6, 7, "1");
            b -= 2;
        }
        if (b - 1 >= 0)
            s.replace(7, 8, "1");


        return s.toString();
    }

    private void compress(File input, VBox box, Button compressionButton) throws RuntimeException {
        if (input == null)
            return;

        ProgressBar bar = new ProgressBar(0);
        if (box.getChildren().size() == 1)
            box.getChildren().add(bar);
        else
            box.getChildren().set(1, bar);


        ListView<String> listView = new ListView<>();
        if (box.getChildren().size() == 2)
            box.getChildren().add(listView);
        else
            box.getChildren().set(2, listView);


        TNode[] bytes = new TNode[256];
        for (int i = 0; i < bytes.length; i++)
            bytes[i] = new TNode((byte) i, 0);

        Thread t = new Thread(() -> {
            compressionButton.setDisable(true);
            long l;
            int countOfBytes = 0;
            try (FileInputStream scan = new FileInputStream(input)) {
                l = Files.size(Path.of(input.getPath()));

                byte[] buffer = new byte[8 * 1024];

                int j, count = 0;
                while ((j = scan.read(buffer)) != -1) {
                    count += j;
                    bar.setProgress((1 - ((double) (l - count) / l)) / 2);
                    for (int i = 0; i < j; i++)
                        if (buffer[i] < 0)
                            countOfBytes += bytes[buffer[i] + 256].increment();
                        else
                            countOfBytes += bytes[buffer[i]].increment();

                }

                bar.setProgress(0.5);

            } catch (IOException e) {
                ArrayList<String> list = new ArrayList<>();
                list.add(e.getMessage());
                bar.setProgress(0);
                listView.setItems(FXCollections.observableList(list));
                return;
            }

            TNode[] nodes = new TNode[countOfBytes];
            for (int i = 0, j = 0; i < bytes.length; i++)
                if (bytes[i].getFrequency() != 0)
                    nodes[j++] = bytes[i];

            StringBuilder header;
            String[] arr;
            if (nodes.length == 0) {
                ArrayList<String> list = new ArrayList<>();
                list.add("File size is zero, can't compress !!");
                bar.setProgress(0);
                listView.setItems(FXCollections.observableList(list));
                return;
            } else if (nodes.length == 1) {
                arr = new String[256];
                arr[nodes[0].getByteData()] = "0";
                header = new StringBuilder("0" + toBinaryString(nodes[0].getByteData()));
            } else {
                PriorityQueue<TNode> all = new PriorityQueue<>(List.of(nodes));

                while (all.size() > 1) {
                    TNode x = all.remove();
                    TNode y = all.remove();
                    TNode z = new TNode(x.getFrequency() + y.getFrequency());
                    z.setLeft(x);
                    z.setRight(y);
                    all.add(z);
                }
                header = new StringBuilder(all.peek().traverse());
                arr = all.peek().createList();
            }

            ArrayList<String> list = new ArrayList<>(countOfBytes);

            list.add(String.format("%15s%20s%20s%20s", "byte", "Huffman", "frequency", "length"));

            for (int i = 0; i < arr.length; i++)
                if (arr[i] != null && arr[i].length() > 0)
                    if (i < 128)
                        list.add(String.format("%15d%20s%25d%25d", i, arr[i], bytes[i].getFrequency(), arr[i].length()));
                    else
                        list.add(String.format("%15d%20s%25d%25d", i - 256, arr[i], bytes[i].getFrequency(), arr[i].length()));

            listView.setItems(FXCollections.observableList(list));

            try {

                FileOutputStream out = new FileOutputStream(input.getName().substring(0, input.getName().lastIndexOf(".")) + ".huf");
                String fileExtension = input.getName().substring(input.getName().lastIndexOf(".") + 1);
                byte[] bufferExtension = new byte[8];
                for (int i = 0; i < fileExtension.length(); i++)
                    bufferExtension[i] = (byte) fileExtension.charAt(i);

                out.write(bufferExtension);

                int count = 4;

                byte[] comparisonBuffer = new byte[257 * 2];

                byte[] buffer = ByteBuffer.allocate(4).putInt(header.length()).array();       // size of the header without extension
                comparisonBuffer[0] = buffer[0];
                comparisonBuffer[1] = buffer[1];
                comparisonBuffer[2] = buffer[2];
                comparisonBuffer[3] = buffer[3];

                if (header.length() % 8 != 0)
                    header.append("0".repeat(8 - header.length() % 8));
                for (int i = 0; i < header.length(); ) {
                    comparisonBuffer[count++] = parseByte(header.substring(i, i + 8));
                    i += 8;
                }


                out.write(Arrays.copyOfRange(comparisonBuffer, 0, count));

                comparisonBuffer = new byte[8 * 1024];
                StringBuilder getByte = new StringBuilder();
                count = 0;
                FileInputStream scan = new FileInputStream(input);

                buffer = new byte[8 * 1024];
                int j = 0, length = countOfBytes * 8, bufferLength;
                while ((bufferLength = scan.read(buffer)) != -1)
                    for (int k = 0; k < bufferLength; k++) {
                        if (buffer[k] < 0)
                            getByte.append(arr[buffer[k] + 256]);
                        else
                            getByte.append(arr[buffer[k]]);

                        bar.setProgress(Math.abs(((double) length - j) / length) / 2 + 0.5);
                        if (getByte.length() >= 8) {
                            comparisonBuffer[count++] = parseByte(getByte.substring(0, 8));
                            getByte.delete(0, 8);
                            j += 8;
                            if (count == 8 * 1024) {
                                out.write(comparisonBuffer);
                                count = 0;
                                comparisonBuffer = new byte[8 * 1024];
                            }
                        }

                    }


                int i = 8;
                while (getByte.length() > 0)
                    if (8 > getByte.length()) {
                        i = getByte.length();
                        getByte.append("0".repeat(8 - getByte.length()));
                        comparisonBuffer[count++] = parseByte(getByte.toString());
                        break;
                    } else {
                        i = 8;
                        comparisonBuffer[count++] = (parseByte(getByte.substring(0, 8)));
                        getByte.delete(0, 8);
                        if (count == 8 * 1024) {
                            out.write(comparisonBuffer);
                            count = 0;
                            comparisonBuffer = new byte[8 * 1024];
                        }
                    }

                if (count > 0) {
                    out.write(Arrays.copyOfRange(comparisonBuffer, 0, count));
                    out.write((byte) i);
                } else
                    out.write(8);

                bar.setProgress(1);
                out.close();

                Runtime.getRuntime().exec("explorer /select, " + input.getName().substring(0, input.getName().lastIndexOf(".")) + ".huf");

            } catch (IOException e) {
                ArrayList<String> print = new ArrayList<>();
                print.add(e.getMessage());
                bar.setProgress(0);
                listView.setItems(FXCollections.observableList(print));
                return;
            }

            compressionButton.setDisable(false);

        });

        t.start();

    }

}