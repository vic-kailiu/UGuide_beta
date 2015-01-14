package com.kai.uGuide.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.kai.uGuide.ui.fragment.SuperAwesomeCardFragment;

import java.util.ArrayList;

public class HomePagerAdapter extends FragmentPagerAdapter {

    private final String[] TITLES = {"Attractions", "Tours", "Food", "Hotels", "Events"};
    private ArrayList<SuperAwesomeCardFragment> items = new ArrayList<SuperAwesomeCardFragment>();

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
        items.add(position, SuperAwesomeCardFragment.newInstance(position) );
        return items.get(position);
        //return getPager(position);
    }
//
//    @Override
//    public int getItemPosition(Object object) {
//        return POSITION_NONE;
//    }

    public SuperAwesomeCardFragment getPager(int position) {
        return items.get(position);
        //return SuperAwesomeCardFragment.newInstance(position);
    }
}