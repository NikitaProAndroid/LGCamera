package com.lge.camera.controller;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.command.Command;
import com.lge.camera.components.RotateLayout;
import com.lge.camera.components.RotateView;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.properties.MultimediaProperties;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.setting.ListPreference;
import com.lge.camera.setting.Setting;
import com.lge.camera.setting.SettingMenuAdapter;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.ColorUtil;
import com.lge.camera.util.DialogCreater;
import com.lge.camera.util.Util;
import com.lge.morpho.utils.multimedia.MediaProviderUtils;
import com.lge.olaworks.define.Ola_Exif.Tag;
import com.lge.olaworks.library.FaceDetector;
import com.lge.voiceshutter.library.LGKeyRec;

public class SettingRotatableController extends SettingController {
    private static final long SETTING_ANIMATION_TIME = 300;
    protected int BG_BORDER_HEIGHT;
    protected int CHILD_BRIGHTNESS_LAND_HEIGHT;
    protected int CHILD_BRIGHTNESS_MARGIN_LEFT;
    protected int CHILD_BRIGHTNESS_PORT_HEIGHT;
    protected int CHILD_BRIGHTNESS_WIDTH;
    protected int CHILD_HEIGHT;
    protected int CHILD_MARGIN_LEFT;
    protected int CHILD_MARGIN_TOP;
    protected int CHILD_WIDTH;
    protected int CHILD_ZOOM_HEIGHT;
    protected int CHILD_ZOOM_WIDTH;
    protected int CONFIG_ITEM_HEIGHT;
    protected int DIVIDER;
    protected int LCD_HEIGHT;
    protected int LCD_WIDTH;
    protected int MARGIN_HEIGHT;
    protected int PANEL_WIDTH;
    protected int PARENT_HEIGHT;
    protected int PARENT_MARGIN_LEFT;
    protected int PARENT_MARGIN_TOP;
    protected int PARENT_WIDTH;
    protected int TITLE_HEIGHT;
    protected SettingMenuAdapter mChildAdapter;
    protected ListView mChildSettingListView;
    private OnItemClickListener mChildSettingListViewItemClickListener;
    private OnTouchListener mChildSettingListViewTouchListener;
    private int mDegree;
    private boolean mInitSettingBar;
    private boolean mIsRtoL;
    protected SettingMenuAdapter mParentAdapter;
    protected ListView mParentSettingListView;
    private OnItemClickListener mParentSettingListViewItemClickListener;
    private OnTouchListener mParentSettingListViewTouchListener;
    protected View mSettingView;
    private boolean mSettingViewRemoving;

    public SettingRotatableController(ControllerFunction function) {
        super(function);
        this.BG_BORDER_HEIGHT = ProjectVariables.getSettingMenuBoarderHeight();
        this.DIVIDER = 2;
        this.mDegree = -1;
        this.mInitSettingBar = false;
        this.mIsRtoL = false;
        this.mSettingViewRemoving = false;
        this.mParentSettingListViewTouchListener = new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent me) {
                switch (me.getAction()) {
                    case LGKeyRec.EVENT_NO_MATCH /*1*/:
                        if (!(view == null || view != SettingRotatableController.this.mParentSettingListView || SettingRotatableController.this.isInView(view, me)) || me.getPointerCount() > 1) {
                            SettingRotatableController.this.mParentAdapter.update();
                            break;
                        }
                    case LGKeyRec.EVENT_INCOMPLETE /*2*/:
                        if (SettingRotatableController.this.mParentSettingListView != null && SettingRotatableController.this.mGet.getSubMenuMode() == 16 && (SettingRotatableController.this.mParentSettingListView.getFirstVisiblePosition() > SettingRotatableController.this.mGet.getCurrentSettingMenuIndex() || SettingRotatableController.this.mParentSettingListView.getLastVisiblePosition() < SettingRotatableController.this.mGet.getCurrentSettingMenuIndex())) {
                            if (SettingRotatableController.this.mChildSettingListView != null) {
                                SettingRotatableController.this.mChildSettingListView.setVisibility(4);
                            }
                            SettingRotatableController.this.removeChildSettingView(false);
                            SettingRotatableController.this.mGet.hideChildCustomView(false);
                            break;
                        }
                }
                return false;
            }
        };
        this.mParentSettingListViewItemClickListener = new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (SettingRotatableController.this.checkMediator() && SettingRotatableController.this.mGet.getDialogID() == -1) {
                    CamLog.d(FaceDetector.TAG, "wheel itemIndex: " + position);
                    if (SettingRotatableController.this.mGet.getVideoState() != 0) {
                        CamLog.d(FaceDetector.TAG, "return because of recording staus");
                    } else if (SettingRotatableController.this.mGet.getCurrentSettingMenuIndex() == position && SettingRotatableController.this.mGet.getSubMenuMode() == 16) {
                        if (SettingRotatableController.this.mChildSettingListView != null) {
                            SettingRotatableController.this.mChildSettingListView.setVisibility(4);
                        }
                        SettingRotatableController.this.removeChildSettingView(true);
                        SettingRotatableController.this.mGet.hideChildCustomView(true);
                    } else {
                        SettingRotatableController.this.mGet.setCurrentSettingMenu(position);
                        SettingRotatableController.this.setChildViewPattern(position);
                        if (SettingRotatableController.this.mChildSettingListView != null) {
                            SettingRotatableController.this.mChildAdapter.setShowSelectedChild(true);
                            ListPreference pref = SettingRotatableController.this.mGet.findPreference(SettingRotatableController.this.mGet.getIndexMenuKey(position));
                            if (pref != null) {
                                if (Setting.KEY_HELP_GUIDE.equals(pref.getKey())) {
                                    SettingRotatableController.this.mChildAdapter.setShowSelectedChild(false);
                                    SettingRotatableController.this.mChildSettingListView.setSelected(false);
                                    SettingRotatableController.this.mGet.runOnUiThread(new Runnable() {
                                        public void run() {
                                            if (SettingRotatableController.this.checkMediator() && SettingRotatableController.this.mChildAdapter != null) {
                                                SettingRotatableController.this.mGet.removePostRunnable(this);
                                                SettingRotatableController.this.mChildAdapter.update();
                                            }
                                        }
                                    });
                                    return;
                                }
                            }
                            SettingRotatableController.this.mChildSettingListView.post(new Runnable() {
                                public void run() {
                                    if (SettingRotatableController.this.getCurrentSettingMenu() != null && SettingRotatableController.this.mChildSettingListView != null) {
                                        SettingRotatableController.this.mChildSettingListView.setSelectionFromTop(SettingRotatableController.this.mGet.getSelectedChildIndex(), 0);
                                    }
                                }
                            });
                        }
                    }
                }
            }
        };
        this.mChildSettingListViewTouchListener = new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent me) {
                switch (me.getAction()) {
                    case LGKeyRec.EVENT_NO_MATCH /*1*/:
                        if (!(view == null || view != SettingRotatableController.this.mChildSettingListView || SettingRotatableController.this.isInView(view, me)) || me.getPointerCount() > 1) {
                            SettingRotatableController.this.mChildAdapter.update();
                            break;
                        }
                }
                return false;
            }
        };
        this.mChildSettingListViewItemClickListener = new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (SettingRotatableController.this.checkMediator() && SettingRotatableController.this.mGet.getDialogID() == -1 && SettingRotatableController.this.mGet.getEnableInput()) {
                    CamLog.d(FaceDetector.TAG, "OnItemClickListener position[" + position + "]");
                    String currentMenukey = SettingRotatableController.this.mGet.getCurrentMenuKey();
                    if ((Setting.KEY_CAMERA_PICTURESIZE.equals(currentMenukey) || Setting.KEY_CAMERA_SHOT_MODE.equals(currentMenukey) || Setting.KEY_VIDEO_RECORD_MODE.equals(currentMenukey)) && position == SettingRotatableController.this.mGet.getSelectedChildIndex()) {
                        CamLog.d(FaceDetector.TAG, "Exit OnItemClickListener because press down same setting");
                        return;
                    }
                    final String selectedChildName = SettingRotatableController.this.mGet.getCurrentSettingMenu().getCurrentMenu().getChild(position).getParameterValue();
                    if (!CameraConstants.TYPE_RECORDMODE_LIVE_EFFECT.equals(selectedChildName) || SettingRotatableController.this.mGet.findPreference(Setting.KEY_VIDEO_RECORD_MODE) == null) {
                        SettingRotatableController.this.mGet.setSelectedChild(position);
                    }
                    String className = SettingRotatableController.this.mGet.getMenuCommand();
                    if (className != null && className.equals(Command.SHOW_HELP_GUIDE_POPUP)) {
                        String helpGuide = SettingRotatableController.this.mGet.getSettingParameterValue();
                        if (helpGuide != null) {
                            SettingRotatableController.this.mGet.showHelpGuidePopup("", DialogCreater.getHelpDialogId(helpGuide), true);
                            return;
                        }
                    }
                    final String command = className;
                    SettingRotatableController.this.mGet.postOnUiThread(new Runnable() {
                        public void run() {
                            SettingRotatableController.this.mGet.removePostRunnable(this);
                            if (!CameraConstants.TYPE_RECORDMODE_LIVE_EFFECT.equals(selectedChildName) || SettingRotatableController.this.mGet.findPreference(Setting.KEY_VIDEO_RECORD_MODE) == null) {
                                SettingRotatableController.this.doCommandSubMenuClicked(SettingRotatableController.this.mGet.getCurrentMenuKey(), command);
                            } else {
                                SettingRotatableController.this.mGet.doCommand(command, new String(), selectedChildName);
                            }
                        }
                    });
                    SettingRotatableController.this.mChildAdapter.update();
                }
            }
        };
    }

    public void initController() {
        this.PARENT_WIDTH = getPixelFromDimens(R.dimen.parent_settingview_width);
        this.PARENT_HEIGHT = getPixelFromDimens(R.dimen.parent_settingview_height);
        this.PARENT_MARGIN_LEFT = getPixelFromDimens(R.dimen.parent_settingview_marginLeft);
        this.PARENT_MARGIN_TOP = getPixelFromDimens(R.dimen.parent_settingview_marginTop);
        this.CHILD_WIDTH = getPixelFromDimens(R.dimen.child_settingview_width);
        this.CHILD_HEIGHT = getPixelFromDimens(R.dimen.child_settingview_height);
        this.CHILD_MARGIN_LEFT = getPixelFromDimens(R.dimen.child_settingview_marginLeft);
        this.CHILD_MARGIN_TOP = getPixelFromDimens(R.dimen.child_settingview_marginTop);
        this.TITLE_HEIGHT = getPixelFromDimens(R.dimen.parent_settingview_title_height);
        this.LCD_WIDTH = getPixelFromDimens(R.dimen.lcd_width);
        this.LCD_HEIGHT = getPixelFromDimens(R.dimen.lcd_height);
        this.PANEL_WIDTH = getPixelFromDimens(R.dimen.preview_panel_width);
        this.CONFIG_ITEM_HEIGHT = getPixelFromDimens(R.dimen.config_item_layout_height);
        this.MARGIN_HEIGHT = this.CONFIG_ITEM_HEIGHT / 2;
        this.CHILD_ZOOM_WIDTH = getPixelFromDimens(R.dimen.setting_zoom_control_width);
        this.CHILD_ZOOM_HEIGHT = getPixelFromDimens(R.dimen.setting_zoom_control_height);
        this.CHILD_BRIGHTNESS_WIDTH = getPixelFromDimens(R.dimen.setting_brightness_bar_width);
        this.CHILD_BRIGHTNESS_LAND_HEIGHT = getPixelFromDimens(R.dimen.setting_brightness_bar_landscape_height);
        this.CHILD_BRIGHTNESS_PORT_HEIGHT = getPixelFromDimens(R.dimen.setting_brightness_bar_portrait_height);
        this.CHILD_BRIGHTNESS_MARGIN_LEFT = getPixelFromDimens(R.dimen.setting_brightness_bar_marginLeft);
        this.mDegree = this.mGet.getOrientationDegree();
        super.initController();
    }

    public void displaySettingView() {
        CamLog.d(FaceDetector.TAG, "settingView : displaySettingView-start");
        if (this.mSettingViewRemoving) {
            this.mGet.removeScheduledCommand(Command.DISPLAY_SETTING_MENU);
            this.mGet.removeScheduledCommand(Command.SHOW_SETTING_MENU);
            this.mGet.doCommandDelayed(Command.SHOW_SETTING_MENU, SETTING_ANIMATION_TIME);
            CamLog.d(FaceDetector.TAG, "settingView : displaySettingView - return, send command");
            return;
        }
        this.mDegree = this.mGet.getOrientationDegree();
        this.mGet.hideChildCustomView(false);
        this.mGet.setSubMenuMode(5);
        removeSettingItem();
        this.mChildAdapter = new SettingMenuAdapter(this.mGet.getApplicationContext(), getCurrentSettingMenu(), 1);
        this.mParentAdapter = new SettingMenuAdapter(this.mGet.getApplicationContext(), getCurrentSettingMenu(), 0);
        if (this.mSettingView == null) {
            CamLog.d(FaceDetector.TAG, "inflate Setting Layout...!!");
            this.mSettingView = this.mGet.inflateView(R.layout.setting_rotate);
            ((ViewGroup) this.mGet.findViewById(R.id.init)).addView(this.mSettingView);
        }
        this.mSettingView.setVisibility(0);
        if (this.mGet.getBackupCurrentMenuIndex() != -1) {
            this.mGet.setCurrentSettingMenu(this.mGet.getBackupCurrentMenuIndex());
            this.mGet.setBackupCurrentMenuIndex(-1);
        }
        for (int i = this.mGet.getCurrentSettingMenuIndex(); i < this.mGet.getSettingMenuCount(); i++) {
            if (this.mGet.getSettingMenuItem(i).enable) {
                if (ModelProperties.getCarrierCode() == 6) {
                    this.mGet.setCurrentSettingMenu(0);
                } else {
                    this.mGet.setCurrentSettingMenu(i);
                }
                this.mChildSettingListView = (ListView) this.mGet.findViewById(R.id.child_settingview);
                this.mChildSettingListView.setFocusable(false);
                this.mChildSettingListView.setAdapter(this.mChildAdapter);
                this.mChildSettingListView.setSelected(false);
                this.mChildSettingListView.setOnTouchListener(this.mChildSettingListViewTouchListener);
                this.mChildSettingListView.setOnItemClickListener(this.mChildSettingListViewItemClickListener);
                this.mChildSettingListView.setSelectionFromTop(this.mGet.getSelectedChildIndex(), 0);
                this.mChildSettingListView.setVisibility(4);
                this.mParentSettingListView = (ListView) this.mGet.findViewById(R.id.parent_settingview);
                this.mParentSettingListView.setFocusable(false);
                this.mParentSettingListView.setAdapter(this.mParentAdapter);
                this.mParentSettingListView.setSelected(false);
                this.mParentSettingListView.setOnTouchListener(this.mParentSettingListViewTouchListener);
                this.mParentSettingListView.setOnItemClickListener(this.mParentSettingListViewItemClickListener);
                this.mParentSettingListView.setSelectionFromTop(this.mGet.getSelectedChildIndex(), 0);
                ((TextView) this.mGet.findViewById(R.id.parent_settingview_title)).setTextColor(ColorUtil.getItemColor(2));
                rotateParentList(this.mDegree);
                rotateChildList(this.mDegree);
                rotateChildCustom(this.mDegree);
                showAnimation();
                CamLog.d(FaceDetector.TAG, "displaySettingView-end");
            }
        }
        this.mChildSettingListView = (ListView) this.mGet.findViewById(R.id.child_settingview);
        this.mChildSettingListView.setFocusable(false);
        this.mChildSettingListView.setAdapter(this.mChildAdapter);
        this.mChildSettingListView.setSelected(false);
        this.mChildSettingListView.setOnTouchListener(this.mChildSettingListViewTouchListener);
        this.mChildSettingListView.setOnItemClickListener(this.mChildSettingListViewItemClickListener);
        this.mChildSettingListView.setSelectionFromTop(this.mGet.getSelectedChildIndex(), 0);
        this.mChildSettingListView.setVisibility(4);
        this.mParentSettingListView = (ListView) this.mGet.findViewById(R.id.parent_settingview);
        this.mParentSettingListView.setFocusable(false);
        this.mParentSettingListView.setAdapter(this.mParentAdapter);
        this.mParentSettingListView.setSelected(false);
        this.mParentSettingListView.setOnTouchListener(this.mParentSettingListViewTouchListener);
        this.mParentSettingListView.setOnItemClickListener(this.mParentSettingListViewItemClickListener);
        this.mParentSettingListView.setSelectionFromTop(this.mGet.getSelectedChildIndex(), 0);
        ((TextView) this.mGet.findViewById(R.id.parent_settingview_title)).setTextColor(ColorUtil.getItemColor(2));
        rotateParentList(this.mDegree);
        rotateChildList(this.mDegree);
        rotateChildCustom(this.mDegree);
        showAnimation();
        CamLog.d(FaceDetector.TAG, "displaySettingView-end");
    }

    public void removeSettingView() {
        CamLog.d(FaceDetector.TAG, "settingView : removeSettingView - animation start");
        if (this.mGet.findViewById(R.id.parent_layout) == null || this.mGet.findViewById(R.id.child_layout) == null || this.mSettingViewRemoving) {
            CamLog.d(FaceDetector.TAG, "settingView : removeSettingView - return");
            return;
        }
        this.mSettingViewRemoving = true;
        RotateLayout rlP = (RotateLayout) this.mGet.findViewById(R.id.parent_layout);
        RotateLayout rlC = (RotateLayout) this.mGet.findViewById(R.id.child_layout);
        rlP.clearAnimation();
        rlC.clearAnimation();
        if (this.mGet.isPausing()) {
            removeSettingViewAll();
        } else {
            Animation anim = new AlphaAnimation(RotateView.DEFAULT_TEXT_SCALE_X, 0.0f);
            anim.setAnimationListener(new AnimationListener() {
                public void onAnimationStart(Animation animation) {
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    if (SettingRotatableController.this.checkMediator()) {
                        RotateLayout rlP = (RotateLayout) SettingRotatableController.this.mGet.findViewById(R.id.parent_layout);
                        RotateLayout rlC = (RotateLayout) SettingRotatableController.this.mGet.findViewById(R.id.child_layout);
                        if (!(rlP == null || rlC == null)) {
                            rlP.setVisibility(4);
                            rlC.setVisibility(4);
                        }
                        SettingRotatableController.this.mGet.postOnUiThread(new Runnable() {
                            public void run() {
                                if (SettingRotatableController.this.checkMediator()) {
                                    SettingRotatableController.this.mGet.removePostRunnable(this);
                                    SettingRotatableController.this.removeSettingViewAll();
                                }
                            }
                        });
                    }
                }
            });
            anim.setDuration(SETTING_ANIMATION_TIME);
            rlP.startAnimation(anim);
            rlC.startAnimation(anim);
        }
        this.mGet.hideChildCustomView(true);
    }

    public void removeSettingViewAll() {
        CamLog.d(FaceDetector.TAG, "settingView : removeSettingViewAll -start");
        if (this.mSettingView == null || this.mSettingView.getVisibility() == 4) {
            CamLog.d(FaceDetector.TAG, "settingView : removeSettingView -return, mSettingView is null");
            this.mSettingViewRemoving = false;
            return;
        }
        this.mGet.doCommandUi(Command.ROTATE);
        this.mSettingView.setVisibility(4);
        this.mParentSettingListView.setAdapter(null);
        this.mParentSettingListView.setOnTouchListener(null);
        this.mParentSettingListView.setOnItemClickListener(null);
        this.mParentSettingListView.removeAllViewsInLayout();
        if (this.mParentSettingListView.getBackground() != null) {
            this.mParentSettingListView.getBackground().setCallback(null);
            this.mParentSettingListView.setBackground(null);
        }
        this.mParentSettingListView = null;
        this.mParentAdapter.setShowChild(false);
        this.mParentAdapter.close();
        this.mParentAdapter = null;
        this.mChildSettingListView.setAdapter(null);
        this.mChildSettingListView.setOnTouchListener(null);
        this.mChildSettingListView.setOnItemClickListener(null);
        this.mChildSettingListView.removeAllViewsInLayout();
        if (this.mChildSettingListView.getBackground() != null) {
            this.mChildSettingListView.getBackground().setCallback(null);
            this.mChildSettingListView.setBackground(null);
        }
        this.mChildSettingListView = null;
        this.mChildAdapter.close();
        this.mChildAdapter = null;
        ((ViewGroup) this.mGet.findViewById(R.id.init)).removeView(this.mSettingView);
        this.mSettingView = null;
        this.mGet.hideChildCustomView(false);
        initSettingMenu();
        this.mDegree = -1;
        System.gc();
        this.mSettingViewRemoving = false;
        if (this.mGet.getSubMenuMode() != 6 && this.mGet.getSubMenuMode() != 25 && this.mGet.getSubMenuMode() != 18) {
            this.mGet.setSubMenuMode(0);
        }
    }

    public boolean isSettingViewRemoving() {
        return this.mSettingViewRemoving;
    }

    public void removeChildSettingView(boolean isShowAnim) {
        if (this.mGet.findViewById(R.id.parent_layout) != null && this.mGet.findViewById(R.id.child_layout) != null && this.mGet.getSubMenuMode() == 16) {
            RotateLayout rlC = (RotateLayout) this.mGet.findViewById(R.id.child_layout);
            Animation anim = new AlphaAnimation(RotateView.DEFAULT_TEXT_SCALE_X, 0.0f);
            anim.setAnimationListener(new AnimationListener() {
                public void onAnimationStart(Animation animation) {
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    if (SettingRotatableController.this.mChildSettingListView != null) {
                        SettingRotatableController.this.mChildSettingListView.setVisibility(8);
                    }
                }
            });
            anim.setDuration(isShowAnim ? SETTING_ANIMATION_TIME : 0);
            rlC.startAnimation(anim);
            this.mGet.setSubMenuMode(5);
            this.mParentAdapter.setShowChild(false);
            this.mParentSettingListView.setSelected(false);
            this.mGet.runOnUiThread(new Runnable() {
                public void run() {
                    if (SettingRotatableController.this.checkMediator() && SettingRotatableController.this.mParentAdapter != null) {
                        SettingRotatableController.this.mGet.removePostRunnable(this);
                        SettingRotatableController.this.mParentAdapter.update();
                    }
                }
            });
        }
    }

    private void hideChildSettingViewImmediately() {
        if (this.mChildSettingListView != null) {
            this.mChildSettingListView.setVisibility(4);
        }
    }

    public void showChildSettingView() {
        if (this.mGet.findViewById(R.id.parent_layout) != null && this.mGet.findViewById(R.id.child_layout) != null) {
            RotateLayout rlC = (RotateLayout) this.mGet.findViewById(R.id.child_layout);
            rlC.clearAnimation();
            Animation anim = new AlphaAnimation(0.0f, RotateView.DEFAULT_TEXT_SCALE_X);
            anim.setAnimationListener(new AnimationListener() {
                public void onAnimationStart(Animation animation) {
                    if (SettingRotatableController.this.mChildSettingListView != null) {
                        SettingRotatableController.this.mChildSettingListView.setVisibility(0);
                    }
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                }
            });
            anim.setDuration(SETTING_ANIMATION_TIME);
            rlC.startAnimation(anim);
            this.mGet.setSubMenuMode(16);
        }
    }

    public void startRotation(int degree) {
        CamLog.d(FaceDetector.TAG, "mDegree = " + this.mDegree + ", degree = " + degree);
        if (!checkMediator()) {
            return;
        }
        if ((this.mGet.getSubMenuMode() != 5 && this.mGet.getSubMenuMode() != 16) || isNullSettingView()) {
            return;
        }
        if (this.mDegree != degree || this.mIsRtoL || !this.mInitSettingBar) {
            this.mDegree = degree;
            hideAnimation();
            rotateParentList(degree);
            rotateChildList(degree);
            rotateChildCustom(degree);
            showAnimation();
        }
    }

    public void hideAnimation() {
        if (this.mGet.findViewById(R.id.parent_layout) != null && this.mGet.findViewById(R.id.child_layout) != null) {
            RotateLayout rlP = (RotateLayout) this.mGet.findViewById(R.id.parent_layout);
            RotateLayout rlC = (RotateLayout) this.mGet.findViewById(R.id.child_layout);
            Animation anim = new AlphaAnimation(RotateView.DEFAULT_TEXT_SCALE_X, 0.0f);
            anim.setAnimationListener(new AnimationListener() {
                public void onAnimationStart(Animation animation) {
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                }
            });
            anim.setDuration(SETTING_ANIMATION_TIME);
            rlP.startAnimation(anim);
            rlC.startAnimation(anim);
            childCustomViewAnimation(anim);
        }
    }

    public void showAnimation() {
        RotateLayout rlP = (RotateLayout) this.mGet.findViewById(R.id.parent_layout);
        RotateLayout rlC = (RotateLayout) this.mGet.findViewById(R.id.child_layout);
        rlP.clearAnimation();
        rlC.clearAnimation();
        Animation anim = new AlphaAnimation(0.0f, RotateView.DEFAULT_TEXT_SCALE_X);
        anim.setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation animation) {
                if (!(SettingRotatableController.this.getCurrentSettingMenu() == null || SettingRotatableController.this.mParentSettingListView == null)) {
                    SettingRotatableController.this.mParentSettingListView.setSelectionFromTop(SettingRotatableController.this.mGet.getCurrentSettingMenuIndex(), 0);
                    if (SettingRotatableController.this.mChildSettingListView != null) {
                        SettingRotatableController.this.mChildSettingListView.setSelectionFromTop(SettingRotatableController.this.mGet.getSelectedChildIndex(), 0);
                    }
                }
                SettingRotatableController.this.setChildMenuLocation(SettingRotatableController.this.mDegree, true);
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
            }
        });
        anim.setDuration(SETTING_ANIMATION_TIME);
        rlP.startAnimation(anim);
        if (FunctionProperties.isSupportAudiozoom() && this.mGet.getApplicationMode() == 1 && this.mGet.getCameraMode() == 0 && ((Util.isEqualDegree(this.mGet.getResources(), this.mDegree, 90) || Util.isEqualDegree(this.mGet.getResources(), this.mDegree, Tag.IMAGE_DESCRIPTION)) && Setting.KEY_CAMCORDER_AUDIOZOOM.equals(getCurrentSettingMenu().getCurrentMenu().getKey()))) {
            removeChildSettingView(false);
        } else {
            rlC.startAnimation(anim);
        }
        childCustomViewAnimation(anim);
    }

    public void childCustomViewAnimation(Animation anim) {
        if (this.mGet.findViewById(R.id.setting_zoom_rotate) != null) {
            ((RotateLayout) this.mGet.findViewById(R.id.setting_zoom_rotate)).startAnimation(anim);
        }
        if (this.mGet.findViewById(R.id.setting_brightness_rotate) != null) {
            ((RotateLayout) this.mGet.findViewById(R.id.setting_brightness_rotate)).startAnimation(anim);
        }
        if (this.mGet.findViewById(R.id.setting_beautyshot_rotate) != null) {
            ((RotateLayout) this.mGet.findViewById(R.id.setting_beautyshot_rotate)).startAnimation(anim);
        }
    }

    private void rotateChildCustom(int degree) {
        rotateSettingZoom(degree);
        rotateSettingBrightness(degree);
        rotateSettingBeautyShot(degree);
        rotateSettingCamera3dDepth(degree);
        this.mInitSettingBar = true;
    }

    public void rotateParentList(int degree) {
        if (this.mGet.findViewById(R.id.parent_layout) != null) {
            RotateLayout pl = (RotateLayout) this.mGet.findViewById(R.id.parent_layout);
            pl.rotateLayout(degree);
            MarginLayoutParams params_pl = (MarginLayoutParams) pl.getLayoutParams();
            int leftMargin = 0;
            int topMargin = 0;
            if (!this.mGet.isConfigureLandscape()) {
                degree = (degree + 90) % CameraConstants.DEGREE_360;
            }
            switch (degree) {
                case LGKeyRec.EVENT_INVALID /*0*/:
                    leftMargin = this.mIsRtoL ? this.CHILD_MARGIN_LEFT : this.PARENT_MARGIN_LEFT;
                    topMargin = this.PARENT_MARGIN_TOP;
                    break;
                case MediaProviderUtils.ROTATION_90 /*90*/:
                    leftMargin = this.PARENT_MARGIN_LEFT;
                    topMargin = this.mIsRtoL ? 0 : this.LCD_HEIGHT - this.PARENT_WIDTH;
                    break;
                case MediaProviderUtils.ROTATION_180 /*180*/:
                    leftMargin = this.PARENT_MARGIN_LEFT;
                    int parentAdapterCount = this.mParentAdapter.getCount();
                    int layout_height = ((((this.CONFIG_ITEM_HEIGHT + this.DIVIDER) * parentAdapterCount) - this.DIVIDER) + this.TITLE_HEIGHT) + this.BG_BORDER_HEIGHT;
                    if (parentAdapterCount > ProjectVariables.getSettingListHeight()) {
                        topMargin = this.PARENT_MARGIN_TOP;
                        break;
                    } else {
                        topMargin = ((this.CHILD_MARGIN_TOP + this.CHILD_HEIGHT) + this.MARGIN_HEIGHT) - layout_height;
                        break;
                    }
                case Tag.IMAGE_DESCRIPTION /*270*/:
                    leftMargin = this.PARENT_MARGIN_LEFT;
                    topMargin = this.LCD_HEIGHT - this.PARENT_WIDTH;
                    break;
            }
            if (this.mGet.isConfigureLandscape()) {
                params_pl.leftMargin = leftMargin;
                params_pl.topMargin = topMargin;
            } else {
                params_pl.rightMargin = topMargin;
                params_pl.topMargin = leftMargin;
            }
            pl.setLayoutParams(params_pl);
            updateParentSettingListHeight(params_pl.leftMargin, params_pl.topMargin);
        }
    }

    public void rotateChildList(int degree) {
        if (this.mGet.findViewById(R.id.child_layout) != null) {
            RotateLayout cl = (RotateLayout) this.mGet.findViewById(R.id.child_layout);
            cl.rotateLayout(degree);
            MarginLayoutParams params = (MarginLayoutParams) cl.getLayoutParams();
            int leftMargin = 0;
            int topMargin = 0;
            if (!this.mGet.isConfigureLandscape()) {
                degree = (degree + 90) % CameraConstants.DEGREE_360;
            }
            switch (degree) {
                case LGKeyRec.EVENT_INVALID /*0*/:
                    leftMargin = this.mIsRtoL ? this.PARENT_MARGIN_LEFT : this.CHILD_MARGIN_LEFT;
                    topMargin = this.CHILD_MARGIN_TOP;
                    break;
                case MediaProviderUtils.ROTATION_90 /*90*/:
                    leftMargin = this.CHILD_MARGIN_TOP;
                    topMargin = this.mIsRtoL ? this.LCD_HEIGHT - this.PARENT_WIDTH : 0;
                    break;
                case MediaProviderUtils.ROTATION_180 /*180*/:
                    leftMargin = this.CHILD_MARGIN_LEFT;
                    topMargin = this.CHILD_MARGIN_TOP;
                    break;
                case Tag.IMAGE_DESCRIPTION /*270*/:
                    leftMargin = this.CHILD_MARGIN_TOP;
                    topMargin = 0;
                    break;
            }
            if (this.mGet.isConfigureLandscape()) {
                params.leftMargin = leftMargin;
                params.topMargin = topMargin;
            } else {
                params.rightMargin = topMargin;
                params.topMargin = leftMargin;
            }
            cl.setLayoutParams(params);
            updateChildSettingListHeight(this.mGet.getCurrentSettingMenuIndex(), params.leftMargin, params.topMargin);
        }
    }

    public void rotateSettingZoom(int degree) {
        if (this.mGet.findViewById(R.id.setting_zoom_rotate) != null) {
            RotateLayout cl = (RotateLayout) this.mGet.findViewById(R.id.setting_zoom_rotate);
            cl.rotateLayout(degree);
            MarginLayoutParams params = (MarginLayoutParams) cl.getLayoutParams();
            int leftMargin = 0;
            int topMargin = 0;
            if (!this.mGet.isConfigureLandscape()) {
                degree = (degree + 90) % CameraConstants.DEGREE_360;
            }
            switch (degree) {
                case LGKeyRec.EVENT_INVALID /*0*/:
                    leftMargin = this.CHILD_MARGIN_LEFT + this.CHILD_BRIGHTNESS_MARGIN_LEFT;
                    topMargin = this.PARENT_MARGIN_TOP;
                    break;
                case MediaProviderUtils.ROTATION_90 /*90*/:
                    leftMargin = this.PARENT_MARGIN_LEFT;
                    topMargin = ((this.LCD_HEIGHT - this.PARENT_WIDTH) - this.CHILD_BRIGHTNESS_WIDTH) - this.CHILD_BRIGHTNESS_MARGIN_LEFT;
                    break;
                case MediaProviderUtils.ROTATION_180 /*180*/:
                    leftMargin = (this.PARENT_MARGIN_LEFT + this.PARENT_WIDTH) + this.CHILD_BRIGHTNESS_MARGIN_LEFT;
                    topMargin = (getCurrentParentSettingListHeight() + this.PARENT_MARGIN_TOP) - this.CHILD_BRIGHTNESS_LAND_HEIGHT;
                    break;
                case Tag.IMAGE_DESCRIPTION /*270*/:
                    leftMargin = this.PARENT_MARGIN_LEFT;
                    topMargin = ((this.LCD_HEIGHT - this.PARENT_WIDTH) - this.CHILD_BRIGHTNESS_WIDTH) - this.CHILD_BRIGHTNESS_MARGIN_LEFT;
                    break;
            }
            if (this.mGet.isConfigureLandscape()) {
                params.leftMargin = leftMargin;
                params.topMargin = topMargin;
            } else {
                params.rightMargin = topMargin;
                params.topMargin = leftMargin;
            }
            this.mGet.refreshZoomSettingBars();
            cl.setLayoutParams(params);
        }
    }

    public void rotateSettingBrightness(int degree) {
        if (this.mGet.findViewById(R.id.setting_brightness_rotate) != null) {
            RotateLayout cl = (RotateLayout) this.mGet.findViewById(R.id.setting_brightness_rotate);
            cl.rotateLayout(degree);
            MarginLayoutParams params = (MarginLayoutParams) cl.getLayoutParams();
            int leftMargin = 0;
            int topMargin = 0;
            if (!this.mGet.isConfigureLandscape()) {
                degree = (degree + 90) % CameraConstants.DEGREE_360;
            }
            switch (degree) {
                case LGKeyRec.EVENT_INVALID /*0*/:
                    leftMargin = this.CHILD_MARGIN_LEFT + this.CHILD_BRIGHTNESS_MARGIN_LEFT;
                    topMargin = this.PARENT_MARGIN_TOP;
                    break;
                case MediaProviderUtils.ROTATION_90 /*90*/:
                    leftMargin = this.PARENT_MARGIN_LEFT;
                    topMargin = ((this.LCD_HEIGHT - this.PARENT_WIDTH) - this.CHILD_BRIGHTNESS_WIDTH) - this.CHILD_BRIGHTNESS_MARGIN_LEFT;
                    break;
                case MediaProviderUtils.ROTATION_180 /*180*/:
                    leftMargin = (this.PARENT_MARGIN_LEFT + this.PARENT_WIDTH) + this.CHILD_BRIGHTNESS_MARGIN_LEFT;
                    topMargin = (getCurrentParentSettingListHeight() + this.PARENT_MARGIN_TOP) - this.CHILD_BRIGHTNESS_LAND_HEIGHT;
                    break;
                case Tag.IMAGE_DESCRIPTION /*270*/:
                    leftMargin = this.PARENT_MARGIN_LEFT;
                    topMargin = ((this.LCD_HEIGHT - this.PARENT_WIDTH) - this.CHILD_BRIGHTNESS_WIDTH) - this.CHILD_BRIGHTNESS_MARGIN_LEFT;
                    break;
            }
            if (this.mGet.isConfigureLandscape()) {
                params.leftMargin = leftMargin;
                params.topMargin = topMargin;
            } else {
                params.rightMargin = topMargin;
                params.topMargin = leftMargin;
            }
            this.mGet.refreshBrightnessSettingBars();
            cl.setLayoutParams(params);
        }
    }

    public void rotateSettingBeautyShot(int degree) {
        if (this.mGet.findViewById(R.id.setting_beautyshot_rotate) != null) {
            RotateLayout cl = (RotateLayout) this.mGet.findViewById(R.id.setting_beautyshot_rotate);
            cl.rotateLayout(degree);
            MarginLayoutParams params = (MarginLayoutParams) cl.getLayoutParams();
            int leftMargin = 0;
            int topMargin = 0;
            if (!this.mGet.isConfigureLandscape()) {
                degree = (degree + 90) % CameraConstants.DEGREE_360;
            }
            switch (degree) {
                case LGKeyRec.EVENT_INVALID /*0*/:
                    leftMargin = this.CHILD_MARGIN_LEFT + this.CHILD_BRIGHTNESS_MARGIN_LEFT;
                    topMargin = this.PARENT_MARGIN_TOP;
                    break;
                case MediaProviderUtils.ROTATION_90 /*90*/:
                    leftMargin = this.PARENT_MARGIN_LEFT;
                    topMargin = ((this.LCD_HEIGHT - this.PARENT_WIDTH) - this.CHILD_BRIGHTNESS_WIDTH) - this.CHILD_BRIGHTNESS_MARGIN_LEFT;
                    break;
                case MediaProviderUtils.ROTATION_180 /*180*/:
                    leftMargin = (this.PARENT_MARGIN_LEFT + this.PARENT_WIDTH) + this.CHILD_BRIGHTNESS_MARGIN_LEFT;
                    topMargin = (getCurrentParentSettingListHeight() + this.PARENT_MARGIN_TOP) - this.CHILD_BRIGHTNESS_LAND_HEIGHT;
                    break;
                case Tag.IMAGE_DESCRIPTION /*270*/:
                    leftMargin = this.PARENT_MARGIN_LEFT;
                    topMargin = ((this.LCD_HEIGHT - this.PARENT_WIDTH) - this.CHILD_BRIGHTNESS_WIDTH) - this.CHILD_BRIGHTNESS_MARGIN_LEFT;
                    break;
            }
            if (this.mGet.isConfigureLandscape()) {
                params.leftMargin = leftMargin;
                params.topMargin = topMargin;
            } else {
                params.rightMargin = topMargin;
                params.topMargin = leftMargin;
            }
            this.mGet.refreshBeautyshotSettingBars();
            cl.setLayoutParams(params);
        }
    }

    public void rotateSettingCamera3dDepth(int degree) {
        if (this.mGet.findViewById(R.id.setting_camera_3d_depth) != null) {
            RotateLayout cl = (RotateLayout) this.mGet.findViewById(R.id.setting_camera_3d_depth);
            cl.rotateLayout(degree);
            MarginLayoutParams params = (MarginLayoutParams) cl.getLayoutParams();
            int leftMargin = 0;
            int topMargin = 0;
            if (!this.mGet.isConfigureLandscape()) {
                degree = (degree + 90) % CameraConstants.DEGREE_360;
            }
            switch (degree) {
                case LGKeyRec.EVENT_INVALID /*0*/:
                    leftMargin = this.CHILD_MARGIN_LEFT + this.CHILD_BRIGHTNESS_MARGIN_LEFT;
                    topMargin = this.CHILD_MARGIN_TOP;
                    break;
                case MediaProviderUtils.ROTATION_90 /*90*/:
                    leftMargin = this.PARENT_MARGIN_LEFT + this.TITLE_HEIGHT;
                    topMargin = ((this.LCD_HEIGHT - this.PARENT_WIDTH) - this.CHILD_BRIGHTNESS_WIDTH) - this.CHILD_BRIGHTNESS_MARGIN_LEFT;
                    break;
                case MediaProviderUtils.ROTATION_180 /*180*/:
                    leftMargin = (this.PARENT_MARGIN_LEFT + this.PARENT_WIDTH) + this.CHILD_BRIGHTNESS_MARGIN_LEFT;
                    topMargin = ((getCurrentParentSettingListHeight() + this.PARENT_MARGIN_TOP) - this.CHILD_BRIGHTNESS_LAND_HEIGHT) - this.TITLE_HEIGHT;
                    break;
                case Tag.IMAGE_DESCRIPTION /*270*/:
                    leftMargin = this.PARENT_MARGIN_LEFT;
                    topMargin = ((this.LCD_HEIGHT - this.PARENT_WIDTH) - this.CHILD_BRIGHTNESS_WIDTH) - this.CHILD_BRIGHTNESS_MARGIN_LEFT;
                    break;
            }
            if (this.mGet.isConfigureLandscape()) {
                params.leftMargin = leftMargin;
                params.topMargin = topMargin;
            } else {
                params.rightMargin = topMargin;
                params.topMargin = leftMargin;
            }
            if (ModelProperties.is3dSupportedModel()) {
                this.mGet.refresh3dDepthSettingBars();
            }
            cl.setLayoutParams(params);
        }
    }

    public void setChildMenuLocation(int degree, boolean rotation) {
        if (this.mGet.findViewById(R.id.child_layout) != null && this.mParentSettingListView != null) {
            int leftMargin;
            RotateLayout cl = (RotateLayout) this.mGet.findViewById(R.id.child_layout);
            MarginLayoutParams params = (MarginLayoutParams) cl.getLayoutParams();
            int topMargin = 0;
            if (Util.isEqualDegree(this.mGet.getResources(), degree, 0)) {
                leftMargin = this.mIsRtoL ? this.PARENT_MARGIN_LEFT : this.CHILD_MARGIN_LEFT;
                topMargin = this.CHILD_MARGIN_TOP;
            } else {
                int i;
                if (Util.isEqualDegree(this.mGet.getResources(), degree, MediaProviderUtils.ROTATION_180)) {
                    leftMargin = this.PARENT_MARGIN_LEFT;
                    topMargin = this.PARENT_MARGIN_TOP;
                    int mNumOfChildItems = this.mGet.getSelectedChildCount(this.mGet.getCurrentSettingMenuIndex());
                    if (mNumOfChildItems <= ProjectVariables.getSettingListHeight()) {
                        i = this.CONFIG_ITEM_HEIGHT;
                        int i2 = this.DIVIDER;
                        int layout_height = (((r0 + r0) * mNumOfChildItems) - this.DIVIDER) + this.BG_BORDER_HEIGHT;
                        leftMargin = this.CHILD_MARGIN_LEFT;
                        topMargin = ((this.CHILD_MARGIN_TOP + this.CHILD_HEIGHT) + this.MARGIN_HEIGHT) - (this.TITLE_HEIGHT + layout_height);
                    } else if (mNumOfChildItems > ProjectVariables.getSettingListHeight()) {
                        leftMargin = this.CHILD_MARGIN_LEFT;
                        topMargin = this.CHILD_MARGIN_TOP - this.TITLE_HEIGHT;
                    }
                } else {
                    int currentSelectIndex = this.mGet.getCurrentSettingMenuIndex();
                    int firstPosId = this.mParentSettingListView.getFirstVisiblePosition();
                    int lastPosId = this.mParentSettingListView.getLastVisiblePosition();
                    int visibleCount = getVisibleMenuCount();
                    int menuCount = this.mParentAdapter.getCount();
                    if (menuCount <= visibleCount) {
                        visibleCount = menuCount;
                    }
                    if (rotation) {
                        CamLog.d(FaceDetector.TAG, "setChildMenuLocation : visibleCount = " + visibleCount + ", firstPosId = " + firstPosId + ", lastPosId = " + lastPosId);
                        CamLog.d(FaceDetector.TAG, "setChildMenuLocation : currentSelectIndex = " + currentSelectIndex + ", mParentAdapter.getCount() = " + menuCount);
                        if (currentSelectIndex <= menuCount - visibleCount) {
                            firstPosId = currentSelectIndex;
                            lastPosId = (currentSelectIndex + visibleCount) - 1;
                        } else {
                            firstPosId = menuCount - visibleCount;
                            lastPosId = menuCount - 1;
                        }
                    }
                    CamLog.d(FaceDetector.TAG, "setChildMenuLocation : firstPosId = " + firstPosId + ", lastPosId = " + lastPosId);
                    if (visibleCount < menuCount && lastPosId == menuCount - 1) {
                        if (currentSelectIndex <= menuCount - visibleCount) {
                            firstPosId = currentSelectIndex;
                        } else {
                            firstPosId = menuCount - visibleCount;
                        }
                    }
                    int parentPos = ((currentSelectIndex - firstPosId) * (this.CONFIG_ITEM_HEIGHT + this.DIVIDER)) + this.TITLE_HEIGHT;
                    if (menuCount >= 8 && lastPosId == menuCount - 1) {
                        parentPos += this.MARGIN_HEIGHT;
                    }
                    int childHeight = getMenuListHeight(this.mGet.getSelectedChildCount(currentSelectIndex));
                    if (parentPos + childHeight >= (this.LCD_WIDTH - this.PANEL_WIDTH) - this.PARENT_MARGIN_LEFT) {
                        parentPos -= (parentPos + childHeight) - ((this.LCD_WIDTH - this.PANEL_WIDTH) - this.PARENT_MARGIN_LEFT);
                        if (parentPos < 0) {
                            parentPos = 0;
                        }
                    }
                    if (Util.isEqualDegree(this.mGet.getResources(), degree, Tag.IMAGE_DESCRIPTION)) {
                        int parentHeight = this.PARENT_HEIGHT;
                        if (this.mParentAdapter.getCount() >= visibleCount) {
                            parentHeight = getMenuListHeight(visibleCount) + this.TITLE_HEIGHT;
                            if (menuCount >= 8) {
                                parentHeight += this.MARGIN_HEIGHT;
                            }
                        }
                        leftMargin = (this.PARENT_MARGIN_LEFT + parentHeight) - (parentPos + childHeight);
                        i = this.PARENT_MARGIN_LEFT;
                        if (leftMargin < r0) {
                            leftMargin = this.PARENT_MARGIN_LEFT;
                        }
                    } else {
                        leftMargin = parentPos + this.PARENT_MARGIN_LEFT;
                    }
                }
            }
            if (this.mGet.isConfigureLandscape()) {
                params.leftMargin = leftMargin;
                params.topMargin = topMargin;
            } else {
                params.rightMargin = topMargin;
                params.topMargin = leftMargin;
            }
            cl.setLayoutParams(params);
        }
    }

    public boolean isNullSettingView() {
        return this.mSettingView == null;
    }

    public boolean isVisible() {
        return isNullSettingView() ? false : this.mSettingView.isShown();
    }

    private int getCurrentParentSettingListHeight() {
        if (this.mGet != null) {
            View view = this.mGet.findViewById(R.id.parent_layout_view);
            if (view != null) {
                return view.getLayoutParams().height;
            }
        }
        return 0;
    }

    private void updateParentSettingListHeight(int leftMargin, int topMargin) {
        int parentAdapterCount = this.mParentAdapter.getCount();
        View parentLayout = this.mGet.findViewById(R.id.parent_layout_view);
        if (parentAdapterCount <= ProjectVariables.getSettingListHeight()) {
            LayoutParams parentLayoutParams = new LayoutParams(this.PARENT_WIDTH, -2);
            this.mParentSettingListView.setLayoutParams(new LayoutParams(this.PARENT_WIDTH, -2));
            if (Util.isEqualDegree(this.mGet.getResources(), this.mDegree, MediaProviderUtils.ROTATION_180)) {
                parentLayoutParams.setMargins(this.PARENT_MARGIN_LEFT, this.PARENT_MARGIN_TOP, 0, 0);
            } else {
                parentLayoutParams.setMargins(this.PARENT_MARGIN_LEFT, this.PARENT_MARGIN_TOP, 0, 0);
            }
            parentLayout.setLayoutParams(parentLayoutParams);
        } else if (Util.isEqualDegree(this.mGet.getResources(), this.mDegree, 90) || Util.isEqualDegree(this.mGet.getResources(), this.mDegree, Tag.IMAGE_DESCRIPTION)) {
            int visibleCount = getVisibleMenuCount();
            int parentLayoutHeight = -2;
            if (parentAdapterCount > visibleCount && parentAdapterCount >= 8) {
                parentLayoutHeight = (getMenuListHeight(visibleCount) + this.TITLE_HEIGHT) + this.MARGIN_HEIGHT;
            }
            this.mParentSettingListView.setLayoutParams(new LayoutParams(this.PARENT_WIDTH, parentLayoutHeight));
            LayoutParams parentLayoutParam = new LayoutParams(this.PARENT_WIDTH, parentLayoutHeight);
            parentLayoutParam.setMargins(leftMargin, topMargin, 0, 0);
            parentLayout.setLayoutParams(parentLayoutParam);
        } else {
            LayoutParams layoutParams = new LayoutParams(this.PARENT_WIDTH, this.PARENT_HEIGHT + this.MARGIN_HEIGHT);
            if (Util.isEqualDegree(this.mGet.getResources(), this.mDegree, MediaProviderUtils.ROTATION_180)) {
                layoutParams.setMargins(this.CHILD_MARGIN_LEFT, this.CHILD_MARGIN_TOP, 0, 0);
            } else {
                layoutParams.setMargins(this.PARENT_MARGIN_LEFT, this.PARENT_MARGIN_TOP, 0, 0);
            }
            parentLayout.setLayoutParams(layoutParams);
        }
    }

    private void updateChildSettingListHeight(int parentSettingPosition, int leftMargin, int topMargin) {
        int mNumOfChildItems = this.mGet.getSelectedChildCount(parentSettingPosition);
        LayoutParams layoutParams;
        if (mNumOfChildItems <= ProjectVariables.getSettingListHeight()) {
            layoutParams = new LayoutParams(this.CHILD_WIDTH, -2);
            if (Util.isEqualDegree(this.mGet.getResources(), this.mDegree, MediaProviderUtils.ROTATION_180)) {
                layoutParams.setMargins(this.CHILD_MARGIN_LEFT, this.CHILD_MARGIN_TOP, 0, 0);
            } else {
                layoutParams.setMargins(this.CHILD_MARGIN_LEFT, this.CHILD_MARGIN_TOP, 0, 0);
            }
            this.mChildSettingListView.setLayoutParams(layoutParams);
        } else if (Util.isEqualDegree(this.mGet.getResources(), this.mDegree, 90) || Util.isEqualDegree(this.mGet.getResources(), this.mDegree, Tag.IMAGE_DESCRIPTION)) {
            int childLayoutHeight = -2;
            int visibleCount = getVisibleMenuCount();
            if (mNumOfChildItems > visibleCount) {
                childLayoutHeight = getMenuListHeight(visibleCount);
            }
            layoutParams = new LayoutParams(this.CHILD_WIDTH, childLayoutHeight);
            layoutParams.setMargins(leftMargin, topMargin, 0, 0);
            this.mChildSettingListView.setLayoutParams(layoutParams);
        } else {
            layoutParams = new LayoutParams(this.CHILD_WIDTH, this.CHILD_HEIGHT + this.MARGIN_HEIGHT);
            if (Util.isEqualDegree(this.mGet.getResources(), this.mDegree, MediaProviderUtils.ROTATION_180)) {
                layoutParams.setMargins(this.PARENT_MARGIN_LEFT + this.TITLE_HEIGHT, this.PARENT_MARGIN_TOP, 0, 0);
            } else {
                layoutParams.setMargins(this.CHILD_MARGIN_LEFT, this.CHILD_MARGIN_TOP, 0, 0);
            }
            this.mChildSettingListView.setLayoutParams(layoutParams);
        }
    }

    private int getVisibleMenuCount() {
        return Math.round((((float) (((this.LCD_WIDTH - this.PANEL_WIDTH) - this.PARENT_MARGIN_LEFT) - this.TITLE_HEIGHT)) / ((float) this.CONFIG_ITEM_HEIGHT)) - CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK);
    }

    private int getMenuListHeight(int visibleCount) {
        return (((this.CONFIG_ITEM_HEIGHT + this.DIVIDER) * visibleCount) - this.DIVIDER) + this.BG_BORDER_HEIGHT;
    }

    private boolean isChildCustomView(String className) {
        if (className != null) {
            return true;
        }
        return false;
    }

    private void setChildViewPattern(int position) {
        this.mGet.hideChildCustomView(false);
        this.mGet.setCurrentSettingMenu(position);
        String className = this.mGet.getSettingMenuCommand();
        ListPreference pref;
        if (isChildCustomView(className)) {
            hideChildSettingViewImmediately();
            this.mGet.runOnUiThread(new Runnable() {
                public void run() {
                    if (SettingRotatableController.this.checkMediator() && SettingRotatableController.this.mParentAdapter != null) {
                        SettingRotatableController.this.mGet.removePostRunnable(this);
                        SettingRotatableController.this.mParentAdapter.update();
                    }
                }
            });
            pref = this.mGet.findPreference(this.mGet.getIndexMenuKey(position));
            if (pref != null && pref.getKey().equals(Setting.KEY_RESTORE)) {
                this.mParentAdapter.setShowChild(false);
                this.mGet.doCommandUi(className);
                this.mGet.setSubMenuMode(5);
                return;
            } else if (pref == null || !pref.getKey().equals(Setting.KEY_EDIT_SHORTCUT)) {
                this.mParentAdapter.setShowChild(true);
                this.mGet.doCommandUi(className);
                this.mGet.setSubMenuMode(16);
                return;
            } else {
                this.mGet.doCommandUi(className);
                return;
            }
        }
        this.mGet.runOnUiThread(new Runnable() {
            public void run() {
                if (SettingRotatableController.this.checkMediator() && SettingRotatableController.this.mParentAdapter != null) {
                    SettingRotatableController.this.mGet.removePostRunnable(this);
                    SettingRotatableController.this.mParentAdapter.update();
                }
            }
        });
        pref = this.mGet.findPreference(this.mGet.getIndexMenuKey(position));
        if (pref == null || !pref.getKey().equals(Setting.KEY_SHUTTER_SOUND)) {
            this.mChildSettingListView.setSoundEffectsEnabled(true);
        } else {
            this.mChildSettingListView.setSoundEffectsEnabled(false);
        }
        this.mParentAdapter.setShowChild(true);
        updateChildSettingListHeight(position, this.CHILD_MARGIN_LEFT, this.CHILD_MARGIN_TOP);
        setChildMenuLocation(this.mDegree, false);
        showChildSettingView();
        if (this.mChildSettingListView != null) {
            this.mChildAdapter.setShowSelectedChild(true);
            if (pref != null && pref.getKey().equals(Setting.KEY_HELP_GUIDE)) {
                this.mChildAdapter.setShowSelectedChild(false);
                this.mChildSettingListView.setSelected(false);
                this.mGet.runOnUiThread(new Runnable() {
                    public void run() {
                        if (SettingRotatableController.this.checkMediator() && SettingRotatableController.this.mChildAdapter != null) {
                            SettingRotatableController.this.mGet.removePostRunnable(this);
                            SettingRotatableController.this.mChildAdapter.update();
                        }
                    }
                });
            }
        }
    }

    private void doCommandSubMenuClicked(String key, String command) {
        CamLog.d(FaceDetector.TAG, "doCommandSubMenuClicked key = " + key);
        Bundle bundle;
        if (Setting.KEY_VOICESHUTTER.equals(key) || Setting.KEY_TIME_MACHINE.equals(key) || Setting.KEY_UPLUS_BOX.equals(key)) {
            bundle = new Bundle();
            bundle.putBoolean("subMenuClicked", true);
            this.mGet.doCommand(command, bundle);
        } else if (!key.equals(Setting.KEY_SWAP)) {
            this.mGet.doCommand(command);
        } else if (!MultimediaProperties.isLiveEffectSupported() || this.mGet.getApplicationMode() != 1 || !this.mGet.isEffectsCamcorderActive()) {
            bundle = new Bundle();
            bundle.putBoolean("showAll", false);
            this.mGet.doCommand(Command.REMOVE_SETTING_MENU, bundle);
            this.mGet.doCommandDelayed(command, 0);
        } else if (this.mGet.getCameraMode() != this.mGet.getSettingIndex(Setting.KEY_SWAP)) {
            CamLog.v(FaceDetector.TAG, "SwapCameraPrepared-start, liveeffect active");
            this.mGet.effectRecorderStopPreviewByCallFrom(DialogCreater.DIALOG_ID_HELP_PANORAMA);
            bundle = new Bundle();
            bundle.putBoolean("liveeffect_mode", true);
            this.mGet.doCommand(command, null, bundle);
        }
    }

    public void onPause() {
        CamLog.d(FaceDetector.TAG, "onPause-start");
        if (this.mSettingInit) {
            if (this.mGet.getApplicationMode() == 0) {
                this.mCameraMainSetting.saveSetting(this.mGet.getApplicationContext());
                this.mCameraFrontSetting.saveSetting(this.mGet.getApplicationContext());
            } else {
                this.mCamcorderMainSetting.saveSetting(this.mGet.getApplicationContext());
                this.mCamcorderFrontSetting.saveSetting(this.mGet.getApplicationContext());
            }
        }
        if (this.mGet.getSubMenuMode() == 5 || this.mGet.getSubMenuMode() == 16) {
            this.mGet.setSubMenuMode(0);
            this.mGet.doCommandUi(Command.REMOVE_SETTING_MENU);
        }
        super.onPause();
        CamLog.d(FaceDetector.TAG, "onPause-end");
    }

    public void onDestroy() {
        CamLog.d(FaceDetector.TAG, "onDestroy-start");
        if (this.mGet.getApplicationMode() == 0) {
            this.mCameraMainSettingMenu.close();
            this.mCameraFrontSettingMenu.close();
            this.mCameraMainSetting.close();
            this.mCameraFrontSetting.close();
        } else {
            this.mVideoMainSettingMenu.close();
            this.mVideoFrontSettingMenu.close();
            this.mCamcorderMainSetting.close();
            this.mCamcorderFrontSetting.close();
        }
        this.mSetting.close();
        if (this.mParentSettingListView != null) {
            CamLog.d(FaceDetector.TAG, "wow not null" + this.mParentAdapter + " " + this.mParentSettingListView);
            this.mParentSettingListView.setAdapter(null);
            this.mParentSettingListView.setOnItemClickListener(null);
            this.mParentSettingListView.removeAllViewsInLayout();
            if (this.mParentSettingListView.getBackground() != null) {
                this.mParentSettingListView.getBackground().setCallback(null);
                this.mParentSettingListView.setBackground(null);
            }
            this.mParentSettingListView = null;
        }
        if (this.mChildSettingListView != null) {
            CamLog.d(FaceDetector.TAG, "wow not null" + this.mChildAdapter + " " + this.mChildSettingListView);
            this.mChildSettingListView.setAdapter(null);
            this.mChildSettingListView.setOnItemClickListener(null);
            this.mChildSettingListView.removeAllViewsInLayout();
            if (this.mChildSettingListView.getBackground() != null) {
                this.mChildSettingListView.getBackground().setCallback(null);
                this.mChildSettingListView.setBackground(null);
            }
            this.mChildSettingListView = null;
        }
        super.onDestroy();
        CamLog.d(FaceDetector.TAG, "onDestroy-end");
    }
}
