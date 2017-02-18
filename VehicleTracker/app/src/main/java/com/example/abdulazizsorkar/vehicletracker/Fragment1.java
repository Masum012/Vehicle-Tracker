package com.example.abdulazizsorkar.vehicletracker;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Abdul Aziz Sorkar on 1/8/2016.
 */
public class Fragment1 extends Fragment {
    public Fragment1(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment1,container,false);

        return rootView;
    }

}
