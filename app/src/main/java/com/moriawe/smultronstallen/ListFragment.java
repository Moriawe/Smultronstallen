package com.moriawe.smultronstallen;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

//create sortlist-branch
public class ListFragment extends Fragment {

    private View view;
    private MenuViewModel viewModel;
    TextView text;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_list, container, false);
        text = view.findViewById(R.id.testTextList);
        viewModel = new ViewModelProvider(getActivity()).get(MenuViewModel.class);

        return view;
    }

}