package com.lge.olaworks.library;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.datastruct.JOlaBitmap;
import com.lge.olaworks.define.Ola_ImageFormat;
import com.lge.olaworks.jni.OlaBitmapGraphicsJNI;
import com.lge.olaworks.jni.OlaFaceDetectorJNI;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class EngineProcessor {
    private static final String TAG = "CameraApp";
    private ArrayList<BaseEngine> mEngineList;
    private boolean mStart;

    public EngineProcessor() {
        this.mEngineList = new ArrayList();
        this.mStart = false;
    }

    public void start() {
        CamLog.v(TAG, "start()");
        Iterator i$ = this.mEngineList.iterator();
        while (i$.hasNext()) {
            if (((BaseEngine) i$.next()).getTag().equals(TAG)) {
                OlaFaceDetectorJNI.initialize();
            }
        }
        this.mStart = true;
    }

    public void stop() {
        CamLog.v(TAG, "stop()");
        this.mStart = false;
    }

    public void destroy() {
        releaseAllEngine();
    }

    public BaseEngine setEngine(BaseEngine engine) {
        return setEngine(engine, true);
    }

    public synchronized BaseEngine setEngine(BaseEngine engine, boolean start) {
        CamLog.d(TAG, "releaseAllEngine call in setEngine");
        releaseAllEngine();
        CamLog.d(TAG, "addEngine");
        addEngine(engine, start);
        this.mStart = start;
        return engine;
    }

    private BaseEngine addEngine(BaseEngine engine, boolean start) {
        return addEngine(engine, start, 10);
    }

    private BaseEngine addEngine(BaseEngine engine, boolean start, int priority) {
        CamLog.v(TAG, "addEngine(), engine = " + engine + ", priority = " + priority + ", start = " + start);
        if (engine != null) {
            BaseEngine baseEngine = engine;
            baseEngine.setPriority(priority);
            baseEngine.create();
            this.mEngineList.add(baseEngine);
        }
        return engine;
    }

    private synchronized BaseEngine releaseEngine(BaseEngine engine) {
        CamLog.d(TAG, "releaseAllEngine");
        if (engine != null) {
            engine.destroy();
            this.mEngineList.remove(engine);
            engine = null;
        }
        return engine;
    }

    public void releaseAllEngine() {
        Iterator i$ = this.mEngineList.iterator();
        while (i$.hasNext()) {
            releaseEngine((BaseEngine) i$.next());
        }
        this.mEngineList.clear();
    }

    public void releaseEngine(String tag) {
        if (tag != null) {
            Iterator i$ = this.mEngineList.iterator();
            while (i$.hasNext()) {
                BaseEngine engine = (BaseEngine) i$.next();
                if (tag.equals(engine.getTag())) {
                    releaseEngine(engine);
                }
            }
        }
    }

    public boolean isEmptyEngine() {
        return this.mEngineList.isEmpty();
    }

    public boolean checkEngineTag(String tag) {
        if (tag == null) {
            return false;
        }
        Iterator i$ = this.mEngineList.iterator();
        while (i$.hasNext()) {
            if (tag.equals(((BaseEngine) i$.next()).getTag())) {
                return true;
            }
        }
        return false;
    }

    public BaseEngine getEngine(String tag) {
        if (tag == null) {
            return null;
        }
        Iterator i$ = this.mEngineList.iterator();
        while (i$.hasNext()) {
            BaseEngine engine = (BaseEngine) i$.next();
            if (tag.equals(engine.getTag())) {
                return engine;
            }
        }
        return null;
    }

    public boolean needPreviewRender() {
        Iterator i$ = this.mEngineList.iterator();
        while (i$.hasNext()) {
            if (((BaseEngine) i$.next()).needRenderMode()) {
                return true;
            }
        }
        return false;
    }

    public void setFlipHorizontal(boolean flipH) {
        Iterator i$ = this.mEngineList.iterator();
        while (i$.hasNext()) {
            ((BaseEngine) i$.next()).setFlipHorizontal(flipH);
        }
    }

    public boolean processPreview(JOlaBitmap rawContext) {
        if (!this.mStart || rawContext == null) {
            return false;
        }
        Iterator i$ = this.mEngineList.iterator();
        while (i$.hasNext()) {
            BaseEngine engine = (BaseEngine) i$.next();
            int ret = engine.processPreview(rawContext);
            if (ret < 0) {
                CamLog.e(TAG, "Precess Preview Raw get Error. engine: " + engine.getTag() + ", val: " + ret);
            }
        }
        return true;
    }

    public void drawOverlay(Canvas canvas) {
        if (this.mStart) {
            Iterator i$ = this.mEngineList.iterator();
            while (i$.hasNext()) {
                ((BaseEngine) i$.next()).drawOverlay(canvas);
            }
        }
    }

    public byte[] processCapture(byte[] jpegData, Bitmap bitmap, int pictureFormat, int orientation) {
        if (pictureFormat != Ola_ImageFormat.RGB_LABEL || this.mEngineList.isEmpty()) {
            return jpegData;
        }
        Iterator i$ = this.mEngineList.iterator();
        while (i$.hasNext()) {
            BaseEngine engine = (BaseEngine) i$.next();
            int ret = engine.processImage(bitmap, orientation);
            if (ret < 0) {
                CamLog.d(TAG, "Process Image get Error. engine: " + engine.getTag() + ", val: " + ret);
                bitmap.recycle();
                return jpegData;
            }
        }
        if (checkEngineTag(TAG)) {
            bitmap.recycle();
            return jpegData;
        }
        ByteArrayOutputStream ostream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, 95, ostream);
        byte[] finalJpegData = Exif.processLoadExif(jpegData, ostream.toByteArray(), bitmap);
        bitmap.recycle();
        return finalJpegData;
    }

    public static byte[] jpegFlipH(byte[] jpegData) {
        Bitmap bitmap = ImageUtil.makeBitmap(jpegData);
        if (bitmap == null || OlaBitmapGraphicsJNI.mirrorYBitmap(bitmap) < 0) {
            return jpegData;
        }
        ByteArrayOutputStream ostream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, 95, ostream);
        byte[] retData = Exif.processLoadExif(jpegData, ostream.toByteArray(), bitmap);
        bitmap.recycle();
        return retData;
    }
}
