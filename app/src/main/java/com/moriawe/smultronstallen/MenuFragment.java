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
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MenuFragment extends Fragment implements View.OnClickListener{

    private View view;
    private Button showHideBtn;
    private Button personalListBtn;
    private Button privateListBtn;
    private Button friendsListBtn;
    private Button completeListBtn;
    MenuViewModel viewModel;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MenuFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MenuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MenuFragment newInstance(String param1, String param2) {
        MenuFragment fragment = new MenuFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_menu, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(MenuViewModel.class);

        completeListBtn = (Button) view.findViewById(R.id.completeListBtn);
        completeListBtn.setOnClickListener(this);

        friendsListBtn = (Button) view.findViewById(R.id.friendsListBtn);
        friendsListBtn.setOnClickListener(this);

        personalListBtn = (Button) view.findViewById(R.id.personalListBtn);
        personalListBtn.setOnClickListener(this);

        showHideBtn = (Button) view.findViewById(R.id.showHideBtnn);
        showHideBtn.setOnClickListener(this);


        // Inflate the layout for this fragment
        return view;
//        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    //Set data in listFragment and MapActivity
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.completeListBtn:
                viewModel.setData("Completebtn set");
                break;
            case R.id.friendsListBtn:
                viewModel.setData("friendslistbtn set");
                break;
            case R.id.personalListBtn:
                viewModel.setData("personlistbtn set");
                break;
            case R.id.showHideBtnn:
                Toast.makeText(getActivity(), "Show/hide list", Toast.LENGTH_SHORT).show();
                ((MapActivity) getActivity()).showHideList();
                break;
            default:
                break;
        }
    }


}