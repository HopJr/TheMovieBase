package com.example.blago.themoviedb.Adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> fragmentList = new ArrayList<>();
    private final List<String> fragmentTittle = new ArrayList<>();


    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public List<String> getFragmentTittle() {
        return fragmentTittle;
    }

    @Override
    public Fragment getItem(int i) {
        return fragmentList.get(i);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    public void addFragment(Fragment fragment, String tittle) {

        fragmentList.add(fragment);
        fragmentTittle.add(tittle);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTittle.get(position);
    }

}