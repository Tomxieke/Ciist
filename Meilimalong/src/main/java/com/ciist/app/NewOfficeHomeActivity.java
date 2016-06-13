package com.ciist.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import java.util.Timer;
import java.util.TimerTask;

public class NewOfficeHomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private String mUsername = "";
    private String mIdentify = "";
    private String mSelfids = "";
    private String mDuties = "";
    private static Boolean isExit = false;
    private android.support.v4.app.FragmentManager mManager;
    private DrawerLayout mDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_office_home_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.newOfficeHomeDrawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();
        mDrawer.openDrawer(GravityCompat.START);
        NavigationView navigationView = (NavigationView) findViewById(R.id.newOfficeHomeNav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Intent intent = this.getIntent();
        mIdentify = intent.getStringExtra("identify");
        mUsername = intent.getStringExtra("username");
        mSelfids = intent.getStringExtra("selfids");
        mDuties = intent.getStringExtra("Duties");

        View _headerView = navigationView.inflateHeaderView(R.layout.new_office_home_nav_header);

        if (_headerView != null) {
            TextView _username = (TextView) _headerView.findViewById(R.id.newOfficeHomeHeaderTextView1);
            TextView _duties = (TextView) _headerView.findViewById(R.id.newOfficeHomeHeaderTextView2);
            if (_username != null && _duties != null) {
                _username.setText(mUsername);
                _duties.setText(mDuties);
            }
        }

        if (mManager == null) {
            mManager = getSupportFragmentManager();
        }
        NewOfficeFragmentJiaoban f1 = new NewOfficeFragmentJiaoban();
        Bundle data = new Bundle();


        data.putString("identify", mIdentify);
        data.putString("username", mUsername);
        data.putString("selfids", mSelfids);
        data.putString("title", "");
        data.putString("action", "jb");
        data.putString("Duties", mDuties);
        f1.setArguments(data);//通过Bundle向Activity中传递值
        mManager.beginTransaction()
                .add(R.id.newOfficeHomeFrameLayout, f1)
                .commit();

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.newOfficeHomeDrawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.new_office_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    /**
     * 跳转选择的年份的fragment
     * @param kode
     */
    private void nextFragment(String kode){
        NewOfficeFragmentZhongdianxiangmuLev1 f1 = new NewOfficeFragmentZhongdianxiangmuLev1();
        Bundle data = new Bundle();
        data.putString("identify", mIdentify);
        data.putString("username", mUsername);
        data.putString("selfids", mSelfids);
        data.putString("title", "");
        data.putString("action", "jb");
        data.putString("subjectname", "重点项目");
        data.putString("subjectcode", kode);
        data.putString("Duties", mDuties);
        f1.setArguments(data);//通过Bundle向Activity中传递值
        mManager.beginTransaction()
                .replace(R.id.newOfficeHomeFrameLayout, f1)
                .commit();
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.new_office_menu_jiaoban) {
            NewOfficeFragmentJiaoban f1 = new NewOfficeFragmentJiaoban();
            Bundle data = new Bundle();
            data.putString("identify", mIdentify);
            data.putString("username", mUsername);
            data.putString("selfids", mSelfids);
            data.putString("title", "");
            data.putString("action", "jb");
            data.putString("Duties", mDuties);
            f1.setArguments(data);//通过Bundle向Activity中传递值
            mManager.beginTransaction()
                    .replace(R.id.newOfficeHomeFrameLayout, f1)
                    .commit();
        } else if (id == R.id.new_office_menu_chengban) {
            NewOfficeFragmentChenban f1 = new NewOfficeFragmentChenban();
            Bundle data = new Bundle();
            data.putString("identify", mIdentify);
            data.putString("username", mUsername);
            data.putString("selfids", mSelfids);
            data.putString("title", "");
            data.putString("action", "zb");
            data.putString("Duties", mDuties);
            f1.setArguments(data);//通过Bundle向Activity中传递值
            mManager.beginTransaction()
                    .replace(R.id.newOfficeHomeFrameLayout, f1)
                    .commit();
        } else if (id == R.id.new_office_menu_zhongdianxiangmu) {
            NewOfficeFragmentZhongdianxiangmuLev1 f1 = new NewOfficeFragmentZhongdianxiangmuLev1();
            Bundle data = new Bundle();
            data.putString("identify", mIdentify);
            data.putString("username", mUsername);
            data.putString("selfids", mSelfids);
            data.putString("title", "");
            data.putString("action", "jb");
            data.putString("subjectname", "重点项目");
            data.putString("subjectcode", "D0C02CD2-BBC4-4DE8-917D-E68E0B434337");
            data.putString("Duties", mDuties);
            f1.setArguments(data);//通过Bundle向Activity中传递值
            mManager.beginTransaction()
                    .replace(R.id.newOfficeHomeFrameLayout, f1)
                    .commit();
        } else if (id == R.id.new_office_menu_zhongdiangongzuo) {
            NewOfficeFragmentNormalList f1 = new NewOfficeFragmentNormalList();
            Bundle data = new Bundle();
            data.putString("identify", mIdentify);
            data.putString("username", mUsername);
            data.putString("selfids", mSelfids);
            data.putString("title", "");
            data.putString("action", "jb");
            data.putString("subjectname", "重点工作");
            data.putString("subjectcode", "B613057F-01F5-40FF-8FA1-45F42923BCCB");
            data.putString("Duties", mDuties);
            f1.setArguments(data);//通过Bundle向Activity中传递值
            mManager.beginTransaction()
                    .replace(R.id.newOfficeHomeFrameLayout, f1)
                    .commit();
        } else if (id == R.id.new_office_menu_zhaoshangyinzi) {



            NewOfficeZhaoFgm f2 = new NewOfficeZhaoFgm();
            Bundle data = new Bundle();
            data.putString("identify", mIdentify);
            data.putString("username", mUsername);
            data.putString("selfids", mSelfids);
            data.putString("title", "");
            data.putString("action", "jb");
//            data.putString("subjectname", "拟签约项目");
//            data.putString("subjectcode", "5F4487B7-13FF-4F55-B0F3-5E8AE57AC031");
            data.putString("Duties", mDuties);
            f2.setArguments(data);//通过Bundle向Activity中传递值
            mManager.beginTransaction()
                    .replace(R.id.newOfficeHomeFrameLayout, f2)
                    .commit();;


//            Intent tmpIntent = new Intent(NewOfficeHomeActivity.this, NewOfficeZhaoActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putString("identify", mIdentify);
//            bundle.putString("username", mUsername);
//            bundle.putString("selfids", mSelfids);
//           bundle.putString("title", "拟签约项目");
//            bundle.putString("action", "nqyxm");
//           bundle.putString("subjectname", "拟签约项目");
//            bundle.putString("subjectcode", "5F4487B7-13FF-4F55-B0F3-5E8AE57AC031");
//            tmpIntent.putExtras(bundle);
//            startActivity(tmpIntent);
        }

//
//        else if(id == R.id.new_office_menu_niqianyue){
//            Intent tmpIntent = new Intent(NewOfficeHomeActivity.this, IndexOfOACoverStyleActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putString("identify", mIdentify);
//            bundle.putString("username", mUsername);
//            bundle.putString("selfids", mSelfids);
//            bundle.putString("title", "拟签约项目");
//            bundle.putString("action", "nqyxm");
//            bundle.putString("subjectname", "拟签约项目");
//            bundle.putString("subjectcode", "5F4487B7-13FF-4F55-B0F3-5E8AE57AC031");
//            tmpIntent.putExtras(bundle);
//            startActivity(tmpIntent);
//        }
//        else if (id == R.id.new_office_menu_yiqianyue) {
//            Intent tmpIntent = new Intent(NewOfficeHomeActivity.this, IndexOfOACoverStyleActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putString("identify", mIdentify);
//            bundle.putString("username", mUsername);
//            bundle.putString("selfids", mSelfids);
//            bundle.putString("title", "已签约项目");
//            bundle.putString("action", "yqyxm");
//            bundle.putString("subjectname", "已签约项目");
//            bundle.putString("subjectcode", "82679EB8-419C-46D0-99EA-67876486FB23");
//            tmpIntent.putExtras(bundle);
//            startActivity(tmpIntent);
//        } else if (id == R.id.new_office_menu_zhongdiangengjin) {
//            Intent tmpIntent = new Intent(NewOfficeHomeActivity.this, IndexOfOACoverStyleActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putString("identify", mIdentify);
//            bundle.putString("username", mUsername);
//            bundle.putString("selfids", mSelfids);
//            bundle.putString("title", "重点更进项目");
//            bundle.putString("action", "zdgjxm");
//            bundle.putString("subjectname", "重点更进项目");
//            bundle.putString("subjectcode", "557C6885-8D79-40CA-A0DD-E4BE0DD7023B");
//            tmpIntent.putExtras(bundle);
//            startActivity(tmpIntent);
//        } else if (id == R.id.new_office_menu_yitichu) {
//            Intent tmpIntent = new Intent(NewOfficeHomeActivity.this, IndexOfOACoverStyleActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putString("identify", mIdentify);
//            bundle.putString("username", mUsername);
//            bundle.putString("selfids", mSelfids);
//            bundle.putString("title", "已剔除项目");
//            bundle.putString("action", "ytcxm");
//            bundle.putString("subjectname", "已剔除项目");
//            bundle.putString("subjectcode", "081D3059-A8FE-45B3-A2C4-FBD8E683F9CC");
//            tmpIntent.putExtras(bundle);
//            startActivity(tmpIntent);
//        }
//

        else if (id == R.id.new_office_menu_bumenliebiao) {

            NewOfficeFragmentBumenliebiao bumenliebiao = NewOfficeFragmentBumenliebiao
                    .newInstance("3EA6562A-3ADE-4600-8C66-66D168CD8380", mIdentify, "0", mUsername, mSelfids);

            mManager.beginTransaction()
                    .replace(R.id.newOfficeHomeFrameLayout, bumenliebiao)
                    .commit();

        }
//        else if (id == R.id.new_office_menu_shujutongji) {
//            Intent tmpIntent = new Intent(NewOfficeHomeActivity.this, ChannelOfCoverStyleActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putInt("color", getResources().getColor(R.color.lvsetitle));
//            bundle.putString("subjectname2", "数据统计");
//
//
//            bundle.putString("subjectname", "数据统计");
//            bundle.putString("subjectcode", "B77F99A7-45A1-40A7-AC81-50230E042D00");
//            tmpIntent.putExtras(bundle);
//            startActivity(tmpIntent);
//        }
        else if (id == R.id.new_office_menu_shujumalong) {
            DataMaLongFgm f1 = new DataMaLongFgm();
            Bundle data = new Bundle();
            data.putString("identify", mIdentify);
            data.putString("username", mUsername);
            data.putString("selfids", mSelfids);
            data.putString("title", "");
            data.putString("action", "jb");
            data.putString("subjectname", "数据马龙");
            data.putString("subjectcode", "7A0822AB-0E9D-4A1C-AA0F-7E292F140A5F");
            data.putString("Duties", mDuties);
            f1.setArguments(data);//通过Bundle向Activity中传递值
            mManager.beginTransaction()
                    .replace(R.id.newOfficeHomeFrameLayout, f1)
                    .commit();
        } else if (id == R.id.new_office_menu_shuju_core) {
            startActivity(new Intent(NewOfficeHomeActivity.this, DataCoreActivity.class));
            Bundle data = new Bundle();
            data.putString("subjectname", "数据中心");
        }else if (id == R.id.new_office_menu_xiugaimima) {
            Intent _int = new Intent(NewOfficeHomeActivity.this, IndexOfOAChangePwdActivity.class);
            startActivity(_int);

        } else if (id == R.id.new_office_menu_zhuxiaoyonghu) {
            SharedPreferences mySharedPreferences = getSharedPreferences("passport", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = mySharedPreferences.edit();
            editor.putString("identify", "0");
            editor.commit();
            Toast.makeText(getApplicationContext(), "请重新登陆", Toast.LENGTH_SHORT).show();
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.newOfficeHomeDrawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitBy2Click();
        }
        return false;
    }

    private void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
//            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            mDrawer.openDrawer(GravityCompat.START);
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
        } else {
            finish();
        }
    }
}
