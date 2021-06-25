package com.md.musculardistrophy.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.md.musculardistrophy.ui.profile.savedPost;
import com.md.musculardistrophy.ui.profile.userPost;

public class TabAdapter  extends FragmentPagerAdapter {

    int totalTabs;

   public TabAdapter(@NonNull FragmentManager fm, int behavior, int tabCount) {
        super(fm,behavior);
        this.totalTabs = tabCount;
    }


    @NonNull
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new userPost();
            case 1:
                return new savedPost();
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return totalTabs;
    }

}