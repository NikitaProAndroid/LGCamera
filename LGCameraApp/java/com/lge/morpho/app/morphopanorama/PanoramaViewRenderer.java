package com.lge.morpho.app.morphopanorama;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import com.lge.camera.R;
import com.lge.camera.components.RotateView;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.camera.util.Util;
import com.lge.morpho.app.morphopanorama.GLTextureView.Renderer;
import com.lge.morpho.app.quickpanorama.MorphoImageStitcher;
import com.lge.olaworks.library.FaceDetector;
import java.util.ArrayList;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class PanoramaViewRenderer implements Renderer {
    public static final int MSG_FINISH_RENDER_PREVIEW = 0;
    public static final int MSG_REQUEST_REREGISTER_TEXTURE = 2;
    public static final int MSG_REQUEST_SET_POSTVIEW_DATA = 1;
    public static final int PANORAMA_POSTVIEW = 1;
    public static final int PANORAMA_PREVIEW = 0;
    private boolean isDefault;
    private boolean isFileSelect;
    private boolean isRegistered;
    private Activity mActivity;
    private int mDispType;
    private int mGyroscopeType;
    private Handler mHandler;
    private onRenderPreviewListener mListner;
    private MorphoImageStitcher mMorphoImageStitcher;
    private long mPrevTimestamp;
    private int mPreviewMode;
    private boolean mRenderEnable;
    private RenderInfo[] mRenderInfo;
    private int[] mRenderInfoID;
    private int mRotation;
    private double mScale;
    private Object mSyncObj;
    private Object mTouchSyncObj;
    private int mViewHeight;
    private int mViewWidth;
    private long render_count;
    private double x_rotate;
    private double y_rotate;

    public interface onRenderPreviewListener {
        void onRenderPreview(ResultInfo resultInfo);
    }

    private class RenderInfo {
        public double[] ac_mat;
        public double[] gr_mat;
        public ArrayList<float[]> gr_value_list;
        public byte[] image;
        public boolean is_set;
        public Object mLock;
        public long mOnDrawEndTime;
        public long mSetRenderInfoEndTime;
        public long mSetRenderInfoStartTime;
        public long mSetRenderInfoTime;
        public int preview_id;
        public double[] rv_mat;
        public boolean use_ac_mat;
        public boolean use_gr_mat;
        public int use_image;
        public boolean use_rv_mat;

        public RenderInfo(int image_buff_size) {
            this.mLock = new Object();
            this.image = new byte[image_buff_size];
            this.gr_mat = new double[9];
            this.rv_mat = new double[9];
            this.ac_mat = new double[9];
            this.use_gr_mat = false;
            this.use_rv_mat = false;
            this.use_ac_mat = false;
            this.is_set = false;
        }
    }

    public class ResultInfo {
        public int mAttachStatus;
        public long mAttachTime;
        public long mCount;
        public long mFrameInterval;
        public float[] mGyroCorrectionValue;
        public int mImageID;
        public int mIsStootable;
        public long mODFToOPF;
        public int mPreviewID;
        public long mRQRenderToOnDrawFrame;
        public long mRenderTime;
        public long mSetRenderInfoTime;
        public int mStopThres;
    }

    public PanoramaViewRenderer(Activity activity, Handler handler, MorphoImageStitcher image_stitcher, int preview_buffer_size) {
        this.isDefault = false;
        this.mSyncObj = new Object();
        this.mTouchSyncObj = new Object();
        this.mScale = 1.0d;
        this.render_count = 0;
        this.mPrevTimestamp = 0;
        this.mDispType = PANORAMA_POSTVIEW;
        this.mRenderEnable = false;
        this.isRegistered = false;
        this.mPreviewMode = MSG_FINISH_RENDER_PREVIEW;
        this.mMorphoImageStitcher = image_stitcher;
        this.mHandler = handler;
        this.mRenderInfo = new RenderInfo[MSG_REQUEST_REREGISTER_TEXTURE];
        this.mRenderInfo[MSG_FINISH_RENDER_PREVIEW] = new RenderInfo(preview_buffer_size);
        this.mRenderInfo[PANORAMA_POSTVIEW] = new RenderInfo(preview_buffer_size);
        this.mRenderInfoID = new int[PANORAMA_POSTVIEW];
        this.mActivity = activity;
    }

    public PanoramaViewRenderer(Activity activity, Handler handler, MorphoImageStitcher image_stitcher, boolean is_file_select) {
        this.isDefault = false;
        this.mSyncObj = new Object();
        this.mTouchSyncObj = new Object();
        this.mScale = 1.0d;
        this.render_count = 0;
        this.mPrevTimestamp = 0;
        this.mDispType = PANORAMA_POSTVIEW;
        this.mRenderEnable = false;
        this.isRegistered = false;
        this.mPreviewMode = PANORAMA_POSTVIEW;
        this.mMorphoImageStitcher = image_stitcher;
        this.mRotation = MSG_FINISH_RENDER_PREVIEW;
        this.isFileSelect = is_file_select;
        this.mHandler = handler;
        this.mActivity = activity;
    }

    public void setStitcher(MorphoImageStitcher stitcher) {
        this.mMorphoImageStitcher = stitcher;
    }

    public void setOnRenderPreviewListener(onRenderPreviewListener listner) {
        this.mListner = listner;
    }

    public void setInputGyroscopeType(int type) {
        this.mGyroscopeType = type;
    }

    public void setRenderInfo(byte[] input_img, ArrayList<float[]> gr_value_list, double[] gr_mat, double[] rv_mat, double[] ac_mat, int use_image, int id) {
        synchronized (this.mRenderInfoID) {
            RenderInfo r_info = this.mRenderInfo[this.mRenderInfoID[MSG_FINISH_RENDER_PREVIEW]];
            this.mRenderInfoID[MSG_FINISH_RENDER_PREVIEW] = this.mRenderInfoID[MSG_FINISH_RENDER_PREVIEW] == PANORAMA_POSTVIEW ? MSG_FINISH_RENDER_PREVIEW : PANORAMA_POSTVIEW;
        }
        synchronized (r_info.mLock) {
            long s_time = System.currentTimeMillis();
            if (input_img.length != r_info.image.length) {
                CamLog.d(FaceDetector.TAG, "Not same size. so skip");
                return;
            }
            System.arraycopy(input_img, MSG_FINISH_RENDER_PREVIEW, r_info.image, MSG_FINISH_RENDER_PREVIEW, r_info.image.length);
            if (gr_value_list != null) {
                r_info.gr_value_list = (ArrayList) gr_value_list.clone();
            }
            if (gr_mat != null) {
                System.arraycopy(gr_mat, MSG_FINISH_RENDER_PREVIEW, r_info.gr_mat, MSG_FINISH_RENDER_PREVIEW, gr_mat.length);
                r_info.use_gr_mat = true;
            } else {
                r_info.use_gr_mat = false;
            }
            if (rv_mat != null) {
                System.arraycopy(rv_mat, MSG_FINISH_RENDER_PREVIEW, r_info.rv_mat, MSG_FINISH_RENDER_PREVIEW, rv_mat.length);
                r_info.use_rv_mat = true;
            } else {
                r_info.use_rv_mat = false;
            }
            if (ac_mat != null) {
                System.arraycopy(ac_mat, MSG_FINISH_RENDER_PREVIEW, r_info.ac_mat, MSG_FINISH_RENDER_PREVIEW, ac_mat.length);
                r_info.use_ac_mat = true;
            } else {
                r_info.use_ac_mat = false;
            }
            r_info.use_image = use_image;
            r_info.is_set = true;
            r_info.preview_id = id;
            long e_time = System.currentTimeMillis();
            r_info.mSetRenderInfoTime = e_time - s_time;
            r_info.mSetRenderInfoStartTime = s_time;
            r_info.mSetRenderInfoEndTime = e_time;
        }
    }

    public void setAngle(float distance_x, float distance_y) {
        synchronized (this.mTouchSyncObj) {
            this.x_rotate += (double) (distance_x / ((float) this.mViewWidth));
            this.y_rotate += (double) (distance_y / ((float) this.mViewHeight));
        }
    }

    public void setScale(float scale) {
        synchronized (this.mTouchSyncObj) {
            this.mScale *= (double) scale;
            if (this.mScale > 3.0d) {
                this.mScale = 3.0d;
            } else if (this.mScale < 0.8d) {
                this.mScale = 0.8d;
            }
            this.x_rotate = 0.0d;
            this.y_rotate = 0.0d;
        }
    }

    public void setDefaultScale(double scale) {
        synchronized (this.mTouchSyncObj) {
            this.mScale = scale;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setPreviewRotation(int r3) {
        /*
        r2 = this;
        r1 = r2.mTouchSyncObj;
        monitor-enter(r1);
        switch(r3) {
            case 0: goto L_0x0008;
            case 90: goto L_0x000f;
            case 180: goto L_0x0013;
            case 270: goto L_0x0017;
            default: goto L_0x0006;
        };
    L_0x0006:
        monitor-exit(r1);	 Catch:{ all -> 0x000c }
        return;
    L_0x0008:
        r0 = 0;
        r2.mRotation = r0;	 Catch:{ all -> 0x000c }
        goto L_0x0006;
    L_0x000c:
        r0 = move-exception;
        monitor-exit(r1);	 Catch:{ all -> 0x000c }
        throw r0;
    L_0x000f:
        r0 = 1;
        r2.mRotation = r0;	 Catch:{ all -> 0x000c }
        goto L_0x0006;
    L_0x0013:
        r0 = 2;
        r2.mRotation = r0;	 Catch:{ all -> 0x000c }
        goto L_0x0006;
    L_0x0017:
        r0 = 3;
        r2.mRotation = r0;	 Catch:{ all -> 0x000c }
        goto L_0x0006;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.morpho.app.morphopanorama.PanoramaViewRenderer.setPreviewRotation(int):void");
    }

    public void setDefault() {
        synchronized (this.mTouchSyncObj) {
            this.x_rotate = 0.0d;
            this.y_rotate = 0.0d;
            this.mScale = 1.0d;
            this.isDefault = true;
        }
    }

    public void setDispType(int type) {
        synchronized (this.mSyncObj) {
            this.mDispType = type;
        }
    }

    public void resetMesureInfo() {
        synchronized (this.mSyncObj) {
            this.render_count = 0;
            this.mPrevTimestamp = 0;
        }
    }

    public void setRenderEnable(boolean enabled) {
        synchronized (this.mSyncObj) {
            this.mRenderEnable = enabled;
        }
    }

    public boolean getRenderEnable() {
        return this.mRenderEnable;
    }

    public void onDrawFrame(GL10 gl) {
        int id = MSG_FINISH_RENDER_PREVIEW;
        synchronized (this.mTouchSyncObj) {
            double x_rot = this.x_rotate;
            double y_rot = this.y_rotate;
            double scale = this.mScale;
            this.x_rotate = 0.0d;
            this.y_rotate = 0.0d;
        }
        synchronized (this.mSyncObj) {
            if (this.mRenderEnable) {
                if (this.mPreviewMode == 0) {
                    if (this.mMorphoImageStitcher.isReady()) {
                        RenderInfo render_info;
                        RenderInfo pre_render_info;
                        synchronized (this.mRenderInfoID) {
                            if (this.mRenderInfoID[MSG_FINISH_RENDER_PREVIEW] != PANORAMA_POSTVIEW) {
                                id = PANORAMA_POSTVIEW;
                            }
                            render_info = this.mRenderInfo[id];
                            pre_render_info = this.mRenderInfo[this.mRenderInfoID[MSG_FINISH_RENDER_PREVIEW]];
                        }
                        renderPreview(render_info, pre_render_info.mOnDrawEndTime);
                    } else {
                        return;
                    }
                } else if (this.mPreviewMode == PANORAMA_POSTVIEW) {
                    if (this.isDefault) {
                        this.mMorphoImageStitcher.renderPostviewDefault(this.mDispType);
                        this.isDefault = false;
                    } else {
                        this.mMorphoImageStitcher.renderPostview(x_rot, y_rot, scale, this.mDispType);
                    }
                }
                return;
            }
        }
    }

    private void renderPreview(RenderInfo r_info, long pre_odf_end_time) {
        synchronized (r_info.mLock) {
            if (r_info.is_set) {
                r_info.is_set = false;
                this.render_count++;
                ResultInfo result = new ResultInfo();
                long now_time = System.currentTimeMillis();
                if (this.mPrevTimestamp != 0) {
                    result.mFrameInterval = now_time - this.mPrevTimestamp;
                }
                this.mPrevTimestamp = now_time;
                result.mRQRenderToOnDrawFrame = now_time - r_info.mSetRenderInfoEndTime;
                result.mCount = this.render_count;
                result.mSetRenderInfoTime = r_info.mSetRenderInfoTime;
                if (r_info.use_gr_mat) {
                    this.mMorphoImageStitcher.setAngleMatrix(r_info.gr_mat, this.mGyroscopeType);
                }
                if (r_info.use_rv_mat) {
                    this.mMorphoImageStitcher.setAngleMatrix(r_info.rv_mat, PANORAMA_POSTVIEW);
                }
                if (r_info.use_ac_mat) {
                    this.mMorphoImageStitcher.setAngleMatrix(r_info.ac_mat, 3);
                }
                int[] image_id = new int[PANORAMA_POSTVIEW];
                int[] status = new int[PANORAMA_POSTVIEW];
                long s_time = System.currentTimeMillis();
                this.mMorphoImageStitcher.attach(r_info.image, r_info.use_image, image_id, status);
                long e_time = System.currentTimeMillis();
                result.mImageID = image_id[MSG_FINISH_RENDER_PREVIEW];
                result.mAttachStatus = status[MSG_FINISH_RENDER_PREVIEW];
                result.mAttachTime = e_time - s_time;
                int[] is_stootable = new int[PANORAMA_POSTVIEW];
                this.mMorphoImageStitcher.getIsShootable(is_stootable);
                result.mIsStootable = is_stootable[MSG_FINISH_RENDER_PREVIEW];
                int[] stop_thres = new int[PANORAMA_POSTVIEW];
                this.mMorphoImageStitcher.getIsStop(stop_thres);
                if (stop_thres[MSG_FINISH_RENDER_PREVIEW] < 70 && r_info.gr_value_list != null) {
                    result.mGyroCorrectionValue = new float[3];
                    FloatMathUtil.getAverage(result.mGyroCorrectionValue, r_info.gr_value_list);
                }
                result.mStopThres = stop_thres[MSG_FINISH_RENDER_PREVIEW];
                s_time = System.currentTimeMillis();
                this.mMorphoImageStitcher.renderPreview(r_info.image, image_id[MSG_FINISH_RENDER_PREVIEW], this.mDispType, this.mRotation);
                result.mRenderTime = System.currentTimeMillis() - s_time;
                if (this.render_count > 1) {
                    result.mODFToOPF = r_info.mSetRenderInfoStartTime - pre_odf_end_time;
                }
                result.mPreviewID = r_info.preview_id;
                if (this.mListner != null) {
                    this.mListner.onRenderPreview(result);
                }
                r_info.mOnDrawEndTime = System.currentTimeMillis();
                return;
            }
        }
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        int disp_w = Common.getPixelFromDimens(this.mActivity.getApplicationContext(), R.dimen.lcd_width);
        int disp_h = Common.getPixelFromDimens(this.mActivity.getApplicationContext(), R.dimen.lcd_height);
        if (Util.isConfigureLandscape(this.mActivity.getResources())) {
            this.mViewWidth = disp_w;
            this.mViewHeight = disp_h;
        } else {
            this.mViewWidth = disp_h;
            this.mViewHeight = disp_w;
        }
        gl.glViewport(MSG_FINISH_RENDER_PREVIEW, MSG_FINISH_RENDER_PREVIEW, this.mViewWidth, this.mViewHeight);
        gl.glOrthof(0.0f, (float) this.mViewWidth, 0.0f, (float) this.mViewHeight, -1.0f, RotateView.DEFAULT_TEXT_SCALE_X);
        gl.glScissor(MSG_FINISH_RENDER_PREVIEW, MSG_FINISH_RENDER_PREVIEW, this.mViewWidth, this.mViewHeight);
        if (this.mPreviewMode != PANORAMA_POSTVIEW) {
            this.mRenderEnable = true;
        } else if (!this.isRegistered) {
            Message msg = Message.obtain();
            if (this.isFileSelect) {
                msg.arg1 = PANORAMA_POSTVIEW;
            } else {
                msg.arg1 = MSG_REQUEST_REREGISTER_TEXTURE;
            }
            this.mHandler.sendMessage(msg);
            this.isRegistered = true;
        }
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        this.isRegistered = false;
        this.mRenderEnable = false;
    }
}
