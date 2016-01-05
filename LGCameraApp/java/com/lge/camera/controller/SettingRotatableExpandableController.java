package com.lge.camera.controller;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.command.Command;
import com.lge.camera.components.InnerListView;
import com.lge.camera.components.RotateLayout;
import com.lge.camera.components.RotateView;
import com.lge.camera.components.SettingExpandParentImage;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.properties.MultimediaProperties;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.setting.ExpandAnimation;
import com.lge.camera.setting.ListPreference;
import com.lge.camera.setting.Setting;
import com.lge.camera.setting.SettingExpandChildMenuAdapter;
import com.lge.camera.setting.SettingExpandParentMenuAdapter;
import com.lge.camera.setting.SettingMenuItem;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.camera.util.DialogCreater;
import com.lge.camera.util.Util;
import com.lge.olaworks.define.Ola_Exif.Tag;
import com.lge.olaworks.library.FaceDetector;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

public class SettingRotatableExpandableController extends SettingController {
    private static final int CHILD_CLOSE_DELAY_TIME = 200;
    private static final int CHILD_OPENCLOSE_ANI_TIME = 200;
    private static final int CHILD_POSITION_NONE = -1;
    private static final int PARENT_SCROLL_TIME = 100;
    private static final double RATE_SAME = 1.0d;
    private static final int SCROLLBAR_ANIMATION_TIME = 300;
    private static final int SCROLLBAR_HIDE_TIME = 3000;
    private static final int SETTING_ANIMATION_TIME = 300;
    private static int SETTING_EXPAND_NUM_OF_CHILD_ROW = 0;
    private static final int STATE_CHILD_CLOSE = 100;
    private static final int STATE_CHILD_CLOSE_ANIMATION_START = 101;
    private static final int STATE_CHILD_DISPLAY_START = 10;
    private static final int STATE_CHILD_NONE = 0;
    private static final int STATE_CHILD_OPEN = 12;
    private static final int STATE_CHILD_OPEN_ADD = 11;
    private static final int STATE_CHILD_OPEN_ANIMATION_START = 13;
    protected int CONFIG_ITEM_HEIGHT;
    protected int DIVIDER;
    private Runnable mCheckCloseAnimation;
    private Runnable mCheckOpenAnimation;
    protected SettingExpandChildMenuAdapter mChildAdapter;
    private AnimationListener mChildCloseAnimationListener;
    protected InnerListView mChildListView;
    private AnimationListener mChildOpenAnimationListener;
    private OnItemClickListener mChildSettingListItemClickListener;
    private int mDegree;
    protected OnTouchListener mOnBackCoverTouchListener;
    private ViewGroup mOpenChildLayout;
    private Point mOpenChildPosition;
    private ArrayList<String> mOrderBackCamcorder;
    private ArrayList<String> mOrderBackCamera;
    public ArrayList<String> mOrderCurrentSetting;
    private ArrayList<String> mOrderFrontCamcorder;
    private ArrayList<String> mOrderFrontCamera;
    protected SettingExpandParentMenuAdapter mParentAdapter;
    protected ListView mParentListView;
    private OnTouchListener mParentListViewTouchListener;
    private OnScrollListener mParentScrollListener;
    protected ImageView mParentScrollbarThumb;
    private int mParentTouchActionState;
    protected View mSettingView;
    private boolean mSettingViewRemoving;
    private int mStateDisplayChild;
    private ViewGroup mTempChildLayout;
    private int mTempColumn;
    private String mTempKey;
    private boolean mTempNeedOpenChild;
    private ViewGroup mTempParentLayout;
    private int mTempRow;
    private Runnable mTouchReleaseRunnable;

    public SettingRotatableExpandableController(ControllerFunction function) {
        super(function);
        this.DIVIDER = 2;
        this.mDegree = CHILD_POSITION_NONE;
        this.mParentTouchActionState = 3;
        this.mParentListViewTouchListener = new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                SettingRotatableExpandableController.this.mParentTouchActionState = event.getAction();
                if (!(SettingRotatableExpandableController.this.mSettingView == null || SettingRotatableExpandableController.this.mParentListView == null || event.getAction() != 0)) {
                    float x = event.getX();
                    float y = event.getY();
                    View parentLastView = SettingRotatableExpandableController.this.mParentListView.getChildAt(SettingRotatableExpandableController.this.mParentListView.getLastVisiblePosition());
                    if (parentLastView != null) {
                        Rect parentTransparentRect = new Rect(parentLastView.getLeft(), parentLastView.getTop(), SettingRotatableExpandableController.this.mParentListView.getRight(), SettingRotatableExpandableController.this.mParentListView.getBottom());
                        if (parentTransparentRect != null && x >= ((float) parentLastView.getLeft()) && x <= ((float) parentTransparentRect.right) && y >= ((float) parentLastView.getBottom()) && y <= ((float) parentTransparentRect.bottom)) {
                            if (SettingRotatableExpandableController.this.checkAndCloseChildView()) {
                                return true;
                            }
                            SettingRotatableExpandableController.this.removeSettingView();
                        }
                    }
                }
                return false;
            }
        };
        this.mParentScrollListener = new OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case SettingRotatableExpandableController.STATE_CHILD_NONE /*0*/:
                        if (!(SettingRotatableExpandableController.this.mParentListView == null || SettingRotatableExpandableController.this.mParentListView.getScrollY() == 0)) {
                            SettingRotatableExpandableController.this.mParentListView.scrollTo(SettingRotatableExpandableController.this.mParentListView.getScrollX(), SettingRotatableExpandableController.STATE_CHILD_NONE);
                        }
                        SettingRotatableExpandableController.this.updateParentScrollbar();
                        SettingRotatableExpandableController.this.mGet.removeScheduledCommand(Command.HIDE_SETTING_EXPAND_PARENT_SCROLLBAR);
                        SettingRotatableExpandableController.this.mGet.doCommandDelayed(Command.HIDE_SETTING_EXPAND_PARENT_SCROLLBAR, ProjectVariables.keepDuration);
                        SettingRotatableExpandableController.this.mGet.postOnUiThread(SettingRotatableExpandableController.this.mTouchReleaseRunnable);
                    default:
                }
            }

            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (SettingRotatableExpandableController.this.mParentTouchActionState == 2) {
                    SettingRotatableExpandableController.this.updateParentScrollbar();
                }
            }
        };
        this.mTouchReleaseRunnable = new Runnable() {
            public void run() {
                SettingRotatableExpandableController.this.mGet.removePostRunnable(this);
                if (SettingRotatableExpandableController.this.mParentAdapter != null) {
                    SettingRotatableExpandableController.this.mParentAdapter.releasePressedCount();
                }
            }
        };
        this.mSettingViewRemoving = false;
        this.mTempColumn = CHILD_POSITION_NONE;
        this.mTempRow = CHILD_POSITION_NONE;
        this.mTempNeedOpenChild = false;
        this.mStateDisplayChild = STATE_CHILD_NONE;
        this.mCheckOpenAnimation = new Runnable() {
            public void run() {
                SettingRotatableExpandableController.this.onOpenChildAnimationEnd();
            }
        };
        this.mChildOpenAnimationListener = new AnimationListener() {
            public void onAnimationStart(Animation animation) {
                SettingRotatableExpandableController.this.mStateDisplayChild = SettingRotatableExpandableController.STATE_CHILD_OPEN_ANIMATION_START;
                SettingRotatableExpandableController.this.mParentListView.smoothScrollToPositionFromTop(SettingRotatableExpandableController.this.mTempRow, SettingRotatableExpandableController.STATE_CHILD_NONE, SettingRotatableExpandableController.STATE_CHILD_CLOSE);
                SettingRotatableExpandableController.this.mGet.getHandler().postDelayed(SettingRotatableExpandableController.this.mCheckOpenAnimation, 200);
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                SettingRotatableExpandableController.this.onOpenChildAnimationEnd();
            }
        };
        this.mCheckCloseAnimation = new Runnable() {
            public void run() {
                SettingRotatableExpandableController.this.onCloseAnimationEnd();
            }
        };
        this.mChildCloseAnimationListener = new AnimationListener() {
            public void onAnimationStart(Animation animation) {
                SettingRotatableExpandableController.this.mStateDisplayChild = SettingRotatableExpandableController.STATE_CHILD_CLOSE_ANIMATION_START;
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                SettingRotatableExpandableController.this.mGet.getHandler().removeCallbacks(SettingRotatableExpandableController.this.mCheckCloseAnimation);
                SettingRotatableExpandableController.this.onCloseAnimationEnd();
                SettingRotatableExpandableController.this.mStateDisplayChild = SettingRotatableExpandableController.STATE_CHILD_NONE;
            }
        };
        this.mChildSettingListItemClickListener = new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (SettingRotatableExpandableController.this.checkMediator() && SettingRotatableExpandableController.this.mGet.getDialogID() == SettingRotatableExpandableController.CHILD_POSITION_NONE && SettingRotatableExpandableController.this.mGet.getEnableInput()) {
                    CamLog.d(FaceDetector.TAG, "OnItemClickListener position[" + position + "]");
                    if ((SettingRotatableExpandableController.this.mGet.getCurrentMenuKey().equals(Setting.KEY_CAMERA_PICTURESIZE) || SettingRotatableExpandableController.this.mGet.getCurrentMenuKey().equals(Setting.KEY_CAMERA_SHOT_MODE) || SettingRotatableExpandableController.this.mGet.getCurrentMenuKey().equals(Setting.KEY_VIDEO_RECORD_MODE)) && position == SettingRotatableExpandableController.this.mGet.getSelectedChildIndex()) {
                        CamLog.d(FaceDetector.TAG, "Exit OnItemClickListener because press down same setting");
                        SettingRotatableExpandableController.this.mGet.postOnUiThread(new Runnable() {
                            public void run() {
                                SettingRotatableExpandableController.this.mGet.removePostRunnable(this);
                                SettingRotatableExpandableController.this.closeChildView(SettingRotatableExpandableController.this.mOpenChildLayout, false);
                            }
                        }, 200);
                        return;
                    }
                    final String selectedChildName = SettingRotatableExpandableController.this.mGet.getCurrentSettingMenu().getCurrentMenu().getChild(position).getParameterValue();
                    if (!CameraConstants.TYPE_RECORDMODE_LIVE_EFFECT.equals(selectedChildName) || SettingRotatableExpandableController.this.mGet.findPreference(Setting.KEY_VIDEO_RECORD_MODE) == null) {
                        SettingRotatableExpandableController.this.mGet.setSelectedChild(position);
                    }
                    String className = SettingRotatableExpandableController.this.mGet.getMenuCommand();
                    if (className != null && className.equals(Command.SHOW_HELP_GUIDE_POPUP)) {
                        String helpGuide = SettingRotatableExpandableController.this.mGet.getSettingParameterValue();
                        if (helpGuide != null) {
                            SettingRotatableExpandableController.this.mGet.showHelpGuidePopup("", DialogCreater.getHelpDialogId(helpGuide), true);
                            return;
                        }
                    }
                    final String command = className;
                    SettingRotatableExpandableController.this.mGet.postOnUiThread(new Runnable() {
                        public void run() {
                            SettingRotatableExpandableController.this.mGet.removePostRunnable(this);
                            if (CameraConstants.TYPE_RECORDMODE_LIVE_EFFECT.equals(selectedChildName) && SettingRotatableExpandableController.this.mGet.findPreference(Setting.KEY_VIDEO_RECORD_MODE) != null) {
                                SettingRotatableExpandableController.this.mGet.doCommand(command, new String(), selectedChildName);
                            } else if (Command.SET_AUDIO_ZOOM.equals(command) && CameraConstants.SMART_MODE_ON.equals(selectedChildName) && (Util.isEqualDegree(SettingRotatableExpandableController.this.mGet.getResources(), SettingRotatableExpandableController.this.mDegree, 90) || Util.isEqualDegree(SettingRotatableExpandableController.this.mGet.getResources(), SettingRotatableExpandableController.this.mDegree, Tag.IMAGE_DESCRIPTION))) {
                                SettingRotatableExpandableController.this.mGet.getToastController().hide();
                                SettingRotatableExpandableController.this.mGet.getToastController().show(String.format(SettingRotatableExpandableController.this.getString(R.string.audio_zoom_warning), new Object[SettingRotatableExpandableController.STATE_CHILD_NONE]), (long) ProjectVariables.keepDuration);
                                SettingRotatableExpandableController.this.doCommandSubMenuClicked(SettingRotatableExpandableController.this.mGet.getCurrentMenuKey(), command);
                            } else {
                                SettingRotatableExpandableController.this.doCommandSubMenuClicked(SettingRotatableExpandableController.this.mGet.getCurrentMenuKey(), command);
                            }
                        }
                    });
                    SettingRotatableExpandableController.this.mChildAdapter.update();
                    SettingRotatableExpandableController.this.mGet.postOnUiThread(new Runnable() {
                        public void run() {
                            SettingRotatableExpandableController.this.mGet.removePostRunnable(this);
                            SettingRotatableExpandableController.this.closeChildView(SettingRotatableExpandableController.this.mOpenChildLayout, false);
                        }
                    }, 200);
                }
            }
        };
        this.mOrderBackCamera = new ArrayList();
        this.mOrderFrontCamera = new ArrayList();
        this.mOrderBackCamcorder = new ArrayList();
        this.mOrderFrontCamcorder = new ArrayList();
        this.mOrderCurrentSetting = new ArrayList();
        this.mOnBackCoverTouchListener = new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                if (SettingRotatableExpandableController.this.mSettingView != null && event.getAction() == 0) {
                    float x = event.getX();
                    float y = event.getY();
                    View rdl = SettingRotatableExpandableController.this.mSettingView.findViewById(R.id.setting_expand_rotate_layout);
                    if (rdl != null && (x < ((float) rdl.getLeft()) || x > ((float) rdl.getRight()) || y < ((float) rdl.getTop()) || y > ((float) rdl.getBottom()))) {
                        if (SettingRotatableExpandableController.this.checkAndCloseChildView()) {
                            return true;
                        }
                        SettingRotatableExpandableController.this.removeSettingView();
                    }
                }
                return false;
            }
        };
    }

    public void initController() {
        this.CONFIG_ITEM_HEIGHT = getPixelFromDimens(R.dimen.expand_child_item_height);
        this.DIVIDER = getPixelFromDimens(R.dimen.expand_child_item_divider_height);
        this.mDegree = this.mGet.getOrientationDegree();
        this.mOpenChildPosition = new Point(CHILD_POSITION_NONE, CHILD_POSITION_NONE);
        initSettingOrder();
        super.initController();
    }

    public void displaySettingView() {
        CamLog.d(FaceDetector.TAG, "settingView : displaySettingView-start");
        if (this.mSettingViewRemoving) {
            this.mGet.removeScheduledCommand(Command.DISPLAY_SETTING_MENU);
            this.mGet.removeScheduledCommand(Command.SHOW_SETTING_MENU);
            this.mGet.doCommandDelayed(Command.SHOW_SETTING_MENU, 300);
            CamLog.d(FaceDetector.TAG, "settingView : displaySettingView - return, send command");
            return;
        }
        this.mGet.showBubblePopupVisibility(STATE_CHILD_NONE, CameraConstants.TOAST_LENGTH_LONG, false);
        this.mDegree = this.mGet.getOrientationDegree();
        this.mGet.setSubMenuMode(5);
        this.mChildAdapter = new SettingExpandChildMenuAdapter(this.mGet.getApplicationContext(), getCurrentSettingMenu());
        this.mParentAdapter = new SettingExpandParentMenuAdapter(this.mGet.getApplicationContext(), R.layout.setting_expand_item_view, getCurrentSettingMenu(), this.mGet);
        if (this.mSettingView == null) {
            CamLog.d(FaceDetector.TAG, "inflate Setting Layout...!!");
            this.mSettingView = this.mGet.inflateView(R.layout.setting_expand_rotate);
            ViewGroup vg = (ViewGroup) this.mGet.findViewById(R.id.init);
            if (vg != null) {
                vg.addView(this.mSettingView);
            }
        }
        setLayoutDegree(this.mSettingView, this.mDegree);
        this.mSettingView.setVisibility(STATE_CHILD_NONE);
        if (this.mGet.getBackupCurrentMenuIndex() != CHILD_POSITION_NONE) {
            this.mGet.setCurrentSettingMenu(this.mGet.getBackupCurrentMenuIndex());
            this.mGet.setBackupCurrentMenuIndex(CHILD_POSITION_NONE);
        }
        for (int i = this.mGet.getCurrentSettingMenuIndex(); i < this.mGet.getSettingMenuCount(); i++) {
            if (this.mGet.getSettingMenuItem(i).enable) {
                if (ModelProperties.getCarrierCode() == 6) {
                    this.mGet.setCurrentSettingMenu(STATE_CHILD_NONE);
                } else {
                    this.mGet.setCurrentSettingMenu(i);
                }
                makeSettingItemOrder();
                addParentMenuItem();
                this.mParentListView = (ListView) this.mGet.findViewById(R.id.setting_expand_listview);
                this.mParentListView.setFocusable(false);
                this.mParentListView.setDivider(null);
                this.mParentListView.setAdapter(this.mParentAdapter);
                this.mParentListView.setVisibility(STATE_CHILD_NONE);
                this.mParentListView.setOnScrollListener(this.mParentScrollListener);
                this.mParentListView.setSelector(R.drawable.cam_icon_empty);
                this.mParentListView.setOnTouchListener(this.mParentListViewTouchListener);
                this.mParentScrollbarThumb = (ImageView) this.mGet.findViewById(R.id.setting_expand_parent_scollbar_thumb);
                this.mSettingView.findViewById(R.id.backcover).setOnTouchListener(this.mOnBackCoverTouchListener);
                rotateExpandList(this.mDegree);
                showSettingView();
                CamLog.d(FaceDetector.TAG, "displaySettingView-end");
            }
        }
        makeSettingItemOrder();
        addParentMenuItem();
        this.mParentListView = (ListView) this.mGet.findViewById(R.id.setting_expand_listview);
        this.mParentListView.setFocusable(false);
        this.mParentListView.setDivider(null);
        this.mParentListView.setAdapter(this.mParentAdapter);
        this.mParentListView.setVisibility(STATE_CHILD_NONE);
        this.mParentListView.setOnScrollListener(this.mParentScrollListener);
        this.mParentListView.setSelector(R.drawable.cam_icon_empty);
        this.mParentListView.setOnTouchListener(this.mParentListViewTouchListener);
        this.mParentScrollbarThumb = (ImageView) this.mGet.findViewById(R.id.setting_expand_parent_scollbar_thumb);
        this.mSettingView.findViewById(R.id.backcover).setOnTouchListener(this.mOnBackCoverTouchListener);
        rotateExpandList(this.mDegree);
        showSettingView();
        CamLog.d(FaceDetector.TAG, "displaySettingView-end");
    }

    private void addParentMenuItem() {
        int numOfMenus = this.mOrderCurrentSetting.size();
        if (numOfMenus >= 7 && numOfMenus <= 9) {
            CameraConstants.SETTING_EXPAND_MAX_COLUMN = 3;
            this.mGet.findViewById(R.id.setting_expand_inner_layout).setLayoutParams(new LayoutParams(Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.expand_settingview_port_3_width), Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.expand_settingview_3_height)));
        } else if (numOfMenus < STATE_CHILD_DISPLAY_START || numOfMenus > STATE_CHILD_OPEN) {
            if (Util.isEqualDegree(this.mGet.getResources(), this.mDegree, 90) || Util.isEqualDegree(this.mGet.getResources(), this.mDegree, Tag.IMAGE_DESCRIPTION)) {
                CameraConstants.SETTING_EXPAND_MAX_COLUMN = 3;
                this.mGet.findViewById(R.id.setting_expand_inner_layout).setLayoutParams(new LayoutParams(Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.expand_settingview_port_3_width), Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.expand_settingview_port_5_height)));
            } else {
                CameraConstants.SETTING_EXPAND_MAX_COLUMN = 4;
                this.mGet.findViewById(R.id.setting_expand_inner_layout).setLayoutParams(new LayoutParams(Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.expand_settingview_land_4_width), Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.expand_settingview_land_4_height)));
            }
        } else if (Util.isEqualDegree(this.mGet.getResources(), this.mDegree, 90) || Util.isEqualDegree(this.mGet.getResources(), this.mDegree, Tag.IMAGE_DESCRIPTION)) {
            CameraConstants.SETTING_EXPAND_MAX_COLUMN = 3;
            this.mGet.findViewById(R.id.setting_expand_inner_layout).setLayoutParams(new LayoutParams(Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.expand_settingview_port_3_width), Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.expand_settingview_land_4_height)));
        } else {
            CameraConstants.SETTING_EXPAND_MAX_COLUMN = 4;
            this.mGet.findViewById(R.id.setting_expand_inner_layout).setLayoutParams(new LayoutParams(Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.expand_settingview_land_4_width), Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.expand_settingview_3_height)));
        }
        int numOfRow = numOfMenus / CameraConstants.SETTING_EXPAND_MAX_COLUMN;
        int numOfLastRowItems = numOfMenus % CameraConstants.SETTING_EXPAND_MAX_COLUMN;
        this.mParentAdapter.clear();
        for (int i = STATE_CHILD_NONE; i <= numOfRow; i++) {
            int numOfEachColumn;
            if (i == numOfRow) {
                numOfEachColumn = numOfLastRowItems;
            } else {
                numOfEachColumn = CameraConstants.SETTING_EXPAND_MAX_COLUMN;
            }
            SettingMenuItem[] arraySettingMenuItem = new SettingMenuItem[numOfEachColumn];
            for (int j = STATE_CHILD_NONE; j < numOfEachColumn; j++) {
                int index = (CameraConstants.SETTING_EXPAND_MAX_COLUMN * i) + j;
                if (index < this.mOrderCurrentSetting.size()) {
                    arraySettingMenuItem[j] = getCurrentSettingMenu().getMenu((String) this.mOrderCurrentSetting.get(index));
                }
            }
            if (numOfEachColumn != 0) {
                this.mParentAdapter.add(arraySettingMenuItem);
            }
        }
    }

    private void updateParentScrollbar() {
        if (this.mParentScrollbarThumb != null && this.mParentListView != null) {
            if (this.mOpenChildLayout != null) {
                int childHeight = this.mOpenChildLayout.getHeight();
                int parentItemHeight = getPixelFromDimens(R.dimen.expand_parent_item_height);
                double rate = ((double) this.mParentListView.getHeight()) / (((double) (this.mParentListView.getCount() * parentItemHeight)) + ((double) childHeight));
                if (rate < RATE_SAME) {
                    this.mParentScrollbarThumb.setVisibility(STATE_CHILD_NONE);
                    LayoutParams lpParentScrollbarThumb = (LayoutParams) this.mParentScrollbarThumb.getLayoutParams();
                    lpParentScrollbarThumb.width = -2;
                    lpParentScrollbarThumb.height = (int) (((double) this.mParentListView.getHeight()) * rate);
                    int topMargin = this.mParentListView.getChildAt(STATE_CHILD_NONE).getTop();
                    for (int i = STATE_CHILD_NONE; i < this.mParentListView.getFirstVisiblePosition(); i++) {
                        if (i == this.mOpenChildPosition.y) {
                            topMargin -= this.mOpenChildLayout.getHeight();
                        } else {
                            topMargin -= parentItemHeight;
                        }
                    }
                    lpParentScrollbarThumb.topMargin = -((int) (((double) topMargin) * rate));
                    this.mParentScrollbarThumb.setLayoutParams(lpParentScrollbarThumb);
                    return;
                }
                this.mParentScrollbarThumb.setVisibility(4);
                return;
            }
            this.mParentScrollbarThumb.setVisibility(4);
        }
    }

    public void hideParentScrollbar() {
        this.mTouchReleaseRunnable.run();
        if (this.mParentScrollbarThumb != null && this.mParentScrollbarThumb.getVisibility() != 4 && this.mParentTouchActionState != 2) {
            Animation anim = new AlphaAnimation(this.mParentScrollbarThumb.getAlpha(), 0.0f);
            anim.setAnimationListener(new AnimationListener() {
                public void onAnimationStart(Animation animation) {
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    if (SettingRotatableExpandableController.this.mParentScrollbarThumb != null) {
                        SettingRotatableExpandableController.this.mParentScrollbarThumb.setVisibility(4);
                    }
                }
            });
            anim.setDuration(300);
            this.mParentScrollbarThumb.startAnimation(anim);
        }
    }

    private void showSettingView() {
        CamLog.d(FaceDetector.TAG, "settingView : showSettingView - animation start");
        if (this.mSettingView == null || this.mSettingViewRemoving) {
            CamLog.d(FaceDetector.TAG, "settingView : showSettingView - return");
            return;
        }
        this.mSettingView.clearAnimation();
        Animation anim = new AlphaAnimation(0.0f, RotateView.DEFAULT_TEXT_SCALE_X);
        anim.setDuration(300);
        this.mSettingView.startAnimation(anim);
    }

    public void removeSettingView() {
        CamLog.d(FaceDetector.TAG, "settingView : removeSettingView - animation start");
        RelativeLayout rl = (RelativeLayout) this.mGet.findViewById(R.id.setting_expand_layout);
        if (rl == null || this.mSettingViewRemoving) {
            CamLog.d(FaceDetector.TAG, "settingView : removeSettingView - return");
            return;
        }
        this.mSettingViewRemoving = true;
        rl.clearAnimation();
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
                    if (SettingRotatableExpandableController.this.checkMediator()) {
                        SettingRotatableExpandableController.this.mGet.postOnUiThread(new Runnable() {
                            public void run() {
                                if (SettingRotatableExpandableController.this.checkMediator()) {
                                    SettingRotatableExpandableController.this.mGet.removePostRunnable(this);
                                    SettingRotatableExpandableController.this.removeSettingViewAll();
                                }
                            }
                        });
                    }
                }
            });
            anim.setDuration(300);
            rl.startAnimation(anim);
            final RelativeLayout layout = rl;
            rl.postDelayed(new Runnable() {
                public void run() {
                    layout.clearAnimation();
                    SettingRotatableExpandableController.this.removeSettingViewAll();
                }
            }, anim.getDuration());
        }
        resetChildShowingValues();
    }

    public void removeSettingViewAll() {
        CamLog.d(FaceDetector.TAG, "settingView : removeSettingViewAll -start");
        if (this.mSettingView == null || this.mSettingView.getVisibility() == 4) {
            CamLog.d(FaceDetector.TAG, "settingView : removeSettingView -return, mSettingView is null");
            this.mSettingViewRemoving = false;
            return;
        }
        this.mGet.doCommandUi(Command.ROTATE);
        if (this.mGet.getApplicationMode() == 0) {
            this.mGet.doCommandUi(Command.RELEASE_TOUCH_FOCUS);
        }
        this.mSettingView.setVisibility(4);
        this.mParentListView.setAdapter(null);
        this.mParentListView.setOnTouchListener(null);
        this.mParentListView.setOnItemClickListener(null);
        this.mParentListView.removeAllViewsInLayout();
        if (this.mParentListView.getBackground() != null) {
            this.mParentListView.getBackground().setCallback(null);
            this.mParentListView.setBackground(null);
        }
        this.mParentListView = null;
        if (this.mParentAdapter != null) {
            this.mParentAdapter.close();
            this.mParentAdapter = null;
        }
        this.mChildListView = null;
        if (this.mChildAdapter != null) {
            this.mChildAdapter.close();
            this.mChildAdapter = null;
        }
        this.mSettingView.findViewById(R.id.backcover).setOnTouchListener(null);
        ViewGroup vg = (ViewGroup) this.mGet.findViewById(R.id.init);
        if (vg != null) {
            vg.removeView(this.mSettingView);
        }
        this.mSettingView = null;
        initSettingMenu();
        this.mDegree = CHILD_POSITION_NONE;
        System.gc();
        this.mSettingViewRemoving = false;
        if (!(this.mGet.getSubMenuMode() == 6 || this.mGet.getSubMenuMode() == 25 || this.mGet.getSubMenuMode() == 18 || this.mGet.getSubMenuMode() == 15)) {
            this.mGet.setSubMenuMode(STATE_CHILD_NONE);
        }
        resetChildShowingValues();
        this.mGet.showControllerForHideSettingMenu(true, true);
    }

    public boolean isSettingViewRemoving() {
        return this.mSettingViewRemoving;
    }

    private void resetChildShowingValues() {
        this.mOpenChildPosition.x = CHILD_POSITION_NONE;
        this.mOpenChildPosition.y = CHILD_POSITION_NONE;
        this.mOpenChildLayout = null;
        this.mTempChildLayout = null;
        this.mTempColumn = CHILD_POSITION_NONE;
        this.mTempRow = CHILD_POSITION_NONE;
        this.mTempKey = null;
        this.mTempNeedOpenChild = false;
        this.mStateDisplayChild = STATE_CHILD_NONE;
    }

    private int calcWantedRow(ListView listView, int row) {
        int wantedRow = row - listView.getFirstVisiblePosition();
        if (wantedRow < 0 || wantedRow >= listView.getChildCount()) {
            return CHILD_POSITION_NONE;
        }
        return wantedRow;
    }

    public synchronized void showChildSetting(final int column, final int row, final String key) {
        CamLog.d(FaceDetector.TAG, "showChildSetting start");
        if (this.mStateDisplayChild > 0) {
            CamLog.d(FaceDetector.TAG, "Exit because state=" + this.mStateDisplayChild);
        } else {
            this.mStateDisplayChild = STATE_CHILD_DISPLAY_START;
            if (doNoChildMenu(column, row, key)) {
                this.mStateDisplayChild = STATE_CHILD_NONE;
            } else {
                int wantedRow = calcWantedRow(this.mParentListView, row);
                if (wantedRow == CHILD_POSITION_NONE) {
                    this.mStateDisplayChild = STATE_CHILD_NONE;
                } else {
                    this.mTempParentLayout = (ViewGroup) this.mParentListView.getChildAt(wantedRow);
                    this.mTempChildLayout = (ViewGroup) this.mTempParentLayout.findViewById(R.id.setting_expand_child_layout);
                    this.mTempColumn = column;
                    this.mTempRow = row;
                    this.mTempKey = key;
                    if (this.mOpenChildPosition.x <= CHILD_POSITION_NONE && this.mOpenChildPosition.y <= CHILD_POSITION_NONE) {
                        int childPosition = getCurrentSettingMenu().getMenuIndex(key);
                        this.mChildAdapter.setCurrentParentKey(key);
                        setChildViewPattern(childPosition, false);
                        this.mGet.postOnUiThread(new Runnable() {
                            public void run() {
                                SettingRotatableExpandableController.this.mGet.removePostRunnable(this);
                                SettingRotatableExpandableController.this.openChildView(SettingRotatableExpandableController.this.mTempChildLayout, column, row, key);
                            }
                        });
                    } else if (this.mOpenChildPosition.y != row) {
                        closeChildView(this.mOpenChildLayout, true);
                    } else if (this.mOpenChildPosition.x == column) {
                        closeChildView(this.mTempChildLayout, false);
                    } else {
                        closeChildView(this.mTempChildLayout, true);
                    }
                }
            }
        }
    }

    private boolean doNoChildMenu(int column, int row, String key) {
        if (!Setting.KEY_EDIT_SHORTCUT.equals(key) && !Setting.KEY_AU_CLOUD.equals(key) && !Setting.KEY_HELP_GUIDE.equals(key) && !Setting.KEY_RESTORE.equals(key)) {
            return false;
        }
        setChildViewPattern(getCurrentSettingMenu().getMenuIndex(key), false);
        String className = this.mGet.getSettingMenuCommand();
        if (className != null) {
            this.mGet.doCommandUi(className);
        }
        if (Setting.KEY_RESTORE.equals(key)) {
            closeChildView(this.mOpenChildLayout, false);
        } else {
            removeSettingView();
        }
        return true;
    }

    private synchronized boolean addChildView(ViewGroup childLayout, int column, int row, String key) {
        boolean z;
        if (childLayout == null) {
            z = false;
        } else {
            this.mStateDisplayChild = STATE_CHILD_OPEN_ADD;
            if (childLayout.getChildCount() > 0) {
                childLayout.removeAllViews();
            }
            RelativeLayout innerChildLayout = new RelativeLayout(this.mGet.getApplicationContext());
            innerChildLayout.setLayoutParams(new LayoutParams(CHILD_POSITION_NONE, -2));
            View childView;
            if (Setting.KEY_BRIGHTNESS.equals(key)) {
                childView = this.mGet.initSettingBrightnessBar();
                if (childView != null) {
                    innerChildLayout.addView(childView);
                    this.mGet.refreshBrightnessExpandSettingBars();
                }
            } else {
                childView = getChildListView(column, row, key);
                if (childView != null) {
                    innerChildLayout.addView(childView);
                }
            }
            childLayout.addView(innerChildLayout);
            z = true;
        }
        return z;
    }

    private void setArrowVisibility(View parentItem, int visibility) {
        parentItem.findViewById(R.id.setting_expand_parent_arrow).setVisibility(visibility);
    }

    public boolean checkAndCloseChildView() {
        if (this.mOpenChildPosition.x <= CHILD_POSITION_NONE && this.mOpenChildPosition.y <= CHILD_POSITION_NONE) {
            return false;
        }
        closeChildView(this.mOpenChildLayout, false);
        return true;
    }

    private synchronized void openChildView(ViewGroup childLayout, int column, int row, String key) {
        if (!addChildView(childLayout, column, row, key)) {
            this.mStateDisplayChild = STATE_CHILD_NONE;
        } else if (childLayout == null) {
            this.mStateDisplayChild = STATE_CHILD_NONE;
        } else if (childLayout.getChildCount() < 0) {
            childLayout.clearAnimation();
            this.mStateDisplayChild = STATE_CHILD_NONE;
        } else {
            this.mStateDisplayChild = STATE_CHILD_OPEN;
            if (column >= 0) {
                View parentLayout = this.mTempParentLayout.findViewById(CameraConstants.PARENT_ITEM_ID[column]);
                if (parentLayout != null) {
                    SettingExpandParentImage parentImage = (SettingExpandParentImage) parentLayout.findViewById(R.id.setting_expand_parent_item_image);
                    parentImage.selected = true;
                    parentImage.setImageLevel(1);
                    ((TextView) parentLayout.findViewById(R.id.setting_expand_parent_item_text)).setTextColor(Color.parseColor("#ff119291"));
                    setArrowVisibility(parentLayout, STATE_CHILD_NONE);
                }
            }
            ExpandAnimation animationChildOpen = new ExpandAnimation(this.mGet.getApplicationContext(), childLayout, CHILD_OPENCLOSE_ANI_TIME, true);
            animationChildOpen.setAnimationListener(this.mChildOpenAnimationListener);
            childLayout.startAnimation(animationChildOpen);
        }
    }

    private void onOpenChildAnimationEnd() {
        if (this.mParentListView == null || this.mTempChildLayout == null || this.mStateDisplayChild == 0) {
            this.mStateDisplayChild = STATE_CHILD_NONE;
            return;
        }
        this.mParentListView.smoothScrollToPositionFromTop(this.mTempRow, STATE_CHILD_NONE, STATE_CHILD_CLOSE);
        this.mOpenChildLayout = this.mTempChildLayout;
        this.mOpenChildPosition.x = this.mTempColumn;
        this.mOpenChildPosition.y = this.mTempRow;
        this.mParentListView.setScrollBarFadeDuration(STATE_CHILD_CLOSE);
        this.mStateDisplayChild = STATE_CHILD_NONE;
        this.mTouchReleaseRunnable.run();
        updateParentScrollbar();
        this.mGet.removeScheduledCommand(Command.HIDE_SETTING_EXPAND_PARENT_SCROLLBAR);
        this.mGet.doCommandDelayed(Command.HIDE_SETTING_EXPAND_PARENT_SCROLLBAR, ProjectVariables.keepDuration);
        this.mGet.postOnUiThread(this.mTouchReleaseRunnable);
        setContentDescriptionForSetting(false);
    }

    private void setContentDescriptionForSetting(boolean isChildItemClosed) {
        if (this.mParentListView != null) {
            this.mParentListView.clearFocus();
            this.mParentListView.sendAccessibilityEvent(65536);
        }
        ListPreference listPref = this.mGet.getSettingListPreference(this.mGet.getCurrentMenuKey());
        String contentDescriptionTitle = listPref.getTitle();
        String contentDescriptionValue = listPref.getEntry();
        if (Setting.KEY_BRIGHTNESS.equals(this.mGet.getCurrentMenuKey())) {
            float value = ((float) (Integer.parseInt(this.mGet.getSettingValue(Setting.KEY_BRIGHTNESS)) - (CameraConstants.MAX_BRIGHTNESS_STEP / 2))) / 3.0f;
            if (ModelProperties.isMTKChipset()) {
                value *= CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK;
            }
            contentDescriptionValue = String.format("%.1f", new Object[]{Float.valueOf(value)});
        }
        if (isChildItemClosed) {
            if (this.mParentListView != null) {
                this.mParentListView.announceForAccessibility(contentDescriptionTitle + "  " + contentDescriptionValue);
            }
        } else if (this.mOpenChildLayout != null) {
            this.mOpenChildLayout.announceForAccessibility(contentDescriptionTitle + "  " + contentDescriptionValue);
        }
    }

    private synchronized void closeChildView(ViewGroup childLayout, boolean needOpenAfterAnimation) {
        closeChildView(childLayout, needOpenAfterAnimation, true);
    }

    private synchronized void closeChildView(ViewGroup childLayout, boolean needOpenAfterAnimation, boolean needAnimation) {
        CamLog.d(FaceDetector.TAG, "closeChildView mStateDisplayChild = " + this.mStateDisplayChild);
        if (this.mStateDisplayChild == 0 || this.mStateDisplayChild <= STATE_CHILD_DISPLAY_START) {
            this.mStateDisplayChild = STATE_CHILD_CLOSE;
            if (childLayout == null) {
                this.mStateDisplayChild = STATE_CHILD_NONE;
            } else if (childLayout.getChildAt(STATE_CHILD_NONE) == null) {
                this.mStateDisplayChild = STATE_CHILD_NONE;
            } else {
                int wantedRow = calcWantedRow(this.mParentListView, this.mOpenChildPosition.y);
                if (wantedRow != CHILD_POSITION_NONE) {
                    View parentLayout = this.mParentListView.getChildAt(wantedRow).findViewById(CameraConstants.PARENT_ITEM_ID[this.mOpenChildPosition.x]);
                    if (parentLayout != null) {
                        ((SettingExpandParentImage) parentLayout.findViewById(R.id.setting_expand_parent_item_image)).selected = false;
                        setArrowVisibility(parentLayout, 4);
                        ((TextView) parentLayout.findViewById(R.id.setting_expand_parent_item_text)).setSelected(false);
                        String currentLanguage = Locale.getDefault().getLanguage();
                        if (ModelProperties.getProjectCode() == 9 && currentLanguage.equalsIgnoreCase("th")) {
                            TextView txt = (TextView) parentLayout.findViewById(R.id.setting_expand_parent_item_text);
                            txt.setShadowLayer(0.0f, 0.0f, 0.0f, CHILD_POSITION_NONE);
                            txt.setTypeface(Typeface.DEFAULT);
                        }
                        ((TextView) parentLayout.findViewById(R.id.setting_expand_parent_item_current_text)).setSelected(false);
                        if (ModelProperties.getProjectCode() == 9 && currentLanguage.equalsIgnoreCase("th")) {
                            TextView txt1 = (TextView) parentLayout.findViewById(R.id.setting_expand_parent_item_current_text);
                            txt1.setShadowLayer(0.0f, 0.0f, 0.0f, CHILD_POSITION_NONE);
                            txt1.setTypeface(Typeface.DEFAULT);
                        }
                    }
                }
                this.mTempNeedOpenChild = needOpenAfterAnimation;
                if (childLayout.getVisibility() == 0 && needAnimation) {
                    ExpandAnimation animationChildClose = new ExpandAnimation(this.mGet.getApplicationContext(), childLayout, CHILD_OPENCLOSE_ANI_TIME, false);
                    animationChildClose.setAnimationListener(this.mChildCloseAnimationListener);
                    childLayout.startAnimation(animationChildClose);
                    this.mGet.getHandler().postDelayed(this.mCheckCloseAnimation, 200);
                } else {
                    doOnCloseAnimationEnd();
                }
            }
        } else {
            CamLog.d(FaceDetector.TAG, "Exit closeChildView mStateDisplayChild = " + this.mStateDisplayChild);
        }
    }

    private void onCloseAnimationEnd() {
        doOnCloseAnimationEnd();
        updateParentScrollbar();
        this.mTouchReleaseRunnable.run();
    }

    private void doOnCloseAnimationEnd() {
        if (this.mTempChildLayout == null || this.mStateDisplayChild == 0) {
            this.mStateDisplayChild = STATE_CHILD_NONE;
            return;
        }
        this.mTempChildLayout.removeAllViews();
        this.mTempChildLayout.setVisibility(8);
        this.mParentListView.setScrollBarFadeDuration(SCROLLBAR_HIDE_TIME);
        if (this.mTempNeedOpenChild) {
            int childPosition = getCurrentSettingMenu().getMenuIndex(this.mTempKey);
            this.mChildAdapter.setCurrentParentKey(this.mTempKey);
            setChildViewPattern(childPosition, true);
            this.mGet.postOnUiThread(new Runnable() {
                public void run() {
                    SettingRotatableExpandableController.this.mGet.removePostRunnable(this);
                    SettingRotatableExpandableController.this.openChildView(SettingRotatableExpandableController.this.mTempChildLayout, SettingRotatableExpandableController.this.mTempColumn, SettingRotatableExpandableController.this.mTempRow, SettingRotatableExpandableController.this.mTempKey);
                }
            });
            return;
        }
        updateSetting();
        resetChildShowingValues();
    }

    static {
        SETTING_EXPAND_NUM_OF_CHILD_ROW = 4;
    }

    private View getChildListView(int column, int row, String key) {
        this.mChildListView = new InnerListView(this.mGet.getApplicationContext());
        this.mChildListView.setDivider(this.mGet.getResources().getDrawable(R.drawable.camera_setting_expand_list_line));
        this.mChildListView.setDividerHeight(this.DIVIDER);
        this.mChildListView.setFocusable(false);
        this.mChildListView.setAdapter(this.mChildAdapter);
        this.mChildListView.setSelected(false);
        int numOfMenus = this.mOrderCurrentSetting.size();
        if (numOfMenus >= 7 && numOfMenus <= 9) {
            SETTING_EXPAND_NUM_OF_CHILD_ROW = 3;
        } else if (numOfMenus < STATE_CHILD_DISPLAY_START || numOfMenus > STATE_CHILD_OPEN) {
            if (Util.isEqualDegree(this.mGet.getResources(), this.mDegree, 90) || Util.isEqualDegree(this.mGet.getResources(), this.mDegree, Tag.IMAGE_DESCRIPTION)) {
                SETTING_EXPAND_NUM_OF_CHILD_ROW = 5;
            } else {
                SETTING_EXPAND_NUM_OF_CHILD_ROW = 4;
            }
        } else if (Util.isEqualDegree(this.mGet.getResources(), this.mDegree, 90) || Util.isEqualDegree(this.mGet.getResources(), this.mDegree, Tag.IMAGE_DESCRIPTION)) {
            SETTING_EXPAND_NUM_OF_CHILD_ROW = 4;
        } else {
            SETTING_EXPAND_NUM_OF_CHILD_ROW = 3;
        }
        int childListHeight = (int) (((double) this.CONFIG_ITEM_HEIGHT) * (((double) SETTING_EXPAND_NUM_OF_CHILD_ROW) + 0.5d));
        int childListCountWithOutHeader = this.mChildListView.getCount();
        int childHeight = childListCountWithOutHeader * (this.CONFIG_ITEM_HEIGHT + this.DIVIDER);
        int topPosition = this.mGet.getSelectedChildIndex();
        if (childListCountWithOutHeader > SETTING_EXPAND_NUM_OF_CHILD_ROW) {
            childHeight = (int) (((double) childHeight) + (((double) this.CONFIG_ITEM_HEIGHT) * 0.5d));
            if (topPosition >= SETTING_EXPAND_NUM_OF_CHILD_ROW) {
                topPosition += CHILD_POSITION_NONE;
            } else {
                topPosition = STATE_CHILD_NONE;
            }
        }
        if (childListHeight > childHeight) {
            childListHeight = childHeight;
        }
        this.mChildListView.setLayoutParams(new LayoutParams(CHILD_POSITION_NONE, childListHeight));
        this.mChildListView.setOnItemClickListener(this.mChildSettingListItemClickListener);
        this.mChildListView.setSelectionFromTop(topPosition, STATE_CHILD_NONE);
        return this.mChildListView;
    }

    protected void setLayoutDegree(View settingMenuLayout, int degree) {
        int lcdWidth = this.mGet.getResources().getDisplayMetrics().widthPixels;
        int lcdHeight = this.mGet.getResources().getDisplayMetrics().heightPixels;
        RelativeLayout settingSubLayout = (RelativeLayout) settingMenuLayout.findViewById(R.id.setting_expand_sub_layout);
        LayoutParams lp = (LayoutParams) settingSubLayout.getLayoutParams();
        lp.width = lcdWidth;
        lp.height = lcdHeight;
        settingSubLayout.setLayoutParams(lp);
    }

    public void startRotation(int degree) {
        CamLog.d(FaceDetector.TAG, "mDegree = " + this.mDegree + ", degree = " + degree);
        if (!checkMediator()) {
            return;
        }
        if ((this.mGet.getSubMenuMode() == 5 || this.mGet.getSubMenuMode() == 16) && !isNullSettingView() && !this.mSettingViewRemoving && this.mDegree != degree) {
            this.mDegree = degree;
            hideAnimation();
            closeChildView(this.mOpenChildLayout, false, false);
            addParentMenuItem();
            rotateExpandList(degree);
            showAnimation();
        }
    }

    public void hideAnimation() {
        if (this.mGet.findViewById(R.id.setting_expand_rotate_layout) != null) {
            RotateLayout rl = (RotateLayout) this.mGet.findViewById(R.id.setting_expand_rotate_layout);
            Animation anim = new AlphaAnimation(RotateView.DEFAULT_TEXT_SCALE_X, 0.0f);
            anim.setAnimationListener(new AnimationListener() {
                public void onAnimationStart(Animation animation) {
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                }
            });
            anim.setDuration(300);
            rl.startAnimation(anim);
        }
    }

    public void showAnimation() {
        if (!this.mSettingViewRemoving) {
            RotateLayout rl = (RotateLayout) this.mGet.findViewById(R.id.setting_expand_rotate_layout);
            rl.clearAnimation();
            Animation anim = new AlphaAnimation(0.0f, RotateView.DEFAULT_TEXT_SCALE_X);
            anim.setDuration(300);
            rl.startAnimation(anim);
        }
    }

    public void rotateExpandList(int degree) {
        if (this.mGet.findViewById(R.id.setting_expand_rotate_layout) != null) {
            ((RotateLayout) this.mGet.findViewById(R.id.setting_expand_rotate_layout)).rotateLayout(degree);
        }
    }

    public boolean isNullSettingView() {
        return this.mSettingView == null;
    }

    public boolean isVisible() {
        return isNullSettingView() ? false : this.mSettingView.isShown();
    }

    private void setChildViewPattern(int position, boolean notify) {
        if (notify) {
            this.mGet.setCurrentSettingMenu(position);
        } else {
            this.mGet.setCurrentSettingMenuOnly(position);
        }
        if (this.mChildListView != null) {
            ListPreference pref = this.mGet.findPreference(this.mGet.getIndexMenuKey(position));
            if (pref == null || !pref.getKey().equals(Setting.KEY_SHUTTER_SOUND)) {
                this.mChildListView.setSoundEffectsEnabled(true);
            } else {
                this.mChildListView.setSoundEffectsEnabled(false);
            }
            this.mChildAdapter.setShowSelectedChild(true);
            if (pref == null) {
                return;
            }
            if (Setting.KEY_HELP_GUIDE.equals(pref.getKey()) || Setting.KEY_AU_CLOUD.equals(pref.getKey())) {
                this.mChildAdapter.setShowSelectedChild(false);
                this.mChildListView.setSelected(false);
                this.mGet.runOnUiThread(new Runnable() {
                    public void run() {
                        if (SettingRotatableExpandableController.this.checkMediator() && SettingRotatableExpandableController.this.mChildAdapter != null) {
                            SettingRotatableExpandableController.this.mGet.removePostRunnable(this);
                            SettingRotatableExpandableController.this.mChildAdapter.update();
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
        } else if (MultimediaProperties.isLiveEffectSupported() && this.mGet.getApplicationMode() == 1 && this.mGet.isEffectsCamcorderActive() && this.mGet.getCameraMode() != this.mGet.getSettingIndex(Setting.KEY_SWAP)) {
            CamLog.v(FaceDetector.TAG, "SwapCameraPrepared-start, liveeffect active");
            this.mGet.effectRecorderStopPreviewByCallFrom(DialogCreater.DIALOG_ID_HELP_PANORAMA);
            bundle = new Bundle();
            bundle.putBoolean("liveeffect_mode", true);
            this.mGet.doCommand(command, null, bundle);
        }
    }

    public void updateSetting() {
        this.mParentAdapter.update();
        this.mChildAdapter.update();
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
        if (isVisible()) {
            this.mGet.setSubMenuMode(STATE_CHILD_NONE);
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
        if (this.mChildListView != null) {
            CamLog.d(FaceDetector.TAG, "wow not null" + this.mChildAdapter + " " + this.mChildListView);
            this.mChildListView.setAdapter(null);
            this.mChildListView.setOnItemClickListener(null);
            this.mChildListView.removeAllViewsInLayout();
            if (this.mChildListView.getBackground() != null) {
                this.mChildListView.getBackground().setCallback(null);
                this.mChildListView.setBackground(null);
            }
            this.mChildListView = null;
        }
        if (this.mParentListView != null) {
            this.mParentListView.setAdapter(null);
            this.mParentListView.setOnItemClickListener(null);
            this.mParentListView.removeAllViewsInLayout();
            if (this.mParentListView.getBackground() != null) {
                this.mParentListView.getBackground().setCallback(null);
                this.mParentListView.setBackground(null);
            }
            this.mParentListView = null;
        }
        this.mParentScrollbarThumb = null;
        super.onDestroy();
        CamLog.d(FaceDetector.TAG, "onDestroy-end");
    }

    private void initSettingOrder() {
        if (this.mOrderBackCamera != null) {
            if (this.mOrderBackCamera.size() > 0) {
                this.mOrderBackCamera.clear();
            }
            this.mOrderBackCamera.add(Setting.KEY_VOICESHUTTER);
            this.mOrderBackCamera.add(Setting.KEY_BRIGHTNESS);
            this.mOrderBackCamera.add(Setting.KEY_FOCUS);
            this.mOrderBackCamera.add(Setting.KEY_CAMERA_PICTURESIZE);
            this.mOrderBackCamera.add(Setting.KEY_ISO);
            this.mOrderBackCamera.add(Setting.KEY_CAMERA_WHITEBALANCE);
            this.mOrderBackCamera.add(Setting.KEY_CAMERA_COLOREFFECT);
            this.mOrderBackCamera.add(Setting.KEY_CAMERA_TIMER);
            this.mOrderBackCamera.add(Setting.KEY_CAMERA_AUTO_REVIEW);
            this.mOrderBackCamera.add(Setting.KEY_CAMERA_TAG_LOCATION);
            this.mOrderBackCamera.add(Setting.KEY_SHUTTER_SOUND);
            this.mOrderBackCamera.add(Setting.KEY_VOLUME);
            this.mOrderBackCamera.add(Setting.KEY_STORAGE);
            this.mOrderBackCamera.add(Setting.KEY_AU_CLOUD);
            this.mOrderBackCamera.add(Setting.KEY_HELP_GUIDE);
            this.mOrderBackCamera.add(Setting.KEY_RESTORE);
        }
        if (this.mOrderFrontCamera != null) {
            if (this.mOrderFrontCamera.size() > 0) {
                this.mOrderFrontCamera.clear();
            }
            this.mOrderFrontCamera.add(Setting.KEY_VOICESHUTTER);
            this.mOrderFrontCamera.add(Setting.KEY_BRIGHTNESS);
            this.mOrderFrontCamera.add(Setting.KEY_CAMERA_PICTURESIZE);
            this.mOrderFrontCamera.add(Setting.KEY_CAMERA_WHITEBALANCE);
            this.mOrderFrontCamera.add(Setting.KEY_CAMERA_COLOREFFECT);
            this.mOrderFrontCamera.add(Setting.KEY_CAMERA_TIMER);
            this.mOrderFrontCamera.add(Setting.KEY_SAVE_DIRECTION);
            this.mOrderFrontCamera.add(Setting.KEY_CAMERA_AUTO_REVIEW);
            this.mOrderFrontCamera.add(Setting.KEY_CAMERA_TAG_LOCATION);
            this.mOrderFrontCamera.add(Setting.KEY_SHUTTER_SOUND);
            this.mOrderFrontCamera.add(Setting.KEY_VOLUME);
            this.mOrderFrontCamera.add(Setting.KEY_STORAGE);
            this.mOrderFrontCamera.add(Setting.KEY_AU_CLOUD);
            this.mOrderFrontCamera.add(Setting.KEY_HELP_GUIDE);
            this.mOrderFrontCamera.add(Setting.KEY_RESTORE);
        }
        if (this.mOrderBackCamcorder != null) {
            if (this.mOrderBackCamcorder.size() > 0) {
                this.mOrderBackCamcorder.clear();
            }
            this.mOrderBackCamcorder.add(Setting.KEY_CAMCORDER_AUDIOZOOM);
            this.mOrderBackCamcorder.add(Setting.KEY_PREVIEW_SIZE_ON_DEVICE);
            this.mOrderBackCamcorder.add(Setting.KEY_BRIGHTNESS);
            this.mOrderBackCamcorder.add(Setting.KEY_VIDEO_STABILIZATION);
            this.mOrderBackCamcorder.add(Setting.KEY_CAMERA_WHITEBALANCE);
            this.mOrderBackCamcorder.add(Setting.KEY_CAMERA_COLOREFFECT);
            this.mOrderBackCamcorder.add(Setting.KEY_VIDEO_AUTO_REVIEW);
            this.mOrderBackCamcorder.add(Setting.KEY_CAMERA_TAG_LOCATION);
            this.mOrderBackCamcorder.add(Setting.KEY_VOLUME);
            this.mOrderBackCamcorder.add(Setting.KEY_STORAGE);
            this.mOrderBackCamcorder.add(Setting.KEY_AU_CLOUD);
            this.mOrderBackCamcorder.add(Setting.KEY_HELP_GUIDE);
            this.mOrderBackCamcorder.add(Setting.KEY_RESTORE);
        }
        if (this.mOrderFrontCamcorder != null) {
            if (this.mOrderFrontCamcorder.size() > 0) {
                this.mOrderFrontCamcorder.clear();
            }
            this.mOrderFrontCamcorder.add(Setting.KEY_PREVIEW_SIZE_ON_DEVICE);
            this.mOrderFrontCamcorder.add(Setting.KEY_BRIGHTNESS);
            this.mOrderFrontCamcorder.add(Setting.KEY_VIDEO_STABILIZATION);
            this.mOrderFrontCamcorder.add(Setting.KEY_CAMERA_WHITEBALANCE);
            this.mOrderFrontCamcorder.add(Setting.KEY_CAMERA_COLOREFFECT);
            this.mOrderFrontCamcorder.add(Setting.KEY_VIDEO_AUTO_REVIEW);
            this.mOrderFrontCamcorder.add(Setting.KEY_CAMERA_TAG_LOCATION);
            this.mOrderFrontCamcorder.add(Setting.KEY_VOLUME);
            this.mOrderFrontCamcorder.add(Setting.KEY_STORAGE);
            this.mOrderFrontCamcorder.add(Setting.KEY_AU_CLOUD);
            this.mOrderFrontCamcorder.add(Setting.KEY_HELP_GUIDE);
            this.mOrderFrontCamcorder.add(Setting.KEY_RESTORE);
        }
    }

    private void makeSettingItemOrder() {
        ArrayList<String> orderGuide = new ArrayList();
        if (this.mGet.getApplicationMode() == 0) {
            if (this.mGet.getCameraMode() != 0) {
                orderGuide = this.mOrderFrontCamera;
            } else if (this.mGet.getCameraDimension() == 0) {
                orderGuide = this.mOrderBackCamera;
            }
        } else if (this.mGet.getCameraMode() != 0) {
            orderGuide = this.mOrderFrontCamcorder;
        } else if (this.mGet.getCameraDimension() == 0) {
            orderGuide = this.mOrderBackCamcorder;
        }
        this.mOrderCurrentSetting.clear();
        Iterator i$ = orderGuide.iterator();
        while (i$.hasNext()) {
            String key = (String) i$.next();
            if (getCurrentSettingMenu().getMenu(key) != null) {
                this.mOrderCurrentSetting.add(key);
            }
        }
    }

    public void removeChildSettingView(boolean isShowAnim) {
    }
}
