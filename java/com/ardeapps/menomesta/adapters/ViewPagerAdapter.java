package com.ardeapps.menomesta.adapters;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.R;
import com.ardeapps.menomesta.fragments.BarsFragment;
import com.ardeapps.menomesta.fragments.ChatFragment;
import com.ardeapps.menomesta.fragments.EventsFragment;

import java.util.ArrayList;

/**
 * Created by Arttu on 24.9.2016.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private EventsFragment eventsFragment;
    private BarsFragment barsFragment;
    private ChatFragment chatFragment;
    private ArrayList<String> fragmentNames = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager supportFragmentManager, ArrayList<Fragment> fragments) {
        super(supportFragmentManager);

        eventsFragment = (EventsFragment) fragments.get(0);
        barsFragment = (BarsFragment) fragments.get(1);
        chatFragment = (ChatFragment) fragments.get(2);

        fragmentNames.clear();
        fragmentNames.add(AppRes.getContext().getString(R.string.events));
        fragmentNames.add(AppRes.getContext().getString(R.string.bars));
        fragmentNames.add(AppRes.getContext().getString(R.string.chat));
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return eventsFragment;
            case 1:
                return barsFragment;
            case 2:
                return chatFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return fragmentNames.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentNames.get(position);
    }

    public void updateBarList() {
        barsFragment.refreshData();
        barsFragment.update();
    }

    public void updateChatList() {
        chatFragment.refreshData();
        chatFragment.update();
    }

    public void updateEvents() {
        eventsFragment.refreshData();
        eventsFragment.update();
    }
}
