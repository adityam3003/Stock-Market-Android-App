package com.example.webtech_app;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder>{

    private JSONArray news = new JSONArray();
    private Context context;
    @Override
    public int getItemViewType(int position) {
        if (position == 0) return 1;
        else return 2;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType==1){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_1, parent, false);
        }
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_layout, parent, false);
        }
        ViewHolder holder = new ViewHolder(view);
        context = parent.getContext();
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        try {

            if(news.getJSONObject(position).getString("image").length()>0){
                java.util.Date news_time = new java.util.Date(Long.parseLong(news.getJSONObject(position).getString("datetime")) * 1000);
                String my_date = getDate(news_time);
                String toDate = setDate(news_time);
                String source = news.getJSONObject(position).getString("source");
                String headline = news.getJSONObject(position).getString("headline");
                String summary = news.getJSONObject(position).getString("summary");
                String url = news.getJSONObject(position).getString("url");
                holder.newsDate.setText(my_date);
                holder.newsSource.setText(news.getJSONObject(position).getString("source"));
                holder.newdescText.setText(news.getJSONObject(position).getString("headline"));
                Glide.with(context).load(news.getJSONObject(position).getString("image")).into(holder.priceIcon);
                holder.parent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Dialog dialog1 = new Dialog(context);
                        dialog1.setContentView(R.layout.news_data);
                        dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        // set the custom dialog components - text, image and button
                        dialog1.show();
                        TextView newSource = dialog1.findViewById(R.id.newsSource);
                        TextView newsDate = dialog1.findViewById(R.id.newsDate);
                        TextView newsheadline = dialog1.findViewById(R.id.newsheadline);
                        TextView newssummary = dialog1.findViewById(R.id.newssummary);
                        newSource.setText(source);
                        newsDate.setText(toDate);
                        newsheadline.setText(headline);
                        newssummary.setText(summary);
                        ImageView chrome = dialog1.findViewById(R.id.chrome);
                        chrome.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                context.startActivity(browserIntent);
                            }
                        });
                        ImageView twitter = dialog1.findViewById(R.id.twitter);
                        twitter.setOnClickListener(new View.OnClickListener() {
                            @Override

                            public void onClick(View view) {
                                String nurl= "";
                                String nheadline ="";
                                try {
                                    nheadline = URLEncoder.encode(headline, "UTF-8")
                                            .replaceAll("\\+", "%20")
                                            .replaceAll("\\%21", "!")
                                            .replaceAll("\\%27", "'")
                                            .replaceAll("\\%28", "(")
                                            .replaceAll("\\%29", ")")
                                            .replaceAll("\\%7E", "~");
                                    nurl = URLEncoder.encode(url, "UTF-8")
                                            .replaceAll("\\+", "%20")
                                            .replaceAll("\\%21", "!")
                                            .replaceAll("\\%27", "'")
                                            .replaceAll("\\%28", "(")
                                            .replaceAll("\\%29", ")")
                                            .replaceAll("\\%7E", "~");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                String tweetURL = "https://twitter.com/intent/tweet?text=" +
                                                (nheadline) +
                                                "&url=" +
                                                (nurl);
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetURL));
                                context.startActivity(browserIntent);
                            }
                        });
                        ImageView facebook = dialog1.findViewById(R.id.facebook);
                        facebook.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String nurl = null;
                                try {
                                    nurl = URLEncoder.encode(url, "UTF-8")
                                            .replaceAll("\\+", "%20")
                                            .replaceAll("\\%21", "!")
                                            .replaceAll("\\%27", "'")
                                            .replaceAll("\\%28", "(")
                                            .replaceAll("\\%29", ")")
                                            .replaceAll("\\%7E", "~");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                String tweetURL = "https://www.facebook.com/sharer/sharer.php?u=" +
                                        (nurl) +
                                        "&src=sdkpreparse";
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetURL));
                                context.startActivity(browserIntent);
                            }
                        });
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String setDate(Date timestamp){
        String[]monthName={"January","February","March", "April", "May", "June", "July",
                "August", "September", "October", "November",
                "December"};
        String month=monthName[timestamp.getMonth()];
        return month+" "+timestamp.getDate()+", 2022";
        }

    @Override
    public int getItemCount() {
        return 20;
    }

    public String getDate(Date timestamp){
        Date today = new Date();
        long diff = (today.getTime() - timestamp.getTime())/1000;
        long minutes = diff / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        if(days>1){
            return days+" days ago";
        }
        else if (hours>1){
            return hours+" hours ago";
        }
        else{
            return minutes+" minutes ago";
        }
    }

    public void setPfStocks(JSONArray s){
        this.news = s;
        notifyDataSetChanged();

    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView newsSource;
        private TextView newsDate;
        private TextView newdescText;
        private ImageView priceIcon;
        private RelativeLayout parent;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            newsSource = itemView.findViewById(R.id.newsSource);
            newsDate = itemView.findViewById(R.id.newsDate);
            newdescText = itemView.findViewById(R.id.newdescText);
            priceIcon = itemView.findViewById(R.id.newsImage);
            parent = itemView.findViewById(R.id.parent);

        }

    }
}
