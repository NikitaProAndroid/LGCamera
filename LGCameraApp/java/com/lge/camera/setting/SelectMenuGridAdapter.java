package com.lge.camera.setting;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.lge.camera.R;
import com.lge.camera.components.RotateImageButton;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.ColorUtil;
import com.lge.camera.util.Common;
import com.lge.olaworks.library.FaceDetector;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class SelectMenuGridAdapter extends BaseAdapter {
    private int ROW_NUM_LAND;
    private WeakReference<Context> mContext;
    private WeakReference<ArrayList<RotateImageButton>> mDragViewGroup;
    private WeakReference<ArrayList<SettingMenuItem>> mMenus;
    private int mRowNum;

    public SelectMenuGridAdapter(Context context, ArrayList<SettingMenuItem> menus, ArrayList<RotateImageButton> dragViewGroup, int colNum) {
        this.ROW_NUM_LAND = 0;
        this.mContext = new WeakReference(context);
        this.mMenus = new WeakReference(menus);
        this.mDragViewGroup = new WeakReference(dragViewGroup);
        this.mRowNum = colNum;
    }

    public void setRowNumLandscape(int num) {
        this.ROW_NUM_LAND = num;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View selectItemView = convertView;
        if (selectItemView == null) {
            selectItemView = ((LayoutInflater) ((Context) this.mContext.get()).getSystemService("layout_inflater")).inflate(R.layout.select_menu_item_view, null);
            if (selectItemView == null) {
                CamLog.w(FaceDetector.TAG, "SelectMenuGridAdapter error. view is null.");
                return null;
            }
        }
        ImageView iconView = (ImageView) selectItemView.findViewById(R.id.select_menu_item_icon);
        TextView tv = (TextView) selectItemView.findViewById(R.id.select_menu_item_text);
        if (!(tv == null || iconView == null)) {
            int gridLayoutWidth;
            int gridLayoutHeight;
            int topMargin;
            tv.setTypeface(Typeface.DEFAULT, 1);
            if (this.mRowNum == this.ROW_NUM_LAND) {
                gridLayoutWidth = Common.getPixelFromDimens((Context) this.mContext.get(), R.dimen.select_menu_item_landscape_width);
                gridLayoutHeight = Common.getPixelFromDimens((Context) this.mContext.get(), R.dimen.select_menu_item_landscape_height);
                topMargin = Common.getPixelFromDimens((Context) this.mContext.get(), R.dimen.select_menu_item_icon_landscape_marginTop);
            } else {
                gridLayoutWidth = Common.getPixelFromDimens((Context) this.mContext.get(), R.dimen.select_menu_item_portrait_width);
                gridLayoutHeight = Common.getPixelFromDimens((Context) this.mContext.get(), R.dimen.select_menu_item_portrait_height);
                topMargin = Common.getPixelFromDimens((Context) this.mContext.get(), R.dimen.select_menu_item_icon_portrait_marginTop);
            }
            selectItemView.setLayoutParams(new LayoutParams(gridLayoutWidth, gridLayoutHeight));
            RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) iconView.getLayoutParams();
            param.topMargin = topMargin;
            iconView.setLayoutParams(param);
            SettingMenuItem ci = getItem(position);
            if (ci != null) {
                tv.setText(ci.name);
                boolean isDefinedQFL = !isDefinedQFL(ci.getKey());
                int textColor = isDefinedQFL ? ColorUtil.getItemColor(2) : ColorUtil.getItemColor(3);
                int iconColor = isDefinedQFL ? ColorUtil.getItemColor(5) : ColorUtil.getItemColor(3);
                tv.setTextColor(textColor);
                if (ci.iconResourceId != 0) {
                    iconView.setImageResource(ci.iconResourceId);
                    iconView.setVisibility(0);
                    iconView.setColorFilter(iconColor);
                } else {
                    iconView.setVisibility(4);
                }
            } else {
                tv.setText(null);
                iconView.setImageResource(0);
            }
        }
        selectItemView.setBackgroundResource(R.drawable.selector_grid_setting);
        return selectItemView;
    }

    private boolean isDefinedQFL(String key) {
        for (int i = 0; i < ((ArrayList) this.mDragViewGroup.get()).size(); i++) {
            ListPreference pref = (ListPreference) ((RotateImageButton) ((ArrayList) this.mDragViewGroup.get()).get(i)).getTag();
            if (pref != null && key.compareTo(pref.getKey()) == 0) {
                return true;
            }
        }
        return false;
    }

    public int getCount() {
        return adjustCount();
    }

    public SettingMenuItem getItem(int position) {
        if (position < ((ArrayList) this.mMenus.get()).size()) {
            return (SettingMenuItem) ((ArrayList) this.mMenus.get()).get(position);
        }
        return null;
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public void setRowNum(int rowNum) {
        this.mRowNum = rowNum;
    }

    private int adjustCount() {
        int remainder = ((ArrayList) this.mMenus.get()).size() % this.mRowNum;
        int addCount = 0;
        if (remainder != 0) {
            addCount = this.mRowNum - remainder;
        }
        return ((ArrayList) this.mMenus.get()).size() + addCount;
    }
}
