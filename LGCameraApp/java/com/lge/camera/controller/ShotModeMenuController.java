package com.lge.camera.controller;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.command.Command;
import com.lge.camera.components.CustomHelpScrollView;
import com.lge.camera.components.ModeMenuListView;
import com.lge.camera.components.ModeMenuListView.OnModeMenuListListener;
import com.lge.camera.components.RotateImageButton;
import com.lge.camera.components.RotateImageView;
import com.lge.camera.components.RotateLayout;
import com.lge.camera.components.RotateView;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.setting.Setting;
import com.lge.camera.setting.ShotModeMenuGridAdapter;
import com.lge.camera.setting.ShotModeMenuListAdapter;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.camera.util.Util;
import com.lge.morpho.utils.multimedia.MediaProviderUtils;
import com.lge.olaworks.define.Ola_Exif.Tag;
import com.lge.olaworks.library.FaceDetector;
import com.lge.voiceshutter.library.LGKeyRec;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class ShotModeMenuController extends Controller {
    protected static final int ANI_DURATION = 300;
    protected static final int BACK_COVER_ANI_DURATION = 500;
    protected static final int COMMAND_DELAY = 300;
    protected static final String MODE_MENU_VIEW_LIST = "mode_menu_view_list";
    protected static final int VIEW_MODE_GRID = 1;
    protected static final int VIEW_MODE_LIST = 0;
    protected int CENTER_POS;
    protected int LAYOUT_HEIGHT;
    protected int LAYOUT_WIDTH;
    protected int NAVI_MARGIN;
    protected int SELECTED_POS;
    protected OnClickListener mAutoButtonClickListener;
    protected RotateImageButton mAutoModeButton;
    protected RotateImageView mChangeButton;
    protected OnClickListener mChangeButtonClickListener;
    protected View mContentView;
    protected TextView mDescText;
    protected ShotModeMenuGridAdapter mGridAdapter;
    protected GridView mGridMenuView;
    protected OnTouchListener mGridViewTouchListener;
    protected Animation mHide;
    private Thread mImageCacheThread;
    protected ShotModeMenuListAdapter mListAdapter;
    protected ModeMenuListView mListView;
    protected ArrayList<ModeItem> mModeItemList;
    protected OnItemClickListener mModeMenuClickListener;
    protected OnTouchListener mOnBackCoverTouchListener;
    protected OnModeMenuListListener mOnModeMenuListListener;
    protected OnScrollListener mOnScrollListener;
    protected Runnable mScrollStarted;
    protected int mScrollState;
    protected Runnable mScrollStateIdle;
    protected CustomHelpScrollView mScrollView;
    protected View mShotModeMenuView;
    protected Animation mShow;
    protected boolean mShowAnimation;
    protected TextView mTitleText;
    protected int mViewMode;

    public class ModeItem {
        public String mCommand;
        public String mDesc;
        public LevelListDrawable mDrawable;
        public int mImgResId;
        public String mKey;
        public String mTitle;
        public String mValue;

        public ModeItem(String key, String value, String title, String desc, int imgResId, String command) {
            this.mKey = null;
            this.mValue = null;
            this.mTitle = null;
            this.mDesc = null;
            this.mImgResId = 0;
            this.mDrawable = null;
            this.mCommand = null;
            this.mKey = key;
            this.mValue = value;
            this.mTitle = title;
            this.mDesc = desc;
            this.mImgResId = imgResId;
            this.mCommand = command;
        }

        public String getKey() {
            return this.mKey;
        }

        public String getValue() {
            return this.mValue;
        }

        public String getTitle() {
            return this.mTitle;
        }

        public String getDescription() {
            return this.mDesc;
        }

        public int getImageResourceId() {
            return this.mImgResId;
        }

        public void setImageDrawable(LevelListDrawable drawable) {
            this.mDrawable = drawable;
        }

        public LevelListDrawable getImageDrawable() {
            return this.mDrawable;
        }

        public String getCommand() {
            return this.mCommand;
        }
    }

    protected abstract int getCurrentItem();

    protected abstract String getCurrentItemTitle();

    protected abstract void makeItemList();

    protected abstract void setDefaultMode();

    public ShotModeMenuController(ControllerFunction function) {
        super(function);
        this.mShotModeMenuView = null;
        this.mListView = null;
        this.mListAdapter = null;
        this.mGridAdapter = null;
        this.mGridMenuView = null;
        this.mContentView = null;
        this.mTitleText = null;
        this.mDescText = null;
        this.mChangeButton = null;
        this.mAutoModeButton = null;
        this.mScrollView = null;
        this.mViewMode = VIEW_MODE_GRID;
        this.LAYOUT_WIDTH = 0;
        this.LAYOUT_HEIGHT = 0;
        this.NAVI_MARGIN = 0;
        this.CENTER_POS = 0;
        this.SELECTED_POS = 0;
        this.mModeItemList = new ArrayList();
        this.mShow = new AlphaAnimation(0.0f, RotateView.DEFAULT_TEXT_SCALE_X);
        this.mHide = new AlphaAnimation(RotateView.DEFAULT_TEXT_SCALE_X, 0.0f);
        this.mImageCacheThread = null;
        this.mScrollState = 0;
        this.mOnScrollListener = new OnScrollListener() {
            public void onScrollStateChanged(AbsListView abslistview, int scrollState) {
                if (abslistview != null) {
                    ShotModeMenuController.this.mScrollState = scrollState;
                    switch (scrollState) {
                        case LGKeyRec.EVENT_INVALID /*0*/:
                            if (ShotModeMenuController.this.mShowAnimation) {
                                ShotModeMenuController.this.mShowAnimation = false;
                                ShotModeMenuController.this.mGet.getHandler().removeCallbacks(ShotModeMenuController.this.mScrollStateIdle);
                                ShotModeMenuController.this.mGet.getHandler().removeCallbacks(ShotModeMenuController.this.mScrollStarted);
                                ShotModeMenuController.this.mGet.getHandler().post(ShotModeMenuController.this.mScrollStateIdle);
                            }
                        default:
                    }
                }
            }

            public void onScroll(AbsListView abslistview, int firstVisibleItem, int visibleItemCount, int totlaItemCount) {
            }
        };
        this.mModeMenuClickListener = new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                ModeItem item = null;
                boolean isSelected = false;
                final Bundle bundle = new Bundle();
                bundle.putBoolean(CameraConstants.MODE_MENU_COMMAND, true);
                if (ShotModeMenuController.this.mViewMode == 0) {
                    int pos = position;
                    if (ShotModeMenuController.this.mListAdapter != null) {
                        item = ShotModeMenuController.this.mListAdapter.getItem(pos);
                        if (!ShotModeMenuController.this.mListAdapter.isSelectedItem(pos)) {
                            ShotModeMenuController.this.updateListViewItem(pos);
                            isSelected = true;
                        }
                    }
                } else if (ShotModeMenuController.this.mGridAdapter != null) {
                    item = ShotModeMenuController.this.mGridAdapter.getItem(position);
                    if (!ShotModeMenuController.this.mGridAdapter.isSelectedItem(position)) {
                        ShotModeMenuController.this.updateGridViewItem(position);
                        bundle.putBoolean("fromGridView", true);
                        isSelected = true;
                    }
                }
                if (isSelected) {
                    final ModeItem selectedItem = item;
                    if (selectedItem != null) {
                        ShotModeMenuController.this.mGet.setSetting(selectedItem.getKey(), selectedItem.getValue());
                        ShotModeMenuController.this.mGet.postOnUiThread(new Runnable() {
                            public void run() {
                                ShotModeMenuController.this.mGet.removePostRunnable(this);
                                ShotModeMenuController.this.mGet.doCommand(selectedItem.getCommand(), null, bundle);
                                ShotModeMenuController.this.mGet.updateModeMenuIndicator();
                            }
                        });
                    }
                }
                ShotModeMenuController.this.hide();
            }
        };
        this.mShowAnimation = false;
        this.mOnModeMenuListListener = new OnModeMenuListListener() {
            public void onTouchScrollStarted() {
                if (ShotModeMenuController.this.mListView != null && ShotModeMenuController.this.mViewMode == 0) {
                    ShotModeMenuController.this.mShowAnimation = false;
                    ShotModeMenuController.this.mGet.getHandler().removeCallbacks(ShotModeMenuController.this.mScrollStateIdle);
                    ShotModeMenuController.this.mGet.getHandler().removeCallbacks(ShotModeMenuController.this.mScrollStarted);
                    ShotModeMenuController.this.mGet.getHandler().post(ShotModeMenuController.this.mScrollStarted);
                }
            }

            public void onTouchScrollTab(int position) {
                if (ShotModeMenuController.this.mListAdapter != null && ShotModeMenuController.this.mTitleText != null && ShotModeMenuController.this.mDescText != null) {
                    if (ShotModeMenuController.this.mContentView.getVisibility() != 0) {
                        ShotModeMenuController.this.mContentView.setVisibility(0);
                    }
                    ShotModeMenuController.this.CENTER_POS = position;
                    ModeItem item = ShotModeMenuController.this.mListAdapter.getItem(position);
                    if (item != null) {
                        ShotModeMenuController.this.mTitleText.setText(item.getTitle());
                        ShotModeMenuController.this.mDescText.setText(item.getDescription());
                    }
                    ShotModeMenuController.this.checkScrollContents();
                }
            }

            public void onTouchScrollEnd() {
                ShotModeMenuController.this.mShowAnimation = true;
            }

            public void onLayoutChanged(boolean changed, int left, int top, int right, int bottom) {
            }
        };
        this.mChangeButtonClickListener = new OnClickListener() {
            public void onClick(View view) {
                SharedPreferences pref = ShotModeMenuController.this.mGet.getActivity().getSharedPreferences(Setting.SETTING_PRIMARY, 0);
                if (pref != null && ShotModeMenuController.this.mListView != null && ShotModeMenuController.this.mGridMenuView != null) {
                    boolean currViewIsList = pref.getBoolean(ShotModeMenuController.MODE_MENU_VIEW_LIST, false);
                    View contentsLayout = ShotModeMenuController.this.mShotModeMenuView.findViewById(R.id.shot_mode_menu_contents_layout);
                    View listLayout = ShotModeMenuController.this.mShotModeMenuView.findViewById(R.id.shot_mode_menu_list_layout);
                    View gridLayout = ShotModeMenuController.this.mShotModeMenuView.findViewById(R.id.shot_mode_menu_grid_layout);
                    contentsLayout.clearAnimation();
                    listLayout.clearAnimation();
                    gridLayout.clearAnimation();
                    if (currViewIsList) {
                        ShotModeMenuController.this.mViewMode = ShotModeMenuController.VIEW_MODE_GRID;
                        ShotModeMenuController.this.mChangeButton.setImageLevel(ShotModeMenuController.VIEW_MODE_GRID);
                        gridLayout.setVisibility(4);
                    } else {
                        ShotModeMenuController.this.mViewMode = 0;
                        ShotModeMenuController.this.mChangeButton.setImageLevel(0);
                        listLayout.setVisibility(4);
                        contentsLayout.setVisibility(4);
                    }
                    ShotModeMenuController.this.setLayoutDegree(ShotModeMenuController.this.mShotModeMenuView.findViewById(R.id.shot_mode_menu_layout), ShotModeMenuController.this.mGet.getOrientationDegree(), false);
                    if (currViewIsList) {
                        currViewIsList = false;
                    } else {
                        currViewIsList = true;
                    }
                    Editor edit = pref.edit();
                    edit.putBoolean(ShotModeMenuController.MODE_MENU_VIEW_LIST, currViewIsList);
                    edit.apply();
                    if (ShotModeMenuController.this.mViewMode == ShotModeMenuController.VIEW_MODE_GRID) {
                        listLayout.setVisibility(8);
                        contentsLayout.setVisibility(8);
                        gridLayout.setVisibility(0);
                        ShotModeMenuController.this.startAlphaAnimation(gridLayout, 0, ShotModeMenuController.VIEW_MODE_GRID, 50);
                        ShotModeMenuController.this.mChangeButton.setContentDescription(ShotModeMenuController.this.mGet.getString(R.string.accessiblity_change_view_list_button));
                        return;
                    }
                    gridLayout.setVisibility(8);
                    listLayout.setVisibility(0);
                    ShotModeMenuController.this.startAlphaAnimation(listLayout, 0, ShotModeMenuController.VIEW_MODE_GRID, 100);
                    contentsLayout.setVisibility(0);
                    ShotModeMenuController.this.startAlphaAnimation(contentsLayout, 0, ShotModeMenuController.VIEW_MODE_GRID, 100);
                    ShotModeMenuController.this.mChangeButton.setContentDescription(ShotModeMenuController.this.mGet.getString(R.string.accessiblity_change_view_grid_button));
                }
            }
        };
        this.mAutoButtonClickListener = new OnClickListener() {
            public void onClick(View v) {
                ShotModeMenuController.this.setDefaultMode();
            }
        };
        this.mOnBackCoverTouchListener = new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                if (ShotModeMenuController.this.mShotModeMenuView != null && event.getAction() == 0) {
                    float x = event.getX();
                    float y = event.getY();
                    View rdl = ShotModeMenuController.this.mShotModeMenuView.findViewById(R.id.shot_mode_menu_rotate_layout);
                    if (rdl != null) {
                        boolean outTouch;
                        int margin = Math.round(Util.dpToPx(ShotModeMenuController.this.mGet.getApplicationContext(), 32.0f));
                        int top;
                        int bottom;
                        if (ShotModeMenuController.this.mGet.isConfigureLandscape()) {
                            top = rdl.getTop();
                            bottom = CameraConstants.LCD_SIZE_HEIGHT - rdl.getBottom();
                            if (x < ((float) rdl.getLeft()) || x > ((float) rdl.getRight())) {
                                outTouch = true;
                            } else {
                                outTouch = false;
                            }
                            if (outTouch || ((top >= margin && y < ((float) rdl.getTop())) || (bottom >= margin && y > ((float) rdl.getBottom())))) {
                                outTouch = true;
                            } else {
                                outTouch = false;
                            }
                        } else {
                            top = rdl.getLeft();
                            bottom = CameraConstants.LCD_SIZE_HEIGHT - rdl.getRight();
                            if (y < ((float) rdl.getTop()) || y > ((float) rdl.getBottom())) {
                                outTouch = true;
                            } else {
                                outTouch = false;
                            }
                            if (outTouch || ((top >= margin && x < ((float) rdl.getLeft())) || (bottom >= margin && x > ((float) rdl.getRight())))) {
                                outTouch = true;
                            } else {
                                outTouch = false;
                            }
                        }
                        if (outTouch) {
                            ShotModeMenuController.this.hide();
                        }
                    }
                }
                return false;
            }
        };
        this.mGridViewTouchListener = new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getPointerCount() > ShotModeMenuController.VIEW_MODE_GRID && ShotModeMenuController.this.mViewMode == ShotModeMenuController.VIEW_MODE_GRID && ShotModeMenuController.this.mGridAdapter != null) {
                    int i = 0;
                    while (i < ShotModeMenuController.this.mGridMenuView.getCount()) {
                        if (ShotModeMenuController.this.mGridAdapter.getItem(i) != null) {
                            View view = ShotModeMenuController.this.mGridMenuView.getChildAt(i);
                            if (view == null) {
                                CamLog.d(FaceDetector.TAG, "view is null!");
                                break;
                            } else if (view.isPressed() && ShotModeMenuController.this.SELECTED_POS != i) {
                                ImageView imgView = (ImageView) view.findViewById(R.id.shot_mode_menu_item_icon);
                                imgView.setBackgroundResource(R.drawable.camera_mode_menu_grid_bg_normal);
                                ShotModeMenuController.this.mGridAdapter.notifyDataSetChanged();
                                imgView.invalidate();
                            }
                        }
                        i += ShotModeMenuController.VIEW_MODE_GRID;
                    }
                }
                return false;
            }
        };
        this.mScrollStarted = new Runnable() {
            public void run() {
                if (ShotModeMenuController.this.mListView != null && ShotModeMenuController.this.mContentView.getVisibility() == 0) {
                    ShotModeMenuController.this.showingAnimation(ShotModeMenuController.this.mContentView, false);
                }
            }
        };
        this.mScrollStateIdle = new Runnable() {
            public void run() {
                if (ShotModeMenuController.this.mViewMode == 0 && ShotModeMenuController.this.mListView != null && ShotModeMenuController.this.mListAdapter != null) {
                    ShotModeMenuController.this.isCurrentCentered();
                    ShotModeMenuController.this.showingAnimation(ShotModeMenuController.this.mContentView, true);
                }
            }
        };
    }

    public void initController() {
        this.mShow.setDuration(300);
        this.mShow.setInterpolator(new DecelerateInterpolator());
        this.mHide.setDuration(300);
        this.mHide.setInterpolator(new DecelerateInterpolator());
        this.LAYOUT_WIDTH = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.shot_mode_menu_width);
        this.LAYOUT_HEIGHT = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.shot_mode_menu_height);
        this.NAVI_MARGIN = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.preview_panel_marginBottom);
        this.mInit = true;
        this.mGet.updateModeMenuIndicator();
    }

    protected void displayShotModeMenuView() {
        CamLog.d(FaceDetector.TAG, "displayShotModeMenuView - start");
        this.mGet.removeScheduledCommand(Command.EXIT_ZOOM_BRIGHTNESS_INTERACTION);
        makeItemList();
        if (this.mModeItemList.size() == 0) {
            CamLog.w(FaceDetector.TAG, "Item List is empty.");
            return;
        }
        makeAllImageResources();
        if (this.mShotModeMenuView == null) {
            this.mShotModeMenuView = this.mGet.inflateView(R.layout.shot_mode_menu);
            ((ViewGroup) this.mGet.findViewById(R.id.init)).addView(this.mShotModeMenuView);
        }
        this.mContentView = this.mGet.findViewById(R.id.shot_mode_menu_content_inner_layout);
        this.mTitleText = (TextView) this.mGet.findViewById(R.id.shot_mode_menu_title);
        this.mDescText = (TextView) this.mGet.findViewById(R.id.shot_mode_menu_desc);
        this.mShotModeMenuView.setVisibility(0);
        this.mListAdapter = new ShotModeMenuListAdapter(this.mGet.getApplicationContext(), this.mModeItemList);
        this.mListView = (ModeMenuListView) this.mShotModeMenuView.findViewById(R.id.shot_mode_menu_list);
        this.mListView.setAdapter(this.mListAdapter);
        this.mListView.setFocusable(false);
        this.mListView.setSelected(false);
        this.mListView.setDivider(null);
        this.mListView.setOnItemClickListener(this.mModeMenuClickListener);
        this.mListView.setOnModeMenuListListener(this.mOnModeMenuListListener);
        this.mListView.setOnScrollListener(this.mOnScrollListener);
        this.mGridAdapter = new ShotModeMenuGridAdapter(this.mGet.getApplicationContext(), this.mModeItemList, isLandscape(this.mGet.getOrientationDegree()) ? 4 : 3);
        this.mGridMenuView = (GridView) this.mShotModeMenuView.findViewById(R.id.shot_mode_menu_grid);
        this.mGridMenuView.setFocusable(false);
        this.mGridMenuView.setAdapter(this.mGridAdapter);
        this.mGridMenuView.setSelected(false);
        this.mGridMenuView.setSelector(R.drawable.cam_icon_empty);
        this.mGridMenuView.setOnItemClickListener(this.mModeMenuClickListener);
        this.mGridMenuView.setOnTouchListener(this.mGridViewTouchListener);
        this.SELECTED_POS = getCurrentItem();
        this.CENTER_POS = this.mListAdapter.getMiddleValue() + this.SELECTED_POS;
        this.mScrollView = (CustomHelpScrollView) this.mShotModeMenuView.findViewById(R.id.shot_mode_menu_content_scroll);
        this.mChangeButton = (RotateImageView) this.mShotModeMenuView.findViewById(R.id.shot_mode_view_changed_button);
        this.mChangeButton.setOnClickListener(this.mChangeButtonClickListener);
        setDefaultAutoButton();
        SharedPreferences pref = this.mGet.getActivity().getSharedPreferences(Setting.SETTING_PRIMARY, 0);
        if (pref != null) {
            View contentsLayout = this.mShotModeMenuView.findViewById(R.id.shot_mode_menu_contents_layout);
            View listLayout = this.mShotModeMenuView.findViewById(R.id.shot_mode_menu_list_layout);
            View gridLayout = this.mShotModeMenuView.findViewById(R.id.shot_mode_menu_grid_layout);
            if (pref.getBoolean(MODE_MENU_VIEW_LIST, false)) {
                this.mViewMode = 0;
                listLayout.setVisibility(0);
                contentsLayout.setVisibility(0);
                gridLayout.setVisibility(8);
                this.mChangeButton.setImageLevel(0);
                this.mChangeButton.setContentDescription(this.mGet.getString(R.string.accessiblity_change_view_grid_button));
            } else {
                this.mViewMode = VIEW_MODE_GRID;
                listLayout.setVisibility(8);
                contentsLayout.setVisibility(8);
                gridLayout.setVisibility(0);
                this.mChangeButton.setImageLevel(VIEW_MODE_GRID);
                this.mChangeButton.setContentDescription(this.mGet.getString(R.string.accessiblity_change_view_list_button));
            }
        }
        this.mShotModeMenuView.findViewById(R.id.backcover).setOnTouchListener(this.mOnBackCoverTouchListener);
        View shotModeMenuLayout = this.mShotModeMenuView.findViewById(R.id.shot_mode_menu_layout);
        if (ModelProperties.isSoftKeyNavigationBarModel()) {
            LayoutParams marginParams = (LayoutParams) shotModeMenuLayout.getLayoutParams();
            Common.resetLayoutParameter(marginParams);
            if (Util.isConfigureLandscape(this.mGet.getResources())) {
                marginParams.addRule(20, VIEW_MODE_GRID);
                marginParams.rightMargin = this.NAVI_MARGIN;
            } else {
                marginParams.addRule(10, VIEW_MODE_GRID);
                marginParams.bottomMargin = this.NAVI_MARGIN;
            }
            shotModeMenuLayout.setLayoutParams(marginParams);
        }
        setLayoutDegree(shotModeMenuLayout, this.mGet.getOrientationDegree(), false);
        waitImageCacheThread(false);
        showBackcoverAnimation();
        this.mGet.setSubMenuMode(27);
        CamLog.d(FaceDetector.TAG, "displayShotModeMenuView - end");
    }

    private void addHeaderView() {
        if (this.mListView != null) {
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.shot_mode_menu_list_item_width) / 2, Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.shot_mode_menu_list_item_height) / 2);
            ImageView view = new ImageView(this.mGet.getApplicationContext());
            view.setLayoutParams(params);
            this.mListView.addHeaderView(view, null, false);
        }
    }

    private void addFooterView() {
        if (this.mListView != null) {
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.shot_mode_menu_list_item_width) / 2, Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.shot_mode_menu_list_item_height) / 2);
            ImageView view = new ImageView(this.mGet.getApplicationContext());
            view.setLayoutParams(params);
            this.mListView.addFooterView(view, null, false);
        }
    }

    private void setDefaultAutoButton() {
        this.mAutoModeButton = (RotateImageButton) this.mShotModeMenuView.findViewById(R.id.shot_mode_default_button);
        this.mAutoModeButton.setOnClickListener(this.mAutoButtonClickListener);
        this.mAutoModeButton.setContentDescription(this.mGet.getString(R.string.accessiblity_default_auto_button));
        this.mAutoModeButton.setTextScaleX(RotateView.DEFAULT_TEXT_SCALE_X);
        this.mAutoModeButton.setTextStyle(VIEW_MODE_GRID);
        Paint tp = this.mAutoModeButton.getTextPaint();
        tp.setTextSize((float) this.mAutoModeButton.getTextSize());
        Drawable drawable = this.mGet.getResources().getDrawable(R.drawable.camera_mode_default_auto_button_normal);
        float buttonTargetWidth = ((float) drawable.getIntrinsicWidth()) - Util.dpToPx(this.mGet.getApplicationContext(), 8.0f);
        float mearsureText = tp.measureText(this.mGet.getString(R.string.sp_scene_mode_normal_NORMAL));
        float scaleFactor = 0.0f;
        if (Float.compare(mearsureText, 0.0f) != 0 && Float.compare(buttonTargetWidth, 0.0f) != 0 && Float.compare(mearsureText, buttonTargetWidth) != 0) {
            if (Float.compare(mearsureText, buttonTargetWidth) >= 0) {
                scaleFactor = buttonTargetWidth / mearsureText;
            }
            CamLog.d(FaceDetector.TAG, "scaleFactor = " + scaleFactor);
            if (Float.compare(scaleFactor, 0.0f) != 0) {
                this.mAutoModeButton.setTextScaleX(scaleFactor);
            }
        }
    }

    protected void removeShotModeMenuView() {
        if (this.mListView != null) {
            this.mListView.setAdapter(null);
            this.mListView.setOnItemClickListener(null);
            this.mListView.setOnScrollListener(null);
            this.mListView.setOnModeMenuListListener(null);
            this.mListView.removeAllViewsInLayout();
            if (this.mListView.getBackground() != null) {
                this.mListView.getBackground().setCallback(null);
                this.mListView.setBackground(null);
            }
            this.mListView = null;
        }
        if (this.mGridMenuView != null) {
            this.mGridMenuView.setAdapter(null);
            this.mGridMenuView.setOnItemClickListener(null);
            this.mGridMenuView.setOnTouchListener(null);
            this.mGridMenuView.removeAllViewsInLayout();
            if (this.mGridMenuView.getBackground() != null) {
                this.mGridMenuView.getBackground().setCallback(null);
                this.mGridMenuView.setBackground(null);
            }
            this.mGridMenuView = null;
        }
        if (this.mShotModeMenuView != null) {
            this.mShotModeMenuView.setVisibility(8);
            ViewGroup vg = (ViewGroup) this.mGet.findViewById(R.id.init);
            if (!(vg == null || this.mShotModeMenuView == null)) {
                vg.removeView(this.mShotModeMenuView);
            }
            this.mShotModeMenuView = null;
        }
        this.mListAdapter = null;
        this.mGridAdapter = null;
        this.mContentView = null;
        this.mTitleText = null;
        this.mDescText = null;
        this.mScrollView = null;
        if (this.mChangeButton != null) {
            this.mChangeButton.setOnClickListener(null);
            this.mChangeButton = null;
        }
        if (this.mAutoModeButton != null) {
            this.mAutoModeButton.setOnClickListener(null);
            this.mAutoModeButton = null;
        }
        waitImageCacheThread(true);
        releaseAllImageResources();
        System.gc();
    }

    public void startRotation(int degree, boolean animation) {
        if (checkComponents()) {
            View shotModeMenuLayout = this.mShotModeMenuView.findViewById(R.id.shot_mode_menu_layout);
            shotModeMenuLayout.setVisibility(4);
            setLayoutDegree(shotModeMenuLayout, degree, animation);
        }
    }

    protected void setLayoutDegree(View shotModeMenuLayout, int degree, boolean animation) {
        RotateLayout rl = (RotateLayout) this.mShotModeMenuView.findViewById(R.id.shot_mode_menu_rotate_layout);
        View innerLayout = this.mShotModeMenuView.findViewById(R.id.shot_mode_menu_inner_layout);
        LayoutParams innerParams = (LayoutParams) innerLayout.getLayoutParams();
        RotateLayout contentRl = (RotateLayout) this.mShotModeMenuView.findViewById(R.id.shot_mode_menu_content_rotate_layout);
        View autoModeButtonLayout = this.mShotModeMenuView.findViewById(R.id.shot_mode_default_button_layout);
        View changeButtonLayout = this.mShotModeMenuView.findViewById(R.id.shot_mode_view_changed_button_layout);
        LayoutParams autoButtonParams = (LayoutParams) autoModeButtonLayout.getLayoutParams();
        Common.resetLayoutParameter(autoButtonParams);
        LayoutParams changeButtonParams = (LayoutParams) changeButtonLayout.getLayoutParams();
        Common.resetLayoutParameter(changeButtonParams);
        int contentManginTop = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.shot_mode_menu_contents_title_marginTop);
        int gridMargin = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.shot_mode_menu_grid_layout_marginLeft);
        int buttonMargin = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.shot_mode_menu_change_button_margin);
        autoModeButtonLayout.setLayoutDirection(0);
        changeButtonLayout.setLayoutDirection(0);
        int i = this.mViewMode;
        if (r0 == VIEW_MODE_GRID) {
            rl.rotateLayout(degree);
            contentRl.rotateLayout(0);
            View girdLayout = this.mShotModeMenuView.findViewById(R.id.shot_mode_menu_grid_layout);
            ViewGroup.LayoutParams gridParams = (LayoutParams) girdLayout.getLayoutParams();
            if (isLandscape(degree)) {
                innerParams.width = this.LAYOUT_WIDTH;
                innerParams.height = this.LAYOUT_HEIGHT;
                this.mGridMenuView.setNumColumns(4);
                gridParams.topMargin = 0;
                gridParams.leftMargin = gridMargin;
                autoButtonParams.addRule(20, VIEW_MODE_GRID);
                autoButtonParams.addRule(10, VIEW_MODE_GRID);
                autoButtonParams.leftMargin = buttonMargin;
                autoButtonParams.topMargin = buttonMargin;
                autoButtonParams.rightMargin = 0;
                changeButtonParams.addRule(20, VIEW_MODE_GRID);
                changeButtonParams.addRule(12, VIEW_MODE_GRID);
                changeButtonParams.leftMargin = buttonMargin;
                changeButtonParams.topMargin = 0;
                changeButtonParams.bottomMargin = buttonMargin;
            } else {
                innerParams.width = this.LAYOUT_HEIGHT;
                innerParams.height = this.LAYOUT_WIDTH;
                this.mGridMenuView.setNumColumns(3);
                gridParams.topMargin = gridMargin;
                gridParams.leftMargin = 0;
                autoButtonParams.addRule(21, VIEW_MODE_GRID);
                autoButtonParams.addRule(10, VIEW_MODE_GRID);
                autoButtonParams.leftMargin = 0;
                autoButtonParams.topMargin = buttonMargin;
                autoButtonParams.rightMargin = buttonMargin;
                changeButtonParams.addRule(20, VIEW_MODE_GRID);
                changeButtonParams.addRule(10, VIEW_MODE_GRID);
                changeButtonParams.leftMargin = buttonMargin;
                changeButtonParams.topMargin = buttonMargin;
                changeButtonParams.bottomMargin = 0;
            }
            this.mGridAdapter.setSelectedItem(this.SELECTED_POS);
            this.mGet.setDegree(R.id.shot_mode_view_changed_button, 0, false);
            this.mGet.setDegree(R.id.shot_mode_default_button, 0, false);
            girdLayout.setLayoutParams(gridParams);
        } else {
            autoButtonParams.addRule(20, VIEW_MODE_GRID);
            autoButtonParams.addRule(10, VIEW_MODE_GRID);
            autoButtonParams.leftMargin = buttonMargin;
            autoButtonParams.topMargin = buttonMargin;
            autoButtonParams.rightMargin = 0;
            changeButtonParams.addRule(20, VIEW_MODE_GRID);
            changeButtonParams.addRule(12, VIEW_MODE_GRID);
            changeButtonParams.leftMargin = buttonMargin;
            changeButtonParams.topMargin = 0;
            changeButtonParams.bottomMargin = buttonMargin;
            innerParams.width = this.LAYOUT_WIDTH;
            innerParams.height = this.LAYOUT_HEIGHT;
            int paddingTop = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.shot_mode_menu_contents_paddingTop);
            int paddingLeft = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.shot_mode_menu_contents_paddingLeft);
            int paddingExtra = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.shot_mode_menu_contents_padding);
            int contentMarginLeft = 0;
            View contents = this.mShotModeMenuView.findViewById(R.id.shot_mode_menu_content_scroll_layout);
            if (isLandscape(degree)) {
                contents.setPaddingRelative(paddingLeft, contentManginTop + paddingTop, paddingLeft, paddingExtra);
                rl.rotateLayout(degree);
                contentRl.rotateLayout(0);
                this.mListAdapter.setListItemDegree(0);
                this.mGet.setDegree(R.id.shot_mode_view_changed_button, 0, false);
                this.mGet.setDegree(R.id.shot_mode_default_button, 0, false);
            } else {
                contentMarginLeft = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.shot_mode_menu_contents_port_marginLeft);
                contents.setPaddingRelative(paddingLeft, contentManginTop + paddingTop, paddingLeft, paddingExtra);
                rl.rotateLayout(degree == 0 ? Tag.IMAGE_DESCRIPTION : 90);
                contentRl.rotateLayout(90);
                this.mListAdapter.setListItemDegree(90);
                this.mGet.setDegree(R.id.shot_mode_view_changed_button, 90, false);
                this.mGet.setDegree(R.id.shot_mode_default_button, 90, false);
            }
            MarginLayoutParams contentsParams = (MarginLayoutParams) contents.getLayoutParams();
            contentsParams.leftMargin = contentMarginLeft;
            contents.setLayoutParams(contentsParams);
            this.mListView.invalidateViews();
            int height = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.shot_mode_menu_list_item_height);
            this.mListView.setSelectionFromTop(this.CENTER_POS, height / 2);
        }
        autoModeButtonLayout.setLayoutParams(autoButtonParams);
        changeButtonLayout.setLayoutParams(changeButtonParams);
        innerLayout.setLayoutParams(innerParams);
        final View view = shotModeMenuLayout;
        final boolean z = animation;
        this.mGet.getHandler().postDelayed(new Runnable() {
            public void run() {
                if (ShotModeMenuController.this.mViewMode == 0) {
                    if (ShotModeMenuController.this.mContentView != null) {
                        ShotModeMenuController.this.isCurrentCentered();
                        ShotModeMenuController.this.mContentView.setVisibility(0);
                    }
                } else if (ShotModeMenuController.this.mGridMenuView != null) {
                    ShotModeMenuController.this.mGridMenuView.setSelection(ShotModeMenuController.this.SELECTED_POS);
                    ShotModeMenuController.this.mGridMenuView.smoothScrollToPositionFromTop(ShotModeMenuController.this.SELECTED_POS, 0, 0);
                }
                if (view != null && z) {
                    ShotModeMenuController.this.showingAnimation(view, true);
                } else if (view != null) {
                    view.setVisibility(0);
                }
            }
        }, 0);
    }

    public void hide() {
        hide(true);
    }

    public void hide(boolean showAnimation) {
        if (this.mInit && this.mShotModeMenuView != null) {
            if (this.mListView != null) {
                this.mListView.setOnItemClickListener(null);
            }
            if (this.mGridMenuView != null) {
                this.mGridMenuView.setOnItemClickListener(null);
            }
            View backCover = this.mShotModeMenuView.findViewById(R.id.backcover);
            if (this.mAutoModeButton != null) {
                this.mAutoModeButton.setOnClickListener(null);
                this.mAutoModeButton.setEnabled(false);
            }
            if (this.mChangeButton != null) {
                this.mChangeButton.setOnClickListener(null);
                this.mChangeButton.setEnabled(false);
            }
            if (backCover != null) {
                backCover.setOnTouchListener(null);
            }
            if (showAnimation) {
                shotModeMenuAnimation(this.mShotModeMenuView, false);
            } else {
                removeShotModeMenuView();
            }
            if (this.mGet.getApplicationMode() == 0) {
                this.mGet.doCommandUi(Command.RELEASE_TOUCH_FOCUS);
            }
            showMenuForEffects();
            showViewForShotMode();
            if (this.mGet.needProgressBar()) {
                this.mGet.recordingControllerShow();
            }
            if (this.mGet.getSubMenuMode() != 15) {
                this.mGet.setSubMenuMode(0);
            }
            if (!(Setting.HELP_FACE_TRACKING_LED.equals(this.mGet.getSettingValue(Setting.KEY_FOCUS)) || CameraConstants.FOCUS_SETTING_VALUE_MANUAL.equals(this.mGet.getSettingValue(Setting.KEY_FOCUS)))) {
                this.mGet.showFocus();
            }
            if (this.mGet.getApplicationMode() == 0 && this.mGet.getCameraMode() == VIEW_MODE_GRID) {
                this.mGet.doCommandUi(Command.RELEASE_TOUCH_FOCUS);
            }
        }
    }

    public void show() {
        if (this.mInit) {
            displayShotModeMenuView();
        }
    }

    public boolean isVisible() {
        if (this.mInit && this.mGet.findViewById(R.id.shot_mode_menu_view) != null && this.mGet.findViewById(R.id.shot_mode_menu_view).getVisibility() == 0) {
            return true;
        }
        return false;
    }

    protected boolean isLandscape(int degree) {
        return Util.isEqualDegree(this.mGet.getResources(), degree, 0) || Util.isEqualDegree(this.mGet.getResources(), degree, MediaProviderUtils.ROTATION_180);
    }

    protected void isCurrentCentered() {
        CamLog.d(FaceDetector.TAG, "isCurrentCentered");
        if (this.mListView != null && this.mListAdapter != null && this.mListView.getChildCount() > 0) {
            int center_pos = this.mListView.pointToPosition(this.mListView.getMeasuredWidth() / 2, this.mListView.getMeasuredHeight() / 2);
            int destY = (this.mListView.getMeasuredHeight() - this.mListView.getChildAt(0).getMeasuredHeight()) / 2;
            View view = this.mListView.getChildAt(center_pos - this.mListView.getFirstVisiblePosition());
            int childY = Math.round(view != null ? view.getY() : 0.0f);
            if (childY - destY != 0) {
                this.mListView.smoothScrollBy(childY - destY, 0);
            }
            int pos = center_pos;
            this.CENTER_POS = pos;
            ModeItem item = this.mListAdapter.getItem(pos);
            if (!(item == null || this.mTitleText == null || this.mDescText == null)) {
                this.mTitleText.setText(item.getTitle());
                this.mDescText.setText(item.getDescription());
            }
            checkScrollContents();
        }
    }

    protected void shotModeMenuAnimation(final View aniView, final boolean show) {
        CamLog.d(FaceDetector.TAG, "shotModeMenuAnimation-start : show = " + show);
        if (aniView != null) {
            aniView.clearAnimation();
            aniView.setVisibility(show ? 0 : 4);
            Animation animation = show ? new AlphaAnimation(0.0f, RotateView.DEFAULT_TEXT_SCALE_X) : new AlphaAnimation(RotateView.DEFAULT_TEXT_SCALE_X, 0.0f);
            animation.setDuration(300);
            animation.setInterpolator(new DecelerateInterpolator());
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
                    } else {
                        ShotModeMenuController.this.mGet.postOnUiThread(new Runnable() {
                            public void run() {
                                ShotModeMenuController.this.mGet.removePostRunnable(this);
                                ShotModeMenuController.this.removeShotModeMenuView();
                            }
                        });
                    }
                }
            });
            aniView.startAnimation(animation);
        }
    }

    protected void showingAnimation(View aniView, boolean show) {
        CamLog.d(FaceDetector.TAG, "showingAnimation-start : show = " + show);
        if (aniView != null) {
            aniView.setVisibility(show ? 0 : 4);
            aniView.startAnimation(show ? this.mShow : this.mHide);
        }
    }

    protected void startAlphaAnimation(View v, int start, int end, int duration) {
        Animation anim = new AlphaAnimation((float) start, (float) end);
        anim.setDuration((long) duration);
        v.startAnimation(anim);
    }

    protected void showBackcoverAnimation() {
        if (this.mShotModeMenuView != null) {
            RelativeLayout rl = (RelativeLayout) this.mShotModeMenuView.findViewById(R.id.backcover);
            Animation anim = new AlphaAnimation(0.0f, RotateView.DEFAULT_TEXT_SCALE_X);
            anim.setDuration(500);
            rl.startAnimation(anim);
        }
    }

    private void updateGridViewItem(int position) {
        if (this.mGridMenuView == null || this.mGridAdapter == null) {
            CamLog.d(FaceDetector.TAG, "mGridMenuView is null = ");
            return;
        }
        changeGridBackground(this.mGridAdapter.getSelectedItem(), false);
        changeGridBackground(position, true);
        this.mGridAdapter.setSelectedItem(position);
        this.mGridAdapter.notifyDataSetChanged();
    }

    private void changeGridBackground(int position, boolean selected) {
        if (this.mGridMenuView == null || this.mGridAdapter == null) {
            CamLog.d(FaceDetector.TAG, "mGridMenuView is null = ");
            return;
        }
        View v = this.mGridMenuView.getChildAt(position);
        if (v != null) {
            ((ImageView) v.findViewById(R.id.shot_mode_menu_item_icon)).setBackgroundResource(selected ? R.drawable.camera_mode_menu_grid_bg_pressed : R.drawable.selector_mode_menu_grid_bg);
        }
    }

    private void updateListViewItem(int position) {
        if (this.mListView != null && this.mListAdapter != null) {
            changeListBackground(this.mListAdapter.getSelectedItem(), false);
            changeListBackground(position, true);
            this.mListAdapter.setSelectedItem(position);
            this.mListAdapter.notifyDataSetChanged();
        }
    }

    private void changeListBackground(int position, boolean selected) {
        if (this.mListView != null && this.mListAdapter != null) {
            View v = this.mListView.getChildAt(position);
            if (v != null) {
                v.findViewById(R.id.shot_mode_menu_list_item_layout).setBackgroundResource(selected ? R.drawable.camera_mode_menu_list_bg_pressed : R.drawable.selector_mode_menu_list_bg);
                ((TextView) v.findViewById(R.id.shot_mode_menu_item_text_bottom)).setPressed(selected);
            }
        }
    }

    private void showMenuForEffects() {
        if (this.mGet.isDualCameraActive() || this.mGet.isDualRecordingActive() || this.mGet.isSmartZoomRecordingActive()) {
            this.mGet.doCommandUi(Command.SHOW_PIP_FRAME_SUB_MENU);
        } else if (this.mGet.isLiveEffectActive()) {
            Bundle isOpen = new Bundle();
            isOpen.putBoolean("menu_open", false);
            this.mGet.doCommandUi(Command.SHOW_LIVEEFFECT_SUBMENU_DRAWER, isOpen);
        }
    }

    private void showViewForShotMode() {
        String shotMode = this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE);
        if (CameraConstants.TYPE_SHOTMODE_FRONT_BEAUTY.equals(shotMode) || CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY.equals(shotMode)) {
            this.mGet.showBeautyshotController(true);
        }
    }

    public void onPause() {
        CamLog.d(FaceDetector.TAG, "onPause-start");
        if (isVisible()) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("immediately", true);
            this.mGet.doCommandUi(Command.HIDE_MODE_MENU, bundle);
        }
        super.onPause();
        CamLog.d(FaceDetector.TAG, "onPause-end");
    }

    public void onDestroy() {
        CamLog.d(FaceDetector.TAG, "onDestroy");
        super.onPause();
    }

    protected void checkScrollContents() {
        if (this.mScrollView != null && this.mScrollView.getScrollY() != 0) {
            this.mScrollView.scrollTo(0, 0);
        }
    }

    protected boolean checkComponents() {
        return (!this.mInit || this.mShotModeMenuView == null || this.mListAdapter == null || this.mListView == null || this.mGridAdapter == null || this.mGridMenuView == null || this.mContentView == null || this.mTitleText == null || this.mDescText == null || this.mChangeButton == null || this.mAutoModeButton == null) ? false : true;
    }

    public String getCurrentSelectedTitle() {
        if (this.mModeItemList == null) {
            return "";
        }
        String title;
        synchronized (this) {
            title = "";
            if (this.mModeItemList.size() == 0) {
                makeItemList();
                title = getCurrentItemTitle();
                releaseAllImageResources();
            } else {
                title = getCurrentItemTitle();
            }
        }
        return title;
    }

    private void waitImageCacheThread(boolean cancel) {
        if (this.mImageCacheThread != null && this.mImageCacheThread.isAlive()) {
            if (cancel) {
                this.mImageCacheThread.interrupt();
            }
            try {
                this.mImageCacheThread.join();
            } catch (InterruptedException e) {
                CamLog.d(FaceDetector.TAG, "Image cache thread join. ", e);
            }
            this.mImageCacheThread = null;
        }
    }

    private void makeAllImageResources() {
        this.mImageCacheThread = new Thread() {
            public void run() {
                if (ShotModeMenuController.this.mModeItemList != null) {
                    Iterator i$ = ShotModeMenuController.this.mModeItemList.iterator();
                    while (i$.hasNext()) {
                        ModeItem item = (ModeItem) i$.next();
                        if (!ShotModeMenuController.this.mImageCacheThread.isInterrupted()) {
                            if (item != null) {
                                item.setImageDrawable((LevelListDrawable) ShotModeMenuController.this.mGet.getDrawable(item.getImageResourceId()));
                            }
                        } else {
                            return;
                        }
                    }
                }
            }
        };
        this.mImageCacheThread.start();
    }

    private void releaseAllImageResources() {
        if (this.mModeItemList != null) {
            Iterator i$ = this.mModeItemList.iterator();
            while (i$.hasNext()) {
                ModeItem item = (ModeItem) i$.next();
                if (item.mDrawable != null) {
                    item.mDrawable = null;
                }
            }
            this.mModeItemList.clear();
        }
    }
}
