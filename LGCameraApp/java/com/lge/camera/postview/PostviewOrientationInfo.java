package com.lge.camera.postview;

import android.app.Activity;
import android.view.OrientationEventListener;
import android.view.WindowManager;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.camera.util.DialogCreater;
import com.lge.olaworks.define.Limit;
import com.lge.olaworks.library.FaceDetector;
import com.lge.voiceshutter.library.LGKeyRec;

public class PostviewOrientationInfo {
    private int mDisplayOrientationSetting;
    private PostviewOrientationInfoFunction mGet;
    private int mOrientation;
    private boolean mOrientationChangeEnabled;
    private OrientationEventListener mOrientationListener;
    private Runnable mSetOrientationReload;

    public interface PostviewOrientationInfoFunction {
        Activity getActivity();

        PostViewParameters getPostViewParameters();

        boolean isPausing();
    }

    public PostviewOrientationInfo(PostviewOrientationInfoFunction function) {
        this.mGet = null;
        this.mOrientation = 0;
        this.mOrientationListener = null;
        this.mOrientationChangeEnabled = false;
        this.mDisplayOrientationSetting = 0;
        this.mSetOrientationReload = new Runnable() {
            public void run() {
                if (!PostviewOrientationInfo.this.mGet.getActivity().isFinishing()) {
                    PostviewOrientationInfo.this.mGet.getActivity().setRequestedOrientation(PostviewOrientationInfo.this.getActivityOrientation());
                }
            }
        };
        this.mGet = function;
    }

    public int getOrientation() {
        return this.mOrientation;
    }

    public boolean getOrientationListenerEnable() {
        return this.mOrientationChangeEnabled;
    }

    public void enableOrientationListener(boolean enable) {
        if (this.mGet.isPausing()) {
            CamLog.d(FaceDetector.TAG, "Activity is finishing. so listener must be disabled");
            enable = false;
        }
        this.mOrientationChangeEnabled = enable;
        if (this.mOrientationListener == null) {
            return;
        }
        if (enable) {
            this.mOrientationListener.enable();
        } else {
            this.mOrientationListener.disable();
        }
    }

    public void releaseOrientationListener() {
        this.mOrientationListener = null;
    }

    public void setOrientationListener() {
        CamLog.v(FaceDetector.TAG, "setOrientationListener");
        if (!this.mOrientationChangeEnabled) {
            if (this.mOrientationListener != null) {
                this.mOrientationListener.disable();
                this.mOrientationListener = null;
            }
            this.mOrientationListener = new OrientationEventListener(this.mGet.getActivity(), 2) {
                public void onOrientationChanged(int orientation) {
                    if (orientation != -1 && PostviewOrientationInfo.this.mOrientationChangeEnabled) {
                        if (Common.isScreenLocked()) {
                            setOrientationPortrait();
                        } else if (orientation > CameraConstants.ORIENTATION_LANDSCAPE_DEGREE_FROM && orientation < CameraConstants.ORIENTATION_LANDSCAPE_DEGREE_TO) {
                            setOrientationLandscape();
                        } else if (orientation > CameraConstants.ORIENTATION_PORTRAIT_DEGREE_FROM || orientation < 20) {
                            setOrientationPortrait();
                        } else if (orientation > CameraConstants.ORIENTATION_PORTRAIT_OPPOSITE_DEGREE_FROM && orientation < Limit.OLA_FIFO_DATA_MAX_SIZE_BYTE) {
                            setOrientationPortraitOpposite();
                        } else if (orientation > 70 && orientation < DialogCreater.DIALOG_ID_HELP_INTELLIGENT_AUTO_MODE) {
                            setOrientationLandscapeOpposite();
                        }
                    }
                }

                private void setOrientationLandscapeOpposite() {
                    if (PostviewOrientationInfo.this.mOrientation != 2) {
                        PostviewOrientationInfo.this.mOrientation = 2;
                        PostviewOrientationInfo.this.setActivityOrientationRun(0);
                        CamLog.d(FaceDetector.TAG, "mOrientationListener:ORIENTATION_LANDSCAPE_OPPOSITE");
                    }
                }

                private void setOrientationPortraitOpposite() {
                    if (PostviewOrientationInfo.this.mDisplayOrientationSetting == 0) {
                        if (PostviewOrientationInfo.this.mOrientation != 1) {
                            PostviewOrientationInfo.this.mOrientation = 1;
                            PostviewOrientationInfo.this.setActivityOrientationRun(0);
                            CamLog.d(FaceDetector.TAG, "mOrientationListener:mDisplayOrientationSetting is set 1 = ORIENTATION_PORTRAIT");
                        }
                    } else if (PostviewOrientationInfo.this.mOrientation != 3) {
                        PostviewOrientationInfo.this.mOrientation = 3;
                        PostviewOrientationInfo.this.setActivityOrientationRun(0);
                        CamLog.d(FaceDetector.TAG, "mOrientationListener:ORIENTATION_PORTRAIT_OPPOSITE");
                    }
                }

                private void setOrientationPortrait() {
                    if (PostviewOrientationInfo.this.mOrientation != 1) {
                        PostviewOrientationInfo.this.mOrientation = 1;
                        PostviewOrientationInfo.this.setActivityOrientationRun(0);
                        CamLog.d(FaceDetector.TAG, "mOrientationListener:ORIENTATION_PORTRAIT");
                    }
                }

                private void setOrientationLandscape() {
                    if (PostviewOrientationInfo.this.mOrientation != 0) {
                        PostviewOrientationInfo.this.mOrientation = 0;
                        PostviewOrientationInfo.this.setActivityOrientationRun(0);
                        CamLog.d(FaceDetector.TAG, "mOrientationListener:ORIENTATION_LANDSCAPE");
                    }
                }
            };
        }
    }

    public void setActivityOrientationRun(int delay) {
        if (this.mGet.getPostViewParameters() != null) {
            this.mGet.getPostViewParameters().setPreviewOrientation(-1);
            this.mSetOrientationReload.run();
        }
    }

    public void setOrientationByPreview(int orientation) {
        if (orientation >= 0 && this.mOrientation != orientation) {
            this.mOrientation = orientation;
            setActivityOrientationRun(0);
        }
    }

    public void setOrientationByWindowOrientation() {
        this.mOrientation = getWindowOrientation();
    }

    public int getWindowOrientation() {
        int rotation = ((WindowManager) this.mGet.getActivity().getSystemService("window")).getDefaultDisplay().getRotation();
        int oldOrientation = this.mOrientation;
        int newOrientation = 0;
        switch (rotation) {
            case LGKeyRec.EVENT_INVALID /*0*/:
                newOrientation = 1;
                break;
            case LGKeyRec.EVENT_NO_MATCH /*1*/:
                newOrientation = 0;
                break;
            case LGKeyRec.EVENT_INCOMPLETE /*2*/:
                newOrientation = 3;
                break;
            case LGKeyRec.EVENT_STARTED /*3*/:
                if (!isHardKeyboradShowing()) {
                    newOrientation = 2;
                    break;
                }
                newOrientation = 0;
                break;
        }
        CamLog.d(FaceDetector.TAG, "getWindowOrientation:Old orientation = " + oldOrientation + ", New orientation = " + newOrientation + " isHardKeyboradShowing() = " + isHardKeyboradShowing());
        return newOrientation;
    }

    public int getActivityOrientation() {
        switch (this.mOrientation) {
            case LGKeyRec.EVENT_NO_MATCH /*1*/:
                return 1;
            case LGKeyRec.EVENT_INCOMPLETE /*2*/:
                return 8;
            case LGKeyRec.EVENT_STARTED /*3*/:
                return 9;
            default:
                return 0;
        }
    }

    public boolean isHardKeyboradShowing() {
        if (!ProjectVariables.isSupportHardKeyborad()) {
            return false;
        }
        if (this.mGet.getActivity().getResources().getConfiguration().hardKeyboardHidden == 1) {
            return true;
        }
        return false;
    }

    public void setDisplayOrientationSettingValue(int value) {
        this.mDisplayOrientationSetting = value;
    }
}
