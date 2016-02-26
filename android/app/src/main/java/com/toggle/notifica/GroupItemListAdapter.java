package com.toggle.notifica;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class GroupItemListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private HashMap<String, List<ItemListAdapter.Item>> mItems;
    private List<String> mGroups;
    private GroupItemListFragment mParent;

    public GroupItemListAdapter(GroupItemListFragment parent) {
        mParent = parent;
        mItems = parent.getItems();
        mGroups = parent.getGroups();
    }

    @Override
    public int getItemCount() {
        int electivesSize = 0;
        for (String group: mGroups)
            electivesSize += mItems.get(group).size()+1;
        return electivesSize;
    }

    @Override
    public int getItemViewType(int position) {
        int origin = 0;
        for (String group: mGroups) {
            if (position == origin)
                return 0;
            else if (position < origin)
                break;

            origin += mItems.get(group).size()+1;
        }

        return 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0)
            return  new HeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.layout_item_header, parent, false
            ));
        else
            return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.layout_profile_item, parent, false
            ));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        int origin = 0;
        for (final String group: mGroups) {
            List<ItemListAdapter.Item> items = mItems.get(group);

            // group header
            if (origin == position) {
                HeaderViewHolder holder = (HeaderViewHolder) viewHolder;
                holder.header.setText(group);
            }

            // item
            else if (position - origin-1 < items.size()) {
                final ItemListAdapter.Item item = items.get(position - origin - 1);
                ItemViewHolder holder = (ItemViewHolder) viewHolder;
                Utilities.fillProfileView(holder.view, item.color, item.avatar,
                        null, item.title, item.subTitle, item.details, item.shortName);
                holder.view.setSelected(item.selected);

                if (!item.selected)
                    holder.view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mParent.selectItem(group, item, new Listener() {
                                @Override
                                public void refresh() {
                                    mItems = mParent.getItems();
                                    mGroups = mParent.getGroups();
                                    notifyDataSetChanged();
                                }
                            });
                        }
                    });
            }

            origin += items.size() + 1;
        }
    }

    public interface Listener {
        void refresh();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        protected View view;

        public ItemViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        protected TextView header;
        public HeaderViewHolder(View itemView) {
            super(itemView);

            header = (TextView) itemView.findViewById(R.id.header);
        }
    }
}
