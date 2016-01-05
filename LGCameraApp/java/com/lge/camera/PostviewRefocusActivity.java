package com.lge.camera;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.lge.camera.components.RotateView;
import com.lge.camera.postview.PostViewBar;
import com.lge.camera.postview.PostViewBarListener;
import com.lge.camera.postview.PostviewDialog;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.AnimationUtil;
import com.lge.camera.util.BitmapManager;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.camera.util.ExifUtil;
import com.lge.camera.util.ImageManager;
import com.lge.camera.util.SharedPreferenceUtil;
import com.lge.camera.util.Util;
import com.lge.olaworks.define.Ola_Exif.Tag;
import com.lge.olaworks.define.Ola_ShotParam;
import com.lge.olaworks.library.FaceDetector;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class PostviewRefocusActivity extends ShotPostviewActivity implements PostViewBarListener {
    private static final int ANI_END = 2;
    private static final int ANI_NONE = 0;
    private static final int ANI_RUNNING = 1;
    private static final int DUR_300 = 300;
    private BitmapDrawable mAllinFocusImage;
    private Animation mAnim;
    private int mAnimationState;
    private Timer mAnimationTimer;
    private int mCurMakingImageIndex;
    private long mDepthHeight;
    private long mDepthWidth;
    private long mFirstRefocusDataSize;
    private ArrayList<Drawable> mFrameList;
    private Runnable mHideTouchEffect;
    private int mImageDegree;
    private ImageSize mImageSize;
    private int mIndex;
    private boolean mIsAllinFocusShow;
    private boolean mLoadCompleted;
    private Thread mMakeFramesThread;
    private byte[] mMapBuf;
    private int mMaxFrameIndex;
    private ImageSize mPreviewSize;
    private Runnable mRefocusAnimationRunnable;
    private int mRefocusTouch;
    private ImageView mRefocusTouchView;
    private Thread mSaveRefocusImageThread;
    private int mScheduledTime;
    private int mSelectedIndex;
    private int mTimerCount;
    private boolean mUseEnteringAnimation;

    public class ImageSize {
        private int mHeight;
        private int mWidth;

        public ImageSize(int w, int h) {
            this.mWidth = w;
            this.mHeight = h;
        }

        public boolean isValid() {
            if (this.mWidth <= 0 || this.mHeight <= 0) {
                return false;
            }
            return true;
        }

        public int getWidth() {
            return this.mWidth;
        }

        public int getHeight() {
            return this.mHeight;
        }
    }

    public PostviewRefocusActivity() {
        this.mFirstRefocusDataSize = 0;
        this.mImageSize = null;
        this.mPreviewSize = null;
        this.mIsAllinFocusShow = false;
        this.mLoadCompleted = false;
        this.mMakeFramesThread = null;
        this.mSaveRefocusImageThread = null;
        this.mFrameList = new ArrayList();
        this.mAllinFocusImage = null;
        this.mSelectedIndex = ANI_NONE;
        this.mCurMakingImageIndex = ANI_NONE;
        this.mMaxFrameIndex = ANI_NONE;
        this.mIndex = ANI_NONE;
        this.mDepthWidth = 0;
        this.mDepthHeight = 0;
        this.mMapBuf = null;
        this.mImageDegree = ANI_NONE;
        this.mAnimationState = ANI_NONE;
        this.mTimerCount = ANI_NONE;
        this.mAnimationTimer = null;
        this.mAnim = null;
        this.mScheduledTime = 5;
        this.mUseEnteringAnimation = false;
        this.mRefocusTouch = ANI_NONE;
        this.mRefocusTouchView = null;
        this.mHideTouchEffect = new Runnable() {
            public void run() {
                PostviewRefocusActivity.this.removePostRunnable(this);
                if (PostviewRefocusActivity.this.mRefocusTouchView != null) {
                    PostviewRefocusActivity.this.mRefocusTouchView.clearAnimation();
                    PostviewRefocusActivity.this.mRefocusTouchView.setBackgroundResource(R.drawable.focus_guide);
                    PostviewRefocusActivity.this.mRefocusTouchView.setVisibility(4);
                }
            }
        };
        this.mRefocusAnimationRunnable = new Runnable() {
            public void run() {
                PostviewRefocusActivity.this.removePostRunnable(this);
                if (PostviewRefocusActivity.this.isPausing() || PostviewRefocusActivity.this.mAnimationTimer == null || PostviewRefocusActivity.this.mScheduledTime <= 5) {
                    PostviewRefocusActivity.this.mScheduledTime = PostviewRefocusActivity.this.mScheduledTime + PostviewRefocusActivity.ANI_RUNNING;
                    return;
                }
                try {
                    if (PostviewRefocusActivity.this.mTimerCount >= PostviewRefocusActivity.this.mMaxFrameIndex) {
                        PostviewRefocusActivity.this.stopRefocusAnimation();
                        PostviewRefocusActivity.this.updateGuideTextView();
                    } else if (PostviewRefocusActivity.this.mCurMakingImageIndex == 0 || PostviewRefocusActivity.this.mCurMakingImageIndex <= PostviewRefocusActivity.this.mTimerCount + PostviewRefocusActivity.ANI_RUNNING) {
                        PostviewRefocusActivity.this.mScheduledTime = PostviewRefocusActivity.this.mScheduledTime + PostviewRefocusActivity.ANI_RUNNING;
                    } else {
                        CamLog.d(FaceDetector.TAG, "mCurMakingImageIndex = " + PostviewRefocusActivity.this.mCurMakingImageIndex);
                        CamLog.d(FaceDetector.TAG, "mTimerCount : " + PostviewRefocusActivity.this.mTimerCount);
                        PostviewRefocusActivity.this.mScheduledTime = PostviewRefocusActivity.ANI_NONE;
                        if (!PostviewRefocusActivity.this.startFrameAnimation(PostviewRefocusActivity.this.mTimerCount)) {
                            PostviewRefocusActivity.this.stopRefocusAnimation();
                        }
                        PostviewRefocusActivity.this.mTimerCount = PostviewRefocusActivity.this.mTimerCount + PostviewRefocusActivity.ANI_RUNNING;
                        if (PostviewRefocusActivity.this.mTimerCount <= PostviewRefocusActivity.this.mMaxFrameIndex) {
                            PostviewRefocusActivity.this.setBarValue(PostviewRefocusActivity.this.mMaxFrameIndex - PostviewRefocusActivity.this.mTimerCount);
                        }
                    }
                } catch (Exception e) {
                    CamLog.e(FaceDetector.TAG, "Exception!", e);
                }
            }
        };
    }

    protected void doPreProcessOnCreate() {
        this.mMaxFrameIndex = this.mPostViewParameters.getUriList().size() - 1;
        this.mCurMakingImageIndex = ANI_NONE;
        if (this.mUseEnteringAnimation) {
            this.mAnimationState = ANI_NONE;
        } else {
            this.mAnimationState = ANI_END;
        }
    }

    protected void doProcessOnCreate() {
        this.isFromCreateProcess = true;
        this.mLoadCompleted = false;
        this.mFirstRefocusDataSize = new File(((Uri) this.mPostViewParameters.getUriList().get(ANI_NONE)).getPath()).lastModified();
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
        setBarHandlePos();
        if (this.isFromCreateProcess) {
            if (this.mUseEnteringAnimation) {
                startRefocusAnimation();
            } else {
                this.mAnimationState = ANI_END;
                this.mSelectedIndex = this.mMaxFrameIndex;
                setBarValue(ANI_NONE);
                setBarListener();
            }
        } else if (!checkValidateRefocusImages()) {
            postOnUiThread(this.mExitInteraction);
            this.isFromCreateProcess = false;
            return;
        } else if (checkTempFileOverwritten()) {
            CamLog.d(FaceDetector.TAG, "File over written! need to reload.");
            if (this.mCapturedBitmap != null) {
                this.mCapturedBitmap.recycle();
                this.mCapturedBitmap = null;
            }
            loadSingleCapturedImages();
            makeFrameList();
        } else {
            if (!this.mLoadCompleted) {
                makeFrameList();
                if (this.mUseEnteringAnimation) {
                    this.mAnimationState = ANI_NONE;
                }
            }
            if (this.mUseEnteringAnimation) {
                startRefocusAnimation();
            }
        }
        this.isFromCreateProcess = false;
    }

    protected void doProcessOnPause() {
        stopRefocusAnimation();
        if (this.mHideTouchEffect != null) {
            this.mHideTouchEffect.run();
        }
        if (this.mMakeFramesThread != null && this.mMakeFramesThread.isAlive()) {
            try {
                this.mMakeFramesThread.interrupt();
                this.mMakeFramesThread.join();
            } catch (InterruptedException e) {
                CamLog.d(FaceDetector.TAG, "InterruptedException: ", e);
            }
            this.mMakeFramesThread = null;
        }
    }

    protected void doProcessOnDestroy() {
        if (this.mFrameList != null) {
            int imageListSize = this.mFrameList == null ? ANI_NONE : this.mFrameList.size();
            for (int i = ANI_NONE; i < imageListSize; i += ANI_RUNNING) {
                Util.recycleBitmapDrawable((Drawable) this.mFrameList.get(i));
            }
            this.mFrameList.clear();
            this.mFrameList = null;
        }
        PostViewBar postviewBar = (PostViewBar) findViewById(R.id.refocus_bar_handler);
        if (postviewBar != null) {
            postviewBar.unbind();
        }
        if (this.mMapBuf != null) {
            this.mMapBuf = null;
        }
        if (this.mAllinFocusImage != null) {
            Util.recycleBitmapDrawable(this.mAllinFocusImage);
            this.mAllinFocusImage = null;
        }
        this.mImageSize = null;
        this.mPreviewSize = null;
        this.mRefocusTouchView = null;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        CamLog.d(FaceDetector.TAG, "onConfigurationChanged : newConfig = " + newConfig.orientation);
        if (this.mOrientationInfo != null) {
            this.mOrientationInfo.setOrientationByWindowOrientation();
            setBarHandlePos();
        }
        setImageSizeInfo();
        stopRefocusAnimation();
        showAllinFocusImage(this.mIsAllinFocusShow);
        updateGuideTextView();
        super.onConfigurationChanged(newConfig);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 16908332:
                doBackKeyInPostview();
                stopRefocusAnimation();
                break;
            case R.id.postview_allinfocus /*2131559013*/:
                stopRefocusAnimation();
                setAllinFocusOptionItem();
                showAllinFocusImage(this.mIsAllinFocusShow);
                updateGuideTextView();
                break;
            case R.id.postview_save /*2131559014*/:
                stopRefocusAnimation();
                saveRefocusImages();
                break;
        }
        return true;
    }

    private void setAllinFocusOptionItem() {
        this.mIsAllinFocusShow = !this.mIsAllinFocusShow;
        invalidateOptionsMenu();
    }

    private void showAllinFocusImage(boolean show) {
        if (this.mPostViewParameters == null) {
            CamLog.w(FaceDetector.TAG, "showAllinFocusImage : postview parameters get fail.");
            return;
        }
        ImageView postview = (ImageView) findViewById(R.id.captured_image);
        PostViewBar postviewBar = (PostViewBar) findViewById(R.id.refocus_bar_handler);
        if (postview != null && postviewBar != null) {
            if (this.mAllinFocusImage == null) {
                Uri allinfocusUri = this.mPostViewParameters.getSavedUri();
                String allinfocusImagePath = BitmapManager.getRealPathFromURI(getActivity(), allinfocusUri);
                CamLog.d(FaceDetector.TAG, "showAllinFocusImage : allinfocusUri = " + allinfocusUri);
                this.mAllinFocusImage = makeDrawableFrame(new int[ANI_END], Uri.parse(allinfocusImagePath));
            }
            if (show) {
                AnimationUtil.startShowingAnimation(postviewBar, false, 300, null, false);
                postview.setImageDrawable(this.mAllinFocusImage);
                postview.setVisibility(ANI_NONE);
                return;
            }
            refreshLoadCapturedImages(this.mSelectedIndex);
            AnimationUtil.startShowingAnimation(postviewBar, true, 300, null, false);
        }
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        if (this.mAnimationState != ANI_END) {
            return super.onPrepareOptionsMenu(menu);
        }
        if (menu.findItem(R.id.postview_allinfocus) == null) {
            getMenuInflater().inflate(R.menu.shot_postview_action_menu_refocus, menu);
        }
        menu.findItem(R.id.postview_allinfocus).setIcon(this.mIsAllinFocusShow ? R.drawable.camera_postview_shot_menu_allinfocus_on : R.drawable.camera_postview_shot_menu_allinfocus_off);
        return super.onPrepareOptionsMenu(menu);
    }

    protected void setActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.shot_mode_magic_focus);
    }

    protected void postviewShow() {
        CamLog.d(FaceDetector.TAG, "TIME_CHECK show()");
        View postView = findViewById(R.id.postview_shotmode_refocus);
        if (postView == null) {
            CamLog.w(FaceDetector.TAG, "postviewShow : inflate view fail.");
        } else if (checkValidateRefocusImages()) {
            if (postView.getVisibility() != 0) {
                postView.setVisibility(ANI_NONE);
            }
            loadSingleCapturedImages();
            makeFrameList();
        } else {
            postOnUiThread(this.mExitInteraction);
        }
    }

    protected void reloadedPostview() {
        if (this.mCapturedBitmap != null) {
            ((ImageView) findViewById(R.id.captured_image)).setImageBitmap(this.mCapturedBitmap);
        }
    }

    protected void setupLayout() {
        inflateStub(R.id.stub_refocus_postview);
        PostViewBar postviewBar = (PostViewBar) findViewById(R.id.refocus_bar_handler);
        if (postviewBar != null) {
            postviewBar.initBar(this.mMaxFrameIndex, this);
        }
    }

    protected void doBackKeyInPostview() {
        CamLog.d(FaceDetector.TAG, "KEYCODE_BACK");
        if (this.mPause || getActivity().isFinishing()) {
            CamLog.d(FaceDetector.TAG, "KEYCODE_BACK - return...");
        } else {
            onCreateDialog(8, this.mPostViewParameters.getApplicationMode());
        }
    }

    protected void doVolumeKey(KeyEvent event) {
        if (this.mPostViewParameters != null && CameraConstants.VOLUME_SHUTTER.equals(this.mPostViewParameters.getVolumeKey()) && event != null && event.getRepeatCount() == 0 && !getActivity().isFinishing()) {
            doBackKeyInPostview();
        }
    }

    private void makeFrameList() {
        if (this.mPostViewParameters == null) {
            CamLog.w(FaceDetector.TAG, "# makeFrameList : mPostViewParameters is null.");
            return;
        }
        if (!this.mUseEnteringAnimation) {
            showProgressDialog(9, this.mPostViewParameters.getApplicationMode());
        }
        if (this.mFrameList != null) {
            this.mFrameList.clear();
        }
        this.mMakeFramesThread = new Thread(new Runnable() {
            public void run() {
                CamLog.d(FaceDetector.TAG, "mMakeFramesThread-start");
                PostviewRefocusActivity.this.mSelectedIndex = PostviewRefocusActivity.ANI_NONE;
                PostviewRefocusActivity.this.mCurMakingImageIndex = PostviewRefocusActivity.ANI_NONE;
                int[] dstSize = new int[PostviewRefocusActivity.ANI_END];
                int i = PostviewRefocusActivity.ANI_NONE;
                while (i <= PostviewRefocusActivity.this.mMaxFrameIndex) {
                    if (PostviewRefocusActivity.this.mPostViewParameters != null && PostviewRefocusActivity.this.mMakeFramesThread != null && !PostviewRefocusActivity.this.mMakeFramesThread.isInterrupted()) {
                        BitmapDrawable bmpD = PostviewRefocusActivity.this.makeDrawableFrame(dstSize, (Uri) PostviewRefocusActivity.this.mPostViewParameters.getUriList().get(i));
                        if (PostviewRefocusActivity.this.mFrameList != null) {
                            PostviewRefocusActivity.this.mFrameList.add(i, bmpD);
                            if (i == 0 || (PostviewRefocusActivity.this.mAnimationState == PostviewRefocusActivity.ANI_END && i == PostviewRefocusActivity.this.mMaxFrameIndex)) {
                                final int refreshIdx = i;
                                PostviewRefocusActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        PostviewRefocusActivity.this.removePostRunnable(this);
                                        PostviewRefocusActivity.this.refreshLoadCapturedImages(refreshIdx);
                                    }
                                });
                            }
                        }
                        PostviewRefocusActivity.this.mCurMakingImageIndex = PostviewRefocusActivity.this.mCurMakingImageIndex + PostviewRefocusActivity.ANI_RUNNING;
                        i += PostviewRefocusActivity.ANI_RUNNING;
                    } else {
                        return;
                    }
                }
                if (PostviewRefocusActivity.this.mMakeFramesThread != null && !PostviewRefocusActivity.this.mMakeFramesThread.isInterrupted()) {
                    PostviewRefocusActivity.this.mAllinFocusImage = PostviewRefocusActivity.this.makeDrawableFrame(dstSize, Uri.parse(BitmapManager.getRealPathFromURI(PostviewRefocusActivity.this.getActivity(), PostviewRefocusActivity.this.mPostViewParameters.getSavedUri())));
                    PostviewRefocusActivity.this.makeDepthMapInfo();
                    PostviewRefocusActivity.this.setImageSizeInfo();
                    PostviewRefocusActivity.this.mLoadCompleted = true;
                    if (!PostviewRefocusActivity.this.mUseEnteringAnimation) {
                        PostviewRefocusActivity.this.postOnUiThread(new Runnable() {
                            public void run() {
                                AnimationUtil.startShowingAnimation((PostViewBar) PostviewRefocusActivity.this.findViewById(R.id.refocus_bar_handler), true, 300, null, false);
                                PostviewRefocusActivity.this.updateGuideTextView();
                                PostviewRefocusActivity.this.dismissProgressDialog();
                            }
                        });
                    }
                }
            }
        });
        this.mMakeFramesThread.start();
    }

    private BitmapDrawable makeDrawableFrame(int[] dstSize, Uri imageUri) {
        int degree = ExifUtil.getExifOrientationDegree(imageUri.getPath());
        dstSize = Util.getFitSizeOfBitmapForLCD(getActivity(), ExifUtil.getExifWidth(imageUri.getPath()), ExifUtil.getExifHeight(imageUri.getPath()));
        return new BitmapDrawable(getResources(), this.mImageHandler.getImage(ImageManager.loadScaledBitmap(getContentResolver(), imageUri.toString(), dstSize[ANI_NONE], dstSize[ANI_RUNNING]), degree, false));
    }

    private void setBarHandlePos() {
        int margin = ANI_NONE;
        if (this.mOrientationInfo != null) {
            try {
                Theme theme = getApplicationContext().getTheme();
                int[] iArr = new int[ANI_RUNNING];
                iArr[ANI_NONE] = 16843499;
                TypedArray styledAttributes = theme.obtainStyledAttributes(iArr);
                int actionBarHeight = (int) styledAttributes.getDimension(ANI_NONE, 0.0f);
                styledAttributes.recycle();
                if (this.mOrientationInfo.getOrientation() == 0 || this.mOrientationInfo.getOrientation() == ANI_END) {
                    margin = actionBarHeight;
                }
                View barHandlerLayout = findViewById(R.id.refocus_bar_handler_layout);
                if (barHandlerLayout != null) {
                    LayoutParams params = (LayoutParams) barHandlerLayout.getLayoutParams();
                    params.topMargin = margin;
                    barHandlerLayout.setLayoutParams(params);
                }
            } catch (Exception e) {
                CamLog.e(FaceDetector.TAG, "setBarHandlePos exception : ", e);
            }
        }
    }

    private void updateGuideTextView() {
        TextView guideTextView = (TextView) findViewById(R.id.refocus_guide_text_view);
        if (guideTextView != null && this.mOrientationInfo != null) {
            guideTextView.setVisibility(ANI_NONE);
            if (this.mIsAllinFocusShow) {
                guideTextView.setText(getString(R.string.sp_camera_refocus_postview_allinfocus_message));
            } else if (ModelProperties.getCarrierCode() != 6) {
                guideTextView.setText(getString(R.string.sp_refocus_postview_message_vzw_new));
            } else {
                String textContent = getString(R.string.sp_refocus_postview_message_new);
                String goal = "(#1#)";
                int index = textContent.indexOf(goal);
                if (index < 0 || index > textContent.length() - 1) {
                    guideTextView.setText(getString(R.string.sp_refocus_postview_message_vzw_new));
                    guideTextView.setVisibility(ANI_NONE);
                    return;
                }
                SpannableString result = new SpannableString(textContent);
                Drawable saveIcon = getResources().getDrawable(R.drawable.camera_postview_shot_menu_save);
                saveIcon.setBounds(ANI_NONE, ANI_NONE, saveIcon.getIntrinsicWidth(), saveIcon.getIntrinsicHeight());
                result.setSpan(new ImageSpan(saveIcon), index, goal.length() + index, 33);
                guideTextView.setText(result);
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int getSelectedFocusIndexFrame(float r29, float r30) {
        /*
        r28 = this;
        r0 = r28;
        r0 = r0.mImageSize;
        r24 = r0;
        if (r24 == 0) goto L_0x0020;
    L_0x0008:
        r0 = r28;
        r0 = r0.mPreviewSize;
        r24 = r0;
        if (r24 == 0) goto L_0x0020;
    L_0x0010:
        r0 = r28;
        r0 = r0.mMapBuf;
        r24 = r0;
        if (r24 == 0) goto L_0x0020;
    L_0x0018:
        r0 = r28;
        r0 = r0.mOrientationInfo;
        r24 = r0;
        if (r24 != 0) goto L_0x0023;
    L_0x0020:
        r24 = 0;
    L_0x0022:
        return r24;
    L_0x0023:
        r0 = r28;
        r0 = r0.mPreviewSize;
        r24 = r0;
        r15 = r24.getWidth();
        r0 = r28;
        r0 = r0.mPreviewSize;
        r24 = r0;
        r14 = r24.getHeight();
        r0 = r28;
        r0 = r0.mImageDegree;
        r24 = r0;
        r25 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
        r0 = r24;
        r1 = r25;
        if (r0 == r1) goto L_0x0053;
    L_0x0045:
        r0 = r28;
        r0 = r0.mImageDegree;
        r24 = r0;
        r25 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
        r0 = r24;
        r1 = r25;
        if (r0 != r1) goto L_0x005d;
    L_0x0053:
        r0 = (float) r15;
        r24 = r0;
        r29 = r24 - r29;
        r0 = (float) r14;
        r24 = r0;
        r30 = r24 - r30;
    L_0x005d:
        r0 = r28;
        r0 = r0.mImageDegree;
        r24 = r0;
        r25 = 90;
        r0 = r24;
        r1 = r25;
        if (r0 == r1) goto L_0x0079;
    L_0x006b:
        r0 = r28;
        r0 = r0.mImageDegree;
        r24 = r0;
        r25 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
        r0 = r24;
        r1 = r25;
        if (r0 != r1) goto L_0x007e;
    L_0x0079:
        r18 = r15;
        r15 = r14;
        r14 = r18;
    L_0x007e:
        r24 = "CameraApp";
        r25 = new java.lang.StringBuilder;
        r25.<init>();
        r26 = "SIK test previewWidth = ";
        r25 = r25.append(r26);
        r0 = r25;
        r25 = r0.append(r15);
        r26 = ", previewHeight = ";
        r25 = r25.append(r26);
        r0 = r25;
        r25 = r0.append(r14);
        r25 = r25.toString();
        com.lge.camera.util.CamLog.d(r24, r25);
        r24 = "CameraApp";
        r25 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x02c0 }
        r25.<init>();	 Catch:{ Exception -> 0x02c0 }
        r26 = "SIK test depthWidth = ";
        r25 = r25.append(r26);	 Catch:{ Exception -> 0x02c0 }
        r0 = r28;
        r0 = r0.mDepthWidth;	 Catch:{ Exception -> 0x02c0 }
        r26 = r0;
        r25 = r25.append(r26);	 Catch:{ Exception -> 0x02c0 }
        r26 = ", depthHeight = ";
        r25 = r25.append(r26);	 Catch:{ Exception -> 0x02c0 }
        r0 = r28;
        r0 = r0.mDepthHeight;	 Catch:{ Exception -> 0x02c0 }
        r26 = r0;
        r25 = r25.append(r26);	 Catch:{ Exception -> 0x02c0 }
        r25 = r25.toString();	 Catch:{ Exception -> 0x02c0 }
        com.lge.camera.util.CamLog.d(r24, r25);	 Catch:{ Exception -> 0x02c0 }
        r0 = r28;
        r0 = r0.mDepthWidth;	 Catch:{ Exception -> 0x02c0 }
        r24 = r0;
        r0 = r24;
        r0 = (float) r0;	 Catch:{ Exception -> 0x02c0 }
        r24 = r0;
        r0 = (float) r15;	 Catch:{ Exception -> 0x02c0 }
        r25 = r0;
        r2 = r24 / r25;
        r0 = r28;
        r0 = r0.mDepthHeight;	 Catch:{ Exception -> 0x02c0 }
        r24 = r0;
        r0 = r24;
        r0 = (float) r0;	 Catch:{ Exception -> 0x02c0 }
        r24 = r0;
        r0 = (float) r14;	 Catch:{ Exception -> 0x02c0 }
        r25 = r0;
        r3 = r24 / r25;
        r24 = r2 * r29;
        r4 = java.lang.Math.round(r24);	 Catch:{ Exception -> 0x02c0 }
        r24 = r3 * r30;
        r5 = java.lang.Math.round(r24);	 Catch:{ Exception -> 0x02c0 }
        r0 = r28;
        r0 = r0.mImageDegree;	 Catch:{ Exception -> 0x02c0 }
        r24 = r0;
        r25 = 90;
        r0 = r24;
        r1 = r25;
        if (r0 == r1) goto L_0x011a;
    L_0x010c:
        r0 = r28;
        r0 = r0.mImageDegree;	 Catch:{ Exception -> 0x02c0 }
        r24 = r0;
        r25 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
        r0 = r24;
        r1 = r25;
        if (r0 != r1) goto L_0x012a;
    L_0x011a:
        r18 = r4;
        r4 = r5;
        r0 = r28;
        r0 = r0.mDepthHeight;	 Catch:{ Exception -> 0x02c0 }
        r24 = r0;
        r0 = r24;
        r0 = (int) r0;	 Catch:{ Exception -> 0x02c0 }
        r24 = r0;
        r5 = r24 - r18;
    L_0x012a:
        r24 = 0;
        r25 = r4 + -30;
        r16 = java.lang.Math.max(r24, r25);	 Catch:{ Exception -> 0x02c0 }
        r24 = 0;
        r25 = r5 + -30;
        r17 = java.lang.Math.max(r24, r25);	 Catch:{ Exception -> 0x02c0 }
        r0 = r28;
        r0 = r0.mDepthWidth;	 Catch:{ Exception -> 0x02c0 }
        r24 = r0;
        r0 = r24;
        r0 = (int) r0;	 Catch:{ Exception -> 0x02c0 }
        r24 = r0;
        r25 = r16 + 61;
        r7 = java.lang.Math.min(r24, r25);	 Catch:{ Exception -> 0x02c0 }
        r0 = r28;
        r0 = r0.mDepthHeight;	 Catch:{ Exception -> 0x02c0 }
        r24 = r0;
        r0 = r24;
        r0 = (int) r0;	 Catch:{ Exception -> 0x02c0 }
        r24 = r0;
        r25 = r17 + 61;
        r8 = java.lang.Math.min(r24, r25);	 Catch:{ Exception -> 0x02c0 }
        r24 = "CameraApp";
        r25 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x02c0 }
        r25.<init>();	 Catch:{ Exception -> 0x02c0 }
        r26 = "SIK dx = ";
        r25 = r25.append(r26);	 Catch:{ Exception -> 0x02c0 }
        r0 = r25;
        r25 = r0.append(r4);	 Catch:{ Exception -> 0x02c0 }
        r26 = ", dy = ";
        r25 = r25.append(r26);	 Catch:{ Exception -> 0x02c0 }
        r0 = r25;
        r25 = r0.append(r5);	 Catch:{ Exception -> 0x02c0 }
        r25 = r25.toString();	 Catch:{ Exception -> 0x02c0 }
        com.lge.camera.util.CamLog.d(r24, r25);	 Catch:{ Exception -> 0x02c0 }
        r23 = 0;
        r13 = 0;
        r20 = 0;
        r19 = 0;
        r9 = 0;
        r22 = r17;
    L_0x018c:
        r0 = r22;
        if (r0 >= r8) goto L_0x01c5;
    L_0x0190:
        r21 = r16;
    L_0x0192:
        r0 = r21;
        if (r0 >= r7) goto L_0x01c2;
    L_0x0196:
        r0 = r28;
        r0 = r0.mMapBuf;	 Catch:{ Exception -> 0x02c0 }
        r24 = r0;
        r0 = r28;
        r0 = r0.mDepthWidth;	 Catch:{ Exception -> 0x02c0 }
        r26 = r0;
        r0 = r26;
        r0 = (int) r0;	 Catch:{ Exception -> 0x02c0 }
        r25 = r0;
        r25 = r25 * r22;
        r25 = r25 + r21;
        r24 = r24[r25];	 Catch:{ Exception -> 0x02c0 }
        switch(r24) {
            case 0: goto L_0x01b3;
            case 1: goto L_0x01b6;
            case 2: goto L_0x01b9;
            case 3: goto L_0x01bc;
            case 4: goto L_0x01bf;
            default: goto L_0x01b0;
        };	 Catch:{ Exception -> 0x02c0 }
    L_0x01b0:
        r21 = r21 + 1;
        goto L_0x0192;
    L_0x01b3:
        r23 = r23 + 1;
        goto L_0x01b0;
    L_0x01b6:
        r13 = r13 + 1;
        goto L_0x01b0;
    L_0x01b9:
        r20 = r20 + 1;
        goto L_0x01b0;
    L_0x01bc:
        r19 = r19 + 1;
        goto L_0x01b0;
    L_0x01bf:
        r9 = r9 + 1;
        goto L_0x01b0;
    L_0x01c2:
        r22 = r22 + 1;
        goto L_0x018c;
    L_0x01c5:
        r11 = new android.util.SparseIntArray;	 Catch:{ Exception -> 0x02c0 }
        r11.<init>();	 Catch:{ Exception -> 0x02c0 }
        r24 = 0;
        r0 = r23;
        r1 = r24;
        r11.put(r0, r1);	 Catch:{ Exception -> 0x02c0 }
        r24 = 1;
        r0 = r24;
        r11.put(r13, r0);	 Catch:{ Exception -> 0x02c0 }
        r24 = 2;
        r0 = r20;
        r1 = r24;
        r11.put(r0, r1);	 Catch:{ Exception -> 0x02c0 }
        r24 = 3;
        r0 = r19;
        r1 = r24;
        r11.put(r0, r1);	 Catch:{ Exception -> 0x02c0 }
        r24 = 4;
        r0 = r24;
        r11.put(r9, r0);	 Catch:{ Exception -> 0x02c0 }
        r24 = 5;
        r0 = r24;
        r10 = new int[r0];	 Catch:{ Exception -> 0x02c0 }
        r24 = 0;
        r10[r24] = r23;	 Catch:{ Exception -> 0x02c0 }
        r24 = 1;
        r10[r24] = r13;	 Catch:{ Exception -> 0x02c0 }
        r24 = 2;
        r10[r24] = r20;	 Catch:{ Exception -> 0x02c0 }
        r24 = 3;
        r10[r24] = r19;	 Catch:{ Exception -> 0x02c0 }
        r24 = 4;
        r10[r24] = r9;	 Catch:{ Exception -> 0x02c0 }
        java.util.Arrays.sort(r10);	 Catch:{ Exception -> 0x02c0 }
        r0 = r10.length;	 Catch:{ Exception -> 0x02c0 }
        r24 = r0;
        r24 = r24 + -1;
        r24 = r10[r24];	 Catch:{ Exception -> 0x02c0 }
        r0 = r24;
        r12 = r11.get(r0);	 Catch:{ Exception -> 0x02c0 }
        r24 = "CameraApp";
        r25 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x02c0 }
        r25.<init>();	 Catch:{ Exception -> 0x02c0 }
        r26 = "SIK test zeroCnt = ";
        r25 = r25.append(r26);	 Catch:{ Exception -> 0x02c0 }
        r0 = r25;
        r1 = r23;
        r25 = r0.append(r1);	 Catch:{ Exception -> 0x02c0 }
        r26 = ", oneCnt = ";
        r25 = r25.append(r26);	 Catch:{ Exception -> 0x02c0 }
        r0 = r25;
        r25 = r0.append(r13);	 Catch:{ Exception -> 0x02c0 }
        r26 = ", twoCnt = ";
        r25 = r25.append(r26);	 Catch:{ Exception -> 0x02c0 }
        r0 = r25;
        r1 = r20;
        r25 = r0.append(r1);	 Catch:{ Exception -> 0x02c0 }
        r26 = ", threeCnt = ";
        r25 = r25.append(r26);	 Catch:{ Exception -> 0x02c0 }
        r0 = r25;
        r1 = r19;
        r25 = r0.append(r1);	 Catch:{ Exception -> 0x02c0 }
        r26 = ", fourCnt = ";
        r25 = r25.append(r26);	 Catch:{ Exception -> 0x02c0 }
        r0 = r25;
        r25 = r0.append(r9);	 Catch:{ Exception -> 0x02c0 }
        r25 = r25.toString();	 Catch:{ Exception -> 0x02c0 }
        com.lge.camera.util.CamLog.d(r24, r25);	 Catch:{ Exception -> 0x02c0 }
        r24 = "CameraApp";
        r25 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x02c0 }
        r25.<init>();	 Catch:{ Exception -> 0x02c0 }
        r26 = "SIK test largestCnt = ";
        r25 = r25.append(r26);	 Catch:{ Exception -> 0x02c0 }
        r0 = r25;
        r25 = r0.append(r12);	 Catch:{ Exception -> 0x02c0 }
        r25 = r25.toString();	 Catch:{ Exception -> 0x02c0 }
        com.lge.camera.util.CamLog.d(r24, r25);	 Catch:{ Exception -> 0x02c0 }
        r0 = r28;
        r0.setBarValue(r12);	 Catch:{ Exception -> 0x02c0 }
        r0 = r28;
        r0 = r0.mMaxFrameIndex;	 Catch:{ Exception -> 0x02c0 }
        r24 = r0;
        r24 = r24 - r12;
        r0 = r24;
        r1 = r28;
        r1.mIndex = r0;	 Catch:{ Exception -> 0x02c0 }
    L_0x029a:
        r24 = "CameraApp";
        r25 = new java.lang.StringBuilder;
        r25.<init>();
        r26 = "mIndex = ";
        r25 = r25.append(r26);
        r0 = r28;
        r0 = r0.mIndex;
        r26 = r0;
        r25 = r25.append(r26);
        r25 = r25.toString();
        com.lge.camera.util.CamLog.d(r24, r25);
        r0 = r28;
        r0 = r0.mIndex;
        r24 = r0;
        goto L_0x0022;
    L_0x02c0:
        r6 = move-exception;
        r24 = "CameraApp";
        r25 = "Map file reading fail : ";
        r0 = r24;
        r1 = r25;
        com.lge.camera.util.CamLog.e(r0, r1, r6);
        goto L_0x029a;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.camera.PostviewRefocusActivity.getSelectedFocusIndexFrame(float, float):int");
    }

    private void makeDepthMapInfo() {
        DataInputStream dis;
        Exception e;
        FileInputStream fileInputStream;
        Throwable th;
        DataInputStream dis2 = null;
        try {
            File mapFile = new File(this.mPostViewParameters.getTimeMachineStorageDirectory() + CameraConstants.REFOCUS_MAP_FILE);
            int mapFileSize = (int) mapFile.length();
            CamLog.d(FaceDetector.TAG, "mapFile.length() = " + mapFileSize);
            this.mMapBuf = new byte[((int) mapFile.length())];
            FileInputStream fis = new FileInputStream(mapFile);
            try {
                dis = new DataInputStream(fis);
            } catch (Exception e2) {
                e = e2;
                fileInputStream = fis;
                try {
                    CamLog.e(FaceDetector.TAG, "Map file reading fail : ", e);
                    if (dis2 == null) {
                        try {
                            dis2.close();
                        } catch (IOException e3) {
                            CamLog.e(FaceDetector.TAG, "BufferedInpuStream cloase fail.");
                            return;
                        }
                    }
                } catch (Throwable th2) {
                    th = th2;
                    if (dis2 != null) {
                        try {
                            dis2.close();
                        } catch (IOException e4) {
                            CamLog.e(FaceDetector.TAG, "BufferedInpuStream cloase fail.");
                        }
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                fileInputStream = fis;
                if (dis2 != null) {
                    dis2.close();
                }
                throw th;
            }
            try {
                dis.readFully(this.mMapBuf);
                int metaStartPos = mapFileSize - 25;
                dis.close();
                dis2 = null;
                CamLog.d(FaceDetector.TAG, "Refocus metadata mark : " + (this.mMapBuf[metaStartPos] & Ola_ShotParam.AnimalMask_Random));
                this.mDepthWidth = (long) (((((this.mMapBuf[metaStartPos + ANI_RUNNING] & Ola_ShotParam.AnimalMask_Random) << 24) | ((this.mMapBuf[metaStartPos + ANI_END] & Ola_ShotParam.AnimalMask_Random) << 16)) | ((this.mMapBuf[metaStartPos + 3] & Ola_ShotParam.AnimalMask_Random) << 8)) | (this.mMapBuf[metaStartPos + 4] & Ola_ShotParam.AnimalMask_Random));
                this.mDepthHeight = (long) (((((this.mMapBuf[metaStartPos + 5] & Ola_ShotParam.AnimalMask_Random) << 24) | ((this.mMapBuf[metaStartPos + 6] & Ola_ShotParam.AnimalMask_Random) << 16)) | ((this.mMapBuf[metaStartPos + 7] & Ola_ShotParam.AnimalMask_Random) << 8)) | (this.mMapBuf[metaStartPos + 8] & Ola_ShotParam.AnimalMask_Random));
                if (dis2 != null) {
                    try {
                        dis2.close();
                        fileInputStream = fis;
                        return;
                    } catch (IOException e5) {
                        CamLog.e(FaceDetector.TAG, "BufferedInpuStream cloase fail.");
                        fileInputStream = fis;
                        return;
                    }
                }
            } catch (Exception e6) {
                e = e6;
                dis2 = dis;
                fileInputStream = fis;
                CamLog.e(FaceDetector.TAG, "Map file reading fail : ", e);
                if (dis2 == null) {
                    dis2.close();
                }
            } catch (Throwable th4) {
                th = th4;
                dis2 = dis;
                fileInputStream = fis;
                if (dis2 != null) {
                    dis2.close();
                }
                throw th;
            }
        } catch (Exception e7) {
            e = e7;
            CamLog.e(FaceDetector.TAG, "Map file reading fail : ", e);
            if (dis2 == null) {
                dis2.close();
            }
        }
    }

    private void setImageSizeInfo() {
        if (this.mPostViewParameters != null && this.mOrientationInfo != null) {
            Uri imageUri = (Uri) this.mPostViewParameters.getUriList().get(this.mMaxFrameIndex);
            int imageWidth = ExifUtil.getExifWidth(imageUri.getPath());
            int imageHeight = ExifUtil.getExifHeight(imageUri.getPath());
            this.mImageDegree = ExifUtil.getExifOrientationDegree(imageUri.getPath());
            if (this.mImageDegree == 90 || this.mImageDegree == Tag.IMAGE_DESCRIPTION) {
                int temp = imageWidth;
                imageWidth = imageHeight;
                imageHeight = temp;
            }
            this.mImageSize = new ImageSize(imageWidth, imageHeight);
            int[] size = Util.calcFitSizeOfImageForLCD(getActivity(), this.mImageSize.getWidth(), this.mImageSize.getHeight(), this.mOrientationInfo.getOrientation());
            this.mPreviewSize = new ImageSize(size[ANI_NONE], size[ANI_RUNNING]);
        }
    }

    private boolean refreshLoadCapturedImages(int selectedIndex) {
        if (this.mPostViewParameters == null) {
            CamLog.w(FaceDetector.TAG, "refreshLoadCapturedImages : postview parameters get fail.");
            return false;
        }
        int listSize = this.mPostViewParameters.getUriList().size();
        CamLog.d(FaceDetector.TAG, "refreshLoadCapturedImages : listSize = " + listSize);
        if (listSize > 0) {
            ImageView postview = (ImageView) findViewById(R.id.captured_image);
            if (postview != null) {
                try {
                    if (this.mFrameList != null && this.mFrameList.size() > selectedIndex) {
                        postview.setImageDrawable((BitmapDrawable) this.mFrameList.get(selectedIndex));
                    }
                    postview.setVisibility(ANI_NONE);
                } catch (Exception e) {
                    CamLog.e(FaceDetector.TAG, "refreshLoadCapturedImages Exception!", e);
                }
            }
        }
        return true;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (!(this.mOrientationInfo == null || this.mPreviewSize == null || this.mAnimationState != ANI_END)) {
            if (this.mIsAllinFocusShow) {
                setAllinFocusOptionItem();
                showAllinFocusImage(this.mIsAllinFocusShow);
                updateGuideTextView();
            }
            DisplayMetrics outMetrics = new DisplayMetrics();
            ((WindowManager) getSystemService("window")).getDefaultDisplay().getMetrics(outMetrics);
            int lcdWidth = outMetrics.widthPixels;
            int touchX = (int) event.getX();
            int touchY = (int) event.getY();
            int pointX = touchX - ((lcdWidth - this.mPreviewSize.getWidth()) / ANI_END);
            int pointY = touchY - ((outMetrics.heightPixels - this.mPreviewSize.getHeight()) / ANI_END);
            if (pointX >= 0 && pointY >= 0 && pointX < this.mPreviewSize.getWidth() && pointY < this.mPreviewSize.getHeight()) {
                switch (event.getActionMasked() & Ola_ShotParam.AnimalMask_Random) {
                    case ANI_NONE /*0*/:
                        showFocusDown(touchX, touchY);
                        break;
                    case ANI_RUNNING /*1*/:
                        showFocusUp(touchX, touchY);
                        try {
                            CamLog.d(FaceDetector.TAG, "SIK touch pointX = " + pointX + ", pointY = " + pointY);
                            this.mSelectedIndex = getSelectedFocusIndexFrame((float) pointX, (float) pointY);
                            refreshLoadCapturedImages(this.mSelectedIndex);
                            break;
                        } catch (Exception e) {
                            CamLog.e(FaceDetector.TAG, "onTouchEvent : ", e);
                            break;
                        }
                    case ANI_END /*2*/:
                        showFocusMove(touchX, touchY);
                        break;
                }
            }
            postOnUiThread(this.mHideTouchEffect);
        }
        return super.onTouchEvent(event);
    }

    private void showFocusDown(int x, int y) {
        this.mRefocusTouchView = (ImageView) findViewById(R.id.touch_focus_view);
        if (this.mRefocusTouchView != null) {
            this.mRefocusTouchView.clearAnimation();
            this.mRefocusTouchView.setBackgroundResource(R.drawable.focus_guide);
            this.mRefocusTouch = Common.getPixelFromDimens(getApplicationContext(), R.dimen.focus_rectangle_width);
            showFocusMove(x, y);
        }
    }

    private void showFocusMove(int x, int y) {
        removePostRunnable(this.mHideTouchEffect);
        FrameLayout rl = (FrameLayout) findViewById(R.id.touch_focus_view_layout_framelayout_inner);
        if (rl != null) {
            FrameLayout.LayoutParams lpLayout = (FrameLayout.LayoutParams) rl.getLayoutParams();
            lpLayout.setMarginStart(x - (this.mRefocusTouch / ANI_END));
            lpLayout.topMargin = y - (this.mRefocusTouch / ANI_END);
            rl.setLayoutParams(lpLayout);
        }
        if (this.mRefocusTouchView != null && this.mRefocusTouchView.getVisibility() != 0) {
            this.mRefocusTouchView.setVisibility(ANI_NONE);
        }
    }

    private void showFocusUp(int x, int y) {
        if (this.mRefocusTouchView != null) {
            ScaleAnimation mAniFocusScale = new ScaleAnimation(RotateView.DEFAULT_TEXT_SCALE_X, 0.59f, RotateView.DEFAULT_TEXT_SCALE_X, 0.59f, (float) (this.mRefocusTouch / ANI_END), (float) (this.mRefocusTouch / ANI_END));
            AlphaAnimation mAniFocusAlpha = new AlphaAnimation(0.25f, RotateView.DEFAULT_TEXT_SCALE_X);
            AnimationSet aniSet = new AnimationSet(true);
            aniSet.addAnimation(mAniFocusScale);
            aniSet.addAnimation(mAniFocusAlpha);
            aniSet.setFillAfter(true);
            aniSet.setDuration(300);
            aniSet.setInterpolator(new AccelerateDecelerateInterpolator());
            aniSet.setAnimationListener(new AnimationListener() {
                public void onAnimationStart(Animation animation) {
                    if (PostviewRefocusActivity.this.mRefocusTouchView != null) {
                        PostviewRefocusActivity.this.mRefocusTouchView.setBackgroundResource(R.drawable.focus_fail_taf);
                        PostviewRefocusActivity.this.mRefocusTouchView.setVisibility(PostviewRefocusActivity.ANI_NONE);
                    }
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    PostviewRefocusActivity.this.postOnUiThread(PostviewRefocusActivity.this.mHideTouchEffect, 500);
                }
            });
            this.mRefocusTouchView.startAnimation(aniSet);
        }
    }

    private void saveRefocusImages() {
        if (this.mPostViewParameters != null) {
            CamLog.d(FaceDetector.TAG, "saveRefocusImages : start.");
            if (this.mSaveRefocusImageThread == null || !this.mSaveRefocusImageThread.isAlive()) {
                showProgressDialog(10, this.mPostViewParameters.getApplicationMode());
                this.mSaveRefocusImageThread = new Thread(new Runnable() {
                    public void run() {
                        try {
                            if (!PostviewRefocusActivity.this.mIsAllinFocusShow) {
                                String fileExt = CameraConstants.TIME_MACHINE_TEMPFILE_EXT;
                                Uri savedUri = PostviewRefocusActivity.this.mPostViewParameters.getSavedUri();
                                String fileDir = PostviewRefocusActivity.this.mPostViewParameters.getCurrentStorageDirectory();
                                String savedFileName = PostviewRefocusActivity.this.mPostViewParameters.getSaveFileName();
                                CamLog.d(FaceDetector.TAG, "savedFileName = " + savedFileName);
                                File dir = new File(fileDir);
                                if (!dir.exists()) {
                                    dir.mkdirs();
                                }
                                String savedFilePath = BitmapManager.getRealPathFromURI(PostviewRefocusActivity.this.getActivity(), savedUri);
                                int degree = ExifUtil.getExifOrientationDegree(savedFilePath);
                                File savedFile = new File(savedFilePath);
                                if (savedFile.exists() && savedFile.delete()) {
                                    ImageManager.deleteImage(PostviewRefocusActivity.this.getContentResolver(), savedUri);
                                    PostviewRefocusActivity.this.setSecureImageList(savedUri, false);
                                }
                                File savingFile = new File(((Uri) PostviewRefocusActivity.this.mPostViewParameters.getUriList().get(PostviewRefocusActivity.this.mSelectedIndex)).getPath());
                                savedFile = new File(savedFilePath);
                                if (savingFile.exists() && savedFileName != null && savedFileName.trim().length() != 0 && savingFile.renameTo(savedFile)) {
                                    Uri resultUri = ImageManager.insertToContentResolver(PostviewRefocusActivity.this.getContentResolver(), savedFileName, System.currentTimeMillis(), PostviewRefocusActivity.this.mPostViewParameters.getLocationLatitude(), PostviewRefocusActivity.this.mPostViewParameters.getLocationLongitude(), fileDir, savedFileName + fileExt, degree, false);
                                    CamLog.d(FaceDetector.TAG, "result uri = " + resultUri);
                                    Util.broadcastNewPicture(PostviewRefocusActivity.this.getActivity(), resultUri);
                                    SharedPreferenceUtil.saveLastPicture(PostviewRefocusActivity.this.getActivity(), resultUri);
                                    Util.requestUpBoxBackupPhoto(PostviewRefocusActivity.this.getActivity(), savedFileName, PostviewRefocusActivity.this.getSettingValue(Setting.KEY_UPLUS_BOX).equals(CameraConstants.SMART_MODE_ON));
                                    PostviewRefocusActivity.this.setSecureImageList(resultUri, true);
                                }
                            }
                            PostviewRefocusActivity.this.deleteAllTempFiles();
                            PostviewRefocusActivity.this.postOnUiThread(new Runnable() {
                                public void run() {
                                    PostviewRefocusActivity.this.removePostRunnable(this);
                                    PostviewRefocusActivity.this.saveFinished();
                                }
                            });
                        } catch (Exception e) {
                            CamLog.e(FaceDetector.TAG, "Exception!", e);
                            PostviewRefocusActivity.this.finish();
                        }
                    }
                });
                this.mSaveRefocusImageThread.start();
                return;
            }
            CamLog.d(FaceDetector.TAG, "saveRefocusImagesThread is already running.");
        }
    }

    private void deleteTempFile(String fileNameWithExtension) {
        try {
            String filePath = this.mPostViewParameters.getTimeMachineStorageDirectory() + fileNameWithExtension;
            File file = new File(filePath);
            if (file.exists() && file.delete()) {
                CamLog.d(FaceDetector.TAG, "Refocus TempFile is deleted : " + filePath);
            } else {
                CamLog.d(FaceDetector.TAG, "Refocus TempFile delete fail.");
            }
        } catch (Exception e) {
            CamLog.e(FaceDetector.TAG, "deleteTempFile fail!:", e);
        }
    }

    private void deleteAllTempFiles() {
        int i = ANI_NONE;
        while (i <= this.mMaxFrameIndex) {
            try {
                deleteTempFile((CameraConstants.REFOCUS_SHOT_TEMPFILE + Integer.toString(i + ANI_RUNNING)) + CameraConstants.TIME_MACHINE_TEMPFILE_EXT);
                i += ANI_RUNNING;
            } catch (Exception e) {
                CamLog.e(FaceDetector.TAG, "deleteAllTempFiles fail!:", e);
                return;
            }
        }
        deleteTempFile(CameraConstants.REFOCUS_MAP_FILE);
    }

    protected void onCreateDialog(int dialogId, int applicationMode) {
        if (dialogId == 8) {
            SharedPreferences pref = getActivity().getSharedPreferences(Setting.SETTING_PRIMARY, ANI_NONE);
            if (pref != null && pref.getBoolean(CameraConstants.REFOCUS_WARNING_AGAIN, false)) {
                if (this.mMakeFramesThread != null && this.mMakeFramesThread.isAlive()) {
                    this.mMakeFramesThread.interrupt();
                }
                finish();
                return;
            }
        }
        stopRefocusAnimation();
        PostviewDialog.getPostviewDialog(dialogId, applicationMode).show(getFragmentManager(), CameraConstants.TAG_DIALOG_POSTVIEW);
    }

    public void doRefocusWarningPositiveClick(CheckBox checkBox) {
        try {
            doRefocusWarningNegativeClick(checkBox);
            deleteAllTempFiles();
        } catch (Exception e) {
            CamLog.e(FaceDetector.TAG, "Exception!", e);
        } finally {
            finish();
        }
    }

    public void doRefocusWarningNegativeClick(CheckBox checkBox) {
        try {
            if (checkBox.isChecked()) {
                Editor edit = getActivity().getSharedPreferences(Setting.SETTING_PRIMARY, ANI_NONE).edit();
                edit.putBoolean(CameraConstants.REFOCUS_WARNING_AGAIN, true);
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

    private boolean checkValidateRefocusImages() {
        int i = ANI_NONE;
        while (i <= this.mMaxFrameIndex) {
            try {
                if (!isTempfileExist((CameraConstants.REFOCUS_SHOT_TEMPFILE + Integer.toString(i + ANI_RUNNING)) + CameraConstants.TIME_MACHINE_TEMPFILE_EXT)) {
                    return false;
                }
                i += ANI_RUNNING;
            } catch (Exception e) {
                CamLog.e(FaceDetector.TAG, "checkValidateRefocusImages fail!:", e);
                return false;
            }
        }
        if (isTempfileExist(CameraConstants.REFOCUS_MAP_FILE) && isAllinfocusfileExist()) {
            return true;
        }
        return false;
    }

    private boolean isAllinfocusfileExist() {
        boolean z = false;
        try {
            if (this.mPostViewParameters == null) {
                CamLog.w(FaceDetector.TAG, "mPostViewParameters.");
            } else {
                String allinfocusImagePath = BitmapManager.getRealPathFromURI(getActivity(), this.mPostViewParameters.getSavedUri());
                if (allinfocusImagePath != null) {
                    z = Common.isFileExist(allinfocusImagePath);
                }
            }
        } catch (Exception e) {
            CamLog.e(FaceDetector.TAG, "isTempfileExit fail!:", e);
        }
        return z;
    }

    private boolean isTempfileExist(String fileNameWithExtension) {
        try {
            return Common.isFileExist(this.mPostViewParameters.getTimeMachineStorageDirectory() + fileNameWithExtension);
        } catch (Exception e) {
            CamLog.e(FaceDetector.TAG, "isTempfileExit fail!:", e);
            return false;
        }
    }

    protected boolean checkTempFileOverwritten() {
        ArrayList<Uri> mTempFileNameList = this.mPostViewParameters.getUriList();
        boolean checkValue = false;
        if (!(mTempFileNameList == null || mTempFileNameList.isEmpty())) {
            File mPresentTempfile = new File(((Uri) this.mPostViewParameters.getUriList().get(ANI_NONE)).getPath());
            if (this.mFirstRefocusDataSize != mPresentTempfile.lastModified()) {
                checkValue = true;
            } else {
                checkValue = false;
            }
            this.mFirstRefocusDataSize = mPresentTempfile.lastModified();
        }
        return checkValue;
    }

    private void changeFrame(int curIndex) {
        ImageView imageView = (ImageView) findViewById(R.id.captured_image);
        if (this.mFrameList != null && imageView != null && curIndex <= this.mMaxFrameIndex) {
            imageView.setImageDrawable((BitmapDrawable) this.mFrameList.get(curIndex));
        }
    }

    private void setBarValue(int barValue) {
        PostViewBar postviewBar = (PostViewBar) findViewById(R.id.refocus_bar_handler);
        if (postviewBar != null) {
            postviewBar.setBarValue(barValue);
        }
    }

    private void setBarListener() {
        PostViewBar postviewBar = (PostViewBar) findViewById(R.id.refocus_bar_handler);
        if (postviewBar != null) {
            postviewBar.setListener(true);
        }
    }

    public void onCursorUpdated(int value) {
        final int frameValue = Math.max(this.mMaxFrameIndex - value, ANI_NONE);
        CamLog.d(FaceDetector.TAG, "onCursorUpdated value = " + value + ", frameValue = " + frameValue);
        postOnUiThread(new Runnable() {
            public void run() {
                PostviewRefocusActivity.this.removePostRunnable(this);
                if (PostviewRefocusActivity.this.mFrameList.size() > frameValue) {
                    PostviewRefocusActivity.this.changeFrame(frameValue);
                    PostviewRefocusActivity.this.mSelectedIndex = frameValue;
                    PostviewRefocusActivity.this.invalidateOptionsMenu();
                }
            }
        });
    }

    public void onCursorMoving(boolean actionEnd) {
    }

    public int getOrientation() {
        return this.mOrientationInfo.getOrientation();
    }

    public int getPx(int resId) {
        return Common.getPixelFromDimens(getApplicationContext(), resId);
    }

    private void initializeFrameAnimation() {
        if (this.mAnim == null) {
            this.mAnim = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.time_machine);
            this.mAnim.setFillAfter(true);
        }
    }

    private boolean startFrameAnimation(int curIndex) {
        ImageView frameView = (ImageView) findViewById(R.id.captured_ani_view);
        ImageView frameBackView = (ImageView) findViewById(R.id.captured_image);
        if (this.mFrameList == null || frameView == null || frameBackView == null) {
            return false;
        }
        if (curIndex < this.mMaxFrameIndex) {
            BitmapDrawable bmpD1 = (BitmapDrawable) this.mFrameList.get(curIndex);
            BitmapDrawable bmpD2 = (BitmapDrawable) this.mFrameList.get(curIndex + ANI_RUNNING);
            frameBackView.setVisibility(ANI_NONE);
            frameBackView.setImageDrawable(bmpD2);
            frameView.setVisibility(ANI_NONE);
            frameView.setImageDrawable(bmpD1);
            frameView.clearAnimation();
            frameView.startAnimation(this.mAnim);
        } else if (curIndex == this.mMaxFrameIndex) {
            frameBackView.setImageDrawable((BitmapDrawable) this.mFrameList.get(curIndex));
            frameBackView.setVisibility(ANI_NONE);
            frameView.setVisibility(8);
        }
        return true;
    }

    private void startRefocusAnimation() {
        CamLog.d(FaceDetector.TAG, "startRefocusAnimation-start");
        if (this.mAnimationState != 0) {
            updateGuideTextView();
            if (this.mAnimationState == ANI_RUNNING) {
                setBarValue(ANI_NONE);
            }
            this.mAnimationState = ANI_END;
            invalidateOptionsMenu();
            if (this.mIsAllinFocusShow) {
                showAllinFocusImage(true);
                return;
            } else {
                refreshLoadCapturedImages(this.mSelectedIndex);
                return;
            }
        }
        setBarValue(this.mMaxFrameIndex);
        postOnUiThread(new Runnable() {
            public void run() {
                PostviewRefocusActivity.this.removePostRunnable(this);
                if (PostviewRefocusActivity.this.mPostViewParameters != null && PostviewRefocusActivity.this.mPostViewParameters.getUriList() != null) {
                    PostviewRefocusActivity.this.initializeFrameAnimation();
                    PostviewRefocusActivity.this.mAnimationState = PostviewRefocusActivity.ANI_RUNNING;
                    PostviewRefocusActivity.this.mTimerCount = PostviewRefocusActivity.ANI_NONE;
                    View imageView = PostviewRefocusActivity.this.findViewById(R.id.captured_image);
                    if (imageView != null) {
                        imageView.setVisibility(4);
                    }
                    PostviewRefocusActivity.this.mAnimationTimer = new Timer("RefocusAnimation");
                    PostviewRefocusActivity.this.mAnimationTimer.scheduleAtFixedRate(new TimerTask() {
                        public void run() {
                            PostviewRefocusActivity.this.runOnUiThread(PostviewRefocusActivity.this.mRefocusAnimationRunnable);
                        }
                    }, 100, 100);
                }
            }
        }, 100);
    }

    private void stopRefocusAnimation() {
        if (this.mAnimationTimer != null) {
            CamLog.d(FaceDetector.TAG, "stopRefocusAnimation-stop");
            this.mAnimationTimer.purge();
            this.mAnimationTimer.cancel();
            this.mAnimationTimer = null;
            this.mSelectedIndex = this.mMaxFrameIndex;
            setBarValue(ANI_NONE);
            setBarListener();
            ImageView aniView = (ImageView) findViewById(R.id.captured_ani_view);
            if (aniView != null) {
                aniView.clearAnimation();
                aniView.setVisibility(8);
                Util.clearImageViewDrawableOnly(aniView);
            }
            View imageView = findViewById(R.id.captured_image);
            if (imageView != null) {
                imageView.clearAnimation();
                imageView.setVisibility(ANI_NONE);
            }
            refreshLoadCapturedImages(this.mSelectedIndex);
            this.mAnimationState = ANI_END;
            updateGuideTextView();
            invalidateOptionsMenu();
        } else {
            this.mAnimationState = ANI_END;
        }
        this.mTimerCount = ANI_NONE;
    }
}
