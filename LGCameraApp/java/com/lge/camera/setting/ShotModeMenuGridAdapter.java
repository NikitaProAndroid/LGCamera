package com.lge.camera.setting;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.LevelListDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.lge.camera.R;
import com.lge.camera.controller.ShotModeMenuController.ModeItem;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class ShotModeMenuGridAdapter extends BaseAdapter {
    public static final int ROW_COUNT_LAND = 4;
    public static final int ROW_COUNT_PORT = 3;
    private WeakReference<Context> mContext;
    private WeakReference<ArrayList<ModeItem>> mMenus;
    private int mSelectedItemPos;

    public class ModeMenuViewHolder {
        ImageView mImage;
        TextView mName;
    }

    public ShotModeMenuGridAdapter(Context context, ArrayList<ModeItem> menus, int rowNum) {
        this.mSelectedItemPos = -1;
        this.mContext = new WeakReference(context);
        this.mMenus = new WeakReference(menus);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ModeMenuViewHolder holder;
        View modeItemView = convertView;
        if (modeItemView == null) {
            modeItemView = ((LayoutInflater) ((Context) this.mContext.get()).getSystemService("layout_inflater")).inflate(R.layout.shot_mode_menu_grid_item_view, null);
            if (modeItemView == null) {
                CamLog.w(FaceDetector.TAG, "SelectMenuGridAdapter error. view is null.");
                return null;
            }
            holder = new ModeMenuViewHolder();
            holder.mImage = (ImageView) modeItemView.findViewById(R.id.shot_mode_menu_item_icon);
            holder.mName = (TextView) modeItemView.findViewById(R.id.shot_mode_menu_item_text_bottom);
            modeItemView.setTag(holder);
        } else {
            holder = (ModeMenuViewHolder) modeItemView.getTag();
        }
        if (!(holder.mName == null || holder.mImage == null)) {
            holder.mName.setTypeface(Typeface.DEFAULT, 1);
            ModeItem ci = getItem(position);
            if (ci != null) {
                String title = ci.getTitle();
                holder.mName.setText(title);
                modeItemView.setContentDescription(title);
                LevelListDrawable imageDrawable = ci.getImageDrawable();
                if (!(ci.getImageResourceId() == 0 || imageDrawable == null)) {
                    holder.mImage.setImageResource(ci.getImageResourceId());
                    holder.mImage.setImageLevel(1);
                }
                if (isSelectedItem(position)) {
                    holder.mImage.setBackgroundResource(R.drawable.camera_mode_menu_grid_bg_pressed);
                } else {
                    holder.mImage.setBackgroundResource(R.drawable.selector_mode_menu_grid_bg);
                }
            } else {
                holder.mName.setText(null);
                holder.mImage.setImageDrawable(null);
                holder.mImage.setBackground(null);
            }
        }
        return modeItemView;
    }

    public void setSelectedItem(int position) {
        this.mSelectedItemPos = position;
    }

    public int getSelectedItem() {
        return this.mSelectedItemPos;
    }

    public boolean isSelectedItem(int position) {
        return this.mSelectedItemPos == position;
    }

    public int getCount() {
        return ((ArrayList) this.mMenus.get()).size();
    }

    public ModeItem getItem(int position) {
        if (position < ((ArrayList) this.mMenus.get()).size()) {
            return (ModeItem) ((ArrayList) this.mMenus.get()).get(position);
        }
        return null;
    }

    public long getItemId(int position) {
        return (long) position;
    }
}
