package com.lge.camera.controller;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.command.Command;
import com.lge.camera.components.DragLayout;
import com.lge.camera.components.RotateDragView;
import com.lge.camera.components.RotateImageButton;
import com.lge.camera.components.RotateLayout;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.setting.ListPreference;
import com.lge.camera.setting.SelectMenuGridAdapter;
import com.lge.camera.setting.SettingMenuItem;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.ColorUtil;
import com.lge.camera.util.Util;
import com.lge.morpho.utils.multimedia.MediaProviderUtils;
import com.lge.olaworks.define.Ola_Exif.Tag;
import com.lge.olaworks.define.Ola_ShotParam;
import com.lge.olaworks.library.FaceDetector;
import com.lge.voiceshutter.library.LGKeyRec;
import java.util.ArrayList;
import java.util.Iterator;

public class QuickFunctionDragController extends Controller {
    private static final int CLEAR_X = -200;
    private static final int CLEAR_Y = -200;
    public static final int COL_MIN_NUM_LANDSCAPE = 3;
    public static final int COL_MIN_NUM_PORTRAIT;
    public static final int ROW_NUM_LANDSCAPE;
    public static final int ROW_NUM_PORTRAIT;
    private static final int VIBRATE_DURATION = 35;
    private int QUICKFUNCTION_WIDTH;
    private int SELECT_MENU_ITEM_HEIGHT;
    private int SELECT_MENU_VIEW_HEIGHT;
    private int SELECT_MENU_VIEW_WIDTH;
    private final int[] mCoordinatesTemp;
    private int mDegree;
    private DragLayout mDragLayout;
    private RotateDragView mDragView;
    private ArrayList<RotateImageButton> mDragViewGroup;
    private OnTouchListener mOnGridViewTouchListener;
    public OnTouchListener mOnTouchDragListener;
    private View mOriginator;
    private int mSelectIndex;
    private SelectMenuGridAdapter mSelectMenuAdapter;
    private GridView mSelectMenuGridView;
    ArrayList<SettingMenuItem> mSelectMenuList;
    private OnItemClickListener mSelectMenuListViewItemClickListener;
    private View mSelectMenuView;
    private Bitmap mSrcBitmap;
    private Vibrator mVibrator;

    static {
        ROW_NUM_LANDSCAPE = getRowNumLandscape();
        ROW_NUM_PORTRAIT = getRowNumPortrait();
        COL_MIN_NUM_PORTRAIT = getColMinNumPortrait();
    }

    private static int getRowNumLandscape() {
        ModelProperties.getProjectCode();
        return 4;
    }

    private static int getRowNumPortrait() {
        switch (ModelProperties.getProjectCode()) {
            case Ola_ShotParam.ImageEffect_Vivid /*14*/:
                return 4;
            default:
                return COL_MIN_NUM_LANDSCAPE;
        }
    }

    private static int getColMinNumPortrait() {
        ModelProperties.getProjectCode();
        return 4;
    }

    public QuickFunctionDragController(ControllerFunction function) {
        super(function);
        this.mDragViewGroup = null;
        this.mDegree = -1;
        this.mCoordinatesTemp = new int[2];
        this.mSelectMenuList = new ArrayList();
        this.mSelectMenuListViewItemClickListener = new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                CamLog.d(FaceDetector.TAG, "selectMenu List itemIndex: " + position);
                if (QuickFunctionDragController.this.mInit) {
                    int menuPosition = position + 1;
                    if (QuickFunctionDragController.this.checkPreference(menuPosition)) {
                        ListPreference listPref = QuickFunctionDragController.this.mGet.findPreference(QuickFunctionDragController.this.mGet.getIndexMenuKey(menuPosition));
                        if (listPref != null && !QuickFunctionDragController.this.checkMenuDuplicated(listPref.getKey())) {
                            int iconResId;
                            QuickFunctionDragController.this.mSelectMenuAdapter.notifyDataSetChanged();
                            ((RotateImageButton) QuickFunctionDragController.this.mDragViewGroup.get(QuickFunctionDragController.this.mSelectIndex)).setTag(listPref);
                            int iconIndex = listPref.findIndexOfValue(listPref.getValue());
                            if (iconIndex != -1) {
                                try {
                                    iconResId = listPref.getMenuIconResources()[iconIndex];
                                } catch (ArrayIndexOutOfBoundsException e) {
                                    CamLog.w(FaceDetector.TAG, "ArrayIndexOutOfBoundsException:", e);
                                    iconResId = QuickFunctionDragController.ROW_NUM_PORTRAIT;
                                }
                            } else {
                                iconResId = listPref.getMenuIconResources()[QuickFunctionDragController.ROW_NUM_PORTRAIT];
                            }
                            ((RotateImageButton) QuickFunctionDragController.this.mDragViewGroup.get(QuickFunctionDragController.this.mSelectIndex)).setImageResource(iconResId);
                            ((RotateImageButton) QuickFunctionDragController.this.mDragViewGroup.get(QuickFunctionDragController.this.mSelectIndex)).setContentDescription(QuickFunctionDragController.this.getMenuIconStringResource(QuickFunctionDragController.this.mGet.getIndexMenuKey(menuPosition)));
                            if (QuickFunctionDragController.this.mGet.getSettingMenuEnable(menuPosition)) {
                                ((RotateImageButton) QuickFunctionDragController.this.mDragViewGroup.get(QuickFunctionDragController.this.mSelectIndex)).setColorFilter(ColorUtil.getDefaultColor());
                            } else {
                                ((RotateImageButton) QuickFunctionDragController.this.mDragViewGroup.get(QuickFunctionDragController.this.mSelectIndex)).setColorFilter(ColorUtil.getDimColor());
                            }
                        }
                    }
                }
            }
        };
        this.mOnGridViewTouchListener = new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent me) {
                if (QuickFunctionDragController.this.mInit && ((me.getAction() == 1 && view != null && view == QuickFunctionDragController.this.mSelectMenuGridView && !QuickFunctionDragController.this.isInView(view, me)) || me.getPointerCount() > 1)) {
                    QuickFunctionDragController.this.mSelectMenuAdapter.notifyDataSetChanged();
                }
                return false;
            }
        };
        this.mOnTouchDragListener = new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (!QuickFunctionDragController.this.mInit || QuickFunctionDragController.this.mDragLayout == null || !QuickFunctionDragController.this.mDragLayout.getDrag() || event.getPointerCount() > 1) {
                    return false;
                }
                int screenX = (int) event.getRawX();
                int screenY = (int) event.getRawY();
                switch (event.getAction()) {
                    case QuickFunctionDragController.ROW_NUM_PORTRAIT:
                        QuickFunctionDragController.this.mDragLayout.setMotionDownX((float) screenX);
                        QuickFunctionDragController.this.mDragLayout.setMotionDownY((float) screenY);
                        break;
                    case LGKeyRec.EVENT_NO_MATCH /*1*/:
                        if (QuickFunctionDragController.this.mDragLayout != null && QuickFunctionDragController.this.mDragLayout.getDrag()) {
                            QuickFunctionDragController.this.drop((float) screenX, (float) screenY);
                        }
                        QuickFunctionDragController.this.endDrag();
                        break;
                    case LGKeyRec.EVENT_INCOMPLETE /*2*/:
                        QuickFunctionDragController.this.mDragView.move((int) event.getRawX(), (int) event.getRawY());
                        if (!QuickFunctionDragController.this.isOverlapTrashIcon(screenX, screenY)) {
                            QuickFunctionDragController.this.mGet.findViewById(R.id.osd_drag_btn5).setPressed(false);
                            break;
                        }
                        QuickFunctionDragController.this.mGet.findViewById(R.id.osd_drag_btn5).setPressed(true);
                        break;
                    case QuickFunctionDragController.COL_MIN_NUM_LANDSCAPE /*3*/:
                        QuickFunctionDragController.this.cancelDrag();
                        break;
                }
                return true;
            }
        };
    }

    public void initController() {
    }

    public void hide(boolean showAnimation) {
        if (this.mInit) {
            if (this.mDragLayout != null && this.mDragLayout.getDrag()) {
                drop(-200.0f, -200.0f);
                endDrag();
            }
            View qflDragView = this.mGet.findViewById(R.id.quick_function_drag_drop);
            if (qflDragView != null) {
                qflDragView.setVisibility(4);
                if (showAnimation) {
                    qflDragAnimation(qflDragView, false);
                }
            }
        }
    }

    public void hide() {
        hide(true);
    }

    public void show() {
        if (this.mInit) {
            clearSelectedDragViewGroup();
            ((RotateImageButton) this.mDragViewGroup.get(this.mSelectIndex)).setSelected(true);
            initDragDropMenus();
            View qflDragView = this.mGet.findViewById(R.id.quick_function_drag_drop);
            if (qflDragView != null) {
                qflDragView.setVisibility(ROW_NUM_PORTRAIT);
                displaySelectMenuView();
            }
        }
    }

    private void makeSelectMenuList() {
        if (this.mInit) {
            this.mGet.initSettingMenu();
            this.mSelectMenuList.clear();
            for (int i = 1; i < this.mGet.getSettingMenuCount(); i++) {
                this.mSelectMenuList.add(this.mGet.getSettingMenuItem(i));
            }
        }
    }

    public void qflDragAnimation(final View aniView, final boolean show) {
        CamLog.d(FaceDetector.TAG, "qflSettingAnimation-start");
        if (this.mInit && aniView != null) {
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
                            aniView.setVisibility(QuickFunctionDragController.ROW_NUM_PORTRAIT);
                        } else {
                            QuickFunctionDragController.this.mGet.postOnUiThread(new Runnable() {
                                public void run() {
                                    QuickFunctionDragController.this.mGet.removePostRunnable(this);
                                    QuickFunctionDragController.this.removeSelectMenuView();
                                }
                            });
                        }
                    }
                });
                aniView.startAnimation(animation);
            } catch (NullPointerException e) {
                CamLog.e(FaceDetector.TAG, "NullPointerException : ", e);
            }
        }
    }

    private void displaySelectMenuView() {
        if (this.mInit) {
            makeSelectMenuList();
            this.mSelectMenuAdapter = new SelectMenuGridAdapter(this.mGet.getApplicationContext(), this.mSelectMenuList, this.mDragViewGroup, 5);
            this.mSelectMenuAdapter.setRowNumLandscape(ROW_NUM_LANDSCAPE);
            if (this.mSelectMenuView == null) {
                this.mSelectMenuView = this.mGet.inflateView(R.layout.select_menu_rotate);
                this.mDragLayout.addView(this.mSelectMenuView);
            }
            this.mSelectMenuView.setVisibility(ROW_NUM_PORTRAIT);
            this.mSelectMenuGridView = (GridView) this.mGet.findViewById(R.id.select_menu_view);
            this.mSelectMenuGridView.setFocusable(false);
            this.mSelectMenuGridView.setAdapter(this.mSelectMenuAdapter);
            this.mSelectMenuGridView.setSelected(false);
            this.mSelectMenuGridView.setOnTouchListener(this.mOnGridViewTouchListener);
            this.mSelectMenuGridView.setOnItemClickListener(this.mSelectMenuListViewItemClickListener);
            this.mGet.doCommandUi(Command.ROTATE);
            qflDragAnimation(this.mSelectMenuGridView, true);
        }
    }

    private void removeSelectMenuView() {
        if (this.mInit) {
            if (this.mSelectMenuGridView != null) {
                this.mSelectMenuGridView.setAdapter(null);
                this.mSelectMenuGridView.setOnTouchListener(null);
                this.mSelectMenuGridView.setOnItemClickListener(null);
                this.mSelectMenuGridView.removeAllViewsInLayout();
                if (this.mSelectMenuGridView.getBackground() != null) {
                    this.mSelectMenuGridView.getBackground().setCallback(null);
                    this.mSelectMenuGridView.setBackground(null);
                }
                this.mSelectMenuGridView = null;
            }
            if (this.mSelectMenuView != null) {
                this.mSelectMenuView.setVisibility(4);
                ViewGroup vg = this.mDragLayout;
                if (!(vg == null || this.mSelectMenuView == null)) {
                    vg.removeView(this.mSelectMenuView);
                }
                this.mSelectMenuView = null;
            }
            this.mSelectMenuAdapter = null;
        }
    }

    private boolean checkMenuDuplicated(String key) {
        if (!this.mInit) {
            return false;
        }
        for (int i = ROW_NUM_PORTRAIT; i < this.mDragViewGroup.size(); i++) {
            ListPreference pref = (ListPreference) ((RotateImageButton) this.mDragViewGroup.get(i)).getTag();
            if (pref != null && key.equals(pref.getKey())) {
                return true;
            }
        }
        return false;
    }

    public View getDragView(int index) {
        if (this.mInit) {
            return (View) this.mDragViewGroup.get(index);
        }
        return null;
    }

    public boolean isVisible() {
        if (this.mInit && this.mGet.findViewById(R.id.quick_function_drag_drop).getVisibility() == 0) {
            return true;
        }
        return false;
    }

    public boolean isNullSelectMenuView() {
        return this.mSelectMenuGridView == null;
    }

    public void startRotation(int degree, boolean animation) {
        if (this.mInit) {
            this.mDegree = degree;
            for (int i = ROW_NUM_PORTRAIT; i < this.mDragViewGroup.size(); i++) {
                this.mGet.setDegree(((RotateImageButton) this.mDragViewGroup.get(i)).getId(), degree, animation);
            }
            if (checkMediator() && !isNullSelectMenuView()) {
                rotateSelectMenu(degree);
            }
            if (this.mDragView != null) {
                this.mDragView.setDegree(degree, animation);
            }
        }
    }

    private void rotateSelectMenu(int degree) {
        if (this.mInit && this.mGet.findViewById(R.id.select_menu_layout) != null) {
            RotateLayout cl = (RotateLayout) this.mGet.findViewById(R.id.select_menu_layout);
            cl.rotateLayout(degree);
            updateSelectMenuGridLayout();
            int marginTop = ROW_NUM_PORTRAIT;
            int marginLeft = ROW_NUM_PORTRAIT;
            if (!this.mGet.isConfigureLandscape()) {
                degree = (degree + 90) % CameraConstants.DEGREE_360;
            }
            MarginLayoutParams params = (MarginLayoutParams) cl.getLayoutParams();
            switch (degree) {
                case ROW_NUM_PORTRAIT:
                case MediaProviderUtils.ROTATION_180 /*180*/:
                    marginTop = getPixelFromDimens(R.dimen.select_menu_layout_landscape_marginTop);
                    marginLeft = this.QUICKFUNCTION_WIDTH;
                    break;
                case MediaProviderUtils.ROTATION_90 /*90*/:
                case Tag.IMAGE_DESCRIPTION /*270*/:
                    marginTop = getPixelFromDimens(R.dimen.select_menu_layout_landscape_marginTop);
                    marginLeft = this.QUICKFUNCTION_WIDTH;
                    break;
            }
            if (this.mGet.isConfigureLandscape()) {
                params.leftMargin = marginLeft;
                params.topMargin = marginTop;
            } else {
                params.topMargin = marginLeft;
                params.rightMargin = marginTop;
            }
            cl.setLayoutParams(params);
        }
    }

    private void updateSelectMenuGridLayout() {
        if (this.mInit) {
            LayoutParams layoutParams;
            int col;
            if (Util.isEqualDegree(this.mGet.getResources(), this.mDegree, ROW_NUM_PORTRAIT) || Util.isEqualDegree(this.mGet.getResources(), this.mDegree, MediaProviderUtils.ROTATION_180)) {
                this.SELECT_MENU_ITEM_HEIGHT = getPixelFromDimens(R.dimen.select_menu_item_landscape_height);
                this.mSelectMenuAdapter.setRowNum(ROW_NUM_LANDSCAPE);
                this.mSelectMenuGridView.setNumColumns(ROW_NUM_LANDSCAPE);
                col = this.mSelectMenuAdapter.getCount() / ROW_NUM_LANDSCAPE;
                if (col > COL_MIN_NUM_LANDSCAPE) {
                    this.SELECT_MENU_VIEW_HEIGHT = getPixelFromDimens(R.dimen.select_menu_view_landscape_height);
                } else {
                    this.SELECT_MENU_VIEW_HEIGHT = (this.SELECT_MENU_ITEM_HEIGHT * col) + col;
                }
                this.SELECT_MENU_VIEW_WIDTH = getPixelFromDimens(R.dimen.select_menu_view_landscape_width);
                layoutParams = new LayoutParams(this.SELECT_MENU_VIEW_WIDTH, this.SELECT_MENU_VIEW_HEIGHT);
            } else {
                this.SELECT_MENU_ITEM_HEIGHT = getPixelFromDimens(R.dimen.select_menu_item_portrait_height);
                this.mSelectMenuAdapter.setRowNum(ROW_NUM_PORTRAIT);
                this.mSelectMenuGridView.setNumColumns(ROW_NUM_PORTRAIT);
                col = this.mSelectMenuAdapter.getCount() / ROW_NUM_PORTRAIT;
                if (col > COL_MIN_NUM_PORTRAIT) {
                    this.SELECT_MENU_VIEW_HEIGHT = getPixelFromDimens(R.dimen.select_menu_view_portrait_height);
                } else {
                    this.SELECT_MENU_VIEW_HEIGHT = (this.SELECT_MENU_ITEM_HEIGHT * col) + col;
                }
                this.SELECT_MENU_VIEW_WIDTH = getPixelFromDimens(R.dimen.select_menu_view_portrait_width);
                layoutParams = new LayoutParams(this.SELECT_MENU_VIEW_WIDTH, this.SELECT_MENU_VIEW_HEIGHT);
            }
            this.mSelectMenuGridView.setLayoutParams(layoutParams);
        }
    }

    public void setSelectIndex(int index) {
        this.mSelectIndex = index;
    }

    public void initDragDropMenus() {
        if (this.mInit) {
            int i = ROW_NUM_PORTRAIT;
            while (i < this.mDragViewGroup.size()) {
                ((RotateImageButton) this.mDragViewGroup.get(i)).setTag(null);
                ListPreference pref = this.mGet.getQuickFunctionControllerMenuTag(i);
                if (pref != null) {
                    if (i < this.mDragViewGroup.size() - 1) {
                        BitmapDrawable bitmapDrawable = (BitmapDrawable) ((ImageView) this.mGet.getQuickFunctionControllerMenuView(i)).getDrawable();
                        if (bitmapDrawable != null) {
                            setDragDropIcon((View) this.mDragViewGroup.get(i), pref, bitmapDrawable.getBitmap());
                        } else {
                            setEmptyIcon((View) this.mDragViewGroup.get(i));
                        }
                    } else {
                        setDragDropIcon((View) this.mDragViewGroup.get(i), pref, (int) R.drawable.camera_preview_quickfunction_none);
                    }
                    i++;
                } else {
                    return;
                }
            }
        }
    }

    private void clearSelectedDragViewGroup() {
        if (this.mInit) {
            Iterator i$ = this.mDragViewGroup.iterator();
            while (i$.hasNext()) {
                ((RotateImageButton) i$.next()).setSelected(false);
            }
        }
    }

    private int getDragViewGroupIndex(View v) {
        if (!this.mInit) {
            return -1;
        }
        for (int i = ROW_NUM_PORTRAIT; i < this.mDragViewGroup.size(); i++) {
            if (v.getId() == ((RotateImageButton) this.mDragViewGroup.get(i)).getId()) {
                return i;
            }
        }
        return -1;
    }

    public void startDrag(View v) {
        if (this.mInit) {
            ListPreference pref = (ListPreference) v.getTag();
            if (this.mDragLayout != null && pref != null) {
                this.mVibrator = (Vibrator) this.mGet.getActivity().getSystemService("vibrator");
                this.mVibrator.vibrate(35);
                this.mOriginator = v;
                BitmapDrawable bd = (BitmapDrawable) ((ImageView) v).getDrawable();
                if (bd != null) {
                    this.mSrcBitmap = bd.getBitmap();
                    if (this.mSrcBitmap != null) {
                        int[] loc = this.mCoordinatesTemp;
                        v.getLocationOnScreen(loc);
                        int screenX = loc[ROW_NUM_PORTRAIT];
                        int regX = ((int) this.mDragLayout.getMotionDownX()) - screenX;
                        int regY = ((int) this.mDragLayout.getMotionDownY()) - loc[1];
                        this.mDragLayout.setDrag(true);
                        this.mDragView = new RotateDragView(this.mGet.getApplicationContext(), this.mDragLayout, regX, regY, ROW_NUM_PORTRAIT, ROW_NUM_PORTRAIT, this.mSrcBitmap.getWidth(), this.mSrcBitmap.getHeight());
                        this.mDragView.setImageBitmap(this.mSrcBitmap);
                        Drawable dragBackGround = this.mGet.getDrawable(R.drawable.camera_preview_quickfunction_pressed);
                        if (dragBackGround != null) {
                            dragBackGround.setAlpha(178);
                            this.mDragView.setBackground(dragBackGround);
                        }
                        this.mDragView.setDegree(this.mGet.getOrientationDegree(), false);
                        this.mDragView.show((int) this.mDragLayout.getMotionDownX(), (int) this.mDragLayout.getMotionDownY());
                        setEmptyIcon(v);
                        ((RotateImageButton) this.mGet.findViewById(R.id.osd_drag_btn5)).setImageResource(R.drawable.camera_preview_quickfunction_delete);
                    }
                }
            }
        }
    }

    private void setEmptyIcon(View v) {
        if (this.mInit) {
            ((ImageView) v).setColorFilter(ColorUtil.getDefaultColor());
            ((ImageView) v).setImageResource(R.drawable.camera_empty_plus);
            ((ImageView) v).setContentDescription(this.mGet.getString(R.string.sp_info_empty_SHORT));
        }
    }

    private void setDragDropIcon(View v, ListPreference pref, Bitmap bitmap) {
        if (this.mInit) {
            v.setTag(pref);
            if (pref != null) {
                SettingMenuItem menuItem = this.mGet.getSettingMenuItem(pref.getKey());
                if (menuItem != null) {
                    if (menuItem.enable) {
                        ((ImageView) v).setColorFilter(ColorUtil.getDefaultColor());
                    } else {
                        ((ImageView) v).setColorFilter(ColorUtil.getDimColor());
                    }
                    ((ImageView) v).setContentDescription(getMenuIconStringResource(menuItem.getKey()));
                }
            }
            ((ImageView) v).setImageBitmap(bitmap);
        }
    }

    private void setDragDropIcon(View v, ListPreference pref, int resId) {
        if (this.mInit) {
            v.setTag(pref);
            if (pref != null) {
                SettingMenuItem menuItem = this.mGet.getSettingMenuItem(pref.getKey());
                if (menuItem != null) {
                    if (menuItem.enable) {
                        ((ImageView) v).setColorFilter(ColorUtil.getDefaultColor());
                    } else {
                        ((ImageView) v).setColorFilter(ColorUtil.getDimColor());
                    }
                }
            }
            ((ImageView) v).setImageResource(resId);
        }
    }

    public void cancelDrag() {
        endDrag();
    }

    private RotateImageButton findDropTarget(int x, int y) {
        if (!this.mInit) {
            return null;
        }
        Rect rect = new Rect();
        for (int i = ROW_NUM_PORTRAIT; i < this.mDragViewGroup.size(); i++) {
            View view = (View) this.mDragViewGroup.get(i);
            if (view != null) {
                view.getHitRect(rect);
                if (rect.contains(x, y)) {
                    return (RotateImageButton) view;
                }
            }
        }
        return null;
    }

    private boolean isOverlapTrashIcon(int x, int y) {
        if (!this.mInit) {
            return false;
        }
        View overlabView = findDropTarget(x, y);
        if (overlabView == null || overlabView.getId() != R.id.osd_drag_btn5) {
            return false;
        }
        return true;
    }

    private void drop(float x, float y) {
        if (this.mInit) {
            View dropTraget = findDropTarget((int) x, (int) y);
            if (this.mOriginator == null) {
                CamLog.w(FaceDetector.TAG, "drop-mOriginator is null.");
                return;
            }
            ListPreference srcPref = (ListPreference) this.mOriginator.getTag();
            if (dropTraget == null) {
                this.mGet.findViewById(R.id.osd_drag_btn5).setPressed(false);
                setDragDropIcon(this.mOriginator, srcPref, this.mSrcBitmap);
                this.mOriginator.setSelected(true);
            } else if (dropTraget.getId() == R.id.osd_drag_btn5) {
                this.mGet.findViewById(R.id.osd_drag_btn5).setPressed(false);
                this.mOriginator.setTag(null);
                this.mOriginator.setSelected(true);
                if (this.mSelectMenuAdapter != null) {
                    this.mSelectMenuAdapter.notifyDataSetChanged();
                }
            } else {
                ListPreference destPref = (ListPreference) dropTraget.getTag();
                if (destPref != null) {
                    setDragDropIcon(this.mOriginator, destPref, ((BitmapDrawable) ((ImageView) dropTraget).getDrawable()).getBitmap());
                } else {
                    this.mOriginator.setTag(null);
                }
                setDragDropIcon(dropTraget, srcPref, this.mSrcBitmap);
                this.mSelectIndex = getDragViewGroupIndex(dropTraget);
                ((RotateImageButton) this.mDragViewGroup.get(this.mSelectIndex)).setSelected(true);
            }
            ((RotateImageButton) this.mGet.findViewById(R.id.osd_drag_btn5)).setImageResource(R.drawable.camera_preview_quickfunction_none);
        }
    }

    private void endDrag() {
        if (this.mInit && this.mDragLayout != null && this.mDragLayout.getDrag()) {
            this.mDragLayout.setDrag(false);
            if (this.mDragView != null) {
                this.mDragView.remove();
                this.mDragView = null;
            }
        }
    }

    public void onPause() {
        if (this.mInit) {
            if (isVisible()) {
                this.mGet.doCommand(Command.HIDE_QUICK_FUNCTION_DRAG_DROP);
            }
            super.onPause();
        }
    }

    public void onDestroy() {
        if (this.mDragLayout != null) {
            this.mDragLayout.setOnTouchListener(null);
            this.mDragLayout = null;
        }
        this.mOriginator = null;
        this.mVibrator = null;
        this.mSelectMenuView = null;
        if (this.mSrcBitmap != null) {
            this.mSrcBitmap = null;
        }
        this.mDragViewGroup = null;
        super.onDestroy();
    }
}
