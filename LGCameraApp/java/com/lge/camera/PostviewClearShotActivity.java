package com.lge.camera;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.lge.almalence.app.clearshot.AlmaCLRShot;
import com.lge.almalence.app.clearshot.AlmaCLRShot.ImageType;
import com.lge.almalence.app.clearshot.AlmaCLRShot.ObjBorderInfo;
import com.lge.almalence.app.clearshot.AlmaCLRShot.ObjectInfo;
import com.lge.almalence.app.clearshot.Size;
import com.lge.camera.components.RotateView;
import com.lge.camera.postview.PostviewDialog;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.camera.util.ExifUtil;
import com.lge.camera.util.FileNamer;
import com.lge.camera.util.ImageManager;
import com.lge.camera.util.SharedPreferenceUtil;
import com.lge.camera.util.Util;
import com.lge.olaworks.define.LGT_Limit;
import com.lge.olaworks.library.FaceDetector;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

public class PostviewClearShotActivity extends ShotPostviewActivity {
    private static final int CORNER_BOTTOM = 8;
    private static final int CORNER_LEFT = 1;
    private static final int CORNER_NONE = 0;
    private static final int CORNER_RIGHT = 4;
    private static final int CORNER_TOP = 2;
    private static final float DASH_INTERVAL_DP = 2.0f;
    private static final float DASH_PHASE_DP = 3.0f;
    private static final int EXPAND_DIP = 15;
    private static final int SMALL_SIZE = 45;
    private static final float STROKE_WIDTH_DP = 2.0f;
    private ArrayList<byte[]> compressed_frame;
    private long mFirstClearShotDataSize;
    private int mImageDegree;
    private boolean mLoadCompleted;
    private Thread mMakeObjectInfoThread;
    private ObjBorderInfo[] mObjBoundaryInfo;
    private ObjectInfo[] mObjInfo;
    private ArrayList<ObjectInfoLayout> mObjectInfoList;
    private Size mPreviewSize;
    private Size mRealImageSize;
    private Thread mSaveClearShotImageThread;
    private float[] touchPoint;

    private class ObjectInfoLayout extends RelativeLayout {
        private ImageView mDotObject;
        private ImageView mNonDotObject;
        private Rect mObjectRect;
        private boolean mSelected;
        private int mTagIndex;

        public ObjectInfoLayout(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            this.mDotObject = null;
            this.mNonDotObject = null;
            this.mObjectRect = new Rect();
            this.mSelected = false;
            this.mTagIndex = PostviewClearShotActivity.CORNER_NONE;
        }

        public ObjectInfoLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
            this.mDotObject = null;
            this.mNonDotObject = null;
            this.mObjectRect = new Rect();
            this.mSelected = false;
            this.mTagIndex = PostviewClearShotActivity.CORNER_NONE;
        }

        public ObjectInfoLayout(Context contex, int tagIndex, Bitmap dot, Bitmap nonDot, Rect rect) {
            super(contex);
            this.mDotObject = null;
            this.mNonDotObject = null;
            this.mObjectRect = new Rect();
            this.mSelected = false;
            this.mTagIndex = PostviewClearShotActivity.CORNER_NONE;
            this.mObjectRect.set(rect);
            setLayoutParams(new LayoutParams(rect.right - rect.left, rect.bottom - rect.top));
            this.mNonDotObject = new ImageView(contex);
            this.mNonDotObject.setLayoutParams(new LayoutParams(-1, -1));
            this.mNonDotObject.setImageBitmap(nonDot);
            this.mNonDotObject.setScaleType(ScaleType.FIT_CENTER);
            this.mNonDotObject.setVisibility(PostviewClearShotActivity.CORNER_NONE);
            addView(this.mNonDotObject);
            this.mDotObject = new ImageView(contex);
            this.mDotObject.setLayoutParams(new LayoutParams(-1, -1));
            this.mDotObject.setImageBitmap(dot);
            this.mDotObject.setScaleType(ScaleType.FIT_CENTER);
            this.mDotObject.setVisibility(PostviewClearShotActivity.CORNER_NONE);
            addView(this.mDotObject);
            this.mTagIndex = tagIndex;
            setTag(Integer.valueOf(tagIndex));
        }

        public void setRect(Rect rect, int corner) {
            if (this.mNonDotObject != null && this.mDotObject != null) {
                LayoutParams rlp = (LayoutParams) this.mNonDotObject.getLayoutParams();
                addRuleWithCorner(rlp, corner);
                rlp.width = rect.right - rect.left;
                rlp.height = rect.bottom - rect.top;
                this.mNonDotObject.setLayoutParams(rlp);
                rlp = (LayoutParams) this.mDotObject.getLayoutParams();
                addRuleWithCorner(rlp, corner);
                rlp.width = rect.right - rect.left;
                rlp.height = rect.bottom - rect.top;
                this.mDotObject.setLayoutParams(rlp);
            }
        }

        public void addRuleWithCorner(LayoutParams rlp, int corner) {
            Common.resetLayoutParameter(rlp);
            if (corner == 0) {
                rlp.addRule(13);
                return;
            }
            if ((corner & PostviewClearShotActivity.CORNER_LEFT) != 0) {
                rlp.addRule(20);
                rlp.addRule(PostviewClearShotActivity.EXPAND_DIP);
            }
            if ((corner & PostviewClearShotActivity.CORNER_RIGHT) != 0) {
                rlp.addRule(21);
                rlp.addRule(PostviewClearShotActivity.EXPAND_DIP);
            }
            if ((corner & PostviewClearShotActivity.CORNER_TOP) != 0) {
                rlp.addRule(10);
                rlp.addRule(14);
            }
            if ((corner & PostviewClearShotActivity.CORNER_BOTTOM) != 0) {
                rlp.addRule(12);
                rlp.addRule(14);
            }
        }

        public void setSelected() {
            setSelected(!this.mSelected);
        }

        public void setSelected(boolean select) {
            if (this.mNonDotObject != null) {
                if (select) {
                    this.mNonDotObject.setVisibility(PostviewClearShotActivity.CORNER_RIGHT);
                } else {
                    this.mNonDotObject.setVisibility(PostviewClearShotActivity.CORNER_NONE);
                }
            }
            this.mSelected = select;
            animationStart(select);
        }

        public void animationStart(boolean select) {
            float toAlpha = 0.0f;
            if (this.mNonDotObject != null) {
                float fromAlpha = select ? RotateView.DEFAULT_TEXT_SCALE_X : 0.0f;
                if (!select) {
                    toAlpha = RotateView.DEFAULT_TEXT_SCALE_X;
                }
                AlphaAnimation aa = new AlphaAnimation(fromAlpha, toAlpha);
                aa.setDuration(300);
                this.mNonDotObject.clearAnimation();
                this.mNonDotObject.startAnimation(aa);
            }
        }

        public boolean getSelected() {
            return this.mSelected;
        }

        public int getObjectTagIndex() {
            return this.mTagIndex;
        }

        public Rect getObjectRect() {
            return this.mObjectRect;
        }

        public void unbind() {
            this.mObjectRect = null;
            this.mDotObject = null;
            this.mNonDotObject = null;
        }
    }

    public PostviewClearShotActivity() {
        this.mRealImageSize = new Size(LGT_Limit.PREVIEW_SIZE_WIDTH, LGT_Limit.PREVIEW_SIZE_HEIGHT);
        this.compressed_frame = new ArrayList();
        this.mFirstClearShotDataSize = 0;
        this.touchPoint = new float[]{0.0f, 0.0f};
        this.mObjectInfoList = new ArrayList();
        this.mMakeObjectInfoThread = null;
        this.mLoadCompleted = false;
        this.mObjInfo = null;
        this.mObjBoundaryInfo = null;
        this.mSaveClearShotImageThread = null;
    }

    protected void doPreProcessOnCreate() {
        releaseClearShotLibrary();
    }

    protected void doProcessOnCreate() {
        this.isFromCreateProcess = true;
        this.mLoadCompleted = false;
        this.mFirstClearShotDataSize = new File(((Uri) this.mPostViewParameters.getUriList().get(CORNER_NONE)).getPath()).lastModified();
        Fragment fragment = getFragmentManager().findFragmentByTag(CameraConstants.TAG_DIALOG_POSTVIEW);
        if (fragment != null && fragment.isAdded()) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            if (fragmentTransaction != null) {
                fragmentTransaction.remove(fragment);
                fragmentTransaction.commit();
            }
        }
    }

    protected void doProcessOnResume() {
        FileNamer.get().startFileNamer(getApplicationContext(), this.mPostViewParameters.getApplicationMode(), this.mPostViewParameters.getCurrentStorage(), this.mPostViewParameters.getCurrentStorageDirectory(), true);
        if (!this.isFromCreateProcess) {
            if (!checkValidateClearShotImages()) {
                postOnUiThread(this.mExitInteraction);
                this.isFromCreateProcess = false;
                return;
            } else if (checkClearShotFileOverwritten()) {
                CamLog.d(FaceDetector.TAG, "File over written! need to reload.");
                releaseClearShotLibrary();
                removeAllObjectInfoList();
                if (this.mCapturedBitmap != null) {
                    this.mCapturedBitmap.recycle();
                    this.mCapturedBitmap = null;
                }
                loadSingleCapturedImages();
                makeObjectInfoList();
            } else if (!this.mLoadCompleted) {
                makeObjectInfoView();
            }
        }
        this.isFromCreateProcess = false;
    }

    protected void doProcessOnPause() {
        if (this.mMakeObjectInfoThread != null && this.mMakeObjectInfoThread.isAlive()) {
            try {
                this.mMakeObjectInfoThread.interrupt();
                this.mMakeObjectInfoThread.join();
            } catch (InterruptedException e) {
                CamLog.d(FaceDetector.TAG, "InterruptedException: ", e);
            }
            this.mMakeObjectInfoThread = null;
        }
        dismissProgressDialog();
        FileNamer.get().close(getApplicationContext(), this.mPostViewParameters.getCurrentStorage());
    }

    protected void doProcessOnDestroy() {
        removeAllObjectInfoList();
        releaseClearShotLibrary();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        CamLog.d(FaceDetector.TAG, "onConfigurationChanged : newConfig = " + newConfig.orientation);
        if (this.mOrientationInfo != null) {
            this.mOrientationInfo.setOrientationByWindowOrientation();
        }
        reloadObjectInfoList();
        super.onConfigurationChanged(newConfig);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 16908332:
                doBackKeyInPostview();
                break;
            case R.id.postview_save /*2131559014*/:
                saveClearShotImages();
                break;
        }
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        if (menu.findItem(R.id.postview_save) == null) {
            if (ModelProperties.getCarrierCode() == 6) {
                getMenuInflater().inflate(R.menu.shot_postview_action_menu_save_vzw, menu);
            } else {
                getMenuInflater().inflate(R.menu.shot_postview_action_menu_save, menu);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    protected void setActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.sp_shot_mode_shot_and_clear);
    }

    protected void postviewShow() {
        CamLog.d(FaceDetector.TAG, "TIME_CHECK show()");
        View postView = findViewById(R.id.postview_shotmode_clearshot);
        if (postView == null) {
            CamLog.w(FaceDetector.TAG, "postviewShow : inflate view fail.");
            return;
        }
        if (postView.getVisibility() != 0) {
            postView.setVisibility(CORNER_NONE);
        }
        loadSingleCapturedImages();
        makeObjectInfoList();
    }

    protected void reloadedPostview() {
        if (this.mCapturedBitmap != null) {
            ((ImageView) findViewById(R.id.captured_image)).setImageBitmap(this.mCapturedBitmap);
        }
    }

    protected boolean loadSingleCapturedImages() {
        if (this.mPostViewParameters == null || this.mPostViewParameters.getUriList() == null || this.mPostViewParameters.getUriList().size() == 0) {
            CamLog.e(FaceDetector.TAG, "mUriList.size() is 0 !!");
            return false;
        }
        ImageView postview = (ImageView) findViewById(R.id.captured_image);
        if (this.mCapturedBitmap != null) {
            reloadedPostview();
            return true;
        }
        try {
            Uri capturedImageUri = (Uri) this.mPostViewParameters.getUriList().get(CORNER_NONE);
            if (this.mCapturedBitmap != null) {
                this.mCapturedBitmap.recycle();
                this.mCapturedBitmap = null;
            }
            this.mCapturedBitmap = loadCapturedImage(capturedImageUri, CORNER_NONE);
            if (this.mCapturedBitmap == null) {
                return false;
            }
            postview.setImageBitmap(this.mCapturedBitmap);
            return true;
        } catch (Exception e) {
            CamLog.e(FaceDetector.TAG, "Exception!", e);
        }
    }

    protected Bitmap loadCapturedImage(Uri uri, int degrees) {
        String str = FaceDetector.TAG;
        Object[] objArr = new Object[CORNER_TOP];
        objArr[CORNER_NONE] = uri;
        objArr[CORNER_LEFT] = Integer.valueOf(degrees);
        CamLog.d(str, String.format("Load captured image:%s, degrees:%d", objArr));
        Bitmap bmp = null;
        int degree = CORNER_NONE;
        if (!(this.mPostViewParameters == null || this.mOrientationInfo == null)) {
            Uri imageUri = (Uri) this.mPostViewParameters.getUriList().get(5);
            degree = ExifUtil.getExifOrientationDegree(imageUri.getPath());
            int[] dstSize = Util.getFitSizeOfBitmapForLCD(getActivity(), ExifUtil.getExifWidth(imageUri.getPath()), ExifUtil.getExifHeight(imageUri.getPath()));
            bmp = ImageManager.loadScaledBitmap(getContentResolver(), uri.toString(), dstSize[CORNER_NONE], dstSize[CORNER_LEFT]);
        }
        if (bmp != null) {
            return this.mImageHandler.getImage(bmp, degree, false);
        }
        CamLog.e(FaceDetector.TAG, "LoadBitmap fail!");
        return null;
    }

    protected void setupLayout() {
        inflateStub(R.id.stub_clearshot_postview);
    }

    protected void doBackKeyInPostview() {
        CamLog.d(FaceDetector.TAG, "KEYCODE_BACK");
        if (this.mPause || getActivity().isFinishing()) {
            CamLog.d(FaceDetector.TAG, "KEYCODE_BACK - return...");
        } else {
            onCreateDialog(7, this.mPostViewParameters.getApplicationMode());
        }
    }

    protected void doVolumeKey(KeyEvent event) {
        if (this.mPostViewParameters != null && CameraConstants.VOLUME_SHUTTER.equals(this.mPostViewParameters.getVolumeKey()) && event != null && event.getRepeatCount() == 0 && !getActivity().isFinishing()) {
            doBackKeyInPostview();
        }
    }

    private void releaseClearShotLibrary() {
        this.mObjInfo = null;
        this.mObjBoundaryInfo = null;
        if (AlmaCLRShot.getInstance() != null) {
            this.compressed_frame.clear();
            AlmaCLRShot.getInstance().release();
        }
    }

    private void makeObjectInfoList() {
        showProgressDialog(9, this.mPostViewParameters.getApplicationMode());
        this.mMakeObjectInfoThread = new Thread(new Runnable() {
            public void run() {
                if (PostviewClearShotActivity.this.mMakeObjectInfoThread != null && !PostviewClearShotActivity.this.mMakeObjectInfoThread.isInterrupted()) {
                    PostviewClearShotActivity.this.mLoadCompleted = PostviewClearShotActivity.this.createObjectInformation();
                }
            }
        });
        this.mMakeObjectInfoThread.start();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private byte[] getBytesFromFile(java.io.File r10) throws java.io.IOException {
        /*
        r9 = this;
        r4 = r10.length();
        r7 = (int) r4;
        r0 = new byte[r7];
        r6 = 0;
        r3 = 0;
        r2 = new java.io.FileInputStream;
        r2.<init>(r10);
    L_0x000e:
        r7 = r0.length;	 Catch:{ IOException -> 0x0020 }
        if (r6 >= r7) goto L_0x001b;
    L_0x0011:
        r7 = r0.length;	 Catch:{ IOException -> 0x0020 }
        r7 = r7 - r6;
        r3 = r2.read(r0, r6, r7);	 Catch:{ IOException -> 0x0020 }
        if (r3 < 0) goto L_0x001b;
    L_0x0019:
        r6 = r6 + r3;
        goto L_0x000e;
    L_0x001b:
        r2.close();
        r2 = 0;
    L_0x001f:
        return r0;
    L_0x0020:
        r1 = move-exception;
        r7 = "CameraApp";
        r8 = "getBytesFromFile-IOException : ";
        com.lge.camera.util.CamLog.w(r7, r8, r1);	 Catch:{ all -> 0x002d }
        r2.close();
        r2 = 0;
        goto L_0x001f;
    L_0x002d:
        r7 = move-exception;
        r2.close();
        r2 = 0;
        throw r7;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.camera.PostviewClearShotActivity.getBytesFromFile(java.io.File):byte[]");
    }

    private void setImageInfo() {
        if (this.mPostViewParameters != null && this.mOrientationInfo != null) {
            Uri imageUri = (Uri) this.mPostViewParameters.getUriList().get(5);
            int imageWidth = ExifUtil.getExifWidth(imageUri.getPath());
            int imageHeight = ExifUtil.getExifHeight(imageUri.getPath());
            this.mImageDegree = ExifUtil.getExifOrientationDegree(imageUri.getPath());
            this.mRealImageSize = new Size(imageWidth, imageHeight);
            int[] iArr = Util.calcFitSizeOfImageForLCD(getActivity(), this.mRealImageSize.getWidth(), this.mRealImageSize.getHeight(), this.mOrientationInfo.getOrientation());
            this.mPreviewSize = new Size(iArr[CORNER_NONE], iArr[CORNER_LEFT]);
        }
    }

    private boolean createObjectInformation() {
        CamLog.d(FaceDetector.TAG, "createObjectInformation-start");
        if (!(this.mPostViewParameters == null || this.mPostViewParameters.getUriList() == null || this.mPostViewParameters.getUriList().size() == 0)) {
            int i = CORNER_NONE;
            while (i < 6) {
                try {
                    this.compressed_frame.add(getBytesFromFile(new File(this.mPostViewParameters.getTimeMachineStorageDirectory() + (CameraConstants.CLEAR_SHOT_TEMPFILE + Integer.toString(i + CORNER_LEFT)) + CameraConstants.TIME_MACHINE_TEMPFILE_EXT)));
                    i += CORNER_LEFT;
                } catch (Exception e) {
                    CamLog.e(FaceDetector.TAG, "Exception!", e);
                    if (CamLog.getLogOn()) {
                        for (i = CORNER_NONE; i < 6; i += CORNER_LEFT) {
                            saveClearShotErrorPicture((byte[]) this.compressed_frame.get(i), i + CORNER_LEFT);
                        }
                    }
                    if (AlmaCLRShot.getInstance() != null) {
                        this.compressed_frame.clear();
                        AlmaCLRShot.getInstance().release();
                        finish();
                    }
                }
            }
            setImageInfo();
            AlmaCLRShot.getInstance().addInputFrame(this.compressed_frame, this.mRealImageSize, ImageType.JPEG);
            AlmaCLRShot.getInstance().initialize(this.mPreviewSize, this.mImageDegree, -1, CORNER_NONE, (this.mRealImageSize.getWidth() * this.mRealImageSize.getHeight()) / 100, CORNER_TOP, null);
            this.mCapturedBitmap = AlmaCLRShot.getInstance().getPreviewBitmap();
            this.mObjInfo = AlmaCLRShot.getInstance().getObjectInfoList();
            float dash_interval = TypedValue.applyDimension(CORNER_LEFT, STROKE_WIDTH_DP, getResources().getDisplayMetrics());
            float dash_phase = TypedValue.applyDimension(CORNER_LEFT, DASH_PHASE_DP, getResources().getDisplayMetrics());
            float stroke_width = TypedValue.applyDimension(CORNER_LEFT, STROKE_WIDTH_DP, getResources().getDisplayMetrics());
            Paint paint = new Paint();
            paint.setStrokeWidth(stroke_width);
            paint.setStrokeCap(Cap.ROUND);
            paint.setStrokeJoin(Join.ROUND);
            paint.setAntiAlias(true);
            float[] fArr = new float[CORNER_TOP];
            fArr[CORNER_NONE] = dash_interval;
            fArr[CORNER_LEFT] = dash_phase;
            paint.setPathEffect(new DashPathEffect(fArr, 0.0f));
            if (AlmaCLRShot.getInstance() != null) {
                this.mObjBoundaryInfo = AlmaCLRShot.getInstance().getObjBorderBitmap(paint);
                CamLog.d(FaceDetector.TAG, "objInfo size = " + this.mObjInfo.length + ", mObjBoundaryInfo = " + this.mObjBoundaryInfo.length);
            }
            if (this.mMakeObjectInfoThread == null || this.mMakeObjectInfoThread.isInterrupted()) {
                CamLog.d(FaceDetector.TAG, "mMakeObjectInfoThread is interrupted.");
                return false;
            }
            makeObjectInfoView();
        }
        CamLog.d(FaceDetector.TAG, "createObjectInformation-end");
        return true;
    }

    private void makeObjectInfoView() {
        postOnUiThread(new Runnable() {
            public void run() {
                PostviewClearShotActivity.this.removePostRunnable(this);
                CamLog.d(FaceDetector.TAG, "makeObjectInfoView-start");
                boolean isEmptyObject = true;
                if (!(PostviewClearShotActivity.this.mObjInfo == null || PostviewClearShotActivity.this.mObjBoundaryInfo == null)) {
                    int infoSize = PostviewClearShotActivity.this.mObjInfo.length;
                    if (infoSize > 0) {
                        isEmptyObject = false;
                    }
                    int i = PostviewClearShotActivity.CORNER_NONE;
                    while (i < infoSize) {
                        if (!(PostviewClearShotActivity.this.mObjInfo[i].getThumbnail() == null || PostviewClearShotActivity.this.mObjBoundaryInfo[i].getThumbnail() == null)) {
                            int i2 = PostviewClearShotActivity.this.mObjInfo[i].getRect().top;
                            Rect newRect = new Rect(PostviewClearShotActivity.this.mObjInfo[i].getRect().left, top, PostviewClearShotActivity.this.mObjInfo[i].getRect().right, PostviewClearShotActivity.this.mObjInfo[i].getRect().bottom);
                            ViewGroup objectLayout = (ViewGroup) PostviewClearShotActivity.this.findViewById(R.id.object_background_layout);
                            if (objectLayout != null) {
                                ObjectInfoLayout objectInfo = new ObjectInfoLayout(PostviewClearShotActivity.this.getApplicationContext(), i, PostviewClearShotActivity.this.mObjBoundaryInfo[i].getThumbnail(), PostviewClearShotActivity.this.mObjInfo[i].getThumbnail(), newRect);
                                PostviewClearShotActivity.this.addObjectInfoList(objectInfo);
                                ViewGroup.LayoutParams rlp = (LayoutParams) objectInfo.getLayoutParams();
                                Rect dstRect = PostviewClearShotActivity.this.calcObjectRect(newRect);
                                if (objectInfo != null) {
                                    objectInfo.setRect(dstRect, PostviewClearShotActivity.this.expandObjectRect(rlp, dstRect));
                                    objectInfo.setLayoutParams(rlp);
                                    objectLayout.addView(objectInfo);
                                }
                            }
                        }
                        i += PostviewClearShotActivity.CORNER_LEFT;
                    }
                }
                PostviewClearShotActivity.this.reloadedPostview();
                TextView guideTextView = (TextView) PostviewClearShotActivity.this.findViewById(R.id.remove_guide_text);
                if (guideTextView != null) {
                    guideTextView.setVisibility(PostviewClearShotActivity.CORNER_NONE);
                    if (isEmptyObject) {
                        guideTextView.setText(R.string.sp_clear_shot_not_found_object);
                    } else {
                        guideTextView.setText(R.string.sp_clear_shot_tab_guide_v2);
                    }
                }
                PostviewClearShotActivity.this.dismissProgressDialog();
                PostviewClearShotActivity.this.mObjInfo = null;
                PostviewClearShotActivity.this.mObjBoundaryInfo = null;
                PostviewClearShotActivity.this.mLoadCompleted = true;
            }
        });
    }

    private void reloadObjectInfoList() {
        ViewGroup objectLayout = (ViewGroup) findViewById(R.id.object_background_layout);
        if (this.mObjectInfoList != null && objectLayout != null) {
            Iterator i$ = this.mObjectInfoList.iterator();
            while (i$.hasNext()) {
                ObjectInfoLayout objectInfo = (ObjectInfoLayout) i$.next();
                LayoutParams rlp = (LayoutParams) objectInfo.getLayoutParams();
                Rect dstRect = calcObjectRect(objectInfo.getObjectRect());
                objectInfo.setRect(dstRect, expandObjectRect(rlp, dstRect));
                objectInfo.setLayoutParams(rlp);
            }
        }
    }

    private int expandObjectRect(LayoutParams rlp, Rect dstRect) {
        DisplayMetrics outMetrics = new DisplayMetrics();
        ((WindowManager) getSystemService("window")).getDefaultDisplay().getMetrics(outMetrics);
        int expand = Math.round(TypedValue.applyDimension(CORNER_LEFT, 15.0f, getResources().getDisplayMetrics()));
        if (dstRect.right - dstRect.left >= Math.round(TypedValue.applyDimension(CORNER_LEFT, 45.0f, getResources().getDisplayMetrics()))) {
            expand = CORNER_NONE;
        }
        rlp.width = (dstRect.right - dstRect.left) + (expand * CORNER_TOP);
        rlp.height = (dstRect.bottom - dstRect.top) + (expand * CORNER_TOP);
        rlp.leftMargin = dstRect.left - expand;
        rlp.topMargin = dstRect.top - expand;
        int corner = CORNER_NONE;
        if (rlp.leftMargin < 0) {
            rlp.leftMargin = CORNER_NONE;
            corner = CORNER_NONE | CORNER_LEFT;
        }
        if (rlp.leftMargin + rlp.width > outMetrics.widthPixels) {
            rlp.leftMargin = outMetrics.widthPixels - rlp.width;
            corner |= CORNER_RIGHT;
        }
        if (rlp.topMargin < 0) {
            rlp.topMargin = CORNER_NONE;
            corner |= CORNER_TOP;
        }
        if (rlp.topMargin + rlp.height <= outMetrics.heightPixels) {
            return corner;
        }
        rlp.topMargin = outMetrics.heightPixels - rlp.height;
        return corner | CORNER_BOTTOM;
    }

    private Rect calcObjectRect(Rect originRect) {
        int currOrientation;
        int leftMargin;
        int topMargin;
        int currPreviewWidth;
        Uri imageUri = (Uri) this.mPostViewParameters.getUriList().get(5);
        int imageWidth = ExifUtil.getExifWidth(imageUri.getPath());
        int imageHeight = ExifUtil.getExifHeight(imageUri.getPath());
        int degree = ExifUtil.getExifOrientationDegree(imageUri.getPath());
        int orientWidth = imageWidth;
        int orientHeight = imageHeight;
        if (FunctionProperties.isSupportRotateSaveImage()) {
            if (degree == 0 || degree == 180) {
                orientWidth = imageWidth;
            } else {
                orientWidth = imageHeight;
            }
            if (degree == 0 || degree == 180) {
                orientHeight = imageHeight;
            } else {
                orientHeight = imageWidth;
            }
        }
        if (this.mOrientationInfo != null) {
            currOrientation = this.mOrientationInfo.getOrientation();
        } else {
            currOrientation = CORNER_NONE;
        }
        int[] dstSize = Util.calcFitSizeOfImageForLCD(getActivity(), orientWidth, orientHeight, currOrientation);
        CamLog.d(FaceDetector.TAG, "dstSize[0] = " + dstSize[CORNER_NONE] + ", dstSize[1] = " + dstSize[CORNER_LEFT]);
        DisplayMetrics outMetrics = new DisplayMetrics();
        ((WindowManager) getSystemService("window")).getDefaultDisplay().getMetrics(outMetrics);
        if (currOrientation == 0 || currOrientation == CORNER_TOP) {
            leftMargin = (outMetrics.widthPixels - dstSize[CORNER_NONE]) / CORNER_TOP;
            topMargin = (outMetrics.heightPixels - dstSize[CORNER_LEFT]) / CORNER_TOP;
        } else {
            leftMargin = (outMetrics.widthPixels - dstSize[CORNER_NONE]) / CORNER_TOP;
            topMargin = (outMetrics.heightPixels - dstSize[CORNER_LEFT]) / CORNER_TOP;
        }
        if (this.mCapturedBitmap != null) {
            currPreviewWidth = this.mCapturedBitmap.getWidth();
        } else {
            currPreviewWidth = imageWidth;
        }
        float ratio = ((float) dstSize[CORNER_NONE]) / ((float) currPreviewWidth);
        int top = Math.round(((float) originRect.top) * ratio) + topMargin;
        int right = Math.round(((float) originRect.right) * ratio) + leftMargin;
        return new Rect(Math.round(((float) originRect.left) * ratio) + leftMargin, top, right, Math.round(((float) originRect.bottom) * ratio) + topMargin);
    }

    private void addObjectInfoList(ObjectInfoLayout objectInfo) {
        if (this.mObjectInfoList != null) {
            CamLog.d(FaceDetector.TAG, "addObjectInfoList");
            this.mObjectInfoList.add(objectInfo);
        }
    }

    private void removeAllObjectInfoList() {
        CamLog.d(FaceDetector.TAG, "removeAllObjectInfoList");
        if (this.mObjectInfoList != null) {
            Iterator i$ = this.mObjectInfoList.iterator();
            while (i$.hasNext()) {
                ((ObjectInfoLayout) i$.next()).unbind();
            }
            this.mObjectInfoList.clear();
            ViewGroup objectLayout = (ViewGroup) findViewById(R.id.object_background_layout);
            if (objectLayout != null) {
                objectLayout.removeAllViews();
            }
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == CORNER_LEFT) {
            Matrix inverse = new Matrix();
            ((ImageView) findViewById(R.id.captured_image)).getImageMatrix().invert(inverse);
            this.touchPoint[CORNER_NONE] = event.getX();
            this.touchPoint[CORNER_LEFT] = event.getY();
            inverse.mapPoints(this.touchPoint);
            ActionBar actionBar;
            try {
                int index = AlmaCLRShot.getInstance().getOccupiedObject(this.touchPoint[CORNER_NONE], this.touchPoint[CORNER_LEFT]);
                CamLog.d(FaceDetector.TAG, "Object selected, index = " + index);
                if (index != 0) {
                    ((ObjectInfoLayout) this.mObjectInfoList.get(index - 1)).setSelected();
                } else {
                    actionBar = getActionBar();
                    if (actionBar.isShowing()) {
                        actionBar.hide();
                    } else {
                        actionBar.show();
                    }
                }
            } catch (Exception e) {
                actionBar = getActionBar();
                if (actionBar.isShowing()) {
                    actionBar.hide();
                } else {
                    actionBar.show();
                }
            }
        }
        return super.onTouchEvent(event);
    }

    public boolean saveClearShotErrorPicture(byte[] data, int clearShotTempFileCount) {
        return ImageManager.saveTempFileForTimeMachineShot(data, this.mPostViewParameters.getTimeMachineStorageDirectory() + "/ClearShotError/", CameraConstants.CLEAR_SHOT_TEMPFILE + Integer.toString(clearShotTempFileCount), CameraConstants.TIME_MACHINE_TEMPFILE_EXT);
    }

    private void saveClearShotImages() {
        if (this.mPostViewParameters != null) {
            CamLog.d(FaceDetector.TAG, "saveClearShotImages : start.");
            if (this.mSaveClearShotImageThread == null || !this.mSaveClearShotImageThread.isAlive()) {
                showProgressDialog(10, this.mPostViewParameters.getApplicationMode());
                this.mSaveClearShotImageThread = new Thread(new Runnable() {
                    public void run() {
                        try {
                            String fileExt = CameraConstants.TIME_MACHINE_TEMPFILE_EXT;
                            long dateTaken = System.currentTimeMillis();
                            FileNamer.get().markTakeTime(CameraConstants.TYPE_SHOTMODE_CLEAR_SHOT, PostviewClearShotActivity.this.getSettingValue(Setting.KEY_SCENE_MODE));
                            if (PostviewClearShotActivity.this.mObjectInfoList.size() != 0) {
                                boolean[] tempArr = new boolean[PostviewClearShotActivity.this.mObjectInfoList.size()];
                                int j = PostviewClearShotActivity.CORNER_NONE;
                                Iterator i$ = PostviewClearShotActivity.this.mObjectInfoList.iterator();
                                while (i$.hasNext()) {
                                    ObjectInfoLayout objectInfo = (ObjectInfoLayout) i$.next();
                                    CamLog.d(FaceDetector.TAG, "saveClearShotImages: index() = " + objectInfo.getObjectTagIndex() + ", selected = " + objectInfo.getSelected());
                                    tempArr[j] = objectInfo.getSelected();
                                    j += PostviewClearShotActivity.CORNER_LEFT;
                                }
                                AlmaCLRShot.getInstance().setObjectList(tempArr);
                            }
                            byte[] saveBuffer = AlmaCLRShot.getInstance().processingSaveData();
                            String newFileName = FileNamer.get().getFileNewName(PostviewClearShotActivity.this.getApplicationContext(), PostviewClearShotActivity.this.mPostViewParameters.getApplicationMode(), PostviewClearShotActivity.this.mPostViewParameters.getCurrentStorage(), PostviewClearShotActivity.this.mPostViewParameters.getCurrentStorageDirectory(), false, CameraConstants.TYPE_SHOTMODE_CLEAR_SHOT, false);
                            CamLog.d(FaceDetector.TAG, "newFileName = " + newFileName);
                            String newFileDir = PostviewClearShotActivity.this.mPostViewParameters.getCurrentStorageDirectory();
                            String newFilePath = newFileDir + newFileName + fileExt;
                            File newFile = new File(newFilePath);
                            CamLog.d(FaceDetector.TAG, "Rename clear shot newFilePath = " + newFilePath);
                            File file = new File(newFileDir);
                            if (!file.exists()) {
                                file.mkdirs();
                            }
                            if (!(newFile.exists() || newFileName == null || newFileName.trim().length() == 0)) {
                                ImageManager.saveTempFileForTimeMachineShot(saveBuffer, newFileDir, newFileName, fileExt);
                                ExifInterface originalExif = new ExifInterface(PostviewClearShotActivity.this.mPostViewParameters.getTimeMachineStorageDirectory() + "ClearShotImage1" + CameraConstants.TIME_MACHINE_TEMPFILE_EXT);
                                ExifInterface exifInterface = new ExifInterface(newFile.getAbsolutePath());
                                String[] exceptionTags = new String[PostviewClearShotActivity.CORNER_TOP];
                                exceptionTags[PostviewClearShotActivity.CORNER_NONE] = "ImageLength";
                                exceptionTags[PostviewClearShotActivity.CORNER_LEFT] = "ImageWidth";
                                ExifUtil.copyExif(originalExif, exifInterface, exceptionTags);
                                exifInterface.saveAttributes();
                                Uri resultUri = ImageManager.insertToContentResolver(PostviewClearShotActivity.this.getContentResolver(), newFileName, dateTaken, PostviewClearShotActivity.this.mPostViewParameters.getLocationLatitude(), PostviewClearShotActivity.this.mPostViewParameters.getLocationLongitude(), newFileDir, newFileName + fileExt, PostviewClearShotActivity.this.mImageDegree, PostviewClearShotActivity.this.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE).equals(CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS));
                                Util.broadcastNewPicture(PostviewClearShotActivity.this.getActivity(), resultUri);
                                SharedPreferenceUtil.saveLastPicture(PostviewClearShotActivity.this.getActivity(), resultUri);
                                Util.requestUpBoxBackupPhoto(PostviewClearShotActivity.this.getActivity(), newFileName, PostviewClearShotActivity.this.getSettingValue(Setting.KEY_UPLUS_BOX).equals(CameraConstants.SMART_MODE_ON));
                                PostviewClearShotActivity.this.setSecureImageList(resultUri, true);
                            }
                            int i = PostviewClearShotActivity.CORNER_NONE;
                            while (i < 6) {
                                try {
                                    String fullFilePath = PostviewClearShotActivity.this.mPostViewParameters.getTimeMachineStorageDirectory() + (CameraConstants.CLEAR_SHOT_TEMPFILE + Integer.toString(i + PostviewClearShotActivity.CORNER_LEFT)) + CameraConstants.TIME_MACHINE_TEMPFILE_EXT;
                                    if (!Common.isFileExist(fullFilePath)) {
                                        CamLog.d(FaceDetector.TAG, "file is not exist : " + fullFilePath);
                                    } else if (!new File(fullFilePath).delete()) {
                                        CamLog.d(FaceDetector.TAG, "clear shot temp file delete fail.");
                                    }
                                    i += PostviewClearShotActivity.CORNER_LEFT;
                                } catch (Throwable e) {
                                    CamLog.e(FaceDetector.TAG, "deleteClearShotImages fail!:", e);
                                }
                            }
                            PostviewClearShotActivity.this.postOnUiThread(new Runnable() {
                                public void run() {
                                    PostviewClearShotActivity.this.removePostRunnable(this);
                                    PostviewClearShotActivity.this.dismissProgressDialog();
                                    PostviewClearShotActivity.this.saveFinished();
                                }
                            });
                        } catch (Throwable e2) {
                            CamLog.e(FaceDetector.TAG, "Exception!", e2);
                            PostviewClearShotActivity.this.finish();
                        }
                    }
                });
                this.mSaveClearShotImageThread.start();
                return;
            }
            CamLog.d(FaceDetector.TAG, "mSaveClearShotImageThread is already running.");
        }
    }

    protected void onCreateDialog(int dialogId, int applicationMode) {
        if (dialogId == 7) {
            SharedPreferences pref = getActivity().getSharedPreferences(Setting.SETTING_PRIMARY, CORNER_NONE);
            if (pref != null && pref.getBoolean(CameraConstants.CLEAR_SHOT_WARNING_AGAIN, false)) {
                if (this.mMakeObjectInfoThread != null && this.mMakeObjectInfoThread.isAlive()) {
                    this.mMakeObjectInfoThread.interrupt();
                }
                finish();
                return;
            }
        }
        PostviewDialog.getPostviewDialog(dialogId, applicationMode).show(getFragmentManager(), CameraConstants.TAG_DIALOG_POSTVIEW);
    }

    public void doClearShotWarningPositiveClick(CheckBox checkBox) {
        try {
            doClearShotWarningNegativeClick(checkBox);
        } catch (Exception e) {
            CamLog.e(FaceDetector.TAG, "Exception!", e);
        } finally {
            finish();
        }
    }

    public void doClearShotWarningNegativeClick(CheckBox checkBox) {
        try {
            if (checkBox.isChecked()) {
                Editor edit = getActivity().getSharedPreferences(Setting.SETTING_PRIMARY, CORNER_NONE).edit();
                edit.putBoolean(CameraConstants.CLEAR_SHOT_WARNING_AGAIN, true);
                edit.apply();
            }
            ActionBar actionBar = getActionBar();
            if (actionBar != null && !actionBar.isShowing()) {
                actionBar.show();
            }
        } catch (Exception e) {
            CamLog.e(FaceDetector.TAG, "Exception!", e);
        }
    }

    private boolean checkValidateClearShotImages() {
        int i = CORNER_NONE;
        while (i < 6) {
            try {
                if (!Common.isFileExist(this.mPostViewParameters.getTimeMachineStorageDirectory() + (CameraConstants.CLEAR_SHOT_TEMPFILE + Integer.toString(i + CORNER_LEFT)) + CameraConstants.TIME_MACHINE_TEMPFILE_EXT)) {
                    return false;
                }
                i += CORNER_LEFT;
            } catch (Exception e) {
                CamLog.e(FaceDetector.TAG, "deleteTimeMachineImages fail!:", e);
                return false;
            }
        }
        return true;
    }

    protected boolean checkClearShotFileOverwritten() {
        ArrayList<Uri> mTempFileNameList = this.mPostViewParameters.getUriList();
        boolean checkValue = false;
        if (!(mTempFileNameList == null || mTempFileNameList.isEmpty())) {
            File mPresentClearShotfile = new File(((Uri) this.mPostViewParameters.getUriList().get(CORNER_NONE)).getPath());
            if (this.mFirstClearShotDataSize != mPresentClearShotfile.lastModified()) {
                checkValue = true;
            } else {
                checkValue = false;
            }
            this.mFirstClearShotDataSize = mPresentClearShotfile.lastModified();
        }
        return checkValue;
    }
}
