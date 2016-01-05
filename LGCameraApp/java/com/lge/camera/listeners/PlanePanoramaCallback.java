package com.lge.camera.listeners;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.NinePatchDrawable;
import android.hardware.Camera;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout.LayoutParams;
import com.lge.camera.R;
import com.lge.camera.components.RotateView;
import com.lge.camera.controller.camera.PlanePanoramaControllerBase;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.util.AnimationUtil;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.ColorConverter;
import com.lge.camera.util.Common;
import com.lge.camera.util.Util;
import com.lge.morpho.core.MorphoPanoramaGP.ImageSize;
import com.lge.morpho.utils.multimedia.StillImageData;
import com.lge.olaworks.define.Ola_Exif.Tag;
import com.lge.olaworks.define.Ola_ShotParam;
import com.lge.olaworks.library.FaceDetector;
import com.lge.voiceshutter.library.LGKeyRec;

public class PlanePanoramaCallback extends PlanePanoramaCallbackBase {
    public PlanePanoramaCallback(PlanePanoramaCallbackFunction function, View base) {
        super(function);
        initView(base);
    }

    public void onPreviewFrame(byte[] data, Camera camera) {
        synchronized (this.mGet.getSyncObj()) {
            if (this.mGet.getCameraDevice() == null) {
            } else if (this.mPreviewSkipCount > 0) {
                this.mPreviewSkipCount--;
                this.mGet.getCameraDevice().addCallbackBuffer(this.mGet.getPreviewBuff());
            } else if (this.mGet.getState() != 0 || this.mGet.getPreviewMini() == null) {
                if (this.mGet.getState() >= 2 && this.mGet.getState() <= 3) {
                    doTakingStateJob(data);
                }
            } else {
                doPreviewStateJob(data);
            }
        }
    }

    public void onPictureTakenPreview(byte[] data) {
        synchronized (this.mGet.getSyncObj()) {
            if (this.mGet.getCameraDevice() == null || this.mGet.getMorphoPanoramaGP() == null) {
                return;
            }
            int id = ((Integer) this.mImageIDList.get(0)).intValue();
            int status = ((Integer) this.mImageStatusList.get(0)).intValue();
            this.mImageIDList.remove(0);
            this.mImageStatusList.remove(0);
            this.mGet.addStillImage(new StillImageData(id, this.mPreviewCount, data, this.mMotionData));
            CamLog.d(FaceDetector.TAG, "onPictureTakenPreview mNeedStop=" + this.mNeedStop + " status=" + status);
            if (this.mNeedStop) {
                status = 11;
                this.mNeedStop = false;
            }
            switch (status) {
                case LGKeyRec.EVENT_NO_MATCH /*1*/:
                case LGKeyRec.EVENT_STARTED /*3*/:
                case Ola_ShotParam.ImageEffect_MotionBlur /*11*/:
                    this.mGet.stopPanorama();
                    this.mGet.getCameraDevice().startPreview();
                    break;
            }
        }
    }

    public void onPictureTaken(byte[] data, Camera camera) {
        CamLog.d(FaceDetector.TAG, "onPictureTaken START");
        this.mGet.setRequestTakePicture(false);
        synchronized (this.mGet.getSyncObj()) {
            if (this.mGet.getCameraDevice() == null || this.mGet.getMorphoPanoramaGP() == null) {
                CamLog.d(FaceDetector.TAG, "exit onPictureTaken");
                return;
            }
            int id = ((Integer) this.mImageIDList.get(0)).intValue();
            int status = ((Integer) this.mImageStatusList.get(0)).intValue();
            this.mImageIDList.remove(0);
            this.mImageStatusList.remove(0);
            this.mGet.addStillImage(new StillImageData(id, this.mPreviewCount, data, this.mMotionData));
            checkStatusAfterPictureTaken(status);
            CamLog.d(FaceDetector.TAG, "onPictureTaken END");
        }
    }

    private void checkStatusAfterPictureTaken(int status) {
        CamLog.d(FaceDetector.TAG, "checkStatusAfterPictureTaken mNeedStop=" + this.mNeedStop + " status=" + status);
        if (this.mNeedStop) {
            status = 11;
            this.mNeedStop = false;
        }
        switch (status) {
            case LGKeyRec.EVENT_NO_MATCH /*1*/:
            case LGKeyRec.EVENT_STARTED /*3*/:
            case Ola_ShotParam.ImageEffect_MotionBlur /*11*/:
                this.mGet.stopPanorama();
                this.mGet.getCameraDevice().startPreview();
            default:
                this.mGet.getCameraDevice().startPreview();
                if (this.mGet.isShooting()) {
                    this.mGet.getCameraDevice().addCallbackBuffer(this.mGet.getPreviewBuff());
                    resetPreviewSkipCount();
                    this.mGet.getCameraDevice().setPreviewCallbackWithBuffer(this);
                }
        }
    }

    public void onAutoFocus(boolean success, Camera camera) {
        synchronized (this.mGet.getSyncObj()) {
            if (this.mGet.getCameraDevice() == null) {
                return;
            }
            this.mGet.playRecordingSound(true);
            this.mGet.setStatus(2);
            this.mGet.setShutterButtonImage(true, this.mGet.getOrientationDegree());
            this.mGet.setVisibleTakingGuide(true, false);
            this.mGet.setVisibleArrowGuide(true, true, true);
            resetPreviewSkipCount();
            resetImageIdAndStatusList();
            this.mGet.getCameraDevice().getParameters().setAutoExposureLock(false);
            this.mGet.getCameraDevice().getParameters().setAutoWhiteBalanceLock(false);
            rotateGuide(this.mGet.getOrientationDegree());
        }
    }

    private void doPreviewStateJob(byte[] data) {
        ColorConverter.yuv420spToBitmap(this.mPreviewImageMini, data, this.mPreviewW, this.mPreviewH);
        if (this.mPreviewMiniCanvas != null) {
            this.mPreviewMiniCanvas.drawBitmap(this.mPreviewImageMini, null, this.mRectPreviewMini, null);
        }
        this.mGet.getPreviewMini().setImageBitmap(this.mDisplayPreviewImageMini);
        this.mGet.getCameraDevice().addCallbackBuffer(this.mGet.getPreviewBuff());
    }

    private void drawBarGuide() {
        this.mProgressImageW = this.mProgressImage.getWidth();
        this.mProgressImageH = this.mProgressImage.getHeight();
        this.offsetX = 0;
        this.offsetY = 0;
        this.mBarCanvas.drawColor(0, Mode.SRC);
        if (this.mRectPreview == null) {
            this.mRectPreview = new Rect(0, 0, this.mBarW, this.mBarH);
        }
        drawProgressImage();
        drawBoxes();
        drawProgressOutline();
    }

    private void drawProgressImage() {
        Rect src;
        switch (this.mAppPanoramaDirection) {
            case LGKeyRec.EVENT_NO_MATCH /*1*/:
                this.mProgressImageW -= this.mPreviewCropAdjust * 2;
                this.offsetX = ((-this.mProgressImageW) * 10) / 100;
                this.offsetY = 0;
                src = new Rect(this.mPreviewCropAdjust + ((this.mProgressImageW * 10) / 100), 0, this.mPreviewCropAdjust + ((this.mProgressImageW * 80) / 100), this.mProgressImageH);
                this.mRectPreview.set((int) (((double) this.mBarW) * 0.25d), 0, (int) (((double) this.mBarW) * 0.75d), this.mBarH);
                break;
            default:
                this.mProgressImageH -= this.mPreviewCropAdjust * 2;
                this.offsetX = 0;
                this.offsetY = ((-this.mProgressImageH) * 10) / 100;
                src = new Rect(0, this.mPreviewCropAdjust + ((this.mProgressImageH * 10) / 100), this.mProgressImageW, this.mPreviewCropAdjust + ((this.mProgressImageH * 80) / 100));
                this.mRectPreview.set(0, (int) (((double) this.mBarH) * 0.25d), this.mBarW, (int) (((double) this.mBarH) * 0.75d));
                break;
        }
        this.mRectProgressBar = new Rect(this.mRectPreview.left + this.mBoxOutlineNinePatchMargin, this.mRectPreview.top + this.mBoxOutlineNinePatchMargin, this.mRectPreview.right - this.mBoxOutlineNinePatchMargin, this.mRectPreview.bottom - this.mBoxOutlineNinePatchMargin);
        this.mBarCanvas.drawBitmap(this.mProgressImage, src, this.mRectProgressBar, null);
    }

    private void drawAttachGuideVertical(Point attachedPos) {
        if (this.mAppPanoramaDirection == 0) {
            drawAttachGuideVerticalToHor(attachedPos);
        } else {
            drawAttachGuideVerticalToVer(attachedPos);
        }
    }

    private void drawAttachGuideVerticalToHor(Point attachedPos) {
        int barH = this.mProgressImage.getHeight();
        int margin = (int) (((float) barH) * 0.12f);
        int gap = attachedPos.y - ((int) (((double) barH) * 0.5d));
        checkAttachBoxRedZone(gap, (int) (((float) barH) * 0.3f));
        if (Math.abs(gap) < margin) {
            if (!this.isShowHorizontalArrow) {
                hideGuideCase();
            }
            this.isOnMagneticVertical = true;
            return;
        }
        this.isOnMagneticVertical = false;
        if (gap < 0) {
            drawAttachGuideVerticalToHorDown();
        } else {
            drawAttachGuideVerticalToHorUp();
        }
    }

    private void drawAttachGuideVerticalToVer(Point attachedPos) {
        int barH = this.mProgressImage.getWidth();
        int margin = (int) (((float) barH) * 0.12f);
        int gap = attachedPos.x - ((int) ((((double) barH) * 0.5d) - ((double) this.mPreviewCropAdjust)));
        checkAttachBoxRedZone(gap, (int) (((float) barH) * 0.3f));
        if (Math.abs(gap) < margin) {
            if (!this.isShowHorizontalArrow) {
                hideGuideCase();
            }
            this.isOnMagneticVertical = true;
            return;
        }
        this.isOnMagneticVertical = false;
        if (gap < 0) {
            drawAttachGuideVerticalToVerUp();
        } else {
            drawAttachGuideVerticalToVerDown();
        }
    }

    private void drawAttachGuideHorizontal(Point attachedPos, Point guidePos) {
        if (!this.isOnMagneticVertical) {
            this.isShowHorizontalArrow = false;
        } else if (this.mAppPanoramaDirection == 0) {
            drawAttachGuideHorizontalToHor(attachedPos, guidePos);
        } else {
            drawAttachGuideHorizontalToVer(attachedPos, guidePos);
        }
    }

    private void drawAttachGuideHorizontalToHor(Point attachedPos, Point guidePos) {
        int gap = attachedPos.x - guidePos.x;
        this.isShowHorizontalArrow = ((int) (((float) this.mGuideAreaLength) * PREVIEW_GUIDE_AREA_RATIO_HOR_HOR)) - Math.abs(gap) <= 0;
        if (gap >= 0) {
            drawAttachGuideHorizontalToHorLeft();
        } else {
            drawAttachGuideHorizontalToHorRight();
        }
    }

    private void drawAttachGuideHorizontalToVer(Point attachedPos, Point guidePos) {
        int gap = attachedPos.y - guidePos.y;
        this.isShowHorizontalArrow = ((int) (((float) this.mGuideAreaLength) * PREVIEW_GUIDE_AREA_RATIO_HOR_VER)) - Math.abs(gap) <= 0;
        if (gap >= 0) {
            drawAttachGuideHorizontalToVerLeft();
        } else {
            drawAttachGuideHorizontalToVerRight();
        }
    }

    private void alignMagneticMarginHorizontal(Point attachedPos, Point guidePos) {
        if (this.isOnMagneticVertical && this.mStatus[0] == 4) {
            if (this.mAppPanoramaDirection == 0) {
                attachedPos.x = guidePos.x;
            } else {
                attachedPos.y = guidePos.y;
            }
            if (this.mBoxAttachCurrentBgId != R.drawable.panorama_blue_box) {
                this.mBoxAttachOutlineArrow.setBackgroundResource(R.drawable.panorama_blue_box);
                this.mBoxAttachCurrentBgId = R.drawable.panorama_blue_box;
            }
        }
    }

    private void alignMagneticMarginVertical(Point attachedPos, Point guidePos) {
        int centerH;
        int margin;
        int gap;
        if (this.mAppPanoramaDirection == 0) {
            int barH = this.mProgressImage.getHeight();
            centerH = (int) ((((double) barH) * 0.5d) - ((double) this.mGuideBoxAdjustHor));
            margin = (int) (((float) barH) * RotateView.BASE_TEXT_SCALE_X_RATE);
            gap = centerH - attachedPos.y;
            if (Math.abs(gap) < margin) {
                attachedPos.y = centerH;
            } else if (gap > 0) {
                attachedPos.y += margin;
            } else {
                attachedPos.y -= margin;
            }
            guidePos.y = centerH;
            return;
        }
        barH = this.mProgressImage.getWidth();
        centerH = (int) (((((double) barH) * 0.5d) - ((double) this.mPreviewCropAdjust)) - ((double) this.mGuideBoxAdjustVer));
        margin = (int) (((float) barH) * RotateView.BASE_TEXT_SCALE_X_RATE);
        gap = centerH - attachedPos.x;
        if (Math.abs(gap) < margin) {
            attachedPos.x = centerH;
        } else if (gap > 0) {
            attachedPos.x += margin;
        } else {
            attachedPos.x -= margin;
        }
        guidePos.x = centerH;
    }

    private void drawAdjustBox(Point guidePos, Point attachedPos, int fw, int fh) {
        int x0 = (this.offsetX + guidePos.x) - fw;
        int x1 = (this.offsetX + guidePos.x) + fw;
        int y0 = (this.offsetY + guidePos.y) - fh;
        int y1 = (this.offsetY + guidePos.y) + fh;
        if (this.mAppPanoramaDirection == 0) {
            y0 = Math.max((((y0 * 10) / 100) + y0) + (this.mGuideBoxLineWidth * 2), this.mGuideBoxLineWidth);
            y1 = Math.min((y1 - ((y1 * 10) / 100)) - (this.mGuideBoxLineWidth * 2), this.mBarH - this.mGuideBoxLineWidth);
        } else {
            x0 = Math.max((((x0 * 10) / 100) + x0) + (this.mGuideBoxLineWidth * 2), this.mGuideBoxLineWidth);
            x1 = Math.min((x1 - ((x1 * 10) / 100)) - (this.mGuideBoxLineWidth * 2), this.mBarW - this.mGuideBoxLineWidth);
        }
        Paint p = new Paint();
        p.setStyle(Style.FILL);
        p.setXfermode(new PorterDuffXfermode(Mode.SRC));
        p.setColor(Color.argb(Common.KEYCODE_TESTMODE_CAMCORDER_PLAY_MOVING_FILE, 0, 0, 0));
        this.mBarCanvas.drawRect((float) this.mRectProgressBar.left, (float) y0, (float) x1, (float) y1, p);
    }

    private void drawGuidePositionBox(Point guidePos, Point attachedPos, int fw, int fh) {
        float maxDistance;
        Point start = new Point((this.offsetX + guidePos.x) - fw, (this.offsetY + guidePos.y) - fh);
        Point end = new Point((this.offsetX + guidePos.x) + fw, (this.offsetY + guidePos.y) + fh);
        if (this.mAppPanoramaDirection == 0) {
            start.y = Math.max((start.y + ((start.y * 10) / 100)) + (this.mGuideBoxLineWidth * 2), this.mGuideBoxLineWidth);
            end.y = Math.min((end.y - ((end.y * 10) / 100)) - (this.mGuideBoxLineWidth * 2), this.mBarH - this.mGuideBoxLineWidth);
            maxDistance = (float) Util.distance(start.y, end.y);
        } else {
            start.x = Math.max((start.x + ((start.x * 10) / 100)) + (this.mGuideBoxLineWidth * 2), this.mGuideBoxLineWidth);
            end.x = Math.min((end.x - ((end.x * 10) / 100)) - (this.mGuideBoxLineWidth * 2), this.mBarW - this.mGuideBoxLineWidth);
            maxDistance = (float) Util.distance(start.x, end.x);
        }
        adjustGuidePositionBox(start, end);
        setGuidePositionBoxLayout((this.mDistGuideAttachPos >= maxDistance ? 0.0f : 255.0f * ((maxDistance - this.mDistGuideAttachPos) / maxDistance)) / 255.0f, start, end);
        if (this.mBoxGuide.getVisibility() != 0) {
            this.mBoxGuide.setVisibility(0);
        }
    }

    private void adjustGuidePositionBox(Point start, Point end) {
        switch (this.mGet.getDirection()[0]) {
            case LGKeyRec.EVENT_INCOMPLETE /*2*/:
                if (start.y < this.mRectPreview.top + this.mGuideBoxLineWidth) {
                    start.y = this.mRectPreview.top + this.mGuideBoxLineWidth;
                }
            case LGKeyRec.EVENT_STARTED /*3*/:
                if (end.y > this.mRectPreview.bottom - this.mGuideBoxLineWidth) {
                    end.y = this.mRectPreview.bottom - this.mGuideBoxLineWidth;
                }
            case LGKeyRec.EVENT_STOPPED /*4*/:
                if (end.x > this.mRectPreview.right - this.mGuideBoxLineWidth) {
                    end.x = this.mRectPreview.right - this.mGuideBoxLineWidth;
                }
            case LGKeyRec.EVENT_START_OF_VOICING /*5*/:
                if (start.x < this.mRectPreview.left + this.mGuideBoxLineWidth) {
                    start.x = this.mRectPreview.left + this.mGuideBoxLineWidth;
                }
            default:
        }
    }

    private void setGuidePositionBoxLayout(float alpha, Point start, Point end) {
        if (this.mBoxGuide != null) {
            this.mBoxGuide.setAlpha(alpha);
            LayoutParams lp = (LayoutParams) this.mBoxGuide.getLayoutParams();
            Common.resetLayoutParameter(lp);
            lp.setLayoutDirection(0);
            lp.width = Util.distance(start.x, end.x);
            lp.height = Util.distance(start.y, end.y);
            if (this.mAppPanoramaDirection == 0) {
                this.mGuideAreaLength = lp.width;
                lp.height = this.mRectProgressBar.height();
                lp.setMarginStart(start.x + this.mBarLayoutMargin);
                lp.topMargin = this.mGet.getBarLayout().getTop() + start.y;
            } else {
                this.mGuideAreaLength = lp.height;
                lp.width = this.mRectProgressBar.width();
                lp.setMarginStart(start.x);
                lp.topMargin = this.mGet.getBarLayout().getTop() + start.y;
            }
            this.mBoxGuide.setLayoutParams(lp);
        }
    }

    private void drawAttchPositionBox(Point attachedPos, int fw, int fh) {
        Point start = new Point((this.offsetX + attachedPos.x) - fw, (this.offsetY + attachedPos.y) - fh);
        Point end = new Point((this.offsetX + attachedPos.x) + fw, (this.offsetY + attachedPos.y) + fh);
        Point gapWithGuideBox = new Point(0, 0);
        int maxStartH;
        if (this.mAppPanoramaDirection == 0) {
            start.y = Math.max((start.y + ((start.y * 10) / 100)) + (this.mGuideBoxLineWidth * 2), this.mGuideBoxLineWidth);
            end.y = Math.min((end.y - ((end.y * 10) / 100)) - (this.mGuideBoxLineWidth * 2), this.mBarH - this.mGuideBoxLineWidth);
            maxStartH = (this.mBarH - this.offsetY) - fh;
            if (start.y > maxStartH) {
                start.y = maxStartH;
            }
        } else {
            start.x = Math.max((start.x + ((start.x * 10) / 100)) + (this.mGuideBoxLineWidth * 2), this.mGuideBoxLineWidth);
            end.x = Math.min((end.x - ((end.x * 10) / 100)) - (this.mGuideBoxLineWidth * 2), this.mBarW - this.mGuideBoxLineWidth);
            maxStartH = (this.mBarW - this.offsetX) - fw;
            if (start.x > maxStartH) {
                start.x = maxStartH;
            }
        }
        Point outlineSize = new Point(Util.distance(start.x, end.x), Util.distance(start.y, end.y));
        setAttachPositionBoxOutlineLayout(outlineSize, gapWithGuideBox);
        setAttachPositionBoxArrowTextLayout(start, outlineSize, gapWithGuideBox);
        if (this.mGuideDrawingSkipCount < 3) {
            this.mGuideDrawingSkipCount++;
            return;
        }
        if (!(this.mBoxAttachOutlineArrow == null || this.mBoxAttachOutlineArrow.getVisibility() == 0)) {
            this.mBoxAttachOutlineArrow.setVisibility(0);
        }
        if (!(this.mBoxAttachFrameArrow == null || this.mBoxAttachFrameArrow.getVisibility() == 0)) {
            this.mBoxAttachFrameArrow.setVisibility(0);
        }
        if (this.mBoxAttachFrameText != null && this.mBoxAttachFrameText.getVisibility() != 0) {
            this.mBoxAttachFrameText.setVisibility(0);
        }
    }

    private void setAttachPositionBoxOutlineLayout(Point outlineSize, Point gapWithGuideBox) {
        if (this.mBoxAttachOutlineArrow != null) {
            if (this.mAppPanoramaDirection == 0) {
                outlineSize.y = this.mRectProgressBar.height();
            } else {
                outlineSize.x = this.mRectProgressBar.width();
            }
            adjustAttachPositionBoxOutline(outlineSize, gapWithGuideBox);
            if (this.mCurDegree == 90 || this.mCurDegree == Tag.IMAGE_DESCRIPTION) {
                int temp = outlineSize.y;
                outlineSize.y = outlineSize.x;
                outlineSize.x = temp;
            }
            LayoutParams lpArrow = (LayoutParams) this.mBoxAttachOutlineArrow.getLayoutParams();
            lpArrow.setLayoutDirection(0);
            lpArrow.width = outlineSize.x;
            lpArrow.height = outlineSize.y;
            this.mBoxAttachOutlineArrow.setLayoutParams(lpArrow);
            if (this.mBoxAttachOutlineText != null) {
                LayoutParams lpText = (LayoutParams) this.mBoxAttachOutlineText.getLayoutParams();
                lpText.setLayoutDirection(0);
                lpText.width = lpArrow.width;
                lpText.height = lpArrow.height;
                this.mBoxAttachOutlineText.setLayoutParams(lpText);
            }
        }
    }

    private void adjustAttachPositionBoxOutline(Point outlineSize, Point gapWithGuideBox) {
        switch (this.mGet.getDirection()[0]) {
            case LGKeyRec.EVENT_INCOMPLETE /*2*/:
                if (this.mBoxGuide.getHeight() < outlineSize.y) {
                    gapWithGuideBox.y = Util.distance(outlineSize.y, this.mBoxGuide.getHeight());
                    outlineSize.y = this.mBoxGuide.getHeight();
                }
            case LGKeyRec.EVENT_STARTED /*3*/:
                if (this.mBoxGuide.getHeight() < outlineSize.y) {
                    outlineSize.y = this.mBoxGuide.getHeight();
                }
            case LGKeyRec.EVENT_STOPPED /*4*/:
                if (this.mBoxGuide.getWidth() < outlineSize.x) {
                    outlineSize.x = this.mBoxGuide.getWidth();
                }
            case LGKeyRec.EVENT_START_OF_VOICING /*5*/:
                if (this.mBoxGuide.getWidth() < outlineSize.x) {
                    gapWithGuideBox.x = Util.distance(outlineSize.x, this.mBoxGuide.getWidth());
                    outlineSize.x = this.mBoxGuide.getWidth();
                }
            default:
        }
    }

    private void setAttachPositionBoxArrowTextLayout(Point start, Point outlineSize, Point gapWithGuideBox) {
        if (this.mBoxAttachFrameArrow != null && this.mBoxAttachFrameText != null) {
            int barStartMargin;
            if (this.mAppPanoramaDirection == 0) {
                barStartMargin = this.mBarLayoutMargin;
            } else {
                barStartMargin = 0;
            }
            int arrowLeftW = this.mBoxAttachRotateLayoutArrow.getLeft() + this.mBoxAttachOutlineArrow.getLeft();
            int arrowUpH = this.mBoxAttachRotateLayoutArrow.getTop() + this.mBoxAttachOutlineArrow.getTop();
            if (this.mCurDegree == 90 || this.mCurDegree == Tag.IMAGE_DESCRIPTION) {
                int gapWH = (int) (((double) Util.distance(outlineSize.x, outlineSize.y)) * 0.5d);
                if (this.mAppPanoramaDirection == 0) {
                    arrowLeftW -= gapWH;
                    arrowUpH += gapWH;
                } else {
                    arrowLeftW += gapWH;
                    arrowUpH -= gapWH;
                }
            }
            FrameLayout.LayoutParams lpFrameArrow = (FrameLayout.LayoutParams) this.mBoxAttachFrameArrow.getLayoutParams();
            lpFrameArrow.setMarginStart(((start.x + barStartMargin) - arrowLeftW) + gapWithGuideBox.x);
            lpFrameArrow.topMargin = ((this.mGet.getBarLayout().getTop() + start.y) - arrowUpH) + gapWithGuideBox.y;
            this.mBoxAttachFrameArrow.setLayoutParams(lpFrameArrow);
            this.mBoxAttachOutlineArrow.getLocationOnScreen(this.outlineLocationArrow);
            this.mBoxAttachOutlineText.getLocationOnScreen(this.outlineLocationText);
            int gapH = this.outlineLocationArrow[1] - this.outlineLocationText[1];
            FrameLayout.LayoutParams lpFrameText = (FrameLayout.LayoutParams) this.mBoxAttachFrameText.getLayoutParams();
            lpFrameText.setMarginStart(lpFrameText.getMarginStart() + (this.outlineLocationArrow[0] - this.outlineLocationText[0]));
            lpFrameText.topMargin += gapH;
            this.mBoxAttachFrameText.setLayoutParams(lpFrameText);
        }
    }

    private void adjustBoxPosition(Point attachedPos, Point guidePos) {
        switch (this.mGet.getDirection()[0]) {
            case LGKeyRec.EVENT_INCOMPLETE /*2*/:
            case LGKeyRec.EVENT_STARTED /*3*/:
                attachedPos.x -= this.mPreviewCropAdjust;
                guidePos.x -= this.mPreviewCropAdjust;
            default:
        }
    }

    private void drawBoxes() {
        if (!this.mNeedStop) {
            float f;
            int fw;
            int fh;
            boolean z;
            Point attachedPos = new Point();
            Point guidePos = new Point();
            Point attachedPosOrg = new Point();
            Point guidePosOrg = new Point();
            this.mGet.getMorphoPanoramaGP().getGuidancePos(attachedPos, guidePos);
            rotatePreviewPoints(attachedPos, this.mGet.getRoratePreview());
            rotatePreviewPoints(guidePos, this.mGet.getRoratePreview());
            adjustBoxPosition(attachedPos, guidePos);
            attachedPosOrg.x = attachedPos.x;
            attachedPosOrg.y = attachedPos.y;
            guidePosOrg.x = guidePos.x;
            guidePosOrg.y = guidePos.y;
            alignMagneticMarginHorizontal(attachedPos, guidePos);
            alignMagneticMarginVertical(attachedPos, guidePos);
            if (this.mBarW < this.mBarH) {
                f = ((float) this.mBarH) / ((float) this.mProgressImageH);
            } else {
                f = ((float) this.mBarW) / ((float) this.mProgressImageW);
            }
            double barLengthRatio = (double) f;
            attachedPos.x = (int) (((double) attachedPos.x) * barLengthRatio);
            attachedPos.y = (int) (((double) attachedPos.y) * barLengthRatio);
            guidePos.x = (int) (((double) guidePos.x) * barLengthRatio);
            guidePos.y = (int) (((double) guidePos.y) * barLengthRatio);
            this.offsetX = (int) (((double) this.offsetX) * barLengthRatio);
            this.offsetY = (int) (((double) this.offsetY) * barLengthRatio);
            if (this.mAppPanoramaDirection == 0) {
                this.offsetY += (this.mBarH / 4) + this.mGuideBoxLineWidth;
            } else {
                this.offsetX += (this.mBarW / 4) + this.mGuideBoxLineWidth;
            }
            if (this.mGet.getRotateUI() == 90 || this.mGet.getRotateUI() == Tag.IMAGE_DESCRIPTION) {
                fw = this.mGet.getInitParam().preview_height;
                fh = this.mGet.getInitParam().preview_width;
                if (ADJUST_CURRENT_FRAME_ASPECT_RATIO && this.mAppPanoramaDirection == 1) {
                    fh = (fw * 80) / 100;
                }
            } else {
                fw = this.mGet.getInitParam().preview_width;
                fh = this.mGet.getInitParam().preview_height;
                if (ADJUST_CURRENT_FRAME_ASPECT_RATIO && this.mAppPanoramaDirection == 0) {
                    fh = (fw * 80) / 100;
                }
            }
            fw = (int) ((((double) (fw / this.mGet.getInitParam().preview_shrink_ratio)) * barLengthRatio) * 0.5d);
            fh = (int) ((((double) (fh / this.mGet.getInitParam().preview_shrink_ratio)) * barLengthRatio) * 0.5d);
            if (ADJUST_CURRENT_FRAME_ASPECT_RATIO) {
                drawAdjustBox(guidePos, attachedPos, fw, fh);
            }
            this.mDistGuideAttachPos = Util.distance((float) guidePos.x, (float) guidePos.y, (float) attachedPos.x, (float) attachedPos.y);
            drawGuidePositionBox(guidePos, attachedPos, fw, fh);
            drawAttchPositionBox(attachedPos, fw, fh);
            drawAttachGuideVertical(attachedPosOrg);
            drawAttachGuideHorizontal(attachedPosOrg, guidePosOrg);
            int i = this.mGet.getDirection()[0];
            if (!this.isOnMagneticVertical || this.isShowHorizontalArrow) {
                z = false;
            } else {
                z = true;
            }
            showDirectionArrow(i, z);
        }
    }

    private void drawProgressOutline() {
        if (this.mOutline == null) {
            this.mOutline = (NinePatchDrawable) this.mGet.getResources().getDrawable(R.drawable.panorama_bg_box);
        }
        this.mOutline.setBounds(this.mRectPreview);
        this.mOutline.draw(this.mBarCanvas);
    }

    private void showDirectionArrow(int direction, boolean show) {
        LayoutParams lp = (LayoutParams) this.mBarArrow.getLayoutParams();
        Common.resetLayoutParameter(lp);
        lp.setLayoutDirection(0);
        switch (direction) {
            case LGKeyRec.EVENT_INCOMPLETE /*2*/:
                lp.addRule(10, 1);
                lp.addRule(14, 1);
                lp.topMargin = this.mDirectionArrowMargin;
                this.mBarArrow.setRotation(CameraConstants.SMARTZOOM_ZOOM_AREA_HEIGHT_UVGA);
                break;
            case LGKeyRec.EVENT_STARTED /*3*/:
                lp.addRule(12, 1);
                lp.addRule(14, 1);
                lp.bottomMargin = this.mDirectionArrowMargin;
                this.mBarArrow.setRotation(270.0f);
                break;
            case LGKeyRec.EVENT_STOPPED /*4*/:
                lp.addRule(21, 1);
                lp.addRule(15, 1);
                lp.setMarginEnd(this.mDirectionArrowMargin);
                this.mBarArrow.setRotation(CameraConstants.PIP_SUBWINDOW_INIT_HEIGHT_UVGA);
                break;
            case LGKeyRec.EVENT_START_OF_VOICING /*5*/:
                lp.addRule(20, 1);
                lp.addRule(15, 1);
                lp.setMarginStart(this.mDirectionArrowMargin);
                this.mBarArrow.setRotation(0.0f);
                break;
        }
        this.mBarArrow.setLayoutParams(lp);
        AnimationUtil.startShowingAnimation(this.mBarArrow, show, 300, null, false);
        AnimationUtil.startAnimationList(this.mBarArrow, show);
    }

    private void showDirectionGuide() {
        if (this.mGet.getDirection()[0] != 0 && this.mGet.getDirection()[0] != 1 && this.mGet.getDirection()[0] != 6) {
            if (this.mPrevDirection == 6) {
                showDirectionGuideDirAuto();
            }
            this.mGet.setVisiblePreviewBar(true, true);
            this.mGet.setVisiblePreviewMini(false, true);
            this.mGet.setVisibleArrowGuide(false, true, false);
            this.mGet.setVisibleTakingGuide(false, true);
            this.mGet.setStatus(3);
            this.mGet.setShutterButtonImage(true, this.mGet.getOrientationDegree());
        }
    }

    private void showDirectionGuideDirAuto() {
        int i = 1;
        switch (this.mGet.getDirection()[0]) {
            case LGKeyRec.EVENT_INCOMPLETE /*2*/:
            case LGKeyRec.EVENT_STARTED /*3*/:
                if (this.mAppDeviceRotation == 4) {
                    i = 0;
                }
                this.mAppPanoramaDirection = i;
                break;
            case LGKeyRec.EVENT_STOPPED /*4*/:
            case LGKeyRec.EVENT_START_OF_VOICING /*5*/:
                if (this.mAppDeviceRotation != 4) {
                    i = 0;
                }
                this.mAppPanoramaDirection = i;
                break;
        }
        ImageSize newPreviewSize = new ImageSize();
        ImageSize newOutputSize = new ImageSize();
        this.mGet.getMorphoPanoramaGP().getImageSize(newPreviewSize, newOutputSize);
        this.mGet.getInitParam().preview_img_width = newPreviewSize.width;
        this.mGet.getInitParam().preview_img_height = newPreviewSize.height;
        this.mGet.getInitParam().dst_img_width = newOutputSize.width;
        this.mGet.getInitParam().dst_img_height = newOutputSize.height;
        if (this.mGet.getDirection()[0] != 2 && this.mGet.getDirection()[0] != 3) {
            this.mPreviewCropAdjust = 0;
        } else if (this.mGet.getRoratePreview() == 90 || this.mGet.getRoratePreview() == Tag.IMAGE_DESCRIPTION) {
            this.mPreviewCropAdjust = (this.mGet.getInitParam().preview_img_height - (this.mGet.getInitParam().preview_height / this.mGet.getInitParam().preview_shrink_ratio)) / 2;
        } else {
            this.mPreviewCropAdjust = (this.mGet.getInitParam().preview_img_width - (this.mGet.getInitParam().preview_height / this.mGet.getInitParam().preview_shrink_ratio)) / 2;
        }
        allocateDisplayBuffers(this.mAppDeviceRotation + this.mAppPanoramaDirection);
        setBarLayoutMargin(this.mGet.getDirection()[0]);
        this.mCurDegree = this.mGet.getRoratePreview();
    }

    private void setBarLayoutMargin(int direction) {
        LayoutParams lp = (LayoutParams) this.mGet.getBarLayout().getLayoutParams();
        int dispW = Common.getPixelFromDimens(this.mGet.getActivity().getApplicationContext(), R.dimen.lcd_height);
        int dispH = Common.getPixelFromDimens(this.mGet.getActivity().getApplicationContext(), R.dimen.lcd_width);
        switch (direction) {
            case LGKeyRec.EVENT_INCOMPLETE /*2*/:
            case LGKeyRec.EVENT_STARTED /*3*/:
                if (this.mBarImage != null) {
                    lp.width = this.mBarImage.getWidth();
                }
                lp.height = (dispH - this.mBarLayoutMargin) - this.mBarLayoutMargin;
                lp.setMarginStart(0);
                lp.setMarginEnd(0);
                lp.topMargin = this.mQuickButtonWidth + this.mBarLayoutMargin;
                lp.bottomMargin = (this.mBarLayoutMargin + this.mPreviewPanelWidth) + this.mPreviewPanelBottmMargin;
                break;
            case LGKeyRec.EVENT_STOPPED /*4*/:
            case LGKeyRec.EVENT_START_OF_VOICING /*5*/:
                lp.width = (dispW - this.mBarLayoutMargin) - this.mBarLayoutMargin;
                if (this.mBarImage != null) {
                    lp.height = this.mBarImage.getHeight();
                }
                lp.setMarginStart(this.mBarLayoutMargin);
                lp.setMarginEnd(this.mBarLayoutMargin);
                lp.topMargin = 0;
                lp.bottomMargin = this.mPreviewPanelWidth + this.mPreviewPanelBottmMargin;
                break;
        }
        this.mGet.getBarLayout().setLayoutParams(lp);
    }

    private void doTakePreview(byte[] data) {
        CamLog.d(FaceDetector.TAG, "doTakePreview START");
        if (this.mGet == null || this.mGet.getCameraDevice() == null || this.mGet.getState() >= 4) {
            CamLog.d(FaceDetector.TAG, "doTakePreview EXIT");
            return;
        }
        this.mGet.increseCntReqShoot();
        onPictureTakenPreview(data);
        if (this.mGet.getNumOfShoot() > 1) {
            this.mGet.playPanoramaShutterSound();
        }
        this.mGet.getCameraDevice().addCallbackBuffer(this.mGet.getPreviewBuff());
        CamLog.d(FaceDetector.TAG, "doTakePreview END");
    }

    private void doTakePicture() {
        CamLog.d(FaceDetector.TAG, "doTakePicture START");
        if (this.mGet == null || this.mGet.getCameraDevice() == null || this.mGet.getState() >= 4) {
            CamLog.d(FaceDetector.TAG, "doTakePicture EXIT");
            return;
        }
        this.mGet.setRequestTakePicture(true);
        this.mGet.increseCntReqShoot();
        this.mGet.getCameraDevice().addCallbackBuffer(this.mGet.getPreviewBuff());
        this.mGet.getCameraDevice().takePicture(null, null, this);
        CamLog.d(FaceDetector.TAG, "doTakePicture END");
    }

    private void checkNeedFinish() {
        int end = 0;
        int lastGuide = 0;
        int lastGuideW = 0;
        int[] locBar = new int[]{0, 0};
        int[] locBox = new int[]{0, 0};
        this.mBoxGuide.getLocationOnScreen(locBox);
        this.mGet.getBarLayout().getLocationOnScreen(locBar);
        switch (this.mGet.getDirection()[0]) {
            case LGKeyRec.EVENT_INCOMPLETE /*2*/:
                end = locBar[1];
                lastGuide = locBox[1];
                lastGuideW = this.mBoxGuide.getHeight();
                break;
            case LGKeyRec.EVENT_STARTED /*3*/:
                end = locBar[1] + this.mGet.getBarLayout().getHeight();
                lastGuide = locBox[1] + this.mBoxGuide.getHeight();
                lastGuideW = this.mBoxGuide.getHeight();
                break;
            case LGKeyRec.EVENT_STOPPED /*4*/:
                end = locBar[0] + this.mGet.getBarLayout().getWidth();
                lastGuide = locBox[0] + this.mBoxGuide.getWidth();
                lastGuideW = this.mBoxGuide.getWidth();
                break;
            case LGKeyRec.EVENT_START_OF_VOICING /*5*/:
                end = locBar[0];
                lastGuide = locBox[0];
                lastGuideW = this.mBoxGuide.getWidth();
                break;
        }
        if (((float) lastGuideW) * 0.2f > ((float) Util.distance(end, lastGuide))) {
            this.mNeedStop = true;
            hideViews();
        }
    }

    private void showTakingGuiGuide(byte[] data) {
        if (this.mGet.getMorphoPanoramaGP().getCurrentDirection(this.mGet.getDirection()) != 0) {
            CamLog.e(FaceDetector.TAG, String.format("getCurrentDirection() -> 0x%x", new Object[]{Integer.valueOf(ret)}));
        }
        if (this.mPrevDirection == 0 || this.mPrevDirection == 1 || this.mPrevDirection == 6) {
            showDirectionGuide();
        } else {
            drawBarGuide();
        }
        if (!(this.mBarImage == null || this.mBarImage.isRecycled())) {
            this.mGet.getBar().setImageBitmap(this.mBarImage);
        }
        if (this.mImageID[0] >= 0) {
            checkNeedFinish();
            final byte[] finalData = data;
            this.mGet.getCameraDevice().addCallbackBuffer(null);
            this.mImageIDList.add(Integer.valueOf(this.mImageID[0]));
            this.mImageStatusList.add(Integer.valueOf(this.mStatus[0]));
            new Handler().post(new Runnable() {
                public void run() {
                    PlanePanoramaCallback.this.mGet.perfLockAcquire();
                    if (PlanePanoramaControllerBase.IS_PREVIEW_INPUT) {
                        PlanePanoramaCallback.this.doTakePreview(finalData);
                    } else {
                        PlanePanoramaCallback.this.doTakePicture();
                    }
                }
            });
        } else {
            this.mGet.getCameraDevice().addCallbackBuffer(this.mGet.getPreviewBuff());
        }
        this.mPrevDirection = this.mGet.getDirection()[0];
    }

    private void doTakingStateJob(byte[] data) {
        if (this.mGet.getMorphoPanoramaGP() != null && !this.mGet.isProcessingFinishTask() && this.mProgressImage != null && this.mBarImage != null) {
            this.mPreviewCount++;
            if (this.mGet.getMorphoPanoramaGP().attachPreview(data, this.mUseImage, this.mImageID, this.mMotionData, this.mStatus, this.mProgressImage) != 0) {
                CamLog.e(FaceDetector.TAG, String.format("attachPreview() -> 0x%x", new Object[]{Integer.valueOf(ret)}));
            }
            checkVeryFar();
            if (showTakingTextGuide()) {
                showTakingGuiGuide(data);
            }
        }
    }

    private void checkVeryFar() {
        if (this.mStatus[0] != 11 && this.mRectProgressBar != null && this.mBoxGuide != null) {
            float progressBarLength = 0.0f;
            int barMargin = this.mBarLayoutMargin * 2;
            switch (this.mGet.getDirection()[0]) {
                case LGKeyRec.EVENT_INCOMPLETE /*2*/:
                    progressBarLength = (float) ((this.mRectProgressBar.height() - (this.mRectProgressBar.height() - this.mBoxGuide.getTop())) + barMargin);
                    break;
                case LGKeyRec.EVENT_STARTED /*3*/:
                    progressBarLength = (float) (((this.mRectProgressBar.height() - this.mBoxGuide.getTop()) + this.mRectProgressBar.width()) + barMargin);
                    break;
                case LGKeyRec.EVENT_STOPPED /*4*/:
                    progressBarLength = (float) ((this.mRectProgressBar.width() - this.mBoxGuide.getLeft()) + barMargin);
                    break;
                case LGKeyRec.EVENT_START_OF_VOICING /*5*/:
                    progressBarLength = (float) ((this.mRectProgressBar.width() - (this.mRectProgressBar.width() - this.mBoxGuide.getLeft())) + barMargin);
                    break;
            }
            if (progressBarLength > 0.0f && this.mDistGuideAttachPos > progressBarLength) {
                this.mStatus[0] = 7;
            }
        }
    }
}
