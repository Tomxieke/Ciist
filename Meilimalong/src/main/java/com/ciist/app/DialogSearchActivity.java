package com.ciist.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.ciist.app.R;

public class DialogSearchActivity extends AppCompatActivity {

    private String[] names = new String[] {};

    private ListView mlist;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adapter);
        Intent intent = getIntent();
        names = intent.getStringArrayExtra("names");
        mlist = (ListView) findViewById(R.id.array_list);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, names);
        mlist.setAdapter(adapter);

        mlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = (String) parent.getItemAtPosition(position);

        //        Toast.makeText(DialogSearchActivity.this,"  name   " + name,Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("name",name);
                setResult(0,intent);
                finish();
            }
        });

        editText = (EditText) findViewById(R.id.edit_adapter);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onBackPressed() {
       //数据是使用Intent返回
        Intent intent = new Intent();
        //把返回数据存入Intent
        intent.putExtra("name" ,"未选择联系人");
        //设置返回数据
        DialogSearchActivity.this.setResult(0, intent);
        //关闭Activity,上边三行是要传回activityA中的信息，可以根据需要填写 也可不写
        DialogSearchActivity.this.finish();
    }
}
