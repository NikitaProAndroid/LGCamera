package com.lge.camera.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import com.lge.camera.properties.CameraConstants;
import com.lge.morpho.core.Error;
import java.util.ArrayList;

public class RotateTextView extends RotateView {
    private boolean mPortrait;

    public RotateTextView(Context context) {
        this(context, null);
    }

    public RotateTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RotateTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mPortrait = false;
        this.BASE_TEXT_PADDING_RATE = 0.2f;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        int height;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        setTextPaint();
        if (widthMode == 1073741824) {
            width = widthSize;
        } else {
            width = this.mPortrait ? getDesiredHeight() : getDesiredWidth();
            if (widthMode == Error.ERROR_GENERAL_ERROR) {
                if (!this.mPortrait && width > widthSize) {
                    this.mLandscapeTextLines = wordWrap(widthSize);
                    setTextLines(this.mLandscapeTextLines);
                    width = getDesiredWidth();
                }
                width = Math.min(widthSize, width);
            }
        }
        if (heightMode == 1073741824) {
            height = heightSize;
        } else {
            height = this.mPortrait ? getDesiredWidth() : getDesiredHeight();
            if (heightMode == Error.ERROR_GENERAL_ERROR) {
                if (this.mPortrait && height > heightSize) {
                    this.mPortraitTextLines = wordWrap(heightSize);
                    setTextLines(this.mPortraitTextLines);
                    height = getDesiredWidth();
                }
                height = Math.min(height, heightSize);
            }
        }
        setMeasuredDimension(width, height);
    }

    protected void canvasRotate(Canvas canvas, int viewWidth, int viewHeight) {
        float pivotX = (float) this.mRotatePivotLeft;
        float pivotY = (float) this.mRotatePivotTop;
        if (this.mRotatePivotLeft == RotateView.PIVOT_CENTER) {
            pivotX = ((float) viewWidth) / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK;
        }
        if (this.mRotatePivotTop == RotateView.PIVOT_CENTER) {
            pivotY = ((float) viewHeight) / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK;
        }
        canvas.rotate((float) (-this.mRotationInfo.getCurrentDegree()), pivotX, pivotY);
    }

    protected boolean checkBackground(Canvas canvas) {
        return true;
    }

    public int getTextPaintWidth() {
        Paint p = new Paint();
        p.setTextSize((float) this.mTextSize);
        return (int) p.measureText(this.mText);
    }

    public void setRotated(int degree) {
        boolean portrait;
        if (degree == 1 || degree == 3) {
            setTextLines(this.mPortraitTextLines);
            portrait = true;
        } else {
            setTextLines(this.mLandscapeTextLines);
            portrait = false;
        }
        if (this.mPortrait != portrait) {
            this.mPortrait = portrait;
            invalidate();
        }
    }

    private int getDesiredWidth() {
        float width = 0.0f;
        if (this.mTextLines != null) {
            for (String measureText : this.mTextLines) {
                float textWidth = this.mTextPaint.measureText(measureText);
                width = Math.max((textWidth + (RotateView.PORTRAIT_TEXT_WIDTH_CORRECTION_RATE * textWidth)) + (((((float) this.mTextPaddingLeft) + this.mTextBasePadding) + ((float) this.mTextPaddingRight)) + this.mTextBasePadding), width);
            }
        }
        return (int) width;
    }

    private int getDesiredHeight() {
        float textHeight = this.mTextPaint.getFontSpacing();
        float totalTextLineHeight = 0.0f;
        if (this.mTextLines != null) {
            totalTextLineHeight = (textHeight * ((float) this.mTextLines.length)) + (((((float) this.mTextPaddingTop) + this.mTextBasePadding) + ((float) this.mTextPaddingBottom)) + this.mTextBasePadding);
        }
        return (int) totalTextLineHeight;
    }

    private void setTextLines(String[] textLines) {
        if (textLines != null) {
            this.mTextLines = textLines;
            if (this.mTextLines != null) {
                for (int i = 0; i < this.mTextLines.length; i++) {
                    if (this.mPath[i] == null) {
                        this.mPath[i] = new Path();
                    }
                }
            }
        }
    }

    private StringBuffer mergeStrings(String[] strings) {
        StringBuffer mergedText = new StringBuffer();
        for (int i = 0; i < this.mTextLines.length; i++) {
            if (i != 0) {
                mergedText.append(' ');
            }
            mergedText.append(this.mTextLines[i]);
        }
        return mergedText;
    }

    private String[] wordWrap(int maxWidth) {
        StringBuffer mergedText = mergeStrings(this.mTextLines);
        maxWidth = (int) (((float) maxWidth) - (((((float) this.mTextPaddingLeft) + this.mTextBasePadding) + ((float) this.mTextPaddingRight)) + this.mTextBasePadding));
        maxWidth = (int) (((float) maxWidth) - (((float) maxWidth) * RotateView.PORTRAIT_TEXT_WIDTH_CORRECTION_RATE));
        ArrayList<String> textLines = new ArrayList();
        int i = 0;
        while (mergedText.length() > 0 && i < 5) {
            while (mergedText.length() > 0 && mergedText.charAt(0) == ' ') {
                mergedText.deleteCharAt(0);
            }
            String remainText = mergedText.toString();
            maxWidth += 20;
            int lineLength = this.mTextPaint.breakText(remainText, true, (float) maxWidth, new float[5]);
            String currentLine = mergedText.substring(0, lineLength);
            if (lineLength < remainText.length()) {
                lineLength = currentLine.lastIndexOf(32);
                if (lineLength < 0) {
                    lineLength = currentLine.length();
                }
            }
            textLines.add(mergedText.substring(0, lineLength));
            mergedText.delete(0, lineLength);
            i++;
        }
        String[] result = new String[textLines.size()];
        textLines.toArray(result);
        return result;
    }
}
