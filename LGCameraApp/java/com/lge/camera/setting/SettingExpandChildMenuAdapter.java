package com.lge.camera.setting;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.lge.camera.R;
import com.lge.camera.components.RotateView;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.ColorUtil;
import com.lge.camera.util.Common;
import com.lge.olaworks.library.FaceDetector;
import java.lang.ref.WeakReference;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

public class SettingExpandChildMenuAdapter extends BaseAdapter implements Observer {
    private final float ALPHA_DISABLE;
    private final float ALPHA_ENABLE;
    private boolean isShowSelectedCheck;
    private WeakReference<Context> mContext;
    private String mCurrentParentKey;
    private WeakReference<SettingMenu> mMenus;

    public class ChildViewHolder {
        ImageView mImage;
        TextView mName;
        ImageView mRadio;
    }

    public SettingExpandChildMenuAdapter(Context context, SettingMenu menus) {
        this.isShowSelectedCheck = true;
        this.ALPHA_ENABLE = RotateView.DEFAULT_TEXT_SCALE_X;
        this.ALPHA_DISABLE = 0.3f;
        this.mCurrentParentKey = "";
        this.mContext = new WeakReference(context);
        this.mMenus = new WeakReference(menus);
        menus.addObserver(this);
    }

    public void close() {
        SettingMenu settingMenu = (SettingMenu) this.mMenus.get();
        if (settingMenu != null) {
            settingMenu.deleteObserver(this);
        }
    }

    public void setShowSelectedChild(boolean set) {
        this.isShowSelectedCheck = set;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ChildViewHolder holder;
        Context context = (Context) this.mContext.get();
        SettingMenu settingMenu = (SettingMenu) this.mMenus.get();
        View childView = convertView;
        if (childView == null) {
            childView = ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.setting_expand_child_item_view, null);
            if (childView == null) {
                CamLog.w(FaceDetector.TAG, "SettingExpandChildMenuAdapter error. view is null.");
                return null;
            }
            holder = new ChildViewHolder();
            holder.mName = (TextView) childView.findViewById(R.id.setting_expand_child_item_text);
            String currentLanguage = Locale.getDefault().getLanguage();
            if (ModelProperties.getProjectCode() == 9 && currentLanguage.equalsIgnoreCase("th")) {
                holder.mName.setShadowLayer(0.0f, 0.0f, 0.0f, -1);
                holder.mName.setTypeface(Typeface.DEFAULT);
            }
            holder.mImage = (ImageView) childView.findViewById(R.id.setting_expand_child_item_icon);
            holder.mRadio = (ImageView) childView.findViewById(R.id.setting_expand_child_item_radioButton);
            childView.setTag(holder);
        } else {
            holder = (ChildViewHolder) childView.getTag();
        }
        LayoutParams lp = (LayoutParams) holder.mName.getLayoutParams();
        if (CameraConstants.SETTING_EXPAND_MAX_COLUMN == 3) {
            lp.setMargins(Common.getPixelFromDimens(context, R.dimen.expand_child_item_text_3_marginLeft), 0, Common.getPixelFromDimens(context, R.dimen.expand_child_item_text_3_marginRight), 0);
            lp.width = Common.getPixelFromDimens(context, R.dimen.expand_child_item_text_width);
        } else {
            lp.setMargins(Common.getPixelFromDimens(context, R.dimen.expand_child_item_text_4_marginLeft), 0, Common.getPixelFromDimens(context, R.dimen.expand_child_item_text_4_marginRight), 0);
            lp.width = -2;
        }
        holder.mName.setLayoutParams(lp);
        SettingMenuItem menuItem = getItem(position);
        if (!(menuItem == null || childView == null || context == null)) {
            if (menuItem.enable) {
                holder.mName.setTextColor(ColorUtil.getItemColor(2));
                if (ModelProperties.getProjectCode() == 9 && "th".equalsIgnoreCase(Locale.getDefault().getLanguage())) {
                    holder.mName.setShadowLayer(0.0f, 0.0f, 0.0f, -1);
                }
                holder.mName.setAlpha(RotateView.DEFAULT_TEXT_SCALE_X);
                holder.mImage.setAlpha(RotateView.DEFAULT_TEXT_SCALE_X);
                holder.mRadio.setAlpha(RotateView.DEFAULT_TEXT_SCALE_X);
            } else {
                holder.mName.setTextColor(ColorUtil.getItemColor(3));
                holder.mName.setAlpha(0.3f);
                holder.mImage.setAlpha(0.3f);
                holder.mRadio.setAlpha(0.3f);
            }
            childView.setBackgroundResource(R.drawable.selector_setting_expand);
            if (ModelProperties.getProjectCode() == 9 && "th".equalsIgnoreCase(Locale.getDefault().getLanguage())) {
                holder.mName.setShadowLayer(0.0f, 0.0f, 0.0f, -1);
            }
            holder.mName.setTypeface(Typeface.DEFAULT, 1);
            holder.mName.setText(menuItem.name);
            int[] settingIcons = settingMenu.getCurrentChildSettingIcons(this.mCurrentParentKey);
            if (settingIcons != null && settingIcons.length > position) {
                holder.mImage.setImageResource(settingIcons[position]);
            }
            if (this.isShowSelectedCheck && menuItem.enable && settingMenu != null && position == settingMenu.getCurrentMenu().selectedChildPosition) {
                holder.mRadio.setImageResource(R.drawable.btn_radio_on_holo_light_camera);
            } else {
                holder.mRadio.setImageResource(R.drawable.selector_child_setting_selected_radio);
            }
        }
        return childView;
    }

    public void setCurrentParentKey(String key) {
        this.mCurrentParentKey = key;
    }

    public int getCount() {
        SettingMenu settingMenu = (SettingMenu) this.mMenus.get();
        if (settingMenu != null) {
            return settingMenu.getCurrentMenu().getChildCount();
        }
        CamLog.d(FaceDetector.TAG, "Cannot return menu counting because settingMenu is null");
        return 0;
    }

    public SettingMenuItem getItem(int position) {
        SettingMenu settingMenu = (SettingMenu) this.mMenus.get();
        if (settingMenu == null) {
            CamLog.d(FaceDetector.TAG, "Cannot return menu item because settingMenu is null");
            return null;
        } else if (position < getCount()) {
            return settingMenu.getCurrentMenu().getChild(position);
        } else {
            CamLog.d(FaceDetector.TAG, "position is invalid");
            return null;
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

    public void update(Observable observable, Object obj) {
        update();
    }

    public void update() {
        final SettingMenu settingMenu = (SettingMenu) this.mMenus.get();
        if (settingMenu != null && settingMenu.mGet != null) {
            settingMenu.mGet.runOnUiThread(new Runnable() {
                public void run() {
                    settingMenu.mGet.removePostRunnable(this);
                    SettingExpandChildMenuAdapter.this.notifyDataSetChanged();
                }
            });
        }
    }
}
