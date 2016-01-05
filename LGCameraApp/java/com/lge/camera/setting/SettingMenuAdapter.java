package com.lge.camera.setting;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.lge.camera.R;
import com.lge.camera.components.RotateView;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.ColorUtil;
import com.lge.camera.util.Common;
import com.lge.olaworks.library.FaceDetector;
import java.lang.ref.WeakReference;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

public class SettingMenuAdapter extends BaseAdapter implements Observer {
    public static final int CHILD_MENU = 1;
    public static final int COLOR_BACKNORMAL = 2;
    public static final int COLOR_BACKSELECT = 1;
    public static final int PARENT_MENU = 0;
    private float PARENT_ITEM_TEXT_WIDTH;
    private WeakReference<Context> context;
    private boolean isShowChild;
    private boolean isShowSelectedCheck;
    private WeakReference<SettingMenu> menus;
    private int type;

    public SettingMenuAdapter(Context context, SettingMenu menus) {
        this(context, menus, COLOR_BACKSELECT);
    }

    public SettingMenuAdapter(Context context, SettingMenu menus, int type) {
        this.type = COLOR_BACKSELECT;
        this.isShowChild = false;
        this.isShowSelectedCheck = true;
        this.PARENT_ITEM_TEXT_WIDTH = 0.0f;
        this.context = new WeakReference(context);
        this.menus = new WeakReference(menus);
        menus.addObserver(this);
        this.type = type;
        this.PARENT_ITEM_TEXT_WIDTH = (float) Common.getPixelFromDimens(context, R.dimen.config_parent_item_text_width);
    }

    public void close() {
        SettingMenu settingMenu = (SettingMenu) this.menus.get();
        if (settingMenu != null) {
            settingMenu.deleteObserver(this);
        }
    }

    public void setShowChild(boolean set) {
        this.isShowChild = set;
    }

    public void setShowSelectedChild(boolean set) {
        this.isShowSelectedCheck = set;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Context tempContext = (Context) this.context.get();
        SettingMenu settingMenu = (SettingMenu) this.menus.get();
        if (convertView == null && tempContext != null) {
            LayoutInflater vi = (LayoutInflater) tempContext.getSystemService("layout_inflater");
            if (this.type == 0) {
                convertView = vi.inflate(R.layout.setting_parent_item_view, null);
            } else {
                convertView = vi.inflate(R.layout.setting_item_view, null);
            }
        }
        SettingMenuItem menuItem = getItem(position);
        String currentLanguage = Locale.getDefault().getLanguage();
        if (!(menuItem == null || convertView == null || tempContext == null)) {
            TextView menuText = (TextView) convertView.findViewById(R.id.config_item_text);
            if (ModelProperties.getProjectCode() == 9 && "th".equalsIgnoreCase(currentLanguage)) {
                menuText.setShadowLayer(0.0f, 0.0f, 0.0f, -1);
            }
            if (menuItem.enable) {
                getViewMenuItemEnable(position, convertView, tempContext, settingMenu, menuText);
            } else {
                getViewMenuItemDisable(convertView, tempContext, menuText);
            }
            if (this.type == 0) {
                TextView currentSettingText = (TextView) convertView.findViewById(R.id.config_item_current_text);
                if (menuItem.enable) {
                    currentSettingText.setVisibility(0);
                    String value = settingMenu.getCurrentChildSettingValue(position);
                    if (value == null || "".equals(value)) {
                        currentSettingText.setVisibility(8);
                    } else {
                        currentSettingText.setText(value);
                    }
                } else {
                    currentSettingText.setVisibility(8);
                    currentSettingText.setText("Disable");
                }
                currentSettingText.setTypeface(Typeface.DEFAULT);
                currentSettingText.setTextColor(ColorUtil.getItemColor(6));
                TextPaint tp = menuText.getPaint();
                menuText.setTextScaleX(RotateView.DEFAULT_TEXT_SCALE_X);
                float mearsureTextWidth = tp.measureText(menuItem.name);
                float textViewWidth = this.PARENT_ITEM_TEXT_WIDTH;
                if (!(Float.compare(mearsureTextWidth, 0.0f) == 0 || Float.compare(textViewWidth, 0.0f) == 0 || Float.compare(mearsureTextWidth, textViewWidth) < 0)) {
                    menuText.setTextScaleX(textViewWidth / mearsureTextWidth);
                }
                menuText.setTypeface(Typeface.DEFAULT, COLOR_BACKSELECT);
            } else {
                menuText.setTypeface(Typeface.DEFAULT, COLOR_BACKSELECT);
            }
            menuText.setText(menuItem.name);
            setCheckImage(position, convertView, settingMenu, menuItem);
            setSettingImages(position, convertView, tempContext, settingMenu, menuItem, menuText);
        }
        return convertView;
    }

    private void setSettingImages(int position, View convertView, Context tempContext, SettingMenu settingMenu, SettingMenuItem menuItem, TextView menuText) {
        if (menuItem.iconResourceId != 0) {
            ImageView icon = (ImageView) convertView.findViewById(R.id.setting_icon);
            icon.setImageResource(menuItem.iconResourceId);
            if (this.type == 0) {
                icon.setVisibility(0);
                if (this.isShowChild && settingMenu != null && settingMenu.getCurrentMenuIndex() == position) {
                    icon.setColorFilter(ColorUtil.getItemColor(5));
                    return;
                } else if (menuItem.enable) {
                    icon.setColorFilter(ColorUtil.getItemColor(5));
                    return;
                } else {
                    icon.setColorFilter(ColorUtil.getItemColor(3));
                    return;
                }
            }
            icon.setVisibility(4);
            return;
        }
        ((ImageView) convertView.findViewById(R.id.setting_icon)).setVisibility(4);
    }

    private void setCheckImage(int position, View convertView, SettingMenu settingMenu, SettingMenuItem menuItem) {
        if (this.type == COLOR_BACKSELECT) {
            ImageView checkImage = (ImageView) convertView.findViewById(R.id.config_item_check);
            if (this.isShowSelectedCheck && menuItem.enable && settingMenu != null && position == settingMenu.getCurrentMenu().selectedChildPosition) {
                checkImage.setVisibility(0);
                checkImage.setColorFilter(ColorUtil.getItemColor(4));
                return;
            }
            checkImage.setVisibility(4);
        }
    }

    private void getViewMenuItemDisable(View convertView, Context tempContext, TextView tv) {
        tv.setTextColor(ColorUtil.getItemColor(3));
        if (this.type == 0) {
            convertView.setBackgroundResource(getItemResource(COLOR_BACKNORMAL));
        } else {
            convertView.setBackgroundResource(getItemResource(COLOR_BACKNORMAL));
        }
    }

    private void getViewMenuItemEnable(int position, View convertView, Context tempContext, SettingMenu settingMenu, TextView tv) {
        if (this.type == 0) {
            if (this.isShowChild && settingMenu != null && settingMenu.getCurrentMenuIndex() == position) {
                tv.setTextColor(ColorUtil.getItemColor(COLOR_BACKSELECT));
                convertView.setBackgroundResource(getItemResource(COLOR_BACKSELECT));
                return;
            }
            tv.setTextColor(ColorUtil.getItemColor(COLOR_BACKNORMAL));
            convertView.setBackgroundResource(getItemResource(COLOR_BACKNORMAL));
        } else if (settingMenu == null || settingMenu.getCurrentMenu().selectedChildPosition != position) {
            tv.setTextColor(ColorUtil.getItemColor(COLOR_BACKNORMAL));
            convertView.setBackgroundResource(getItemResource(COLOR_BACKNORMAL));
        } else {
            tv.setTextColor(ColorUtil.getItemColor(COLOR_BACKSELECT));
            convertView.setBackgroundResource(getItemResource(COLOR_BACKSELECT));
        }
    }

    public int getCount() {
        SettingMenu settingMenu = (SettingMenu) this.menus.get();
        if (settingMenu == null) {
            CamLog.d(FaceDetector.TAG, "Cannot return menu counting because settingMenu is null");
            return 0;
        } else if (this.type == 0) {
            return settingMenu.getMenuCount();
        } else {
            return settingMenu.getCurrentMenu().getChildCount();
        }
    }

    public SettingMenuItem getItem(int position) {
        SettingMenu settingMenu = (SettingMenu) this.menus.get();
        if (settingMenu == null) {
            CamLog.d(FaceDetector.TAG, "Cannot return menu item because settingMenu is null");
            return null;
        } else if (this.type == 0) {
            return settingMenu.getMenu(position);
        } else {
            return settingMenu.getCurrentMenu().getChild(position);
        }
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public boolean isEnabled(int position) {
        SettingMenuItem ci = getItem(position);
        if (ci == null) {
            return false;
        }
        return ci.enable;
    }

    private int getItemResource(int type) {
        switch (type) {
            case COLOR_BACKSELECT /*1*/:
                return R.drawable.selector_setting_select;
            default:
                return R.drawable.selector_setting_expand;
        }
    }

    public void update(Observable observable, Object obj) {
        update();
    }

    public void update() {
        final SettingMenu settingMenu = (SettingMenu) this.menus.get();
        if (settingMenu != null && settingMenu.mGet != null) {
            settingMenu.mGet.runOnUiThread(new Runnable() {
                public void run() {
                    settingMenu.mGet.removePostRunnable(this);
                    SettingMenuAdapter.this.notifyDataSetChanged();
                }
            });
        }
    }
}
