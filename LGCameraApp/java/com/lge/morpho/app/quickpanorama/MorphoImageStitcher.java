package com.lge.morpho.app.quickpanorama;

import android.app.Activity;
import android.graphics.Rect;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class MorphoImageStitcher {
    public static final int ALPHA_BLEND_IMAGE_FRAME_OFF = 0;
    public static final int ALPHA_BLEND_IMAGE_FRAME_ON = 1;
    public static final int CONTENT_TYPE_MORPHO_PANORAMA = 1;
    public static final int CONTENT_TYPE_NONE = 0;
    public static final int CONTENT_TYPE_PHOTO_SPHERE = 2;
    public static final int CURRENT_IMAGE_FIX_AT_CENTER = 1;
    public static final int CURRENT_IMAGE_FREE = 0;
    public static final int CURRENT_IMAGE_FREE_NEAR_EQUATOR = 2;
    public static final int DISP_TYPE_BACKGROUND = 2;
    public static final int DISP_TYPE_NONE = 0;
    public static final int DISP_TYPE_WIRE_FRAME = 1;
    public static final int ERROR_GENERAL_ERROR = Integer.MIN_VALUE;
    public static final int ERROR_IO = -2147483640;
    public static final int ERROR_MALLOC = -2147483644;
    public static final int ERROR_PARAM = -2147483647;
    public static final int ERROR_STATE = -2147483646;
    public static final int ERROR_UNKNOWN = -1073741824;
    public static final int ERROR_UNSUPPORTED = -2147483632;
    public static final int GUIDE_TYPE_FREE = -1;
    public static final int GUIDE_TYPE_HORIZONTAL = 0;
    public static final int GUIDE_TYPE_RADIAL = 3;
    public static final int GUIDE_TYPE_VANILLA = 4;
    public static final int GUIDE_TYPE_VANILLA2 = 5;
    public static final int GUIDE_TYPE_VERTICAL = 1;
    public static final int GUIDE_TYPE_WHIRLPOOL = 2;
    public static final int MODE_STITCHING = 0;
    public static final int MODE_VIEWING = 1;
    public static final int MORPHO_DOPROCESS = 1;
    public static final int MORPHO_OK = 0;
    private static final int POINT_INFO_SIZE = 2;
    private static final int POINT_X_OFFSET = 0;
    private static final int POINT_Y_OFFSET = 1;
    public static final int PROJECTION_TYPE_CYLINDRICAL_H = 3;
    public static final int PROJECTION_TYPE_CYLINDRICAL_V = 4;
    public static final int PROJECTION_TYPE_FISHEYE = 5;
    public static final int PROJECTION_TYPE_MERCATOR_H = 0;
    public static final int PROJECTION_TYPE_MERCATOR_V = 1;
    public static final int PROJECTION_TYPE_PERSPECTIVE = 2;
    private static final int RECT_BOTTOM_OFFSET = 3;
    private static final int RECT_INFO_SIZE = 4;
    private static final int RECT_LEFT_OFFSET = 0;
    private static final int RECT_RIGHT_OFFSET = 2;
    private static final int RECT_TOP_OFFSET = 1;
    public static final int RENDER_MODE_OPEN_GL = 1;
    public static final int RENDER_MODE_SOFT = 0;
    public static final int ROTATE_0 = 0;
    public static final int ROTATE_180 = 2;
    public static final int ROTATE_270 = 3;
    public static final int ROTATE_90 = 1;
    public static final int SCROLL_LIMIT_TYPE_BOUNDARY_CENTER = 1;
    public static final int SCROLL_LIMIT_TYPE_BOUNDARY_EDGE = 0;
    public static final int SENSOR_TYPE_ACCELEROMETER = 3;
    public static final int SENSOR_TYPE_CORRECTED_GYROSCOPE = 2;
    public static final int SENSOR_TYPE_GYROSCOPE = 0;
    public static final int SENSOR_TYPE_ROTATION_VECTOR = 1;
    public static final int STATUS_2_3RD_LATITUDE_COMPLETE = 11;
    public static final int STATUS_ALIGN_FAILURE = 3;
    public static final int STATUS_GUIDE_ENDED = 2;
    public static final int STATUS_OUT_OF_MEMORY = 1;
    public static final int STATUS_STITCHING = 0;
    public static final int STATUS_STOPPED_BY_ERROR = 4;
    public static final int STATUS_WARNING_ALIGN_FAILURE = 8;
    public static final int STATUS_WARNING_NEED_TO_STOP = 5;
    public static final int STATUS_WARNING_ROTATED_CLOCKWISE = 9;
    public static final int STATUS_WARNING_ROTATED_COUNTERCLOCKWISE = 10;
    public static final int STATUS_WARNING_TOO_FAR = 7;
    public static final int STATUS_WARNING_TOO_FAST = 6;
    public static final int STATUS_WHOLE_SPHERE_COMPLETE = 12;
    public static final int STILL_IMAGE_FORMAT_JPEG = 256;
    public static final int STILL_IMAGE_FORMAT_YVU420SP = 17;
    private static final int STITCH_INFO_ANGLE_OFFSET = 1;
    private static final int STITCH_INFO_INFO_SIZE = 2;
    private static final int STITCH_INFO_SCALE_OFFSET = 0;
    public static final int USE_IMAGE_FORCE = 1;
    public static final int USE_IMAGE_NONE = -1;
    public static final int USE_IMAGE_NORMAL = 0;
    public static final int USE_SENSOR_FOR_ALIGNMENT_WHEN_FAILED = 0;
    public static final int USE_SENSOR_FOR_GLOBAL_ALIGNMENT = 1;
    public static final int VERSION_1 = 0;
    public static final int VERSION_2 = 1;
    private boolean mFinished;
    private boolean mIsInitialized;
    private int mNative;

    public static class BgColor {
        public float A;
        public float B;
        public float G;
        public float R;
    }

    public static class FrameColor {
        public float A;
        public float B;
        public float G;
        public float R;
        public float Width;
    }

    public static class GalleryData {
        public int cropped_area_image_height;
        public int cropped_area_image_width;
        public int cropped_area_left;
        public int cropped_area_top;
        public int full_pano_height;
        public int full_pano_width;
    }

    public static class PanoramaInitParam {
        public int all_guide_disp_remaining_num;
        public int alpha_blending_image_frame;
        public int angle_fov;
        public BgColor bg_color;
        public int blink_preview_mode;
        public int disp_current_image;
        public FrameColor effective_input_frame_color;
        public int fix_current_image;
        public String format;
        public int gradually_disp_guide_frame;
        public FrameColor guide_frame_color;
        public double input_angle_of_view_degree;
        public int input_height;
        public int input_width;
        public int mask_poles;
        public int max_angle_fov;
        public int mode;
        public FrameColor preview_frame_color;
        public FrameColor registered_frame_color;
        public int render_mode;
        public int scroll_limit_type;
        public FrameColor state_error_alignment_frame_color;
        public FrameColor state_info_stitchable_frame_color;
        public FrameColor state_warning_need_to_stop_frame_color;
        public FrameColor state_warning_rotated_frame_color;
        public FrameColor state_warning_toofar_frame_color;
        public FrameColor state_warning_toofast_frame_color;
        public double still_angle_of_view_degree;
        public int still_height;
        public int still_width;
        public int use_still_capture;
        public int version;
        public FrameColor wire_frame_color;

        public PanoramaInitParam() {
            this.wire_frame_color = new FrameColor();
            this.preview_frame_color = new FrameColor();
            this.registered_frame_color = new FrameColor();
            this.effective_input_frame_color = new FrameColor();
            this.state_warning_need_to_stop_frame_color = new FrameColor();
            this.state_info_stitchable_frame_color = new FrameColor();
            this.state_warning_toofast_frame_color = new FrameColor();
            this.state_warning_toofar_frame_color = new FrameColor();
            this.state_error_alignment_frame_color = new FrameColor();
            this.state_warning_rotated_frame_color = new FrameColor();
            this.bg_color = new BgColor();
            this.guide_frame_color = new FrameColor();
        }
    }

    public static class ViewParam {
        public double scale;
        public double x_rotate;
        public double y_rotate;

        public ViewParam() {
            this.x_rotate = 0.0d;
            this.y_rotate = 0.0d;
            this.scale = 1.0d;
        }
    }

    private final native int createNativeObject();

    private final native void deleteNativeObject(int i);

    private final native int nativeAttach(int i, byte[] bArr, int i2, int[] iArr, int[] iArr2);

    private final native int nativeDecodeJpeg(int i, String str, byte[] bArr, String str2, int i2, int i3);

    private final native int nativeDecodePostview(int i, String str, int[] iArr, int[] iArr2, int[] iArr3, int[] iArr4, int[] iArr5);

    private final native int nativeEnd(int i);

    private final native int nativeFinish(int i);

    private final native int nativeGetBoundingRect(int i, int[] iArr);

    private final native int nativeGetClippingRect(int i, int[] iArr);

    private static final native int nativeGetContentType(String str);

    private final native int nativeGetGalleryDataOfAppSeg(int i, byte[] bArr);

    private final native int nativeGetGuideType(int i, int[] iArr);

    private final native int nativeGetImage(int i, byte[] bArr, int i2, int i3, int i4, int i5);

    private final native int nativeGetIsShootable(int i, int[] iArr);

    private final native int nativeGetIsStop(int i, int[] iArr);

    private final native int nativeGetPostviewParam(int i, ViewParam viewParam, ViewParam viewParam2);

    private final native int nativeGetPreviewImage(int i, int i2, int i3, byte[] bArr, byte[] bArr2);

    private final native int nativeGetProjectionType(int i, int[] iArr);

    private final native int nativeGetUseSensorAssist(int i, int i2, int[] iArr);

    private final native int nativeGetUsedHeapSize(int i, int[] iArr);

    private static final native String nativeGetVersion();

    private final native int nativeInitialize(int i, PanoramaInitParam panoramaInitParam, int[] iArr);

    private final native int nativeRegisterStillImage(int i, byte[] bArr, int i2, int i3, String str);

    private final native int nativeReleaseRegisteredImage(int i);

    private final native int nativeRenderPostview(int i, double d, double d2, double d3, int i2);

    private final native int nativeRenderPostviewDefault(int i, int i2);

    private final native int nativeRenderPreview(int i, byte[] bArr, int i2, int i3, int i4);

    private final native int nativeSaveJpeg(int i, String str, byte[] bArr, String str2, int i2, int i3, int i4);

    private final native int nativeSaveOutputJpeg(int i, String str, int i2, int i3, int i4, int i5, int i6, int[] iArr, String str2, String str3, boolean z);

    private final native int nativeSaveRegisteredImage(int i);

    private final native int nativeSetAngleMatrix(int i, double[] dArr, int i2);

    private final native int nativeSetGalleryData(int i, GalleryData galleryData, int i2, int i3);

    private final native int nativeSetGuideType(int i, int i2);

    private final native int nativeSetListenerFromNative(int i, Activity activity);

    private final native int nativeSetMotionlessThreshold(int i, int i2);

    private final native int nativeSetPostviewData(int i, int i2, int i3);

    private final native int nativeSetPostviewParam(int i, ViewParam viewParam, ViewParam viewParam2);

    private final native int nativeSetProjectionType(int i, int i2);

    private final native int nativeSetTextureShrinkRatio(int i, int i2);

    private final native int nativeSetUseReplayMode(int i, int i2);

    private final native int nativeSetUseSensorAssist(int i, int i2, int i3);

    private final native int nativeSetUseSensorThreshold(int i, int i2);

    private final native int nativeSetUseThreshold(int i, int i2);

    private final native int nativeStart(int i, int i2);

    private final native int nativereReRegisterTexture(int i);

    static {
        try {
            System.loadLibrary("morpho_panorama_wa_4");
        } catch (UnsatisfiedLinkError e) {
            CamLog.e("MorphoImageStitcher", e.getMessage());
            CamLog.e("MorphoImageStitcher", "can't loadLibrary");
        }
    }

    public boolean isInitialized() {
        return this.mIsInitialized;
    }

    public boolean isFinished() {
        return this.mFinished;
    }

    public static String getVersion() {
        return nativeGetVersion();
    }

    public static int getContentType(String path) {
        return nativeGetContentType(path);
    }

    public MorphoImageStitcher() {
        this.mIsInitialized = false;
        this.mFinished = true;
        this.mNative = VERSION_1;
        CamLog.d(FaceDetector.TAG, "MorphoImageStitcher create Start");
        int ret = createNativeObject();
        if (ret != 0) {
            this.mFinished = false;
            this.mNative = ret;
        } else {
            this.mNative = VERSION_1;
            ret = ERROR_MALLOC;
        }
        CamLog.d("MorphoImageStitcher", "create End ret=" + ret);
    }

    public int initialize(PanoramaInitParam param, int[] buffer_size) {
        int ret;
        CamLog.d(FaceDetector.TAG, "MorphoImageStitcher initialize Start");
        if (this.mNative != 0) {
            ret = nativeInitialize(this.mNative, param, buffer_size);
        } else {
            ret = ERROR_STATE;
        }
        if (ret == 0) {
            this.mIsInitialized = true;
        }
        CamLog.d(FaceDetector.TAG, "MorphoImageStitcher initialize End ret=" + ret);
        return ret;
    }

    public boolean isReady() {
        if (this.mNative == 0 || !this.mIsInitialized) {
            return false;
        }
        return true;
    }

    public int finish() {
        CamLog.d(FaceDetector.TAG, "MorphoImageStitcher finish Start mIsInitialized=" + this.mIsInitialized);
        int ret = VERSION_1;
        if (isReady()) {
            this.mFinished = true;
            if (this.mIsInitialized) {
                ret = nativeFinish(this.mNative);
            }
            deleteNativeObject(this.mNative);
            this.mNative = VERSION_1;
            this.mIsInitialized = false;
        } else {
            ret = ERROR_STATE;
        }
        CamLog.d(FaceDetector.TAG, "MorphoImageStitcher finish End ret=" + ret);
        return ret;
    }

    public int start(int use_only_preview) {
        if (isReady()) {
            return nativeStart(this.mNative, use_only_preview);
        }
        return ERROR_STATE;
    }

    public int attach(byte[] input_image, int use_image, int[] image_id, int[] status) {
        if (isReady()) {
            return nativeAttach(this.mNative, input_image, use_image, image_id, status);
        }
        return ERROR_STATE;
    }

    public int end() {
        int ret;
        CamLog.d(FaceDetector.TAG, "MorphoImageStitcher end Start mIsInitialized=" + this.mIsInitialized);
        if (isReady()) {
            ret = nativeEnd(this.mNative);
        } else {
            ret = ERROR_STATE;
        }
        CamLog.d(FaceDetector.TAG, "MorphoImageStitcher end End ret=" + ret);
        return ret;
    }

    public int getImage(byte[] output_image, Rect rect) {
        if (!isReady()) {
            return ERROR_STATE;
        }
        return nativeGetImage(this.mNative, output_image, rect.left, rect.top, rect.right, rect.bottom);
    }

    public int renderPreview(byte[] input_img, int image_id, int disp_type, int rotation) {
        if (isReady()) {
            return nativeRenderPreview(this.mNative, input_img, image_id, disp_type, rotation);
        }
        return ERROR_STATE;
    }

    public int renderPostview(double x_rotate, double y_rotate, double scale, int disp_type) {
        if (isReady()) {
            return nativeRenderPostview(this.mNative, x_rotate, y_rotate, scale, disp_type);
        }
        return ERROR_STATE;
    }

    public int renderPostviewDefault(int disp_type) {
        if (isReady()) {
            return nativeRenderPostviewDefault(this.mNative, disp_type);
        }
        return ERROR_STATE;
    }

    public int reRegisterTexture() {
        if (isReady()) {
            return nativereReRegisterTexture(this.mNative);
        }
        return ERROR_STATE;
    }

    public int getPreviewImage(int output_width, int output_height, byte[] output_image, byte[] input_image) {
        if (isReady()) {
            return nativeGetPreviewImage(this.mNative, output_width, output_height, output_image, input_image);
        }
        return ERROR_STATE;
    }

    public int getBoundingRect(Rect rect) {
        int ret;
        int[] rect_info = new int[STATUS_STOPPED_BY_ERROR];
        if (isReady()) {
            ret = nativeGetBoundingRect(this.mNative, rect_info);
            if (ret == 0) {
                rect.set(rect_info[VERSION_1], rect_info[VERSION_2], rect_info[STITCH_INFO_INFO_SIZE], rect_info[STATUS_ALIGN_FAILURE]);
            }
        } else {
            ret = ERROR_STATE;
        }
        if (ret != 0) {
            rect.set(VERSION_1, VERSION_1, VERSION_1, VERSION_1);
        }
        return ret;
    }

    public int getClippingRect(Rect rect) {
        int ret;
        int[] rect_info = new int[STATUS_STOPPED_BY_ERROR];
        if (isReady()) {
            ret = nativeGetClippingRect(this.mNative, rect_info);
            if (ret == 0) {
                rect.set(rect_info[VERSION_1], rect_info[VERSION_2], rect_info[STITCH_INFO_INFO_SIZE], rect_info[STATUS_ALIGN_FAILURE]);
            }
        } else {
            ret = ERROR_STATE;
        }
        if (ret != 0) {
            rect.set(VERSION_1, VERSION_1, VERSION_1, VERSION_1);
        }
        return ret;
    }

    public int getGuideType(int[] guide_type) {
        if (isReady()) {
            return nativeGetGuideType(this.mNative, guide_type);
        }
        return ERROR_STATE;
    }

    public int getProjectionType(int[] projection_type) {
        if (isReady()) {
            return nativeGetProjectionType(this.mNative, projection_type);
        }
        return ERROR_STATE;
    }

    public int getUsedHeapSize(int[] used_heap_size) {
        if (isReady()) {
            return nativeGetUsedHeapSize(this.mNative, used_heap_size);
        }
        return ERROR_STATE;
    }

    public int getUseSensorAssist(int use_case, int[] enable) {
        if (isReady()) {
            return nativeGetUseSensorAssist(this.mNative, use_case, enable);
        }
        return ERROR_STATE;
    }

    public int setGuideType(int guide_type) {
        if (isReady()) {
            return nativeSetGuideType(this.mNative, guide_type);
        }
        return ERROR_STATE;
    }

    public int setProjectionType(int projection_type) {
        if (isReady()) {
            return nativeSetProjectionType(this.mNative, projection_type);
        }
        return ERROR_STATE;
    }

    public int setMotionlessThreshold(int motionless_threshold) {
        if (isReady()) {
            return nativeSetMotionlessThreshold(this.mNative, motionless_threshold);
        }
        return ERROR_STATE;
    }

    public int setUseThreshold(int use_threshold) {
        if (isReady()) {
            return nativeSetUseThreshold(this.mNative, use_threshold);
        }
        return ERROR_STATE;
    }

    public int setUseSensorThreshold(int use_sensor_threshold) {
        if (isReady()) {
            return nativeSetUseSensorThreshold(this.mNative, use_sensor_threshold);
        }
        return ERROR_STATE;
    }

    public int setAngleMatrix(double[] matrix, int sensor_type) {
        if (isReady()) {
            return nativeSetAngleMatrix(this.mNative, matrix, sensor_type);
        }
        return ERROR_STATE;
    }

    public int setPostviewData(int rotation, int render_low_image) {
        if (isReady()) {
            return nativeSetPostviewData(this.mNative, rotation, render_low_image);
        }
        return ERROR_STATE;
    }

    public int setGalleryData(GalleryData gallery_data, int rotation, int render_low_image) {
        if (isReady()) {
            return nativeSetGalleryData(this.mNative, gallery_data, rotation, render_low_image);
        }
        return ERROR_STATE;
    }

    public int setUseSensorAssist(int use_case, int enable) {
        if (isReady()) {
            return nativeSetUseSensorAssist(this.mNative, use_case, enable);
        }
        return ERROR_STATE;
    }

    public int setTextureShrinkRatio(int ratio) {
        if (isReady()) {
            return nativeSetTextureShrinkRatio(this.mNative, ratio);
        }
        return ERROR_STATE;
    }

    public int releaseRegisteredImage() {
        try {
            if (isReady()) {
                return nativeReleaseRegisteredImage(this.mNative);
            }
            return ERROR_STATE;
        } catch (Exception e) {
            CamLog.e(FaceDetector.TAG, "MorphoImageStitcher : " + e);
            return ERROR_STATE;
        }
    }

    public int setUseReplayMode(int enable) {
        if (isReady()) {
            return nativeSetUseReplayMode(this.mNative, enable);
        }
        return ERROR_STATE;
    }

    public int getIsStop(int[] is_stop) {
        if (isReady()) {
            return nativeGetIsStop(this.mNative, is_stop);
        }
        return ERROR_STATE;
    }

    public int getIsShootable(int[] is_shootable) {
        if (isReady()) {
            return nativeGetIsShootable(this.mNative, is_shootable);
        }
        return ERROR_STATE;
    }

    public int getPostviewParam(ViewParam param, ViewParam def_param) {
        if (isReady()) {
            return nativeGetPostviewParam(this.mNative, param, def_param);
        }
        return ERROR_STATE;
    }

    public int setPostviewParam(ViewParam param, ViewParam def_param) {
        if (isReady()) {
            return nativeSetPostviewParam(this.mNative, param, def_param);
        }
        return ERROR_STATE;
    }

    public int saveRegisteredImage() {
        if (isReady()) {
            return nativeSaveRegisteredImage(this.mNative);
        }
        return ERROR_STATE;
    }

    public int registerStillImage(byte[] still_image, int img_id, int format, String path) {
        if (isReady()) {
            return nativeRegisterStillImage(this.mNative, still_image, img_id, format, path);
        }
        return ERROR_STATE;
    }

    public int decodePostview(String path, int[] out_width, int[] out_height, int[] exif_orientation, int[] postview_data_size, int[] gallery_data_size) {
        if (isReady()) {
            return nativeDecodePostview(this.mNative, path, out_width, out_height, exif_orientation, postview_data_size, gallery_data_size);
        }
        return ERROR_STATE;
    }

    public int getGalleryDataOfAppSeg(byte[] gallery_data) {
        if (isReady()) {
            return nativeGetGalleryDataOfAppSeg(this.mNative, gallery_data);
        }
        return ERROR_STATE;
    }

    public int setListenerFromNative(Activity context) {
        if (isReady()) {
            return nativeSetListenerFromNative(this.mNative, context);
        }
        return ERROR_STATE;
    }

    public int saveOutputJpeg(String path, Rect rect, int orientation, int[] output_size, String first_date, String last_date, boolean addGallerySeg) {
        if (!isReady()) {
            return ERROR_STATE;
        }
        return nativeSaveOutputJpeg(this.mNative, path, rect.left, rect.top, rect.right, rect.bottom, orientation, output_size, first_date, last_date, addGallerySeg);
    }

    public int saveJpeg(String path, byte[] raw_data, String format, int width, int height, int orientation) {
        if (isReady()) {
            return nativeSaveJpeg(this.mNative, path, raw_data, format, width, height, orientation);
        }
        return ERROR_STATE;
    }

    public int decodeJpeg(String path, byte[] output_data, String format, int width, int height) {
        if (isReady()) {
            return nativeDecodeJpeg(this.mNative, path, output_data, format, width, height);
        }
        return ERROR_STATE;
    }
}
