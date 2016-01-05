package com.lge.camera.postview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.lge.camera.R;
import java.util.List;

public class PostviewMenuAdapter extends ArrayAdapter<PostviewMenu> {
    private Context mContext;
    private List<PostviewMenu> mPostviewMenu;
    private final int resId;

    public class PostviewMenuViewHolder {
        ImageView mImage;
        TextView menuExtendName;
        TextView menuName;
    }

    public PostviewMenuAdapter(Context context, int textViewResId, List<PostviewMenu> postviewMenuList) {
        super(context, textViewResId, postviewMenuList);
        this.mContext = context;
        this.resId = textViewResId;
        this.mPostviewMenu = postviewMenuList;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        PostviewMenuViewHolder holder;
        View itemView = convertView;
        if (itemView == null) {
            itemView = ((LayoutInflater) this.mContext.getSystemService("layout_inflater")).inflate(this.resId, null);
            holder = new PostviewMenuViewHolder();
            holder.mImage = (ImageView) itemView.findViewById(R.id.dialog_image);
            holder.menuName = (TextView) itemView.findViewById(R.id.dialog_name);
            holder.menuExtendName = (TextView) itemView.findViewById(R.id.dialog_name2);
            itemView.setTag(holder);
        } else {
            holder = (PostviewMenuViewHolder) itemView.getTag();
        }
        holder.mImage.setImageDrawable(((PostviewMenu) this.mPostviewMenu.get(position)).getIcon());
        holder.menuName.setText(((PostviewMenu) this.mPostviewMenu.get(position)).getLabelImage());
        if (((PostviewMenu) this.mPostviewMenu.get(position)).getLabelApp() != null) {
            holder.menuExtendName.setText(((PostviewMenu) this.mPostviewMenu.get(position)).getLabelApp());
            holder.menuExtendName.setVisibility(0);
        } else {
            holder.menuExtendName.setVisibility(8);
        }
        return itemView;
    }

    public void unbind() {
        this.mContext = null;
        if (this.mPostviewMenu != null) {
            this.mPostviewMenu.clear();
            this.mPostviewMenu = null;
        }
    }
}
