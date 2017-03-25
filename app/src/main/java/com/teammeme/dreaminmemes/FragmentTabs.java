package com.teammeme.dreaminmemes;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by dillo on 3/25/2017.
 */

public class FragmentTabs extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tabs_layout, viewGroup, false);
        return view;
    }
}
