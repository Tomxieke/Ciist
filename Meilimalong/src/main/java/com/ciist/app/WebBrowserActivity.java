package com.ciist.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

public class WebBrowserActivity extends AppCompatActivity {
    String WhereUrl = "";
    private ProgressBar waiting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_browser);
        waiting = (ProgressBar) findViewById(R.id.waitloading);
        final WebView webview = (WebView) findViewById(R.id.webBrowserView);
        webview.getSettings().setJavaScriptEnabled(true);//设置支持脚本
        webview.getSettings().setBuiltInZoomControls(true);// 设置支持缩放
        webview.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);// 屏幕自适应网页,如果没有这个，在低分辨率的手机上显示可能会异常
        Intent i = getIntent();
        WhereUrl = i.getStringExtra("URL");
//        Toast.makeText(this, WhereUrl, Toast.LENGTH_SHORT).show();


        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if(waiting!=null){
                    waiting.setVisibility(View.GONE);
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if(waiting!=null){
                    waiting.setVisibility(View.GONE);
                }
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                if(waiting!=null){
                    waiting.setVisibility(View.GONE);
                }
            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) { //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
                view.loadUrl(url);
                return true;
            }
        });

        webview.loadUrl(WhereUrl);

    }

}
