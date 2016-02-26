package com.toggle.notifica;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class RoutineDayFragment extends Fragment{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_routine_day, container, false);

        RecyclerView recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view_periods);
        recyclerView.addItemDecoration(new PeriodDivider(rootView.getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setClickable(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(rootView.getContext());
        recyclerView.setLayoutManager(layoutManager);

        Bundle args = getArguments();
        int day = args.getInt("day");
        RecyclerView.Adapter adapter = new PeriodAdapter(getActivity(), ((RoutineFragment) getParentFragment()).routine.get(day));
        recyclerView.setAdapter(adapter);
        return rootView;
    }

    public class PeriodDivider extends RecyclerView.ItemDecoration{
        private Drawable mDivider;
        public PeriodDivider(Context context){
            mDivider = ContextCompat.getDrawable(context, R.drawable.divider_period);
        }
        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            if( parent.getChildCount() < 2 ) {
                return;
            }

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
