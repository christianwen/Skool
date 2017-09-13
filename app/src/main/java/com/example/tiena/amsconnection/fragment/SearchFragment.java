package com.example.tiena.amsconnection.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.tiena.amsconnection.R;

/**
 * Created by tiena on 22/08/2017.
 */

public class SearchFragment extends Fragment {
    private View layout;
    private ListView lvUser;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        layout= inflater.inflate(R.layout.search_fragment, container, false);
        //lvUser.setAdapter(CustomListAdapter);

        return layout;
    }
}
