package com.lge.morpho.utils.opengl;

public enum GLESVersion {
    GLES10 {
        public int[] getContextAttributeList() {
            return null;
        }
    },
    GLES20 {
        public int[] getContextAttributeList() {
            return new int[]{GLESVersion.EGL_CONTEXT_CLIENT_VERSION, 2, 12344};
        }
    };
    
    private static int EGL_CONTEXT_CLIENT_VERSION;

    public abstract int[] getContextAttributeList();

    static {
        EGL_CONTEXT_CLIENT_VERSION = 12440;
    }
}
