package com.lipi.notifica;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by fhx on 1/1/16.
 */
public class RoutineDayFragment extends Fragment{
    private RecyclerView.Adapter mAdapter;
    private int mDay;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_routine_day, container, false);
        RecyclerView recyclerView;
        RecyclerView.LayoutManager layoutManager;
        recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view_periods);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(rootView.getContext());
        recyclerView.setLayoutManager(layoutManager);
        Bundle args = getArguments();
        mDay = args.getInt("day");
        mAdapter = new PeriodAdapter(((RoutineFragment)getParentFragment()).routine.get(mDay));
        recyclerView.setAdapter(mAdapter);
        return rootView;
    }
}
