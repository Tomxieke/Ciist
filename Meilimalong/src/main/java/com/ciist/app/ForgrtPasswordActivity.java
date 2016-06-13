package com.ciist.app;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ForgrtPasswordActivity extends AppCompatActivity {

    private Context mcontext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgrt_password);
        mcontext = getApplicationContext();
    }
}
