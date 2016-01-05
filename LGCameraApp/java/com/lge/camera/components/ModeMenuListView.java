package com.lge.camera.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class ModeMenuListView extends ListView {
    private static final int DEFAULT_FLING_DURATION = 250;
    private static final int INVALID_POINTER = -1;
    private int dy;
    private boolean isClick;
    private int mActivePointerId;
    private float mDeceleration;
    private int mFirstVisiblePos;
    private int mMaximumVelocity;
    private OnModeMenuListListener mModeMenuListListener;
    private PerformClick mPerformClick;
    private int mPointPos;
    private int mPressedStateDuration;
    private Runnable mTabRunnable;
    private int mTapTimeout;
    private int mTouchSlop;
    private VelocityTracker mVelocityTracker;
    private float old_y;
    private float start;

    public interface OnModeMenuListListener {
        void onLayoutChanged(boolean z, int i, int i2, int i3, int i4);

        void onTouchScrollEnd();

        void onTouchScrollStarted();

        void onTouchScrollTab(int i);
    }

    private class WindowRunnnable {
        private int mOriginalAttachCount;

        private WindowRunnnable() {
        }

        public void rememberWindowAttachCount() {
            this.mOriginalAttachCount = ModeMenuListView.this.getWindowAttachCount();
        }

        public boolean sameWindow() {
            return ModeMenuListView.this.hasWindowFocus() && ModeMenuListView.this.getWindowAttachCount() == this.mOriginalAttachCount;
        }
    }

    private class PerformClick extends WindowRunnnable implements Runnable {
        View mChild;
        int mClickMotionPosition;

        private PerformClick() {
            super();
        }

        public void run() {
            ListAdapter adapter = ModeMenuListView.this.getAdapter();
            int motionPosition = this.mClickMotionPosition;
            if (adapter != null && ModeMenuListView.this.getAdapter().getCount() > 0 && motionPosition != ModeMenuListView.INVALID_POINTER && motionPosition < adapter.getCount() && sameWindow() && this.mChild != null) {
                this.mChild.setPressed(true);
                ModeMenuListView.this.performItemClick(this.mChild, motionPosition, adapter.getItemId(motionPosition));
            }
        }
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        CamLog.d(FaceDetector.TAG, "onInitializeAccessibilityEvent. return.");
    }

    public void setOnModeMenuListListener(OnModeMenuListListener listener) {
        this.mModeMenuListListener = listener;
    }

    public ModeMenuListView(Context context) {
        super(context);
        this.mModeMenuListListener = null;
        this.mDeceleration = 0.0f;
        this.mTouchSlop = 0;
        this.start = 0.0f;
        this.old_y = 0.0f;
        this.dy = 0;
        this.mPointPos = 0;
        this.mFirstVisiblePos = 0;
        this.isClick = false;
        this.mVelocityTracker = null;
        this.mMaximumVelocity = 0;
        this.mTapTimeout = 0;
        this.mPressedStateDuration = 0;
        this.mActivePointerId = INVALID_POINTER;
        this.mTabRunnable = new Runnable() {
            public void run() {
                View motionView = ModeMenuListView.this.getChildAt(ModeMenuListView.this.mPointPos - ModeMenuListView.this.mFirstVisiblePos);
                if (motionView != null) {
                    motionView.setPressed(true);
                    ModeMenuListView.this.setPressed(true);
                }
            }
        };
        this.mPerformClick = null;
        init(context);
    }

    public ModeMenuListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mModeMenuListListener = null;
        this.mDeceleration = 0.0f;
        this.mTouchSlop = 0;
        this.start = 0.0f;
        this.old_y = 0.0f;
        this.dy = 0;
        this.mPointPos = 0;
        this.mFirstVisiblePos = 0;
        this.isClick = false;
        this.mVelocityTracker = null;
        this.mMaximumVelocity = 0;
        this.mTapTimeout = 0;
        this.mPressedStateDuration = 0;
        this.mActivePointerId = INVALID_POINTER;
        this.mTabRunnable = new Runnable() {
            public void run() {
                View motionView = ModeMenuListView.this.getChildAt(ModeMenuListView.this.mPointPos - ModeMenuListView.this.mFirstVisiblePos);
                if (motionView != null) {
                    motionView.setPressed(true);
                    ModeMenuListView.this.setPressed(true);
                }
            }
        };
        this.mPerformClick = null;
        init(context);
    }

    public ModeMenuListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mModeMenuListListener = null;
        this.mDeceleration = 0.0f;
        this.mTouchSlop = 0;
        this.start = 0.0f;
        this.old_y = 0.0f;
        this.dy = 0;
        this.mPointPos = 0;
        this.mFirstVisiblePos = 0;
        this.isClick = false;
        this.mVelocityTracker = null;
        this.mMaximumVelocity = 0;
        this.mTapTimeout = 0;
        this.mPressedStateDuration = 0;
        this.mActivePointerId = INVALID_POINTER;
        this.mTabRunnable = new Runnable() {
            public void run() {
                View motionView = ModeMenuListView.this.getChildAt(ModeMenuListView.this.mPointPos - ModeMenuListView.this.mFirstVisiblePos);
                if (motionView != null) {
                    motionView.setPressed(true);
                    ModeMenuListView.this.setPressed(true);
                }
            }
        };
        this.mPerformClick = null;
        init(context);
    }

    private void init(Context context) {
        ViewConfiguration configuration = ViewConfiguration.get(context);
        this.mTouchSlop = configuration.getScaledTouchSlop();
        this.mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        this.mTapTimeout = ViewConfiguration.getTapTimeout();
        this.mPressedStateDuration = ViewConfiguration.getPressedStateDuration();
        this.mDeceleration = (386.0878f * (context.getResources().getDisplayMetrics().density * CameraConstants.SMARTZOOM_ZOOM_AREA_WIDTH)) * ViewConfiguration.getScrollFriction();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r33) {
        /*
        r32 = this;
        r26 = r33.getX();
        r27 = r33.getY();
        r0 = r32;
        r0 = r0.mVelocityTracker;
        r28 = r0;
        if (r28 != 0) goto L_0x001a;
    L_0x0010:
        r28 = android.view.VelocityTracker.obtain();
        r0 = r28;
        r1 = r32;
        r1.mVelocityTracker = r0;
    L_0x001a:
        r0 = r32;
        r0 = r0.mVelocityTracker;
        r28 = r0;
        r0 = r28;
        r1 = r33;
        r0.addMovement(r1);
        r28 = r33.getActionMasked();
        switch(r28) {
            case 0: goto L_0x0031;
            case 1: goto L_0x0154;
            case 2: goto L_0x0092;
            case 3: goto L_0x038c;
            case 4: goto L_0x002e;
            case 5: goto L_0x002e;
            case 6: goto L_0x03df;
            default: goto L_0x002e;
        };
    L_0x002e:
        r28 = 1;
        return r28;
    L_0x0031:
        r28 = 0;
        r0 = r33;
        r1 = r28;
        r28 = r0.getPointerId(r1);
        r0 = r28;
        r1 = r32;
        r1.mActivePointerId = r0;
        r0 = r27;
        r1 = r32;
        r1.start = r0;
        r0 = r27;
        r1 = r32;
        r1.old_y = r0;
        r28 = r32.getFirstVisiblePosition();
        r0 = r28;
        r1 = r32;
        r1.mFirstVisiblePos = r0;
        r28 = java.lang.Math.round(r26);
        r29 = java.lang.Math.round(r27);
        r0 = r32;
        r1 = r28;
        r2 = r29;
        r28 = r0.pointToPosition(r1, r2);
        r0 = r28;
        r1 = r32;
        r1.mPointPos = r0;
        r0 = r32;
        r0 = r0.mTabRunnable;
        r28 = r0;
        r0 = r32;
        r0 = r0.mTapTimeout;
        r29 = r0;
        r0 = r29;
        r0 = (long) r0;
        r30 = r0;
        r0 = r32;
        r1 = r28;
        r2 = r30;
        r0.postDelayed(r1, r2);
        r28 = 1;
        r0 = r28;
        r1 = r32;
        r1.isClick = r0;
        goto L_0x002e;
    L_0x0092:
        r0 = r32;
        r0 = r0.mPointPos;
        r16 = r0;
        r0 = r32;
        r0 = r0.mActivePointerId;
        r28 = r0;
        r0 = r33;
        r1 = r28;
        r18 = r0.findPointerIndex(r1);
        r0 = r33;
        r1 = r18;
        r28 = r0.getY(r1);
        r0 = r28;
        r9 = (int) r0;
        r0 = (float) r9;
        r28 = r0;
        r0 = r32;
        r0 = r0.start;
        r29 = r0;
        r28 = r28 - r29;
        r28 = java.lang.Math.abs(r28);
        r0 = r32;
        r0 = r0.mTouchSlop;
        r29 = r0;
        r0 = r29;
        r0 = (float) r0;
        r29 = r0;
        r28 = (r28 > r29 ? 1 : (r28 == r29 ? 0 : -1));
        if (r28 <= 0) goto L_0x011e;
    L_0x00cf:
        r28 = r32.getFirstVisiblePosition();
        r0 = r28;
        r1 = r32;
        r1.mFirstVisiblePos = r0;
        r0 = r32;
        r0 = r0.mFirstVisiblePos;
        r28 = r0;
        r28 = r16 - r28;
        r0 = r32;
        r1 = r28;
        r4 = r0.getChildAt(r1);
        if (r4 == 0) goto L_0x00f2;
    L_0x00eb:
        r28 = 0;
        r0 = r28;
        r4.setPressed(r0);
    L_0x00f2:
        r28 = 0;
        r0 = r28;
        r1 = r32;
        r1.isClick = r0;
        r28 = r32.getHandler();
        if (r28 == 0) goto L_0x010d;
    L_0x0100:
        r28 = r32.getHandler();
        r0 = r32;
        r0 = r0.mTabRunnable;
        r29 = r0;
        r28.removeCallbacks(r29);
    L_0x010d:
        r0 = r32;
        r0 = r0.mModeMenuListListener;
        r28 = r0;
        if (r28 == 0) goto L_0x011e;
    L_0x0115:
        r0 = r32;
        r0 = r0.mModeMenuListListener;
        r28 = r0;
        r28.onTouchScrollStarted();
    L_0x011e:
        r0 = (float) r9;
        r28 = r0;
        r0 = r32;
        r0 = r0.old_y;
        r29 = r0;
        r28 = r28 - r29;
        r28 = java.lang.Math.round(r28);
        r0 = r28;
        r1 = r32;
        r1.dy = r0;
        r0 = (float) r9;
        r28 = r0;
        r0 = r28;
        r1 = r32;
        r1.old_y = r0;
        r0 = r32;
        r0 = r0.dy;
        r28 = r0;
        r0 = r28;
        r0 = -r0;
        r28 = r0;
        r29 = 0;
        r0 = r32;
        r1 = r28;
        r2 = r29;
        r0.smoothScrollBy(r1, r2);
        goto L_0x002e;
    L_0x0154:
        r0 = r32;
        r12 = r0.mPointPos;
        r28 = r32.getFirstVisiblePosition();
        r0 = r28;
        r1 = r32;
        r1.mFirstVisiblePos = r0;
        r0 = r32;
        r0 = r0.mPointPos;
        r28 = r0;
        r0 = r32;
        r0 = r0.mFirstVisiblePos;
        r29 = r0;
        r28 = r28 - r29;
        r0 = r32;
        r1 = r28;
        r4 = r0.getChildAt(r1);
        if (r4 == 0) goto L_0x020b;
    L_0x017a:
        r11 = r32.getMeasuredHeight();
        r5 = r4.getMeasuredHeight();
        r28 = r11 - r5;
        r23 = r28 / 2;
        r0 = r32;
        r0 = r0.isClick;
        r28 = r0;
        if (r28 == 0) goto L_0x02a5;
    L_0x018e:
        r28 = r11 - r5;
        r28 = r28 / 2;
        r28 = java.lang.Math.abs(r28);
        r0 = r28;
        r0 = (float) r0;
        r28 = r0;
        r28 = (r28 > r27 ? 1 : (r28 == r27 ? 0 : -1));
        if (r28 > 0) goto L_0x0236;
    L_0x019f:
        r28 = r11 + r5;
        r28 = r28 / 2;
        r28 = java.lang.Math.abs(r28);
        r0 = r28;
        r0 = (float) r0;
        r28 = r0;
        r28 = (r28 > r27 ? 1 : (r28 == r27 ? 0 : -1));
        if (r28 < 0) goto L_0x0236;
    L_0x01b0:
        r0 = r32;
        r0 = r0.mPerformClick;
        r28 = r0;
        if (r28 != 0) goto L_0x01cb;
    L_0x01b8:
        r28 = new com.lge.camera.components.ModeMenuListView$PerformClick;
        r29 = 0;
        r0 = r28;
        r1 = r32;
        r2 = r29;
        r0.<init>();
        r0 = r28;
        r1 = r32;
        r1.mPerformClick = r0;
    L_0x01cb:
        r0 = r32;
        r15 = r0.mPerformClick;
        r15.mChild = r4;
        r15.mClickMotionPosition = r12;
        r15.rememberWindowAttachCount();
        r28 = 1;
        r0 = r28;
        r4.setPressed(r0);
        r28 = 1;
        r0 = r32;
        r1 = r28;
        r0.setPressed(r1);
        r28 = new com.lge.camera.components.ModeMenuListView$1;
        r0 = r28;
        r1 = r32;
        r0.<init>(r15);
        r0 = r32;
        r0 = r0.mPressedStateDuration;
        r29 = r0;
        r0 = r29;
        r0 = (long) r0;
        r30 = r0;
        r0 = r32;
        r1 = r28;
        r2 = r30;
        r0.postDelayed(r1, r2);
    L_0x0203:
        r28 = 0;
        r0 = r28;
        r1 = r32;
        r1.isClick = r0;
    L_0x020b:
        r28 = 0;
        r0 = r28;
        r1 = r32;
        r1.old_y = r0;
        r0 = r32;
        r0 = r0.mVelocityTracker;
        r28 = r0;
        if (r28 == 0) goto L_0x022c;
    L_0x021b:
        r0 = r32;
        r0 = r0.mVelocityTracker;
        r28 = r0;
        r28.recycle();
        r28 = 0;
        r0 = r28;
        r1 = r32;
        r1.mVelocityTracker = r0;
    L_0x022c:
        r28 = -1;
        r0 = r28;
        r1 = r32;
        r1.mActivePointerId = r0;
        goto L_0x002e;
    L_0x0236:
        r28 = r11 - r5;
        r28 = r28 / 2;
        r28 = java.lang.Math.abs(r28);
        r0 = r28;
        r0 = (float) r0;
        r28 = r0;
        r28 = (r28 > r27 ? 1 : (r28 == r27 ? 0 : -1));
        if (r28 > 0) goto L_0x0258;
    L_0x0247:
        r28 = r11 + r5;
        r28 = r28 / 2;
        r28 = java.lang.Math.abs(r28);
        r0 = r28;
        r0 = (float) r0;
        r28 = r0;
        r28 = (r28 > r27 ? 1 : (r28 == r27 ? 0 : -1));
        if (r28 >= 0) goto L_0x0203;
    L_0x0258:
        r28 = 0;
        r0 = r28;
        r4.setPressed(r0);
        r28 = r32.getHandler();
        if (r28 == 0) goto L_0x0272;
    L_0x0265:
        r28 = r32.getHandler();
        r0 = r32;
        r0 = r0.mTabRunnable;
        r29 = r0;
        r28.removeCallbacks(r29);
    L_0x0272:
        r28 = r4.getY();
        r28 = java.lang.Math.round(r28);
        r28 = r28 - r23;
        r29 = 250; // 0xfa float:3.5E-43 double:1.235E-321;
        r0 = r32;
        r1 = r28;
        r2 = r29;
        r0.smoothScrollBy(r1, r2);
        r0 = r32;
        r0 = r0.mPointPos;
        r20 = r0;
        r28 = new com.lge.camera.components.ModeMenuListView$2;
        r0 = r28;
        r1 = r32;
        r2 = r20;
        r0.<init>(r2);
        r30 = 250; // 0xfa float:3.5E-43 double:1.235E-321;
        r0 = r32;
        r1 = r28;
        r2 = r30;
        r0.postDelayed(r1, r2);
        goto L_0x0203;
    L_0x02a5:
        r28 = r32.getHandler();
        if (r28 == 0) goto L_0x02b8;
    L_0x02ab:
        r28 = r32.getHandler();
        r0 = r32;
        r0 = r0.mTabRunnable;
        r29 = r0;
        r28.removeCallbacks(r29);
    L_0x02b8:
        r28 = 0;
        r0 = r28;
        r4.setPressed(r0);
        r0 = r32;
        r0 = r0.mVelocityTracker;
        r25 = r0;
        r28 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r0 = r32;
        r0 = r0.mMaximumVelocity;
        r29 = r0;
        r0 = r29;
        r0 = (float) r0;
        r29 = r0;
        r0 = r25;
        r1 = r28;
        r2 = r29;
        r0.computeCurrentVelocity(r1, r2);
        r0 = r32;
        r0 = r0.mActivePointerId;
        r28 = r0;
        r0 = r25;
        r1 = r28;
        r28 = r0.getYVelocity(r1);
        r0 = r28;
        r10 = (int) r0;
        if (r10 < 0) goto L_0x0384;
    L_0x02ee:
        r22 = 1;
    L_0x02f0:
        r28 = r4.getY();
        r6 = java.lang.Math.round(r28);
        r28 = r6 - r23;
        r7 = r28 % r5;
        r28 = java.lang.Math.abs(r7);
        r29 = r5 / 4;
        r0 = r29;
        r0 = (float) r0;
        r29 = r0;
        r29 = java.lang.Math.round(r29);
        r0 = r28;
        r1 = r29;
        if (r0 <= r1) goto L_0x0316;
    L_0x0311:
        if (r10 <= 0) goto L_0x0388;
    L_0x0313:
        if (r7 <= 0) goto L_0x0316;
    L_0x0315:
        r7 = r7 - r5;
    L_0x0316:
        r28 = r10 / 4;
        r24 = java.lang.Math.abs(r28);
        r28 = r24 % r5;
        r28 = r24 - r28;
        r28 = r28 * r22;
        r21 = r7 - r28;
        r0 = r24;
        r0 = r0 * 1000;
        r28 = r0;
        r0 = r28;
        r0 = (float) r0;
        r28 = r0;
        r0 = r32;
        r0 = r0.mDeceleration;
        r29 = r0;
        r28 = r28 / r29;
        r0 = r28;
        r8 = (int) r0;
        r28 = 250; // 0xfa float:3.5E-43 double:1.235E-321;
        r0 = r28;
        r8 = java.lang.Math.max(r8, r0);
        r28 = "CameraApp";
        r29 = new java.lang.StringBuilder;
        r29.<init>();
        r30 = "scollDest = ";
        r29 = r29.append(r30);
        r0 = r29;
        r1 = r21;
        r29 = r0.append(r1);
        r30 = ", duration = ";
        r29 = r29.append(r30);
        r0 = r29;
        r29 = r0.append(r8);
        r29 = r29.toString();
        com.lge.camera.util.CamLog.d(r28, r29);
        r0 = r32;
        r1 = r21;
        r0.smoothScrollBy(r1, r8);
        r0 = r32;
        r0 = r0.mModeMenuListListener;
        r28 = r0;
        if (r28 == 0) goto L_0x020b;
    L_0x0379:
        r0 = r32;
        r0 = r0.mModeMenuListListener;
        r28 = r0;
        r28.onTouchScrollEnd();
        goto L_0x020b;
    L_0x0384:
        r22 = -1;
        goto L_0x02f0;
    L_0x0388:
        if (r7 >= 0) goto L_0x0316;
    L_0x038a:
        r7 = r7 + r5;
        goto L_0x0316;
    L_0x038c:
        r28 = 0;
        r0 = r32;
        r1 = r28;
        r0.setPressed(r1);
        r0 = r32;
        r0 = r0.mPointPos;
        r28 = r0;
        r0 = r32;
        r0 = r0.mFirstVisiblePos;
        r29 = r0;
        r28 = r28 - r29;
        r0 = r32;
        r1 = r28;
        r13 = r0.getChildAt(r1);
        if (r13 == 0) goto L_0x03b4;
    L_0x03ad:
        r28 = 0;
        r0 = r28;
        r13.setPressed(r0);
    L_0x03b4:
        r28 = 0;
        r0 = r28;
        r1 = r32;
        r1.old_y = r0;
        r0 = r32;
        r0 = r0.mVelocityTracker;
        r28 = r0;
        if (r28 == 0) goto L_0x03d5;
    L_0x03c4:
        r0 = r32;
        r0 = r0.mVelocityTracker;
        r28 = r0;
        r28.recycle();
        r28 = 0;
        r0 = r28;
        r1 = r32;
        r1.mVelocityTracker = r0;
    L_0x03d5:
        r28 = -1;
        r0 = r28;
        r1 = r32;
        r1.mActivePointerId = r0;
        goto L_0x002e;
    L_0x03df:
        r28 = r33.getAction();
        r29 = 65280; // 0xff00 float:9.1477E-41 double:3.22526E-319;
        r28 = r28 & r29;
        r19 = r28 >> 8;
        r0 = r33;
        r1 = r19;
        r17 = r0.getPointerId(r1);
        r0 = r32;
        r0 = r0.mActivePointerId;
        r28 = r0;
        r0 = r17;
        r1 = r28;
        if (r0 != r1) goto L_0x002e;
    L_0x03fe:
        if (r19 != 0) goto L_0x0420;
    L_0x0400:
        r14 = 1;
    L_0x0401:
        r0 = r33;
        r28 = r0.getPointerId(r14);
        r0 = r28;
        r1 = r32;
        r1.mActivePointerId = r0;
        r0 = r32;
        r0 = r0.mVelocityTracker;
        r28 = r0;
        if (r28 == 0) goto L_0x002e;
    L_0x0415:
        r0 = r32;
        r0 = r0.mVelocityTracker;
        r28 = r0;
        r28.clear();
        goto L_0x002e;
    L_0x0420:
        r14 = 0;
        goto L_0x0401;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.camera.components.ModeMenuListView.onTouchEvent(android.view.MotionEvent):boolean");
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }
}
