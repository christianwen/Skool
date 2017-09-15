package com.example.tiena.amsconnection.viewholder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.tiena.amsconnection.R;
import com.example.tiena.amsconnection.item.ItemMore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Welcome on 8/27/2016.
 */
public class CustomAdapterMoreFragment extends ArrayAdapter<ItemMore> {

    private Context context;
    private int resource;
    private List<ItemMore> arrItem;

    public CustomAdapterMoreFragment(Context context, int resource, ArrayList<ItemMore> arrItem) {
        super(context, resource, arrItem);
        this.context = context;
        this.resource = resource;
        this.arrItem = arrItem;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_item_more, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.itemName = convertView.findViewById(R.id.item_name);

            viewHolder.itemIcon = convertView.findViewById(R.id.item_icon);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ItemMore itemMore = arrItem.get(position);

        viewHolder.itemIcon.setBackground(context.getResources().getDrawable(itemMore.getItemIcon()));
        viewHolder.itemName.setText(itemMore.getItemName());

        return convertView;
    }

    public class ViewHolder {
        TextView itemName;
        ImageView itemIcon;

    }
}