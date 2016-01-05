package com.lge.camera.components;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import com.lge.camera.R;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.camera.util.Util;
import com.lge.olaworks.library.FaceDetector;
import java.io.IOException;

public class ThumbnailController {
    private ImageView mButton;
    private int mCircleRadius;
    private ContentResolver mContentResolver;
    private int mDefaultImage;
    private Resources mResources;
    private boolean mShouldAnimateThumb;
    private Bitmap mThumb;
    private TransitionDrawable mThumbTransition;
    private Drawable[] mThumbs;
    private Uri mUri;

    public ThumbnailController(Resources resources, ImageView button, ContentResolver contentResolver) {
        this.mDefaultImage = R.drawable.last_picture_default;
        this.mCircleRadius = 0;
        this.mResources = resources;
        this.mContentResolver = contentResolver;
        this.mButton = button;
        this.mButton.setScaleType(ScaleType.FIT_XY);
        this.mButton.setImageResource(R.drawable.last_picture_default);
        this.mCircleRadius = Common.getPixelFromDimens(button.getContext(), R.dimen.review_thumbnail_circle_radius);
    }

    public ThumbnailController(Resources resources, ImageView button, ContentResolver contentResolver, boolean isSecureCamera) {
        this.mDefaultImage = R.drawable.last_picture_default;
        this.mCircleRadius = 0;
        this.mResources = resources;
        this.mContentResolver = contentResolver;
        this.mButton = button;
        this.mButton.setScaleType(ScaleType.FIT_XY);
        setSecureDefaultImage(isSecureCamera);
        this.mButton.setImageResource(this.mDefaultImage);
        this.mCircleRadius = Common.getPixelFromDimens(button.getContext(), R.dimen.review_thumbnail_circle_radius);
    }

    public void setSecureDefaultImage(boolean isSecureCamera) {
        this.mDefaultImage = isSecureCamera ? R.drawable.last_picture_default_lock : R.drawable.last_picture_default;
    }

    public void close() {
        CamLog.d(FaceDetector.TAG, " memory free");
        if (this.mThumb != null) {
            this.mThumb.recycle();
            this.mThumb = null;
        }
        if (this.mThumbs != null) {
            for (int i = 0; i < this.mThumbs.length; i++) {
                Util.recycleBitmapDrawable(this.mThumbs[i]);
                this.mThumbs[i] = null;
            }
        }
        this.mThumbTransition = null;
        this.mContentResolver = null;
        this.mResources = null;
        this.mButton = null;
        this.mThumbs = null;
    }

    public void setButton(ImageView button) {
        this.mButton = button;
        this.mButton.setScaleType(ScaleType.FIT_XY);
        this.mButton.setImageResource(this.mDefaultImage);
    }

    public void setData(Uri uri, Bitmap original, boolean useTransition) {
        if (uri == null || original == null) {
            uri = null;
            original = null;
        }
        this.mUri = uri;
        setupTransition(original, useTransition);
    }

    public Uri getUri() {
        return this.mUri;
    }

    public void startTransition(int time) {
        if (this.mShouldAnimateThumb) {
            this.mThumbTransition.startTransition(time);
            this.mShouldAnimateThumb = false;
        }
    }

    public int getThumbnailWidth() {
        return (this.mButton.getLayoutParams().width - this.mButton.getPaddingStart()) - this.mButton.getPaddingEnd();
    }

    public int getThumbnailHeight() {
        return (this.mButton.getLayoutParams().height - this.mButton.getPaddingTop()) - this.mButton.getPaddingBottom();
    }

    private void setupTransition(Bitmap source, boolean useTransition) {
        int miniThumbWidth = getThumbnailWidth();
        int miniThumbHeight = getThumbnailHeight();
        if (source == null) {
            this.mThumb = Util.getRoundedImage(BitmapFactory.decodeResource(this.mResources, this.mDefaultImage), miniThumbWidth, miniThumbHeight, this.mCircleRadius);
            CamLog.d(FaceDetector.TAG, String.format("Set thumbnail empty", new Object[0]));
        } else {
            CamLog.d(FaceDetector.TAG, "before Extract from bitmap");
            this.mThumb = Util.getRoundedImage(ThumbnailUtils.extractThumbnail(source, miniThumbWidth, miniThumbHeight), miniThumbWidth, miniThumbHeight, this.mCircleRadius);
            if (this.mThumb != null) {
                CamLog.d(FaceDetector.TAG, String.format("after Extract from bitmap(%dx%d) to thumb(%dx%d)", new Object[]{Integer.valueOf(source.getWidth()), Integer.valueOf(source.getHeight()), Integer.valueOf(this.mThumb.getWidth()), Integer.valueOf(this.mThumb.getHeight())}));
            }
        }
        if (useTransition) {
            Drawable drawable;
            if (this.mThumbs == null) {
                Bitmap bmpGalleryButton = BitmapFactory.decodeResource(this.mResources, this.mDefaultImage);
                this.mThumbs = new Drawable[2];
                this.mThumbs[0] = new BitmapDrawable(this.mResources, bmpGalleryButton);
                this.mThumbs[1] = new BitmapDrawable(this.mResources, this.mThumb);
                this.mThumbTransition = new TransitionDrawable(this.mThumbs);
                drawable = this.mThumbTransition;
                this.mShouldAnimateThumb = true;
            } else {
                this.mThumbs[0] = this.mThumbs[1];
                this.mThumbs[1] = new BitmapDrawable(this.mResources, this.mThumb);
                this.mThumbTransition = new TransitionDrawable(this.mThumbs);
                drawable = this.mThumbTransition;
                this.mShouldAnimateThumb = true;
            }
            if (source == null) {
                this.mButton.setImageBitmap(this.mThumb);
                return;
            } else {
                this.mButton.setImageDrawable(drawable);
                return;
            }
        }
        if (this.mThumbs == null) {
            this.mThumbs = new Drawable[2];
        }
        this.mThumbs[0] = new BitmapDrawable(this.mResources, BitmapFactory.decodeResource(this.mResources, this.mDefaultImage));
        this.mThumbs[1] = new BitmapDrawable(this.mResources, this.mThumb);
        this.mShouldAnimateThumb = false;
        this.mButton.setImageDrawable(this.mThumbs[1]);
    }

    public boolean isUriValid() {
        if (this.mUri == null) {
            return false;
        }
        try {
            ParcelFileDescriptor pfd = this.mContentResolver.openFileDescriptor(this.mUri, "r");
            if (pfd == null) {
                CamLog.e(FaceDetector.TAG, "Fail to open URI.");
                return false;
            }
            pfd.close();
            return true;
        } catch (IOException ex) {
            CamLog.e(FaceDetector.TAG, "IOException : ", ex);
            return false;
        }
    }
}
