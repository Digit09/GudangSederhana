package com.example.gudangsederhana;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GoodsAdapter extends RecyclerView.Adapter<GoodsAdapter.MyViewHolder> {

    Context context;
    ArrayList<Goods> list;
    SpannableString str;
    ForegroundColorSpan greenC = new ForegroundColorSpan(Color.GREEN);

    public GoodsAdapter(Context context, ArrayList<Goods> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.recyclerview_main_item,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Goods goods = list.get(position);
        holder.id = goods.getId();
        if (MenuSearch.lengthStrSearch > 0) {
            // Change Substring Color
            str = new SpannableString(goods.getName());
            str.setSpan(greenC, 0, MenuSearch.lengthStrSearch, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.nama.setText(str);
        } else {
            holder.nama.setText(goods.getName());
        }
        holder.produsen.setText(" " + goods.getProducer());
        holder.harga.setText(" " + MainActivity.rupiahkan(goods.getPrice()));
        holder.rlItem.setOnClickListener(v -> {
            //InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            //imm.hideSoftInputFromWindow(holder.edSearch.getWindowToken(), 0);
            Intent intent = new Intent(context, MenuSearchDetail.class);
            intent.putExtra("result", holder.id);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rlItem;
        String id;
        TextView nama, produsen, harga;
        EditText edSearch;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            id = null;
            rlItem = itemView.findViewById(R.id.rlItem_mi);
            nama = itemView.findViewById(R.id.tvNamaBarang_item);
            produsen = itemView.findViewById(R.id.tvProdusen_item);
            harga = itemView.findViewById(R.id.tvHarga_item);
            edSearch = itemView.findViewById(R.id.edSearch);
        }
    }
}
