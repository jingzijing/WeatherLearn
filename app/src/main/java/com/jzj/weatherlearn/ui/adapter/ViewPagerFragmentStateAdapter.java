package com.jzj.weatherlearn.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.jzj.weatherlearn.ui.WeatherFragment;

import java.util.List;

public class ViewPagerFragmentStateAdapter extends FragmentStateAdapter {

    private List<WeatherFragment> mFragmentLists;

    public ViewPagerFragmentStateAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, List<WeatherFragment> mFragmentLists) {
        super(fragmentManager, lifecycle);
        this.mFragmentLists = mFragmentLists;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return mFragmentLists.get(position);
    }

    @Override
    public int getItemCount() {
        return mFragmentLists.size();
    }
}
