package com.pingu.driverapp.ui.home.task;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.pingu.driverapp.ui.home.task.all_tasks.PickupAllTasksFragment;
import com.pingu.driverapp.ui.home.task.available_tasks.PickupAvailableTasksFragment;
import com.pingu.driverapp.ui.home.task.my_tasks.PickupMyTasksFragment;

import java.util.ArrayList;
import java.util.List;

public class PickupListTabPagerAdapter extends FragmentStatePagerAdapter {
    private String[] tabTitles;
    private List<Fragment> fragmentList;

    public PickupListTabPagerAdapter(FragmentManager fm, String[] tabTitles) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.tabTitles = tabTitles;

        fragmentList = new ArrayList<>();
        fragmentList.add(PickupAllTasksFragment.newInstance());
        fragmentList.add(PickupAvailableTasksFragment.newInstance());
        fragmentList.add(PickupMyTasksFragment.newInstance());
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}

