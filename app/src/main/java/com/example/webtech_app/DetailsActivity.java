package com.example.webtech_app;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity {
    DecimalFormat df = new DecimalFormat("#.##");
    private TextView noShares;
    private EditText yourEditText;
    private RecyclerView news;
    private RequestQueue mq;
    LinearLayout peer;
    private boolean inFav = false;
    ImageView favIc;
    int timestamp;
    String value = "";
    float Change = 0;
    final Context context = this;
    private Button button;
    JSONObject companyData;
    JSONArray newsData;
    JSONArray peerData;
    JSONObject priceData;
    private TabLayout tablayout;
    ProgressBar processing;
    ArrayList <MyStocks> favStocks;
    ArrayList <MyStocks> pfStocks;
    private ViewPager viewp;
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 15000;
    RelativeLayout rl;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        rl = findViewById(R.id.main_layout);
        rl.setVisibility(View.GONE);
        processing = findViewById(R.id.processing);
        processing.setVisibility(View.VISIBLE);
        Bundle extras = getIntent().getExtras();
        peer = findViewById(R.id.peerval);
        if (extras != null) {
            value = extras.getString("Stock");
        }


        news = findViewById(R.id.newsRec);
        tablayout = findViewById(R.id.swipe);

        viewp = findViewById(R.id.chartFrag);
        tablayout.setupWithViewPager(viewp);





        mq = Volley.newRequestQueue(this);


        SharedPreferences sp = getApplicationContext().getSharedPreferences("shared_pref",0);
        SharedPreferences.Editor ed = sp.edit();
        Gson gson = new Gson();


        String json = sp.getString("Favourites", null);
        Type type = new TypeToken<ArrayList<MyStocks>>() {}.getType();
        favStocks = gson.fromJson(json, type);

        String url = "https://webtech-8.wl.r.appspot.com/latestprice/"+value;
        JsonObjectRequest req1 = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            priceData = response;
                            String json1 = sp.getString("PortFolio", null);
                            Type type1 = new TypeToken<ArrayList<MyStocks>>() {}.getType();
                            pfStocks = gson.fromJson(json1, type1);
                            for(MyStocks i:pfStocks){

                                if(i.getSymbol().equals(value)){
                                    TextView pfQ = findViewById(R.id.pfQ);
                                    pfQ.setText(String.valueOf(i.getQuantity()));
                                    TextView avgP = findViewById(R.id.avgP);
                                    avgP.setText("$"+String.format("%.2f",(i.getPrice())));
                                    TextView totalP = findViewById(R.id.totalP);
                                    totalP.setText("$"+String.format("%.2f",(i.getQuantity()*i.getPrice())));
                                    TextView changeP = findViewById(R.id.changeP);

                                    float priceChange = Float.valueOf(df.format((float) (priceData.getDouble("c") - i.getPrice())));
                                    changeP.setText("$"+String.format("%.2f", priceChange));
                                    TextView marketP = findViewById(R.id.marketP);
                                    marketP.setText("$"+String.format("%.2f",(i.getQuantity()*priceData.getDouble("c"))));
                                    if(priceChange<0){
                                        changeP.setTextColor(getResources().getColor(R.color.stock_red));
                                        marketP.setTextColor(getResources().getColor(R.color.stock_red));
                                    }
                                    else if (priceChange>0){
                                        changeP.setTextColor(getResources().getColor(R.color.stock_green));
                                        marketP.setTextColor(getResources().getColor(R.color.stock_green));
                                    }
                                    else{
                                        changeP.setTextColor(getResources().getColor(R.color.black));
                                        marketP.setTextColor(getResources().getColor(R.color.black));
                                    }
                                    break;
                                }
                            }
                            TextView price = findViewById(R.id.stockPrice);
                            price.setText("$"+String.format("%.2f",(priceData.getDouble("c"))));
                            TextView change = findViewById(R.id.stockChange);
                            change.setText(String.format("%.2f",(priceData.getDouble("d")))+" ("+String.format("%.2f",(priceData.getDouble("dp")))+")%");
                            ImageView pv = findViewById(R.id.pricetrend);
                            if(priceData.getDouble("d")>0){
                               pv.setImageResource(R.drawable.trending_up);
                               change.setTextColor(ContextCompat.getColor(change.getContext(), R.color.stock_green));
                            }
                            else if(priceData.getDouble("d")<0){
                                pv.setImageResource(R.drawable.trending_down);
                                change.setTextColor(ContextCompat.getColor(change.getContext(), R.color.stock_red));
                            }
                            Change = (float) (priceData.getDouble("c") - priceData.getDouble("pc"));
                            timestamp =  priceData.getInt("t");
                            JSONObject chart1 = new JSONObject();
                            try {
                                chart1.put("change", Change);
                                chart1.put("stock", value);
                                chart1.put("url", "https://webtech-8.wl.r.appspot.com/chartdata/"+value+"*"+Integer.toString(timestamp));
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            FragAdapter fragNew = new FragAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
                            fragNew.addFragment(new chart1Fragment(chart1));
                            fragNew.addFragment(new chart2Fragment(value));
                            viewp.setAdapter(fragNew);


                            tablayout.getTabAt(0).setIcon(R.drawable.chart1);
                            tablayout.getTabAt(1).setIcon(R.drawable.chart2);
                            tablayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
                                @Override
                                public void onTabSelected(TabLayout.Tab tab) {
                                    int tabIconColor = ContextCompat.getColor(context, R.color.blue);
                                    tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                                }

                                @Override
                                public void onTabUnselected(TabLayout.Tab tab) {
                                    int tabIconColor = ContextCompat.getColor(context, R.color.black);
                                    tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                                }

                                @Override
                                public void onTabReselected(TabLayout.Tab tab) {

                                }
                            });


                            TextView op = findViewById(R.id.op);
                            op.setText("Open Price: $"+priceData.getString("o"));
                            TextView hp = findViewById(R.id.hp);
                            hp.setText("High Price: $"+priceData.getString("h"));
                            TextView lp = findViewById(R.id.lp);
                            lp.setText("Low Price: $"+priceData.getString("l"));
                            TextView pc = findViewById(R.id.pc);
                            pc.setText("Prev. Close: $"+priceData.getString("pc"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        });
        req1.setRetryPolicy(new DefaultRetryPolicy(
                6000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mq.add(req1);
        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);


        url = "https://webtech-8.wl.r.appspot.com/stock/"+value;
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            companyData = response;
                            ImageView logo = findViewById(R.id.logo);
                            String imageurl = companyData.getString("logo");
                            Picasso.get().load(imageurl).into(logo);
                            TextView ticker = findViewById(R.id.stockName);
                            ticker.setText(companyData.getString("ticker"));
                            TextView name = findViewById(R.id.stockTicker);
                            name.setText(companyData.getString("name"));
                            TextView ipoDate = findViewById(R.id.ipoval);
                            ipoDate.setText(companyData.getString("ipo"));
                            TextView industry = findViewById(R.id.industryval);
                            industry.setText(companyData.getString("finnhubIndustry"));
                            TextView webpage = findViewById(R.id.webpageval);
                            webpage.setClickable(true);
                            webpage.setMovementMethod(LinkMovementMethod.getInstance());
                            String weburl = companyData.getString("weburl");
                            webpage.setText(weburl);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        });
        req.setRetryPolicy(new DefaultRetryPolicy(
                6000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mq.add(req);


        url = "https://webtech-8.wl.r.appspot.com/peers/"+value;
        JsonArrayRequest req2 = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try {
                            peerData = response;
                            for(int i =0;i< response.length();i++){
                                String weburl = (String) response.get(i);
                                TextView tv = new TextView(DetailsActivity.this);
                                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                );
                                if(i==response.length()-1){
                                    tv.setText((String) response.get(i)+" ");

                                }
                                else{
                                    tv.setText((String) response.get(i)+", ");
                                }
                                tv.setTextColor(Color.BLUE);
                                tv.isClickable();
                                tv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent ni = new Intent(DetailsActivity.this,DetailsActivity.class);
                                        ni.putExtra("Stock",weburl);
                                        DetailsActivity.this.startActivity(ni);
                                    }
                                });
                                tv.setLayoutParams(p);
                                peer.addView(tv);
                                processing.setVisibility(View.GONE);
                                rl.setVisibility(View.VISIBLE);
                                getSupportActionBar().setDisplayShowCustomEnabled(true);
                                getSupportActionBar().setCustomView(R.layout.main_action_bar);
                                //getSupportActionBar().setElevation(0);

                                View view = getSupportActionBar().getCustomView();

                                TextView name = view.findViewById(R.id.name);
                                name.setText(value);
                                favIc = findViewById(R.id.fav_icon);
                                for(MyStocks j:favStocks){
                                    if(j.getSymbol().equals(value)){
                                        inFav = true;
                                        favIc.setImageResource(R.drawable.star);
                                        break;
                                    }
                                }

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        });
        req2.setRetryPolicy(new DefaultRetryPolicy(
                6000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mq.add(req2);

        url = "https://webtech-8.wl.r.appspot.com/socialsentiment/"+value;
        JsonObjectRequest req4 = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            int twitterPos,twitterNeg,twitterTotal,redditPos,redditNeg,redditTotal;
                            twitterPos=twitterNeg=twitterTotal=redditPos=redditNeg=redditTotal=0;
                            JSONArray twitterData = response.getJSONArray("twitter");
                            for(int i=0;i<twitterData.length();i++){
                                JSONObject a = new JSONObject(twitterData.get(i).toString());
                                twitterPos+=a.getInt("positiveMention");
                                twitterNeg+=a.getInt("negativeMention");
                                twitterTotal+=a.getInt("mention");
                            }
                            JSONArray redditData = response.getJSONArray("reddit");
                            for(int i=0;i<redditData.length();i++){
                                JSONObject a = new JSONObject(redditData.get(i).toString());
                                redditPos+=a.getInt("positiveMention");
                                redditNeg+=a.getInt("negativeMention");
                                redditTotal+=a.getInt("mention");
                            }
                            TextView ssstock = findViewById(R.id.ssstock);
                            ssstock.setText(companyData.getString("name"));
                            TextView redmen = findViewById(R.id.redmen);
                            redmen.setText(String.valueOf(redditTotal));
                            TextView redpm = findViewById(R.id.redpm);
                            redpm.setText(String.valueOf(redditPos));
                            TextView rednm = findViewById(R.id.rednm);
                            rednm.setText(String.valueOf(redditNeg));
                            TextView twitmen = findViewById(R.id.twitmen);
                            twitmen.setText(String.valueOf(twitterTotal));
                            TextView twitpm = findViewById(R.id.twitpm);
                            twitpm.setText(String.valueOf(twitterPos));
                            TextView twitnm = findViewById(R.id.twitnm);
                            twitnm.setText(String.valueOf(twitterNeg));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        });
        req4.setRetryPolicy(new DefaultRetryPolicy(
                6000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mq.add(req4);


        String newUrl = "https://webtech-8.wl.r.appspot.com/recommendation/"+value;
        JsonObjectRequest req6 = new JsonObjectRequest(Request.Method.GET, newUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            WebView webview = findViewById(R.id.chart3);
                            WebSettings webSettings = webview.getSettings();

                            webSettings.setJavaScriptEnabled(true);

                            webSettings.setBuiltInZoomControls(true);

                            webview.requestFocusFromTouch();


                            webview.loadUrl("file:///android_asset/index3.html");


                            webview.setWebViewClient(new WebViewClient(){
                                public void onPageFinished(WebView view, String weburl){
                                    webview.loadUrl("javascript:myFunc('"+response+"')");
                                }
                            });
                            webview.setWebChromeClient(new WebChromeClient());


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        });
        req6.setRetryPolicy(new DefaultRetryPolicy(
                6000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mq.add(req6);
        newUrl = "https://webtech-8.wl.r.appspot.com/earnings/"+value;
        JsonObjectRequest req7 = new JsonObjectRequest(Request.Method.GET, newUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            WebView webview = findViewById(R.id.chart4);
                            WebSettings webSettings = webview.getSettings();
                            webSettings.setJavaScriptEnabled(true);
                            webSettings.setBuiltInZoomControls(true);
                            webview.requestFocusFromTouch();

                            webview.loadUrl("file:///android_asset/index4.html");


                            webview.setWebViewClient(new WebViewClient(){
                                public void onPageFinished(WebView view, String weburl){
                                    webview.loadUrl("javascript:myFunc('"+response+"')");
                                }
                            });
                            webview.setWebChromeClient(new WebChromeClient());


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        });
        req7.setRetryPolicy(new DefaultRetryPolicy(
                6000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mq.add(req7);
        news.setLayoutManager(new LinearLayoutManager(this));
        newUrl = "https://webtech-8.wl.r.appspot.com/news/"+value;
        JsonArrayRequest req8 = new JsonArrayRequest(Request.Method.GET, newUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try {
                            newsData = response;
                            NewsAdapter newsAd = new NewsAdapter();
                            newsAd.setPfStocks(newsData);
                            news.setAdapter(newsAd);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        });
        req8.setRetryPolicy(new DefaultRetryPolicy(
                6000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mq.add(req8);


        button = (Button) findViewById(R.id.trade);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.trade);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                int quantity = 0;
                String json1 = sp.getString("PortFolio", null);
                float walletAmount = sp.getFloat("Amount",0);
                Type type1 = new TypeToken<ArrayList<MyStocks>>() {}.getType();
                pfStocks = gson.fromJson(json1, type1);
                for(MyStocks i:pfStocks){

                    if(i.getSymbol().equals(value)){
                        quantity = i.getQuantity();
                        break;
                    }
                }
                TextView b = dialog.findViewById(R.id.trade_dialogue);
                try {
                    b.setText("Trade "+companyData.getString("name")+" shares");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                TextView a = dialog.findViewById(R.id.wallet);
                a.setText("$"+walletAmount+" to buy "+value);
                yourEditText = dialog.findViewById(R.id.shares);
                noShares = dialog.findViewById(R.id.calculation);
                try {
                    noShares.setText("0.0*$"+priceData.getDouble("c")+"/shares = 0.0");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                yourEditText.addTextChangedListener(new TextWatcher() {

                    public void afterTextChanged(Editable s) {


                    }

                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        try {
                            String se = "";
                            String regexStr = "^[0-9]*$";
                            if(!(yourEditText.getText().toString().trim().matches(regexStr))){
                                Toast.makeText(getApplicationContext(),"Please enter a valid amount",Toast.LENGTH_SHORT).show();
                                se = "0.0*$"+String.format("%.2f", priceData.getDouble("c"))+"/shares = 0.0";
                            }
                            else if(yourEditText.getText().toString().trim().length()==0){
                                se = "0.0*$"+String.format("%.2f", priceData.getDouble("c"))+"/shares = 0.0";
                            }
                            else{
                                int shares =Integer.parseInt(String.valueOf(yourEditText.getText()));
                                se = yourEditText.getText()+"*$"+String.format("%.2f", priceData.getDouble("c"))+"/shares = "+String.format("%.2f",(priceData.getDouble("c")*shares));
                            }
                            noShares.setText(se);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
                Button dialogButton = (Button) dialog.findViewById(R.id.buy);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onClick(View v) {
                        String regexStr = "^[0-9]*$";
                        if(!(yourEditText.getText().toString().trim().matches(regexStr))){
                            Toast.makeText(getApplicationContext(),"Please enter a valid amount",Toast.LENGTH_SHORT).show();
                        }
                        else if(yourEditText.getText().length()==0 || (Integer.valueOf(String.valueOf(yourEditText.getText()))==0)){
                            Toast.makeText(getApplicationContext(),"Cannot buy non-positive shares",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            try {
                                if(Float.valueOf(df.format((Integer.valueOf(String.valueOf(yourEditText.getText())))*(float) priceData.getDouble("c"))) > walletAmount){
                                    Toast.makeText(getApplicationContext(),"Not enough money to buy ",Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    boolean elementF = false;
                                    float totalNew = Float.valueOf(df.format((Integer.valueOf(String.valueOf(yourEditText.getText())))*Float.valueOf(df.format((float) priceData.getDouble("c")))));
                                    for(int i=0;i<pfStocks.size();i++){
                                        if(pfStocks.get(i).getSymbol().equals(value)){
                                            int quant = pfStocks.get(i).getQuantity()+Integer.valueOf(String.valueOf(yourEditText.getText()));
                                            float price = Float.valueOf(df.format((float) ((totalNew +pfStocks.get(i).getPrice()*pfStocks.get(i).getQuantity())/(quant))));
                                            pfStocks.get(i).setPrice(price);
                                            pfStocks.get(i).setQuantity(quant);
                                            pfStocks.get(i).setChange(String.valueOf(Float.valueOf(df.format((float) priceData.getDouble("c")))));
                                            elementF = true;
                                            TextView pfQ = findViewById(R.id.pfQ);
                                            pfQ.setText(String.valueOf(quant));
                                            TextView avgP = findViewById(R.id.avgP);
                                            avgP.setText("$"+String.valueOf(price));
                                            TextView totalP = findViewById(R.id.totalP);
                                            totalP.setText("$"+String.valueOf(Float.valueOf(df.format(totalNew +Float.valueOf(df.format(favStocks.get(i).getPrice()))*favStocks.get(i).getQuantity()))));
                                            TextView changeP = findViewById(R.id.changeP);
                                            float priceChange = Float.valueOf(df.format(priceData.getDouble("c")))-price;
                                            changeP.setText("$"+String.valueOf(priceChange));
                                            TextView marketP = findViewById(R.id.marketP);
                                            marketP.setText("$"+String.valueOf(quant*Float.valueOf(df.format(priceData.getDouble("c")))));
                                            if(priceChange<0){
                                                changeP.setTextColor(getResources().getColor(R.color.stock_red));
                                                marketP.setTextColor(getResources().getColor(R.color.stock_red));
                                            }
                                            else if (priceChange>0){
                                                changeP.setTextColor(getResources().getColor(R.color.stock_green));
                                                marketP.setTextColor(getResources().getColor(R.color.stock_green));
                                            }
                                            else{
                                                changeP.setTextColor(getResources().getColor(R.color.black));
                                                marketP.setTextColor(getResources().getColor(R.color.black));
                                            }
                                            ed.putFloat("Amount",Float.valueOf(df.format(Float.valueOf(df.format(Float.valueOf(df.format(walletAmount)) -totalNew)))));
                                            ed.apply();
                                            break;
                                        }
                                    }
                                    if(!elementF){

                                        pfStocks.add((new MyStocks(companyData.getString("name"),companyData.getString("ticker"), Float.valueOf(df.format((float) priceData.getDouble("c"))),Integer.valueOf(String.valueOf(yourEditText.getText())),String.valueOf((float) priceData.getDouble("c")))));
                                        TextView pfQ = findViewById(R.id.pfQ);
                                        pfQ.setText(String.valueOf(Integer.valueOf(String.valueOf(yourEditText.getText()))));
                                        TextView avgP = findViewById(R.id.avgP);
                                        avgP.setText("$"+String.format("%.2f",((float) priceData.getDouble("c"))));
                                        TextView totalP = findViewById(R.id.totalP);
                                        totalP.setText("$"+String.format("%.2f",((float) priceData.getDouble("c")*Integer.valueOf(String.valueOf(yourEditText.getText())))));
                                        TextView changeP = findViewById(R.id.changeP);
                                        changeP.setText("$0.00");
                                        TextView marketP = findViewById(R.id.marketP);
                                        marketP.setText("$"+String.format("%.2f",((float) priceData.getDouble("c")*Integer.valueOf(String.valueOf(yourEditText.getText())))));
                                        ed.putFloat("Amount",Float.valueOf(df.format(Float.valueOf(df.format(walletAmount)) -(Float.valueOf(df.format((float) priceData.getDouble("c")))*Integer.valueOf(String.valueOf(yourEditText.getText()))))));
                                        ed.apply();
                                    }
                                    String json = gson.toJson(pfStocks);
                                    ed.putString("PortFolio", json);
                                    ed.apply();


                                    final Dialog dialog1 = new Dialog(context);
                                    dialog1.setContentView(R.layout.trade_success);
                                    dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    dialog1.show();
                                    TextView tradeDetails = dialog1.findViewById(R.id.trade_details);
                                    tradeDetails.setText("You have successfully bought " + yourEditText.getText() + " shares of " + value);
                                    Button dialogButton2 = (Button) dialog1.findViewById(R.id.done);
                                    dialogButton2.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog1.dismiss();
                                        }
                                    });
                                    dialog.dismiss();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                Button dialogButton1 = (Button) dialog.findViewById(R.id.sell);

                int finalQuantity = quantity;
                dialogButton1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String regexStr = "^[0-9]*$";
                        if(!(yourEditText.getText().toString().trim().matches(regexStr))){
                            Toast.makeText(getApplicationContext(),"Please enter a valid amount",Toast.LENGTH_SHORT).show();
                        }
                        else if(yourEditText.getText().length()==0 ||  (Integer.valueOf(String.valueOf(yourEditText.getText()))==0)){
                            Toast.makeText(getApplicationContext(),"Cannot sell non-positive shares",Toast.LENGTH_SHORT).show();
                        }

                        else if(Integer.valueOf(String.valueOf(yourEditText.getText()))> finalQuantity){
                            Toast.makeText(getApplicationContext(),"Not enough shares to sell",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            int idx = -1;
                            for(int i=0;i<pfStocks.size();i++){
                                if(pfStocks.get(i).getSymbol().equals(value) && (finalQuantity==Integer.valueOf(String.valueOf(yourEditText.getText())))){
                                    idx = i;

                                    break;
                                }
                                else if(pfStocks.get(i).getSymbol().equals(value)){
                                    float totalNew = 0;
                                    try {
                                        totalNew = (Integer.valueOf(String.valueOf(yourEditText.getText())))*Float.valueOf(df.format((float) priceData.getDouble("c")));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    int quant = finalQuantity - Integer.valueOf(String.valueOf(yourEditText.getText()));
                                    float price = Float.valueOf(df.format(((Float.valueOf(df.format(pfStocks.get(i).getPrice()))*pfStocks.get(i).getQuantity()) - totalNew)/quant));
                                    pfStocks.get(i).setPrice(price);
                                    pfStocks.get(i).setQuantity(quant);
                                    TextView pfQ = findViewById(R.id.pfQ);
                                    pfQ.setText(String.valueOf(quant));
                                    TextView avgP = findViewById(R.id.avgP);
                                    avgP.setText(String.format("%.2f",(price)));
                                    TextView totalP = findViewById(R.id.totalP);
                                    totalP.setText("$"+String.format("%.2f",(quant*price)));
                                    TextView changeP = findViewById(R.id.changeP);
                                    float priceChange = 0;
                                    try {
                                        priceChange = Float.valueOf(df.format((float)priceData.getDouble("c")))-price;
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    changeP.setText("$"+String.format("%.2f",(priceChange)));
                                    TextView marketP = findViewById(R.id.marketP);
                                    try {
                                        marketP.setText("$"+String.format("%.2f",(quant*priceData.getDouble("c"))));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    if(priceChange<0){
                                        changeP.setTextColor(getResources().getColor(R.color.stock_red));
                                        marketP.setTextColor(getResources().getColor(R.color.stock_red));
                                    }
                                    else if (priceChange>0){
                                        changeP.setTextColor(getResources().getColor(R.color.stock_green));
                                        marketP.setTextColor(getResources().getColor(R.color.stock_green));
                                    }
                                    else{
                                        changeP.setTextColor(getResources().getColor(R.color.black));
                                        marketP.setTextColor(getResources().getColor(R.color.black));
                                    }
                                    String json = gson.toJson(pfStocks);
                                    ed.putString("PortFolio", json);
                                    ed.putFloat("Amount",Float.valueOf(df.format((Float.valueOf(df.format(walletAmount))) + totalNew)));
                                    ed.apply();
                                    break;
                                }
                            }

                            if(idx!=-1){
                                float totalNew = 0;
                                try {
                                    totalNew = (Integer.valueOf(String.valueOf(yourEditText.getText())))*Float.valueOf(df.format((float) priceData.getDouble("c")));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                pfStocks.remove(idx);
                                TextView pfQ = findViewById(R.id.pfQ);
                                pfQ.setText("0");
                                TextView avgP = findViewById(R.id.avgP);
                                avgP.setText("$0.00");
                                TextView totalP = findViewById(R.id.totalP);
                                totalP.setText("$0.00");
                                TextView changeP = findViewById(R.id.changeP);
                                changeP.setText("$0.00");
                                TextView marketP = findViewById(R.id.marketP);
                                marketP.setText("$0.00");
                                changeP.setTextColor(getResources().getColor(R.color.black));
                                marketP.setTextColor(getResources().getColor(R.color.black));
                                String json = gson.toJson(pfStocks);
                                ed.putString("PortFolio", json);
                                ed.putFloat("Amount",Float.valueOf(df.format(Float.valueOf(df.format(walletAmount)) + totalNew))) ;
                                ed.apply();
                            }
                            final Dialog dialog1 = new Dialog(context);
                            dialog1.setContentView(R.layout.trade_success);
                            dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                            dialog1.show();
                            TextView tradeDetails = dialog1.findViewById(R.id.trade_details);
                            tradeDetails.setText("You have successfully sold "+yourEditText.getText()+" shares of "+value);
                            Button dialogButton2 = (Button) dialog1.findViewById(R.id.done);
                            dialogButton2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    dialog1.dismiss();
                                }
                            });
                            dialog.dismiss();
                        }
                    }
                });

                dialog.show();



            }
        });



    }

    public void onClick(View v)
    {
        TextView TextViewTest = findViewById(R.id.webpageval);
        String Test1 = TextViewTest.getText().toString();
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Test1));
        startActivity(browserIntent);
    }

//    @Override
//    protected void onResume() {
//        handler.postDelayed(runnable = new Runnable() {
//            public void run() {
//                handler.postDelayed(runnable, delay);
//                RequestQueue mq = Volley.newRequestQueue(context);
//                String url = "https://webtech-8.wl.r.appspot.com/latestprice/"+value;
//                JsonObjectRequest req1 = new JsonObjectRequest(Request.Method.GET, url, null,
//                        new Response.Listener<JSONObject>() {
//                            @SuppressLint("ResourceAsColor")
//                            @Override
//                            public void onResponse(JSONObject response) {
//
//                                try {
//                                    priceData = response;
//
//                                    TextView price = findViewById(R.id.stockPrice);
//                                    price.setText("$"+String.format("%.2f",(priceData.getDouble("c"))));
//                                    TextView change = findViewById(R.id.stockChange);
//                                    change.setText(String.format("%.2f",(priceData.getDouble("d")))+" ("+String.format("%.2f",(priceData.getDouble("dp")))+")%");
//                                    ImageView pv = findViewById(R.id.pricetrend);
//                                    if(priceData.getDouble("d")>0){
//                                        pv.setImageResource(R.drawable.trending_up);
//                                        change.setTextColor(ContextCompat.getColor(change.getContext(), R.color.stock_green));
//                                    }
//                                    else if(priceData.getDouble("d")<0){
//                                        pv.setImageResource(R.drawable.trending_down);
//                                        change.setTextColor(ContextCompat.getColor(change.getContext(), R.color.stock_red));
//                                    }
//
//                                    TextView op = findViewById(R.id.op);
//                                    op.setText("Open Price: $"+priceData.getString("o"));
//                                    TextView hp = findViewById(R.id.hp);
//                                    hp.setText("High Price: $"+priceData.getString("h"));
//                                    TextView lp = findViewById(R.id.lp);
//                                    lp.setText("Low Price: $"+priceData.getString("l"));
//                                    TextView pc = findViewById(R.id.pc);
//                                    pc.setText("Prev. Close: $"+priceData.getString("pc"));
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//
//                            }
//                        }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        System.out.println(error);
//                    }
//                });
//                mq.add(req1);
//            }
//        }, delay);
//        super.onResume();
//    }

    public void goBack(View view){
        finish();
        return;
    }
    public void favChange(View view){
        favIc = findViewById(R.id.fav_icon);
        Context context = getApplicationContext();
        String text = value;
        int duration = Toast.LENGTH_SHORT;
        SharedPreferences sp = getApplicationContext().getSharedPreferences("shared_pref",0);
        SharedPreferences.Editor ed = sp.edit();


        if(!inFav){

            Gson gson = new Gson();
            String json = sp.getString("Favourites", null);
            Type type = new TypeToken<ArrayList<MyStocks>>() {}.getType();
            ArrayList <MyStocks> favStocks = gson.fromJson(json, type);
            try {
                favStocks.add(new MyStocks(companyData.getString("name"),companyData.getString("ticker"), (float) priceData.getDouble("c"),0,(float) priceData.getDouble("dp")+"*"+(float) priceData.getDouble("d")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String newjson = gson.toJson(favStocks);

            ed.putString("Favourites", newjson);

            ed.apply();
            inFav = true;
            favIc.setImageResource(R.drawable.star);
            text+=" Added to Favourites";
        }
        else{
            Gson gson = new Gson();
            String json = sp.getString("Favourites", null);
            Type type = new TypeToken<ArrayList<MyStocks>>() {}.getType();
            ArrayList <MyStocks> favStocks = gson.fromJson(json, type);
            int idx = -1;
            for(int i=0;i<favStocks.size();i++){
                if(favStocks.get(i).getSymbol().equals(value)){
                    idx = i;
                    break;
                }
            }
            favStocks.remove(idx);
            String newjson = gson.toJson(favStocks);
            ed.putString("Favourites", newjson);
            ed.apply();
            inFav = false;
            favIc.setImageResource(R.drawable.star_empty);
            text+=" Removed from Favourites";
        }
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }



}
