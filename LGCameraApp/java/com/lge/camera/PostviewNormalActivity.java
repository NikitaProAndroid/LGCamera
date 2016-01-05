package com.lge.camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.lge.camera.util.BitmapManager;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.ExifUtil;
import com.lge.camera.util.ImageManager;
import com.lge.camera.util.Util;
import com.lge.olaworks.library.FaceDetector;
import java.io.File;

public class PostviewNormalActivity extends ShotPostviewActivity {
    protected void doPreProcessOnCreate() {
    }

    protected void doProcessOnCreate() {
        this.isFromCreateProcess = true;
    }

    protected void doProcessOnResume() {
        if (this.isFromCreateProcess || checkValidateImage()) {
            this.isFromCreateProcess = false;
            return;
        }
        postOnUiThread(this.mExitInteraction);
        this.isFromCreateProcess = false;
    }

    protected void doProcessOnPause() {
    }

    protected void doProcessOnDestroy() {
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 16908332:
                doBackKeyInPostview();
                return true;
            case R.id.postview_share /*2131559010*/:
                onCreateDialog(1, this.mPostViewParameters.getApplicationMode());
                return true;
            case R.id.postview_set_as /*2131559011*/:
                onCreateDialog(2, this.mPostViewParameters.getApplicationMode());
                return true;
            case R.id.postview_delete /*2131559012*/:
                onCreateDialog(4, this.mPostViewParameters.getApplicationMode());
                return true;
            default:
                return false;
        }
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.shot_postview_action_menu_normal, menu);
        return super.onPrepareOptionsMenu(menu);
    }

    protected void postviewShow() {
        CamLog.d(FaceDetector.TAG, "TIME_CHECK show()");
        View postView = findViewById(R.id.postview_shotmode_normal);
        if (postView == null) {
            CamLog.w(FaceDetector.TAG, "postviewShow : inflate view fail.");
            return;
        }
        if (postView.getVisibility() != 0) {
            postView.setVisibility(0);
        }
        loadSingleCapturedImages();
    }

    protected void reloadedPostview() {
        if (this.mCapturedBitmap != null) {
            ((ImageView) findViewById(R.id.captured_image)).setImageBitmap(this.mCapturedBitmap);
        }
    }

    protected boolean loadSingleCapturedImages() {
        if (this.mPostViewParameters == null || this.mPostViewParameters.getUriList() == null || this.mPostViewParameters.getUriList().size() == 0) {
            CamLog.e(FaceDetector.TAG, "mUriList.size() is 0 !!");
            return false;
        }
        setFileNameLayout();
        ImageView postview = (ImageView) findViewById(R.id.captured_image);
        if (this.mCapturedBitmap != null) {
            reloadedPostview();
            return true;
        }
        try {
            Uri capturedImageUri = (Uri) this.mPostViewParameters.getUriList().get(0);
            if (this.mCapturedBitmap != null) {
                this.mCapturedBitmap.recycle();
                this.mCapturedBitmap = null;
            }
            this.mCapturedBitmap = loadCapturedImage(capturedImageUri, 0);
            if (this.mCapturedBitmap == null) {
                return false;
            }
            postview.setImageBitmap(this.mCapturedBitmap);
            return true;
        } catch (Exception e) {
            CamLog.e(FaceDetector.TAG, "Exception!", e);
        }
    }

    protected Bitmap loadCapturedImage(Uri uri, int degrees) {
        CamLog.d(FaceDetector.TAG, String.format("Load captured image:%s, degrees:%d", new Object[]{uri, Integer.valueOf(degrees)}));
        Bitmap bmp = null;
        if (this.mPostViewParameters != null) {
            if (this.mPostViewParameters.getApplicationMode() == 0) {
                int[] dstSize = Util.getFitSizeOfBitmapForLCD(getActivity(), ExifUtil.getExifWidth(BitmapManager.getRealPathFromURI(getActivity(), uri)), ExifUtil.getExifHeight(BitmapManager.getRealPathFromURI(getActivity(), uri)));
                bmp = ImageManager.loadScaledBitmap(getContentResolver(), uri.toString(), dstSize[0], dstSize[1]);
            } else {
                bmp = getLastThumbnail(uri, this.mPostViewParameters.getApplicationMode());
            }
        }
        if (bmp == null) {
            CamLog.e(FaceDetector.TAG, "LoadBitmap fail!");
            return null;
        }
        return this.mImageHandler.getImage(bmp, ExifUtil.getExifOrientationDegree(BitmapManager.getRealPathFromURI(getActivity(), uri)), false);
    }

    protected void setFileName() {
        TextView tv = (TextView) findViewById(R.id.file_name);
        if (tv != null && this.mPostViewParameters != null) {
            tv.setText(this.mPostViewParameters.getSaveFileName());
            tv.setVisibility(0);
        }
    }

    protected void setupLayout() {
        inflateStub(R.id.stub_noraml_postview);
    }

    public void doDeletePositiveClick() {
        try {
            if (!(this.mPostViewParameters == null || this.mPostViewParameters.getUriList() == null)) {
                deleteImage(this.mPostViewParameters.getSaveFileName(), (Uri) this.mPostViewParameters.getUriList().get(0));
            }
            deleteFinished();
        } catch (Exception e) {
            CamLog.e(FaceDetector.TAG, "Exception!", e);
        }
    }

    protected boolean checkValidateImage() {
        try {
            if (!(this.mPostViewParameters == null || this.mPostViewParameters.getUriList() == null)) {
                String tempFilePath = BitmapManager.getRealPathFromURI(getActivity(), (Uri) this.mPostViewParameters.getUriList().get(0));
                if (tempFilePath == null) {
                    return false;
                }
                if (!new File(tempFilePath).exists()) {
                    this.mPostViewParameters.getUriList().remove(0);
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            CamLog.e(FaceDetector.TAG, "Exception!", e);
            return false;
        }
    }

    protected void deleteFinished() {
        Intent intent = getIntent();
        intent.putExtra("delete_done", true);
        setResult(100, intent);
        finish();
    }

    protected void doBackKeyInPostview() {
        CamLog.d(FaceDetector.TAG, "KEYCODE_BACK");
        if (this.mPause || getActivity().isFinishing()) {
            CamLog.d(FaceDetector.TAG, "KEYCODE_BACK - return...");
        } else {
            finish();
        }
    }
}
