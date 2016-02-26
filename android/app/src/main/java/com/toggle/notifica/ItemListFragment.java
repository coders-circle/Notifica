package com.toggle.notifica;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public abstract class ItemListFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_item_list,container,false);

        RecyclerView recyclerView = (RecyclerView)v.findViewById(R.id.itemsRecyclerView);
        ItemListAdapter adapter = new ItemListAdapter(getItems());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return v;
    }

    public abstract List<ItemListAdapter.Item> getItems();
}
