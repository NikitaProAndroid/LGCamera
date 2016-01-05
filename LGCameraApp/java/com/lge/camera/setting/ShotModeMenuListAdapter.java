package com.lge.camera.setting;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.LevelListDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.lge.camera.R;
import com.lge.camera.components.RotateLayout;
import com.lge.camera.components.RotateView;
import com.lge.camera.controller.ShotModeMenuController.ModeItem;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class ShotModeMenuListAdapter extends BaseAdapter {
    public static final boolean CIRCULAR = true;
    private static final int HALF_MAX_VALUE = 1073741823;
    private final int MIDDLE_VALUE;
    private WeakReference<Context> mContext;
    private int mDegree;
    private WeakReference<ArrayList<ModeItem>> mMenus;
    private int mSelectedAmount;
    private int mSelectedItemPos;

    public class ModeMenuViewHolder {
        ImageView mImage;
        RelativeLayout mLayout;
        TextView mNameBottom;
        RotateLayout mRotate;
    }

    public ShotModeMenuListAdapter(Context context, ArrayList<ModeItem> menus) {
        this.mDegree = 0;
        this.mSelectedItemPos = -1;
        this.mSelectedAmount = 0;
        this.mContext = new WeakReference(context);
        this.mMenus = new WeakReference(menus);
        this.MIDDLE_VALUE = HALF_MAX_VALUE - (HALF_MAX_VALUE % menus.size());
    }

    public int getMiddleValue() {
        return this.MIDDLE_VALUE;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ModeMenuViewHolder holder;
        View modeItemView = convertView;
        if (modeItemView == null) {
            modeItemView = ((LayoutInflater) ((Context) this.mContext.get()).getSystemService("layout_inflater")).inflate(R.layout.shot_mode_menu_list_item_view, null);
            if (modeItemView == null) {
                CamLog.w(FaceDetector.TAG, "SelectMenuGridAdapter error. view is null.");
                return null;
            }
            holder = new ModeMenuViewHolder();
            holder.mLayout = (RelativeLayout) modeItemView.findViewById(R.id.shot_mode_menu_list_item_layout);
            holder.mRotate = (RotateLayout) modeItemView.findViewById(R.id.shot_mode_menu_list_item_content_rotate);
            holder.mImage = (ImageView) modeItemView.findViewById(R.id.shot_mode_menu_item_icon);
            holder.mNameBottom = (TextView) modeItemView.findViewById(R.id.shot_mode_menu_item_text_bottom);
            modeItemView.setTag(holder);
        } else {
            holder = (ModeMenuViewHolder) modeItemView.getTag();
        }
        holder.mRotate.rotateLayout(getListItemDegree());
        holder.mNameBottom.setTypeface(Typeface.DEFAULT, 1);
        ModeItem ci = getItem(position);
        if (ci != null) {
            String title = ci.getTitle();
            holder.mNameBottom.setText(title);
            modeItemView.setContentDescription(title);
            LevelListDrawable imageDrawable = ci.getImageDrawable();
            if (!(ci.getImageResourceId() == 0 || imageDrawable == null)) {
                holder.mImage.setImageResource(ci.getImageResourceId());
                holder.mImage.setImageLevel(0);
            }
        } else {
            holder.mNameBottom.setText(null);
            holder.mImage.setImageDrawable(null);
        }
        if (isSelectedItem(position)) {
            holder.mNameBottom.setSelected(CIRCULAR);
            holder.mLayout.setBackgroundResource(R.drawable.camera_mode_menu_list_bg_pressed);
        } else {
            holder.mNameBottom.setSelected(false);
            holder.mLayout.setBackgroundResource(R.drawable.selector_mode_menu_list_bg);
        }
        return modeItemView;
    }

    public int getListItemDegree() {
        return this.mDegree;
    }

    public void setListItemDegree(int degree) {
        this.mDegree = degree;
    }

    public void setSelectedItem(int position) {
        if (((ArrayList) this.mMenus.get()).size() <= 0) {
            this.mSelectedItemPos = -1;
            this.mSelectedAmount = 0;
            return;
        }
        this.mSelectedAmount = position / ((ArrayList) this.mMenus.get()).size();
        this.mSelectedItemPos = position % ((ArrayList) this.mMenus.get()).size();
    }

    public int getSelectedItem() {
        return (((ArrayList) this.mMenus.get()).size() * this.mSelectedAmount) + this.mSelectedItemPos;
    }

    public boolean isSelectedItem(int position) {
        if (((ArrayList) this.mMenus.get()).size() <= 0) {
            return false;
        }
        return position % ((ArrayList) this.mMenus.get()).size() == this.mSelectedItemPos ? CIRCULAR : false;
    }

    public int getCount() {
        return RotateView.PIVOT_CENTER;
    }

    public ModeItem getItem(int position) {
        if (((ArrayList) this.mMenus.get()).size() <= 0) {
            return null;
        }
        return (ModeItem) ((ArrayList) this.mMenus.get()).get(position % ((ArrayList) this.mMenus.get()).size());
    }

    public long getItemId(int position) {
        return (long) position;
    }
}
