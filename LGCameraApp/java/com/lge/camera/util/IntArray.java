package com.lge.camera.util;

public class IntArray {
    private static final int INIT_CAPACITY = 8;
    private int[] mData;
    private int mSize;

    public IntArray() {
        this.mData = new int[INIT_CAPACITY];
        this.mSize = 0;
    }

    public void add(int value) {
        if (this.mData.length == this.mSize) {
            int[] temp = new int[(this.mSize + this.mSize)];
            System.arraycopy(this.mData, 0, temp, 0, this.mSize);
            this.mData = temp;
        }
        int[] iArr = this.mData;
        int i = this.mSize;
        this.mSize = i + 1;
        iArr[i] = value;
    }

    public int size() {
        return this.mSize;
    }

    public int[] toArray(int[] result) {
        if (result == null || result.length < this.mSize) {
            result = new int[this.mSize];
        }
        System.arraycopy(this.mData, 0, result, 0, this.mSize);
        return result;
    }
}
