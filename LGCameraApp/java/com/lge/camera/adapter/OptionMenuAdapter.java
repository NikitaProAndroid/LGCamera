package com.lge.camera.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.lge.camera.R;
import com.lge.camera.components.OptionMenu;
import java.util.ArrayList;

public class OptionMenuAdapter extends ArrayAdapter<OptionMenu> {
    private int adapterResId;
    private Context mContext;
    private ArrayList<OptionMenu> mOptionMenuList;

    public class OptionMenuViewHolder {
        ImageView mIcon;
        TextView mMenuTitle;
    }

    public OptionMenuAdapter(Context context, int textViewResId, ArrayList<OptionMenu> optionMenuList) {
        super(context, textViewResId, optionMenuList);
        this.mContext = context;
        this.mOptionMenuList = optionMenuList;
        this.adapterResId = textViewResId;
    }

    public void unbind() {
        this.mContext = null;
        if (this.mOptionMenuList != null) {
            this.mOptionMenuList.clear();
            this.mOptionMenuList = null;
        }
    }

    public View getView(int index, View view, ViewGroup viewgroup) {
        if (this.mContext == null) {
            return null;
        }
        OptionMenuViewHolder holder;
        View itemView = view;
        if (itemView == null) {
            itemView = LayoutInflater.from(this.mContext).inflate(this.adapterResId, null);
            holder = new OptionMenuViewHolder();
            holder.mIcon = (ImageView) itemView.findViewById(R.id.option_item_icon);
            holder.mMenuTitle = (TextView) itemView.findViewById(R.id.option_item_text);
            itemView.setTag(holder);
        } else {
            holder = (OptionMenuViewHolder) itemView.getTag();
        }
        OptionMenu menu = (OptionMenu) this.mOptionMenuList.get(index);
        holder.mIcon.setImageDrawable(menu.getMenuIcon());
        holder.mMenuTitle.setText(menu.getTitle());
        holder.mMenuTitle.setTypeface(Typeface.DEFAULT, 1);
        return itemView;
    }
}
