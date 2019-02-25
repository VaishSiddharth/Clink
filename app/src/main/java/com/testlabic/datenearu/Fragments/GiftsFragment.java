package com.testlabic.datenearu.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.testlabic.datenearu.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class GiftsFragment extends Fragment {
    
    public GiftsFragment() {
        // Required empty public constructor
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gifts, container, false);
    }
    
}
