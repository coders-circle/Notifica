package com.toggle.notifica;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


public class SubjectViewPagerAdapter extends FragmentStatePagerAdapter {

    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public SubjectViewPagerAdapter(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;

    }

    // Returns the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        if(position == 0)
            return new DetailsTabFragment();
        else if(position == 1)
            return new NewsFeedFragment();
        else
            return new ResourcesTabFragment();

    }

    // Return the titles for the Tabs in the Tab Strip
    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    // Returns the number of tabs for the tabs Strip
    @Override
    public int getCount() {
        return NumbOfTabs;
    }
}