package com.example.webtech_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {
    DecimalFormat df = new DecimalFormat("#.##");
    private RecyclerView pf;
    private RecyclerView fav;
    float walletAmount;
    ProgressBar processing;
    FavAdapter favad = new FavAdapter(this);
    PfAdapter pfad = new PfAdapter(this);
    private ArrayList<MyStocks> pfStocks;
    private ArrayList<MyStocks> favStocks;
    public static boolean stockEntered = false;
    Handler handler = new Handler();
    Runnable runnable;
    Context context;
    int delay = 15000;
    RelativeLayout rl;
    SharedPreferences sp;
    boolean dataArr = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        setContentView(R.layout.activity_main);
        rl = findViewById(R.id.main_content);
        rl.setVisibility(View.GONE);
        processing = findViewById(R.id.processing);
        processing.setVisibility(View.VISIBLE);
        pf = findViewById(R.id.pf);
        fav = findViewById(R.id.fav);

        sp = getApplicationContext().getSharedPreferences("shared_pref",0);
        SharedPreferences.Editor ed = sp.edit();
//        ed.clear();
//        ed.commit();
        if(!sp.contains("Amount")){
            ed.putFloat("Amount", Float.parseFloat(String.format("%.2f",25000.00)));
            ed.apply();
        }
        if(!sp.contains("Favourites")){
            Gson gson = new Gson();
            favStocks = new ArrayList<>();
            String json = gson.toJson(favStocks);
            ed.putString("Favourites", json);
            ed.apply();
        }
        else{
            Gson gson = new Gson();
            String json = sp.getString("Favourites", null);
            Type type = new TypeToken<ArrayList<MyStocks>>() {}.getType();
            favStocks = gson.fromJson(json, type);
        }
        if(!sp.contains("PortFolio")){
            Gson gson = new Gson();
            pfStocks = new ArrayList<>();
            String json = gson.toJson(pfStocks);
            ed.putString("PortFolio", json);
            ed.apply();
        }
        else{
            Gson gson = new Gson();
            String json = sp.getString("PortFolio", null);
            Type type = new TypeToken<ArrayList<MyStocks>>() {}.getType();
            pfStocks = gson.fromJson(json, type);
        }

        setPf(pfStocks);
        swipeFunc();
        setDate();
        float stocksPrice = 0;
        for(int i =0;i<pfStocks.size();i++){
            float a = Float.parseFloat(pfStocks.get(i).getChange())* pfStocks.get(i).getQuantity();
            stocksPrice=stocksPrice+a;

        }
        walletAmount = sp.getFloat("Amount",0);
        stocksPrice=stocksPrice+walletAmount;
        TextView wallet = findViewById(R.id.cashb);
        wallet.setText("$"+String.format("%.2f",walletAmount));
        TextView worth = findViewById(R.id.netw);
        worth.setText("$"+String.format("%.2f",(stocksPrice)));


//        favad = new FavAdapter();
        favad.setPfStocks(favStocks);
        ItemTouchHelper.Callback callback =
                new DragAndDrop(favad);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(fav);
        fav.setLayoutManager(new LinearLayoutManager(this));
        fav.setAdapter(favad);
        pfad.setPfStocks(pfStocks);
        ItemTouchHelper.Callback callback1 =
                new DragAndDrop1(pfad);
        ItemTouchHelper touchHelper1 = new ItemTouchHelper(callback1);
        touchHelper1.attachToRecyclerView(pf);
        pf.setLayoutManager(new LinearLayoutManager(this));
        pf.setAdapter(pfad);
        RequestQueue mq = Volley.newRequestQueue(getApplicationContext());
        dataArr = true;
        Handler handler1 = new Handler();
        handler1.postDelayed(runnable = new Runnable() {
            public void run() {
                if(dataArr){
                    processing.setVisibility(View.GONE);
                    rl.setVisibility(View.VISIBLE);
                }
            }

            },2000);
        sp.registerOnSharedPreferenceChangeListener(this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        MenuItem searchMenu = menu.findItem(R.id.search_icon);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenu);
        final SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete)searchView.findViewById(androidx.appcompat.R.id.search_src_text);

        List<String> dataArr = new ArrayList<String>();
        ArrayAdapter<String> abc = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, dataArr);
        searchAutoComplete.setAdapter(abc);
        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int itemIndex, long id) {
                String queryString=(String)adapterView.getItemAtPosition(itemIndex);

                String[] arrOfStr = queryString.split("\\|",0);
                searchAutoComplete.setText("" + arrOfStr[1].trim());
                Intent i = new Intent(MainActivity.this, DetailsActivity.class);
                i.putExtra("Stock",arrOfStr[1].trim());
                startActivity(i);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                RequestQueue mq = Volley.newRequestQueue(getApplicationContext());
                String url = "https://webtech-8.wl.r.appspot.com/stock/"+query;
                JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                if(response.has("ticker") && !stockEntered) {
                                    Intent i = new Intent(MainActivity.this, DetailsActivity.class);
                                    i.putExtra("Stock", query);
                                    stockEntered = true;
                                    startActivity(i);
                                }
                                else if(!response.has("ticker") && !stockEntered){
                                    Toast.makeText(getApplicationContext(),"Cannot find "+query,Toast.LENGTH_SHORT).show();
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error);
                    }
                });

                mq.add(req);
                return false;

            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.length()>0){
                    RequestQueue mq = Volley.newRequestQueue(getApplicationContext());
                    String url = "https://webtech-8.wl.r.appspot.com/search1/"+newText;
                    JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    try {
                                        JSONArray jsonOp = response.getJSONArray("result");
                                        dataArr.clear();
                                        for(int i=0;i<jsonOp.length();i++){

                                            JSONObject object = jsonOp.getJSONObject(i);
                                            if(object.getString("type").equals("Common Stock")){
                                                dataArr.add(object.getString("description")+" | "+object.getString("displaySymbol"));
                                            }


                                        }
                                        ArrayAdapter<String> abc = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, dataArr);
                                        searchAutoComplete.setAdapter(abc);
                                        abc.notifyDataSetChanged();
                                        System.out.println(dataArr);

                                    } catch (JSONException e) {
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
                }
                return false;
            }
        });

        return true;
    }

    public void setDate(){
        Calendar c = Calendar.getInstance();
        String[]monthName={"January","February","March", "April", "May", "June", "July",
                "August", "September", "October", "November",
                "December"};
        String month=monthName[c.get(Calendar.MONTH)];
        int year=c.get(Calendar.YEAR);
        int date=c.get(Calendar.DATE);
        TextView cal = findViewById(R.id.textView);
        cal.setText( date+ " "+month+ " " +  year);
    }

    public void setPf(ArrayList<MyStocks> stocks){

        pfad.setPfStocks(stocks);
        pf.setAdapter(pfad);
        pf.setLayoutManager(new LinearLayoutManager(this));
//        pf.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
    }
    public void setFav(ArrayList<MyStocks> stocks){
        favad.setPfStocks(stocks);
        fav.setLayoutManager(new LinearLayoutManager(this));
        fav.setAdapter(favad);


//        fav.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
    }

    private void swipeFunc() {
        SwipeAction swipeToDeleteCallback = new SwipeAction(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                SharedPreferences sp = getApplicationContext().getSharedPreferences("shared_pref",0);
                SharedPreferences.Editor ed = sp.edit();
                final int position = viewHolder.getAdapterPosition();
                favad.removeItem(position);
                Gson gson = new Gson();
                String json = gson.toJson(favad.getData());
                ed.putString("Favourites", json);
                ed.apply();





            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(fav);
    }

    @Override
    protected void onResume() {

        SharedPreferences sp = getApplicationContext().getSharedPreferences("shared_pref",0);
        sp.registerOnSharedPreferenceChangeListener(this);
        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                handler.postDelayed(runnable, delay);
                SharedPreferences sp = getApplicationContext().getSharedPreferences("shared_pref",0);

                SharedPreferences.Editor ed = sp.edit();

                Gson gson = new Gson();
                String json = sp.getString("Favourites", null);
                Type type = new TypeToken<ArrayList<MyStocks>>() {}.getType();
                favStocks = gson.fromJson(json, type);
                RequestQueue mq = Volley.newRequestQueue(getApplicationContext());
                final int[] countStocks1 = {0};
                int sizeStocks1 = favStocks.size();
                for(int i =0;i<favStocks.size();i++){
                    String url = "https://webtech-8.wl.r.appspot.com/latestprice/"+favStocks.get(i).getSymbol();
                    int finalI = i;
                    JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        countStocks1[0] +=1;
                                        favStocks.get(finalI).setPrice((float) response.getDouble("c"));
                                        favStocks.get(finalI).setChange((float) response.getDouble("dp")+"*"+(float) response.getDouble("d"));
                                        if(countStocks1[0] == sizeStocks1){
                                            String json = gson.toJson(favStocks);
                                            ed.putString("Favourites", json);
                                            ed.apply();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println(error);
                        }
                    });

                    mq.add(req);
                }
                String json1 = sp.getString("PortFolio", null);
                Type type1 = new TypeToken<ArrayList<MyStocks>>() {}.getType();
                pfStocks = gson.fromJson(json1, type1);
                final float[] stocksPrice = {0};
//                Collections.reverse(pfStocks);
//                float walletAmount = sp.getFloat("Amount",0);
//                stocksPrice[0]=walletAmount;
                final int[] countStocks = {0};
                int sizeStocks = pfStocks.size();
                for(int i =0;i<pfStocks.size();i++){
                    String url = "https://webtech-8.wl.r.appspot.com/latestprice/"+pfStocks.get(i).getSymbol();
                    int finalI = i;
                    JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
//                                        stocksPrice[0] = (float) (stocksPrice[0]+ pfStocks.get(finalI).getQuantity()*response.getDouble("c"));
                                        countStocks[0] +=1;
                                        pfStocks.get(finalI).setChange(String.valueOf(response.getDouble("c")));
                                        if(countStocks[0] == sizeStocks){
                                            setPf(pfStocks);
                                            String json = gson.toJson(pfStocks);
                                            ed.putString("PortFolio", json);
                                            ed.apply();
//                                            TextView worth = findViewById(R.id.netw);
//                                            worth.setText("$"+String.format("%.2f",(stocksPrice[0])));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println(error);
                        }
                    });

                    mq.add(req);
                }

            }
        }, delay);

        super.onResume();
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        SharedPreferences sp = getApplicationContext().getSharedPreferences("shared_pref",0);
        Gson gson = new Gson();
        String json = sp.getString("Favourites", null);
        Type type = new TypeToken<ArrayList<MyStocks>>() {}.getType();
        favStocks = gson.fromJson(json, type);
//        fav = findViewById(R.id.fav);
//        fav.setLayoutManager(new LinearLayoutManager(this));
//        favad = new FavAdapter();
//        favad.setPfStocks(favStocks);
//        ItemTouchHelper.Callback callback =
//                new DragAndDrop(favad);
//        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
//        touchHelper.attachToRecyclerView(fav);
//        fav.setAdapter(favad);
        setFav(favStocks);
        String json1 = sp.getString("PortFolio", null);
        Type type1 = new TypeToken<ArrayList<MyStocks>>() {}.getType();
        pfStocks = gson.fromJson(json1, type1);
        setPf(pfStocks);
        float stocksPrice = 0;
        for(int i =0;i<pfStocks.size();i++){
            float a = Float.parseFloat(pfStocks.get(i).getChange())* pfStocks.get(i).getQuantity();
            stocksPrice=stocksPrice+a;

        }
        walletAmount = sp.getFloat("Amount",0);
        stocksPrice=stocksPrice+walletAmount;
        TextView wallet = findViewById(R.id.cashb);
        wallet.setText("$"+String.format("%.2f",walletAmount));
        TextView worth = findViewById(R.id.netw);
        worth.setText("$"+String.format("%.2f",stocksPrice));
    }

    public void setPref(Context c,ArrayList<MyStocks> a){
        SharedPreferences sp = c.getSharedPreferences("shared_pref",0);
        SharedPreferences.Editor ed = sp.edit();
        Gson gson = new Gson();
        String json = gson.toJson(a);
        ed.putString("PortFolio", json);
        ed.apply();
        sp.registerOnSharedPreferenceChangeListener(this);

    }

    public void onClick(View v){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://finnhub.io/"));
        startActivity(browserIntent);

    }

}