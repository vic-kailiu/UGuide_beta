package com.kai.uGuide.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.kai.uGuide.ui.fragment.SuperAwesomeCardFragment;

import java.util.ArrayList;

public class HomePagerAdapter extends FragmentPagerAdapter {

    private String[] TITLES;
    private ArrayList<SuperAwesomeCardFragment> items = new ArrayList<SuperAwesomeCardFragment>();

    public HomePagerAdapter(FragmentManager fm, String[] TITLES) {
        super(fm);
        this.TITLES = TITLES;
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
        if (TITLES == Home.TITLES)
            items.add(position, SuperAwesomeCardFragment.newInstance(position, 0) );
        else
            items.add(position, SuperAwesomeCardFragment.newInstance(position, 1) );
        return items.get(position);
        //return getPager(position);
    }

    public SuperAwesomeCardFragment getPager(int position) {
        return items.get(position);
        //return SuperAwesomeCardFragment.newInstance(position);
    }
}