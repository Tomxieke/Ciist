package com.ciist.app;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public class OnlineHuifuFragment extends Fragment {
    private Context mcontext;

    private static final String TAG_PARMA = "param";
    private String mParam;



    public static OnlineHuifuFragment newInstance(String title) {

        Bundle args = new Bundle();
        args.putString(TAG_PARMA, title);
        OnlineHuifuFragment docfragment = new OnlineHuifuFragment();
        docfragment.setArguments(args);
        return docfragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam = getArguments().getString(TAG_PARMA);
        }
        mcontext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_online_huifu, container, false);
        return view;
    }


}
