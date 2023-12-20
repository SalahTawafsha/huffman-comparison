package com.example.huffmancomparison.model;

public class BinaryConverter {
    public static byte parseByte(String substring) {
        byte i = 0;
        int pow = 0;
        for (int j = substring.length() - 1; j >= 0; j--, pow++) {
            if (substring.charAt(j) == '1')
                i += (byte) (1 << pow);
        }
        return i;
    }

    public static String toBinaryString(byte b) {
        StringBuilder s = new StringBuilder("00000000");
        if (b < 0) {
            s.replace(0, 1, "1");
            b += (byte) 128;
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

}
