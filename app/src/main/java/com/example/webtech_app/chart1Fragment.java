package com.example.webtech_app;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONException;
import org.json.JSONObject;


public class chart1Fragment extends Fragment {


    private final JSONObject chart1;

    public chart1Fragment(JSONObject chart1){
        this.chart1 = chart1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_chart1, container, false);
        WebView webview1 = view.findViewById(R.id.chart1);
        WebSettings webSettings1 = webview1.getSettings();

        webSettings1.setJavaScriptEnabled(true);

        webSettings1.setBuiltInZoomControls(true);

        webview1.requestFocusFromTouch();

        webview1.loadUrl("file:///android_asset/index.html");


        webview1.setWebViewClient(new WebViewClient(){
            public void onPageFinished(WebView view, String weburl){
                webview1.loadUrl("javascript:myFunction('"+chart1+"')");
            }
        });
        webview1.setWebChromeClient(new WebChromeClient());
        return view;
    }
}