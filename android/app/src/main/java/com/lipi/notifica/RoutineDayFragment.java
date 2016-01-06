package com.lipi.notifica;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
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
        recyclerView.addItemDecoration(new PeriodDivider(rootView.getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setClickable(true);
        layoutManager = new LinearLayoutManager(rootView.getContext());
        recyclerView.setLayoutManager(layoutManager);
        Bundle args = getArguments();
        mDay = args.getInt("day");
        mAdapter = new PeriodAdapter(getActivity(), ((RoutineFragment)getParentFragment()).routine.get(mDay));
        recyclerView.setAdapter(mAdapter);
        return rootView;
    }

    public class PeriodDivider extends RecyclerView.ItemDecoration{
        private Drawable mDivider;
        public PeriodDivider(Context context){
            mDivider = context.getResources().getDrawable(R.drawable.divider_period);

        }
        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft()
                    + parent.getChildAt(0).findViewById(R.id.sub_shortname).getRight()
                    + parent.getChildAt(0).findViewById(R.id.subject).getLeft();
            int right = parent.getWidth() - parent.getPaddingRight() - 10;
            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount-1; i++) {
                View child = parent.getChildAt(i);
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }
}
