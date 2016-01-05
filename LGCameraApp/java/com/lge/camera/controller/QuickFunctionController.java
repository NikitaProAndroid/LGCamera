package com.lge.camera.controller;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.command.Command;
import com.lge.camera.components.MultiDirectionSlidingDrawer;
import com.lge.camera.components.MultiDirectionSlidingDrawer.OnDrawerCloseListener;
import com.lge.camera.components.MultiDirectionSlidingDrawer.OnDrawerOpenListener;
import com.lge.camera.components.RotateImageButton;
import com.lge.camera.components.RotateLayout;
import com.lge.camera.components.RotateView;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.properties.MultimediaProperties;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.setting.ListPreference;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.ColorUtil;
import com.lge.camera.util.DialogCreater;
import com.lge.camera.util.TelephonyUtil;
import com.lge.camera.util.Util;
import com.lge.morpho.utils.multimedia.MediaProviderUtils;
import com.lge.olaworks.define.Limit;
import com.lge.olaworks.define.Ola_Exif.Tag;
import com.lge.olaworks.library.FaceDetector;
import com.lge.voiceshutter.library.LGKeyRec;
import java.util.ArrayList;
import java.util.Iterator;

public class QuickFunctionController extends Controller {
    private float alpha;
    private Object blockObject;
    private boolean isForcedMenuDisable;
    private long mCheckClickTime;
    private int mDegree;
    private int mDegreeSlidingDrawer;
    private int mMenu;
    private boolean[] mMenuEnabled;
    private ArrayList<View> mMenuHiddenTouchViewGroup;
    private ArrayList<RotateImageButton> mMenuViewGroup;
    private OnClickListener mOnMenuClickListener;
    private OnLongClickListener mOnMenuLongClickListener;
    private Animation mQFLanimation;
    private boolean mSliding;
    private int mTargetX;
    private int mTargetY;

    public QuickFunctionController(ControllerFunction function) {
        super(function);
        this.mMenuViewGroup = null;
        this.mMenuHiddenTouchViewGroup = null;
        this.mMenuEnabled = new boolean[]{true, true, true, true, true};
        this.mQFLanimation = null;
        this.alpha = RotateView.DEFAULT_TEXT_SCALE_X;
        this.mDegree = -1;
        this.mDegreeSlidingDrawer = -1;
        this.blockObject = new Object();
        this.mOnMenuClickListener = new OnClickListener() {
            public void onClick(View v) {
                if (QuickFunctionController.this.checkOnClick()) {
                    int index = QuickFunctionController.this.mMenuViewGroup.indexOf(v);
                    ListPreference pref = (ListPreference) v.getTag();
                    if (pref != null) {
                        synchronized (QuickFunctionController.this.blockObject) {
                            if (!pref.getKey().equals(Setting.KEY_SETTING)) {
                                QuickFunctionController.this.mGet.initSettingMenu();
                            }
                            if (QuickFunctionController.this.mGet.isAttachIntent()) {
                                pref.setSaveSettingEnabled(false);
                            } else {
                                pref.setSaveSettingEnabled(true);
                            }
                            QuickFunctionController.this.setMenuSelected(index, true);
                            String prefKey = pref.getKey();
                            CamLog.d(FaceDetector.TAG, " ===> pref.getKey(): " + prefKey);
                            if (checkQFLitemAndClearSubMenu(prefKey)) {
                                clearPIPRecordingUI();
                                if (Setting.KEY_VOICESHUTTER.equals(prefKey) || Setting.KEY_TIME_MACHINE.equals(prefKey) || Setting.KEY_UPLUS_BOX.equals(prefKey) || Setting.KEY_SMART_MODE.equals(prefKey)) {
                                    useToggleButton(index, pref, prefKey);
                                } else if (Setting.KEY_SWAP.equals(prefKey)) {
                                    selectSwapCamera(index, pref);
                                } else {
                                    QuickFunctionController.this.mGet.doCommand(pref.getCommand(), prefKey);
                                }
                                if (QuickFunctionController.this.mGet.getSubMenuMode() == 18) {
                                    QuickFunctionController.this.setMenuSelected(index, true);
                                }
                                return;
                            }
                        }
                    }
                }
            }

            private void selectSwapCamera(int index, ListPreference pref) {
                if (!QuickFunctionController.this.setCheckToggleTime(1)) {
                    QuickFunctionController.this.setMenuSelected(index, false);
                } else if (MultimediaProperties.isLiveEffectSupported() && QuickFunctionController.this.mGet.getApplicationMode() == 1 && QuickFunctionController.this.mGet.isEffectsCamcorderActive()) {
                    CamLog.v(FaceDetector.TAG, "SwapCameraPrepared-start, liveeffect active");
                    QuickFunctionController.this.mGet.effectRecorderStopPreviewByCallFrom(DialogCreater.DIALOG_ID_HELP_PANORAMA);
                    QuickFunctionController.this.mGet.hideSmartZoomFocusView();
                    QuickFunctionController.this.mCheckClickTime = System.currentTimeMillis();
                    QuickFunctionController.this.setQuickFunctionMenuForcedDisable(true);
                    QuickFunctionController.this.setAllMenuEnabled(false, false);
                    QuickFunctionController.this.mGet.setQuickButtonForcedDisable(true);
                    QuickFunctionController.this.mGet.setQuickButtonMenuEnable(false, false);
                } else {
                    CamLog.v(FaceDetector.TAG, "SwapCameraPrepared-start, go to swap");
                    if (QuickFunctionController.this.mGet.getSubMenuMode() == 21) {
                        QuickFunctionController.this.mGet.setSubMenuMode(0);
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("immediately", true);
                        QuickFunctionController.this.mGet.doCommand(Command.HIDE_QUICK_FUNCTION_SETTING_MENU, bundle);
                    }
                    QuickFunctionController.this.setQuickFunctionMenuForcedDisable(true);
                    QuickFunctionController.this.setAllMenuEnabled(false, false);
                    QuickFunctionController.this.mGet.setQuickButtonForcedDisable(true);
                    QuickFunctionController.this.mGet.setQuickButtonMenuEnable(false, false);
                    QuickFunctionController.this.mGet.doCommandDelayed(pref.getCommand(), 0);
                    QuickFunctionController.this.mCheckClickTime = System.currentTimeMillis();
                }
            }

            private void useToggleButton(int index, ListPreference pref, String prefKey) {
                QuickFunctionController.this.mGet.clearSubMenu();
                QuickFunctionController.this.mGet.doCommand(Command.RELEASE_TOUCH_FOCUS);
                CamLog.d(FaceDetector.TAG, "QFL checkTime = " + (System.currentTimeMillis() - QuickFunctionController.this.mCheckClickTime));
                if (QuickFunctionController.this.setCheckToggleTime(Setting.KEY_TIME_MACHINE.equals(prefKey) ? 0 : 2)) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("subMenuClicked", true);
                    String value = QuickFunctionController.this.mGet.getSettingValue(prefKey);
                    String setValue = (QuickFunctionController.this.mGet.getApplicationMode() == 1 && Setting.KEY_FLASH.equals(prefKey)) ? CameraConstants.SMART_MODE_OFF.equals(value) ? CameraConstants.FLASH_TORCH : CameraConstants.SMART_MODE_OFF : CameraConstants.SMART_MODE_ON.equals(value) ? CameraConstants.SMART_MODE_OFF : CameraConstants.SMART_MODE_ON;
                    QuickFunctionController.this.mGet.setSetting(prefKey, setValue);
                    QuickFunctionController.this.mGet.setSelectedChild(QuickFunctionController.this.mGet.getCurrentSettingMenuIndex(prefKey), pref.findIndexOfValue(setValue));
                    if (Setting.KEY_SMART_MODE.equals(prefKey)) {
                        QuickFunctionController.this.mGet.doCommandUi(pref.getEntryCommand());
                    } else {
                        QuickFunctionController.this.mGet.doCommandUi(pref.getEntryCommand(), bundle);
                    }
                    QuickFunctionController.this.mCheckClickTime = System.currentTimeMillis();
                    if (QuickFunctionController.this.mQFLanimation != null) {
                        View qfl = QuickFunctionController.this.getMenuView(index);
                        if (qfl != null) {
                            qfl.clearAnimation();
                            qfl.startAnimation(QuickFunctionController.this.mQFLanimation);
                        }
                    }
                }
                QuickFunctionController.this.setMenuSelected(index, false);
            }

            private boolean checkQFLitemAndClearSubMenu(String prefKey) {
                if (QuickFunctionController.this.isEqualPreferenceAndSubmenu(prefKey)) {
                    if (QuickFunctionController.this.mGet.getSubMenuMode() == 5 || QuickFunctionController.this.mGet.getSubMenuMode() == 16) {
                        QuickFunctionController.this.mGet.setSubMenuMode(0);
                        QuickFunctionController.this.mGet.doCommandUi(Command.REMOVE_SETTING_MENU);
                        return false;
                    } else if (QuickFunctionController.this.mGet.getSubMenuMode() == 21) {
                        QuickFunctionController.this.mGet.setSubMenuMode(0);
                        QuickFunctionController.this.mGet.doCommandUi(Command.HIDE_QUICK_FUNCTION_SETTING_MENU);
                        return false;
                    } else if (QuickFunctionController.this.mGet.getSubMenuMode() != 0) {
                        QuickFunctionController.this.mGet.clearSubMenu();
                        if (QuickFunctionController.this.mGet.getApplicationMode() != 0) {
                            return false;
                        }
                        QuickFunctionController.this.mGet.setClearFocusAnimation();
                        if (Setting.HELP_FACE_TRACKING_LED.equals(QuickFunctionController.this.mGet.getSettingValue(Setting.KEY_FOCUS)) || CameraConstants.FOCUS_SETTING_VALUE_MANUAL.equals(QuickFunctionController.this.mGet.getSettingValue(Setting.KEY_FOCUS))) {
                            return false;
                        }
                        QuickFunctionController.this.mGet.showFocus();
                        return false;
                    }
                } else if (QuickFunctionController.this.mGet.getSubMenuMode() == 5 || QuickFunctionController.this.mGet.getSubMenuMode() == 16) {
                    QuickFunctionController.this.mGet.setSubMenuMode(0);
                    bundle = new Bundle();
                    bundle.putBoolean("showAll", false);
                    QuickFunctionController.this.mGet.doCommandUi(Command.REMOVE_SETTING_MENU, bundle);
                } else if (QuickFunctionController.this.mGet.getSubMenuMode() == 21) {
                    QuickFunctionController.this.mGet.setSubMenuMode(0);
                    bundle = new Bundle();
                    bundle.putBoolean("immediately", true);
                    QuickFunctionController.this.mGet.doCommandUi(Command.HIDE_QUICK_FUNCTION_SETTING_MENU, bundle);
                } else if (QuickFunctionController.this.mGet.getSubMenuMode() == 18) {
                    QuickFunctionController.this.mGet.clearSubMenu();
                }
                return true;
            }

            private void clearPIPRecordingUI() {
                if (QuickFunctionController.this.mGet.isDualRecordingActive() || QuickFunctionController.this.mGet.isSmartZoomRecordingActive() || QuickFunctionController.this.mGet.isDualCameraActive()) {
                    QuickFunctionController.this.mGet.doCommand(Command.HIDE_PIP_SUBWINDOW_RESIZE_HANDLER);
                }
            }
        };
        this.mCheckClickTime = 0;
        this.mOnMenuLongClickListener = new OnLongClickListener() {
            public boolean onLongClick(View v) {
                if (QuickFunctionController.this.checkOnClick()) {
                    QuickFunctionController.this.mGet.initSettingMenu();
                    QuickFunctionController.this.mGet.doCommand(Command.SHOW_QUICK_FUNCTION_DRAG_DROP, Integer.valueOf(QuickFunctionController.this.findTargetView(v)));
                }
                return true;
            }
        };
        this.isForcedMenuDisable = false;
        this.mSliding = false;
    }

    public void initController() {
        initLiveEffectMenus();
    }

    private void initLiveEffectMenus() {
        try {
            if (MultimediaProperties.isLiveEffectSupported() && this.mGet.getApplicationMode() == 1) {
                this.mGet.inflateStub(R.id.stub_live_effect_sliding_drawer_menu);
                this.mGet.findViewById(R.id.live_effect_sliding_drawer_menu_view).setVisibility(8);
                MultiDirectionSlidingDrawer slidingDrawer = (MultiDirectionSlidingDrawer) this.mGet.findViewById(R.id.live_effect_sliding_drawer_menu_slide);
                slidingDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {
                    public void onDrawerOpened() {
                        if (QuickFunctionController.this.mGet.isConfigureLandscape()) {
                            ((ImageView) QuickFunctionController.this.mGet.findViewById(R.id.live_effect_sliding_drawer_menu_handle)).setImageResource(R.drawable.dual_camera_handler_open_land);
                        } else {
                            ((ImageView) QuickFunctionController.this.mGet.findViewById(R.id.live_effect_sliding_drawer_menu_handle)).setImageResource(R.drawable.dual_camera_handler_open);
                        }
                        QuickFunctionController.this.mGet.setQuickFunctionAllMenuEnabled(false, true);
                        QuickFunctionController.this.mGet.setQuickButtonForcedDisable(true);
                        QuickFunctionController.this.mGet.setQuickButtonMenuEnable(false, false);
                        QuickFunctionController.this.mGet.startLiveEffectDrawerSubMenuRotation(QuickFunctionController.this.mGet.getOrientationDegree());
                    }
                });
                slidingDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {
                    public void onDrawerClosed() {
                        if (QuickFunctionController.this.mGet.isConfigureLandscape()) {
                            ((ImageView) QuickFunctionController.this.mGet.findViewById(R.id.live_effect_sliding_drawer_menu_handle)).setImageResource(R.drawable.dual_camera_handler_close_land);
                        } else {
                            ((ImageView) QuickFunctionController.this.mGet.findViewById(R.id.live_effect_sliding_drawer_menu_handle)).setImageResource(R.drawable.dual_camera_handler_close);
                        }
                        if (QuickFunctionController.this.mGet.getVideoState() == 0) {
                            QuickFunctionController.this.mGet.quickFunctionControllerRefresh(true);
                            QuickFunctionController.this.mGet.setQuickButtonForcedDisable(false);
                            QuickFunctionController.this.mGet.setButtonRemainRefresh();
                            QuickFunctionController.this.mGet.startLiveEffectDrawerSubMenuRotation(QuickFunctionController.this.mGet.getOrientationDegree());
                        }
                    }
                });
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            this.mGet.toast((int) R.string.error_not_enough_memory);
        }
    }

    private void setMenuDisableForInit(String key) {
        if (this.mInit) {
            int menu = this.mGet.getQfIndex(key);
            if (this.mGet.isQuickFunctionList(menu)) {
                setMenuDim(menu, false);
            }
        }
    }

    public boolean[] getQFLMenuEnable() {
        return this.mMenuEnabled;
    }

    public void reset() {
        if (this.mInit) {
            this.mGet.resetQFIndex();
            initEnabled();
            initMenu();
            quickFunctionControllerRefresh(true);
        }
    }

    public void initEnabled() {
        if (this.mInit) {
            for (int i = 0; i < this.mMenuEnabled.length; i++) {
                this.mMenuEnabled[i] = true;
            }
        }
    }

    public void initMenu() {
        if (this.mInit) {
            int size = this.mMenuViewGroup.size();
            for (int i = 0; i < size; i++) {
                ((RotateImageButton) this.mMenuViewGroup.get(i)).setOnClickListener(this.mOnMenuClickListener);
                ((RotateImageButton) this.mMenuViewGroup.get(i)).setOnLongClickListener(this.mOnMenuLongClickListener);
                ((View) this.mMenuHiddenTouchViewGroup.get(i)).setOnLongClickListener(this.mOnMenuLongClickListener);
            }
            this.mGet.findViewById(R.id.quick_function_control).setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    QuickFunctionController.this.mTargetX = (int) event.getX();
                    QuickFunctionController.this.mTargetY = (int) event.getY();
                    return false;
                }
            });
        }
    }

    public void setCheckClickTime(long time) {
        this.mCheckClickTime = time;
    }

    public boolean setCheckToggleTime(int usage) {
        switch (usage) {
            case LGKeyRec.EVENT_INVALID /*0*/:
                if (this.mCheckClickTime == 0) {
                    return true;
                }
                if (System.currentTimeMillis() - this.mCheckClickTime >= 750) {
                    return true;
                }
                break;
            case LGKeyRec.EVENT_NO_MATCH /*1*/:
            case LGKeyRec.EVENT_INCOMPLETE /*2*/:
                if (this.mCheckClickTime == 0) {
                    return true;
                }
                if (System.currentTimeMillis() - this.mCheckClickTime >= 500) {
                    return true;
                }
                break;
        }
        return false;
    }

    private boolean checkOnClick() {
        if (!this.mInit || this.mGet.isPausing() || this.mGet.isEnteringViewShowing() || !this.mGet.getEnableInput()) {
            return false;
        }
        if (!this.mGet.isNullSettingView() && this.mGet.isSettingViewRemoving()) {
            CamLog.d(FaceDetector.TAG, "settingview is not null && Removing!!!  ->> block menu click");
            return false;
        } else if (this.mGet.isShutterButtonLongKey()) {
            CamLog.d(FaceDetector.TAG, "ShutterButton pressed.. ->> block menu click");
            return false;
        } else if (this.mGet.isPressedShutterButton() || this.mGet.getInCaptureProgress()) {
            return false;
        } else {
            if (this.mGet.getApplicationMode() == 1 && (this.mGet.getVideoState() == 1 || this.mGet.getVideoState() == 2 || this.mGet.getVideoState() == 3 || this.mGet.getVideoState() == 4)) {
                CamLog.d(FaceDetector.TAG, "Video state is recording.");
                return false;
            } else if (this.mGet.isRotateDialogVisible()) {
                return false;
            } else {
                return true;
            }
        }
    }

    private int findTargetView(View v) {
        if (!this.mInit) {
            return 0;
        }
        int targetIndex = 0;
        Rect rect = new Rect();
        int i = 0;
        while (i < this.mMenuViewGroup.size()) {
            View view = (View) this.mMenuViewGroup.get(i);
            if (v.getId() == R.id.quick_function_control) {
                view.getHitRect(rect);
                if (rect.contains(this.mTargetX, this.mTargetY)) {
                    targetIndex = i;
                    break;
                }
            } else if (v.getId() == ((RotateImageButton) this.mMenuViewGroup.get(i)).getId() || v.getId() == ((View) this.mMenuHiddenTouchViewGroup.get(i)).getId()) {
                targetIndex = i;
                break;
            }
            i++;
        }
        if (targetIndex == this.mMenuViewGroup.size() - 1) {
            return 0;
        }
        return targetIndex;
    }

    public String getSelectedMenuKey() {
        if (!this.mInit) {
            return "";
        }
        if (this.mMenuViewGroup != null) {
            int size = this.mMenuViewGroup.size();
            for (int i = 0; i < size; i++) {
                if (((RotateImageButton) this.mMenuViewGroup.get(i)).isSelected()) {
                    String key = ((ListPreference) ((RotateImageButton) this.mMenuViewGroup.get(i)).getTag()).getKey();
                    CamLog.d(FaceDetector.TAG, "selected menu key=" + key);
                    return key;
                }
            }
        }
        return null;
    }

    public void startSubMenuRotation(int degree) {
        if (this.mInit && this.mGet.findViewById(R.id.effect_menu_rotate_view) != null && this.mDegree != degree) {
            RotateLayout rl = (RotateLayout) this.mGet.findViewById(R.id.effect_menu_rotate_view);
            Animation anim = new AlphaAnimation(RotateView.DEFAULT_TEXT_SCALE_X, 0.0f);
            anim.setAnimationListener(new AnimationListener() {
                public void onAnimationEnd(Animation animation) {
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationStart(Animation animation) {
                }
            });
            anim.setDuration(300);
            rl.startAnimation(anim);
            this.mDegree = -1;
            HorizontalScrollView s1 = (HorizontalScrollView) this.mGet.findViewById(R.id.face_effect_menu_view);
            rl.setAngle(degree);
            MarginLayoutParams params = (MarginLayoutParams) rl.getLayoutParams();
            MarginLayoutParams scroll_face_params = (MarginLayoutParams) s1.getLayoutParams();
            int convDegree = degree;
            if (!this.mGet.isConfigureLandscape()) {
                convDegree = (degree + 90) % CameraConstants.DEGREE_360;
            }
            int leftMargin = getPixelFromDimens(R.dimen.liveeffect_layout_marginLeft);
            int topMargin = getPixelFromDimens(R.dimen.liveeffect_layout_marginTop);
            int menuWidth = getPixelFromDimens(R.dimen.liveeffect_layout_width);
            int menuHeight = getPixelFromDimens(R.dimen.liveeffect_layout_height);
            int scrollWidth = getPixelFromDimens(R.dimen.liveeffect_scrollview_width);
            int scrollWidthPort = getPixelFromDimens(R.dimen.liveeffect_scrollview_port_width);
            switch (convDegree) {
                case LGKeyRec.EVENT_INVALID /*0*/:
                case MediaProviderUtils.ROTATION_180 /*180*/:
                    if (!this.mGet.isConfigureLandscape()) {
                        params.leftMargin = (CameraConstants.LCD_SIZE_HEIGHT - menuHeight) - topMargin;
                        params.topMargin = leftMargin;
                        params.width = menuHeight;
                        params.height = menuWidth;
                        scroll_face_params.width = scrollWidth;
                        break;
                    }
                    params.leftMargin = leftMargin;
                    params.topMargin = topMargin;
                    params.width = menuWidth;
                    params.height = menuHeight;
                    scroll_face_params.width = scrollWidth;
                    break;
                case MediaProviderUtils.ROTATION_90 /*90*/:
                case Tag.IMAGE_DESCRIPTION /*270*/:
                    if (!this.mGet.isConfigureLandscape()) {
                        params.leftMargin = 0;
                        params.topMargin = leftMargin;
                        params.width = menuWidth;
                        params.height = menuHeight;
                        scroll_face_params.width = scrollWidthPort;
                        break;
                    }
                    params.leftMargin = leftMargin;
                    params.topMargin = 0;
                    params.width = menuHeight;
                    params.height = menuWidth;
                    scroll_face_params.width = scrollWidthPort;
                    break;
            }
            rl.setLayoutParams(params);
            s1.setLayoutParams(scroll_face_params);
            anim = new AlphaAnimation(0.0f, RotateView.DEFAULT_TEXT_SCALE_X);
            anim.setAnimationListener(new AnimationListener() {
                public void onAnimationEnd(Animation animation) {
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationStart(Animation animation) {
                }
            });
            anim.setDuration(300);
            rl.startAnimation(anim);
            this.mDegree = degree;
        }
    }

    public void startLiveEffectDrawerSubMenuRotation(int degree) {
        if (!this.mGet.isConfigurationChanging()) {
            if (this.mGet.findViewById(R.id.live_effect_sliding_drawer_menu_slide) != null) {
                int i;
                Animation anim;
                LinearLayout ll = (LinearLayout) this.mGet.findViewById(R.id.live_effect_sliding_drawer_menu);
                int numPIPMask = ll.getChildCount();
                ArrayList<RotateLayout> lArrayList = new ArrayList();
                for (i = 0; i < numPIPMask; i++) {
                    lArrayList.add((RotateLayout) ll.getChildAt(i));
                }
                int i2 = this.mDegreeSlidingDrawer;
                if (r0 != degree) {
                    anim = new AlphaAnimation(RotateView.DEFAULT_TEXT_SCALE_X, 0.0f);
                    anim.setAnimationListener(new AnimationListener() {
                        public void onAnimationEnd(Animation animation) {
                        }

                        public void onAnimationRepeat(Animation animation) {
                        }

                        public void onAnimationStart(Animation animation) {
                        }
                    });
                    anim.setDuration(500);
                    for (i = 0; i < numPIPMask; i++) {
                        ((RotateLayout) lArrayList.get(i)).startAnimation(anim);
                    }
                }
                for (i = 0; i < numPIPMask; i++) {
                    ((RotateLayout) lArrayList.get(i)).setAngle(degree);
                }
                FrameLayout sv_frameLayout = (FrameLayout) this.mGet.findViewById(R.id.live_effect_sliding_drawer_menu_scroll_view);
                MarginLayoutParams params = (MarginLayoutParams) sv_frameLayout.getLayoutParams();
                int leftMargin = getPixelFromDimens(R.dimen.live_effect_drawer_scrollview_marginLeft);
                int leftMargin_portrait = getPixelFromDimens(R.dimen.live_effect_drawer_scrollview_marginLeft_portrait);
                int topMargin_land = getPixelFromDimens(R.dimen.live_effect_drawer_scrollview_marginTop_land);
                int topMargin_portrait_land = getPixelFromDimens(R.dimen.live_effect_drawer_scrollview_marginTop_portrait_land);
                int bottomMarginRotate = getPixelFromDimens(R.dimen.live_effect_drawer_rotatelayout_marginBottom);
                int bottomMarginRotate_portrait = getPixelFromDimens(R.dimen.live_effect_drawer_rotatelayout_marginBottom_portrait);
                int leftMarginRotate = getPixelFromDimens(R.dimen.live_effect_drawer_rotatelayout_marginLeft);
                int leftMarginRotate_portrait = getPixelFromDimens(R.dimen.live_effect_drawer_rotatelayout_marginLeft_portrait);
                int rlWidth = getPixelFromDimens(R.dimen.live_effect_drawer_rotatelayout_width);
                int rlWidth_portrait = getPixelFromDimens(R.dimen.live_effect_drawer_rotatelayout_width_portrait);
                int rlHeight = getPixelFromDimens(R.dimen.live_effect_drawer_rotatelayout_height);
                int rlHeight_portrait = getPixelFromDimens(R.dimen.live_effect_drawer_rotatelayout_height_portrait);
                int convDegree = degree;
                if (!this.mGet.isConfigureLandscape()) {
                    convDegree = (degree + 90) % CameraConstants.DEGREE_360;
                }
                MarginLayoutParams params2;
                switch (convDegree) {
                    case LGKeyRec.EVENT_INVALID /*0*/:
                    case MediaProviderUtils.ROTATION_180 /*180*/:
                        if (!this.mGet.isConfigureLandscape()) {
                            params.leftMargin = leftMargin;
                            for (i = 0; i < numPIPMask; i++) {
                                params2 = (MarginLayoutParams) ((RotateLayout) lArrayList.get(i)).getLayoutParams();
                                params2.bottomMargin = bottomMarginRotate;
                                params2.leftMargin = leftMarginRotate;
                                params2.width = rlWidth;
                                params2.height = rlHeight;
                                ((RotateLayout) lArrayList.get(i)).setLayoutParams(params2);
                            }
                            break;
                        }
                        params.topMargin = topMargin_land;
                        for (i = 0; i < numPIPMask; i++) {
                            params2 = (MarginLayoutParams) ((RotateLayout) lArrayList.get(i)).getLayoutParams();
                            params2.rightMargin = bottomMarginRotate;
                            params2.bottomMargin = leftMarginRotate;
                            params2.height = rlWidth;
                            params2.width = rlHeight;
                            ((RotateLayout) lArrayList.get(i)).setLayoutParams(params2);
                        }
                        break;
                    case MediaProviderUtils.ROTATION_90 /*90*/:
                    case Tag.IMAGE_DESCRIPTION /*270*/:
                        if (!this.mGet.isConfigureLandscape()) {
                            params.leftMargin = leftMargin_portrait;
                            for (i = 0; i < numPIPMask; i++) {
                                params2 = (MarginLayoutParams) ((RotateLayout) lArrayList.get(i)).getLayoutParams();
                                params2.bottomMargin = bottomMarginRotate_portrait;
                                params2.leftMargin = leftMarginRotate_portrait;
                                params2.width = rlWidth_portrait;
                                params2.height = rlHeight_portrait;
                                ((RotateLayout) lArrayList.get(i)).setLayoutParams(params2);
                            }
                            break;
                        }
                        params.topMargin = topMargin_portrait_land;
                        for (i = 0; i < numPIPMask; i++) {
                            params2 = (MarginLayoutParams) ((RotateLayout) lArrayList.get(i)).getLayoutParams();
                            params2.rightMargin = bottomMarginRotate_portrait;
                            params2.bottomMargin = leftMarginRotate_portrait;
                            params2.height = rlWidth_portrait;
                            params2.width = rlHeight_portrait;
                            ((RotateLayout) lArrayList.get(i)).setLayoutParams(params2);
                        }
                        break;
                }
                if (sv_frameLayout != null) {
                    sv_frameLayout.setLayoutParams(params);
                }
                i2 = this.mDegreeSlidingDrawer;
                if (r0 != degree) {
                    anim = new AlphaAnimation(0.0f, RotateView.DEFAULT_TEXT_SCALE_X);
                    anim.setAnimationListener(new AnimationListener() {
                        public void onAnimationEnd(Animation animation) {
                        }

                        public void onAnimationRepeat(Animation animation) {
                        }

                        public void onAnimationStart(Animation animation) {
                        }
                    });
                    anim.setDuration(500);
                    for (i = 0; i < numPIPMask; i++) {
                        ((RotateLayout) lArrayList.get(i)).startAnimation(anim);
                    }
                }
                this.mDegreeSlidingDrawer = degree;
            }
        }
    }

    public void startPIPFrameSubMenuRotation(int degree) {
        if (!this.mGet.isConfigurationChanging()) {
            if (this.mGet.findViewById(R.id.pip_frame_sliding_drawer_menu_slide) != null) {
                int i;
                Animation anim;
                LinearLayout ll = (LinearLayout) this.mGet.findViewById(R.id.pip_frame_sliding_drawer_menu);
                int numPIPMask = ll.getChildCount();
                ArrayList<RotateLayout> lArrayList = new ArrayList();
                for (i = 0; i < numPIPMask; i++) {
                    lArrayList.add((RotateLayout) ll.getChildAt(i));
                }
                int i2 = this.mDegreeSlidingDrawer;
                if (r0 != degree) {
                    anim = new AlphaAnimation(RotateView.DEFAULT_TEXT_SCALE_X, 0.0f);
                    anim.setAnimationListener(new AnimationListener() {
                        public void onAnimationEnd(Animation animation) {
                        }

                        public void onAnimationRepeat(Animation animation) {
                        }

                        public void onAnimationStart(Animation animation) {
                        }
                    });
                    anim.setDuration(500);
                    for (i = 0; i < numPIPMask; i++) {
                        ((RotateLayout) lArrayList.get(i)).startAnimation(anim);
                    }
                }
                for (i = 0; i < numPIPMask; i++) {
                    ((RotateLayout) lArrayList.get(i)).setAngle(degree);
                }
                FrameLayout sv_frameLayout = (FrameLayout) this.mGet.findViewById(R.id.pip_frame_sliding_drawer_menu_scroll_view);
                MarginLayoutParams params = (MarginLayoutParams) sv_frameLayout.getLayoutParams();
                int leftMargin = getPixelFromDimens(R.dimen.pip_frame_menu_scrollview_marginLeft);
                int leftMargin_portrait = getPixelFromDimens(R.dimen.pip_frame_menu_scrollview_marginLeft_portrait);
                int topMargin_land = getPixelFromDimens(R.dimen.pip_frame_menu_scrollview_marginTop_land);
                int topMargin_portrait_land = getPixelFromDimens(R.dimen.pip_frame_menu_scrollview_marginTop_portrait_land);
                int bottomMarginRotate = getPixelFromDimens(R.dimen.pip_frame_menu_rotatelayout_marginBottom);
                int bottomMarginRotate_portrait = getPixelFromDimens(R.dimen.pip_frame_menu_rotatelayout_marginBottom_portrait);
                int leftMarginRotate = getPixelFromDimens(R.dimen.pip_frame_menu_rotatelayout_marginLeft);
                int leftMarginRotate_portrait = getPixelFromDimens(R.dimen.pip_frame_menu_rotatelayout_marginLeft_portrait);
                int rlWidth = getPixelFromDimens(R.dimen.pip_frame_menu_rotatelayout_width);
                int rlWidth_portrait = getPixelFromDimens(R.dimen.pip_frame_menu_rotatelayout_width_portrait);
                int rlHeight = getPixelFromDimens(R.dimen.pip_frame_menu_rotatelayout_height);
                int rlHeight_portrait = getPixelFromDimens(R.dimen.pip_frame_menu_rotatelayout_height_portrait);
                int convDegree = degree;
                if (!this.mGet.isConfigureLandscape()) {
                    convDegree = (degree + 90) % CameraConstants.DEGREE_360;
                }
                MarginLayoutParams params2;
                switch (convDegree) {
                    case LGKeyRec.EVENT_INVALID /*0*/:
                    case MediaProviderUtils.ROTATION_180 /*180*/:
                        if (!this.mGet.isConfigureLandscape()) {
                            params.leftMargin = leftMargin;
                            for (i = 0; i < numPIPMask; i++) {
                                params2 = (MarginLayoutParams) ((RotateLayout) lArrayList.get(i)).getLayoutParams();
                                params2.bottomMargin = bottomMarginRotate;
                                params2.leftMargin = leftMarginRotate;
                                params2.width = rlWidth;
                                params2.height = rlHeight;
                                ((RotateLayout) lArrayList.get(i)).setLayoutParams(params2);
                            }
                            break;
                        }
                        params.topMargin = topMargin_land;
                        for (i = 0; i < numPIPMask; i++) {
                            params2 = (MarginLayoutParams) ((RotateLayout) lArrayList.get(i)).getLayoutParams();
                            params2.rightMargin = bottomMarginRotate;
                            params2.bottomMargin = leftMarginRotate;
                            params2.height = rlWidth;
                            params2.width = rlHeight;
                            ((RotateLayout) lArrayList.get(i)).setLayoutParams(params2);
                        }
                        break;
                    case MediaProviderUtils.ROTATION_90 /*90*/:
                    case Tag.IMAGE_DESCRIPTION /*270*/:
                        if (!this.mGet.isConfigureLandscape()) {
                            params.leftMargin = leftMargin_portrait;
                            for (i = 0; i < numPIPMask; i++) {
                                params2 = (MarginLayoutParams) ((RotateLayout) lArrayList.get(i)).getLayoutParams();
                                params2.bottomMargin = bottomMarginRotate_portrait;
                                params2.leftMargin = leftMarginRotate_portrait;
                                params2.width = rlWidth_portrait;
                                params2.height = rlHeight_portrait;
                                ((RotateLayout) lArrayList.get(i)).setLayoutParams(params2);
                            }
                            break;
                        }
                        params.topMargin = topMargin_portrait_land;
                        for (i = 0; i < numPIPMask; i++) {
                            params2 = (MarginLayoutParams) ((RotateLayout) lArrayList.get(i)).getLayoutParams();
                            params2.rightMargin = bottomMarginRotate_portrait;
                            params2.bottomMargin = leftMarginRotate_portrait;
                            params2.height = rlWidth_portrait;
                            params2.width = rlHeight_portrait;
                            ((RotateLayout) lArrayList.get(i)).setLayoutParams(params2);
                        }
                        break;
                }
                if (sv_frameLayout != null) {
                    sv_frameLayout.setLayoutParams(params);
                }
                i2 = this.mDegreeSlidingDrawer;
                if (r0 != degree) {
                    anim = new AlphaAnimation(0.0f, RotateView.DEFAULT_TEXT_SCALE_X);
                    anim.setAnimationListener(new AnimationListener() {
                        public void onAnimationEnd(Animation animation) {
                        }

                        public void onAnimationRepeat(Animation animation) {
                        }

                        public void onAnimationStart(Animation animation) {
                        }
                    });
                    anim.setDuration(500);
                    for (i = 0; i < numPIPMask; i++) {
                        ((RotateLayout) lArrayList.get(i)).startAnimation(anim);
                    }
                }
                this.mDegreeSlidingDrawer = degree;
            }
        }
    }

    public void startAudiozoomMenuRotation(int degree) {
        if (!this.mGet.isPausing() && FunctionProperties.isSupportAudiozoom()) {
            this.mGet.setForced_audiozoom(true);
        }
    }

    public View getMenuView(int index) {
        if (this.mInit) {
            return (View) this.mMenuViewGroup.get(index);
        }
        return null;
    }

    public void setMenu(int menuIndex) {
        this.mMenu = menuIndex;
    }

    public int getMenu() {
        return this.mMenu;
    }

    public void setQuickFunctionMenuForcedDisable(boolean set) {
        this.isForcedMenuDisable = set;
    }

    public void setMenuEnabled(final int menuIndex, final boolean enabled) {
        if (this.mInit) {
            this.mMenuEnabled[menuIndex] = enabled;
            if (this.mInit && this.mMenuViewGroup != null) {
                this.mGet.runOnUiThread(new Runnable() {
                    public void run() {
                        QuickFunctionController.this.mGet.removePostRunnable(this);
                        boolean setEnable = QuickFunctionController.this.isForcedMenuDisable ? false : enabled;
                        ((RotateImageButton) QuickFunctionController.this.mMenuViewGroup.get(menuIndex)).setEnabled(setEnable);
                        QuickFunctionController.this.setMenuDim(menuIndex, setEnable);
                        if (setEnable) {
                            ((View) QuickFunctionController.this.mMenuHiddenTouchViewGroup.get(menuIndex)).setVisibility(4);
                        } else {
                            ((View) QuickFunctionController.this.mMenuHiddenTouchViewGroup.get(menuIndex)).setVisibility(0);
                        }
                        if (((ListPreference) ((RotateImageButton) QuickFunctionController.this.mMenuViewGroup.get(menuIndex)).getTag()) == null) {
                            ((RotateImageButton) QuickFunctionController.this.mMenuViewGroup.get(menuIndex)).setEnabled(false);
                        }
                    }
                });
            }
        }
    }

    public void setAllMenuEnabled(final boolean enabled, final boolean dimByEnable) {
        if (this.mInit && this.mMenuViewGroup != null) {
            this.mGet.runOnUiThread(new Runnable() {
                public void run() {
                    QuickFunctionController.this.mGet.removePostRunnable(this);
                    boolean setEnable = QuickFunctionController.this.isForcedMenuDisable ? false : enabled;
                    int size = QuickFunctionController.this.mMenuViewGroup.size();
                    for (int i = 0; i < size; i++) {
                        ((RotateImageButton) QuickFunctionController.this.mMenuViewGroup.get(i)).setEnabled(setEnable);
                        boolean menuEnabled;
                        if (QuickFunctionController.this.isForcedMenuDisable) {
                            menuEnabled = false;
                        } else {
                            menuEnabled = ((RotateImageButton) QuickFunctionController.this.mMenuViewGroup.get(i)).isEnabled();
                            if (QuickFunctionController.this.mMenuEnabled[i]) {
                                menuEnabled = true;
                            }
                        }
                        if (dimByEnable || !menuEnabled) {
                            QuickFunctionController.this.setMenuDim(i, setEnable);
                        } else {
                            QuickFunctionController.this.setMenuDim(i, true);
                        }
                        if (((ListPreference) ((RotateImageButton) QuickFunctionController.this.mMenuViewGroup.get(i)).getTag()) == null) {
                            ((RotateImageButton) QuickFunctionController.this.mMenuViewGroup.get(i)).setEnabled(false);
                        }
                    }
                }
            });
        }
    }

    public void setMenuIcon(final int menuIndex, final int iconIndex) {
        if (this.mInit) {
            this.mGet.runOnUiThread(new Runnable() {
                public void run() {
                    QuickFunctionController.this.mGet.removePostRunnable(this);
                    if (QuickFunctionController.this.mMenuViewGroup != null) {
                        ((RotateImageButton) QuickFunctionController.this.mMenuViewGroup.get(menuIndex)).setImageResource(QuickFunctionController.this.getMenuIconResource(menuIndex, iconIndex));
                    }
                }
            });
        }
    }

    public void setAllMenuIcons() {
        if (this.mInit && this.mMenuViewGroup != null) {
            this.mGet.runOnUiThread(new Runnable() {
                public void run() {
                    int i;
                    QuickFunctionController.this.mGet.removePostRunnable(this);
                    int size = QuickFunctionController.this.mMenuViewGroup.size();
                    for (i = 0; i < size; i++) {
                        QuickFunctionController.this.setMenuIconSetTag(i);
                        ((RotateImageButton) QuickFunctionController.this.mMenuViewGroup.get(i)).setImageResource(QuickFunctionController.this.getMenuIconResource(i));
                        ((RotateImageButton) QuickFunctionController.this.mMenuViewGroup.get(i)).setContentDescription(QuickFunctionController.this.getMenuIconStringResource(QuickFunctionController.this.mGet.getQFIndexListItem(i)));
                    }
                    for (i = 0; i < QuickFunctionController.this.mMenuViewGroup.size(); i++) {
                        RotateImageButton rButton = (RotateImageButton) QuickFunctionController.this.mMenuViewGroup.get(i);
                        if (rButton != null) {
                            if (rButton.getTag() == null) {
                                rButton.setVisibility(4);
                                rButton.setEnabled(false);
                            } else {
                                rButton.setVisibility(0);
                                if (((RotateImageButton) QuickFunctionController.this.mMenuViewGroup.get(i)).isEnabled()) {
                                    rButton.setEnabled(true);
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    private void setMenuIconSetTag(int menuIndex) {
        if (this.mInit) {
            ListPreference pref = null;
            try {
                String key = this.mGet.getQFIndexListItem(menuIndex);
                if (key != null) {
                    pref = this.mGet.getListPreference(this.mGet.findPreferenceIndex(key));
                }
                ((RotateImageButton) this.mMenuViewGroup.get(menuIndex)).setTag(pref);
            } catch (IndexOutOfBoundsException e) {
                CamLog.w(FaceDetector.TAG, "IndexOutOfBoundsException:", e);
                CamLog.w(FaceDetector.TAG, "menuIndex:" + menuIndex + ", keyIndex = " + 0);
            }
        }
    }

    public void setMenuDim(final int menuIndex, final boolean enable) {
        if (this.mInit && this.mMenuViewGroup != null) {
            this.mGet.runOnUiThread(new Runnable() {
                public void run() {
                    QuickFunctionController.this.mGet.removePostRunnable(this);
                    if (enable) {
                        ((RotateImageButton) QuickFunctionController.this.mMenuViewGroup.get(menuIndex)).setColorFilter(ColorUtil.getDefaultColor());
                        ((RotateImageButton) QuickFunctionController.this.mMenuViewGroup.get(menuIndex)).setAlpha(RotateView.DEFAULT_TEXT_SCALE_X);
                        return;
                    }
                    ((RotateImageButton) QuickFunctionController.this.mMenuViewGroup.get(menuIndex)).setColorFilter(ColorUtil.getDimColor());
                    ((RotateImageButton) QuickFunctionController.this.mMenuViewGroup.get(menuIndex)).setAlpha(QuickFunctionController.this.alpha);
                }
            });
        }
    }

    private int getMenuIconResource(int menuIndex, int iconIndex) {
        if (!this.mInit) {
            return 0;
        }
        try {
            String key = this.mGet.getQFIndexListItem(menuIndex);
            if (key == null) {
                return 0;
            }
            ListPreference pref = this.mGet.getListPreference(this.mGet.findPreferenceIndex(key));
            if (pref != null) {
                return pref.getMenuIconResources()[iconIndex];
            }
            return 0;
        } catch (ArrayIndexOutOfBoundsException e) {
            CamLog.w(FaceDetector.TAG, "ArrayIndexOutOfBoundsException:", e);
            return 0;
        }
    }

    public int getMenuIconResource(int menuIndex) {
        if (!this.mInit) {
            return 0;
        }
        String key = this.mGet.getQFIndexListItem(menuIndex);
        if (key == null) {
            return 0;
        }
        ListPreference pref = this.mGet.getListPreference(this.mGet.findPreferenceIndex(key));
        if (pref == null) {
            return 0;
        }
        int iconIndex = pref.findIndexOfValue(pref.getValue());
        if (iconIndex == -1) {
            return pref.getMenuIconResources()[0];
        }
        try {
            return pref.getMenuIconResources()[iconIndex];
        } catch (ArrayIndexOutOfBoundsException e) {
            CamLog.w(FaceDetector.TAG, "ArrayIndexOutOfBoundsException:", e);
            CamLog.w(FaceDetector.TAG, "pref.getValue():" + pref.getValue() + ", iconIndex = " + iconIndex);
            return 0;
        }
    }

    public void setMenuSelected(int menuIndex, boolean selected) {
        if (this.mInit && this.mMenuViewGroup != null && menuIndex < 5) {
            CamLog.d(FaceDetector.TAG, "setMenuSelected : " + selected);
            setAllMenuSelected(false, menuIndex);
            ((RotateImageButton) this.mMenuViewGroup.get(menuIndex)).setPressed(false);
            ((RotateImageButton) this.mMenuViewGroup.get(menuIndex)).setSelected(selected);
        }
    }

    public void setAllMenuSelected(boolean selected) {
        if (this.mInit && this.mMenuViewGroup != null) {
            int size = this.mMenuViewGroup.size();
            for (int i = 0; i < size; i++) {
                ((RotateImageButton) this.mMenuViewGroup.get(i)).setPressed(false);
                ((RotateImageButton) this.mMenuViewGroup.get(i)).setSelected(selected);
            }
        }
    }

    public void setAllMenuSelected(boolean selected, int exception) {
        if (this.mInit && this.mMenuViewGroup != null) {
            int size = this.mMenuViewGroup.size();
            for (int i = 0; i < size; i++) {
                if (i != exception) {
                    ((RotateImageButton) this.mMenuViewGroup.get(i)).setPressed(false);
                    ((RotateImageButton) this.mMenuViewGroup.get(i)).setSelected(selected);
                }
            }
        }
    }

    public void quickFunctionControllerRefresh(boolean show) {
        if (!this.mInit) {
            return;
        }
        if (!show || (!this.mGet.isLiveEffectDrawerOpened() && !this.mGet.isPIPFrameDrawerOpened())) {
            if ((!ModelProperties.isXGAmodel() && !ModelProperties.isUVGAmodel()) || this.mGet.getSubMenuMode() == 0) {
                if (show) {
                    show();
                } else {
                    hide();
                }
                setAllMenuIcons();
                setMmsLimit();
                for (int i = 0; i < 5; i++) {
                    setMenuEnabled(i, this.mMenuEnabled[i]);
                }
                if (this.mGet.getSubMenuMode() == 5 || this.mGet.getSubMenuMode() == 16) {
                    setAllMenuSelected(false, this.mGet.getQfIndex(Setting.KEY_SETTING));
                } else {
                    setAllMenuSelected(false);
                }
                if (this.mGet.getApplicationMode() == 0 && TelephonyUtil.phoneInCall(this.mGet.getApplicationContext())) {
                    setMenuDisableForInit(Setting.KEY_VOICESHUTTER);
                }
                if (this.mGet.getApplicationMode() == 1 && this.mGet.isAudiozoom_ExceptionCase(true)) {
                    setMenuDisableForInit(Setting.KEY_CAMCORDER_AUDIOZOOM);
                }
            }
        }
    }

    public void clearSubMenu() {
        if (this.mInit) {
            View view = this.mGet.findViewById(R.id.effect_menu_view);
            if (view != null) {
                view.setVisibility(8);
                ((RelativeLayout) this.mGet.findViewById(R.id.face_effect_menu)).removeAllViews();
                ((RelativeLayout) this.mGet.findViewById(R.id.face_effect_title)).removeAllViews();
            }
            setAllMenuSelected(false);
        }
    }

    public void setMmsLimit() {
        if (this.mInit && this.mGet.getApplicationMode() == 1) {
            if (this.mGet.isQuickFunctionList(this.mGet.getQfIndex(Setting.KEY_PREVIEW_SIZE_ON_DEVICE))) {
                setMmsLimitUiQfl(this.mGet.isMMSIntent());
            } else {
                CamLog.d(FaceDetector.TAG, String.format("preview size setting is not in QFL", new Object[0]));
            }
        }
    }

    public void show() {
        if (this.mInit && this.mGet.getSubMenuMode() == 0) {
            qflShow();
            setAllMenuSelected(false);
        }
    }

    public void hide() {
        if (!ProjectVariables.useHideQFLWhenSettingMenuDisplay() && this.mInit) {
            qflHide();
        }
    }

    public void qflShow() {
        if (!this.mInit) {
            return;
        }
        if (this.mGet.getApplicationMode() != 0 || !CameraConstants.SMART_MODE_ON.equals(this.mGet.getSettingValue(Setting.KEY_SMART_MODE))) {
            if ((!ProjectVariables.isSupportClearView() && !ProjectVariables.isSupportKDDICleanView()) || !this.mGet.isClearView()) {
                View qfl = this.mGet.findViewById(R.id.quick_function_control);
                if (qfl != null) {
                    qfl.setVisibility(0);
                    qfl.setEnabled(true);
                    Iterator i$ = this.mMenuViewGroup.iterator();
                    while (i$.hasNext()) {
                        ((RotateImageButton) i$.next()).setVisibility(0);
                    }
                }
            }
        }
    }

    public void qflHide() {
        if (this.mInit) {
            View qfl = this.mGet.findViewById(R.id.quick_function_control);
            if (qfl != null) {
                qfl.setVisibility(4);
                qfl.setEnabled(false);
                Iterator i$ = this.mMenuViewGroup.iterator();
                while (i$.hasNext()) {
                    ((RotateImageButton) i$.next()).setVisibility(4);
                }
            }
        }
    }

    public boolean isVisible() {
        if (this.mInit && this.mGet.findViewById(R.id.quick_function_control).getVisibility() == 0) {
            return true;
        }
        return false;
    }

    public void onResume() {
        if (this.mInit && this.mGet.getSubMenuMode() == 0) {
            quickFunctionControllerRefresh(true);
        }
        this.mCheckClickTime = 0;
        super.onResume();
    }

    public void onPause() {
        CamLog.d(FaceDetector.TAG, "QFL onPause");
        this.isForcedMenuDisable = false;
        if (this.mInit && checkMediator() && !this.mGet.isRotateDialogVisible()) {
            if (this.mGet.checkPreviewPanelController()) {
                if (this.mGet.getSubMenuMode() == 5 || this.mGet.getSubMenuMode() == 16) {
                    this.mGet.setSubMenuMode(0);
                    this.mGet.doCommandUi(Command.REMOVE_SETTING_MENU);
                    return;
                } else if (this.mGet.getSubMenuMode() != 0) {
                    this.mGet.clearSubMenu();
                    return;
                }
            }
            super.onPause();
        }
    }

    public void onDestroy() {
        if (this.mInit) {
            if (this.mMenuViewGroup != null) {
                this.mMenuViewGroup.clear();
                this.mMenuViewGroup = null;
            }
            if (this.mQFLanimation != null) {
                this.mQFLanimation = null;
            }
            super.onDestroy();
        }
    }

    protected boolean isEqualPreferenceAndSubmenu(String strPref) {
        if (!this.mInit) {
            return false;
        }
        if (checkPreferenceAndSubMenu(strPref, Setting.KEY_PREVIEW_SIZE_ON_DEVICE, 2) || checkPreferenceAndSubMenu(strPref, Setting.KEY_SCENE_MODE, 3) || checkPreferenceAndSubMenu(strPref, Setting.KEY_FOCUS, 4) || checkPreferenceAndSubMenu(strPref, Setting.KEY_SETTING, 5) || checkPreferenceAndSubMenu(strPref, Setting.KEY_SETTING, 16) || checkPreferenceAndSubMenu(strPref, Setting.KEY_ZOOM, 6) || checkPreferenceAndSubMenu(strPref, Setting.KEY_BRIGHTNESS, 7) || checkPreferenceAndSubMenu(strPref, Setting.KEY_FLASH, 8) || checkPreferenceAndSubMenu(strPref, Setting.KEY_FLASH, 9) || checkPreferenceAndSubMenu(strPref, Setting.KEY_VIDEO_DURATION, 10) || checkPreferenceAndSubMenu(strPref, Setting.KEY_SAVE_DIRECTION, 11) || checkPreferenceAndSubMenu(strPref, Setting.KEY_VOICESHUTTER, 19) || checkPreferenceAndSubMenu(strPref, Setting.KEY_UPLUS_BOX, 24) || checkPreferenceAndSubMenu(strPref, Setting.KEY_CAMERA_PICTURESIZE, 12) || checkPreferenceAndSubMenu(strPref, Setting.KEY_VIDEO_AUDIO_RECORDING, 13) || checkPreferenceAndSubMenu(strPref, Setting.KEY_BEAUTYSHOT, 15) || checkPreferenceAndSubMenu(strPref, Setting.KEY_LIVE_EFFECT, 18) || checkPreferenceAndSubMenu(strPref, Setting.KEY_CAMERA_SHOT_MODE, 17) || checkPreferenceAndSubMenu(strPref, Setting.KEY_CAMERA_3D_DEPTH, 23) || checkPreferenceAndSubMenu(strPref, Setting.KEY_SMART_MODE, 26) || checkPreferenceAndSubMenu(strPref, Setting.KEY_VIDEO_RECORD_MODE, 18)) {
            return true;
        }
        if (!this.mGet.isQuickFunctionSettingControllerShowing() || this.mGet.getSubMenuMode() != 21) {
            return false;
        }
        ListPreference pref = this.mGet.findPreference(this.mGet.getCurrentMenuKey());
        if (pref == null || !pref.getKey().equals(strPref)) {
            return false;
        }
        return true;
    }

    private boolean checkPreferenceAndSubMenu(String strPref, String setting, int subMenuMode) {
        if (this.mInit && strPref != null && strPref.equals(setting) && this.mGet.getSubMenuMode() == subMenuMode) {
            return true;
        }
        return false;
    }

    public void qflMenuAnimation(final boolean show, int duration, AnimationListener listener) {
        if (this.mInit) {
            final View qflView = this.mGet.findViewById(R.id.quick_function_control);
            if (qflView != null) {
                if (listener == null) {
                    listener = new AnimationListener() {
                        public void onAnimationStart(Animation animation) {
                        }

                        public void onAnimationRepeat(Animation animation) {
                        }

                        public void onAnimationEnd(Animation animation) {
                            if (show) {
                                qflView.setVisibility(0);
                            } else {
                                qflView.setVisibility(4);
                            }
                            qflView.clearAnimation();
                        }
                    };
                }
                qflView.clearAnimation();
                int direction = this.mGet.isConfigureLandscape() ? 0 : 2;
                if (show) {
                    Util.slideIn(qflView, direction, duration, listener);
                } else {
                    Util.slideOut(qflView, direction, duration, listener);
                }
            }
        }
    }

    public boolean isSliding() {
        return this.mSliding;
    }

    public void slideQFLIn(boolean useAnimation) {
        if (!this.mInit) {
            return;
        }
        if (!ProjectVariables.isSupportClearView() && !ProjectVariables.isSupportKDDICleanView()) {
            return;
        }
        if (this.mGet.getSubMenuMode() == 0 || this.mGet.getSubMenuMode() == 6) {
            setAllMenuSelected(false);
            if (useAnimation) {
                qflMenuAnimation(true, Limit.OLA_FIFO_DATA_MAX_SIZE_BYTE, new AnimationListener() {
                    public void onAnimationStart(Animation arg0) {
                        QuickFunctionController.this.mSliding = true;
                        QuickFunctionController.this.qflShow();
                    }

                    public void onAnimationRepeat(Animation arg0) {
                    }

                    public void onAnimationEnd(Animation arg0) {
                        Iterator i$ = QuickFunctionController.this.mMenuViewGroup.iterator();
                        while (i$.hasNext()) {
                            RotateImageButton menu = (RotateImageButton) i$.next();
                            menu.setClickable(true);
                            menu.setEnabled(true);
                        }
                        QuickFunctionController.this.mSliding = false;
                    }
                });
                return;
            }
            qflShow();
            Iterator i$ = this.mMenuViewGroup.iterator();
            while (i$.hasNext()) {
                RotateImageButton menu = (RotateImageButton) i$.next();
                menu.setClickable(true);
                menu.setEnabled(true);
            }
        }
    }

    public void slideQFLOut(boolean useAnimation) {
        if (!this.mInit) {
            return;
        }
        if (!ProjectVariables.isSupportClearView() && !ProjectVariables.isSupportKDDICleanView()) {
            return;
        }
        if (useAnimation) {
            qflMenuAnimation(false, Limit.OLA_FIFO_DATA_MAX_SIZE_BYTE, new AnimationListener() {
                public void onAnimationStart(Animation arg0) {
                    QuickFunctionController.this.mSliding = true;
                    Iterator i$ = QuickFunctionController.this.mMenuViewGroup.iterator();
                    while (i$.hasNext()) {
                        RotateImageButton menu = (RotateImageButton) i$.next();
                        menu.setClickable(false);
                        menu.setEnabled(false);
                    }
                }

                public void onAnimationRepeat(Animation arg0) {
                }

                public void onAnimationEnd(Animation arg0) {
                    QuickFunctionController.this.mSliding = false;
                    QuickFunctionController.this.qflHide();
                }
            });
            return;
        }
        Iterator i$ = this.mMenuViewGroup.iterator();
        while (i$.hasNext()) {
            RotateImageButton menu = (RotateImageButton) i$.next();
            menu.setClickable(false);
            menu.setEnabled(false);
        }
        qflHide();
    }

    public void setMmsLimit(boolean mmsVideo) {
    }

    public void setMmsLimitUiQfl(boolean mmsVideo) {
    }

    public void restoreLiveEffectSubMenu() {
    }

    public void setLimitationToLiveeffect(boolean beSet) {
    }

    public void setMenuEnableForDualRecording(boolean beSet) {
    }
}
