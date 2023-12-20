package com.example.huffmancomparison.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

import static com.example.huffmancomparison.model.BinaryConverter.toBinaryString;
import static com.example.huffmancomparison.model.BinaryConverter.parseByte;

public class HuffmanCompress {
    // 256 to don't resize array
    private final List<String> listView = new ArrayList<>(256);
    private double progress;
    private final File input;
    private boolean isRunning;

    public HuffmanCompress(File input) {
        this.input = input;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public double getProgress() {
        return progress;
    }

    public ObservableList<String> getListView() {
        return FXCollections.observableList(listView);
    }


    public void compress() {
        if (input == null) throw new IllegalArgumentException("File must be not null");

        isRunning = true;
        TNode[] bytes = new TNode[256];
        for (int i = 0; i < bytes.length; i++)
            bytes[i] = new TNode((byte) i, 0);

        long l;
        int countOfBytes = 0;
        try (FileInputStream scan = new FileInputStream(input)) {
            l = Files.size(Path.of(input.getPath()));

            byte[] buffer = new byte[8 * 1024];

            int j, count = 0;
            while ((j = scan.read(buffer)) != -1) {
                count += j;
                progress = (1 - ((double) (l - count) / l)) / 2;
                for (int i = 0; i < j; i++)
                    if (buffer[i] < 0) countOfBytes += bytes[buffer[i] + 256].increment();
                    else countOfBytes += bytes[buffer[i]].increment();

            }

            progress = 0.5;

        } catch (IOException e) {
            listView.add(e.getMessage());
            progress = 0;
            isRunning = false;
            return;
        }

        TNode[] nodes = new TNode[countOfBytes];
        for (int i = 0, j = 0; i < bytes.length; i++)
            if (bytes[i].getFrequency() != 0) nodes[j++] = bytes[i];

        StringBuilder header;
        String[] arr;
        if (nodes.length == 0) {
            listView.add("File size is zero, can't compress !!");
            progress = 0;
            isRunning = false;
            return;
        } else if (nodes.length == 1) {
            arr = new String[256];
            arr[nodes[0].getByteData()] = "0";
            header = new StringBuilder("0" + toBinaryString(nodes[0].getByteData()));
        } else {
            PriorityQueue<TNode> all = new PriorityQueue<>(List.of(nodes));

            while (all.size() > 1) {
                TNode left = all.remove();
                TNode right = all.remove();
                TNode z = new TNode(left.getFrequency() + right.getFrequency());
                z.setLeft(left);
                z.setRight(right);
                all.add(z);
            }

            TNode finalTree = all.peek();
            if (finalTree != null) {
                // post order traverse for tree
                header = new StringBuilder(finalTree.postOrderTraverse());
                arr = finalTree.createList();
            } else {
                listView.add("Can' compress file !!");
                progress = 0;
                isRunning = false;
                return;
            }
        }


        listView.add(String.format("%15s%20s%20s%20s", "byte", "Huffman", "frequency", "length"));

        for (int i = 0; i < arr.length; i++)
            if (arr[i] != null && !arr[i].isEmpty()) if (i < 128)
                listView.add(String.format("%15d%20s%25d%25d", i, arr[i], bytes[i].getFrequency(), arr[i].length()));
            else
                listView.add(String.format("%15d%20s%25d%25d", i - 256, arr[i], bytes[i].getFrequency(), arr[i].length()));

        try {
            String fileName = input.getName().substring(0, input.getName().lastIndexOf("."));
            String fileExtension = input.getName().substring(input.getName().lastIndexOf(".") + 1);

            FileOutputStream out = new FileOutputStream(fileName + ".huf");

            // 8 char to save extension of file in header
            byte[] bufferExtension = new byte[8];
            for (int i = 0; i < fileExtension.length(); i++)
                bufferExtension[i] = (byte) fileExtension.charAt(i);

            out.write(bufferExtension);

            // start from 4 because first 4 bytes is to save length of header
            int count = 4;

            byte[] bufferForWrite = new byte[257 * 2];

            // length of the header in bytes as unsigned 4 byte
            byte[] bufferForRead = ByteBuffer.allocate(4).putInt(header.length()).array();
            bufferForWrite[0] = bufferForRead[0];
            bufferForWrite[1] = bufferForRead[1];
            bufferForWrite[2] = bufferForRead[2];
            bufferForWrite[3] = bufferForRead[3];

            // append zeros in last byte to write in most significant bit to complete 8 bits
            if (header.length() % 8 != 0) header.append("0".repeat(8 - header.length() % 8));
            for (int i = 0; i < header.length(); ) {
                bufferForWrite[count++] = parseByte(header.substring(i, i + 8));
                i += 8;
            }


            out.write(Arrays.copyOfRange(bufferForWrite, 0, count));
            count = 0;

            FileInputStream scan = new FileInputStream(input);
            bufferForRead = new byte[8 * 1024]; // to read from original file again
            bufferForWrite = new byte[8 * 1024]; // to write on huffman file again
            StringBuilder bits = new StringBuilder(); // to store bits of huffman and then get bytes

            int lengthOfFileInBits = countOfBytes * 8, doneBits = 0, readLength; // try made double
            while ((readLength = scan.read(bufferForRead)) != -1) {
                for (int k = 0; k < readLength; k++) {
                    if (bufferForRead[k] < 0) bits.append(arr[bufferForRead[k] + 256]);
                    else bits.append(arr[bufferForRead[k]]);

                    progress = Math.abs(((double) lengthOfFileInBits - doneBits) / lengthOfFileInBits) / 2 + 0.5;

                    // when bits >= 8 then I have new byte so, I can generate byte and delete fist 8 bit
                    if (bits.length() >= 8) {
                        bufferForWrite[count++] = parseByte(bits.substring(0, 8));
                        bits.delete(0, 8);
                        doneBits += 8;
                        if (count == 8 * 1024) {
                            out.write(bufferForWrite);
                            count = 0;
                            bufferForWrite = new byte[8 * 1024];
                        }
                    }

                }
            }


            int bitsInLastByte = 8;
            while (!bits.isEmpty()) {
                if (bits.length() < 8) {
                    bitsInLastByte = bits.length();
                    bits.append("0".repeat(8 - bits.length()));
                    bufferForWrite[count++] = parseByte(bits.toString());
                    break;
                } else {
                    bufferForWrite[count++] = (parseByte(bits.substring(0, 8)));
                    bits.delete(0, 8);
                    if (count == 8 * 1024) {
                        out.write(bufferForWrite);
                        count = 0;
                        bufferForWrite = new byte[8 * 1024];
                    }
                }
            }

            if (count > 0) {
                out.write(Arrays.copyOfRange(bufferForWrite, 0, count));
                out.write((byte) bitsInLastByte);
            } else out.write(8);

            progress = 1;
            out.close();

            // show file in the folder path
            Runtime.getRuntime().exec("explorer /select, " + input.getName().substring(0, input.getName().lastIndexOf(".")) + ".huf");

        } catch (IOException e) {
            listView.clear();
            listView.add(e.getMessage());
            progress = 0;
        }

        isRunning = false;

    }

}
