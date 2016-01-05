package com.lge.camera.controller;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout.LayoutParams;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.command.Command;
import com.lge.camera.components.RotateLayout;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.setting.Setting;
import com.lge.camera.setting.SettingMenuAdapter;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Util;
import com.lge.morpho.utils.multimedia.MediaProviderUtils;
import com.lge.olaworks.define.Ola_Exif.Tag;
import com.lge.olaworks.library.FaceDetector;
import com.lge.voiceshutter.library.LGKeyRec;

public class QuickFunctionSettingController extends Controller {
    protected int CONFIG_ITEM_HEIGHT;
    protected int DIVIDER_HEIGHT;
    protected int LCD_HEIGHT;
    protected int LCD_WIDTH;
    protected int MARGIN_HEIGHT;
    protected int PANEL_WIDTH;
    protected int QFL_SETTING_HEIGHT;
    protected int QFL_SETTING_MARGIN_LEFT;
    protected int QFL_SETTING_MARGIN_TOP;
    protected int QFL_SETTING_WIDTH;
    private int mDegree;
    protected ListView mQuickFunctionSettingListView;
    protected View mQuickFunctionSettingView;
    private boolean mQuickFunctionSettingViewRemoving;
    protected SettingMenuAdapter mSettingAdapter;
    private OnItemClickListener mSettingListViewItemClickListener;
    private OnTouchListener mSettingListViewItemTouchListener;

    public QuickFunctionSettingController(ControllerFunction function) {
        super(function);
        this.DIVIDER_HEIGHT = 2;
        this.mDegree = -1;
        this.mQuickFunctionSettingViewRemoving = false;
        this.mSettingListViewItemClickListener = new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                final String selectedChildName = QuickFunctionSettingController.this.mGet.getCurrentSettingMenu().getCurrentMenu().getChild(position).getParameterValue();
                if (QuickFunctionSettingController.this.mGet.isPressedShutterButton() || QuickFunctionSettingController.this.mGet.isSwitcherLeverPressed()) {
                    CamLog.d(FaceDetector.TAG, "ShutterButton or SwitcherLever is pressed -> block child menu click");
                    return;
                }
                String currentMenukey = QuickFunctionSettingController.this.mGet.getCurrentMenuKey();
                if ((Setting.KEY_CAMERA_PICTURESIZE.equals(currentMenukey) || Setting.KEY_CAMERA_SHOT_MODE.equals(currentMenukey) || Setting.KEY_VIDEO_RECORD_MODE.equals(currentMenukey)) && position == QuickFunctionSettingController.this.mGet.getSelectedChildIndex()) {
                    CamLog.d(FaceDetector.TAG, "Exit OnItemClickListener because press down same setting");
                    QuickFunctionSettingController.this.mGet.postOnUiThread(new Runnable() {
                        public void run() {
                            QuickFunctionSettingController.this.mGet.removePostRunnable(this);
                            QuickFunctionSettingController.this.mGet.doCommand(Command.HIDE_QUICK_FUNCTION_SETTING_MENU);
                        }
                    });
                    return;
                }
                if (!CameraConstants.TYPE_RECORDMODE_LIVE_EFFECT.equals(selectedChildName) || QuickFunctionSettingController.this.mGet.findPreference(Setting.KEY_VIDEO_RECORD_MODE) == null) {
                    QuickFunctionSettingController.this.mGet.setSelectedChild(position);
                    QuickFunctionSettingController.this.mGet.setQuickFunctionControllerAllMenuIcons();
                }
                QuickFunctionSettingController.this.mGet.doCommandUi(Command.HIDE_QUICK_FUNCTION_SETTING_MENU);
                final String command = QuickFunctionSettingController.this.mGet.getSelectedChild().getCommand();
                QuickFunctionSettingController.this.mGet.postOnUiThread(new Runnable() {
                    public void run() {
                        QuickFunctionSettingController.this.mGet.removePostRunnable(this);
                        if (!CameraConstants.TYPE_RECORDMODE_LIVE_EFFECT.equals(selectedChildName) || QuickFunctionSettingController.this.mGet.findPreference(Setting.KEY_VIDEO_RECORD_MODE) == null) {
                            QuickFunctionSettingController.this.mGet.doCommand(command);
                        } else {
                            QuickFunctionSettingController.this.mGet.doCommand(command, new String(), selectedChildName);
                        }
                    }
                });
                QuickFunctionSettingController.this.mSettingAdapter.update();
            }
        };
        this.mSettingListViewItemTouchListener = new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent me) {
                if ((me.getAction() == 1 && view != null && view == QuickFunctionSettingController.this.mQuickFunctionSettingListView && !QuickFunctionSettingController.this.isInView(view, me)) || me.getPointerCount() > 1) {
                    QuickFunctionSettingController.this.mGet.runOnUiThread(new Runnable() {
                        public void run() {
                            if (QuickFunctionSettingController.this.checkMediator() && QuickFunctionSettingController.this.mSettingAdapter != null) {
                                QuickFunctionSettingController.this.mGet.removePostRunnable(this);
                                QuickFunctionSettingController.this.mSettingAdapter.update();
                            }
                        }
                    });
                }
                return false;
            }
        };
        this.mDegree = this.mGet.getOrientationDegree();
    }

    public void displaySettingView(String key) {
        int index;
        CamLog.d(FaceDetector.TAG, "QuickFunctionSettingController settingView : displaySettingView-start");
        this.QFL_SETTING_WIDTH = getPixelFromDimens(R.dimen.quick_function_settingview_width);
        this.QFL_SETTING_HEIGHT = getPixelFromDimens(R.dimen.quick_function_settingview_height);
        this.QFL_SETTING_MARGIN_LEFT = getPixelFromDimens(R.dimen.quick_function_setting_rotatelayout_marginLeft);
        this.QFL_SETTING_MARGIN_TOP = getPixelFromDimens(R.dimen.quick_function_setting_rotatelayout_marginTop);
        this.LCD_WIDTH = getPixelFromDimens(R.dimen.lcd_width);
        this.LCD_HEIGHT = getPixelFromDimens(R.dimen.lcd_height);
        this.PANEL_WIDTH = getPixelFromDimens(R.dimen.preview_panel_width);
        this.CONFIG_ITEM_HEIGHT = getPixelFromDimens(R.dimen.config_item_layout_height);
        this.MARGIN_HEIGHT = this.CONFIG_ITEM_HEIGHT / 2;
        if (this.mGet.getBackupCurrentMenuIndex() == -1) {
            this.mGet.setBackupCurrentMenuIndex(this.mGet.getCurrentSettingMenuIndex());
        }
        this.mDegree = this.mGet.getOrientationDegree();
        if (key == null) {
            index = 0;
        } else {
            index = this.mGet.getCurrentSettingMenuIndex(key);
        }
        this.mGet.setCurrentSettingMenu(index);
        this.mSettingAdapter = new SettingMenuAdapter(this.mGet.getApplicationContext(), this.mGet.getCurrentSettingMenu(), 1);
        if (key != null && key.equals(Setting.KEY_HELP_GUIDE)) {
            this.mSettingAdapter.setShowSelectedChild(false);
        }
        if (this.mQuickFunctionSettingView == null) {
            CamLog.d(FaceDetector.TAG, "inflate Setting Layout...!!");
            this.mQuickFunctionSettingView = this.mGet.inflateView(R.layout.quick_function_setting);
            ((ViewGroup) this.mGet.findViewById(R.id.init)).addView(this.mQuickFunctionSettingView);
        }
        this.mQuickFunctionSettingView.setVisibility(0);
        this.mQuickFunctionSettingListView = (ListView) this.mGet.findViewById(R.id.quick_function_settingview);
        this.mQuickFunctionSettingListView.setFocusable(false);
        this.mQuickFunctionSettingListView.setAdapter(this.mSettingAdapter);
        this.mQuickFunctionSettingListView.setSelected(false);
        this.mQuickFunctionSettingListView.setOnItemClickListener(this.mSettingListViewItemClickListener);
        this.mQuickFunctionSettingListView.setOnTouchListener(this.mSettingListViewItemTouchListener);
        this.mQuickFunctionSettingListView.setSelectionFromTop(this.mGet.getSelectedChildIndex(), 0);
        this.mGet.setSubMenuMode(21);
        rotateSettingList(this.mDegree);
        qflSettingAnimation(this.mQuickFunctionSettingListView, true);
        CamLog.d(FaceDetector.TAG, "displaySettingView-end");
    }

    public boolean isQuickFunctionSettingViewRemoving() {
        return this.mQuickFunctionSettingViewRemoving;
    }

    public void removeSettingView() {
        CamLog.d(FaceDetector.TAG, "settingView : removeSettingView");
        if (this.mGet.findViewById(R.id.quick_function_setting_rotatelayout) == null) {
            CamLog.d(FaceDetector.TAG, "settingView : removeSettingView - return");
        } else if (this.mQuickFunctionSettingView == null || this.mQuickFunctionSettingView.getVisibility() == 4) {
            CamLog.d(FaceDetector.TAG, "settingView : removeSettingView -return, mSettingView is null");
        } else {
            this.mQuickFunctionSettingViewRemoving = true;
            this.mGet.doCommandUi(Command.ROTATE);
            this.mQuickFunctionSettingView.setVisibility(4);
            this.mQuickFunctionSettingListView.setAdapter(null);
            this.mQuickFunctionSettingListView.setOnItemClickListener(null);
            this.mQuickFunctionSettingListView.removeAllViewsInLayout();
            if (this.mQuickFunctionSettingListView.getBackground() != null) {
                this.mQuickFunctionSettingListView.getBackground().setCallback(null);
                this.mQuickFunctionSettingListView.setBackground(null);
            }
            this.mQuickFunctionSettingListView = null;
            this.mSettingAdapter.close();
            this.mSettingAdapter = null;
            ((ViewGroup) this.mGet.findViewById(R.id.init)).removeView(this.mQuickFunctionSettingView);
            this.mQuickFunctionSettingView = null;
            this.mDegree = -1;
            System.gc();
            if (this.mGet.getSubMenuMode() != 18) {
                this.mGet.setSubMenuMode(0);
            }
            this.mGet.quickFunctionAllMenuSelected(false);
            this.mQuickFunctionSettingViewRemoving = false;
        }
    }

    public void startRotation(int degree) {
        CamLog.d(FaceDetector.TAG, "startRotation mDegree = " + this.mDegree + ", degree = " + degree);
        if (checkMediator() && !isNullSettingView() && this.mDegree != degree) {
            this.mDegree = degree;
            rotateSettingList(degree);
        }
    }

    public void rotateSettingList(int degree) {
        if (this.mGet.findViewById(R.id.quick_function_setting_rotatelayout) != null) {
            RotateLayout cl = (RotateLayout) this.mGet.findViewById(R.id.quick_function_setting_rotatelayout);
            cl.setAngle(degree);
            MarginLayoutParams params = (MarginLayoutParams) cl.getLayoutParams();
            int leftMargin = 0;
            int topMargin = 0;
            if (!this.mGet.isConfigureLandscape()) {
                degree = (degree + 90) % CameraConstants.DEGREE_360;
            }
            switch (degree) {
                case LGKeyRec.EVENT_INVALID /*0*/:
                    leftMargin = this.QFL_SETTING_MARGIN_LEFT;
                    topMargin = this.QFL_SETTING_MARGIN_TOP;
                    break;
                case MediaProviderUtils.ROTATION_90 /*90*/:
                    leftMargin = this.QFL_SETTING_MARGIN_LEFT;
                    topMargin = (this.LCD_HEIGHT - this.QFL_SETTING_WIDTH) / 2;
                    break;
                case MediaProviderUtils.ROTATION_180 /*180*/:
                    leftMargin = this.QFL_SETTING_MARGIN_LEFT;
                    topMargin = this.QFL_SETTING_MARGIN_TOP;
                    break;
                case Tag.IMAGE_DESCRIPTION /*270*/:
                    leftMargin = this.QFL_SETTING_MARGIN_LEFT;
                    topMargin = (this.LCD_HEIGHT - this.QFL_SETTING_WIDTH) / 2;
                    break;
            }
            if (this.mGet.isConfigureLandscape()) {
                params.leftMargin = leftMargin;
                params.topMargin = topMargin;
            } else {
                params.topMargin = leftMargin;
                params.rightMargin = topMargin;
            }
            cl.setLayoutParams(params);
            updateSettingListHeight(this.mGet.getCurrentSettingMenuIndex(), params.leftMargin, params.topMargin);
        }
    }

    public boolean isNullSettingView() {
        return this.mQuickFunctionSettingView == null;
    }

    public boolean isVisible() {
        return isNullSettingView() ? false : this.mQuickFunctionSettingView.isShown();
    }

    private void updateSettingListHeight(int parentSettingPosition, int leftMargin, int topMargin) {
        int mNumOfChildItems = this.mGet.getSettingMenuItem(parentSettingPosition).getChildCount();
        LayoutParams layoutParams;
        if (mNumOfChildItems <= ProjectVariables.getSettingListHeight()) {
            layoutParams = new LayoutParams(this.QFL_SETTING_WIDTH, -2);
            if (Util.isEqualDegree(this.mGet.getResources(), this.mDegree, MediaProviderUtils.ROTATION_180)) {
                layoutParams.setMargins(this.QFL_SETTING_MARGIN_LEFT, this.QFL_SETTING_MARGIN_TOP, 0, 0);
            } else {
                layoutParams.setMargins(this.QFL_SETTING_MARGIN_LEFT, this.QFL_SETTING_MARGIN_TOP, 0, 0);
            }
            this.mQuickFunctionSettingListView.setLayoutParams(layoutParams);
        } else if (mNumOfChildItems <= ProjectVariables.getSettingListHeight()) {
        } else {
            if (Util.isEqualDegree(this.mGet.getResources(), this.mDegree, 90) || Util.isEqualDegree(this.mGet.getResources(), this.mDegree, Tag.IMAGE_DESCRIPTION)) {
                int layout_height = -2;
                int itemCount = (((this.LCD_WIDTH - this.PANEL_WIDTH) - this.QFL_SETTING_MARGIN_LEFT) / this.CONFIG_ITEM_HEIGHT) - 2;
                if (mNumOfChildItems > itemCount) {
                    layout_height = (((this.CONFIG_ITEM_HEIGHT + this.DIVIDER_HEIGHT) * itemCount) + this.DIVIDER_HEIGHT) + this.MARGIN_HEIGHT;
                }
                layoutParams = new LayoutParams(this.QFL_SETTING_WIDTH, layout_height);
                layoutParams.setMargins(leftMargin, topMargin, 0, 0);
                this.mQuickFunctionSettingListView.setLayoutParams(layoutParams);
                return;
            }
            layoutParams = new LayoutParams(this.QFL_SETTING_WIDTH, this.QFL_SETTING_HEIGHT + this.MARGIN_HEIGHT);
            if (Util.isEqualDegree(this.mGet.getResources(), this.mDegree, MediaProviderUtils.ROTATION_180)) {
                layoutParams.setMargins(this.QFL_SETTING_MARGIN_LEFT, this.QFL_SETTING_MARGIN_TOP, 0, 0);
            } else {
                layoutParams.setMargins(this.QFL_SETTING_MARGIN_LEFT, this.QFL_SETTING_MARGIN_TOP, 0, 0);
            }
            this.mQuickFunctionSettingListView.setLayoutParams(layoutParams);
        }
    }

    public void qflSettingAnimation(final View aniView, final boolean show) {
        CamLog.d(FaceDetector.TAG, "qflSettingAnimation-start");
        if (aniView != null) {
            try {
                Animation animation;
                aniView.clearAnimation();
                aniView.setVisibility(4);
                int showResId = this.mGet.isConfigureLandscape() ? R.anim.qfl_show_slide : R.anim.qfl_show_slide_up;
                int hideResId = this.mGet.isConfigureLandscape() ? R.anim.qfl_hide_slide : R.anim.qfl_hide_slide_up;
                Animation showAni = AnimationUtils.loadAnimation(this.mGet.getApplicationContext(), showResId);
                Animation hideAni = AnimationUtils.loadAnimation(this.mGet.getApplicationContext(), hideResId);
                if (show) {
                    animation = showAni;
                } else {
                    animation = hideAni;
                }
                animation.setAnimationListener(new AnimationListener() {
                    public void onAnimationStart(Animation animation) {
                    }

                    public void onAnimationRepeat(Animation animation) {
                    }

                    public void onAnimationEnd(Animation animation) {
                        if (aniView == null) {
                            return;
                        }
                        if (show) {
                            aniView.setVisibility(0);
                            return;
                        }
                        aniView.invalidate();
                        QuickFunctionSettingController.this.mGet.postOnUiThread(new Runnable() {
                            public void run() {
                                QuickFunctionSettingController.this.mGet.removePostRunnable(this);
                                QuickFunctionSettingController.this.removeSettingView();
                            }
                        });
                    }
                });
                aniView.startAnimation(animation);
            } catch (NullPointerException e) {
                CamLog.e(FaceDetector.TAG, "NullPointerException : ", e);
            }
        }
    }

    public void onPause() {
        removeSettingView();
        super.onPause();
    }

    public void onDestroy() {
        CamLog.d(FaceDetector.TAG, "onDestroy-start");
        if (this.mQuickFunctionSettingListView != null) {
            CamLog.d(FaceDetector.TAG, "wow not null" + this.mSettingAdapter + " " + this.mQuickFunctionSettingListView);
            this.mQuickFunctionSettingListView.setAdapter(null);
            this.mQuickFunctionSettingListView.setOnItemClickListener(null);
            this.mQuickFunctionSettingListView.removeAllViewsInLayout();
            if (this.mQuickFunctionSettingListView.getBackground() != null) {
                this.mQuickFunctionSettingListView.getBackground().setCallback(null);
                this.mQuickFunctionSettingListView.setBackground(null);
            }
            this.mQuickFunctionSettingListView = null;
        }
        super.onDestroy();
        CamLog.d(FaceDetector.TAG, "onDestroy-end");
    }
}
