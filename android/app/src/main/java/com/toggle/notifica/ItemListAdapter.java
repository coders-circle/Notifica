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

    public interface Listener {
        void onSelect(int position, Item item);
    }

    private List<Item> mItems;
    private Listener mListener;

    public ItemListAdapter(List<Item> items) {
        mItems = items;
    }

    public ItemListAdapter(List<Item> items, Listener listener) {
        mItems = items;
        mListener = listener;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.layout_profile_item_light, parent, false
        ));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Item item = mItems.get(position );
        Utilities.fillProfileView(holder.view, item.color, item.avatar,
                null, item.title, item.subTitle, item.details, item.shortName);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.onSelect(position, item);
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }
    }
}
