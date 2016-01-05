package com.lge.camera.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import com.lge.camera.properties.CameraConstants;
import com.lge.morpho.utils.multimedia.MediaProviderUtils;
import com.lge.olaworks.define.Ola_Exif.Tag;
import com.lge.voiceshutter.library.LGKeyRec;

public class RotateLayout extends RelativeLayout {
    private View mChild;
    private int mOrientation;

    public RotateLayout(Context context) {
        super(context);
    }

    public RotateLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (getResources() != null) {
            setBackgroundResource(17170445);
        }
    }

    protected void onFinishInflate() {
        this.mChild = getChildAt(0);
        if (this.mChild != null) {
            this.mChild.setPivotX(0.0f);
            this.mChild.setPivotY(0.0f);
        }
    }

    protected void onLayout(boolean change, int left, int top, int right, int bottom) {
        int width = right - left;
        int height = bottom - top;
        switch (this.mOrientation) {
            case LGKeyRec.EVENT_INVALID /*0*/:
            case MediaProviderUtils.ROTATION_180 /*180*/:
                this.mChild.layout(0, 0, width, height);
            case MediaProviderUtils.ROTATION_90 /*90*/:
            case Tag.IMAGE_DESCRIPTION /*270*/:
                this.mChild.layout(0, 0, height, width);
            default:
        }
    }

    protected void onMeasure(int widthSpec, int heightSpec) {
        int w = 0;
        int h = 0;
        if (this.mChild == null) {
            onFinishInflate();
        }
        switch (this.mOrientation) {
            case LGKeyRec.EVENT_INVALID /*0*/:
            case MediaProviderUtils.ROTATION_180 /*180*/:
                measureChild(this.mChild, widthSpec, heightSpec);
                w = this.mChild.getMeasuredWidth();
                h = this.mChild.getMeasuredHeight();
                break;
            case MediaProviderUtils.ROTATION_90 /*90*/:
            case Tag.IMAGE_DESCRIPTION /*270*/:
                measureChild(this.mChild, heightSpec, widthSpec);
                w = this.mChild.getMeasuredHeight();
                h = this.mChild.getMeasuredWidth();
                break;
        }
        setMeasuredDimension(w, h);
        switch (this.mOrientation) {
            case LGKeyRec.EVENT_INVALID /*0*/:
                this.mChild.setTranslationX(0.0f);
                this.mChild.setTranslationY(0.0f);
                break;
            case MediaProviderUtils.ROTATION_90 /*90*/:
                this.mChild.setTranslationX(0.0f);
                this.mChild.setTranslationY((float) h);
                break;
            case MediaProviderUtils.ROTATION_180 /*180*/:
                this.mChild.setTranslationX((float) w);
                this.mChild.setTranslationY((float) h);
                break;
            case Tag.IMAGE_DESCRIPTION /*270*/:
                this.mChild.setTranslationX((float) w);
                this.mChild.setTranslationY(0.0f);
                break;
        }
        this.mChild.setRotation((float) (-this.mOrientation));
    }

    public void setAngle(int orientation) {
        orientation %= CameraConstants.DEGREE_360;
        if (this.mOrientation != orientation) {
            this.mOrientation = orientation;
            requestLayout();
        }
    }

    public int getAngle() {
        return this.mOrientation;
    }

    public void rotateLayout(int degree) {
        setAngle(degree);
        invalidate();
    }
}
