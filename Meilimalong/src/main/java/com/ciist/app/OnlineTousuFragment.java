package com.ciist.app;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ciist.customview.xlistview.CiistTitleView;
import com.ciist.customview.xlistview.ScrollListView;
import com.ciist.entities.GISLocation;
import com.ciist.toolkits.DocumentGerenAdapter;
import com.hw.ciist.util.Hutils;

import java.util.ArrayList;


public class OnlineTousuFragment extends Fragment {

    private Context mcontext;

    private static final String TAG_PARMA = "param";
    private String mParam;

    private static final int MSG_SUCCESS = 101;

    private Button online_com_tijiao;
    private EditText name;
    private EditText phone_number;
    private EditText neirong;
    private TextView help;



    public static OnlineTousuFragment newInstance(String title) {

        Bundle args = new Bundle();
        args.putString(TAG_PARMA, title);
        OnlineTousuFragment docfragment = new OnlineTousuFragment();
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
       View view= inflater.inflate(R.layout.fragment_online_tousu, container, false);
        name=(EditText)view.findViewById(R.id.online_tousu_name_edi);
        neirong=(EditText)view.findViewById(R.id.online_tousu_neirong_edi);
        phone_number=(EditText)view.findViewById(R.id.online_tousu_phonenumber_edi);
        help=(TextView)view.findViewById(R.id.online_tousu_help_tv);
        online_com_tijiao=(Button)view.findViewById(R.id.online_tousu_button);

        online_com_tijiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name.length()==0&&neirong.length()==0&&phone_number.length()==0){
                    Toast.makeText(mcontext,"请输入信息",Toast.LENGTH_LONG).show();
                }else if(name.length()!=0&&neirong.length()!=0&&phone_number.length()!=11){


                }else if(name.length()!=0&&neirong.length()!=0&&phone_number.length()==11){
                    Toast.makeText(mcontext,"提交成功",Toast.LENGTH_LONG).show();
                }else if(name.length()==0&&neirong.length()!=0&&phone_number.length()==11){
                    Toast.makeText(mcontext,"请输入姓名",Toast.LENGTH_LONG).show();
                }else if(name.length()!=0&&neirong.length()==0&&phone_number.length()==11){
                    Toast.makeText(mcontext,"请输入投诉信息",Toast.LENGTH_LONG).show();
                }
            }
        });



        return view;
    }



}
