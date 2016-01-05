package com.lge.morpho.utils;

import android.util.Log;
import java.nio.ByteBuffer;

public class NativeMemoryAllocator {
    public static final String TAG = "MorphoNativeMemoryAllocator";

    public static final native ByteBuffer allocateBuffer(int i);

    public static final native int freeBuffer(ByteBuffer byteBuffer);

    static {
        try {
            System.loadLibrary("morpho_memory_allocator");
            Log.d(TAG, "load libmorpho_memory_allocator.so");
        } catch (UnsatisfiedLinkError e) {
            Log.d(TAG, e.getMessage());
            Log.d(TAG, "can't loadLibrary");
        }
    }
}
