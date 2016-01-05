package com.lge.camera.controller;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout.LayoutParams;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.adapter.OptionMenuAdapter;
import com.lge.camera.command.Command;
import com.lge.camera.components.OptionMenu;
import com.lge.camera.components.RotateLayout;
import com.lge.camera.components.RotateView;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.morpho.utils.multimedia.MediaProviderUtils;
import com.lge.olaworks.define.Ola_Exif.Tag;
import com.lge.olaworks.library.FaceDetector;
import com.lge.voiceshutter.library.LGKeyRec;
import java.util.ArrayList;

public class OptionMenuController extends Controller {
    private OptionMenuAdapter mAdapter;
    private int[][] mCameraOptionMenu;
    private boolean mHideOptionMenu;
    private OnItemClickListener mOptionItemClickListener;
    private ArrayList<OptionMenu> mOptionMenuList;
    private int mOptionMenuState;
    private View mOptionMenuView;
    private int[][] mSmartCameraOptionMenu;
    private int[][] mVideoOptionMenu;

    public OptionMenuController(ControllerFunction function) {
        super(function);
        this.mOptionMenuState = 0;
        this.mOptionMenuList = new ArrayList();
        this.mAdapter = null;
        this.mOptionMenuView = null;
        this.mCameraOptionMenu = new int[][]{new int[]{R.string.edit_quick_menu, -1}, new int[]{R.string.reset, -1}, new int[]{R.string.help_title, -1}};
        this.mSmartCameraOptionMenu = new int[][]{new int[]{R.string.reset, -1}, new int[]{R.string.help_title, -1}};
        this.mVideoOptionMenu = new int[][]{new int[]{R.string.edit_quick_menu, -1}, new int[]{R.string.reset, -1}, new int[]{R.string.sp_help_video_title_NORMAL, -1}};
        this.mHideOptionMenu = false;
        this.mOptionItemClickListener = new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (OptionMenuController.this.checkMediator()) {
                    CamLog.d(FaceDetector.TAG, "OnItemClickListener position[" + position + "]");
                    String itemTitle = null;
                    OptionMenu optionMenu = (OptionMenu) parent.getAdapter().getItem(position);
                    if (optionMenu != null) {
                        itemTitle = optionMenu.getTitle();
                    }
                    OptionMenuController.this.hideOptionMenu();
                    OptionMenuController.this.onOptionItemSeleteced(itemTitle);
                }
            }
        };
    }

    public void showOptionMenu(int type) {
        createOptionMenu(type);
        if (this.mOptionMenuView == null) {
            this.mOptionMenuView = this.mGet.inflateView(R.layout.option_menu_rotate);
            ((ViewGroup) this.mGet.findViewById(R.id.init)).addView(this.mOptionMenuView);
            this.mOptionMenuView.setVisibility(0);
            this.mAdapter = new OptionMenuAdapter(this.mGet.getApplicationContext(), R.layout.option_item_view, this.mOptionMenuList);
            ListView optionMenu = (ListView) this.mGet.findViewById(R.id.option_list_view);
            optionMenu.setAdapter(this.mAdapter);
            optionMenu.setOnItemClickListener(this.mOptionItemClickListener);
            startRotation(this.mGet.getOrientationDegree());
            startOptionMenuAnimation(true, null);
        }
    }

    public void hideOptionMenu() {
        if (this.mOptionMenuView != null && this.mOptionMenuView.getVisibility() == 0 && !this.mHideOptionMenu) {
            this.mHideOptionMenu = true;
            this.mOptionMenuView.setVisibility(4);
            ((ListView) this.mGet.findViewById(R.id.option_list_view)).setOnItemClickListener(null);
            startOptionMenuAnimation(false, new AnimationListener() {
                public void onAnimationStart(Animation animation) {
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    OptionMenuController.this.mGet.postOnUiThread(new Runnable() {
                        public void run() {
                            if (OptionMenuController.this.checkMediator()) {
                                OptionMenuController.this.mGet.removePostRunnable(this);
                                OptionMenuController.this.releaseOptionMenu();
                            }
                        }
                    });
                }
            });
        }
    }

    public void releaseOptionMenu() {
        if (this.mOptionMenuView != null) {
            this.mOptionMenuView.setVisibility(8);
            ListView optionMenu = (ListView) this.mGet.findViewById(R.id.option_list_view);
            optionMenu.setAdapter(null);
            optionMenu.setOnItemClickListener(null);
            ((ViewGroup) this.mGet.findViewById(R.id.init)).removeView(this.mOptionMenuView);
            this.mOptionMenuView = null;
            if (this.mAdapter != null) {
                this.mAdapter.unbind();
                this.mAdapter = null;
            }
            this.mOptionMenuList.clear();
            this.mHideOptionMenu = false;
        }
    }

    public boolean isOptionMenuShowing() {
        return (this.mOptionMenuView == null || this.mOptionMenuView.getVisibility() == 8) ? false : true;
    }

    public void createOptionMenu(int type) {
        if (this.mOptionMenuList != null) {
            this.mOptionMenuList.clear();
            this.mOptionMenuState = type;
            switch (this.mOptionMenuState) {
                case LGKeyRec.EVENT_INVALID /*0*/:
                    makeMenuList(this.mCameraOptionMenu);
                case LGKeyRec.EVENT_NO_MATCH /*1*/:
                    makeMenuList(this.mSmartCameraOptionMenu);
                case LGKeyRec.EVENT_INCOMPLETE /*2*/:
                    makeMenuList(this.mVideoOptionMenu);
                default:
            }
        }
    }

    public void makeMenuList(int[][] menuList) {
        if (menuList.length >= 0) {
            String menuString = "";
            for (int i = 0; i < menuList.length; i++) {
                addOptionMenu(this.mGet.getString(menuList[i][0]), menuList[i][1]);
            }
        }
    }

    public void addOptionMenu(String title, int resId) {
        if (this.mOptionMenuList != null) {
            this.mOptionMenuList.add(new OptionMenu(title, resId == -1 ? null : this.mGet.getDrawable(resId)));
        }
    }

    public void removeMenu(String title, int resId) {
        if (this.mOptionMenuList != null && title != null) {
            int menuSize = this.mOptionMenuList.size();
            for (int i = 0; i < menuSize; i++) {
                OptionMenu menu = (OptionMenu) this.mOptionMenuList.get(i);
                if (menu != null && title.equals(menu.getTitle())) {
                    this.mOptionMenuList.remove(i);
                }
            }
        }
    }

    public void startRotation(int degree) {
        try {
            RotateLayout rl = (RotateLayout) this.mGet.findViewById(R.id.option_layout_rotate);
            if (rl != null) {
                LayoutParams params = (LayoutParams) rl.getLayoutParams();
                Common.resetLayoutParameter(params);
                if (this.mGet.isConfigureLandscape()) {
                    params.addRule(21, 1);
                } else {
                    params.addRule(12, 1);
                }
                int lcdHeight = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.lcd_height);
                int optionMenuWidth = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.option_menu_list_width);
                switch (degree) {
                    case LGKeyRec.EVENT_INVALID /*0*/:
                        if (!this.mGet.isConfigureLandscape()) {
                            params.addRule(20, 1);
                            params.topMargin = 0;
                            params.leftMargin = (lcdHeight - optionMenuWidth) / 2;
                            break;
                        }
                        params.addRule(12, 1);
                        params.topMargin = 0;
                        break;
                    case MediaProviderUtils.ROTATION_90 /*90*/:
                    case Tag.IMAGE_DESCRIPTION /*270*/:
                        if (!this.mGet.isConfigureLandscape()) {
                            params.addRule(20, 1);
                            params.leftMargin = 0;
                            break;
                        }
                        params.addRule(12, 0);
                        params.topMargin = (lcdHeight - optionMenuWidth) / 2;
                        break;
                    case MediaProviderUtils.ROTATION_180 /*180*/:
                        if (!this.mGet.isConfigureLandscape()) {
                            params.addRule(20, 1);
                            params.topMargin = 0;
                            params.leftMargin = (lcdHeight - optionMenuWidth) / 2;
                            break;
                        }
                        params.addRule(10, 1);
                        params.topMargin = 0;
                        break;
                }
                rl.setLayoutParams(params);
                rl.rotateLayout(degree);
            }
        } catch (ClassCastException e) {
            CamLog.w(FaceDetector.TAG, "ClassCastException:", e);
        }
    }

    public void onOptionItemSeleteced(String itemTitle) {
        if (itemTitle != null) {
            if (itemTitle.equals(this.mGet.getString(R.string.edit_quick_menu))) {
                this.mGet.doCommandDelayed(Command.SHOW_QUICK_FUNCTION_DRAG_DROP, 200);
            } else if (itemTitle.equals(this.mGet.getString(R.string.reset))) {
                this.mGet.doCommandDelayed(Command.SHOW_RESET_DIALOG, 200);
            } else if (itemTitle.equals(this.mGet.getString(R.string.help_title)) || itemTitle.equals(this.mGet.getString(R.string.sp_help_video_title_NORMAL))) {
                this.mGet.postOnUiThread(new Runnable() {
                    public void run() {
                        if (OptionMenuController.this.checkMediator()) {
                            OptionMenuController.this.mGet.removePostRunnable(this);
                            OptionMenuController.this.mGet.gotoHelpActivity(OptionMenuController.this.mGet.getApplicationMode() == 0 ? "camera help" : "camcorder help");
                        }
                    }
                }, 300);
            }
        }
    }

    public void onPause() {
        releaseOptionMenu();
        super.onPause();
    }

    public void startOptionMenuAnimation(boolean show, AnimationListener listener) {
        if (this.mOptionMenuView != null) {
            this.mOptionMenuView.clearAnimation();
            float fromX = RotateView.DEFAULT_TEXT_SCALE_X;
            float toX = RotateView.DEFAULT_TEXT_SCALE_X;
            float fromY = RotateView.DEFAULT_TEXT_SCALE_X;
            float toY = RotateView.DEFAULT_TEXT_SCALE_X;
            float startAlpha = RotateView.DEFAULT_TEXT_SCALE_X;
            float endAlpha = RotateView.DEFAULT_TEXT_SCALE_X;
            float pivotX = 0.0f;
            float pivotY = 0.0f;
            if (show) {
                fromX = 0.9f;
                fromY = 0.9f;
                startAlpha = 0.0f;
            } else {
                toX = 0.9f;
                toY = 0.9f;
                endAlpha = 0.0f;
            }
            switch (this.mGet.getOrientationDegree()) {
                case LGKeyRec.EVENT_INVALID /*0*/:
                    if (!this.mGet.isConfigureLandscape()) {
                        pivotX = 0.5f;
                        pivotY = RotateView.DEFAULT_TEXT_SCALE_X;
                        break;
                    }
                    pivotX = RotateView.DEFAULT_TEXT_SCALE_X;
                    pivotY = RotateView.DEFAULT_TEXT_SCALE_X;
                    break;
                case MediaProviderUtils.ROTATION_90 /*90*/:
                    if (!this.mGet.isConfigureLandscape()) {
                        pivotX = 0.0f;
                        pivotY = RotateView.DEFAULT_TEXT_SCALE_X;
                        break;
                    }
                    pivotX = RotateView.DEFAULT_TEXT_SCALE_X;
                    pivotY = 0.5f;
                    break;
                case MediaProviderUtils.ROTATION_180 /*180*/:
                    if (!this.mGet.isConfigureLandscape()) {
                        pivotX = 0.5f;
                        pivotY = RotateView.DEFAULT_TEXT_SCALE_X;
                        break;
                    }
                    pivotX = RotateView.DEFAULT_TEXT_SCALE_X;
                    pivotY = 0.0f;
                    break;
                case Tag.IMAGE_DESCRIPTION /*270*/:
                    if (!this.mGet.isConfigureLandscape()) {
                        pivotX = 0.0f;
                        pivotY = RotateView.DEFAULT_TEXT_SCALE_X;
                        break;
                    }
                    pivotX = RotateView.DEFAULT_TEXT_SCALE_X;
                    pivotY = 0.5f;
                    break;
            }
            ScaleAnimation sa = new ScaleAnimation(fromX, toX, fromY, toY, 1, pivotX, 1, pivotY);
            sa.setInterpolator(new DecelerateInterpolator(2.5f));
            AlphaAnimation aa = new AlphaAnimation(startAlpha, endAlpha);
            aa.setInterpolator(new DecelerateInterpolator(1.5f));
            AnimationSet aniSet = new AnimationSet(true);
            aniSet.addAnimation(sa);
            aniSet.addAnimation(aa);
            aniSet.setDuration(200);
            aniSet.setAnimationListener(listener);
            this.mOptionMenuView.startAnimation(aniSet);
        }
    }
}
