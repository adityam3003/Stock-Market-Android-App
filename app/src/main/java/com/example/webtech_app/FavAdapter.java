package com.example.webtech_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

public class FavAdapter extends RecyclerView.Adapter<FavAdapter.ViewHolder>implements DragAndDrop.ItemTouchHelperContract{

    private ArrayList<MyStocks> pfStocks = new ArrayList<>();

    SharedPreferences sp;
    SharedPreferences.Editor ed;
    Context c;
    public FavAdapter(Context c) {
        this.c = c;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fav_layout,parent,false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.stockName.setText(pfStocks.get(position).getSymbol());
        holder.stockTicker.setText(pfStocks.get(position).getName());
        holder.price.setText("$"+String.valueOf(pfStocks.get(position).getPrice()));
        String[] arrOfStr = pfStocks.get(position).getChange().split("\\*",0);
        holder.change.setText("$"+String.format("%.2f",Float.parseFloat(arrOfStr[1]))+" ("+String.format("%.2f",Float.parseFloat(arrOfStr[0]))+"%)");
        if(Float.parseFloat(arrOfStr[0])>0){
            holder.icon.setImageResource(R.drawable.trending_up);
            holder.change.setTextColor(ContextCompat.getColor(holder.change.getContext(), R.color.stock_green));
        }
        else if(Float.parseFloat(arrOfStr[0])<0){
            holder.icon.setImageResource(R.drawable.trending_down);
            holder.change.setTextColor(ContextCompat.getColor(holder.change.getContext(), R.color.stock_red));
        }
        holder.btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String stockSymbol = pfStocks.get(holder.getAdapterPosition()).getSymbol();
                Intent ni = new Intent(c,DetailsActivity.class);
                ni.putExtra("Stock",stockSymbol);
                c.startActivity(ni);
            }
        });
    }
    public ArrayList<MyStocks> getData() {
        return pfStocks;
    }
    @Override
    public int getItemCount() {
        return pfStocks.size();
    }
    public void removeItem(int position) {
        pfStocks.remove(position);
        notifyItemRemoved(position);
    }

    public void setPfStocks(ArrayList<MyStocks> s){
        this.pfStocks = s;
        notifyDataSetChanged();

    }
    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
//        sp = context.getSharedPreferences("shared_pref",0);
//        ed = sp.edit();
//        Gson gson = new Gson();
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(pfStocks, i, i + 1);

            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(pfStocks, i, i - 1);
            }
        }
//        String json = gson.toJson(pfStocks);
//        ed.putString("Favourites", json);
//        ed.apply();
//        MainActivity m = new MainActivity();
//        m.setPref(context,pfStocks);
        notifyItemMoved(fromPosition,toPosition);




    }
    @Override
    public void onRowSelected(ViewHolder myViewHolder) {
        myViewHolder.itemView.setBackgroundColor(Color.WHITE);

    }

    @Override
    public void onRowClear(ViewHolder myViewHolder) {
        myViewHolder.itemView.setBackgroundColor(Color.WHITE);

    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView stockName;
        private TextView stockTicker;
        private ImageView icon;
        private TextView price;
        private TextView change;
        private ImageButton btnNext;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            stockName = itemView.findViewById(R.id.stockName);
            stockTicker = itemView.findViewById(R.id.stockTicker);
            price = itemView.findViewById(R.id.stockP);
            change = itemView.findViewById(R.id.stockC);
            icon = itemView.findViewById(R.id.pricetrend);
            btnNext = itemView.findViewById(R.id.btnNext);
        }
    }



}
