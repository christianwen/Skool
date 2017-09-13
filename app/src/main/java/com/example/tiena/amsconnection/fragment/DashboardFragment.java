package com.example.tiena.amsconnection.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tiena.amsconnection.R;

/**
 * Created by tiena on 22/08/2017.
 */

public class DashboardFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dashboard_fragment, container, false);

    }
}