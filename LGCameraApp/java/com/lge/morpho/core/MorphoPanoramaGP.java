package com.lge.morpho.core;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import com.lge.camera.util.CamLog;
import java.nio.ByteBuffer;

public class MorphoPanoramaGP {
    public static final int DIRECTION_AUTO = 6;
    public static final int DIRECTION_HORIZONTAL = 0;
    public static final int DIRECTION_HORIZONTAL_LEFT = 2;
    public static final int DIRECTION_HORIZONTAL_RIGHT = 3;
    public static final int DIRECTION_VERTICAL = 1;
    public static final int DIRECTION_VERTICAL_DOWN = 5;
    public static final int DIRECTION_VERTICAL_UP = 4;
    public static final int GUIDE_TYPE_HORIZONTAL = 0;
    public static final int GUIDE_TYPE_VERTICAL = 1;
    private static final int RECT_BOTTOM_OFFSET = 3;
    private static final int RECT_INFO_SIZE = 4;
    private static final int RECT_LEFT_OFFSET = 0;
    private static final int RECT_RIGHT_OFFSET = 2;
    private static final int RECT_TOP_OFFSET = 1;
    public static final int ROTATE_0 = 0;
    public static final int ROTATE_180 = 2;
    public static final int ROTATE_270 = 3;
    public static final int ROTATE_90 = 1;
    public static final int SENSOR_TYPE_GYROSCOPE = 0;
    public static final int SENSOR_TYPE_ROTATION_VECTOR = 1;
    public static final int STATUS_ALIGN_FAILURE = 2;
    public static final int STATUS_OUT_OF_MEMORY = 1;
    public static final int STATUS_STITCHING = 0;
    public static final int STATUS_STOPPED_BY_ERROR = 3;
    public static final int STATUS_WARNING_ALIGN_FAILURE = 7;
    public static final int STATUS_WARNING_LITTLE_FAR_1 = 12;
    public static final int STATUS_WARNING_LITTLE_FAR_2 = 13;
    public static final int STATUS_WARNING_NEED_TO_STOP = 4;
    public static final int STATUS_WARNING_REVERSE = 10;
    public static final int STATUS_WARNING_TOO_FAR = 6;
    public static final int STATUS_WARNING_TOO_FAR_1 = 8;
    public static final int STATUS_WARNING_TOO_FAR_2 = 9;
    public static final int STATUS_WARNING_TOO_FAST = 5;
    public static final int STATUS_WHOLE_AREA_COMPLETE = 11;
    public static final int STILL_IMAGE_FORMAT_JPEG = 256;
    public static final int STILL_IMAGE_FORMAT_YVU420SP = 17;
    public static final int USE_IMAGE_FORCE = 1;
    public static final int USE_IMAGE_NONE = -1;
    public static final int USE_IMAGE_NORMAL = 0;
    public static final int USE_SENSOR_FOR_ALIGNMENT_WHEN_FAILED = 0;
    private boolean mIsInitialized;
    private int mNative;

    public static class ImageSize {
        public int height;
        public int width;

        public void setSize(int w, int h) {
            this.width = w;
            this.height = h;
        }
    }

    public static class InitParam {
        public double angle_of_view_degree;
        public int direction;
        public int draw_cur_image;
        public int dst_img_height;
        public int dst_img_width;
        public String format;
        public int mode;
        public int output_rotation;
        public int preview_box_background_alpha;
        public int preview_box_foreground_alpha;
        public int preview_height;
        public int preview_img_height;
        public int preview_img_width;
        public int preview_rotation;
        public int preview_shrink_ratio;
        public int preview_width;
        public int still_height;
        public int still_width;
        public int use_threshold;
    }

    private final native int createNativeObject();

    private final native void deleteNativeObject(int i);

    private final native int nativeAttachPreview(int i, byte[] bArr, int i2, int[] iArr, byte[] bArr2, int[] iArr2, Bitmap bitmap);

    private final native int nativeAttachStillImage(int i, byte[] bArr, int i2, byte[] bArr2);

    private final native int nativeAttachStillImageExt(int i, ByteBuffer byteBuffer, int i2, ByteBuffer byteBuffer2);

    private final native int nativeAttachStillImageRaw(int i, ByteBuffer byteBuffer, int i2, ByteBuffer byteBuffer2);

    private static final native int nativeCalcImageSize(InitParam initParam, double d);

    private static final native int nativeConvertImage(Bitmap bitmap, byte[] bArr, String str, int i, int i2);

    private static final native int nativeDecodeJpeg(String str, byte[] bArr, String str2, int i, int i2);

    private final native int nativeEnd(int i);

    private final native int nativeFinish(int i);

    private final native int nativeGetBoundingRect(int i, int[] iArr);

    private final native int nativeGetClippingRect(int i, int[] iArr);

    private final native int nativeGetCurrentDirection(int i, int[] iArr);

    private final native int nativeGetGuidancePos(int i, int[] iArr);

    private final native int nativeGetImageSize(int i, int[] iArr, int[] iArr2);

    private final native int nativeGetMotionlessThreshold(int i, int[] iArr);

    private final native int nativeGetNumOfShooting(int i, int[] iArr);

    private final native int nativeGetUseSensorAssist(int i, int i2, int[] iArr);

    private final native int nativeGetUsedHeapSize(int i, int[] iArr);

    private static final native String nativeGetVersion();

    private final native int nativeInitialize(int i, InitParam initParam, int[] iArr);

    private static final native int nativeSaveJpeg(String str, byte[] bArr, String str2, int i, int i2, int i3);

    private final native int nativeSaveOutputJpeg(int i, String str, int i2, int i3, int i4, int i5, int i6);

    private final native int nativeSetAngleMatrix(int i, double[] dArr, int i2);

    private final native int nativeSetFarThreshold(int i, int i2, int i3);

    private final native int nativeSetJpegForCopyingExif(int i, ByteBuffer byteBuffer);

    private final native int nativeSetMotionlessThreshold(int i, int i2);

    private final native int nativeSetUseSensorAssist(int i, int i2, int i3);

    private final native int nativeSetUseSensorThreshold(int i, int i2);

    private final native int nativeStart(int i);

    private final native int setBrightnessCorrection(int i, int i2);

    static {
        try {
            System.loadLibrary("morpho_panorama_gp");
        } catch (UnsatisfiedLinkError e) {
            CamLog.e("MorphoPanoramaGP", "can't loadLibrary \r\n" + e.getMessage());
        }
    }

    public static String getVersion() {
        return nativeGetVersion();
    }

    public static int calcImageSize(InitParam param, double goal_angle) {
        return nativeCalcImageSize(param, goal_angle);
    }

    public static int saveJpeg(String path, byte[] raw_data, String format, int width, int height, int orientation) {
        return nativeSaveJpeg(path, raw_data, format, width, height, orientation);
    }

    public static int decodeJpeg(String path, byte[] output_data, String format, int width, int height) {
        return nativeDecodeJpeg(path, output_data, format, width, height);
    }

    public static int convertImage(Bitmap dst_image, byte[] src_image, String src_format, int src_width, int src_height) {
        return nativeConvertImage(dst_image, src_image, src_format, src_width, src_height);
    }

    public boolean isInitialized() {
        return this.mIsInitialized;
    }

    public boolean isReady() {
        if (this.mNative == 0 || !this.mIsInitialized) {
            return false;
        }
        return true;
    }

    public MorphoPanoramaGP() {
        this.mNative = STATUS_STITCHING;
        this.mIsInitialized = false;
        int ret = createNativeObject();
        if (ret != 0) {
            this.mNative = ret;
        } else {
            this.mNative = STATUS_STITCHING;
        }
    }

    public int initialize(InitParam param, int[] buffer_size) {
        int ret;
        if (this.mNative != 0) {
            ret = nativeInitialize(this.mNative, param, buffer_size);
        } else {
            ret = Error.ERROR_STATE;
        }
        if (ret == 0) {
            this.mIsInitialized = true;
        }
        return ret;
    }

    public int finish() {
        if (!isReady()) {
            return Error.ERROR_STATE;
        }
        int ret = nativeFinish(this.mNative);
        deleteNativeObject(this.mNative);
        this.mNative = STATUS_STITCHING;
        return ret;
    }

    public int start() {
        if (isReady()) {
            return nativeStart(this.mNative);
        }
        return Error.ERROR_STATE;
    }

    public int attachPreview(byte[] input_image, int use_image, int[] image_id, byte[] motion_data, int[] status, Bitmap preview_image) {
        if (isReady()) {
            return nativeAttachPreview(this.mNative, input_image, use_image, image_id, motion_data, status, preview_image);
        }
        return Error.ERROR_STATE;
    }

    public int attachStillImage(byte[] input_image, int image_id, byte[] motion_data) {
        if (isReady()) {
            return nativeAttachStillImage(this.mNative, input_image, image_id, motion_data);
        }
        return Error.ERROR_STATE;
    }

    public int attachStillImageExt(ByteBuffer input_image, int image_id, ByteBuffer motion_data) {
        if (isReady()) {
            return nativeAttachStillImageExt(this.mNative, input_image, image_id, motion_data);
        }
        return Error.ERROR_STATE;
    }

    public int attachStillImageRaw(ByteBuffer input_image, int image_id, ByteBuffer motion_data) {
        if (isReady()) {
            return nativeAttachStillImageRaw(this.mNative, input_image, image_id, motion_data);
        }
        return Error.ERROR_STATE;
    }

    public int attachSetJpegForCopyingExif(ByteBuffer input_image) {
        if (isReady()) {
            return nativeSetJpegForCopyingExif(this.mNative, input_image);
        }
        return Error.ERROR_STATE;
    }

    public int end() {
        if (isReady()) {
            return nativeEnd(this.mNative);
        }
        return Error.ERROR_STATE;
    }

    public int getBoundingRect(Rect rect) {
        int ret;
        int[] rect_info = new int[STATUS_WARNING_NEED_TO_STOP];
        if (isReady()) {
            ret = nativeGetBoundingRect(this.mNative, rect_info);
            if (ret == 0) {
                rect.set(rect_info[STATUS_STITCHING], rect_info[USE_IMAGE_FORCE], rect_info[STATUS_ALIGN_FAILURE], rect_info[STATUS_STOPPED_BY_ERROR]);
            }
        } else {
            ret = Error.ERROR_STATE;
        }
        if (ret != 0) {
            rect.set(STATUS_STITCHING, STATUS_STITCHING, STATUS_STITCHING, STATUS_STITCHING);
        }
        return ret;
    }

    public int getClippingRect(Rect rect) {
        int ret;
        int[] rect_info = new int[STATUS_WARNING_NEED_TO_STOP];
        if (isReady()) {
            ret = nativeGetClippingRect(this.mNative, rect_info);
            if (ret == 0) {
                rect.set(rect_info[STATUS_STITCHING], rect_info[USE_IMAGE_FORCE], rect_info[STATUS_ALIGN_FAILURE], rect_info[STATUS_STOPPED_BY_ERROR]);
            }
        } else {
            ret = Error.ERROR_STATE;
        }
        if (ret != 0) {
            rect.set(STATUS_STITCHING, STATUS_STITCHING, STATUS_STITCHING, STATUS_STITCHING);
        }
        return ret;
    }

    public int getUsedHeapSize(int[] used_heap_size) {
        if (isReady()) {
            return nativeGetUsedHeapSize(this.mNative, used_heap_size);
        }
        return Error.ERROR_STATE;
    }

    public int getUseSensorAssist(int use_case, int[] enable) {
        if (isReady()) {
            return nativeGetUseSensorAssist(this.mNative, use_case, enable);
        }
        return Error.ERROR_STATE;
    }

    public int setMotionlessThreshold(int motionless_threshold) {
        if (isReady()) {
            return nativeSetMotionlessThreshold(this.mNative, motionless_threshold);
        }
        return Error.ERROR_STATE;
    }

    public int setFarThreshold(int far_threshold, int far_threshold_corss) {
        if (isReady()) {
            return nativeSetFarThreshold(this.mNative, far_threshold, far_threshold_corss);
        }
        return Error.ERROR_STATE;
    }

    public int setAngleMatrix(double[] matrix, int sensor_type) {
        if (isReady()) {
            return nativeSetAngleMatrix(this.mNative, matrix, sensor_type);
        }
        return Error.ERROR_STATE;
    }

    public int getCurrentDirection(int[] direction) {
        if (isReady()) {
            return nativeGetCurrentDirection(this.mNative, direction);
        }
        return Error.ERROR_STATE;
    }

    public int setUseSensorAssist(int use_case, int enable) {
        if (isReady()) {
            return nativeSetUseSensorAssist(this.mNative, use_case, enable);
        }
        return Error.ERROR_STATE;
    }

    public int setBrightnessCorrection(int corect) {
        if (isReady()) {
            return setBrightnessCorrection(this.mNative, corect);
        }
        return Error.ERROR_STATE;
    }

    public int setUseSensorThreshold(int threshold) {
        if (isReady()) {
            return nativeSetUseSensorThreshold(this.mNative, threshold);
        }
        return Error.ERROR_STATE;
    }

    public int getGuidancePos(Point attached, Point guide) {
        int[] pos = new int[STATUS_WARNING_NEED_TO_STOP];
        if (!isReady()) {
            return Error.ERROR_STATE;
        }
        int ret = nativeGetGuidancePos(this.mNative, pos);
        attached.set(pos[STATUS_STITCHING], pos[USE_IMAGE_FORCE]);
        guide.set(pos[STATUS_ALIGN_FAILURE], pos[STATUS_STOPPED_BY_ERROR]);
        return ret;
    }

    public int getNumOfShooting(int[] nums) {
        if (isReady()) {
            return nativeGetNumOfShooting(this.mNative, nums);
        }
        return Error.ERROR_STATE;
    }

    public int getImageSize(ImageSize sPreview, ImageSize sOutput) {
        int[] preview = new int[STATUS_ALIGN_FAILURE];
        int[] output = new int[STATUS_ALIGN_FAILURE];
        if (!isReady()) {
            return Error.ERROR_STATE;
        }
        int ret = nativeGetImageSize(this.mNative, preview, output);
        sPreview.setSize(preview[STATUS_STITCHING], preview[USE_IMAGE_FORCE]);
        sOutput.setSize(output[STATUS_STITCHING], output[USE_IMAGE_FORCE]);
        return ret;
    }

    public int saveOutputJpeg(String path, Rect rect, int orientation) {
        if (!isReady()) {
            return Error.ERROR_STATE;
        }
        return nativeSaveOutputJpeg(this.mNative, path, rect.left, rect.top, rect.right, rect.bottom, orientation);
    }
}
