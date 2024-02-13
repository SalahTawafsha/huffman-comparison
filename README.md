# Huffman File Compression

## installation

```bash
git clone https://github.com/SalahTawafsha/huffman-comparison.git
```

## Introduction

This project is a simple implementation of Huffman file compression algorithm. The project is written in java and javaFX
is used for the GUI.

The UI is so simple, it contains two buttons, select file to compress and select file to uncompress.
<br>
<img src="https://github.com/SalahTawafsha/huffman-comparison/assets/93351227/40a4ba1d-7b3d-48da-9b3e-302377c9aa5c" alt="UI" width="400"/>
<br>

## Select file to compress

When the user click on select file to compress

- FileChooser will be opened to select the file that he want
- the program will read the file and calculate the frequency of each byte in the file.
- while compress the file, the program will show progress bar to the user.
- System will build a Huffman tree based on the frequency of the byte.
- it will generate a code for each byte based on the Huffman tree.
- when end compress the program will show the frequency and the code for each byte.
- finally, it will write the compressed data to the disk.
<br>

### Select file compress

after click on select file to compress, FileChooser will be opened to select the file that he want to compress.
<br>
<img src="https://github.com/SalahTawafsha/huffman-comparison/assets/93351227/d4ed0a82-c8b1-4d24-83d3-abc9607308df" alt="Select file to compress" width="400">
<br>
after select the file, the program will start compress the file and show the progress bar to the user.

when the compressing is done, the program will show the Huffman tree and the code for each byte.
<br>
<img src="https://github.com/SalahTawafsha/huffman-comparison/assets/93351227/8531ab5f-aa67-4549-a791-8b66733b1d45" alt="Compressing" width="400">

## Select file to uncompress

When the user selects a file to uncompress

- FileChooser will be opened to select the file that he want to uncompress
- The program will read the file and build the Huffman tree based on the header of the file.
- Program will read the compressed data and decode it using the Huffman tree.
- While uncompress the file, the program will show progress bar to the user.
- Finally, it will write the uncompressed file to the disk.
  <br>

<img src="https://github.com/SalahTawafsha/huffman-comparison/assets/93351227/d4a6f78e-9f17-4ecb-af94-c2357a470288" alt="Select file to uncompress" width="400">