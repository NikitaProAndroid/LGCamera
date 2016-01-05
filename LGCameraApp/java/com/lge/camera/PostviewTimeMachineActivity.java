package com.lge.camera;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.CheckBox;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import com.lge.camera.components.RotateView;
import com.lge.camera.postview.GalleryThumbnailLayout;
import com.lge.camera.postview.PostviewDialog;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.camera.util.ExifUtil;
import com.lge.camera.util.FileNamer;
import com.lge.camera.util.ImageManager;
import com.lge.camera.util.SharedPreferenceUtil;
import com.lge.camera.util.Util;
import com.lge.olaworks.library.FaceDetector;
import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class PostviewTimeMachineActivity extends ShotPostviewActivity {
    private static final int GALLERY_LAUNCH_CLICKED = 1;
    private static final int GALLERY_LAUNCH_NONE = 0;
    private static final int GALLERY_LAUNCH_STARTED = 2;
    private static final int TIMEMACHINE_EFFECT_NOT_START = 0;
    private static final int TIMEMACHINE_SAVE_EFFECT = 1;
    private static final int TIMEMACHINE_SAVE_NORMAL = 2;
    private Animation anim;
    private boolean isAnimationRunning;
    private int isGalleryLaunchingState;
    private Timer mAnimationTimer;
    private int mCurrentMakingImageIndex;
    private long mFirstTimeMachineDataSize;
    private Thread mMakeGalleryImageThread;
    private OnClickListener mOnTimemachineImageClickListener;
    private int mScheduledTime;
    private ArrayList<Integer> mSelectedIndexs;
    private ArrayList<Drawable> mThumbList;
    private ThumbnailSizeInfo mThumbSizeInfo;
    private Runnable mTimeMachineAnimationRunnable;
    private int mTimeMachineShotCount;
    private int mTimemachineMode;
    private int mTimerCount;
    private Point outSize;
    private Runnable saveButtonDone;

    private class ThumbnailSizeInfo {
        private int mLeftMargin;
        private int mThumbHeight;
        private int mThumbWidth;

        private ThumbnailSizeInfo() {
            this.mThumbWidth = PostviewTimeMachineActivity.TIMEMACHINE_EFFECT_NOT_START;
            this.mThumbHeight = PostviewTimeMachineActivity.TIMEMACHINE_EFFECT_NOT_START;
            this.mLeftMargin = PostviewTimeMachineActivity.TIMEMACHINE_EFFECT_NOT_START;
        }

        public void setThumbnailSizeInfo() {
            int i = PostviewTimeMachineActivity.TIMEMACHINE_EFFECT_NOT_START;
            if (PostviewTimeMachineActivity.this.mPostViewParameters != null && PostviewTimeMachineActivity.this.mOrientationInfo != null) {
                int galleryWidth;
                int imageListSize = PostviewTimeMachineActivity.this.mPostViewParameters.getUriList().size();
                PostviewTimeMachineActivity.this.getWindowManager().getDefaultDisplay().getSize(PostviewTimeMachineActivity.this.outSize);
                if (PostviewTimeMachineActivity.this.mOrientationInfo.getOrientation() == 0 || PostviewTimeMachineActivity.this.mOrientationInfo.getOrientation() == PostviewTimeMachineActivity.TIMEMACHINE_SAVE_NORMAL) {
                    galleryWidth = PostviewTimeMachineActivity.this.outSize.x;
                    this.mThumbWidth = PostviewTimeMachineActivity.this.getThumbnailSize(true)[PostviewTimeMachineActivity.TIMEMACHINE_EFFECT_NOT_START];
                    this.mThumbHeight = PostviewTimeMachineActivity.this.getThumbnailSize(true)[PostviewTimeMachineActivity.TIMEMACHINE_SAVE_EFFECT];
                } else {
                    galleryWidth = PostviewTimeMachineActivity.this.outSize.x;
                    this.mThumbWidth = PostviewTimeMachineActivity.this.getThumbnailSize(false)[PostviewTimeMachineActivity.TIMEMACHINE_EFFECT_NOT_START];
                    this.mThumbHeight = PostviewTimeMachineActivity.this.getThumbnailSize(false)[PostviewTimeMachineActivity.TIMEMACHINE_SAVE_EFFECT];
                }
                this.mLeftMargin = (galleryWidth - (this.mThumbWidth * imageListSize)) / (imageListSize + PostviewTimeMachineActivity.TIMEMACHINE_SAVE_EFFECT);
                if (this.mLeftMargin > 0) {
                    i = this.mLeftMargin;
                }
                this.mLeftMargin = i;
                CamLog.d(FaceDetector.TAG, "moo  mLeftMargin = " + this.mLeftMargin);
            }
        }

        public int getThumbWidth() {
            return this.mThumbWidth;
        }

        public int getThumbHeight() {
            return this.mThumbHeight;
        }

        public int getLeftMargin() {
            return this.mLeftMargin;
        }
    }

    public PostviewTimeMachineActivity() {
        this.mTimemachineMode = TIMEMACHINE_EFFECT_NOT_START;
        this.mSelectedIndexs = new ArrayList();
        this.mFirstTimeMachineDataSize = 0;
        this.outSize = new Point();
        this.mThumbSizeInfo = null;
        this.mThumbList = new ArrayList();
        this.mMakeGalleryImageThread = null;
        this.mCurrentMakingImageIndex = 5;
        this.mOnTimemachineImageClickListener = new OnClickListener() {
            public void onClick(View v) {
                CamLog.d(FaceDetector.TAG, "Time Machine Image selected.");
                try {
                    PostviewTimeMachineActivity.this.selectThumbItem(((Integer) v.getTag()).intValue());
                } catch (Exception e) {
                    CamLog.w(FaceDetector.TAG, "Exception:", e);
                }
            }
        };
        this.saveButtonDone = new Runnable() {
            public void run() {
                PostviewTimeMachineActivity.this.saveSelectedImages(false, true);
                PostviewTimeMachineActivity.this.saveFinished();
            }
        };
        this.isAnimationRunning = false;
        this.mTimerCount = TIMEMACHINE_EFFECT_NOT_START;
        this.mTimeMachineShotCount = TIMEMACHINE_EFFECT_NOT_START;
        this.mAnimationTimer = null;
        this.anim = null;
        this.mScheduledTime = Math.round(5.0f);
        this.mTimeMachineAnimationRunnable = new Runnable() {
            public void run() {
                PostviewTimeMachineActivity.this.removePostRunnable(this);
                if (PostviewTimeMachineActivity.this.isPausing() || PostviewTimeMachineActivity.this.mAnimationTimer == null || PostviewTimeMachineActivity.this.mScheduledTime < Math.round(5.0f)) {
                    PostviewTimeMachineActivity.this.mScheduledTime = PostviewTimeMachineActivity.this.mScheduledTime + PostviewTimeMachineActivity.TIMEMACHINE_SAVE_EFFECT;
                    return;
                }
                try {
                    if (PostviewTimeMachineActivity.this.mTimerCount < 0) {
                        PostviewTimeMachineActivity.this.stopTimeMachineAnimationAndGotoMultiSelectMode(true);
                        PostviewTimeMachineActivity.this.invalidateOptionsMenu();
                    } else if (PostviewTimeMachineActivity.this.mCurrentMakingImageIndex <= 0 || PostviewTimeMachineActivity.this.mCurrentMakingImageIndex < PostviewTimeMachineActivity.this.mTimerCount) {
                        CamLog.d(FaceDetector.TAG, "mTimeMachineAnimationRunnable-mTimerCount : " + PostviewTimeMachineActivity.this.mTimerCount);
                        PostviewTimeMachineActivity.this.mScheduledTime = PostviewTimeMachineActivity.TIMEMACHINE_EFFECT_NOT_START;
                        if (PostviewTimeMachineActivity.this.mCurrentMakingImageIndex == 3) {
                            PostviewTimeMachineActivity.this.timeMachineClockMinuteAnimation(2700);
                            PostviewTimeMachineActivity.this.timeMachineClockSecAnimation(2700);
                        }
                        ImageView frameView = (ImageView) PostviewTimeMachineActivity.this.findViewById(R.id.captured_ani_view);
                        ImageView frameBackView = (ImageView) PostviewTimeMachineActivity.this.findViewById(R.id.captured_image);
                        if (frameView == null || frameBackView == null) {
                            PostviewTimeMachineActivity.this.stopTimeMachineAnimationAndGotoMultiSelectMode(false);
                            return;
                        }
                        BitmapDrawable bmpD2 = null;
                        if (PostviewTimeMachineActivity.this.mThumbList != null) {
                            PostviewTimeMachineActivity.this.setThumbListVisible(false, PostviewTimeMachineActivity.this.mTimerCount, PostviewTimeMachineActivity.TIMEMACHINE_EFFECT_NOT_START);
                            BitmapDrawable bmpD1 = (BitmapDrawable) PostviewTimeMachineActivity.this.mThumbList.get(PostviewTimeMachineActivity.this.mTimerCount);
                            if (PostviewTimeMachineActivity.this.mTimerCount >= PostviewTimeMachineActivity.TIMEMACHINE_SAVE_EFFECT) {
                                bmpD2 = (BitmapDrawable) PostviewTimeMachineActivity.this.mThumbList.get(PostviewTimeMachineActivity.this.mTimerCount - 1);
                            }
                            frameBackView.setVisibility(PostviewTimeMachineActivity.TIMEMACHINE_EFFECT_NOT_START);
                            frameBackView.setImageDrawable(bmpD2);
                            frameView.setVisibility(PostviewTimeMachineActivity.TIMEMACHINE_EFFECT_NOT_START);
                            frameView.setImageDrawable(bmpD1);
                            frameView.clearAnimation();
                            if (PostviewTimeMachineActivity.this.mTimerCount >= PostviewTimeMachineActivity.TIMEMACHINE_SAVE_EFFECT) {
                                frameView.startAnimation(PostviewTimeMachineActivity.this.anim);
                                CamLog.d(FaceDetector.TAG, "mTimeMachineAnimationRunnable-startAnimation : " + PostviewTimeMachineActivity.this.mTimerCount);
                            }
                            PostviewTimeMachineActivity.this.mTimerCount = PostviewTimeMachineActivity.this.mTimerCount - 1;
                        }
                    } else {
                        PostviewTimeMachineActivity.this.mScheduledTime = PostviewTimeMachineActivity.this.mScheduledTime + PostviewTimeMachineActivity.TIMEMACHINE_SAVE_EFFECT;
                    }
                } catch (Exception e) {
                    CamLog.e(FaceDetector.TAG, "Exception!", e);
                    PostviewTimeMachineActivity.this.stopTimeMachineAnimationAndGotoMultiSelectMode(false);
                }
            }
        };
        this.isGalleryLaunchingState = TIMEMACHINE_EFFECT_NOT_START;
    }

    protected void doPreProcessOnCreate() {
    }

    protected void doProcessOnCreate() {
        this.isFromCreateProcess = true;
        this.mFirstTimeMachineDataSize = new File(((Uri) this.mPostViewParameters.getUriList().get(TIMEMACHINE_EFFECT_NOT_START)).getPath()).lastModified();
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
        if (checkValidateTimeMachineImage() && checkValidateOneShotImage()) {
            if (this.isFromCreateProcess) {
                View imageView = findViewById(R.id.captured_image);
                if (imageView != null) {
                    imageView.setVisibility(4);
                }
                setThumbListVisible(true, TIMEMACHINE_EFFECT_NOT_START, 4);
                postOnUiThread(new Runnable() {
                    public void run() {
                        PostviewTimeMachineActivity.this.removePostRunnable(this);
                        PostviewTimeMachineActivity.this.startTimeMachineShotAnimation();
                    }
                }, 100);
            }
            if (checkTimeMachineFileOverwritten()) {
                CamLog.d(FaceDetector.TAG, "File over written! need to reload.");
                this.mTimemachineMode = TIMEMACHINE_SAVE_NORMAL;
                this.mCurrentMakingImageIndex = 5;
                if (this.mThumbList != null && this.mThumbList.size() > 0) {
                    this.mThumbList.clear();
                }
            }
            if (this.mTimemachineMode == TIMEMACHINE_SAVE_NORMAL) {
                if (this.mCurrentMakingImageIndex != 0) {
                    makeTimeMachineGalleryImages();
                }
                reloadTimemachineGalleryLayout();
                refreshLoadCapturedImages(this.mCurrentSelectedIndex);
                stopTimeMachineAnimationAndGotoMultiSelectMode(true);
            }
            if (this.isFromCreateProcess) {
                this.mCurrentSelectedIndex = 4;
                GalleryThumbnailLayout galleryThumb = (GalleryThumbnailLayout) findViewById(R.id.timemachine_gallery_images).findViewWithTag(Integer.valueOf(this.mCurrentSelectedIndex));
                galleryThumb.setSelected(true);
                galleryThumb.setChecked();
                this.mSelectedIndexs.add(Integer.valueOf(this.mCurrentSelectedIndex));
                refreshLoadCapturedImages(this.mCurrentSelectedIndex);
            }
            this.isFromCreateProcess = false;
            this.isGalleryLaunchingState = TIMEMACHINE_EFFECT_NOT_START;
            return;
        }
        postOnUiThread(this.mExitInteraction);
        this.isFromCreateProcess = false;
    }

    protected void doProcessOnPause() {
        this.isAnimationRunning = false;
        if (this.mMakeGalleryImageThread != null) {
            try {
                this.mMakeGalleryImageThread.join();
            } catch (InterruptedException e) {
                CamLog.d(FaceDetector.TAG, "InterruptedException: ", e);
            }
            this.mMakeGalleryImageThread = null;
        }
        stopTimeMachineAnimationAndGotoMultiSelectMode(true);
        this.mTimemachineMode = TIMEMACHINE_SAVE_NORMAL;
        FileNamer.get().close(getApplicationContext(), this.mPostViewParameters.getCurrentStorage());
    }

    protected void doProcessOnDestroy() {
        int i;
        View timeMachineGalleryImage = findViewById(R.id.timemachine_gallery_images);
        if (this.mPostViewParameters != null) {
            int size = this.mPostViewParameters.getUriList().size();
            for (i = TIMEMACHINE_EFFECT_NOT_START; i < size; i += TIMEMACHINE_SAVE_EFFECT) {
                GalleryThumbnailLayout galleryThumb = (GalleryThumbnailLayout) findViewById(R.id.timemachine_gallery_images).findViewWithTag(Integer.valueOf(i));
                if (galleryThumb != null) {
                    galleryThumb.unbind();
                }
            }
        }
        if (timeMachineGalleryImage != null) {
            ((RelativeLayout) timeMachineGalleryImage).removeAllViews();
        }
        if (this.mThumbList != null) {
            int imageListSize = this.mThumbList == null ? TIMEMACHINE_EFFECT_NOT_START : this.mThumbList.size();
            for (i = TIMEMACHINE_EFFECT_NOT_START; i < imageListSize; i += TIMEMACHINE_SAVE_EFFECT) {
                Util.recycleBitmapDrawable((Drawable) this.mThumbList.get(i));
            }
            this.mThumbList.clear();
            this.mThumbList = null;
        }
        if (this.mSelectedIndexs != null) {
            this.mSelectedIndexs.clear();
            this.mSelectedIndexs = null;
        }
        this.mThumbSizeInfo = null;
        this.mTimemachineMode = TIMEMACHINE_SAVE_NORMAL;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        CamLog.d(FaceDetector.TAG, "onConfigurationChanged : newConfig = " + newConfig.orientation);
        if (this.mOrientationInfo != null) {
            this.mOrientationInfo.setOrientationByWindowOrientation();
        }
        if (this.mTimemachineMode != 0) {
            stopTimeMachineAnimationAndGotoMultiSelectMode(true);
        }
        reloadTimemachineGalleryLayout();
        refreshLoadCapturedImages(this.mCurrentSelectedIndex);
        super.onConfigurationChanged(newConfig);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 16908332:
                doBackKeyInPostview();
                break;
            case R.id.postview_save_complete /*2131559009*/:
                if (this.mTimemachineMode == TIMEMACHINE_SAVE_EFFECT) {
                    stopTimeMachineAnimationAndGotoMultiSelectMode(true);
                } else {
                    clickTimeMachineSave();
                }
                invalidateOptionsMenu();
                break;
            case R.id.postview_delete /*2131559012*/:
                stopTimeMachineAnimationAndGotoMultiSelectMode(true);
                onCreateDialog(3, this.mPostViewParameters.getApplicationMode());
                break;
        }
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        switch (this.mTimemachineMode) {
            case TIMEMACHINE_EFFECT_NOT_START /*0*/:
            case TIMEMACHINE_SAVE_EFFECT /*1*/:
            case TIMEMACHINE_SAVE_NORMAL /*2*/:
                if (menu.findItem(R.id.postview_save_complete) == null) {
                    if (ModelProperties.getCarrierCode() != 6) {
                        getMenuInflater().inflate(R.menu.shot_postview_action_menu_complete, menu);
                        break;
                    }
                    getMenuInflater().inflate(R.menu.shot_postview_action_menu_complete_vzw, menu);
                    break;
                }
                break;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    protected void setActionBar() {
        int stringId;
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        if (FunctionProperties.useTimeCatchShotTitle()) {
            stringId = R.string.sp_shot_mode_time_catch_NORMAL;
        } else {
            stringId = R.string.sp_shot_mode_time_machine_NORMAL;
        }
        actionBar.setTitle(stringId);
    }

    protected void setupLayout() {
        inflateStub(R.id.stub_time_machine_postview);
        setGalleryLayout();
    }

    protected void postviewShow() {
        CamLog.d(FaceDetector.TAG, "TIME_CHECK show()");
        View postView = findViewById(R.id.postview_shotmode_timemachine);
        if (postView == null) {
            CamLog.w(FaceDetector.TAG, "postviewShow : inflate view fail.");
            return;
        }
        if (postView.getVisibility() != 0) {
            postView.setVisibility(TIMEMACHINE_EFFECT_NOT_START);
        }
        this.mThumbSizeInfo = new ThumbnailSizeInfo();
        this.mThumbSizeInfo.setThumbnailSizeInfo();
        makeTimemachineGalleryLayout();
        makeTimeMachineGalleryImages();
    }

    protected void reloadedPostview() {
        if (this.mCapturedBitmap != null) {
            ((ImageView) findViewById(R.id.captured_image)).setImageBitmap(this.mCapturedBitmap);
        }
    }

    private void setGalleryLayout() {
        View timeMachineGalleryScroll = findViewById(R.id.timemachine_gallery_scroll);
        if (timeMachineGalleryScroll != null && this.mOrientationInfo != null) {
            LayoutParams galleryParams = (LayoutParams) timeMachineGalleryScroll.getLayoutParams();
            getWindowManager().getDefaultDisplay().getSize(this.outSize);
            galleryParams.width = this.outSize.x;
            timeMachineGalleryScroll.setLayoutParams(galleryParams);
        }
    }

    private void makeTimeMachineGalleryImages() {
        if (this.mPostViewParameters == null) {
            CamLog.w(FaceDetector.TAG, "Postview : postview parameters get fail.");
            return;
        }
        final int imageListSize = this.mPostViewParameters.getUriList().size();
        if (this.mThumbList != null && this.mThumbList.size() == 0) {
            Bitmap tempBmp = BitmapFactory.decodeResource(getResources(), R.drawable.temp);
            for (int index = TIMEMACHINE_EFFECT_NOT_START; index < imageListSize; index += TIMEMACHINE_SAVE_EFFECT) {
                this.mThumbList.add(new BitmapDrawable(getResources(), tempBmp));
            }
        }
        this.mCurrentSelectedIndex = 4;
        this.mMakeGalleryImageThread = new Thread(new Runnable() {
            /* JADX WARNING: inconsistent code. */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                /*
                r16 = this;
                r10 = 0;
                r8 = 0;
                r4 = 0;
                r12 = 2;
                r5 = new int[r12];
                r9 = 0;
                r1 = 0;
                r2 = 0;
                r0 = r16;
                r12 = r0;
                r11 = r12 + -1;
                r0 = r16;
                r12 = com.lge.camera.PostviewTimeMachineActivity.this;
                r13 = 5;
                r12.mCurrentMakingImageIndex = r13;
                r7 = r11;
                r3 = r2;
            L_0x0019:
                if (r7 < 0) goto L_0x013e;
            L_0x001b:
                r0 = r16;
                r12 = com.lge.camera.PostviewTimeMachineActivity.this;	 Catch:{ Exception -> 0x0141 }
                r12 = r12.mPostViewParameters;	 Catch:{ Exception -> 0x0141 }
                if (r12 == 0) goto L_0x003b;
            L_0x0023:
                r0 = r16;
                r12 = com.lge.camera.PostviewTimeMachineActivity.this;	 Catch:{ Exception -> 0x0141 }
                r12 = r12.mMakeGalleryImageThread;	 Catch:{ Exception -> 0x0141 }
                if (r12 == 0) goto L_0x003b;
            L_0x002d:
                r0 = r16;
                r12 = com.lge.camera.PostviewTimeMachineActivity.this;	 Catch:{ Exception -> 0x0141 }
                r12 = r12.mMakeGalleryImageThread;	 Catch:{ Exception -> 0x0141 }
                r12 = r12.isInterrupted();	 Catch:{ Exception -> 0x0141 }
                if (r12 == 0) goto L_0x005d;
            L_0x003b:
                r12 = "CameraApp";
                r13 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0141 }
                r13.<init>();	 Catch:{ Exception -> 0x0141 }
                r14 = "mMakeGalleryImageThread interrupted-mCurrentMakingImageIndex : ";
                r13 = r13.append(r14);	 Catch:{ Exception -> 0x0141 }
                r0 = r16;
                r14 = com.lge.camera.PostviewTimeMachineActivity.this;	 Catch:{ Exception -> 0x0141 }
                r14 = r14.mCurrentMakingImageIndex;	 Catch:{ Exception -> 0x0141 }
                r13 = r13.append(r14);	 Catch:{ Exception -> 0x0141 }
                r13 = r13.toString();	 Catch:{ Exception -> 0x0141 }
                com.lge.camera.util.CamLog.d(r12, r13);	 Catch:{ Exception -> 0x0141 }
                r2 = r3;
            L_0x005c:
                return;
            L_0x005d:
                r0 = r16;
                r12 = com.lge.camera.PostviewTimeMachineActivity.this;	 Catch:{ Exception -> 0x0141 }
                r12 = r12.mPostViewParameters;	 Catch:{ Exception -> 0x0141 }
                r12 = r12.getUriList();	 Catch:{ Exception -> 0x0141 }
                r12 = r12.get(r7);	 Catch:{ Exception -> 0x0141 }
                r0 = r12;
                r0 = (android.net.Uri) r0;	 Catch:{ Exception -> 0x0141 }
                r9 = r0;
                r12 = r9.getPath();	 Catch:{ Exception -> 0x0141 }
                r4 = com.lge.camera.util.ExifUtil.getExifOrientationDegree(r12);	 Catch:{ Exception -> 0x0141 }
                r12 = r9.getPath();	 Catch:{ Exception -> 0x0141 }
                r10 = com.lge.camera.util.ExifUtil.getExifWidth(r12);	 Catch:{ Exception -> 0x0141 }
                r12 = r9.getPath();	 Catch:{ Exception -> 0x0141 }
                r8 = com.lge.camera.util.ExifUtil.getExifHeight(r12);	 Catch:{ Exception -> 0x0141 }
                r0 = r16;
                r12 = com.lge.camera.PostviewTimeMachineActivity.this;	 Catch:{ Exception -> 0x0141 }
                r12 = r12.getActivity();	 Catch:{ Exception -> 0x0141 }
                r5 = com.lge.camera.util.Util.getFitSizeOfBitmapForLCD(r12, r10, r8);	 Catch:{ Exception -> 0x0141 }
                r0 = r16;
                r12 = com.lge.camera.PostviewTimeMachineActivity.this;	 Catch:{ Exception -> 0x0141 }
                r12 = r12.getContentResolver();	 Catch:{ Exception -> 0x0141 }
                r13 = r9.toString();	 Catch:{ Exception -> 0x0141 }
                r14 = 0;
                r14 = r5[r14];	 Catch:{ Exception -> 0x0141 }
                r15 = 1;
                r15 = r5[r15];	 Catch:{ Exception -> 0x0141 }
                r1 = com.lge.camera.util.ImageManager.loadScaledBitmap(r12, r13, r14, r15);	 Catch:{ Exception -> 0x0141 }
                r2 = new android.graphics.drawable.BitmapDrawable;	 Catch:{ Exception -> 0x0141 }
                r0 = r16;
                r12 = com.lge.camera.PostviewTimeMachineActivity.this;	 Catch:{ Exception -> 0x0141 }
                r12 = r12.getResources();	 Catch:{ Exception -> 0x0141 }
                r0 = r16;
                r13 = com.lge.camera.PostviewTimeMachineActivity.this;	 Catch:{ Exception -> 0x0141 }
                r13 = r13.mImageHandler;	 Catch:{ Exception -> 0x0141 }
                r14 = 0;
                r13 = r13.getImage(r1, r4, r14);	 Catch:{ Exception -> 0x0141 }
                r2.<init>(r12, r13);	 Catch:{ Exception -> 0x0141 }
                r0 = r16;
                r12 = com.lge.camera.PostviewTimeMachineActivity.this;	 Catch:{ Exception -> 0x014c }
                r12 = r12.mThumbList;	 Catch:{ Exception -> 0x014c }
                if (r12 == 0) goto L_0x0139;
            L_0x00cb:
                r0 = r16;
                r12 = com.lge.camera.PostviewTimeMachineActivity.this;	 Catch:{ Exception -> 0x014c }
                r12 = r12.mThumbList;	 Catch:{ Exception -> 0x014c }
                r12 = r12.size();	 Catch:{ Exception -> 0x014c }
                if (r12 <= r7) goto L_0x0139;
            L_0x00d9:
                r0 = r16;
                r12 = com.lge.camera.PostviewTimeMachineActivity.this;	 Catch:{ Exception -> 0x014c }
                r12 = r12.mThumbList;	 Catch:{ Exception -> 0x014c }
                r12.remove(r7);	 Catch:{ Exception -> 0x014c }
                r0 = r16;
                r12 = com.lge.camera.PostviewTimeMachineActivity.this;	 Catch:{ Exception -> 0x014c }
                r12 = r12.mThumbList;	 Catch:{ Exception -> 0x014c }
                r12.add(r7, r2);	 Catch:{ Exception -> 0x014c }
                r0 = r16;
                r12 = com.lge.camera.PostviewTimeMachineActivity.this;	 Catch:{ Exception -> 0x014c }
                r12.changeThumbnailImages(r2, r7);	 Catch:{ Exception -> 0x014c }
                if (r7 == r11) goto L_0x0104;
            L_0x00f8:
                if (r7 != 0) goto L_0x0112;
            L_0x00fa:
                r0 = r16;
                r12 = com.lge.camera.PostviewTimeMachineActivity.this;	 Catch:{ Exception -> 0x014c }
                r12 = r12.mAnimationTimer;	 Catch:{ Exception -> 0x014c }
                if (r12 != 0) goto L_0x0112;
            L_0x0104:
                r0 = r16;
                r12 = com.lge.camera.PostviewTimeMachineActivity.this;	 Catch:{ Exception -> 0x014c }
                r13 = new com.lge.camera.PostviewTimeMachineActivity$2$1;	 Catch:{ Exception -> 0x014c }
                r0 = r16;
                r13.<init>(r11);	 Catch:{ Exception -> 0x014c }
                r12.runOnUiThread(r13);	 Catch:{ Exception -> 0x014c }
            L_0x0112:
                r0 = r16;
                r12 = com.lge.camera.PostviewTimeMachineActivity.this;	 Catch:{ Exception -> 0x014c }
                r12.mCurrentMakingImageIndex = r12.mCurrentMakingImageIndex - 1;	 Catch:{ Exception -> 0x014c }
                r12 = "CameraApp";
                r13 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x014c }
                r13.<init>();	 Catch:{ Exception -> 0x014c }
                r14 = "mMakeGalleryImageThread-mCurrentMakingImageIndex: ";
                r13 = r13.append(r14);	 Catch:{ Exception -> 0x014c }
                r0 = r16;
                r14 = com.lge.camera.PostviewTimeMachineActivity.this;	 Catch:{ Exception -> 0x014c }
                r14 = r14.mCurrentMakingImageIndex;	 Catch:{ Exception -> 0x014c }
                r13 = r13.append(r14);	 Catch:{ Exception -> 0x014c }
                r13 = r13.toString();	 Catch:{ Exception -> 0x014c }
                com.lge.camera.util.CamLog.d(r12, r13);	 Catch:{ Exception -> 0x014c }
            L_0x0139:
                r7 = r7 + -1;
                r3 = r2;
                goto L_0x0019;
            L_0x013e:
                r2 = r3;
                goto L_0x005c;
            L_0x0141:
                r6 = move-exception;
                r2 = r3;
            L_0x0143:
                r12 = "CameraApp";
                r13 = "mMakeGalleryImageThread-Exception: ";
                com.lge.camera.util.CamLog.e(r12, r13, r6);
                goto L_0x005c;
            L_0x014c:
                r6 = move-exception;
                goto L_0x0143;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.lge.camera.PostviewTimeMachineActivity.2.run():void");
            }
        });
        this.mMakeGalleryImageThread.start();
    }

    private void changeThumbnailImages(final BitmapDrawable bmpD, final int index) {
        runOnUiThread(new Runnable() {
            public void run() {
                PostviewTimeMachineActivity.this.removePostRunnable(this);
                View timeMachineGalleryImage = PostviewTimeMachineActivity.this.findViewById(R.id.timemachine_gallery_images);
                if (timeMachineGalleryImage != null) {
                    GalleryThumbnailLayout galleryThumb = (GalleryThumbnailLayout) timeMachineGalleryImage.findViewWithTag(Integer.valueOf(index));
                    if (galleryThumb != null && PostviewTimeMachineActivity.this.mThumbSizeInfo != null) {
                        galleryThumb.setThumbBitmap(bmpD, PostviewTimeMachineActivity.this.mThumbSizeInfo.getThumbWidth(), PostviewTimeMachineActivity.this.mThumbSizeInfo.getThumbHeight());
                        ((HorizontalScrollView) PostviewTimeMachineActivity.this.findViewById(R.id.timemachine_gallery_scroll)).fullScroll(66);
                    }
                }
            }
        });
    }

    private void reloadTimemachineGalleryLayout() {
        CamLog.d(FaceDetector.TAG, "reloadTimemachineGalleryLayout.");
        if (this.mPostViewParameters == null) {
            CamLog.w(FaceDetector.TAG, "Postview : postview parameters get fail.");
            return;
        }
        View timeMachineGalleryImage = findViewById(R.id.timemachine_gallery_images);
        if (timeMachineGalleryImage != null && this.mThumbSizeInfo != null) {
            int imageListSize = this.mPostViewParameters.getUriList().size();
            this.mThumbSizeInfo.setThumbnailSizeInfo();
            setGalleryLayout();
            if (this.mThumbList != null) {
                for (int i = TIMEMACHINE_EFFECT_NOT_START; i < imageListSize; i += TIMEMACHINE_SAVE_EFFECT) {
                    GalleryThumbnailLayout galleryThumb = (GalleryThumbnailLayout) timeMachineGalleryImage.findViewWithTag(Integer.valueOf(i));
                    if (galleryThumb != null) {
                        galleryThumb.setThumbSize(this.mThumbSizeInfo.getThumbWidth(), this.mThumbSizeInfo.getThumbHeight(), (this.mThumbSizeInfo.getThumbWidth() * i) + ((i + TIMEMACHINE_SAVE_EFFECT) * this.mThumbSizeInfo.getLeftMargin()));
                        galleryThumb.setThumbBitmap((BitmapDrawable) this.mThumbList.get(i), this.mThumbSizeInfo.getThumbWidth(), this.mThumbSizeInfo.getThumbHeight());
                        galleryThumb.setVisibility(TIMEMACHINE_EFFECT_NOT_START);
                    }
                }
            }
        }
    }

    private void makeTimemachineGalleryLayout() {
        if (this.mPostViewParameters == null) {
            CamLog.w(FaceDetector.TAG, "Postview : postview parameters get fail.");
        } else if (findViewById(R.id.timemachine_gallery_images) != null) {
            int imageListSize = this.mPostViewParameters.getUriList().size();
            for (int i = TIMEMACHINE_EFFECT_NOT_START; i < imageListSize; i += TIMEMACHINE_SAVE_EFFECT) {
                addTimemachineImageView(null, i, this.mThumbSizeInfo.getThumbWidth(), this.mThumbSizeInfo.getThumbHeight(), this.mThumbSizeInfo.getLeftMargin());
            }
        }
    }

    private void addTimemachineImageView(BitmapDrawable bmpD, int index, int thumbWidth, int thumbHeight, int leftMargin) {
        GalleryThumbnailLayout galThumb = new GalleryThumbnailLayout(getApplicationContext(), index, bmpD, thumbWidth, thumbHeight, true);
        galThumb.setOnClickListener(this.mOnTimemachineImageClickListener);
        LayoutParams param = new LayoutParams(thumbWidth, thumbHeight);
        param.leftMargin = (index * thumbWidth) + ((index + TIMEMACHINE_SAVE_EFFECT) * leftMargin);
        if (index == this.mCurrentSelectedIndex) {
            galThumb.setSelected(true);
        }
        View timeMachineGalleryImage = findViewById(R.id.timemachine_gallery_images);
        if (timeMachineGalleryImage != null) {
            ((RelativeLayout) timeMachineGalleryImage).addView(galThumb, param);
        }
    }

    private boolean refreshLoadCapturedImages(int selectedIndex) {
        if (this.mPostViewParameters == null) {
            CamLog.w(FaceDetector.TAG, "TMC Postview : postview parameters get fail.");
            return false;
        }
        int listSize = this.mPostViewParameters.getUriList().size();
        CamLog.d(FaceDetector.TAG, "TMC refreshLoadCapturedImages : listSize = " + listSize);
        if (listSize > 0) {
            ImageView postview = (ImageView) findViewById(R.id.captured_image);
            if (postview != null) {
                try {
                    if (this.mThumbList != null && this.mThumbList.size() > selectedIndex) {
                        postview.setImageDrawable((BitmapDrawable) this.mThumbList.get(selectedIndex));
                    }
                    postview.setVisibility(TIMEMACHINE_EFFECT_NOT_START);
                } catch (Exception e) {
                    CamLog.e(FaceDetector.TAG, "TMC setCapturedImageView Exception!", e);
                }
            }
        }
        return true;
    }

    private void selectThumbItem(int selectIndex) {
        int size = this.mPostViewParameters.getUriList().size();
        for (int i = TIMEMACHINE_EFFECT_NOT_START; i < size; i += TIMEMACHINE_SAVE_EFFECT) {
            GalleryThumbnailLayout galleryThumb = (GalleryThumbnailLayout) findViewById(R.id.timemachine_gallery_images).findViewWithTag(Integer.valueOf(i));
            if (galleryThumb != null) {
                if (selectIndex == i) {
                    this.mCurrentSelectedIndex = selectIndex;
                    galleryThumb.setSelected(true);
                    if (this.mTimemachineMode == TIMEMACHINE_SAVE_NORMAL) {
                        galleryThumb.setChecked();
                        if (this.mSelectedIndexs.contains(Integer.valueOf(i))) {
                            this.mSelectedIndexs.remove(Integer.valueOf(i));
                        } else {
                            this.mSelectedIndexs.add(Integer.valueOf(i));
                        }
                    }
                } else {
                    galleryThumb.setSelected(false);
                }
            }
        }
        refreshLoadCapturedImages(selectIndex);
    }

    private void showCheckBox(boolean show) {
        int size = this.mPostViewParameters.getUriList().size();
        for (int i = TIMEMACHINE_EFFECT_NOT_START; i < size; i += TIMEMACHINE_SAVE_EFFECT) {
            GalleryThumbnailLayout galleryThumb = (GalleryThumbnailLayout) findViewById(R.id.timemachine_gallery_images).findViewWithTag(Integer.valueOf(i));
            if (galleryThumb != null) {
                galleryThumb.showCheckbox(show);
                if (this.mSelectedIndexs.contains(Integer.valueOf(i))) {
                    galleryThumb.setChecked(true);
                }
            }
        }
    }

    private int getCountThumbnailSelected() {
        int nCount = TIMEMACHINE_EFFECT_NOT_START;
        int size = this.mPostViewParameters.getUriList().size();
        for (int i = TIMEMACHINE_EFFECT_NOT_START; i < size; i += TIMEMACHINE_SAVE_EFFECT) {
            GalleryThumbnailLayout galleryThumb = (GalleryThumbnailLayout) findViewById(R.id.timemachine_gallery_images).findViewWithTag(Integer.valueOf(i));
            if (galleryThumb != null && galleryThumb.getChecked()) {
                nCount += TIMEMACHINE_SAVE_EFFECT;
            }
        }
        return nCount;
    }

    private void setThumbListVisible(boolean all, int index, int visible) {
        int size = this.mPostViewParameters.getUriList().size();
        for (int i = TIMEMACHINE_EFFECT_NOT_START; i < size; i += TIMEMACHINE_SAVE_EFFECT) {
            GalleryThumbnailLayout galleryThumb = (GalleryThumbnailLayout) findViewById(R.id.timemachine_gallery_images).findViewWithTag(Integer.valueOf(i));
            if (galleryThumb != null) {
                if (all) {
                    galleryThumb.setVisibility(visible);
                    galleryThumb.clearAnimation();
                    if (visible == 0) {
                        if (index == i) {
                            galleryThumb.setSelected(true);
                        } else {
                            galleryThumb.setSelected(false);
                        }
                    }
                } else if (index == i) {
                    this.mCurrentSelectedIndex = index;
                    galleryThumb.setSelected(true);
                    timeMachineThumbAnimation(galleryThumb);
                } else {
                    galleryThumb.setSelected(false);
                }
            }
        }
    }

    protected void onCreateDialog(int dialogId, int applicationMode) {
        if (dialogId == 6) {
            SharedPreferences pref = getActivity().getSharedPreferences(Setting.SETTING_PRIMARY, TIMEMACHINE_EFFECT_NOT_START);
            if (pref != null && pref.getBoolean(CameraConstants.TIME_MACHINE_WARNING_AGAIN, false)) {
                if (this.mMakeGalleryImageThread != null && this.mMakeGalleryImageThread.isAlive()) {
                    this.mMakeGalleryImageThread.interrupt();
                }
                if (this.mPostViewParameters.getUriList().size() <= TIMEMACHINE_SAVE_EFFECT) {
                    return;
                }
                if (this.isGalleryLaunchingState == TIMEMACHINE_SAVE_EFFECT) {
                    saveSelectedImages(true, false);
                    doGalleryLaunching();
                    return;
                }
                startTimeMachineFinishAnimaion();
                saveSelectedImages(true, false);
                return;
            }
        }
        PostviewDialog mDialog = PostviewDialog.getPostviewDialog(dialogId, applicationMode);
        Fragment dialogFragment = getFragmentManager().findFragmentByTag(CameraConstants.TAG_DIALOG_POSTVIEW);
        if (dialogFragment == null || !(dialogFragment == null || dialogFragment.isAdded())) {
            mDialog.show(getFragmentManager(), CameraConstants.TAG_DIALOG_POSTVIEW);
        }
    }

    public void doTimeMachineWarningPositiveClick(CheckBox checkBox) {
        try {
            if (checkBox.isChecked()) {
                Editor edit = getActivity().getSharedPreferences(Setting.SETTING_PRIMARY, TIMEMACHINE_EFFECT_NOT_START).edit();
                edit.putBoolean(CameraConstants.TIME_MACHINE_WARNING_AGAIN, true);
                edit.apply();
            }
            if (this.mPostViewParameters.getUriList().size() <= TIMEMACHINE_SAVE_EFFECT) {
                return;
            }
            if (this.isGalleryLaunchingState == TIMEMACHINE_SAVE_EFFECT) {
                saveSelectedImages(true, false);
                doGalleryLaunching();
                return;
            }
            saveSelectedImages(true, false);
            startTimeMachineFinishAnimaion();
        } catch (Exception e) {
            CamLog.e(FaceDetector.TAG, "Exception!", e);
        }
    }

    public void doTimeMachineWarningNegativeClick(CheckBox checkBox) {
        try {
            if (checkBox.isChecked()) {
                Editor edit = getActivity().getSharedPreferences(Setting.SETTING_PRIMARY, TIMEMACHINE_EFFECT_NOT_START).edit();
                edit.putBoolean(CameraConstants.TIME_MACHINE_WARNING_AGAIN, true);
                edit.apply();
            }
            doTimeMachineWarningDismiss();
        } catch (Exception e) {
            CamLog.e(FaceDetector.TAG, "Exception!", e);
        }
    }

    public void doTimeMachineWarningDismiss() {
        if (this.isGalleryLaunchingState == TIMEMACHINE_SAVE_EFFECT) {
            this.isGalleryLaunchingState = TIMEMACHINE_EFFECT_NOT_START;
        }
    }

    private int saveSelectedImages(boolean deleteAll, boolean deleteOriginalShotFile) {
        CamLog.d(FaceDetector.TAG, "saveSelectedImages()");
        if (this.mMakeGalleryImageThread != null && this.mMakeGalleryImageThread.isAlive()) {
            this.mMakeGalleryImageThread.interrupt();
            try {
                this.mMakeGalleryImageThread.join();
            } catch (InterruptedException e) {
                CamLog.i(FaceDetector.TAG, "InterruptedException : ", e);
            }
            this.mMakeGalleryImageThread = null;
        }
        try {
            for (int index = this.mPostViewParameters.getUriList().size() - 1; index >= 0; index--) {
                if (deleteAll) {
                    deleteSelectedImage(index);
                    this.mSelectedIndexs.remove(Integer.valueOf(index));
                } else if (this.mSelectedIndexs.contains(Integer.valueOf(index))) {
                    this.mSelectedIndexs.remove(Integer.valueOf(index));
                } else {
                    CamLog.d(FaceDetector.TAG, "TMC delete index = " + index);
                    deleteSelectedImage(index);
                }
            }
            if (deleteOriginalShotFile) {
                deleteOriginalShotFile();
            }
            if (!deleteAll) {
                renameForTimeMachineShot();
            }
        } catch (Exception e2) {
            CamLog.e(FaceDetector.TAG, "ArrayIndexOutOfBoundsException!", e2);
            finish();
        }
        return this.mPostViewParameters.getUriList().size();
    }

    private void renameForTimeMachineShot() {
        CamLog.d(FaceDetector.TAG, "renameForTimeMachineShot-start");
        FileNamer.get().markTakeTime(CameraConstants.TYPE_SHOTMODE_TIMEMACHINE, getSettingValue(Setting.KEY_SCENE_MODE));
        if (this.mPostViewParameters != null) {
            try {
                String fileExt = CameraConstants.TIME_MACHINE_TEMPFILE_EXT;
                int size = this.mPostViewParameters.getUriList().size();
                for (int index = TIMEMACHINE_EFFECT_NOT_START; index < size; index += TIMEMACHINE_SAVE_EFFECT) {
                    String newFileName;
                    String tempFilePath = ((Uri) this.mPostViewParameters.getUriList().get(index)).getPath();
                    int degree = ExifUtil.getExifOrientationDegree(tempFilePath);
                    File file = new File(tempFilePath);
                    if (index == 0) {
                        newFileName = this.mPostViewParameters.getSaveFileName();
                        if (ProjectVariables.isUseNewNamingRule()) {
                            newFileName = FileNamer.get().getFileNewName(getApplicationContext(), this.mPostViewParameters.getApplicationMode(), this.mPostViewParameters.getCurrentStorage(), this.mPostViewParameters.getCurrentStorageDirectory(), false, CameraConstants.TYPE_SHOTMODE_TIMEMACHINE, false);
                        }
                    } else {
                        newFileName = FileNamer.get().getFileNewName(getApplicationContext(), this.mPostViewParameters.getApplicationMode(), this.mPostViewParameters.getCurrentStorage(), this.mPostViewParameters.getCurrentStorageDirectory(), false, CameraConstants.TYPE_SHOTMODE_TIMEMACHINE, false);
                    }
                    CamLog.d(FaceDetector.TAG, "newFileName = " + newFileName);
                    String newFileDir = this.mPostViewParameters.getCurrentStorageDirectory();
                    String newFilePath = newFileDir + newFileName + fileExt;
                    File newFile = new File(newFilePath);
                    CamLog.d(FaceDetector.TAG, "Rename TMS tempFilePath = " + tempFilePath);
                    CamLog.d(FaceDetector.TAG, "Rename TMS newFilePath = " + newFilePath);
                    File dir = new File(newFileDir);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    if (!(newFileName == null || !file.exists() || newFile.exists() || newFileName.trim().length() == 0 || !file.renameTo(newFile))) {
                        Uri resultUri = ImageManager.insertToContentResolver(getContentResolver(), newFileName, System.currentTimeMillis(), this.mPostViewParameters.getLocationLatitude(), this.mPostViewParameters.getLocationLongitude(), newFileDir, newFileName + fileExt, degree, getSettingValue(Setting.KEY_CAMERA_SHOT_MODE).equals(CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS));
                        Util.broadcastNewPicture(getActivity(), resultUri);
                        SharedPreferenceUtil.saveLastPicture(getActivity(), resultUri);
                        Util.requestUpBoxBackupPhoto(getActivity(), newFileName, getSettingValue(Setting.KEY_UPLUS_BOX).equals(CameraConstants.SMART_MODE_ON));
                        setSecureImageList(resultUri, true);
                    }
                }
            } catch (Exception e) {
                CamLog.e(FaceDetector.TAG, "Exception!", e);
                finish();
            }
        }
        CamLog.d(FaceDetector.TAG, "renameForTimeMachineShot-end");
    }

    private void deleteOriginalShotFile() {
        String oneShotSaveDir = this.mPostViewParameters.getCurrentStorageDirectory();
        File oneShotFile = new File(oneShotSaveDir + this.mPostViewParameters.getSaveFileName() + CameraConstants.TIME_MACHINE_TEMPFILE_EXT);
        File dir = new File(oneShotSaveDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (oneShotFile.exists() && oneShotFile.delete()) {
            ImageManager.deleteImage(getContentResolver(), this.mPostViewParameters.getSavedUri());
            setSecureImageList(this.mPostViewParameters.getSavedUri(), false);
        }
    }

    private void deleteSelectedImage(int index) {
        String title = CameraConstants.TIME_MACHINE_TEMPFILE + Integer.toString(index + TIMEMACHINE_SAVE_EFFECT);
        if (title != null && this.mThumbList != null) {
            try {
                deleteImage(title, (Uri) this.mPostViewParameters.getUriList().get(index));
                this.mThumbList.remove(index);
            } catch (Exception e) {
                CamLog.e(FaceDetector.TAG, "Exception:", e);
            }
        }
    }

    protected int deleteImage(String filename, Uri uri) {
        String fullPath = this.mPostViewParameters.getTimeMachineStorageDirectory() + filename + CameraConstants.TIME_MACHINE_TEMPFILE_EXT;
        CamLog.d(FaceDetector.TAG, "try to delete " + fullPath);
        if (Common.isFileExist(fullPath)) {
            if (new File(fullPath).delete()) {
                if (this.mPostViewParameters.getUriList().remove(uri)) {
                    CamLog.d(FaceDetector.TAG, "deleted uri");
                } else {
                    CamLog.w(FaceDetector.TAG, "failure to delete uri!");
                }
                return this.mPostViewParameters.getUriList().size();
            }
            CamLog.d(FaceDetector.TAG, "delete failed");
        }
        CamLog.w(FaceDetector.TAG, "failure delete image file (return -1)");
        return -1;
    }

    private boolean checkValidateOneShotImage() {
        String oneShotSaveDir = this.mPostViewParameters.getCurrentStorageDirectory();
        String oneShotFullFilePath = oneShotSaveDir + this.mPostViewParameters.getSaveFileName() + CameraConstants.TIME_MACHINE_TEMPFILE_EXT;
        CamLog.d(FaceDetector.TAG, "checkValidateOneShotImage path = " + oneShotFullFilePath);
        if (new File(oneShotFullFilePath).exists()) {
            CamLog.d(FaceDetector.TAG, "checkValidateOneShotImage File exist.");
            return true;
        }
        CamLog.d(FaceDetector.TAG, "checkValidateOneShotImage file is not exist.");
        return false;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean checkValidateTimeMachineImage() {
        /*
        r13 = this;
        r10 = 0;
        r7 = 0;
        r8 = 0;
        r3 = 0;
        r5 = 0;
        r9 = 2131558772; // 0x7f0d0174 float:1.874287E38 double:1.0531299613E-314;
        r6 = r13.findViewById(r9);	 Catch:{ Exception -> 0x00e1 }
        r9 = r13.mPostViewParameters;	 Catch:{ Exception -> 0x00e1 }
        if (r9 == 0) goto L_0x00df;
    L_0x0010:
        r9 = r13.mPostViewParameters;	 Catch:{ Exception -> 0x00e1 }
        r9 = r9.getUriList();	 Catch:{ Exception -> 0x00e1 }
        if (r9 == 0) goto L_0x00df;
    L_0x0018:
        if (r6 == 0) goto L_0x00df;
    L_0x001a:
        r9 = r13.mThumbList;	 Catch:{ Exception -> 0x00e1 }
        if (r9 == 0) goto L_0x00df;
    L_0x001e:
        r9 = r13.mPostViewParameters;	 Catch:{ Exception -> 0x00e1 }
        r9 = r9.getUriList();	 Catch:{ Exception -> 0x00e1 }
        r7 = r9.size();	 Catch:{ Exception -> 0x00e1 }
        r9 = "CameraApp";
        r11 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00e1 }
        r11.<init>();	 Catch:{ Exception -> 0x00e1 }
        r12 = "validateImage() image list count = ";
        r11 = r11.append(r12);	 Catch:{ Exception -> 0x00e1 }
        r11 = r11.append(r7);	 Catch:{ Exception -> 0x00e1 }
        r11 = r11.toString();	 Catch:{ Exception -> 0x00e1 }
        com.lge.camera.util.CamLog.d(r9, r11);	 Catch:{ Exception -> 0x00e1 }
        r2 = 0;
        r4 = r3;
    L_0x0042:
        r9 = r13.mPostViewParameters;	 Catch:{ Exception -> 0x00eb }
        r9 = r9.getUriList();	 Catch:{ Exception -> 0x00eb }
        r9 = r9.isEmpty();	 Catch:{ Exception -> 0x00eb }
        if (r9 != 0) goto L_0x00f1;
    L_0x004e:
        r9 = r13.mPostViewParameters;	 Catch:{ Exception -> 0x00eb }
        r9 = r9.getUriList();	 Catch:{ Exception -> 0x00eb }
        r9 = r9.get(r2);	 Catch:{ Exception -> 0x00eb }
        r9 = (android.net.Uri) r9;	 Catch:{ Exception -> 0x00eb }
        r5 = r9.getPath();	 Catch:{ Exception -> 0x00eb }
        if (r5 == 0) goto L_0x00c8;
    L_0x0060:
        r3 = new java.io.File;	 Catch:{ Exception -> 0x00eb }
        r3.<init>(r5);	 Catch:{ Exception -> 0x00eb }
        r9 = r3.exists();	 Catch:{ Exception -> 0x00e1 }
        if (r9 != 0) goto L_0x00c5;
    L_0x006b:
        r9 = "CameraApp";
        r11 = "found deleted image!";
        com.lge.camera.util.CamLog.d(r9, r11);	 Catch:{ Exception -> 0x00e1 }
        r9 = r13.mPostViewParameters;	 Catch:{ Exception -> 0x00e1 }
        r9 = r9.getUriList();	 Catch:{ Exception -> 0x00e1 }
        r9.remove(r2);	 Catch:{ Exception -> 0x00e1 }
        r9 = r13.mThumbList;	 Catch:{ Exception -> 0x00e1 }
        r9.remove(r2);	 Catch:{ Exception -> 0x00e1 }
        r0 = r6;
        r0 = (android.widget.RelativeLayout) r0;	 Catch:{ Exception -> 0x00e1 }
        r9 = r0;
        r9.removeViewAt(r2);	 Catch:{ Exception -> 0x00e1 }
    L_0x0087:
        r8 = r8 + 1;
        r9 = "CameraApp";
        r11 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00e1 }
        r11.<init>();	 Catch:{ Exception -> 0x00e1 }
        r12 = "tot = ";
        r11 = r11.append(r12);	 Catch:{ Exception -> 0x00e1 }
        r11 = r11.append(r7);	 Catch:{ Exception -> 0x00e1 }
        r12 = " / index = ";
        r11 = r11.append(r12);	 Catch:{ Exception -> 0x00e1 }
        r11 = r11.append(r2);	 Catch:{ Exception -> 0x00e1 }
        r12 = " / validateCount = ";
        r11 = r11.append(r12);	 Catch:{ Exception -> 0x00e1 }
        r11 = r11.append(r8);	 Catch:{ Exception -> 0x00e1 }
        r11 = r11.toString();	 Catch:{ Exception -> 0x00e1 }
        com.lge.camera.util.CamLog.d(r9, r11);	 Catch:{ Exception -> 0x00e1 }
        if (r7 != r8) goto L_0x00ee;
    L_0x00b7:
        r9 = r13.mPostViewParameters;	 Catch:{ Exception -> 0x00e1 }
        r9 = r9.getUriList();	 Catch:{ Exception -> 0x00e1 }
        r7 = r9.size();	 Catch:{ Exception -> 0x00e1 }
        if (r7 != 0) goto L_0x00df;
    L_0x00c3:
        r9 = r10;
    L_0x00c4:
        return r9;
    L_0x00c5:
        r2 = r2 + 1;
        goto L_0x0087;
    L_0x00c8:
        r9 = r13.mPostViewParameters;	 Catch:{ Exception -> 0x00eb }
        r9 = r9.getUriList();	 Catch:{ Exception -> 0x00eb }
        r9.remove(r2);	 Catch:{ Exception -> 0x00eb }
        r9 = r13.mThumbList;	 Catch:{ Exception -> 0x00eb }
        r9.remove(r2);	 Catch:{ Exception -> 0x00eb }
        r0 = r6;
        r0 = (android.widget.RelativeLayout) r0;	 Catch:{ Exception -> 0x00eb }
        r9 = r0;
        r9.removeViewAt(r2);	 Catch:{ Exception -> 0x00eb }
        r3 = r4;
        goto L_0x0087;
    L_0x00df:
        r9 = 1;
        goto L_0x00c4;
    L_0x00e1:
        r1 = move-exception;
    L_0x00e2:
        r9 = "CameraApp";
        r11 = "Exception!";
        com.lge.camera.util.CamLog.e(r9, r11, r1);
        r9 = r10;
        goto L_0x00c4;
    L_0x00eb:
        r1 = move-exception;
        r3 = r4;
        goto L_0x00e2;
    L_0x00ee:
        r4 = r3;
        goto L_0x0042;
    L_0x00f1:
        r3 = r4;
        goto L_0x00b7;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.camera.PostviewTimeMachineActivity.checkValidateTimeMachineImage():boolean");
    }

    private void startTimeMachineFinishAnimaion() {
        View galleryView = findViewById(R.id.timemachine_gallery_layout);
        if (galleryView == null || galleryView.getVisibility() != 0) {
            postOnUiThread(this.mExitInteraction, 100);
        } else {
            gallerySlideDownAnimation(this.mExitInteraction);
        }
    }

    protected void deleteFinished() {
        Intent intent = getIntent();
        intent.putExtra("delete_done", true);
        setResult(100, intent);
        startTimeMachineFinishAnimaion();
    }

    private void clickTimeMachineSave() {
        CamLog.d(FaceDetector.TAG, "Time machine sava clicked.");
        if (!checkPauseAndAutoReview()) {
            return;
        }
        if (this.mPostViewParameters.getUriList().size() > TIMEMACHINE_SAVE_EFFECT) {
            int nSelectedCount = getCountThumbnailSelected();
            if (nSelectedCount > 0) {
                FileNamer.get().setTMSaveCount(nSelectedCount);
                gallerySlideDownAnimation(this.saveButtonDone);
                return;
            } else if (!this.mToast.isShowing()) {
                toast(getString(R.string.sp_select_photo_NORMAL));
                return;
            } else {
                return;
            }
        }
        showCheckBox(false);
    }

    private void gallerySlideDownAnimation(final Runnable action) {
        CamLog.d(FaceDetector.TAG, "gallerySlideDownAnimation");
        try {
            View galleryView = findViewById(R.id.timemachine_gallery_layout);
            if (!this.isAnimationRunning && galleryView != null) {
                galleryView.setVisibility(4);
                Animation gallerySlideDownAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.gallery_down);
                if (gallerySlideDownAnimation != null) {
                    gallerySlideDownAnimation.setAnimationListener(new AnimationListener() {
                        public void onAnimationStart(Animation animation) {
                        }

                        public void onAnimationRepeat(Animation animation) {
                        }

                        public void onAnimationEnd(Animation animation) {
                            if (action != null) {
                                action.run();
                            }
                            PostviewTimeMachineActivity.this.isAnimationRunning = false;
                        }
                    });
                    galleryView.startAnimation(gallerySlideDownAnimation);
                }
                this.isAnimationRunning = true;
            }
        } catch (NullPointerException e) {
            CamLog.e(FaceDetector.TAG, "NullPointerException : ", e);
        }
    }

    protected void doVolumeKey(KeyEvent event) {
        if (this.mPostViewParameters != null && CameraConstants.VOLUME_SHUTTER.equals(this.mPostViewParameters.getVolumeKey()) && event != null && event.getRepeatCount() == 0 && !getActivity().isFinishing()) {
            doBackKeyInPostview();
        }
    }

    protected void doBackKeyInPostview() {
        CamLog.d(FaceDetector.TAG, "KEYCODE_BACK");
        if (this.mPause || getActivity().isFinishing()) {
            CamLog.d(FaceDetector.TAG, "KEYCODE_BACK - return...");
            return;
        }
        stopTimeMachineAnimationAndGotoMultiSelectMode(false);
        onCreateDialog(6, this.mPostViewParameters.getApplicationMode());
    }

    private void stopTimeMachineAnimationAndGotoMultiSelectMode(boolean shotToast) {
        stopTimeMachineAnimation();
        this.mTimemachineMode = TIMEMACHINE_SAVE_NORMAL;
        if (!this.mPause) {
            showCheckBox(true);
            findViewById(R.id.timemachine_guide_text).setVisibility(TIMEMACHINE_EFFECT_NOT_START);
        }
    }

    private void startTimeMachineShotAnimation() {
        CamLog.d(FaceDetector.TAG, "startAnimation-start");
        try {
            if (this.mPostViewParameters != null && this.mPostViewParameters.getUriList() != null) {
                this.mTimeMachineShotCount = this.mPostViewParameters.getUriList().size();
                if (this.mTimeMachineShotCount != 0) {
                    this.mTimerCount = this.mTimeMachineShotCount - 1;
                    if (this.anim == null) {
                        this.anim = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.time_machine);
                        if (this.anim == null) {
                            CamLog.e(FaceDetector.TAG, "ShowTimeMachineEffect startAnimation() anim = null");
                            return;
                        }
                    }
                    this.mTimemachineMode = TIMEMACHINE_SAVE_EFFECT;
                    this.anim.setFillAfter(true);
                    View imageView = findViewById(R.id.captured_image);
                    if (imageView != null) {
                        imageView.setVisibility(4);
                    }
                    setThumbListVisible(true, TIMEMACHINE_EFFECT_NOT_START, 4);
                    setClockAnimationView(true);
                    this.mAnimationTimer = new Timer("TimeMachine");
                    this.mAnimationTimer.scheduleAtFixedRate(new TimerTask() {
                        public void run() {
                            PostviewTimeMachineActivity.this.runOnUiThread(PostviewTimeMachineActivity.this.mTimeMachineAnimationRunnable);
                        }
                    }, 100, 100);
                }
            }
        } catch (NullPointerException e) {
            CamLog.e(FaceDetector.TAG, "NullPointerException : " + e);
        }
    }

    private void stopTimeMachineAnimation() {
        if (this.mAnimationTimer != null) {
            CamLog.d(FaceDetector.TAG, "stopTimeMachineAnimation-stop");
            this.mAnimationTimer.purge();
            this.mAnimationTimer.cancel();
            this.mAnimationTimer = null;
            this.mCurrentSelectedIndex = 4;
            ImageView clockMinuteView = (ImageView) findViewById(R.id.clock_needle_minute_view);
            ImageView clockSecView = (ImageView) findViewById(R.id.clock_needle_sec_view);
            if (clockMinuteView != null) {
                clockMinuteView.clearAnimation();
            }
            if (clockSecView != null) {
                clockSecView.clearAnimation();
            }
            setClockAnimationView(false);
            ImageView aniView = (ImageView) findViewById(R.id.captured_ani_view);
            if (aniView != null) {
                aniView.clearAnimation();
                aniView.setVisibility(8);
                Util.clearImageViewDrawableOnly(aniView);
            }
            View imageView = findViewById(R.id.captured_image);
            if (imageView != null) {
                imageView.clearAnimation();
                imageView.setVisibility(TIMEMACHINE_EFFECT_NOT_START);
            }
            setThumbListVisible(true, this.mCurrentSelectedIndex, TIMEMACHINE_EFFECT_NOT_START);
            refreshLoadCapturedImages(this.mCurrentSelectedIndex);
            this.anim = null;
            this.mTimemachineMode = TIMEMACHINE_SAVE_NORMAL;
        }
        this.mTimerCount = TIMEMACHINE_EFFECT_NOT_START;
        this.mTimeMachineShotCount = TIMEMACHINE_EFFECT_NOT_START;
    }

    private void setClockAnimationView(boolean isSet) {
        View clockAniView = findViewById(R.id.clock_ani_layout);
        ImageView clockBgView = (ImageView) findViewById(R.id.clock_bg_view);
        ImageView clockNeedleMinuteView = (ImageView) findViewById(R.id.clock_needle_minute_view);
        ImageView clockNeedleSecView = (ImageView) findViewById(R.id.clock_needle_sec_view);
        if (clockAniView != null && clockBgView != null && clockNeedleMinuteView != null && clockNeedleSecView != null && this.mOrientationInfo != null) {
            clockNeedleMinuteView.clearAnimation();
            clockNeedleSecView.clearAnimation();
            clockAniView.setVisibility(8);
            Util.clearImageViewDrawableOnly(clockBgView);
            Util.clearImageViewDrawableOnly(clockNeedleMinuteView);
            Util.clearImageViewDrawableOnly(clockNeedleSecView);
            if (isSet) {
                int clockAniMarginTop;
                clockAniView.setVisibility(TIMEMACHINE_EFFECT_NOT_START);
                clockBgView.setBackgroundResource(R.drawable.camera_postview_timemachine_clock_bg);
                clockNeedleMinuteView.setBackgroundResource(R.drawable.camera_postview_timemachine_clock_needle_minute);
                clockNeedleSecView.setBackgroundResource(R.drawable.camera_postview_timemachine_needle);
                LayoutParams clockAniParam = (LayoutParams) clockAniView.getLayoutParams();
                if (this.mOrientationInfo.getOrientation() == 0 || this.mOrientationInfo.getOrientation() == TIMEMACHINE_SAVE_NORMAL) {
                    clockAniMarginTop = Common.getPixelFromDimens(getApplicationContext(), R.dimen.timemachine_clock_ani_margin_top);
                } else {
                    clockAniMarginTop = Common.getPixelFromDimens(getApplicationContext(), R.dimen.timemachine_clock_ani_margin_top_port);
                }
                Common.resetLayoutParameter(clockAniParam);
                clockAniParam.addRule(10, TIMEMACHINE_SAVE_EFFECT);
                clockAniParam.addRule(21, TIMEMACHINE_SAVE_EFFECT);
                clockAniParam.topMargin = clockAniMarginTop;
                clockAniView.setLayoutParams(clockAniParam);
                return;
            }
            Util.clearImageViewBackgroundDrawable(clockBgView);
            Util.clearImageViewBackgroundDrawable(clockNeedleMinuteView);
            Util.clearImageViewBackgroundDrawable(clockNeedleSecView);
        }
    }

    private void timeMachineClockMinuteAnimation(int clockInterval) {
        if (this.mTimerCount >= 0) {
            try {
                RotateAnimation ra = new RotateAnimation((float) TIMEMACHINE_EFFECT_NOT_START, (float) -30, TIMEMACHINE_SAVE_EFFECT, 0.5f, TIMEMACHINE_SAVE_EFFECT, 0.5f);
                ra.setFillAfter(true);
                ra.setDuration((long) clockInterval);
                ra.setInterpolator(new AccelerateInterpolator());
                ImageView clockView = (ImageView) findViewById(R.id.clock_needle_minute_view);
                if (clockView != null) {
                    clockView.startAnimation(ra);
                }
            } catch (Exception e) {
                CamLog.e(FaceDetector.TAG, "Exception!", e);
                stopTimeMachineAnimationAndGotoMultiSelectMode(false);
            }
        }
    }

    private void timeMachineClockSecAnimation(int clockInterval) {
        if (this.mTimerCount >= 0) {
            try {
                RotateAnimation ra = new RotateAnimation((float) TIMEMACHINE_EFFECT_NOT_START, (float) -360, TIMEMACHINE_SAVE_EFFECT, 0.5f, TIMEMACHINE_SAVE_EFFECT, 0.5f);
                ra.setFillAfter(true);
                ra.setDuration((long) clockInterval);
                ImageView clockView = (ImageView) findViewById(R.id.clock_needle_sec_view);
                if (clockView != null) {
                    clockView.startAnimation(ra);
                }
            } catch (Exception e) {
                CamLog.e(FaceDetector.TAG, "Exception!", e);
                stopTimeMachineAnimationAndGotoMultiSelectMode(false);
            }
        }
    }

    private void timeMachineThumbAnimation(final View view) {
        if (view != null) {
            AlphaAnimation alphaAni = new AlphaAnimation(0.0f, RotateView.DEFAULT_TEXT_SCALE_X);
            alphaAni.setAnimationListener(new AnimationListener() {
                public void onAnimationStart(Animation animation) {
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    if (view != null) {
                        view.clearAnimation();
                        view.setVisibility(PostviewTimeMachineActivity.TIMEMACHINE_EFFECT_NOT_START);
                    }
                }
            });
            alphaAni.setFillAfter(false);
            alphaAni.setDuration(300);
            alphaAni.setInterpolator(new AccelerateInterpolator(CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK));
            view.startAnimation(alphaAni);
        }
    }

    private void doGalleryLaunching() {
        CamLog.d(FaceDetector.TAG, "doGalleryLaunching-start.");
        Uri upToDateUri = this.mPostViewParameters.getSavedUri();
        try {
            if (getActivity().getPackageManager().getApplicationInfo("com.android.gallery3d", 128).enabled) {
                Intent intent = new Intent("com.android.camera.action.REVIEW", upToDateUri);
                intent.addFlags(67108864);
                try {
                    startActivity(intent);
                    this.isGalleryLaunchingState = TIMEMACHINE_SAVE_NORMAL;
                    CamLog.d(FaceDetector.TAG, "doGalleryLaunching-started.");
                    return;
                } catch (ActivityNotFoundException ex) {
                    CamLog.e(FaceDetector.TAG, "review fail! uri:" + upToDateUri, ex);
                    return;
                }
            }
            onCreateDialog(5, this.mPostViewParameters.getApplicationMode());
        } catch (NameNotFoundException e) {
            CamLog.e(FaceDetector.TAG, "Gallery is not founded:", e);
            this.isGalleryLaunchingState = TIMEMACHINE_EFFECT_NOT_START;
        }
    }

    public void doEnableGalleryPositiveClick() {
        this.isGalleryLaunchingState = TIMEMACHINE_SAVE_NORMAL;
        startActivity(new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", Uri.parse("package:com.android.gallery3d")));
    }

    protected boolean checkTimeMachineFileOverwritten() {
        boolean checkValue = false;
        ArrayList<Uri> mTempTmsNameList = this.mPostViewParameters.getUriList();
        if (!(mTempTmsNameList == null || mTempTmsNameList.isEmpty())) {
            File mPresentTimeMachinefile = new File(((Uri) this.mPostViewParameters.getUriList().get(TIMEMACHINE_EFFECT_NOT_START)).getPath());
            if (this.mFirstTimeMachineDataSize != mPresentTimeMachinefile.lastModified()) {
                checkValue = true;
            } else {
                checkValue = false;
            }
            this.mFirstTimeMachineDataSize = mPresentTimeMachinefile.lastModified();
        }
        return checkValue;
    }
}
