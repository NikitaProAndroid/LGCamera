package com.lge.camera.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import com.lge.camera.R;

public class MultiDirectionSlidingDrawer extends ViewGroup {
    private static final int ANIMATION_FRAME_DURATION = 16;
    private static final int COLLAPSED_FULL_CLOSED = -10002;
    private static final int EXPANDED_FULL_OPEN = -10001;
    private static final float MAXIMUM_ACCELERATION = 2000.0f;
    private static final float MAXIMUM_MAJOR_VELOCITY = 200.0f;
    private static final float MAXIMUM_MINOR_VELOCITY = 150.0f;
    private static final float MAXIMUM_TAP_VELOCITY = 100.0f;
    private static final int MSG_ANIMATE = 1000;
    public static final int ORIENTATION_BTT = 1;
    public static final int ORIENTATION_LTR = 2;
    public static final int ORIENTATION_RTL = 0;
    public static final int ORIENTATION_TTB = 3;
    private static final int TAP_THRESHOLD = 6;
    private static final int VELOCITY_UNITS = 1000;
    private boolean mAllowSingleTap;
    private boolean mAnimateOnClick;
    private float mAnimatedAcceleration;
    private float mAnimatedVelocity;
    private boolean mAnimating;
    private long mAnimationLastTime;
    private float mAnimationPosition;
    private int mBottomOffset;
    private View mContent;
    private final int mContentId;
    private long mCurrentAnimationTime;
    private boolean mExpanded;
    private final Rect mFrame;
    private View mHandle;
    private int mHandleHeight;
    private final int mHandleId;
    private int mHandleWidth;
    private final Handler mHandler;
    private final Rect mInvalidate;
    private boolean mInvert;
    private boolean mLocked;
    private int mMaximumAcceleration;
    private int mMaximumMajorVelocity;
    private int mMaximumMinorVelocity;
    private final int mMaximumTapVelocity;
    private OnDrawerCloseListener mOnDrawerCloseListener;
    private OnDrawerOpenListener mOnDrawerOpenListener;
    private OnDrawerScrollListener mOnDrawerScrollListener;
    private final int mTapThreshold;
    private int mTopOffset;
    private int mTouchDelta;
    private boolean mTracking;
    private VelocityTracker mVelocityTracker;
    private final int mVelocityUnits;
    private boolean mVertical;

    private class DrawerToggler implements OnClickListener {
        private DrawerToggler() {
        }

        public void onClick(View v) {
            if (!MultiDirectionSlidingDrawer.this.mLocked) {
                if (MultiDirectionSlidingDrawer.this.mAnimateOnClick) {
                    MultiDirectionSlidingDrawer.this.animateToggle();
                } else {
                    MultiDirectionSlidingDrawer.this.toggle();
                }
            }
        }
    }

    public interface OnDrawerCloseListener {
        void onDrawerClosed();
    }

    public interface OnDrawerOpenListener {
        void onDrawerOpened();
    }

    public interface OnDrawerScrollListener {
        void onScrollEnded();

        void onScrollStarted();
    }

    private class SlidingHandler extends Handler {
        private SlidingHandler() {
        }

        public void handleMessage(Message m) {
            switch (m.what) {
                case MultiDirectionSlidingDrawer.VELOCITY_UNITS /*1000*/:
                    MultiDirectionSlidingDrawer.this.doAnimation();
                default:
            }
        }
    }

    public MultiDirectionSlidingDrawer(Context context, AttributeSet attrs) {
        this(context, attrs, ORIENTATION_RTL);
    }

    public MultiDirectionSlidingDrawer(Context context, AttributeSet attrs, int defStyle) {
        boolean z;
        super(context, attrs, defStyle);
        this.mFrame = new Rect();
        this.mInvalidate = new Rect();
        this.mHandler = new SlidingHandler();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MultiDirectionSlidingDrawer, defStyle, ORIENTATION_RTL);
        int orientation = a.getInt(ORIENTATION_RTL, ORIENTATION_BTT);
        if (orientation == ORIENTATION_BTT || orientation == ORIENTATION_TTB) {
            z = true;
        } else {
            z = false;
        }
        this.mVertical = z;
        this.mBottomOffset = (int) a.getDimension(ORIENTATION_TTB, 0.0f);
        this.mTopOffset = (int) a.getDimension(4, 0.0f);
        this.mAllowSingleTap = a.getBoolean(5, true);
        this.mAnimateOnClick = a.getBoolean(TAP_THRESHOLD, true);
        if (orientation == ORIENTATION_TTB || orientation == ORIENTATION_LTR) {
            z = true;
        } else {
            z = false;
        }
        this.mInvert = z;
        int handleId = a.getResourceId(ORIENTATION_BTT, ORIENTATION_RTL);
        if (handleId == 0) {
            throw new IllegalArgumentException("The handle attribute is required and must refer to a valid child.");
        }
        int contentId = a.getResourceId(ORIENTATION_LTR, ORIENTATION_RTL);
        if (contentId == 0) {
            throw new IllegalArgumentException("The content attribute is required and must refer to a valid child.");
        } else if (handleId == contentId) {
            throw new IllegalArgumentException("The content and handle attributes must refer to different children.");
        } else {
            this.mHandleId = handleId;
            this.mContentId = contentId;
            float density = getResources().getDisplayMetrics().density;
            this.mTapThreshold = (int) ((6.0f * density) + 0.5f);
            this.mMaximumTapVelocity = (int) ((MAXIMUM_TAP_VELOCITY * density) + 0.5f);
            this.mMaximumMinorVelocity = (int) ((MAXIMUM_MINOR_VELOCITY * density) + 0.5f);
            this.mMaximumMajorVelocity = (int) ((MAXIMUM_MAJOR_VELOCITY * density) + 0.5f);
            this.mMaximumAcceleration = (int) ((MAXIMUM_ACCELERATION * density) + 0.5f);
            this.mVelocityUnits = (int) ((1000.0f * density) + 0.5f);
            if (this.mInvert) {
                this.mMaximumAcceleration = -this.mMaximumAcceleration;
                this.mMaximumMajorVelocity = -this.mMaximumMajorVelocity;
                this.mMaximumMinorVelocity = -this.mMaximumMinorVelocity;
            }
            a.recycle();
            setAlwaysDrawnWithCacheEnabled(false);
        }
    }

    protected void onFinishInflate() {
        this.mHandle = findViewById(this.mHandleId);
        if (this.mHandle == null) {
            throw new IllegalArgumentException("The handle attribute is must refer to an existing child.");
        }
        this.mHandle.setOnClickListener(new DrawerToggler());
        if (isOpened()) {
            this.mHandle.setContentDescription(getContext().getString(R.string.camera_accessibility_effect_drawer_close));
        } else {
            this.mHandle.setContentDescription(getContext().getString(R.string.camera_accessibility_effect_drawer_open));
        }
        this.mContent = findViewById(this.mContentId);
        if (this.mContent == null) {
            throw new IllegalArgumentException("The content attribute is must refer to an existing child.");
        }
        this.mContent.setVisibility(4);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthSpecMode == 0 || heightSpecMode == 0) {
            throw new RuntimeException("SlidingDrawer cannot have UNSPECIFIED dimensions");
        }
        View handle = this.mHandle;
        measureChild(handle, widthMeasureSpec, heightMeasureSpec);
        if (this.mVertical) {
            this.mContent.measure(MeasureSpec.makeMeasureSpec(widthSpecSize, 1073741824), MeasureSpec.makeMeasureSpec((heightSpecSize - handle.getMeasuredHeight()) - this.mTopOffset, 1073741824));
        } else {
            this.mContent.measure(MeasureSpec.makeMeasureSpec((widthSpecSize - handle.getMeasuredWidth()) - this.mTopOffset, 1073741824), MeasureSpec.makeMeasureSpec(heightSpecSize, 1073741824));
        }
        setMeasuredDimension(widthSpecSize, heightSpecSize);
    }

    protected void dispatchDraw(Canvas canvas) {
        float f = 0.0f;
        long drawingTime = getDrawingTime();
        View handle = this.mHandle;
        boolean isVertical = this.mVertical;
        drawChild(canvas, handle, drawingTime);
        if (this.mTracking || this.mAnimating) {
            Bitmap cache = this.mContent.getDrawingCache();
            if (cache == null) {
                canvas.save();
                if (this.mInvert) {
                    canvas.translate(isVertical ? 0.0f : (float) (((handle.getLeft() - getWidth()) + this.mTopOffset) + this.mHandleWidth), isVertical ? (float) (((handle.getTop() - getHeight()) + this.mTopOffset) + this.mHandleHeight) : 0.0f);
                } else {
                    float left = isVertical ? 0.0f : (float) (handle.getLeft() - this.mTopOffset);
                    if (isVertical) {
                        f = (float) (handle.getTop() - this.mTopOffset);
                    }
                    canvas.translate(left, f);
                }
                drawChild(canvas, this.mContent, drawingTime);
                canvas.restore();
            } else if (!isVertical) {
                canvas.drawBitmap(cache, this.mInvert ? (float) (handle.getLeft() - cache.getWidth()) : (float) handle.getRight(), 0.0f, null);
            } else if (this.mInvert) {
                canvas.drawBitmap(cache, 0.0f, (float) (handle.getTop() - cache.getHeight()), null);
            } else {
                canvas.drawBitmap(cache, 0.0f, (float) handle.getBottom(), null);
            }
            invalidate();
        } else if (this.mExpanded) {
            drawChild(canvas, this.mContent, drawingTime);
        }
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (!this.mTracking) {
            int handleLeft;
            int handleTop;
            int width = r - l;
            int height = b - t;
            View handle = this.mHandle;
            int handleWidth = handle.getMeasuredWidth();
            int handleHeight = handle.getMeasuredHeight();
            View content = this.mContent;
            if (this.mVertical) {
                handleLeft = (width - handleWidth) / ORIENTATION_LTR;
                if (this.mInvert) {
                    handleTop = this.mExpanded ? (height - this.mTopOffset) - handleHeight : -this.mBottomOffset;
                    content.layout(ORIENTATION_RTL, ((height - this.mTopOffset) - handleHeight) - content.getMeasuredHeight(), content.getMeasuredWidth(), (height - this.mTopOffset) - handleHeight);
                } else {
                    handleTop = this.mExpanded ? this.mTopOffset : (height - handleHeight) + this.mBottomOffset;
                    content.layout(ORIENTATION_RTL, this.mTopOffset + handleHeight, content.getMeasuredWidth(), (this.mTopOffset + handleHeight) + content.getMeasuredHeight());
                }
            } else {
                handleTop = (height - handleHeight) / ORIENTATION_LTR;
                if (this.mInvert) {
                    handleLeft = this.mExpanded ? (width - this.mTopOffset) - handleWidth : -this.mBottomOffset;
                    content.layout(((width - this.mTopOffset) - handleWidth) - content.getMeasuredWidth(), ORIENTATION_RTL, (width - this.mTopOffset) - handleWidth, content.getMeasuredHeight());
                } else {
                    handleLeft = this.mExpanded ? this.mTopOffset : (width - handleWidth) + this.mBottomOffset;
                    content.layout(this.mTopOffset + handleWidth, ORIENTATION_RTL, (this.mTopOffset + handleWidth) + content.getMeasuredWidth(), content.getMeasuredHeight());
                }
            }
            handle.layout(handleLeft, handleTop, handleLeft + handleWidth, handleTop + handleHeight);
            this.mHandleHeight = handle.getHeight();
            this.mHandleWidth = handle.getWidth();
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.mLocked) {
            return false;
        }
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        Rect frame = this.mFrame;
        View handle = this.mHandle;
        handle.getHitRect(frame);
        if (!this.mTracking && !frame.contains((int) x, (int) y)) {
            return false;
        }
        if (action == 0) {
            this.mTracking = true;
            handle.setPressed(true);
            prepareContent();
            if (this.mOnDrawerScrollListener != null) {
                this.mOnDrawerScrollListener.onScrollStarted();
            }
            if (this.mVertical) {
                int top = this.mHandle.getTop();
                this.mTouchDelta = ((int) y) - top;
                prepareTracking(top);
            } else {
                int left = this.mHandle.getLeft();
                this.mTouchDelta = ((int) x) - left;
                prepareTracking(left);
            }
            this.mVelocityTracker.addMovement(event);
        }
        return true;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r23) {
        /*
        r22 = this;
        r0 = r22;
        r0 = r0.mLocked;
        r17 = r0;
        if (r17 == 0) goto L_0x000b;
    L_0x0008:
        r17 = 1;
    L_0x000a:
        return r17;
    L_0x000b:
        r0 = r22;
        r0 = r0.mTracking;
        r17 = r0;
        if (r17 == 0) goto L_0x0027;
    L_0x0013:
        r0 = r22;
        r0 = r0.mVelocityTracker;
        r17 = r0;
        r0 = r17;
        r1 = r23;
        r0.addMovement(r1);
        r2 = r23.getAction();
        switch(r2) {
            case 1: goto L_0x0066;
            case 2: goto L_0x0040;
            case 3: goto L_0x0066;
            default: goto L_0x0027;
        };
    L_0x0027:
        r0 = r22;
        r0 = r0.mTracking;
        r17 = r0;
        if (r17 != 0) goto L_0x003d;
    L_0x002f:
        r0 = r22;
        r0 = r0.mAnimating;
        r17 = r0;
        if (r17 != 0) goto L_0x003d;
    L_0x0037:
        r17 = super.onTouchEvent(r23);
        if (r17 == 0) goto L_0x031e;
    L_0x003d:
        r17 = 1;
        goto L_0x000a;
    L_0x0040:
        r0 = r22;
        r0 = r0.mVertical;
        r17 = r0;
        if (r17 == 0) goto L_0x0061;
    L_0x0048:
        r17 = r23.getY();
    L_0x004c:
        r0 = r17;
        r0 = (int) r0;
        r17 = r0;
        r0 = r22;
        r0 = r0.mTouchDelta;
        r18 = r0;
        r17 = r17 - r18;
        r0 = r22;
        r1 = r17;
        r0.moveHandle(r1);
        goto L_0x0027;
    L_0x0061:
        r17 = r23.getX();
        goto L_0x004c;
    L_0x0066:
        r0 = r22;
        r13 = r0.mVelocityTracker;
        r0 = r22;
        r0 = r0.mVelocityUnits;
        r17 = r0;
        r0 = r17;
        r13.computeCurrentVelocity(r0);
        r16 = r13.getYVelocity();
        r15 = r13.getXVelocity();
        r0 = r22;
        r14 = r0.mVertical;
        if (r14 == 0) goto L_0x01e6;
    L_0x0083:
        r17 = 0;
        r17 = (r16 > r17 ? 1 : (r16 == r17 ? 0 : -1));
        if (r17 >= 0) goto L_0x01e3;
    L_0x0089:
        r11 = 1;
    L_0x008a:
        r17 = 0;
        r17 = (r15 > r17 ? 1 : (r15 == r17 ? 0 : -1));
        if (r17 >= 0) goto L_0x0091;
    L_0x0090:
        r15 = -r15;
    L_0x0091:
        r0 = r22;
        r0 = r0.mInvert;
        r17 = r0;
        if (r17 != 0) goto L_0x00a8;
    L_0x0099:
        r0 = r22;
        r0 = r0.mMaximumMinorVelocity;
        r17 = r0;
        r0 = r17;
        r0 = (float) r0;
        r17 = r0;
        r17 = (r15 > r17 ? 1 : (r15 == r17 ? 0 : -1));
        if (r17 > 0) goto L_0x00bf;
    L_0x00a8:
        r0 = r22;
        r0 = r0.mInvert;
        r17 = r0;
        if (r17 == 0) goto L_0x00c8;
    L_0x00b0:
        r0 = r22;
        r0 = r0.mMaximumMinorVelocity;
        r17 = r0;
        r0 = r17;
        r0 = (float) r0;
        r17 = r0;
        r17 = (r15 > r17 ? 1 : (r15 == r17 ? 0 : -1));
        if (r17 >= 0) goto L_0x00c8;
    L_0x00bf:
        r0 = r22;
        r0 = r0.mMaximumMinorVelocity;
        r17 = r0;
        r0 = r17;
        r15 = (float) r0;
    L_0x00c8:
        r0 = (double) r15;
        r18 = r0;
        r0 = r16;
        r0 = (double) r0;
        r20 = r0;
        r18 = java.lang.Math.hypot(r18, r20);
        r0 = r18;
        r12 = (float) r0;
        if (r11 == 0) goto L_0x00da;
    L_0x00d9:
        r12 = -r12;
    L_0x00da:
        r0 = r22;
        r0 = r0.mHandle;
        r17 = r0;
        r10 = r17.getTop();
        r0 = r22;
        r0 = r0.mHandle;
        r17 = r0;
        r8 = r17.getLeft();
        r0 = r22;
        r0 = r0.mHandle;
        r17 = r0;
        r7 = r17.getBottom();
        r0 = r22;
        r0 = r0.mHandle;
        r17 = r0;
        r9 = r17.getRight();
        r17 = java.lang.Math.abs(r12);
        r0 = r22;
        r0 = r0.mMaximumTapVelocity;
        r18 = r0;
        r0 = r18;
        r0 = (float) r0;
        r18 = r0;
        r17 = (r17 > r18 ? 1 : (r17 == r18 ? 0 : -1));
        if (r17 >= 0) goto L_0x030f;
    L_0x0115:
        r0 = r22;
        r0 = r0.mInvert;
        r17 = r0;
        if (r17 == 0) goto L_0x0241;
    L_0x011d:
        r0 = r22;
        r0 = r0.mExpanded;
        r17 = r0;
        if (r17 == 0) goto L_0x0235;
    L_0x0125:
        r17 = r22.getBottom();
        r18 = r22.getTop();
        r17 = r17 - r18;
        r0 = r22;
        r0 = r0.mTopOffset;
        r18 = r0;
        r17 = r17 - r18;
        r0 = r22;
        r0 = r0.mTapThreshold;
        r18 = r0;
        r17 = r17 - r18;
        r0 = r17;
        if (r7 <= r0) goto L_0x0235;
    L_0x0143:
        r3 = 1;
    L_0x0144:
        r0 = r22;
        r0 = r0.mExpanded;
        r17 = r0;
        if (r17 != 0) goto L_0x0238;
    L_0x014c:
        r0 = r22;
        r0 = r0.mBottomOffset;
        r17 = r0;
        r0 = r17;
        r0 = -r0;
        r17 = r0;
        r0 = r22;
        r0 = r0.mHandleHeight;
        r18 = r0;
        r17 = r17 + r18;
        r0 = r22;
        r0 = r0.mTapThreshold;
        r18 = r0;
        r17 = r17 + r18;
        r0 = r17;
        if (r7 >= r0) goto L_0x0238;
    L_0x016b:
        r4 = 1;
    L_0x016c:
        r0 = r22;
        r0 = r0.mExpanded;
        r17 = r0;
        if (r17 == 0) goto L_0x023b;
    L_0x0174:
        r17 = r22.getRight();
        r18 = r22.getLeft();
        r17 = r17 - r18;
        r0 = r22;
        r0 = r0.mTopOffset;
        r18 = r0;
        r17 = r17 - r18;
        r0 = r22;
        r0 = r0.mTapThreshold;
        r18 = r0;
        r17 = r17 - r18;
        r0 = r17;
        if (r9 <= r0) goto L_0x023b;
    L_0x0192:
        r5 = 1;
    L_0x0193:
        r0 = r22;
        r0 = r0.mExpanded;
        r17 = r0;
        if (r17 != 0) goto L_0x023e;
    L_0x019b:
        r0 = r22;
        r0 = r0.mBottomOffset;
        r17 = r0;
        r0 = r17;
        r0 = -r0;
        r17 = r0;
        r0 = r22;
        r0 = r0.mHandleWidth;
        r18 = r0;
        r17 = r17 + r18;
        r0 = r22;
        r0 = r0.mTapThreshold;
        r18 = r0;
        r17 = r17 + r18;
        r0 = r17;
        if (r9 >= r0) goto L_0x023e;
    L_0x01ba:
        r6 = 1;
    L_0x01bb:
        if (r14 == 0) goto L_0x02df;
    L_0x01bd:
        if (r3 != 0) goto L_0x01c1;
    L_0x01bf:
        if (r4 == 0) goto L_0x02e3;
    L_0x01c1:
        r0 = r22;
        r0 = r0.mAllowSingleTap;
        r17 = r0;
        if (r17 == 0) goto L_0x02fe;
    L_0x01c9:
        r17 = 0;
        r0 = r22;
        r1 = r17;
        r0.playSoundEffect(r1);
        r0 = r22;
        r0 = r0.mExpanded;
        r17 = r0;
        if (r17 == 0) goto L_0x02f3;
    L_0x01da:
        if (r14 == 0) goto L_0x02f0;
    L_0x01dc:
        r0 = r22;
        r0.animateClose(r10);
        goto L_0x0027;
    L_0x01e3:
        r11 = 0;
        goto L_0x008a;
    L_0x01e6:
        r17 = 0;
        r17 = (r15 > r17 ? 1 : (r15 == r17 ? 0 : -1));
        if (r17 >= 0) goto L_0x0233;
    L_0x01ec:
        r11 = 1;
    L_0x01ed:
        r17 = 0;
        r17 = (r16 > r17 ? 1 : (r16 == r17 ? 0 : -1));
        if (r17 >= 0) goto L_0x01f8;
    L_0x01f3:
        r0 = r16;
        r0 = -r0;
        r16 = r0;
    L_0x01f8:
        r0 = r22;
        r0 = r0.mInvert;
        r17 = r0;
        if (r17 != 0) goto L_0x020f;
    L_0x0200:
        r0 = r22;
        r0 = r0.mMaximumMinorVelocity;
        r17 = r0;
        r0 = r17;
        r0 = (float) r0;
        r17 = r0;
        r17 = (r16 > r17 ? 1 : (r16 == r17 ? 0 : -1));
        if (r17 > 0) goto L_0x0226;
    L_0x020f:
        r0 = r22;
        r0 = r0.mInvert;
        r17 = r0;
        if (r17 == 0) goto L_0x00c8;
    L_0x0217:
        r0 = r22;
        r0 = r0.mMaximumMinorVelocity;
        r17 = r0;
        r0 = r17;
        r0 = (float) r0;
        r17 = r0;
        r17 = (r16 > r17 ? 1 : (r16 == r17 ? 0 : -1));
        if (r17 >= 0) goto L_0x00c8;
    L_0x0226:
        r0 = r22;
        r0 = r0.mMaximumMinorVelocity;
        r17 = r0;
        r0 = r17;
        r0 = (float) r0;
        r16 = r0;
        goto L_0x00c8;
    L_0x0233:
        r11 = 0;
        goto L_0x01ed;
    L_0x0235:
        r3 = 0;
        goto L_0x0144;
    L_0x0238:
        r4 = 0;
        goto L_0x016c;
    L_0x023b:
        r5 = 0;
        goto L_0x0193;
    L_0x023e:
        r6 = 0;
        goto L_0x01bb;
    L_0x0241:
        r0 = r22;
        r0 = r0.mExpanded;
        r17 = r0;
        if (r17 == 0) goto L_0x02d7;
    L_0x0249:
        r0 = r22;
        r0 = r0.mTapThreshold;
        r17 = r0;
        r0 = r22;
        r0 = r0.mTopOffset;
        r18 = r0;
        r17 = r17 + r18;
        r0 = r17;
        if (r10 >= r0) goto L_0x02d7;
    L_0x025b:
        r3 = 1;
    L_0x025c:
        r0 = r22;
        r0 = r0.mExpanded;
        r17 = r0;
        if (r17 != 0) goto L_0x02d9;
    L_0x0264:
        r0 = r22;
        r0 = r0.mBottomOffset;
        r17 = r0;
        r18 = r22.getBottom();
        r17 = r17 + r18;
        r18 = r22.getTop();
        r17 = r17 - r18;
        r0 = r22;
        r0 = r0.mHandleHeight;
        r18 = r0;
        r17 = r17 - r18;
        r0 = r22;
        r0 = r0.mTapThreshold;
        r18 = r0;
        r17 = r17 - r18;
        r0 = r17;
        if (r10 <= r0) goto L_0x02d9;
    L_0x028a:
        r4 = 1;
    L_0x028b:
        r0 = r22;
        r0 = r0.mExpanded;
        r17 = r0;
        if (r17 == 0) goto L_0x02db;
    L_0x0293:
        r0 = r22;
        r0 = r0.mTapThreshold;
        r17 = r0;
        r0 = r22;
        r0 = r0.mTopOffset;
        r18 = r0;
        r17 = r17 + r18;
        r0 = r17;
        if (r8 >= r0) goto L_0x02db;
    L_0x02a5:
        r5 = 1;
    L_0x02a6:
        r0 = r22;
        r0 = r0.mExpanded;
        r17 = r0;
        if (r17 != 0) goto L_0x02dd;
    L_0x02ae:
        r0 = r22;
        r0 = r0.mBottomOffset;
        r17 = r0;
        r18 = r22.getRight();
        r17 = r17 + r18;
        r18 = r22.getLeft();
        r17 = r17 - r18;
        r0 = r22;
        r0 = r0.mHandleWidth;
        r18 = r0;
        r17 = r17 - r18;
        r0 = r22;
        r0 = r0.mTapThreshold;
        r18 = r0;
        r17 = r17 - r18;
        r0 = r17;
        if (r8 <= r0) goto L_0x02dd;
    L_0x02d4:
        r6 = 1;
    L_0x02d5:
        goto L_0x01bb;
    L_0x02d7:
        r3 = 0;
        goto L_0x025c;
    L_0x02d9:
        r4 = 0;
        goto L_0x028b;
    L_0x02db:
        r5 = 0;
        goto L_0x02a6;
    L_0x02dd:
        r6 = 0;
        goto L_0x02d5;
    L_0x02df:
        if (r5 != 0) goto L_0x01c1;
    L_0x02e1:
        if (r6 != 0) goto L_0x01c1;
    L_0x02e3:
        if (r14 == 0) goto L_0x030d;
    L_0x02e5:
        r17 = 0;
        r0 = r22;
        r1 = r17;
        r0.performFling(r10, r12, r1);
        goto L_0x0027;
    L_0x02f0:
        r10 = r8;
        goto L_0x01dc;
    L_0x02f3:
        if (r14 == 0) goto L_0x02fc;
    L_0x02f5:
        r0 = r22;
        r0.animateOpen(r10);
        goto L_0x0027;
    L_0x02fc:
        r10 = r8;
        goto L_0x02f5;
    L_0x02fe:
        if (r14 == 0) goto L_0x030b;
    L_0x0300:
        r17 = 0;
        r0 = r22;
        r1 = r17;
        r0.performFling(r10, r12, r1);
        goto L_0x0027;
    L_0x030b:
        r10 = r8;
        goto L_0x0300;
    L_0x030d:
        r10 = r8;
        goto L_0x02e5;
    L_0x030f:
        if (r14 == 0) goto L_0x031c;
    L_0x0311:
        r17 = 0;
        r0 = r22;
        r1 = r17;
        r0.performFling(r10, r12, r1);
        goto L_0x0027;
    L_0x031c:
        r10 = r8;
        goto L_0x0311;
    L_0x031e:
        r17 = 0;
        goto L_0x000a;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.camera.components.MultiDirectionSlidingDrawer.onTouchEvent(android.view.MotionEvent):boolean");
    }

    private void animateClose(int position) {
        prepareTracking(position);
        performFling(position, (float) this.mMaximumAcceleration, true);
    }

    private void animateOpen(int position) {
        prepareTracking(position);
        performFling(position, (float) (-this.mMaximumAcceleration), true);
    }

    private void performFling(int position, float velocity, boolean always) {
        this.mAnimationPosition = (float) position;
        this.mAnimatedVelocity = velocity;
        boolean c1;
        boolean c2;
        boolean c3;
        if (this.mExpanded) {
            int bottom = this.mVertical ? getBottom() : getRight();
            int handleHeight = this.mVertical ? this.mHandleHeight : this.mHandleWidth;
            c1 = this.mInvert ? velocity < ((float) (-this.mMaximumMajorVelocity)) : velocity > ((float) this.mMaximumMajorVelocity);
            if (this.mInvert) {
                c2 = (bottom - (position + handleHeight)) + this.mBottomOffset > handleHeight;
            } else {
                c2 = position > (this.mVertical ? this.mHandleHeight : this.mHandleWidth) + this.mTopOffset;
            }
            c3 = this.mInvert ? velocity < ((float) this.mMaximumMajorVelocity) : velocity > ((float) (-this.mMaximumMajorVelocity));
            if (always || c1 || (c2 && c3)) {
                this.mAnimatedAcceleration = (float) this.mMaximumAcceleration;
                if (this.mInvert) {
                    if (velocity > 0.0f) {
                        this.mAnimatedVelocity = 0.0f;
                    }
                } else if (velocity < 0.0f) {
                    this.mAnimatedVelocity = 0.0f;
                }
            } else {
                this.mAnimatedAcceleration = (float) (-this.mMaximumAcceleration);
                if (this.mInvert) {
                    if (velocity < 0.0f) {
                        this.mAnimatedVelocity = 0.0f;
                    }
                } else if (velocity > 0.0f) {
                    this.mAnimatedVelocity = 0.0f;
                }
            }
        } else {
            c1 = this.mInvert ? velocity < ((float) (-this.mMaximumMajorVelocity)) : velocity > ((float) this.mMaximumMajorVelocity);
            if (this.mInvert) {
                c2 = position < (this.mVertical ? getHeight() : getWidth()) / ORIENTATION_LTR;
            } else {
                c2 = position > (this.mVertical ? getHeight() : getWidth()) / ORIENTATION_LTR;
            }
            c3 = this.mInvert ? velocity < ((float) this.mMaximumMajorVelocity) : velocity > ((float) (-this.mMaximumMajorVelocity));
            boolean c4 = this.mInvert ? position >= (getWidth() - this.mTopOffset) - this.mHandleWidth : position <= (getHeight() - this.mTopOffset) - this.mHandleHeight;
            if (always || c4 || !(c1 || (c2 && c3))) {
                this.mAnimatedAcceleration = (float) (-this.mMaximumAcceleration);
                if (this.mInvert) {
                    if (velocity < 0.0f) {
                        this.mAnimatedVelocity = 0.0f;
                    }
                } else if (velocity > 0.0f) {
                    this.mAnimatedVelocity = 0.0f;
                }
            } else {
                this.mAnimatedAcceleration = (float) this.mMaximumAcceleration;
                if (this.mInvert) {
                    if (velocity > 0.0f) {
                        this.mAnimatedVelocity = 0.0f;
                    }
                } else if (velocity < 0.0f) {
                    this.mAnimatedVelocity = 0.0f;
                }
            }
        }
        long now = SystemClock.uptimeMillis();
        this.mAnimationLastTime = now;
        this.mCurrentAnimationTime = 16 + now;
        this.mAnimating = true;
        this.mHandler.removeMessages(VELOCITY_UNITS);
        this.mHandler.sendMessageAtTime(this.mHandler.obtainMessage(VELOCITY_UNITS), this.mCurrentAnimationTime);
        stopTracking();
    }

    private void prepareTracking(int position) {
        boolean opening;
        this.mTracking = true;
        this.mVelocityTracker = VelocityTracker.obtain();
        if (this.mExpanded) {
            opening = false;
        } else {
            opening = true;
        }
        if (opening) {
            this.mAnimatedAcceleration = (float) this.mMaximumAcceleration;
            this.mAnimatedVelocity = (float) this.mMaximumMajorVelocity;
            if (this.mInvert) {
                this.mAnimationPosition = (float) (-this.mBottomOffset);
            } else {
                this.mAnimationPosition = (float) ((this.mVertical ? getHeight() - this.mHandleHeight : getWidth() - this.mHandleWidth) + this.mBottomOffset);
            }
            moveHandle((int) this.mAnimationPosition);
            this.mAnimating = true;
            this.mHandler.removeMessages(VELOCITY_UNITS);
            long now = SystemClock.uptimeMillis();
            this.mAnimationLastTime = now;
            this.mCurrentAnimationTime = 16 + now;
            this.mAnimating = true;
            return;
        }
        if (this.mAnimating) {
            this.mAnimating = false;
            this.mHandler.removeMessages(VELOCITY_UNITS);
        }
        moveHandle(position);
    }

    private void moveHandle(int position) {
        View handle = this.mHandle;
        Rect frame;
        Rect region;
        if (this.mVertical) {
            if (position == EXPANDED_FULL_OPEN) {
                if (this.mInvert) {
                    handle.offsetTopAndBottom(((((-this.mTopOffset) + getBottom()) - getTop()) - this.mHandleHeight) - handle.getTop());
                } else {
                    handle.offsetTopAndBottom(this.mTopOffset - handle.getTop());
                }
                invalidate();
            } else if (position == COLLAPSED_FULL_CLOSED) {
                if (this.mInvert) {
                    handle.offsetTopAndBottom((-this.mBottomOffset) - handle.getTop());
                } else {
                    handle.offsetTopAndBottom((((this.mBottomOffset + getBottom()) - getTop()) - this.mHandleHeight) - handle.getTop());
                }
                invalidate();
            } else {
                int top = handle.getTop();
                int deltaY = position - top;
                if (this.mInvert) {
                    if (position > (getBottom() - this.mTopOffset) - this.mHandleHeight) {
                        deltaY = ((getBottom() - this.mTopOffset) - this.mHandleHeight) - top;
                    } else if (deltaY < (-this.mBottomOffset) - top) {
                        deltaY = (-this.mBottomOffset) - top;
                    }
                } else if (position < this.mTopOffset) {
                    deltaY = this.mTopOffset - top;
                } else if (deltaY > (((this.mBottomOffset + getBottom()) - getTop()) - this.mHandleHeight) - top) {
                    deltaY = (((this.mBottomOffset + getBottom()) - getTop()) - this.mHandleHeight) - top;
                }
                handle.offsetTopAndBottom(deltaY);
                frame = this.mFrame;
                region = this.mInvalidate;
                handle.getHitRect(frame);
                region.set(frame);
                region.union(frame.left, frame.top - deltaY, frame.right, frame.bottom - deltaY);
                region.union(ORIENTATION_RTL, frame.bottom - deltaY, getWidth(), (frame.bottom - deltaY) + this.mContent.getHeight());
                invalidate(region);
            }
        } else if (position == EXPANDED_FULL_OPEN) {
            if (this.mInvert) {
                handle.offsetLeftAndRight(((((-this.mTopOffset) + getRight()) - getLeft()) - this.mHandleWidth) - handle.getLeft());
            } else {
                handle.offsetLeftAndRight(this.mTopOffset - handle.getLeft());
            }
            invalidate();
        } else if (position == COLLAPSED_FULL_CLOSED) {
            if (this.mInvert) {
                handle.offsetLeftAndRight((-this.mBottomOffset) - handle.getLeft());
            } else {
                handle.offsetLeftAndRight((((this.mBottomOffset + getRight()) - getLeft()) - this.mHandleWidth) - handle.getLeft());
            }
            invalidate();
        } else {
            int left = handle.getLeft();
            int deltaX = position - left;
            if (this.mInvert) {
                if (position > (getRight() - this.mTopOffset) - this.mHandleWidth) {
                    deltaX = ((getRight() - this.mTopOffset) - this.mHandleWidth) - left;
                } else if (deltaX < (-this.mBottomOffset) - left) {
                    deltaX = (-this.mBottomOffset) - left;
                }
            } else if (position < this.mTopOffset) {
                deltaX = this.mTopOffset - left;
            } else if (deltaX > (((this.mBottomOffset + getRight()) - getLeft()) - this.mHandleWidth) - left) {
                deltaX = (((this.mBottomOffset + getRight()) - getLeft()) - this.mHandleWidth) - left;
            }
            handle.offsetLeftAndRight(deltaX);
            frame = this.mFrame;
            region = this.mInvalidate;
            handle.getHitRect(frame);
            region.set(frame);
            region.union(frame.left - deltaX, frame.top, frame.right - deltaX, frame.bottom);
            region.union(frame.right - deltaX, ORIENTATION_RTL, (frame.right - deltaX) + this.mContent.getWidth(), getHeight());
            invalidate(region);
        }
    }

    private void prepareContent() {
        if (!this.mAnimating) {
            View content = this.mContent;
            if (content.isLayoutRequested()) {
                if (this.mVertical) {
                    int handleHeight = this.mHandleHeight;
                    content.measure(MeasureSpec.makeMeasureSpec(getRight() - getLeft(), 1073741824), MeasureSpec.makeMeasureSpec(((getBottom() - getTop()) - handleHeight) - this.mTopOffset, 1073741824));
                    if (this.mInvert) {
                        content.layout(ORIENTATION_RTL, this.mTopOffset, content.getMeasuredWidth(), this.mTopOffset + content.getMeasuredHeight());
                    } else {
                        content.layout(ORIENTATION_RTL, this.mTopOffset + handleHeight, content.getMeasuredWidth(), (this.mTopOffset + handleHeight) + content.getMeasuredHeight());
                    }
                } else {
                    int handleWidth = this.mHandle.getWidth();
                    content.measure(MeasureSpec.makeMeasureSpec(((getRight() - getLeft()) - handleWidth) - this.mTopOffset, 1073741824), MeasureSpec.makeMeasureSpec(getBottom() - getTop(), 1073741824));
                    if (this.mInvert) {
                        content.layout(this.mTopOffset, ORIENTATION_RTL, this.mTopOffset + content.getMeasuredWidth(), content.getMeasuredHeight());
                    } else {
                        content.layout(this.mTopOffset + handleWidth, ORIENTATION_RTL, (this.mTopOffset + handleWidth) + content.getMeasuredWidth(), content.getMeasuredHeight());
                    }
                }
            }
            content.getViewTreeObserver().dispatchOnPreDraw();
            if (content.getVisibility() != 4) {
                content.buildDrawingCache();
            }
            content.setVisibility(4);
        }
    }

    private void stopTracking() {
        this.mHandle.setPressed(false);
        this.mTracking = false;
        if (this.mOnDrawerScrollListener != null) {
            this.mOnDrawerScrollListener.onScrollEnded();
        }
        if (this.mVelocityTracker != null) {
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    private void doAnimation() {
        if (this.mAnimating) {
            incrementAnimation();
            if (!this.mInvert) {
                if (this.mAnimationPosition >= ((float) (((this.mVertical ? getHeight() : getWidth()) + this.mBottomOffset) - 1))) {
                    this.mAnimating = false;
                    closeDrawer();
                } else if (this.mAnimationPosition < ((float) this.mTopOffset)) {
                    this.mAnimating = false;
                    openDrawer();
                } else {
                    moveHandle((int) this.mAnimationPosition);
                    this.mCurrentAnimationTime += 16;
                    this.mHandler.sendMessageAtTime(this.mHandler.obtainMessage(VELOCITY_UNITS), this.mCurrentAnimationTime);
                }
            } else if (this.mAnimationPosition <= ((float) ((-this.mBottomOffset) + ORIENTATION_BTT))) {
                this.mAnimating = false;
                closeDrawer();
            } else {
                if (this.mAnimationPosition > ((float) ((this.mVertical ? getHeight() - this.mHandleHeight : getWidth() - this.mHandleWidth) + (-this.mTopOffset)))) {
                    this.mAnimating = false;
                    openDrawer();
                    return;
                }
                moveHandle((int) this.mAnimationPosition);
                this.mCurrentAnimationTime += 16;
                this.mHandler.sendMessageAtTime(this.mHandler.obtainMessage(VELOCITY_UNITS), this.mCurrentAnimationTime);
            }
        }
    }

    private void incrementAnimation() {
        long now = SystemClock.uptimeMillis();
        float t = ((float) (now - this.mAnimationLastTime)) / 1000.0f;
        float position = this.mAnimationPosition;
        float v = this.mAnimatedVelocity;
        float a = this.mInvert ? this.mAnimatedAcceleration : this.mAnimatedAcceleration;
        this.mAnimationPosition = ((v * t) + position) + (((0.5f * a) * t) * t);
        this.mAnimatedVelocity = (a * t) + v;
        this.mAnimationLastTime = now;
    }

    public void toggle() {
        if (this.mExpanded) {
            closeDrawer();
        } else {
            openDrawer();
        }
        invalidate();
        requestLayout();
    }

    public void animateToggle() {
        if (this.mExpanded) {
            animateClose();
        } else {
            animateOpen();
        }
    }

    public void open() {
        openDrawer();
        invalidate();
        requestLayout();
        sendAccessibilityEvent(32);
    }

    public void close() {
        closeDrawer();
        invalidate();
        requestLayout();
    }

    public void animateClose() {
        prepareContent();
        OnDrawerScrollListener scrollListener = this.mOnDrawerScrollListener;
        if (scrollListener != null) {
            scrollListener.onScrollStarted();
        }
        animateClose(this.mVertical ? this.mHandle.getTop() : this.mHandle.getLeft());
        if (scrollListener != null) {
            scrollListener.onScrollEnded();
        }
    }

    public void animateOpen() {
        prepareContent();
        OnDrawerScrollListener scrollListener = this.mOnDrawerScrollListener;
        if (scrollListener != null) {
            scrollListener.onScrollStarted();
        }
        animateOpen(this.mVertical ? this.mHandle.getTop() : this.mHandle.getLeft());
        sendAccessibilityEvent(32);
        if (scrollListener != null) {
            scrollListener.onScrollEnded();
        }
    }

    private void closeDrawer() {
        moveHandle(COLLAPSED_FULL_CLOSED);
        this.mContent.setVisibility(4);
        this.mContent.destroyDrawingCache();
        if (this.mExpanded) {
            this.mExpanded = false;
            this.mHandle.setContentDescription(getContext().getString(R.string.camera_accessibility_effect_drawer_open));
            if (this.mOnDrawerCloseListener != null) {
                this.mOnDrawerCloseListener.onDrawerClosed();
            }
        }
    }

    private void openDrawer() {
        moveHandle(EXPANDED_FULL_OPEN);
        this.mContent.setVisibility(ORIENTATION_RTL);
        if (!this.mExpanded) {
            this.mExpanded = true;
            this.mHandle.setContentDescription(getContext().getString(R.string.camera_accessibility_effect_drawer_close));
            if (this.mOnDrawerOpenListener != null) {
                this.mOnDrawerOpenListener.onDrawerOpened();
            }
        }
    }

    public void setOnDrawerOpenListener(OnDrawerOpenListener onDrawerOpenListener) {
        this.mOnDrawerOpenListener = onDrawerOpenListener;
    }

    public void setOnDrawerCloseListener(OnDrawerCloseListener onDrawerCloseListener) {
        this.mOnDrawerCloseListener = onDrawerCloseListener;
    }

    public void setOnDrawerScrollListener(OnDrawerScrollListener onDrawerScrollListener) {
        this.mOnDrawerScrollListener = onDrawerScrollListener;
    }

    public View getHandle() {
        return this.mHandle;
    }

    public View getContent() {
        return this.mContent;
    }

    public void unlock() {
        this.mLocked = false;
    }

    public void lock() {
        this.mLocked = true;
    }

    public boolean isOpened() {
        return this.mExpanded;
    }

    public boolean isMoving() {
        return this.mTracking || this.mAnimating;
    }
}
