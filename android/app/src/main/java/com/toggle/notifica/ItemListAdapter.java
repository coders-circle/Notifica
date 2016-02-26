package com.toggle.notifica;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ViewHolder> {

    public static class Item {
        String shortName = null;
        String title = null;
        Bitmap avatar = null;
        String subTitle = null;
        int color = 0xFFFFFFFF;
        public String details = null;

        public boolean selected = false;
    }

    private List<Item> mItems;

    public ItemListAdapter(List<Item> items) {
        mItems = items;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.layout_profile_item, parent, false
        ));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
            Item item = mItems.get(position );
            Utilities.fillProfileView(holder.view, item.color, item.avatar,
                    null, item.title, item.subTitle, item.details, item.shortName);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }
    }
}
