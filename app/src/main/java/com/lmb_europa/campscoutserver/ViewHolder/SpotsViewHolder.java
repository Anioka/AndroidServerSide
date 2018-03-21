package com.lmb_europa.campscoutserver.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lmb_europa.campscoutserver.Common.Common;
import com.lmb_europa.campscoutserver.Interface.ItemClickListener;
import com.lmb_europa.campscoutserver.R;

/**
 * Created by AleksandraPC on 12-Mar-18.
 */

public class  SpotsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {

    public TextView txtSpotName;
    public ImageView imageView;

    private ItemClickListener itemClickListener;

    public SpotsViewHolder(View itemView) {
        super(itemView);

        txtSpotName = (TextView) itemView.findViewById(R.id.spot_name);
        imageView = (ImageView) itemView.findViewById(R.id.menu_image);

        itemView.setOnCreateContextMenuListener(this);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {

        itemClickListener.onClick(view, getAdapterPosition(), false);

    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Select option");

        contextMenu.add(0,0,getAdapterPosition(), Common.UPDATE);
        contextMenu.add(0,1,getAdapterPosition(), Common.DELETE);
    }
}
