package com.lipi.notifica;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.design.widget.TabLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fhx on 1/1/16.
 */
public class RoutineFragment extends Fragment {
    static final int NUM_TABS = 7;
    static final int NUM_DAYS = 7;
    static final String[] tabTitles = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    private RoutineDayFragment mCurrentFragment;
    SlidingTabLayout tabs;
    public ArrayList<ArrayList<Period>> routine;

    public RoutineFragment(){
        routine = new ArrayList<>();
        for( int i = 0; i < NUM_DAYS; i++){
            routine.add(new ArrayList<Period>());
        }
        Period testPeriod = new Period();
        routine.get(0).add(testPeriod);
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_routine, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DaysTabsPagerAdapter adapter = new DaysTabsPagerAdapter(getChildFragmentManager());
        ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.pager_routine);
        viewPager.setAdapter(adapter);

        tabs = (SlidingTabLayout) getActivity().findViewById(R.id.routine_tab);
        tabs.setDistributeEvenly(true);

        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        tabs.setViewPager(viewPager);

        //TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.tabs_days);
        //tabLayout.setupWithViewPager(viewPager);
        //viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));


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
