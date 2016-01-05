package com.lge.camera.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import com.lge.camera.R;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.define.Ola_ShotParam;
import com.lge.olaworks.library.BaseEngine;
import com.lge.olaworks.library.FaceDetector;
import com.lge.voiceshutter.library.LGKeyRec;

public abstract class RotateView extends ImageButton {
    public static final float BASE_TEXT_SCALE_X_RATE = 0.1f;
    protected static final boolean DEBUG_ON = false;
    public static final float DEFAULT_TEXT_SCALE_X = 1.0f;
    public static final float DEFAULT_TEXT_SHADOWRADIUS = 2.0f;
    public static final int DEFAULT_TEXT_SIZE = 20;
    public static final String ELLIPSIS_STRING = "...";
    public static final int GRAVITY_BOTTOM = 4;
    public static final int GRAVITY_CENTER = 17;
    public static final int GRAVITY_CENTER_HORIZONTAL = 16;
    public static final int GRAVITY_CENTER_VERTICAL = 1;
    public static final int GRAVITY_LEFT = 32;
    public static final int GRAVITY_RIGHT = 64;
    public static final int GRAVITY_TOP = 2;
    public static final int MAX_TEXT_LINES = 5;
    public static final int PIVOT_CENTER = Integer.MAX_VALUE;
    public static final float PORTRAIT_TEXT_WIDTH_CORRECTION_RATE = 0.09f;
    protected float BASE_TEXT_PADDING_RATE;
    protected Paint mDebugPaint;
    protected boolean mEllipsisEnabled;
    protected int mExpand4Rotate;
    protected String[] mLandscapeTextLines;
    protected Path[] mPath;
    protected String[] mPortraitTextLines;
    protected boolean mRotateIconOnly;
    protected boolean mRotateInsideView;
    protected int mRotatePivotLeft;
    protected int mRotatePivotTop;
    protected RotationInfo mRotationInfo;
    protected String mText;
    protected float mTextBasePadding;
    protected StringBuffer mTextBuffer;
    protected int mTextColor;
    protected int mTextDisabledShadowColor;
    protected int mTextGravity;
    protected String[] mTextLines;
    protected int mTextPaddingBottom;
    protected int mTextPaddingLeft;
    protected int mTextPaddingRight;
    protected int mTextPaddingTop;
    protected Paint mTextPaint;
    protected float mTextScaleX;
    protected int mTextShadowColor;
    protected float mTextShadowRadius;
    protected int mTextSize;

    protected abstract void canvasRotate(Canvas canvas, int i, int i2);

    protected abstract boolean checkBackground(Canvas canvas);

    public abstract int getTextPaintWidth();

    public abstract void setRotated(int i);

    public RotateView(Context context) {
        this(context, null);
    }

    public RotateView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RotateView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mExpand4Rotate = 0;
        this.BASE_TEXT_PADDING_RATE = BASE_TEXT_SCALE_X_RATE;
        this.mTextSize = DEFAULT_TEXT_SIZE;
        this.mTextColor = -3355444;
        this.mTextPaddingTop = 0;
        this.mTextPaddingBottom = 0;
        this.mTextPaddingLeft = 0;
        this.mTextPaddingRight = 0;
        this.mTextGravity = GRAVITY_CENTER;
        this.mTextShadowColor = -16777216;
        this.mTextDisabledShadowColor = -12303292;
        this.mTextShadowRadius = DEFAULT_TEXT_SHADOWRADIUS;
        this.mText = null;
        this.mTextLines = null;
        this.mLandscapeTextLines = null;
        this.mPortraitTextLines = null;
        this.mTextBuffer = new StringBuffer();
        this.mPath = new Path[MAX_TEXT_LINES];
        this.mRotatePivotLeft = PIVOT_CENTER;
        this.mRotatePivotTop = PIVOT_CENTER;
        this.mTextPaint = new Paint();
        this.mEllipsisEnabled = DEBUG_ON;
        this.mTextScaleX = DEFAULT_TEXT_SCALE_X;
        this.mRotateInsideView = DEBUG_ON;
        this.mRotateIconOnly = DEBUG_ON;
        this.mRotationInfo = new RotationInfo();
        this.mDebugPaint = null;
        setFocusable(DEBUG_ON);
        RotateViewInit(getContext().obtainStyledAttributes(attrs, R.styleable.Rotatable));
    }

    private void RotateViewInit(TypedArray ta) {
        int N = ta.getIndexCount();
        for (int i = 0; i < N; i += GRAVITY_CENTER_VERTICAL) {
            int attr = ta.getIndex(i);
            switch (attr) {
                case LGKeyRec.EVENT_INVALID /*0*/:
                    setBackground(ta.getDrawable(attr));
                    break;
                case GRAVITY_CENTER_VERTICAL /*1*/:
                    String temp = ta.getString(attr);
                    if (temp == null) {
                        break;
                    }
                    setText(temp);
                    break;
                case GRAVITY_TOP /*2*/:
                    this.mTextSize = ta.getDimensionPixelSize(attr, DEFAULT_TEXT_SIZE);
                    break;
                case LGKeyRec.EVENT_STARTED /*3*/:
                    this.mTextColor = ta.getInt(attr, -3355444);
                    break;
                case GRAVITY_BOTTOM /*4*/:
                    this.mTextPaddingTop = ta.getDimensionPixelOffset(attr, 0);
                    break;
                case MAX_TEXT_LINES /*5*/:
                    this.mTextPaddingBottom = ta.getDimensionPixelOffset(attr, 0);
                    break;
                case LGKeyRec.EVENT_RECOGNITION_RESULT /*6*/:
                    this.mTextPaddingLeft = ta.getDimensionPixelOffset(attr, 0);
                    break;
                case LGKeyRec.EVENT_RECOGNITION_TIMEOUT /*7*/:
                    this.mTextPaddingRight = ta.getDimensionPixelOffset(attr, 0);
                    break;
                case LGKeyRec.EVENT_NEED_MORE_AUDIO /*8*/:
                    this.mTextGravity = ta.getInt(attr, GRAVITY_CENTER);
                    break;
                case LGKeyRec.EVENT_MAX_SPEECH /*9*/:
                    this.mTextShadowColor = ta.getInt(attr, -16777216);
                    break;
                case BaseEngine.DEFAULT_PRIORITY /*10*/:
                    this.mTextShadowRadius = ta.getFloat(attr, DEFAULT_TEXT_SHADOWRADIUS);
                    break;
                case Ola_ShotParam.ImageEffect_MotionBlur /*11*/:
                    this.mRotateInsideView = ta.getBoolean(attr, DEBUG_ON);
                    break;
                case Ola_ShotParam.ImageEffect_Solarize /*12*/:
                    this.mRotateIconOnly = ta.getBoolean(attr, DEBUG_ON);
                    break;
                case Ola_ShotParam.ImageEffect_Glow /*13*/:
                    this.mRotatePivotLeft = ta.getDimensionPixelOffset(attr, PIVOT_CENTER);
                    break;
                case Ola_ShotParam.ImageEffect_Vivid /*14*/:
                    this.mRotatePivotTop = ta.getDimensionPixelOffset(attr, PIVOT_CENTER);
                    break;
                case Ola_ShotParam.ImageEffect_AutoWB /*15*/:
                    this.mEllipsisEnabled = ta.getBoolean(attr, DEBUG_ON);
                    break;
                default:
                    CamLog.w(FaceDetector.TAG, "No matched attr");
                    break;
            }
        }
        this.mTextBasePadding = ((float) this.mTextSize) * this.BASE_TEXT_PADDING_RATE;
    }

    public String getText() {
        return this.mText;
    }

    public void setText(String string) {
        if (string == null) {
            this.mText = "";
        } else {
            this.mText = string;
        }
        this.mTextLines = this.mText.split("\n");
        for (int i = 0; i < this.mTextLines.length; i += GRAVITY_CENTER_VERTICAL) {
            if (this.mPath[i] == null) {
                this.mPath[i] = new Path();
            }
        }
        setTextPaint();
        this.mLandscapeTextLines = this.mTextLines;
        requestLayout();
        invalidate();
    }

    public void AdjustFontSize() {
        int textSize = AdjustFontSize(this.mTextSize);
        if (textSize != this.mTextSize) {
            this.mTextSize = textSize;
            this.mTextBasePadding = ((float) this.mTextSize) * this.BASE_TEXT_PADDING_RATE;
            invalidate();
        }
    }

    public int AdjustFontSize(int size) {
        if (this.mTextPaint != null) {
            Rect targetBound = new Rect();
            Rect sourceBound = new Rect();
            LayoutParams layoutParams = getLayoutParams();
            float paddingTop = ((float) this.mTextPaddingTop) + this.mTextBasePadding;
            float paddingLeft = ((float) this.mTextPaddingLeft) + this.mTextBasePadding;
            float paddingBottom = ((float) this.mTextPaddingBottom) + this.mTextBasePadding;
            float paddingRight = ((float) this.mTextPaddingRight) + this.mTextBasePadding;
            targetBound.left = 0;
            targetBound.top = 0;
            targetBound.right = (int) (((float) layoutParams.width) - (paddingLeft + paddingRight));
            targetBound.bottom = (int) (((float) layoutParams.height) - (paddingTop + paddingBottom));
            int maxLength = 0;
            String maxString = null;
            String[] arr$ = this.mTextLines;
            int len$ = arr$.length;
            for (int i$ = 0; i$ < len$; i$ += GRAVITY_CENTER_VERTICAL) {
                String strLine = arr$[i$];
                if (strLine.length() > maxLength) {
                    maxString = strLine;
                    maxLength = strLine.length();
                }
            }
            if (maxString != null && targetBound.width() > 0 && targetBound.height() > 0) {
                int tmpSize = size;
                int tmpScaleX = 10;
                this.mTextScaleX = this.mTextPaint.getTextScaleX();
                this.mTextPaint.setTextSize((float) tmpSize);
                this.mTextPaint.setTextScaleX(this.mTextScaleX);
                this.mTextPaint.getTextBounds(maxString, 0, maxLength, sourceBound);
                sourceBound.offsetTo(0, 0);
                sourceBound.right = (int) (((float) sourceBound.right) + (((float) sourceBound.width()) * this.BASE_TEXT_PADDING_RATE));
                while (!targetBound.contains(sourceBound) && tmpScaleX >= 7) {
                    tmpScaleX--;
                    this.mTextPaint.setTextScaleX(((float) tmpScaleX) * BASE_TEXT_SCALE_X_RATE);
                    this.mTextPaint.getTextBounds(maxString, 0, maxLength, sourceBound);
                    sourceBound.offsetTo(0, 0);
                    this.mTextScaleX = ((float) tmpScaleX) * BASE_TEXT_SCALE_X_RATE;
                }
                while (!targetBound.contains(sourceBound)) {
                    tmpSize--;
                    this.mTextPaint.setTextSize((float) tmpSize);
                    this.mTextPaint.getTextBounds(maxString, 0, maxLength, sourceBound);
                    sourceBound.offsetTo(0, 0);
                }
                if (tmpSize > 0) {
                    size = tmpSize;
                }
            }
        }
        return size;
    }

    public void setTextSize(int size) {
        this.mTextSize = AdjustFontSize(size);
        this.mTextBasePadding = ((float) this.mTextSize) * this.BASE_TEXT_PADDING_RATE;
        invalidate();
    }

    public void setTextColor(int color) {
        this.mTextColor = color;
        invalidate();
    }

    public void setTextGravity(int gravity) {
        this.mTextGravity = gravity;
        invalidate();
    }

    public void setTextShadowColor(int color) {
        this.mTextShadowColor = color;
        invalidate();
    }

    public void setTextShadowRadius(float radius) {
        this.mTextShadowRadius = radius;
        invalidate();
    }

    public void setRotateInsideView(boolean enable) {
        this.mRotateInsideView = enable;
        invalidate();
    }

    public void setDegree(int degree) {
        setDegree(degree, true);
    }

    public void setDegree(int degree, boolean animation) {
        if (this.mRotationInfo != null) {
            this.mRotationInfo.setDegree(degree, animation);
        }
        invalidate();
    }

    public int getDegree() {
        if (this.mRotationInfo != null) {
            return this.mRotationInfo.getTargetDegree();
        }
        return 0;
    }

    protected void onDraw(Canvas canvas) {
        try {
            if (checkBackground(canvas)) {
                if (!(this.mRotationInfo == null || this.mRotationInfo.getCurrentDegree() == this.mRotationInfo.getTargetDegree() || !this.mRotationInfo.calcCurrentDegree())) {
                    invalidate();
                }
                int viewWidth = getWidth();
                int viewHeight = getHeight();
                canvasRotate(canvas, viewWidth, viewHeight);
                super.onDraw(canvas);
                if (this.mText != null) {
                    drawText(canvas, viewWidth, viewHeight);
                }
            }
        } catch (Exception e) {
            String str = FaceDetector.TAG;
            Object[] objArr = new Object[GRAVITY_CENTER_VERTICAL];
            objArr[0] = e;
            CamLog.e(str, String.format("RotateImageButton onDraw exception: %s", objArr));
            e.printStackTrace();
        }
    }

    private void drawText(Canvas canvas, int viewWidth, int viewHeight) {
        setTextPaint();
        float paddingTop = ((float) this.mTextPaddingTop) + this.mTextBasePadding;
        float paddingLeft = ((float) this.mTextPaddingLeft) + this.mTextBasePadding;
        float paddingBottom = ((float) this.mTextPaddingBottom) + this.mTextBasePadding;
        float paddingRight = ((float) this.mTextPaddingRight) + this.mTextBasePadding;
        float textHeight = this.mTextPaint.getFontSpacing();
        float textAreaHeight = (((float) viewHeight) - paddingTop) - paddingBottom;
        float textAreaWidth = (((float) viewWidth) - paddingLeft) - paddingRight;
        float totalTextLineHeight = textHeight * ((float) this.mTextLines.length);
        float aboveBaseLine = -this.mTextPaint.ascent();
        float belowBaseLine = this.mTextPaint.descent();
        int i = 0;
        while (true) {
            int length = this.mTextLines.length;
            if (i < r0) {
                float textOffsetY;
                float textOffsetX;
                float preventClippingMargin;
                float textWidth = this.mTextPaint.measureText(this.mTextLines[i]);
                this.mTextBuffer.setLength(0);
                if (this.mEllipsisEnabled) {
                    if (((float) viewWidth) < (textWidth + paddingLeft) + paddingRight) {
                        float maxWidth = (((float) viewWidth) - paddingLeft) - paddingRight;
                        int length2 = this.mTextPaint.breakText(this.mTextLines[i], true, maxWidth, null);
                        if (length2 > GRAVITY_TOP) {
                            this.mTextBuffer.append(this.mTextLines[i].substring(0, length2 - 2));
                            this.mTextBuffer.append(ELLIPSIS_STRING);
                            textWidth = this.mTextPaint.measureText(this.mTextBuffer.toString());
                        }
                        if ((this.mTextGravity & GRAVITY_TOP) == 0) {
                            textOffsetY = (paddingTop + aboveBaseLine) + (((float) i) * textHeight);
                        } else {
                            if ((this.mTextGravity & GRAVITY_BOTTOM) == 0) {
                                textOffsetY = ((((float) viewHeight) - paddingBottom) - belowBaseLine) - (((float) ((this.mTextLines.length - 1) - i)) * textHeight);
                            } else {
                                textOffsetY = ((((textAreaHeight - totalTextLineHeight) / DEFAULT_TEXT_SHADOWRADIUS) + paddingTop) + aboveBaseLine) + (((float) i) * textHeight);
                            }
                        }
                        if ((this.mTextGravity & GRAVITY_LEFT) == 0) {
                            textOffsetX = paddingLeft;
                        } else {
                            if ((this.mTextGravity & GRAVITY_RIGHT) == 0) {
                                textOffsetX = (((float) viewWidth) - textWidth) - paddingRight;
                            } else {
                                textOffsetX = ((textAreaWidth - textWidth) / DEFAULT_TEXT_SHADOWRADIUS) + paddingLeft;
                            }
                        }
                        preventClippingMargin = textWidth * this.BASE_TEXT_PADDING_RATE;
                        this.mPath[i].reset();
                        this.mPath[i].moveTo(textOffsetX, textOffsetY);
                        this.mPath[i].lineTo((textOffsetX + textWidth) + preventClippingMargin, textOffsetY);
                        canvas.drawText(this.mTextBuffer.toString(), textOffsetX, textOffsetY, this.mTextPaint);
                        i += GRAVITY_CENTER_VERTICAL;
                    }
                }
                this.mTextBuffer.append(this.mTextLines[i]);
                if ((this.mTextGravity & GRAVITY_TOP) == 0) {
                    if ((this.mTextGravity & GRAVITY_BOTTOM) == 0) {
                        textOffsetY = ((((textAreaHeight - totalTextLineHeight) / DEFAULT_TEXT_SHADOWRADIUS) + paddingTop) + aboveBaseLine) + (((float) i) * textHeight);
                    } else {
                        textOffsetY = ((((float) viewHeight) - paddingBottom) - belowBaseLine) - (((float) ((this.mTextLines.length - 1) - i)) * textHeight);
                    }
                } else {
                    textOffsetY = (paddingTop + aboveBaseLine) + (((float) i) * textHeight);
                }
                if ((this.mTextGravity & GRAVITY_LEFT) == 0) {
                    if ((this.mTextGravity & GRAVITY_RIGHT) == 0) {
                        textOffsetX = ((textAreaWidth - textWidth) / DEFAULT_TEXT_SHADOWRADIUS) + paddingLeft;
                    } else {
                        textOffsetX = (((float) viewWidth) - textWidth) - paddingRight;
                    }
                } else {
                    textOffsetX = paddingLeft;
                }
                preventClippingMargin = textWidth * this.BASE_TEXT_PADDING_RATE;
                this.mPath[i].reset();
                this.mPath[i].moveTo(textOffsetX, textOffsetY);
                this.mPath[i].lineTo((textOffsetX + textWidth) + preventClippingMargin, textOffsetY);
                canvas.drawText(this.mTextBuffer.toString(), textOffsetX, textOffsetY, this.mTextPaint);
                i += GRAVITY_CENTER_VERTICAL;
            } else {
                return;
            }
        }
    }

    protected void setTextPaint() {
        this.mTextPaint.setTextSize((float) this.mTextSize);
        this.mTextPaint.setTextScaleX(this.mTextScaleX);
        this.mTextPaint.setColor(this.mTextColor);
        this.mTextPaint.setAntiAlias(true);
        this.mTextPaint.setShadowLayer(this.mTextShadowRadius, 0.0f, 0.0f, this.mTextShadowColor);
        if (!isEnabled()) {
            this.mTextPaint.setShadowLayer(this.mTextShadowRadius, 0.0f, 0.0f, this.mTextDisabledShadowColor);
        }
    }

    public void invalidate() {
        invalidate(0, 0, getWidth(), getHeight());
    }

    public void invalidate(Rect dirty) {
        dirty.left -= this.mExpand4Rotate;
        dirty.top -= this.mExpand4Rotate;
        dirty.right += this.mExpand4Rotate;
        dirty.bottom += this.mExpand4Rotate;
        super.invalidate(dirty);
    }

    public void invalidate(int l, int t, int r, int b) {
        super.invalidate(l - this.mExpand4Rotate, t - this.mExpand4Rotate, this.mExpand4Rotate + r, this.mExpand4Rotate + b);
    }

    public int getTextPaintHeight() {
        Paint p = new Paint();
        Rect textBounds = new Rect();
        p.getTextBounds(this.mText, 0, this.mText.length(), textBounds);
        return textBounds.height();
    }

    public int getTextSize() {
        return this.mTextSize;
    }

    public Paint getTextPaint() {
        return this.mTextPaint;
    }

    public void setTextScaleX(float scaleX) {
        this.mTextScaleX = scaleX;
        invalidate();
    }

    public void setTextStyle(int style) {
        boolean z = DEBUG_ON;
        if (style > 0) {
            float f;
            Typeface tf = Typeface.defaultFromStyle(style);
            this.mTextPaint.setTypeface(tf);
            int need = style & ((tf != null ? tf.getStyle() : 0) ^ -1);
            Paint paint = this.mTextPaint;
            if ((need & GRAVITY_CENTER_VERTICAL) != 0) {
                z = true;
            }
            paint.setFakeBoldText(z);
            paint = this.mTextPaint;
            if ((need & GRAVITY_TOP) != 0) {
                f = -0.25f;
            } else {
                f = 0.0f;
            }
            paint.setTextSkewX(f);
        } else {
            this.mTextPaint.setFakeBoldText(DEBUG_ON);
            this.mTextPaint.setTextSkewX(0.0f);
            this.mTextPaint.setTypeface(null);
        }
        invalidate();
    }
}
