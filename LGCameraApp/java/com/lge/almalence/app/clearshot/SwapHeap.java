package com.lge.almalence.app.clearshot;

public final class SwapHeap {
    public static native byte[] CopyFromHeap(int i, int i2);

    public static native boolean FreeFromHeap(int i);

    public static native byte[] SwapFromHeap(int i, int i2);

    public static native int SwapToHeap(byte[] bArr);

    static {
        System.loadLibrary("swapheap");
    }
}
