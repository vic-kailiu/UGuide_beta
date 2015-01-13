package com.kai.uGuide.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.kai.uGuide.ui.fragment.SuperAwesomeCardFragment;

public class HomePagerAdapter extends FragmentPagerAdapter {

    private final String[] TITLES = {"Attractions", "Tours", "Food", "Hotels", "Events"};
    private SuperAwesomeCardFragment[] items = new SuperAwesomeCardFragment[10];
    private SuperAwesomeCardFragment item;

    public HomePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }

    @Override
    public Fragment getItem(int position) {
        items[position] = SuperAwesomeCardFragment.newInstance(position);
        return items[position];
        //return getPager(position);
    }

    public SuperAwesomeCardFragment getPager(int position) {
//        if (items[position] == null) {
//            items[position] = SuperAwesomeCardFragment.newInstance(position);
//        }
//
        return items[position];
        //return SuperAwesomeCardFragment.newInstance(position);
    }
}