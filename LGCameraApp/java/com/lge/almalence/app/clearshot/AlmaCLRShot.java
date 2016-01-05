package com.lge.almalence.app.clearshot;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.FillType;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.YuvImage;
import com.lge.camera.components.RotateView;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.DialogCreater;
import com.lge.morpho.utils.multimedia.MediaProviderUtils;
import com.lge.olaworks.define.Ola_Exif.Tag;
import com.lge.olaworks.define.Ola_ShotParam;
import com.lge.voiceshutter.library.LGKeyRec;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class AlmaCLRShot {
    private static final int BITMAP_MARGIN = 6;
    private static final int DOWN_DIRECTION = 2;
    private static int IMAGE_TO_LAYOUT = 0;
    private static final int LEFT_DIRECTION = 3;
    private static final int MAX_INPUT_FRAME = 8;
    private static final int RIGHT_DIRECTION = 1;
    private static final int UP_DIRECTION = 0;
    private static AlmaCLRShot mInstance;
    private static final Object syncObject;
    private int[] ARGBBuffer;
    private final String TAG;
    private int mAngle;
    private byte[] mAutoLayout;
    private int[] mBaseArea;
    private int mBaseFrameIndex;
    private Rect[] mBoarderRect;
    private int[] mCrop;
    private byte[] mEnumObj;
    private int mGhosting;
    private ImageType mInputFrameFormat;
    private Size mInputFrameSize;
    private Size mLayoutSize;
    private byte[] mManualLayout;
    private int mMinSize;
    private int mNumOfFrame;
    private ObjBorderInfo[] mObjBorderInfo;
    private ObjectInfo[] mObjInfo;
    private OnProcessingListener mOnProcessingListener;
    private int mOutNV21;
    private Size mPreviewSize;
    private int mSensitivity;
    private int mTotalObj;

    public enum ImageType {
        JPEG,
        YUV420SP,
        YVU420SP
    }

    public class ObjBorderInfo {
        Rect mRect;
        Bitmap mThumb;

        public ObjBorderInfo() {
            this.mRect = null;
            this.mThumb = null;
        }

        public Rect getRect() {
            return this.mRect;
        }

        public Bitmap getThumbnail() {
            return this.mThumb;
        }
    }

    public class ObjectInfo {
        Rect objectRect;
        Bitmap thumbnail;

        public ObjectInfo() {
            this.objectRect = null;
            this.thumbnail = null;
        }

        public Rect getRect() {
            return this.objectRect;
        }

        public Bitmap getThumbnail() {
            return this.thumbnail;
        }
    }

    public interface OnProcessingListener {
        void onObjectCreated(ObjectInfo objectInfo);

        void onProcessingComplete(ObjectInfo[] objectInfoArr);
    }

    private static native int ConvertFromJpeg(int[] iArr, int[] iArr2, int i, int i2, int i3);

    private static native int[] ConvertToARGB(int i, int i2, int i3);

    private static native byte[] ConvertToJpeg(int i, int i2, int i3);

    private static native String Initialize();

    private static native byte[] JpegToNV21(byte[] bArr, int i, int i2);

    private static native int MovObjEnumerate(int i, Size size, byte[] bArr, byte[] bArr2, int i2);

    private static native int MovObjFixHoles(Size size, byte[] bArr, int i);

    private static native int MovObjProcess(int i, Size size, int i2, int i3, int[] iArr, int[] iArr2, byte[] bArr, int i4, int i5, int i6);

    private static native int[] NV12toARGB(int i, Size size, Rect rect, Size size2);

    private static native int[] NV21toARGB(int i, Size size, Rect rect, Size size2);

    private static native int Release(int i);

    private static native int getInputFrame(int i);

    private static native void setInputFrame(int i, int i2);

    static {
        IMAGE_TO_LAYOUT = MAX_INPUT_FRAME;
        syncObject = new Object();
        mInstance = new AlmaCLRShot();
        System.loadLibrary("almashot-clr");
    }

    private AlmaCLRShot() {
        this.TAG = getClass().getName().substring(getClass().getName().lastIndexOf(".") + RIGHT_DIRECTION);
        this.mBaseFrameIndex = 0;
        this.mPreviewSize = new Size(0, 0);
        this.mInputFrameSize = new Size(0, 0);
        this.mLayoutSize = new Size(0, 0);
        this.mNumOfFrame = 0;
        this.ARGBBuffer = null;
        this.mBaseArea = new int[4];
        this.mCrop = new int[5];
        this.mOutNV21 = 0;
        this.mObjInfo = null;
        this.mObjBorderInfo = null;
        this.mBoarderRect = null;
        this.mTotalObj = 0;
        this.mAutoLayout = new byte[RIGHT_DIRECTION];
        this.mManualLayout = new byte[RIGHT_DIRECTION];
        this.mEnumObj = new byte[RIGHT_DIRECTION];
        this.mInputFrameFormat = ImageType.JPEG;
    }

    public static AlmaCLRShot getInstance() {
        if (mInstance == null) {
            mInstance = new AlmaCLRShot();
        }
        return mInstance;
    }

    public int getNumOfFrame() {
        return this.mNumOfFrame;
    }

    public void addInputFrame(List<byte[]> inputFrame, Size size, ImageType e) throws Exception {
        this.mNumOfFrame = inputFrame.size();
        this.mInputFrameSize = size;
        this.mInputFrameFormat = e;
        if (this.mNumOfFrame > MAX_INPUT_FRAME) {
            CamLog.d(this.TAG, "Number of Input Frame = " + this.mNumOfFrame);
            throw new Exception("Too Many Input Frame");
        } else if (this.mInputFrameSize.isValid()) {
            if (((long) (this.mInputFrameSize.getWidth() * this.mInputFrameSize.getHeight())) >= 7680000) {
                IMAGE_TO_LAYOUT = 16;
            } else {
                IMAGE_TO_LAYOUT = MAX_INPUT_FRAME;
            }
            Initialize();
            synchronized (syncObject) {
                int i;
                if (this.mInputFrameFormat == ImageType.JPEG) {
                    int[] PointOfJpegData = new int[this.mNumOfFrame];
                    int[] LengthOfJpegData = new int[this.mNumOfFrame];
                    long start = System.currentTimeMillis();
                    for (i = 0; i < this.mNumOfFrame; i += RIGHT_DIRECTION) {
                        PointOfJpegData[i] = SwapHeap.SwapToHeap((byte[]) inputFrame.get(i));
                        LengthOfJpegData[i] = ((byte[]) inputFrame.get(i)).length;
                        if (PointOfJpegData[i] == 0) {
                            CamLog.d(this.TAG, "Out of Memory in Native");
                            throw new Exception("Out of Memory in Native");
                        }
                    }
                    int error = ConvertFromJpeg(PointOfJpegData, LengthOfJpegData, this.mNumOfFrame, size.getWidth(), size.getHeight());
                    CamLog.d(this.TAG, "ConvertFromJpeg() elapsed time = " + (System.currentTimeMillis() - start));
                    if (error < 0) {
                        CamLog.d(this.TAG, "Out Of Memory");
                        throw new Exception("Out Of Memory");
                    } else if (error < this.mNumOfFrame) {
                        CamLog.d(this.TAG, "JPEG buffer is wrong in " + error + " frame");
                        throw new Exception("Out Of Memory");
                    }
                } else if (this.mInputFrameFormat == ImageType.YUV420SP || this.mInputFrameFormat == ImageType.YVU420SP) {
                    for (i = 0; i < this.mNumOfFrame; i += RIGHT_DIRECTION) {
                        if (((byte[]) inputFrame.get(i)).length != ((size.getWidth() * size.getHeight()) * LEFT_DIRECTION) / DOWN_DIRECTION) {
                            throw new Exception("Input Frame Size is wrong" + i + " frame");
                        }
                        setInputFrame(SwapHeap.SwapToHeap((byte[]) inputFrame.get(i)), i);
                    }
                } else {
                    throw new Exception("Unknown Input Format");
                }
            }
        } else {
            CamLog.d(this.TAG, "Input frame size is wrong ");
            throw new Exception("Too Many Input Frame");
        }
    }

    public boolean initialize(Size previewSize, int angle, int baseFrame, int sensitivity, int minSize, int ghosting, OnProcessingListener listener) throws Exception {
        CamLog.d(this.TAG, "initialize() -- start");
        this.mGhosting = ghosting;
        this.mPreviewSize = previewSize;
        this.mBaseFrameIndex = baseFrame;
        this.mSensitivity = sensitivity;
        this.mMinSize = minSize;
        this.mOnProcessingListener = listener;
        this.mAngle = angle;
        this.mLayoutSize = new Size(this.mInputFrameSize.getWidth() / IMAGE_TO_LAYOUT, this.mInputFrameSize.getHeight() / IMAGE_TO_LAYOUT);
        if (this.mAngle != 0 && this.mAngle != 90 && this.mAngle != MediaProviderUtils.ROTATION_180 && this.mAngle != Tag.IMAGE_DESCRIPTION) {
            CamLog.d(this.TAG, "Angle is invalid");
            throw new Exception("Angle is invalid");
        } else if (!this.mPreviewSize.isValid()) {
            CamLog.d(this.TAG, "Preview size is wrong");
            throw new Exception("Too Many Input Frame");
        } else if (this.mSensitivity < -15 || this.mSensitivity > 15) {
            CamLog.d(this.TAG, "Sensitivity value is wrong");
            throw new Exception("Sensitivity value is wrong");
        } else if (this.mMinSize < 0 || this.mMinSize > this.mInputFrameSize.getWidth() * this.mInputFrameSize.getHeight()) {
            CamLog.d(this.TAG, "MinSize value is wrong");
            throw new Exception("Sensitivity value is wrong");
        } else {
            int length = this.mLayoutSize.getWidth() * this.mLayoutSize.getHeight();
            this.mAutoLayout = new byte[length];
            this.mManualLayout = new byte[length];
            this.mEnumObj = new byte[length];
            this.mCrop = new int[5];
            this.mBaseArea = new int[4];
            this.mCrop[4] = this.mBaseFrameIndex;
            for (int i = 0; i < length; i += RIGHT_DIRECTION) {
                this.mAutoLayout[i] = (byte) this.mBaseFrameIndex;
            }
            this.mAutoLayout[0] = (byte) -1;
            removeProcessing(this.mAutoLayout);
            updateLayout();
            CamLog.d(this.TAG, "initialize() -- end");
            return true;
        }
    }

    private Rect rotateObjRect(Rect rect) {
        if (this.mAngle == 0) {
            return rect;
        }
        Rect newRect = null;
        switch (this.mAngle) {
            case MediaProviderUtils.ROTATION_90 /*90*/:
                newRect = new Rect(this.mInputFrameSize.getHeight() - rect.bottom, rect.left, this.mInputFrameSize.getHeight() - rect.top, rect.right);
                break;
            case MediaProviderUtils.ROTATION_180 /*180*/:
                newRect = new Rect(this.mInputFrameSize.getWidth() - rect.right, this.mInputFrameSize.getHeight() - rect.bottom, this.mInputFrameSize.getWidth() - rect.left, this.mInputFrameSize.getHeight() - rect.top);
                break;
            case Tag.IMAGE_DESCRIPTION /*270*/:
                newRect = new Rect(rect.top, this.mInputFrameSize.getWidth() - rect.left, rect.bottom, this.mInputFrameSize.getWidth() - rect.left);
                break;
        }
        return newRect;
    }

    private Rect rotateRect(Rect rect) {
        if (this.mAngle == 0) {
            return rect;
        }
        Rect newRect = null;
        switch (this.mAngle) {
            case MediaProviderUtils.ROTATION_90 /*90*/:
                newRect = new Rect(this.mPreviewSize.getHeight() - rect.bottom, rect.left, this.mPreviewSize.getHeight() - rect.top, rect.right);
                break;
            case MediaProviderUtils.ROTATION_180 /*180*/:
                newRect = new Rect(this.mPreviewSize.getWidth() - rect.right, this.mPreviewSize.getHeight() - rect.bottom, this.mPreviewSize.getWidth() - rect.left, this.mPreviewSize.getHeight() - rect.top);
                break;
            case Tag.IMAGE_DESCRIPTION /*270*/:
                newRect = new Rect(rect.top, this.mPreviewSize.getWidth() - rect.left, rect.bottom, this.mPreviewSize.getWidth() - rect.left);
                break;
        }
        return newRect;
    }

    public synchronized ObjectInfo[] getObjectInfoList() {
        ObjectInfo[] objectInfoArr;
        CamLog.d(this.TAG, "getObjectInfoList() -- start");
        long start = System.currentTimeMillis();
        if (this.mTotalObj == 0) {
            objectInfoArr = new ObjectInfo[0];
        } else if (this.mObjInfo != null) {
            objectInfoArr = this.mObjInfo;
        } else {
            this.mObjInfo = new ObjectInfo[this.mTotalObj];
            if (this.mBoarderRect == null) {
                this.mBoarderRect = scanLayoutforRect();
            }
            float ratio = ((float) this.mPreviewSize.getWidth()) / (((float) this.mInputFrameSize.getWidth()) / ((float) IMAGE_TO_LAYOUT));
            for (int i = 0; i < this.mTotalObj; i += RIGHT_DIRECTION) {
                CamLog.d(this.TAG, "mObjInfo[" + i + "]");
                Rect orgRect = new Rect(Math.round((float) ((this.mBoarderRect[i].left * IMAGE_TO_LAYOUT) - 6)), Math.round((float) ((this.mBoarderRect[i].top * IMAGE_TO_LAYOUT) - 6)), Math.round((float) ((this.mBoarderRect[i].right * IMAGE_TO_LAYOUT) + BITMAP_MARGIN)), Math.round((float) ((this.mBoarderRect[i].bottom * IMAGE_TO_LAYOUT) + BITMAP_MARGIN)));
                orgRect.setIntersect(orgRect, new Rect(0, 0, this.mInputFrameSize.getWidth() - 1, this.mInputFrameSize.getHeight() - 1));
                Rect PreviewRect = new Rect(Math.round((((float) this.mBoarderRect[i].left) * ratio) - 6.0f), Math.round((((float) this.mBoarderRect[i].top) * ratio) - 6.0f), Math.round((((float) this.mBoarderRect[i].right) * ratio) + 6.0f), Math.round((((float) this.mBoarderRect[i].bottom) * ratio) + 6.0f));
                PreviewRect.setIntersect(PreviewRect, new Rect(0, 0, this.mPreviewSize.getWidth() - 1, this.mPreviewSize.getHeight() - 1));
                this.mObjInfo[i] = new ObjectInfo();
                this.mObjInfo[i].thumbnail = getObjectBitmap(i, orgRect);
                this.mObjInfo[i].objectRect = rotateRect(PreviewRect);
                if (this.mOnProcessingListener != null) {
                    this.mOnProcessingListener.onObjectCreated(this.mObjInfo[i]);
                }
            }
            if (this.mOnProcessingListener != null) {
                this.mOnProcessingListener.onProcessingComplete(this.mObjInfo);
            }
            CamLog.d(this.TAG, "getObjectInfoList() elapsed time = " + (System.currentTimeMillis() - start));
            CamLog.d(this.TAG, "getObjectInfoList() -- end");
            objectInfoArr = this.mObjInfo;
        }
        return objectInfoArr;
    }

    private Bitmap rotateBitmap(Bitmap b, int w, int h, int angle) {
        if (b == null || angle == 0) {
            return b;
        }
        Matrix matrix = new Matrix();
        matrix.preRotate((float) angle);
        Bitmap rotImage = Bitmap.createBitmap(b, 0, 0, w, h, matrix, true);
        b.recycle();
        return rotImage;
    }

    public Bitmap getPreviewBitmap() {
        CamLog.d(this.TAG, "getPreviewBitmap() -- start");
        Bitmap bitmap = Bitmap.createBitmap(this.mPreviewSize.getWidth(), this.mPreviewSize.getHeight(), Config.ARGB_8888);
        this.ARGBBuffer = NV21toARGB(this.mOutNV21, this.mInputFrameSize, new Rect(0, 0, this.mInputFrameSize.getWidth(), this.mInputFrameSize.getHeight()), this.mPreviewSize);
        bitmap.setPixels(this.ARGBBuffer, 0, this.mPreviewSize.getWidth(), 0, 0, this.mPreviewSize.getWidth(), this.mPreviewSize.getHeight());
        this.ARGBBuffer = null;
        CamLog.d(this.TAG, "getPreviewBitmap() -- end");
        return rotateBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), this.mAngle);
    }

    public int getTotalObjNum() {
        return this.mTotalObj;
    }

    private int getLayoutPos(float xx, float yy) {
        float w = (float) this.mLayoutSize.getWidth();
        return (((int) w) * ((int) ((yy * ((float) this.mLayoutSize.getHeight())) / ((float) this.mPreviewSize.getHeight())))) + ((int) ((xx * w) / ((float) this.mPreviewSize.getWidth())));
    }

    private int[] getReversePos(int pos) {
        float w = (float) this.mLayoutSize.getWidth();
        float h = (float) this.mLayoutSize.getHeight();
        int y1 = (int) Math.ceil((double) ((((float) (pos / ((int) w))) * ((float) this.mPreviewSize.getHeight())) / h));
        int[] iArr = new int[DOWN_DIRECTION];
        iArr[0] = (int) Math.ceil((double) ((((float) (pos % ((int) w))) * ((float) this.mPreviewSize.getWidth())) / w));
        iArr[RIGHT_DIRECTION] = y1;
        return iArr;
    }

    private int getLayoutXPos(float xx) {
        return (int) ((xx * ((float) this.mLayoutSize.getWidth())) / ((float) this.mPreviewSize.getWidth()));
    }

    private int getLayoutYPos(float yy) {
        return (int) ((yy * ((float) this.mLayoutSize.getHeight())) / ((float) this.mPreviewSize.getHeight()));
    }

    private Rect[] scanLayoutforRect() {
        int width = this.mLayoutSize.getWidth();
        int height = this.mLayoutSize.getHeight();
        Rect[] rect = new Rect[this.mTotalObj];
        for (int i = 0; i < this.mTotalObj; i += RIGHT_DIRECTION) {
            rect[i] = new Rect(-1, -1, -1, -1);
        }
        for (int y = 0; y < height; y += RIGHT_DIRECTION) {
            for (int x = 0; x < width; x += RIGHT_DIRECTION) {
                int obj = this.mEnumObj[(y * width) + x];
                if (obj > 0) {
                    if (rect[obj - 1].left > x || rect[obj - 1].left == -1) {
                        rect[obj - 1].left = x;
                    }
                    if (rect[obj - 1].right < x || rect[obj - 1].right == -1) {
                        rect[obj - 1].right = x;
                    }
                    if (rect[obj - 1].top > y || rect[obj - 1].top == -1) {
                        rect[obj - 1].top = y;
                    }
                    if (rect[obj - 1].bottom < y || rect[obj - 1].bottom == -1) {
                        rect[obj - 1].bottom = y;
                    }
                }
            }
        }
        return rect;
    }

    private Bitmap getObjBorderSource(int index, Paint paint, Rect rect) {
        int i = 0;
        int x = 0;
        int y = 0;
        int w = rect.width();
        int h = rect.height();
        int last_direction = RIGHT_DIRECTION;
        byte[] tmpBuffer = new byte[(rect.width() * rect.height())];
        Bitmap bitmap = Bitmap.createBitmap(rect.width(), rect.height(), Config.ARGB_8888);
        for (int yy = rect.bottom - 1; yy >= rect.top; yy--) {
            for (int xx = rect.right - 1; xx >= rect.left; xx--) {
                int pos = getLayoutPos((float) xx, (float) yy);
                int new_pos = (xx - rect.left) + (rect.width() * (yy - rect.top));
                if (this.mBaseFrameIndex != this.mAutoLayout[pos]) {
                    tmpBuffer[new_pos] = this.mEnumObj[pos];
                    if (tmpBuffer[new_pos] == index) {
                        x = xx - rect.left;
                        y = yy - rect.top;
                    }
                }
            }
        }
        List<Point> ListArray = new ArrayList();
        int x_starting = x;
        int y_starting = y;
        ListArray.add(new Point(x, y));
        while (true) {
            switch (last_direction) {
                case LGKeyRec.EVENT_INVALID /*0*/:
                    if (y >= h - 1 || x < RIGHT_DIRECTION || tmpBuffer[(x - 1) + ((y + RIGHT_DIRECTION) * w)] != index) {
                        if (x < RIGHT_DIRECTION || tmpBuffer[(x - 1) + (w * y)] != index) {
                            if (x < RIGHT_DIRECTION || y < RIGHT_DIRECTION || tmpBuffer[(x - 1) + ((y - 1) * w)] != index) {
                                if (y < RIGHT_DIRECTION || tmpBuffer[((y - 1) * w) + x] != index) {
                                    if (x >= w - 1 || y < RIGHT_DIRECTION || tmpBuffer[(x + RIGHT_DIRECTION) + ((y - 1) * w)] != index) {
                                        if (x >= w - 1 || tmpBuffer[(x + RIGHT_DIRECTION) + (w * y)] != index) {
                                            if (x < w - 1 && y < h - 1 && tmpBuffer[(x + RIGHT_DIRECTION) + ((y + RIGHT_DIRECTION) * w)] == index) {
                                                x += RIGHT_DIRECTION;
                                                y += RIGHT_DIRECTION;
                                                last_direction = DOWN_DIRECTION;
                                                break;
                                            }
                                        }
                                        x += RIGHT_DIRECTION;
                                        last_direction = DOWN_DIRECTION;
                                        break;
                                    }
                                    x += RIGHT_DIRECTION;
                                    y--;
                                    last_direction = RIGHT_DIRECTION;
                                    break;
                                }
                                y--;
                                last_direction = RIGHT_DIRECTION;
                                break;
                            }
                            x--;
                            y--;
                            last_direction = 0;
                            break;
                        }
                        last_direction = 0;
                        x--;
                        break;
                    }
                    x--;
                    y += RIGHT_DIRECTION;
                    last_direction = LEFT_DIRECTION;
                    break;
                    break;
                case RIGHT_DIRECTION /*1*/:
                    if (x < RIGHT_DIRECTION || y < RIGHT_DIRECTION || tmpBuffer[(x - 1) + ((y - 1) * w)] != index) {
                        if (y < RIGHT_DIRECTION || tmpBuffer[((y - 1) * w) + x] != index) {
                            if (x >= w - 1 || y < RIGHT_DIRECTION || tmpBuffer[(x + RIGHT_DIRECTION) + ((y - 1) * w)] != index) {
                                if (x >= w - 1 || tmpBuffer[(x + RIGHT_DIRECTION) + (w * y)] != index) {
                                    if (x >= w - 1 || y >= h - 1 || tmpBuffer[(x + RIGHT_DIRECTION) + ((y + RIGHT_DIRECTION) * w)] != index) {
                                        if (y >= h - 1 || tmpBuffer[((y + RIGHT_DIRECTION) * w) + x] != index) {
                                            if (y < h - 1 && x >= RIGHT_DIRECTION && tmpBuffer[(x - 1) + ((y + RIGHT_DIRECTION) * w)] == index) {
                                                x--;
                                                y += RIGHT_DIRECTION;
                                                last_direction = LEFT_DIRECTION;
                                                break;
                                            }
                                        }
                                        y += RIGHT_DIRECTION;
                                        last_direction = LEFT_DIRECTION;
                                        break;
                                    }
                                    x += RIGHT_DIRECTION;
                                    y += RIGHT_DIRECTION;
                                    last_direction = DOWN_DIRECTION;
                                    break;
                                }
                                x += RIGHT_DIRECTION;
                                last_direction = DOWN_DIRECTION;
                                break;
                            }
                            x += RIGHT_DIRECTION;
                            y--;
                            last_direction = RIGHT_DIRECTION;
                            break;
                        }
                        y--;
                        last_direction = RIGHT_DIRECTION;
                        break;
                    }
                    x--;
                    y--;
                    last_direction = 0;
                    break;
                    break;
                case DOWN_DIRECTION /*2*/:
                    if (x >= w - 1 || y < RIGHT_DIRECTION || tmpBuffer[(x + RIGHT_DIRECTION) + ((y - 1) * w)] != index) {
                        if (x >= w - 1 || tmpBuffer[(x + RIGHT_DIRECTION) + (w * y)] != index) {
                            if (x >= w - 1 || y >= h - 1 || tmpBuffer[(x + RIGHT_DIRECTION) + ((y + RIGHT_DIRECTION) * w)] != index) {
                                if (y >= h - 1 || tmpBuffer[((y + RIGHT_DIRECTION) * w) + x] != index) {
                                    if (y >= h - 1 || x < RIGHT_DIRECTION || tmpBuffer[(x - 1) + ((y + RIGHT_DIRECTION) * w)] != index) {
                                        if (x < RIGHT_DIRECTION || tmpBuffer[(x - 1) + (w * y)] != index) {
                                            if (x >= RIGHT_DIRECTION && y >= RIGHT_DIRECTION && tmpBuffer[(x - 1) + ((y - 1) * w)] == index) {
                                                x--;
                                                y--;
                                                last_direction = 0;
                                                break;
                                            }
                                        }
                                        x--;
                                        last_direction = 0;
                                        break;
                                    }
                                    x--;
                                    y += RIGHT_DIRECTION;
                                    last_direction = LEFT_DIRECTION;
                                    break;
                                }
                                y += RIGHT_DIRECTION;
                                last_direction = LEFT_DIRECTION;
                                break;
                            }
                            x += RIGHT_DIRECTION;
                            y += RIGHT_DIRECTION;
                            last_direction = DOWN_DIRECTION;
                            break;
                        }
                        x += RIGHT_DIRECTION;
                        last_direction = DOWN_DIRECTION;
                        break;
                    }
                    x += RIGHT_DIRECTION;
                    y--;
                    last_direction = RIGHT_DIRECTION;
                    break;
                    break;
                case LEFT_DIRECTION /*3*/:
                    if (x >= w - 1 || y >= h - 1 || tmpBuffer[(x + RIGHT_DIRECTION) + ((y + RIGHT_DIRECTION) * w)] != index) {
                        if (y >= h - 1 || tmpBuffer[((y + RIGHT_DIRECTION) * w) + x] != index) {
                            if (y >= h - 1 || x < RIGHT_DIRECTION || tmpBuffer[(x - 1) + ((y + RIGHT_DIRECTION) * w)] != index) {
                                if (x < RIGHT_DIRECTION || tmpBuffer[(x - 1) + (w * y)] != index) {
                                    if (x < RIGHT_DIRECTION || y < RIGHT_DIRECTION || tmpBuffer[(x - 1) + ((y - 1) * w)] != index) {
                                        if (y < RIGHT_DIRECTION || tmpBuffer[((y - 1) * w) + x] != index) {
                                            if (x < w - 1 && y >= RIGHT_DIRECTION && tmpBuffer[(x + RIGHT_DIRECTION) + ((y - 1) * w)] == index) {
                                                x += RIGHT_DIRECTION;
                                                y--;
                                                last_direction = RIGHT_DIRECTION;
                                                break;
                                            }
                                        }
                                        y--;
                                        last_direction = RIGHT_DIRECTION;
                                        break;
                                    }
                                    x--;
                                    y--;
                                    last_direction = 0;
                                    break;
                                }
                                last_direction = 0;
                                x--;
                                break;
                            }
                            x--;
                            y += RIGHT_DIRECTION;
                            last_direction = LEFT_DIRECTION;
                            break;
                        }
                        y += RIGHT_DIRECTION;
                        last_direction = LEFT_DIRECTION;
                        break;
                    }
                    x += RIGHT_DIRECTION;
                    y += RIGHT_DIRECTION;
                    last_direction = DOWN_DIRECTION;
                    break;
                    break;
            }
            i += RIGHT_DIRECTION;
            if (i % 20 == 19) {
                ListArray.add(new Point(x, y));
            }
            if (x == x_starting && y == y_starting) {
                Path path = new Path();
                path.setFillType(FillType.EVEN_ODD);
                path.reset();
                Point p = (Point) ListArray.get(0);
                ListArray.remove(0);
                path.moveTo((float) p.x, (float) p.y);
                for (Point point : ListArray) {
                    path.lineTo((float) point.x, (float) point.y);
                }
                path.lineTo((float) x_starting, (float) y_starting);
                Canvas canvas = new Canvas(bitmap);
                paint.setColor(Color.argb(76, 70, 204, Ola_ShotParam.AnimalMask_Random));
                paint.setStyle(Style.FILL);
                canvas.drawPath(path, paint);
                paint.setColor(Color.argb(MediaProviderUtils.ROTATION_180, 0, 85, DialogCreater.DIALOG_ID_HELP_HDR_MOVIE));
                paint.setStyle(Style.STROKE);
                canvas.drawPath(path, paint);
                return bitmap;
            }
        }
    }

    public synchronized ObjBorderInfo[] getObjBorderBitmap(Paint paint) {
        ObjBorderInfo[] objBorderInfoArr;
        CamLog.d(this.TAG, "getObjBoundaryBitmap() -- start");
        if (this.mObjBorderInfo != null) {
            objBorderInfoArr = this.mObjBorderInfo;
        } else {
            this.mObjBorderInfo = new ObjBorderInfo[this.mTotalObj];
            if (this.mBoarderRect == null) {
                this.mBoarderRect = scanLayoutforRect();
            }
            float ratio = ((float) this.mPreviewSize.getWidth()) / (((float) this.mInputFrameSize.getWidth()) / ((float) IMAGE_TO_LAYOUT));
            int i = 0;
            while (i < this.mTotalObj) {
                CamLog.d(this.TAG, "mObjBorderInfo[" + i + "]");
                if (!(this.mObjInfo == null || this.mObjInfo[i].thumbnail == null)) {
                    Rect PreviewRect = new Rect(Math.round((((float) this.mBoarderRect[i].left) * ratio) - 5.0f), Math.round((((float) this.mBoarderRect[i].top) * ratio) - 5.0f), Math.round((((float) this.mBoarderRect[i].right) * ratio) + 5.0f), Math.round((((float) this.mBoarderRect[i].bottom) * ratio) + 5.0f));
                    Rect LayoutRect = new Rect(Math.round((((float) this.mBoarderRect[i].left) * ratio) - 5.0f), Math.round((((float) this.mBoarderRect[i].top) * ratio) - 5.0f), Math.round((((float) this.mBoarderRect[i].right) * ratio) + 5.0f), Math.round((((float) this.mBoarderRect[i].bottom) * ratio) + 5.0f));
                    PreviewRect.setIntersect(PreviewRect, new Rect(0, 0, this.mPreviewSize.getWidth() - 1, this.mPreviewSize.getHeight() - 1));
                    LayoutRect.setIntersect(LayoutRect, new Rect(0, 0, this.mPreviewSize.getWidth() - 1, this.mPreviewSize.getHeight() - 1));
                    this.mObjBorderInfo[i] = new ObjBorderInfo();
                    this.mObjBorderInfo[i].mRect = rotateRect(PreviewRect);
                    if (this.mObjInfo == null || this.mObjInfo[i].thumbnail == null) {
                        this.mObjBorderInfo[i].mThumb = null;
                    } else {
                        this.mObjBorderInfo[i].mThumb = rotateBitmap(getObjBorderSource(i + RIGHT_DIRECTION, paint, LayoutRect), LayoutRect.width(), LayoutRect.height(), this.mAngle);
                    }
                }
                i += RIGHT_DIRECTION;
            }
            objBorderInfoArr = this.mObjBorderInfo;
        }
        return objBorderInfoArr;
    }

    public void setObjectList(boolean[] objectIndex) throws Exception {
        CamLog.d(this.TAG, "setObjectList() -- start");
        long start = System.currentTimeMillis();
        if (objectIndex.length > this.mTotalObj) {
            int length = objectIndex.length;
            CamLog.d(this.TAG, "objectIndex.length = " + r0 + ", mTotalObj = " + this.mTotalObj);
            CamLog.d(this.TAG, "object index is greater than total number of object");
            throw new Exception("object index is greater than total number of object");
        }
        int width = this.mLayoutSize.getWidth();
        int height = this.mLayoutSize.getHeight();
        int obj = 0;
        boolean[] arr$ = objectIndex;
        int len$ = arr$.length;
        for (int i$ = 0; i$ < len$; i$ += RIGHT_DIRECTION) {
            boolean isRemoved = arr$[i$];
            for (int yy = 0; yy < height; yy += RIGHT_DIRECTION) {
                for (int xx = 0; xx < width; xx += RIGHT_DIRECTION) {
                    int xy = xx + (yy * width);
                    if (this.mEnumObj[xy] == obj + RIGHT_DIRECTION) {
                        if (isRemoved) {
                            this.mManualLayout[xy] = this.mAutoLayout[xy];
                        } else {
                            this.mManualLayout[xy] = (byte) this.mBaseFrameIndex;
                        }
                    }
                }
            }
            obj += RIGHT_DIRECTION;
        }
        removeProcessing(this.mManualLayout);
        CamLog.d(this.TAG, "setObjectList() elapsed time = " + (System.currentTimeMillis() - start));
        CamLog.d(this.TAG, "setObjectList() -- end");
    }

    public void setObject(int objectIndex, boolean removed) throws Exception {
        CamLog.d(this.TAG, "setObject() -- start");
        long start = System.currentTimeMillis();
        if (objectIndex > this.mTotalObj) {
            CamLog.d(this.TAG, "object index is greater than total number of object");
            throw new Exception("object index is greater than total number of object");
        }
        int width = this.mLayoutSize.getWidth();
        int height = this.mLayoutSize.getHeight();
        for (int yy = 0; yy < height; yy += RIGHT_DIRECTION) {
            for (int xx = 0; xx < width; xx += RIGHT_DIRECTION) {
                int xy = xx + (yy * width);
                if (this.mEnumObj[xy] == objectIndex) {
                    if (removed) {
                        this.mManualLayout[xy] = this.mAutoLayout[xy];
                    } else {
                        this.mManualLayout[xy] = (byte) this.mBaseFrameIndex;
                    }
                }
            }
        }
        removeProcessing(this.mManualLayout);
        CamLog.d(this.TAG, "setObject() elapsed time = " + (System.currentTimeMillis() - start));
        CamLog.d(this.TAG, "setObject() -- end");
    }

    public void reverseObject(float lx, float ly) {
        int layoutW = this.mLayoutSize.getWidth();
        int layoutH = this.mLayoutSize.getHeight();
        byte obj = this.mEnumObj[getLayoutPos(lx, ly)];
        if (obj != (byte) 0) {
            for (int yy = 0; yy < layoutH; yy += RIGHT_DIRECTION) {
                for (int xx = 0; xx < layoutW; xx += RIGHT_DIRECTION) {
                    int xy = xx + (yy * layoutW);
                    if (this.mEnumObj[xy] == obj) {
                        if (this.mManualLayout[xy] == this.mAutoLayout[xy]) {
                            this.mManualLayout[xy] = (byte) this.mBaseFrameIndex;
                        } else {
                            this.mManualLayout[xy] = this.mAutoLayout[xy];
                        }
                    }
                }
            }
            removeProcessing(this.mManualLayout);
        }
    }

    public int getOccupiedObject(float lx, float ly) throws Exception {
        float x = 0.0f;
        float y = 0.0f;
        switch (this.mAngle) {
            case LGKeyRec.EVENT_INVALID /*0*/:
                x = lx;
                y = ly;
                break;
            case MediaProviderUtils.ROTATION_90 /*90*/:
                x = ly;
                y = ((float) this.mPreviewSize.getHeight()) - lx;
                break;
            case MediaProviderUtils.ROTATION_180 /*180*/:
                x = ((float) this.mPreviewSize.getWidth()) - lx;
                y = ((float) this.mPreviewSize.getHeight()) - ly;
                break;
            case Tag.IMAGE_DESCRIPTION /*270*/:
                x = ((float) this.mPreviewSize.getHeight()) - ly;
                y = lx;
                break;
        }
        if (x <= ((float) this.mPreviewSize.getWidth()) && y <= ((float) this.mPreviewSize.getHeight())) {
            return this.mEnumObj[getLayoutPos(x, y)];
        }
        CamLog.d(this.TAG, "Coordiation is invalid x = " + x + " y = " + y);
        throw new Exception("Invalid touch position");
    }

    public boolean isRemoved(int x, int y) throws Exception {
        int previewW = this.mPreviewSize.getWidth();
        int previewH = this.mPreviewSize.getHeight();
        if (x < 0 || x > previewW || y < 0 || y > previewH) {
            int pos = getLayoutPos((float) x, (float) y);
            return this.mManualLayout[pos] != this.mAutoLayout[pos];
        } else {
            CamLog.d(this.TAG, "Invalid touch position");
            throw new Exception("Invalid touch position");
        }
    }

    public void setBaseFrame(int base) throws Exception {
        if (base >= this.mNumOfFrame) {
            CamLog.d(this.TAG, "Invalid Base Frame");
            throw new Exception("Invalid Base Frame");
        }
        int[] iArr = this.mCrop;
        this.mBaseFrameIndex = base;
        iArr[4] = base;
        for (int i = 0; i < this.mAutoLayout.length; i += RIGHT_DIRECTION) {
            this.mAutoLayout[i] = (byte) this.mBaseFrameIndex;
        }
        this.mAutoLayout[0] = (byte) -1;
        removeProcessing(this.mAutoLayout);
        updateLayout();
        this.mObjInfo = null;
        this.mObjBorderInfo = null;
        this.mBoarderRect = null;
    }

    public boolean isRemoved(int ObjIndex) {
        int width = this.mLayoutSize.getWidth();
        int height = this.mLayoutSize.getHeight();
        for (int yy = 0; yy < height; yy += RIGHT_DIRECTION) {
            int xx = 0;
            while (xx < width) {
                int xy = xx + (yy * width);
                if (this.mEnumObj[xy] != ObjIndex) {
                    xx += RIGHT_DIRECTION;
                } else if (this.mManualLayout[xy] != this.mAutoLayout[xy]) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    public byte[] processingSaveData() {
        YuvImage out = new YuvImage(SwapHeap.SwapFromHeap(this.mOutNV21, ((this.mInputFrameSize.getWidth() * this.mInputFrameSize.getHeight()) * LEFT_DIRECTION) / DOWN_DIRECTION), 17, this.mInputFrameSize.getWidth(), this.mInputFrameSize.getHeight(), null);
        this.mOutNV21 = 0;
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            if (!out.compressToJpeg(new Rect(this.mCrop[0], this.mCrop[RIGHT_DIRECTION], this.mCrop[0] + this.mCrop[DOWN_DIRECTION], this.mCrop[RIGHT_DIRECTION] + this.mCrop[LEFT_DIRECTION]), 95, os)) {
                CamLog.d(this.TAG, "the compression is not successful");
            }
            byte[] jpegBuffer = os.toByteArray();
            os.close();
            return jpegBuffer;
        } catch (Exception e) {
            CamLog.d(this.TAG, "Exception occured");
            e.printStackTrace();
            return null;
        }
    }

    public void release() {
        CamLog.d(this.TAG, "release() start : mNumOfFrame = " + this.mNumOfFrame);
        synchronized (syncObject) {
            if (this.mNumOfFrame > 0) {
                Release(this.mNumOfFrame);
            }
            this.mPreviewSize = null;
            this.mInputFrameSize = null;
            this.mLayoutSize = null;
            this.ARGBBuffer = null;
            this.mCrop = null;
            this.mObjInfo = null;
            this.mObjBorderInfo = null;
            this.mBoarderRect = null;
            this.mAutoLayout = null;
            this.mManualLayout = null;
            this.mEnumObj = null;
            this.mNumOfFrame = 0;
            if (this.mOutNV21 != 0) {
                SwapHeap.FreeFromHeap(this.mOutNV21);
                this.mOutNV21 = 0;
            }
            try {
                finalize();
            } catch (Throwable e) {
                CamLog.d(this.TAG, "Instance is not finalized correctly");
                e.printStackTrace();
            }
            mInstance = null;
        }
        CamLog.d(this.TAG, "release() end");
    }

    private synchronized void removeProcessing(byte[] layout) {
        if (this.mOutNV21 != 0) {
            SwapHeap.FreeFromHeap(this.mOutNV21);
            this.mOutNV21 = 0;
        }
        long start = System.currentTimeMillis();
        if (this.mInputFrameFormat == ImageType.YUV420SP) {
            this.mOutNV21 = MovObjProcess(this.mNumOfFrame, this.mInputFrameSize, this.mSensitivity, this.mMinSize, this.mBaseArea, this.mCrop, layout, RIGHT_DIRECTION, this.mGhosting, IMAGE_TO_LAYOUT);
        } else {
            this.mOutNV21 = MovObjProcess(this.mNumOfFrame, this.mInputFrameSize, this.mSensitivity, this.mMinSize, this.mBaseArea, this.mCrop, layout, 0, this.mGhosting, IMAGE_TO_LAYOUT);
        }
        CamLog.d(this.TAG, "MovObjProcess() elapsed time = " + (System.currentTimeMillis() - start));
        CamLog.d(this.TAG, "mCrop[0] = " + this.mCrop[0] + " mCrop[1] = " + this.mCrop[RIGHT_DIRECTION] + " mCrop[2] = " + this.mCrop[DOWN_DIRECTION] + " mCrop[3] = " + this.mCrop[LEFT_DIRECTION]);
        this.mBaseFrameIndex = this.mCrop[4];
    }

    private synchronized void updateLayout() {
        System.arraycopy(this.mAutoLayout, 0, this.mManualLayout, 0, this.mAutoLayout.length);
        this.mTotalObj = MovObjEnumerate(this.mNumOfFrame, this.mLayoutSize, this.mManualLayout, this.mEnumObj, this.mBaseFrameIndex);
        MovObjFixHoles(this.mLayoutSize, this.mEnumObj, 0);
        MovObjFixHoles(this.mLayoutSize, this.mAutoLayout, this.mBaseFrameIndex);
    }

    private Bitmap getObjectBitmap(int objectIndex, Rect rect) {
        float ratio = ((float) this.mInputFrameSize.getWidth()) / ((float) this.mPreviewSize.getWidth());
        if (((float) rect.width()) / ratio < RotateView.DEFAULT_TEXT_SCALE_X || ((float) rect.height()) / ratio < RotateView.DEFAULT_TEXT_SCALE_X || rect.left < 0 || rect.right < 0 || rect.top < 0 || rect.bottom < 0) {
            return null;
        }
        int[] buffer;
        Rect rect2 = new Rect(((rect.left * this.mBaseArea[DOWN_DIRECTION]) / this.mInputFrameSize.getWidth()) + this.mBaseArea[0], ((rect.top * this.mBaseArea[LEFT_DIRECTION]) / this.mInputFrameSize.getHeight()) + this.mBaseArea[RIGHT_DIRECTION], (((rect.left * this.mBaseArea[DOWN_DIRECTION]) / this.mInputFrameSize.getWidth()) + this.mBaseArea[0]) + ((rect.width() * this.mBaseArea[DOWN_DIRECTION]) / this.mInputFrameSize.getWidth()), (((rect.top * this.mBaseArea[LEFT_DIRECTION]) / this.mInputFrameSize.getHeight()) + this.mBaseArea[RIGHT_DIRECTION]) + ((rect.height() * this.mBaseArea[LEFT_DIRECTION]) / this.mInputFrameSize.getHeight()));
        Size size = new Size((int) (((float) rect.width()) / ratio), (int) (((float) rect.height()) / ratio));
        if (this.mInputFrameFormat == ImageType.YUV420SP) {
            buffer = NV12toARGB(getInputFrame(this.mBaseFrameIndex), this.mInputFrameSize, rect2, size);
        } else {
            buffer = NV21toARGB(getInputFrame(this.mBaseFrameIndex), this.mInputFrameSize, rect2, size);
        }
        if (size.getWidth() < RIGHT_DIRECTION || size.getHeight() < RIGHT_DIRECTION) {
            return null;
        }
        for (int yy = 0; yy < size.getHeight(); yy += RIGHT_DIRECTION) {
            for (int xx = 0; xx < size.getWidth(); xx += RIGHT_DIRECTION) {
                if (((float) xx) + (((float) rect.left) / ratio) > ((float) this.mPreviewSize.getWidth())) {
                    CamLog.d(this.TAG, "x = " + xx);
                    CamLog.d(this.TAG, "width out of range");
                }
                if (((float) yy) + (((float) rect.top) / ratio) > ((float) this.mPreviewSize.getHeight())) {
                    CamLog.d(this.TAG, "y = " + yy);
                    CamLog.d(this.TAG, "height out of range");
                }
                int xy = xx + (size.getWidth() * yy);
                if (this.mEnumObj[getLayoutPos(((float) xx) + (((float) rect.left) / ratio), ((float) yy) + (((float) rect.top) / ratio))] == objectIndex + RIGHT_DIRECTION) {
                    int b = (buffer[xy] >> 0) & Ola_ShotParam.AnimalMask_Random;
                    buffer[xy] = ((-16777216 | (((buffer[xy] >> 16) & Ola_ShotParam.AnimalMask_Random) << 16)) | (((buffer[xy] >> MAX_INPUT_FRAME) & Ola_ShotParam.AnimalMask_Random) << MAX_INPUT_FRAME)) | b;
                } else {
                    buffer[xy] = 0;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(size.getWidth(), size.getHeight(), Config.ARGB_8888);
        bitmap.setPixels(buffer, 0, size.getWidth(), 0, 0, size.getWidth(), size.getHeight());
        return rotateBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), this.mAngle);
    }
}
