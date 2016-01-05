package com.lge.camera;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import com.lge.camera.properties.MultimediaProperties;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class PostviewAttachActivity extends PostviewNormalActivity {
    private OnClickListener mAttachButtonListener;
    private OnClickListener mFinishListener;
    private OnClickListener mPlayButtonListener;

    public PostviewAttachActivity() {
        this.mAttachButtonListener = new OnClickListener() {
            public void onClick(View v) {
                CamLog.d(FaceDetector.TAG, "Attach button clicked.");
                if (PostviewAttachActivity.this.checkPauseAndAutoReview()) {
                    Intent intent = PostviewAttachActivity.this.getIntent();
                    intent.putExtra("doAttach", true);
                    if (PostviewAttachActivity.this.mPostViewParameters.getApplicationMode() == 0) {
                        intent.putExtra("postview_mode", true);
                    } else {
                        intent.putExtra("postview_mode", false);
                    }
                    PostviewAttachActivity.this.setResult(100, intent);
                    PostviewAttachActivity.this.finish();
                }
            }
        };
        this.mFinishListener = new OnClickListener() {
            public void onClick(View v) {
                PostviewAttachActivity.this.finish();
            }
        };
        this.mPlayButtonListener = new OnClickListener() {
            public void onClick(View v) {
                CamLog.d(FaceDetector.TAG, "mPlayButtonListener clicked.");
                if (PostviewAttachActivity.this.checkPauseAndAutoReview()) {
                    Intent intent = new Intent("android.intent.action.VIEW");
                    intent.setDataAndType(PostviewAttachActivity.this.mPostViewParameters.getSavedUri(), MultimediaProperties.VIDEO_MIME_TYPE);
                    intent.putExtra("mimeType", MultimediaProperties.VIDEO_MIME_TYPE);
                    intent.putExtra("android.intent.extra.finishOnCompletion", true);
                    try {
                        PostviewAttachActivity.this.startActivity(intent);
                    } catch (ActivityNotFoundException ex) {
                        CamLog.e(FaceDetector.TAG, "ActivityNotFoundException : ", ex);
                        PostviewAttachActivity.this.toast(PostviewAttachActivity.this.getString(R.string.error_not_exist_app));
                    }
                }
            }
        };
    }

    protected void doPreProcessOnCreate() {
    }

    protected void doProcessOnCreate() {
        this.isFromCreateProcess = true;
        setUpAttachMenu();
    }

    protected void doProcessOnResume() {
        if (!this.isFromCreateProcess) {
            if (checkValidateImage()) {
                setUpAttachMenu();
            } else {
                postOnUiThread(this.mExitInteraction);
                this.isFromCreateProcess = false;
                return;
            }
        }
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
            default:
                return false;
        }
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    protected void postviewShow() {
        CamLog.d(FaceDetector.TAG, "TIME_CHECK show()");
        View postView = findViewById(R.id.postview_shotmode_attach);
        if (postView == null) {
            CamLog.w(FaceDetector.TAG, "postviewShow : inflate view fail.");
            return;
        }
        if (postView.getVisibility() != 0) {
            postView.setVisibility(0);
        }
        loadSingleCapturedImages();
    }

    protected void setupLayout() {
        inflateStub(R.id.stub_attach_postview);
    }

    private void setUpAttachMenu() {
        Button mAttach = (Button) findViewById(R.id.btn_ok);
        Button mFinish = (Button) findViewById(R.id.btn_cancel);
        ImageButton mPlay = (ImageButton) findViewById(R.id.btn_play);
        if (this.mPostViewParameters.getApplicationMode() == 0) {
            mPlay.setVisibility(4);
        } else {
            mPlay.setVisibility(0);
        }
        mAttach.setOnClickListener(this.mAttachButtonListener);
        mFinish.setOnClickListener(this.mFinishListener);
        mPlay.setEnabled(true);
        mPlay.setOnClickListener(this.mPlayButtonListener);
    }
}
