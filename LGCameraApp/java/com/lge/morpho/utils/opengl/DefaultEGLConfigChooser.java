package com.lge.morpho.utils.opengl;

import com.lge.camera.util.CamLog;
import com.lge.morpho.app.morphopanorama.GLTextureView.EGLConfigChooser;
import java.util.ArrayList;
import java.util.List;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

public class DefaultEGLConfigChooser implements EGLConfigChooser {
    private static final String LOG_TAG;
    private int mAlphaSize;
    private int mBlueSize;
    private int mDepthSize;
    private int mGreenSize;
    private int mRedSize;
    private int mStencilSize;

    static {
        LOG_TAG = DefaultEGLConfigChooser.class.getSimpleName();
    }

    public DefaultEGLConfigChooser() {
        this.mRedSize = 8;
        this.mGreenSize = 8;
        this.mBlueSize = 8;
        this.mAlphaSize = 0;
        this.mDepthSize = 16;
        this.mStencilSize = 0;
    }

    public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display, GLESVersion version) {
        int[] config_spec = createConfigSpec(version);
        int[] config_num = new int[1];
        if (!egl.eglChooseConfig(display, config_spec, null, 0, config_num)) {
            throw new IllegalArgumentException("eglChooseConfig failed");
        } else if (config_num[0] <= 0) {
            throw new IllegalArgumentException("No match configSpec");
        } else {
            EGLConfig[] configs = new EGLConfig[config_num[0]];
            if (egl.eglChooseConfig(display, config_spec, configs, configs.length, config_num)) {
                for (int i = 0; i < config_num[0]; i++) {
                    EGLConfig config = configs[i];
                    int c_d = findConfigAttribute(egl, display, config, 12325, 0);
                    int c_s = findConfigAttribute(egl, display, config, 12326, 0);
                    if (c_d >= this.mDepthSize && c_s >= this.mStencilSize) {
                        int c_r = findConfigAttribute(egl, display, config, 12324, 0);
                        int c_g = findConfigAttribute(egl, display, config, 12323, 0);
                        int c_b = findConfigAttribute(egl, display, config, 12322, 0);
                        int c_a = findConfigAttribute(egl, display, config, 12321, 0);
                        if (c_r == this.mRedSize && c_g == this.mGreenSize && c_b == this.mBlueSize && c_a == this.mAlphaSize) {
                            CamLog.d(LOG_TAG, String.format("[%d] R:%d G:%d B:%d A:%d D:%d S:%d", new Object[]{Integer.valueOf(i), Integer.valueOf(c_r), Integer.valueOf(c_g), Integer.valueOf(c_b), Integer.valueOf(c_a), Integer.valueOf(c_d), Integer.valueOf(c_s)}));
                            return config;
                        }
                    }
                }
                return null;
            }
            throw new IllegalArgumentException("eglChooseConfig failed");
        }
    }

    private int[] createConfigSpec(GLESVersion version) {
        List<Integer> config_spec_list = new ArrayList();
        if (version == GLESVersion.GLES20) {
            config_spec_list.add(Integer.valueOf(12352));
            config_spec_list.add(Integer.valueOf(4));
        }
        config_spec_list.add(Integer.valueOf(12324));
        config_spec_list.add(Integer.valueOf(this.mRedSize));
        config_spec_list.add(Integer.valueOf(12323));
        config_spec_list.add(Integer.valueOf(this.mGreenSize));
        config_spec_list.add(Integer.valueOf(12322));
        config_spec_list.add(Integer.valueOf(this.mBlueSize));
        if (this.mAlphaSize > 0) {
            config_spec_list.add(Integer.valueOf(12321));
            config_spec_list.add(Integer.valueOf(this.mAlphaSize));
        }
        if (this.mDepthSize > 0) {
            config_spec_list.add(Integer.valueOf(12325));
            config_spec_list.add(Integer.valueOf(this.mDepthSize));
        }
        if (this.mStencilSize > 0) {
            config_spec_list.add(Integer.valueOf(12326));
            config_spec_list.add(Integer.valueOf(this.mStencilSize));
        }
        config_spec_list.add(Integer.valueOf(12344));
        int[] config_spec_array = new int[config_spec_list.size()];
        for (int i = 0; i < config_spec_list.size(); i++) {
            config_spec_array[i] = ((Integer) config_spec_list.get(i)).intValue();
        }
        return config_spec_array;
    }

    private int findConfigAttribute(EGL10 egl, EGLDisplay display, EGLConfig config, int attribute, int defaultValue) {
        int[] value = new int[1];
        if (egl.eglGetConfigAttrib(display, config, attribute, value)) {
            return value[0];
        }
        return defaultValue;
    }
}
