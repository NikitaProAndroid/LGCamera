package com.lge.camera.postview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import com.lge.camera.R;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;

public class GalleryThumbnailLayout extends RelativeLayout {
    private static final String TAG = "CameraApp";
    private ImageView mCheckbox;
    private boolean mChecked;
    private int mIndex;
    private ImageView mThumbnail;

    public GalleryThumbnailLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mChecked = false;
    }

    public GalleryThumbnailLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mChecked = false;
    }

    public GalleryThumbnailLayout(Context contex, int tagIndex, BitmapDrawable thumb, int width, int height, boolean hasCheckbox) {
        super(contex);
        this.mChecked = false;
        this.mThumbnail = new ImageView(contex);
        if (this.mThumbnail == null) {
            CamLog.e(TAG, " mThumbnail is null.");
            return;
        }
        setTag(Integer.valueOf(tagIndex));
        setThumbBitmap(thumb, width, height);
        int thumb_padding = Common.getPixelFromDimens(contex, R.dimen.thumbnail_gallery_padding);
        this.mThumbnail.setTag(Integer.valueOf(tagIndex));
        this.mThumbnail.setBackgroundResource(R.drawable.camera_shotmode_continuous_list_normal);
        this.mThumbnail.setLayoutParams(new LayoutParams(width, height));
        this.mThumbnail.setPaddingRelative(thumb_padding, thumb_padding, thumb_padding, thumb_padding);
        this.mThumbnail.setFocusable(false);
        this.mIndex = tagIndex;
        addView(this.mThumbnail);
        if (hasCheckbox) {
            this.mCheckbox = new ImageView(contex);
            if (this.mCheckbox == null) {
                CamLog.e(TAG, " mCheckbox is null.");
                return;
            }
            if (this.mCheckbox.getResources() != null) {
                this.mCheckbox.setBackgroundResource(R.drawable.camera_gallery_thumb_uncheckable);
            }
            this.mCheckbox.setVisibility(4);
            this.mCheckbox.setLayoutParams(new LayoutParams(width, height));
            this.mThumbnail.setPaddingRelative(thumb_padding, thumb_padding, thumb_padding, thumb_padding);
            this.mCheckbox.setScaleType(ScaleType.FIT_XY);
            this.mCheckbox.setFocusable(false);
            addView(this.mCheckbox);
        }
    }

    public void setThumbBitmap(BitmapDrawable thumb, int width, int height) {
        if (thumb != null) {
            Bitmap bitmap = thumb.getBitmap();
            if (bitmap != null) {
                int thumbWidth = bitmap.getWidth();
                int thumbHeight = bitmap.getHeight();
                int targetWidth = width;
                int targetHeight = height;
                if (thumbWidth >= thumbHeight) {
                    targetWidth = width;
                    targetHeight = (width * thumbHeight) / thumbWidth;
                } else {
                    targetHeight = height;
                    targetWidth = (height * thumbWidth) / thumbHeight;
                }
                this.mThumbnail.setImageBitmap(Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true));
            }
        }
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        if (this.mThumbnail != null) {
            this.mThumbnail.setOnClickListener(onClickListener);
        }
    }

    public ImageView getThumbnailView() {
        return this.mThumbnail;
    }

    public void setSelected(boolean selected) {
        if (this.mThumbnail != null) {
            this.mThumbnail.setSelected(selected);
            if (selected) {
                this.mThumbnail.setBackgroundResource(R.drawable.camera_shotmode_continuous_list_normal);
            } else {
                this.mThumbnail.setBackgroundResource(R.drawable.camera_shotmode_continuous_list_normal);
            }
        }
    }

    public boolean getChecked() {
        CamLog.d(TAG, "thumbnail item " + this.mIndex + ", checked = " + this.mChecked);
        return this.mChecked;
    }

    public void setChecked() {
        if (this.mCheckbox != null && this.mCheckbox.getVisibility() == 0) {
            if (this.mChecked) {
                setChecked(false);
            } else {
                setChecked(true);
            }
        }
    }

    public void setChecked(boolean pressed) {
        if (this.mCheckbox != null && this.mCheckbox.getVisibility() == 0) {
            if (pressed) {
                this.mCheckbox.setBackgroundResource(R.drawable.camera_time_catch_shot_selected);
            } else {
                this.mCheckbox.setBackgroundResource(R.drawable.camera_time_catch_shot_normal);
            }
            this.mCheckbox.setPressed(pressed);
            this.mChecked = pressed;
        }
    }

    public boolean getCheckboxVisibility() {
        if (this.mCheckbox == null || this.mCheckbox.getVisibility() != 0) {
            return false;
        }
        return true;
    }

    public void showCheckbox(boolean visible) {
        if (this.mCheckbox != null) {
            if (!visible) {
                setChecked(false);
                this.mCheckbox.setVisibility(4);
            } else if (this.mCheckbox.getVisibility() != 0) {
                this.mCheckbox.setVisibility(0);
            }
        }
    }

    public int getIndex() {
        return this.mIndex;
    }

    public void setThumbSize(int width, int height, int leftMargin) {
        CamLog.d(TAG, "GalleryThumbnailLayout : width = " + width + ", height = " + height);
        LayoutParams thislp = (LayoutParams) getLayoutParams();
        thislp.width = width;
        thislp.height = height;
        thislp.leftMargin = leftMargin;
        setLayoutParams(thislp);
        if (this.mThumbnail != null) {
            LayoutParams thumblp = (LayoutParams) this.mThumbnail.getLayoutParams();
            thumblp.width = width;
            thumblp.height = height;
            this.mThumbnail.setLayoutParams(thumblp);
        }
        if (this.mCheckbox != null) {
            LayoutParams checkBoxlp = (LayoutParams) this.mCheckbox.getLayoutParams();
            checkBoxlp.width = width;
            checkBoxlp.height = height;
            this.mCheckbox.setLayoutParams(checkBoxlp);
        }
        postInvalidate();
    }

    public void unbind() {
        CamLog.d(TAG, "GalleryThumbnailLayout unbind()");
        if (this.mThumbnail != null) {
            if (this.mThumbnail.getDrawable() != null) {
                this.mThumbnail.getDrawable().setCallback(null);
                this.mThumbnail.setImageDrawable(null);
            }
            if (this.mThumbnail.getBackground() != null) {
                this.mThumbnail.getBackground().setCallback(null);
                this.mThumbnail.setBackground(null);
            }
        }
        if (this.mCheckbox != null && this.mCheckbox.getBackground() != null) {
            this.mCheckbox.getBackground().setCallback(null);
            this.mCheckbox.setBackground(null);
        }
    }
}
