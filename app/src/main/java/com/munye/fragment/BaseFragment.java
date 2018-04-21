package com.munye.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.munye.parse.DataParser;
import com.munye.utils.PreferenceHelper;

/**
 * Created by Akash on 2/1/2017.
 */

public class BaseFragment extends Fragment {

    protected PreferenceHelper preferenceHelper;
    protected DataParser dataParser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceHelper = PreferenceHelper.getInstance(getActivity());
        dataParser = new DataParser(getActivity());

    }
}
