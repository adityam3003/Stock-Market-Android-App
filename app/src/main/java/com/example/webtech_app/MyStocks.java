package com.example.webtech_app;

import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

public class MyStocks {
    String name;
    private RequestQueue mq;
    String symbol;
    String change;
    float price;
    int quantity;

    public MyStocks(String name, String symbol, float price, int quantity,String change) {
        this.name = name;
        this.symbol = symbol;
        this.price = price;
        this.quantity = quantity;
        this.change = change;
    }


    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setChange(String symbol) {
        this.change = symbol;
    }
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }


    @Override
    public String toString() {
        return "MyStocks{" +
                "name='" + name + '\'' +
                ", symbol='" + symbol + '\'' +
                ", price=" + price +", change=" + change +
                '}';
    }


    public String getChange() {
        return this.change;
    }
}
