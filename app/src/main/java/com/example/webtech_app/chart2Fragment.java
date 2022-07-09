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


public class chart2Fragment extends Fragment {

    private final String ticker;

    public chart2Fragment(String ticker){
        this.ticker = ticker;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_chart2, container, false);
        WebView webview = view.findViewById(R.id.chart2);
        WebSettings webSettings = webview.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webview.requestFocusFromTouch();
        webview.loadUrl("file:///android_asset/index2.html");

        JSONObject chart = new JSONObject();
        try {
            chart.put("url", "https://webtech-8.wl.r.appspot.com/chartdata1/"+ticker);
            chart.put("stock", ticker);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        webview.setWebViewClient(new WebViewClient(){
            public void onPageFinished(WebView view, String weburl){
                webview.loadUrl("javascript:myFunc('"+chart+"')");
            }
        });
        webview.setWebChromeClient(new WebChromeClient());
        return view;

    }
}