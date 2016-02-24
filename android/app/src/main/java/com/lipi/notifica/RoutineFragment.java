package com.lipi.notifica;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lipi.notifica.database.*;
import com.lipi.notifica.database.Period;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class RoutineFragment extends Fragment {
    static final int NUM_TABS = 7;
    static final int NUM_DAYS = 7;
    static final String[] tabTitles = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    private RoutineDayFragment mCurrentFragment;
    SlidingTabLayout tabs;
    public List<List<Period>> routine;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_routine, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Use routine from database
        routine = new ArrayList<>();
        DbHelper helper = new DbHelper(getContext());
        for(int i = 0; i < NUM_DAYS; i++) {
            // Get periods for current day, ordered by start_time
            List<Period> periods = Period.query(Period.class, helper, "day=?", new String[]{i + ""}, null, null, "start_time");

            // Remove electives that user hasn't selected
            for (int j=0; j<periods.size(); ++j) {
                Elective elective = periods.get(j).getSubject(helper).getElective(helper);
                if (elective == null)
                    continue;

                if (!elective.selected) {
                    periods.remove(j);
                    --j;
                }
            }
            routine.add(periods);
        }

        DaysTabsPagerAdapter adapter = new DaysTabsPagerAdapter(getChildFragmentManager());
        ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.pager_routine);
        viewPager.setAdapter(adapter);

        tabs = (SlidingTabLayout) getActivity().findViewById(R.id.routine_tab);
        tabs.setDistributeEvenly(true);

        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return ContextCompat.getColor(getContext(), R.color.tabsScrollColor);
            }
        });

        tabs.setViewPager(viewPager);

        viewPager.setCurrentItem(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1);
    }

    public class DaysTabsPagerAdapter extends FragmentStatePagerAdapter {
        public DaysTabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM_TABS;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            if (position < 0 || position > 6)
                return null;

            Fragment fragment = new RoutineDayFragment();
            Bundle args = new Bundle();
            args.putInt("day", position);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
            //do nothing here! no call to super.restoreState(arg0, arg1);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            if (mCurrentFragment != object) {
                mCurrentFragment = (RoutineDayFragment) object;
            }
            super.setPrimaryItem(container, position, object);
        }
    }
}
