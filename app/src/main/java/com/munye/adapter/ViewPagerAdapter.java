package com.munye.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.munye.user.R;
import com.munye.fragment.ActiveJobFragment;
import com.munye.fragment.PreviousJobFragment;

/**
 * Created by Akash on 1/31/2017.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private Context context;

    public ViewPagerAdapter(FragmentManager fm , Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){

            case 0 :
                return ActiveJobFragment.newInstance();

            case 1:
                return PreviousJobFragment.newInstanse();

        }

        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(position == 0){
            return context.getString(R.string.active);
        }else {
            return context.getString(R.string.previous);
        }
    }
}
