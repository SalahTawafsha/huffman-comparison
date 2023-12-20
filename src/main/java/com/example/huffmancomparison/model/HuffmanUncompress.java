package com.example.huffmancomparison.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Stack;

import static com.example.huffmancomparison.model.BinaryConverter.parseByte;
import static com.example.huffmancomparison.model.BinaryConverter.toBinaryString;

public class HuffmanUncompress {

    private double progress;
    private final File input;
    private boolean isRunning;


    public HuffmanUncompress(File input) {
        this.input = input;
    }

    public double getProgress() {
        return progress;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void uncompress() {
        if (input == null) throw new IllegalArgumentException("File must be not null");

        isRunning = true;
        try {
            StringBuilder fileExtension = new StringBuilder();
            long fileSize = Files.size(Path.of(input.getPath())); // to calculate progress using it

            // read bytes of file extension and extract it
            FileInputStream scan = new FileInputStream(input);
            byte[] bufferForRead = new byte[8];
            int j = scan.read(bufferForRead);
            if (j != -1)
                for (int i = 0; i < j; i++) {
                    if (bufferForRead[i] != 0)
                        fileExtension.append((char) bufferForRead[i]);
                    else
                        break;
                }

            // read bytes of header size and extract it
            bufferForRead = new byte[4];
            scan.read(bufferForRead);
            ByteBuffer byteBuffer = ByteBuffer.allocate(Integer.BYTES);
            byteBuffer.put(bufferForRead);
            byteBuffer.rewind();
            int headerLength = byteBuffer.getInt();

            // to read header char by char
            bufferForRead = new byte[1];
            int readingIndex = 0;

            StringBuilder header = new StringBuilder();
            while (readingIndex < headerLength / 8 + 1 && scan.read(bufferForRead) != -1) {
                readingIndex++;
                header.append(toBinaryString(bufferForRead[0]));
            }

            // build tree from the postfix order of tree
            Stack<TNode> stack = new Stack<>();
            for (int i = 0; i < headerLength; ) {
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

            TNode tree = stack.peek();

            String fileName = input.getName().substring(0, input.getName().lastIndexOf("."));
            FileOutputStream outputFile = new FileOutputStream(fileName + " (1)." + fileExtension);

            bufferForRead = new byte[8 * 1024];
            StringBuilder bits = new StringBuilder();
            int lastBitIndex = 8, doneReadBytes = 0, bufferCounter = 0;

            byte[] bufferForWrite = new byte[8 * 1024];

            while ((j = scan.read(bufferForRead)) != -1) {
                doneReadBytes += j;
                progress = (1 - ((double) (fileSize - doneReadBytes) / fileSize));
                for (int i = 0; i < j; i++) {
                    // byte that for last bit index (in last byte we may have 1110 0000 for example)
                    if (i == j - 1)
                        lastBitIndex = bufferForRead[i];

                    bits.append(toBinaryString(bufferForRead[i]));

                    if (bits.length() > 8 * 1024)
                        while (bits.length() > 10) {
                            bufferCounter = scanNewByte(tree, bits, bufferForWrite, bufferCounter);
                            if (bufferCounter == 8 * 1024) {
                                outputFile.write(bufferForWrite);
                                bufferCounter = 0;
                                bufferForWrite = new byte[8 * 1024];
                            }
                        }
                }
            }

            scan.close();

            lastBitIndex = 16 - lastBitIndex;

            while (bits.length() > lastBitIndex) {
                bufferCounter = scanNewByte(tree, bits, bufferForWrite, bufferCounter);
                if (bufferCounter == 8 * 1024) {
                    outputFile.write(bufferForWrite);
                    bufferCounter = 0;
                    bufferForWrite = new byte[8 * 1024];
                }
            }


            if (bufferCounter != 0)
                outputFile.write(Arrays.copyOfRange(bufferForWrite, 0, bufferCounter));

            progress = 1;

            Runtime.getRuntime().exec("explorer /select, " + input.getName().substring(0, input.getName().lastIndexOf(".")) + " (1)." + fileExtension);

            outputFile.close();

        } catch (
                IOException ignored) {
        }

        isRunning = false;

    }

    private int scanNewByte(TNode tree, StringBuilder bits, byte[] bufferForWrite, int bufferCounter) {
        TNode curr = tree;
        int nextHuffmanCodeLength = 0;

        while (curr != null && nextHuffmanCodeLength < bits.length()) {
            if (bits.charAt(nextHuffmanCodeLength) == '0' && curr.hasLeft())
                curr = curr.getLeft();
            else if (curr.hasRight())
                curr = curr.getRight();
            else
                break;
            nextHuffmanCodeLength++;
        }
        if (nextHuffmanCodeLength > 0 && curr != null && curr.getByteData() != null) {
            bits.delete(0, nextHuffmanCodeLength);
            bufferForWrite[bufferCounter++] = curr.getByteData();
        }
        return bufferCounter;
    }

}
