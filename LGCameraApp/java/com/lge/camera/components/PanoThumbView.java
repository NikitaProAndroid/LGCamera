package com.lge.camera.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import com.lge.camera.R;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.camera.util.Util;
import com.lge.morpho.utils.multimedia.MediaProviderUtils;
import com.lge.olaworks.define.Ola_Exif.Tag;
import com.lge.voiceshutter.library.LGKeyRec;

public class PanoThumbView extends RelativeLayout {
    public static final String TAG = "CameraApp";
    private int ARROW_MARGIN;
    private int BOTTOM_MARGIN;
    public final int DIRECTION_DOWN;
    public final int DIRECTION_INIT;
    public final int DIRECTION_LEFT;
    public final int DIRECTION_RIGHT;
    public final int DIRECTION_UP;
    private int GUIDE_BOX_MARGIN;
    private int THUMB_MARGIN;
    private int mDHeight;
    private int mDWidth;
    private int mDirect;
    private int mThumbHeight;
    private int mThumbWidth;
    private Bitmap mThumbnail;
    private int mTotalHeight;
    private int mTotalWidth;
    private float moveRatioHeight;
    private float moveRatioWidth;

    public PanoThumbView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.DIRECTION_INIT = 0;
        this.DIRECTION_RIGHT = 1;
        this.DIRECTION_LEFT = 2;
        this.DIRECTION_DOWN = 3;
        this.DIRECTION_UP = 4;
        this.mDirect = 0;
        this.THUMB_MARGIN = 0;
        this.ARROW_MARGIN = 0;
        this.BOTTOM_MARGIN = 0;
        this.GUIDE_BOX_MARGIN = 0;
        this.ARROW_MARGIN = Common.getPixelFromDimens(context, R.dimen.panorama_thumb_view_arrow_marginLeft);
        this.THUMB_MARGIN = Common.getPixelFromDimens(context, R.dimen.panorama_thumb_view_marginLeft);
        this.BOTTOM_MARGIN = Common.getPixelFromDimens(getContext(), R.dimen.panorama_thumb_view_marginBottom);
        this.GUIDE_BOX_MARGIN = Common.getPixelFromDimens(context, R.dimen.panorama_guide_view_margin);
    }

    public void init(int tW, int tH, int w, int h, int dw, int dh, int enginInputW, int engineInputH) {
        this.mTotalWidth = tW;
        this.mTotalHeight = tH;
        this.mThumbWidth = w;
        this.mThumbHeight = h;
        this.mDWidth = dw;
        this.mDHeight = dh;
        this.moveRatioWidth = ((float) this.mThumbWidth) / ((float) enginInputW);
        this.moveRatioHeight = ((float) this.mThumbHeight) / ((float) engineInputH);
        setGuidRect();
    }

    private void setGuidRect() {
        if (this.mDirect == 0) {
            CamLog.d(TAG, "setGuidRect");
            ImageView movingRectImage = (ImageView) findViewById(R.id.pano_thumb_movingRect);
            LayoutParams mlp = (LayoutParams) movingRectImage.getLayoutParams();
            Common.resetLayoutParameter(mlp);
            movingRectImage.setLayoutDirection(0);
            ImageView nextRectImage = (ImageView) findViewById(R.id.pano_thumb_nextRect);
            LayoutParams nlp = (LayoutParams) nextRectImage.getLayoutParams();
            Common.resetLayoutParameter(nlp);
            nextRectImage.setLayoutDirection(0);
            mlp.width = this.mThumbWidth;
            mlp.height = this.mThumbHeight;
            mlp.leftMargin = this.THUMB_MARGIN;
            mlp.rightMargin = this.THUMB_MARGIN;
            mlp.topMargin = 0;
            mlp.bottomMargin = this.BOTTOM_MARGIN;
            mlp.addRule(15, 1);
            movingRectImage.setLayoutParams(mlp);
            nlp.width = this.mThumbWidth;
            nlp.height = this.mThumbHeight;
            nlp.leftMargin = 0;
            nlp.rightMargin = 0;
            nlp.topMargin = 0;
            nlp.bottomMargin = 0;
            nlp.addRule(15, 1);
            switch (this.mDirect) {
                case LGKeyRec.EVENT_NO_MATCH /*1*/:
                    nlp.addRule(15, 1);
                    break;
                case LGKeyRec.EVENT_INCOMPLETE /*2*/:
                    nlp.addRule(15, 1);
                    nlp.addRule(21, 1);
                    break;
                case LGKeyRec.EVENT_STARTED /*3*/:
                    nlp.addRule(14, 1);
                    break;
                case LGKeyRec.EVENT_STOPPED /*4*/:
                    nlp.addRule(14, 1);
                    break;
            }
            nextRectImage.setLayoutParams(nlp);
        }
    }

    public void setDirection(int direction, int degree) {
        this.mDirect = direction;
        if (this.mDirect != 0) {
            CamLog.d(TAG, "Panorama Thumb - direction : " + direction + ", degree = " + degree);
            if (!Util.isConfigureLandscape(getResources())) {
                degree = (degree + Tag.IMAGE_DESCRIPTION) % CameraConstants.DEGREE_360;
            }
            LayoutParams lp = (LayoutParams) getLayoutParams();
            Common.resetLayoutParameter(lp);
            lp.addRule(12, 1);
            ImageView arrowView = (ImageView) findViewById(R.id.pano_thumb_arrow);
            LayoutParams alp = (LayoutParams) arrowView.getLayoutParams();
            Common.resetLayoutParameter(alp);
            ImageView thumbImage = (ImageView) findViewById(R.id.pano_thumb_image);
            LayoutParams tlp = (LayoutParams) thumbImage.getLayoutParams();
            Common.resetLayoutParameter(tlp);
            thumbImage.setLayoutDirection(0);
            switch (direction) {
                case LGKeyRec.EVENT_NO_MATCH /*1*/:
                    lp.width = this.mTotalWidth;
                    lp.height = this.mThumbHeight;
                    lp.leftMargin = this.THUMB_MARGIN;
                    lp.rightMargin = this.THUMB_MARGIN;
                    lp.topMargin = 0;
                    lp.bottomMargin = this.BOTTOM_MARGIN;
                    lp.addRule(20, 1);
                    arrowView.setImageResource(R.drawable.pano_bg_arrow_right);
                    arrowView.setLayoutDirection(0);
                    alp.leftMargin = 0;
                    alp.rightMargin = this.ARROW_MARGIN;
                    alp.topMargin = 0;
                    alp.bottomMargin = 0;
                    alp.addRule(15, 1);
                    alp.addRule(21, 1);
                    tlp.addRule(20, 1);
                    break;
                case LGKeyRec.EVENT_INCOMPLETE /*2*/:
                    lp.width = this.mTotalWidth;
                    lp.height = this.mThumbHeight;
                    lp.leftMargin = this.THUMB_MARGIN;
                    lp.rightMargin = this.THUMB_MARGIN;
                    lp.topMargin = 0;
                    lp.bottomMargin = this.BOTTOM_MARGIN;
                    lp.addRule(20, 1);
                    arrowView.setImageResource(R.drawable.pano_bg_arrow_left);
                    arrowView.setLayoutDirection(0);
                    alp.leftMargin = this.ARROW_MARGIN;
                    alp.rightMargin = 0;
                    alp.topMargin = 0;
                    alp.bottomMargin = 0;
                    alp.addRule(15, 1);
                    alp.addRule(20, 1);
                    tlp.addRule(21, 1);
                    break;
                case LGKeyRec.EVENT_STARTED /*3*/:
                    lp.width = this.mThumbWidth;
                    lp.height = this.mTotalHeight;
                    lp.leftMargin = 0;
                    lp.rightMargin = this.BOTTOM_MARGIN;
                    lp.topMargin = this.THUMB_MARGIN;
                    lp.bottomMargin = this.THUMB_MARGIN;
                    lp.addRule(21, 1);
                    arrowView.setImageResource(R.drawable.pano_bg_arrow_bottom);
                    arrowView.setLayoutDirection(0);
                    alp.leftMargin = 0;
                    alp.rightMargin = 0;
                    alp.topMargin = 0;
                    alp.bottomMargin = this.ARROW_MARGIN;
                    alp.addRule(14, 1);
                    alp.addRule(12, 1);
                    tlp.addRule(10, 1);
                    break;
                case LGKeyRec.EVENT_STOPPED /*4*/:
                    lp.width = this.mThumbWidth;
                    lp.height = this.mTotalHeight;
                    lp.leftMargin = 0;
                    lp.rightMargin = this.BOTTOM_MARGIN;
                    lp.topMargin = this.THUMB_MARGIN;
                    lp.bottomMargin = this.THUMB_MARGIN;
                    lp.addRule(21, 1);
                    arrowView.setImageResource(R.drawable.pano_bg_arrow_top);
                    arrowView.setLayoutDirection(0);
                    alp.leftMargin = 0;
                    alp.rightMargin = 0;
                    alp.topMargin = this.ARROW_MARGIN;
                    alp.bottomMargin = 0;
                    alp.addRule(14, 1);
                    alp.addRule(10, 1);
                    tlp.addRule(12, 1);
                    break;
            }
            setLayoutParams(lp);
            setBackgroundColor(Color.argb(MediaProviderUtils.ROTATION_180, 0, 0, 0));
            arrowView.setLayoutParams(alp);
            arrowView.setVisibility(0);
            thumbImage.setLayoutParams(tlp);
            thumbImage.setImageBitmap(this.mThumbnail);
            findViewById(R.id.pano_thumb_border).setVisibility(0);
            arrowAnimation(true);
        }
    }

    public void setThumbnail(Bitmap thumbnail, boolean nextGuide) {
        if (this.mDirect != 0) {
            ((ImageView) findViewById(R.id.pano_thumb_image)).setImageBitmap(thumbnail);
        }
        this.mThumbnail = thumbnail;
        if (thumbnail == null) {
            LayoutParams lp = (LayoutParams) getLayoutParams();
            lp.width = -2;
            lp.height = -2;
            setLayoutParams(lp);
            setBackgroundColor(0);
            findViewById(R.id.pano_thumb_arrow).setVisibility(4);
            findViewById(R.id.pano_thumb_border).setVisibility(4);
            findViewById(R.id.pano_thumb_movingRect).setVisibility(4);
            findViewById(R.id.pano_thumb_nextRect).setVisibility(4);
            arrowAnimation(false);
            this.mDirect = 0;
            if (this.mThumbnail != null && this.mThumbnail.isRecycled()) {
                this.mThumbnail.recycle();
                this.mThumbnail = thumbnail;
            }
        } else if (nextGuide) {
            setNextRect();
        }
    }

    public void arrowAnimation(boolean start) {
        try {
            AnimationDrawable ad = (AnimationDrawable) ((ImageView) findViewById(R.id.pano_thumb_arrow)).getDrawable();
            if (ad == null) {
                return;
            }
            if (start) {
                ad.start();
            } else {
                ad.stop();
            }
        } catch (ClassCastException e) {
            CamLog.w(TAG, "ClassCastException : ", e);
        }
    }

    public void setMovingRect(int hMove, int vMove, int direction) {
        if (this.mDirect != 0) {
            ImageView movingRectImage = (ImageView) findViewById(R.id.pano_thumb_movingRect);
            LayoutParams mlp = (LayoutParams) movingRectImage.getLayoutParams();
            Common.resetLayoutParameter(mlp);
            ImageView nextRectImage = (ImageView) findViewById(R.id.pano_thumb_nextRect);
            LayoutParams nlp = (LayoutParams) nextRectImage.getLayoutParams();
            nextRectImage.setLayoutDirection(0);
            int transMove;
            switch (this.mDirect) {
                case LGKeyRec.EVENT_NO_MATCH /*1*/:
                    transMove = (int) (((float) hMove) * this.moveRatioWidth);
                    mlp.width = this.mThumbWidth;
                    mlp.height = this.mThumbHeight;
                    mlp.leftMargin = transMove;
                    mlp.leftMargin += nlp.leftMargin - this.mDWidth;
                    if (mlp.leftMargin <= this.GUIDE_BOX_MARGIN) {
                        mlp.leftMargin = this.GUIDE_BOX_MARGIN;
                    }
                    mlp.rightMargin = 0;
                    mlp.topMargin = 0;
                    mlp.bottomMargin = this.BOTTOM_MARGIN;
                    mlp.addRule(15, 1);
                    break;
                case LGKeyRec.EVENT_INCOMPLETE /*2*/:
                    transMove = (int) (((float) (-hMove)) * this.moveRatioWidth);
                    mlp.width = this.mThumbWidth;
                    mlp.height = this.mThumbHeight;
                    mlp.rightMargin = transMove;
                    mlp.rightMargin += nlp.rightMargin - this.mDWidth;
                    if (mlp.rightMargin <= this.GUIDE_BOX_MARGIN) {
                        mlp.rightMargin = this.GUIDE_BOX_MARGIN;
                    }
                    mlp.leftMargin = 0;
                    mlp.topMargin = 0;
                    mlp.bottomMargin = this.BOTTOM_MARGIN;
                    mlp.addRule(15, 1);
                    mlp.addRule(21, 1);
                    break;
                case LGKeyRec.EVENT_STARTED /*3*/:
                    transMove = (int) (((float) vMove) * this.moveRatioHeight);
                    mlp.width = this.mThumbWidth;
                    mlp.height = this.mThumbHeight;
                    mlp.leftMargin = 0;
                    mlp.rightMargin = this.BOTTOM_MARGIN;
                    mlp.topMargin = transMove;
                    mlp.topMargin += nlp.topMargin - this.mDHeight;
                    if (mlp.topMargin <= this.GUIDE_BOX_MARGIN) {
                        mlp.topMargin = this.GUIDE_BOX_MARGIN;
                    }
                    mlp.bottomMargin = 0;
                    mlp.addRule(14, 1);
                    mlp.addRule(10, 1);
                    break;
                case LGKeyRec.EVENT_STOPPED /*4*/:
                    transMove = (int) (((float) (-vMove)) * this.moveRatioHeight);
                    mlp.width = this.mThumbWidth;
                    mlp.height = this.mThumbHeight;
                    mlp.leftMargin = 0;
                    mlp.rightMargin = this.BOTTOM_MARGIN;
                    mlp.topMargin = 0;
                    mlp.bottomMargin = transMove;
                    mlp.bottomMargin += nlp.bottomMargin - this.mDHeight;
                    if (mlp.bottomMargin <= this.GUIDE_BOX_MARGIN) {
                        mlp.bottomMargin = this.GUIDE_BOX_MARGIN;
                    }
                    mlp.addRule(14, 1);
                    mlp.addRule(12, 1);
                    break;
            }
            movingRectImage.setLayoutParams(mlp);
            movingRectImage.setVisibility(0);
            if (nextRectImage.getVisibility() != 0) {
                CamLog.w(TAG, "setMovingRect setNextRect");
                setNextRect();
            }
            invalidate();
        }
    }

    private void setNextRect() {
        if (this.mDirect != 0) {
            CamLog.w(TAG, "setNextRect");
            ImageView nextRectImage = (ImageView) findViewById(R.id.pano_thumb_nextRect);
            LayoutParams nlp = (LayoutParams) nextRectImage.getLayoutParams();
            Common.resetLayoutParameter(nlp);
            nextRectImage.setLayoutDirection(0);
            switch (this.mDirect) {
                case LGKeyRec.EVENT_NO_MATCH /*1*/:
                    nlp.width = this.mThumbWidth;
                    nlp.height = this.mThumbHeight;
                    nlp.leftMargin += this.mDWidth;
                    CamLog.w(TAG, "setNextRect nlp.leftMargin + mThumbWidth= " + (nlp.leftMargin + this.mThumbWidth));
                    CamLog.w(TAG, "setNextRect mTotalWidth= " + this.mTotalWidth);
                    if (nlp.leftMargin + this.mThumbWidth >= this.mTotalWidth) {
                        nlp.leftMargin -= this.GUIDE_BOX_MARGIN;
                    }
                    nlp.rightMargin = 0;
                    nlp.topMargin = 0;
                    nlp.bottomMargin = this.BOTTOM_MARGIN;
                    nlp.addRule(15, 1);
                    break;
                case LGKeyRec.EVENT_INCOMPLETE /*2*/:
                    nlp.width = this.mThumbWidth;
                    nlp.height = this.mThumbHeight;
                    nlp.leftMargin = 0;
                    nlp.rightMargin += this.mDWidth;
                    if (nlp.rightMargin + this.mThumbWidth >= this.mTotalWidth) {
                        nlp.rightMargin -= this.GUIDE_BOX_MARGIN;
                    }
                    nlp.topMargin = 0;
                    nlp.bottomMargin = this.BOTTOM_MARGIN;
                    nlp.addRule(15, 1);
                    nlp.addRule(21, 1);
                    break;
                case LGKeyRec.EVENT_STARTED /*3*/:
                    nlp.width = this.mThumbWidth;
                    nlp.height = this.mThumbHeight;
                    nlp.leftMargin = 0;
                    nlp.rightMargin = this.BOTTOM_MARGIN;
                    nlp.topMargin += this.mDHeight;
                    if (nlp.topMargin + this.mThumbHeight >= this.mTotalHeight) {
                        nlp.topMargin -= this.GUIDE_BOX_MARGIN;
                    }
                    nlp.bottomMargin = 0;
                    nlp.addRule(14, 1);
                    nlp.addRule(10, 1);
                    break;
                case LGKeyRec.EVENT_STOPPED /*4*/:
                    nlp.width = this.mThumbWidth;
                    nlp.height = this.mThumbHeight;
                    nlp.leftMargin = 0;
                    nlp.rightMargin = this.BOTTOM_MARGIN;
                    nlp.topMargin = 0;
                    nlp.bottomMargin += this.mDHeight;
                    if (nlp.bottomMargin + this.mThumbHeight >= this.mTotalHeight) {
                        nlp.bottomMargin -= this.GUIDE_BOX_MARGIN;
                    }
                    nlp.addRule(14, 1);
                    nlp.addRule(12, 1);
                    break;
            }
            nextRectImage.setLayoutParams(nlp);
            nextRectImage.setVisibility(0);
        }
    }
}
