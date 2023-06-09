package com.example.huffmancomparison;

import java.util.Stack;

public class TNode implements Comparable<TNode> {
    private TNode left;
    private TNode right;
    private int frequency;

    private final Byte byteData;

    public TNode(byte byteData, int frequency) {
        this.frequency = frequency;
        this.byteData = byteData;
    }

    public TNode(int frequency) {
        this.frequency = frequency;
        byteData = null;
    }

    public TNode(Byte byteData) {
        this.byteData = byteData;
    }

    public Byte getByteData() {
        return byteData;
    }

    public int increment() {
        frequency++;
        if (frequency == 1)
            return 1;
        else
            return 0;
    }

    public TNode getLeft() {
        return left;
    }

    public TNode getRight() {
        return right;
    }

    public boolean hasLeft() {
        return left != null;
    }

    public boolean hasRight() {
        return right != null;
    }

    public int getFrequency() {
        return frequency;
    }

    public String traverse() {
        return traverse(this);
    }


    private String traverse(TNode curr) {
        if (curr != null)
            return traverse(curr.left) + traverse(curr.right) + curr;

        return "";
    }

    public String[] createList() {
        String[] arr = new String[256];
        Stack<Character> bytes = new Stack<>();
        getPath(this, bytes, arr);

        return arr;
    }

    private void getPath(TNode root, Stack<Character> c, String[] arr) {
        if (root == null)
            return;

        if (root.byteData == null) {
            c.push('0');
            getPath(root.left, c, arr);
            c.pop();

            c.push('1');
            getPath(root.right, c, arr);
            c.pop();
        } else if (root.byteData >= 0)
            arr[root.byteData] = toArray(c);
        else
            arr[root.byteData + 256] = toArray(c);


    }

    private String toArray(Stack<Character> c) {
        Character[] a = c.toArray(new Character[0]);
        StringBuilder s = new StringBuilder();
        for (Character character : a)
            s.append(character);

        return s.toString();
    }

    public void setLeft(TNode left) {
        this.left = left;
    }

    public void setRight(TNode right) {
        this.right = right;
    }

    @Override
    public String toString() {
        if (byteData != null)
            return "0" + HelloApplication.toBinaryString(byteData);
        else
            return "1";
    }

    @Override
    public int compareTo(TNode o) {
        return this.getFrequency() - o.getFrequency();
    }

}
