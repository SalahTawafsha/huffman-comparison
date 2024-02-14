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
<br
let's select this test.txt file to compress
<br>
<img src="https://github.com/SalahTawafsha/huffman-comparison/assets/93351227/8118fc94-f828-4476-a88a-c861dcb190ef" alt="Select file to compress" width="400">
<br>
when the compressing is done, the program will show the Huffman tree and the code for each byte, and write new file with
extension .huf to the disk.

Note that in this example, the file size is reduced from 26 KB to 15 KB.
<br>
<img src="https://github.com/SalahTawafsha/huffman-comparison/assets/93351227/abc36355-3069-4fb5-baa4-b7114209dff0" alt="Compressing" width="400">

## Select file to uncompress

When the user selects a file to uncompress

- FileChooser will be opened to select the file that he want to uncompress
- The program will read the file and build the Huffman tree based on the header of the file.
- Program will read the compressed data and decode it using the Huffman tree.
- While uncompress the file, the program will show progress bar to the user.
- Finally, it will write the uncompressed file to the disk and show it in explorer.
  <br>

<img src="https://github.com/SalahTawafsha/huffman-comparison/assets/93351227/5a82b7ee-557a-4c43-b69a-d95bddd36a57" alt="Select file to uncompress" width="400">
Note that the program will return the file to the original size (26 KB), and here is the uncompressed file.
<br>
<img src="https://github.com/SalahTawafsha/huffman-comparison/assets/93351227/7faf57be-e116-46ee-8894-d9c1bf895e7f" alt="Uncompressing" width="400">

