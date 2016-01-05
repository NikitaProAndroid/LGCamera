package com.lge.voiceshutter.library;

public class ByteArray {
    private byte[] array;

    public ByteArray(byte[] ar, int len) {
        this.array = null;
        this.array = new byte[len];
        System.arraycopy(ar, 0, this.array, 0, len);
    }

    public byte[] array() {
        return this.array;
    }
}
