package com.lge.camera.setting;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.command.Command;
import com.lge.camera.components.SettingExpandParentImage;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.ColorUtil;
import com.lge.olaworks.define.Ola_ShotParam;
import com.lge.olaworks.library.FaceDetector;
import com.lge.voiceshutter.library.LGKeyRec;
import java.lang.ref.WeakReference;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

public class SettingExpandParentMenuAdapter extends ArrayAdapter<SettingMenuItem[]> implements Observer {
    private long calcTime;
    private WeakReference<Context> mContext;
    private ControllerFunction mGet;
    private SparseArray<View> mLocalViewMap;
    private WeakReference<SettingMenu> mMenus;
    private OnClickListener mParentOnClickListener;
    private OnTouchListener mParentOnTouchListener;
    private int mPressedCount;

    static /* synthetic */ int access$212(SettingExpandParentMenuAdapter x0, int x1) {
        int i = x0.mPressedCount + x1;
        x0.mPressedCount = i;
        return i;
    }

    static /* synthetic */ int access$220(SettingExpandParentMenuAdapter x0, int x1) {
        int i = x0.mPressedCount - x1;
        x0.mPressedCount = i;
        return i;
    }

    public SettingExpandParentMenuAdapter(Context context, int resId, SettingMenu menus, ControllerFunction mGet) {
        super(context, resId);
        this.mLocalViewMap = new SparseArray();
        this.mParentOnClickListener = new OnClickListener() {
            public void onClick(View v) {
                if (!SettingExpandParentMenuAdapter.this.isClickedLately(500)) {
                    SettingExpandParentMenuAdapter.this.calcTime = System.currentTimeMillis();
                    CamLog.d(FaceDetector.TAG, "mPressedCount=" + SettingExpandParentMenuAdapter.this.mPressedCount);
                    if (SettingExpandParentMenuAdapter.this.mPressedCount <= 0) {
                        SettingExpandParentMenuAdapter.this.mPressedCount = 0;
                        SettingExpandParentImage parentImage = (SettingExpandParentImage) v.findViewById(R.id.setting_expand_parent_item_image);
                        if (!Setting.KEY_NONE.equals(parentImage.key)) {
                            Bundle bundle = new Bundle();
                            bundle.putInt("column", parentImage.column);
                            bundle.putInt("row", parentImage.row);
                            bundle.putString("key", parentImage.key);
                            ((TextView) v.findViewById(R.id.setting_expand_parent_item_text)).setSelected(true);
                            String currentLanguage = Locale.getDefault().getLanguage();
                            if (ModelProperties.getProjectCode() == 9 && currentLanguage.equalsIgnoreCase("th")) {
                                ((TextView) v.findViewById(R.id.setting_expand_parent_item_text)).setShadowLayer(0.0f, 0.0f, 0.0f, -1);
                                ((TextView) v.findViewById(R.id.setting_expand_parent_item_text)).setTypeface(Typeface.DEFAULT);
                            }
                            ((TextView) v.findViewById(R.id.setting_expand_parent_item_current_text)).setSelected(true);
                            if (ModelProperties.getProjectCode() == 9 && currentLanguage.equalsIgnoreCase("th")) {
                                ((TextView) v.findViewById(R.id.setting_expand_parent_item_current_text)).setShadowLayer(0.0f, 0.0f, 0.0f, -1);
                                ((TextView) v.findViewById(R.id.setting_expand_parent_item_current_text)).setTypeface(Typeface.DEFAULT);
                            }
                            SettingExpandParentMenuAdapter.this.mGet.removeScheduledCommand(Command.SHOW_SETTING_EXPAND_CHILD);
                            SettingExpandParentMenuAdapter.this.mGet.doCommand(Command.SHOW_SETTING_EXPAND_CHILD, bundle);
                        }
                    }
                }
            }
        };
        this.mPressedCount = 0;
        this.mParentOnTouchListener = new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked() & Ola_ShotParam.AnimalMask_Random) {
                    case LGKeyRec.EVENT_INVALID /*0*/:
                        SettingExpandParentMenuAdapter.access$212(SettingExpandParentMenuAdapter.this, 1);
                        break;
                    case LGKeyRec.EVENT_NO_MATCH /*1*/:
                    case LGKeyRec.EVENT_STARTED /*3*/:
                        SettingExpandParentMenuAdapter.access$220(SettingExpandParentMenuAdapter.this, 1);
                        SettingExpandParentMenuAdapter.this.mPressedCount = Math.max(SettingExpandParentMenuAdapter.this.mPressedCount, 0);
                        break;
                }
                CamLog.d(FaceDetector.TAG, "mPressedCount=" + SettingExpandParentMenuAdapter.this.mPressedCount + " action=" + event.getAction());
                return false;
            }
        };
        this.mContext = new WeakReference(context);
        this.mMenus = new WeakReference(menus);
        this.mGet = mGet;
        menus.addObserver(this);
        this.mLocalViewMap.clear();
    }

    public void close() {
        SettingMenu settingMenu = (SettingMenu) this.mMenus.get();
        if (settingMenu != null) {
            settingMenu.deleteObserver(this);
        }
        this.mGet = null;
        this.mContext = null;
        if (this.mLocalViewMap != null) {
            this.mLocalViewMap.clear();
        }
    }

    public View getView(int position, View convertViews, ViewGroup parent) {
        Context context = (Context) this.mContext.get();
        SettingMenu settingMenu = (SettingMenu) this.mMenus.get();
        if (context == null) {
            return convertViews;
        }
        View localViewMap = (View) this.mLocalViewMap.get(position);
        if (localViewMap == null) {
            CamLog.d(FaceDetector.TAG, "localViewMap = null");
            if (convertViews != null) {
                localViewMap = convertViews;
                CamLog.d(FaceDetector.TAG, "set convertViews to localViewMap");
            } else {
                localViewMap = ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.setting_expand_item_view, null);
                CamLog.d(FaceDetector.TAG, "Inflate view.");
            }
            this.mLocalViewMap.put(position, localViewMap);
        }
        LinearLayout parentLayout = (LinearLayout) localViewMap.findViewById(R.id.setting_expand_parent_layout);
        parentLayout.setWeightSum((float) CameraConstants.SETTING_EXPAND_MAX_COLUMN);
        if (CameraConstants.SETTING_EXPAND_MAX_COLUMN == 3) {
            parentLayout.findViewById(CameraConstants.PARENT_ITEM_ID[3]).setVisibility(8);
        } else {
            parentLayout.findViewById(CameraConstants.PARENT_ITEM_ID[3]).setVisibility(0);
        }
        for (int i = 0; i < CameraConstants.SETTING_EXPAND_MAX_COLUMN; i++) {
            LinearLayout parentItemLayout = (LinearLayout) parentLayout.findViewById(CameraConstants.PARENT_ITEM_ID[i]);
            SettingExpandParentImage parentImage = (SettingExpandParentImage) parentItemLayout.findViewById(R.id.setting_expand_parent_item_image);
            TextView parentMenuText = (TextView) parentItemLayout.findViewById(R.id.setting_expand_parent_item_text);
            String currentLanguage = Locale.getDefault().getLanguage();
            if (ModelProperties.getProjectCode() == 9) {
                if (currentLanguage.equalsIgnoreCase("th")) {
                    parentMenuText.setShadowLayer(0.0f, 0.0f, 0.0f, -1);
                    parentMenuText.setTypeface(Typeface.DEFAULT);
                }
            }
            TextView parentCurrentText = (TextView) parentItemLayout.findViewById(R.id.setting_expand_parent_item_current_text);
            if (ModelProperties.getProjectCode() == 9) {
                if (currentLanguage.equalsIgnoreCase("th")) {
                    parentCurrentText.setShadowLayer(0.0f, 0.0f, 0.0f, -1);
                    parentCurrentText.setTypeface(Typeface.DEFAULT);
                }
            }
            parentItemLayout.setOnClickListener(this.mParentOnClickListener);
            parentItemLayout.setOnTouchListener(this.mParentOnTouchListener);
            if (((SettingMenuItem[]) getItem(position)).length - 1 < i) {
                if (((SettingMenuItem[]) getItem(position)).length >= CameraConstants.SETTING_EXPAND_MAX_COLUMN - 1) {
                    parentItemLayout.setBackgroundResource(R.drawable.expand_parent_item_bg_normal);
                } else {
                    int length = ((SettingMenuItem[]) getItem(position)).length;
                    if (i == r0) {
                        parentItemLayout.setBackgroundResource(R.drawable.expand_parent_item_empty_left_bg_normal);
                    } else if (i == CameraConstants.SETTING_EXPAND_MAX_COLUMN - 1) {
                        parentItemLayout.setBackgroundResource(R.drawable.expand_parent_item_empty_right_bg_normal);
                    } else {
                        parentItemLayout.setBackgroundResource(R.drawable.expand_parent_item_empty_center_bg_normal);
                    }
                }
                parentItemLayout.setFocusable(false);
                parentItemLayout.setVisibility(0);
                parentItemLayout.setSoundEffectsEnabled(false);
                parentImage.setImageResource(R.drawable.expand_parent_dumy);
                parentImage.key = Setting.KEY_NONE;
                parentMenuText.setText("");
                parentCurrentText.setText("");
            } else {
                SettingMenuItem menuItem = ((SettingMenuItem[]) getItem(position))[i];
                if (menuItem == null || localViewMap == null) {
                    parentItemLayout.setBackgroundResource(R.drawable.expand_parent_item_bg_normal);
                    parentItemLayout.setFocusable(false);
                    parentItemLayout.setVisibility(0);
                    parentItemLayout.setSoundEffectsEnabled(false);
                    parentImage.setImageResource(R.drawable.expand_parent_dumy);
                    parentImage.key = Setting.KEY_NONE;
                    parentMenuText.setText("");
                    parentCurrentText.setText("");
                } else {
                    int parentResourceId;
                    String parentItemDescription;
                    parentMenuText.setText(menuItem.name);
                    String parentCurrentTextString = settingMenu.getCurrentChildSettingValue(menuItem.getKey());
                    if (parentCurrentTextString != null) {
                        if (!"".equals(parentCurrentTextString)) {
                            parentCurrentText.setText(parentCurrentTextString);
                            if (ModelProperties.getProjectCode() == 9 && "th".equalsIgnoreCase(Locale.getDefault().getLanguage())) {
                                parentMenuText.setShadowLayer(0.0f, 0.0f, 0.0f, -1);
                            }
                            parentImage.key = menuItem.getKey();
                            parentImage.column = i;
                            parentImage.row = position;
                            parentResourceId = settingMenu.getCurrentChildSettingIcon(menuItem.getKey());
                            if (parentResourceId != -1) {
                                parentImage.setImageResource(menuItem.iconResourceId);
                            } else {
                                parentImage.setImageResource(parentResourceId);
                            }
                            if (menuItem.enable) {
                                getViewMenuItemDisable(parentItemLayout, parentImage, parentMenuText, parentCurrentText);
                            } else {
                                getViewMenuItemEnable(parentItemLayout, parentImage, parentMenuText, parentCurrentText);
                            }
                            parentItemLayout.setVisibility(0);
                            parentItemDescription = menuItem.name;
                            if (parentCurrentTextString != null) {
                                if (!"".equals(parentCurrentTextString)) {
                                    parentItemDescription = parentItemDescription + parentCurrentTextString;
                                }
                            }
                            parentItemLayout.setContentDescription(parentItemDescription);
                        }
                    }
                    parentCurrentText.setText(" ");
                    parentMenuText.setShadowLayer(0.0f, 0.0f, 0.0f, -1);
                    parentImage.key = menuItem.getKey();
                    parentImage.column = i;
                    parentImage.row = position;
                    parentResourceId = settingMenu.getCurrentChildSettingIcon(menuItem.getKey());
                    if (parentResourceId != -1) {
                        parentImage.setImageResource(parentResourceId);
                    } else {
                        parentImage.setImageResource(menuItem.iconResourceId);
                    }
                    if (menuItem.enable) {
                        getViewMenuItemDisable(parentItemLayout, parentImage, parentMenuText, parentCurrentText);
                    } else {
                        getViewMenuItemEnable(parentItemLayout, parentImage, parentMenuText, parentCurrentText);
                    }
                    parentItemLayout.setVisibility(0);
                    parentItemDescription = menuItem.name;
                    if (parentCurrentTextString != null) {
                        if ("".equals(parentCurrentTextString)) {
                            parentItemDescription = parentItemDescription + parentCurrentTextString;
                        }
                    }
                    parentItemLayout.setContentDescription(parentItemDescription);
                }
            }
        }
        return localViewMap;
    }

    private boolean isClickedLately(long millisToWait) {
        if (System.currentTimeMillis() - this.calcTime < millisToWait) {
            return true;
        }
        return false;
    }

    public void releasePressedCount() {
        this.mPressedCount = 0;
    }

    private void getViewMenuItemEnable(LinearLayout parentItemLayout, SettingExpandParentImage parentImage, TextView menuText, TextView currentText) {
        int i = 1;
        parentItemLayout.setBackgroundResource(R.drawable.ripple_setting_expand_parent_item);
        parentItemLayout.setSoundEffectsEnabled(true);
        parentItemLayout.setEnabled(true);
        int menuTextColor = Color.parseColor("#ff5d5f61");
        if (parentImage.selected) {
            menuTextColor = Color.parseColor("#ff119291");
        }
        parentImage.setColorFilter(ColorUtil.getDefaultColor());
        if (!parentImage.selected) {
            i = 0;
        }
        parentImage.setImageLevel(i);
        menuText.setTextColor(menuTextColor);
        currentText.setTextColor(Color.argb(Ola_ShotParam.AnimalMask_Random, 17, 146, 145));
    }

    private void getViewMenuItemDisable(LinearLayout parentItemLayout, SettingExpandParentImage parentImage, TextView menuText, TextView currentText) {
        parentImage.key = Setting.KEY_NONE;
        parentItemLayout.setSoundEffectsEnabled(false);
        parentItemLayout.setBackgroundResource(R.drawable.ripple_setting_expand_parent_item);
        parentItemLayout.setEnabled(false);
        parentImage.setColorFilter(ColorUtil.getDimColorExpand());
        menuText.setTextColor(ColorUtil.getItemColor(3));
        currentText.setTextColor(ColorUtil.getItemColor(3));
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public boolean isEnabled(int position) {
        return false;
    }

    public boolean isEnabled(int position, int column) {
        SettingMenuItem ci = ((SettingMenuItem[]) getItem(position))[column];
        if (ci == null) {
            return false;
        }
        return ci.enable;
    }

    public void update(Observable observable, Object obj) {
        update();
    }

    public void update() {
        final SettingMenu settingMenu = (SettingMenu) this.mMenus.get();
        if (settingMenu != null && settingMenu.mGet != null) {
            settingMenu.mGet.runOnUiThread(new Runnable() {
                public void run() {
                    settingMenu.mGet.removePostRunnable(this);
                    SettingExpandParentMenuAdapter.this.notifyDataSetChanged();
                }
            });
        }
    }

    public void clear() {
        if (this.mLocalViewMap != null) {
            this.mLocalViewMap.clear();
        }
        super.clear();
    }
}
