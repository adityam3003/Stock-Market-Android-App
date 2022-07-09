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

import java.util.ArrayList;
import java.util.Collections;

public class PfAdapter extends RecyclerView.Adapter<PfAdapter.ViewHolder> implements DragAndDrop1.ItemTouchHelperContract, SharedPreferences.OnSharedPreferenceChangeListener {

    private ArrayList<MyStocks> pfStocks = new ArrayList<>();
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    Context c;
    public PfAdapter(Context c) {
        this.c = c;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pf_layout,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.stockName.setText(pfStocks.get(position).getSymbol());
        holder.quantity.setText(Integer.toString(pfStocks.get(position).getQuantity())+" Shares");
        holder.price.setText("$"+String.valueOf(String.format("%.2f", Float.parseFloat(pfStocks.get(position).getChange())*pfStocks.get(position).getQuantity())));
        float totalPrice = (pfStocks.get(position).getQuantity())*pfStocks.get(position).getPrice();
        float changePrice = (Float.parseFloat(pfStocks.get(position).getChange())-pfStocks.get(position).getPrice())*pfStocks.get(position).getQuantity();
        holder.change.setText("$"+String.valueOf(String.format("%.2f",(changePrice)))+" ("+String.valueOf((String.format("%.2f",((changePrice)*100/totalPrice))))+"%)");
        if(changePrice>0){
            holder.priceIcon.setImageResource(R.drawable.trending_up);
            holder.change.setTextColor(ContextCompat.getColor(holder.change.getContext(), R.color.stock_green));
        }
        else if(changePrice<0){
            holder.priceIcon.setImageResource(R.drawable.trending_down);
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

    @Override
    public int getItemCount() {
        return pfStocks.size();
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
//        String json1 = gson.toJson(pfStocks);
//        ed.putString("PortFolio", json1);
//        ed.apply();
//        MainActivity m = new MainActivity();
//        m.setPref(context,pfStocks);

        notifyItemMoved(fromPosition,toPosition);




    }
    @Override
    public void onRowSelected(PfAdapter.ViewHolder myViewHolder) {
        myViewHolder.itemView.setBackgroundColor(Color.WHITE);

    }

    @Override
    public void onRowClear(PfAdapter.ViewHolder myViewHolder) {
        myViewHolder.itemView.setBackgroundColor(Color.WHITE);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        MainActivity m = new MainActivity();
        m.onSharedPreferenceChanged(sharedPreferences,s);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView stockName;
        private TextView stockTicker;
        private TextView quantity;
        private TextView price;
        private ImageView priceIcon;
        private TextView change;
        private ImageButton btnNext;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            change = itemView.findViewById(R.id.stockC);
            stockName = itemView.findViewById(R.id.stockName);
            stockTicker = itemView.findViewById(R.id.stockTicker);
            quantity = itemView.findViewById(R.id.stockQ);
            price = itemView.findViewById(R.id.stockP);
            priceIcon = itemView.findViewById(R.id.pricetrend);
            btnNext = itemView.findViewById(R.id.btnNext);
        }

    }
}
