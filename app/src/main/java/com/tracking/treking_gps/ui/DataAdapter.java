package com.tracking.treking_gps.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tracking.treking_gps.R;

import java.util.ArrayList;

public class DataAdapter extends RecyclerView.Adapter<ViewHolder> {

    ArrayList<String> messages;
    LayoutInflater inflater;

    public DataAdapter(Context context,ArrayList<String> messages) {
        this.messages = messages;
        this.inflater=LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=inflater.inflate(R.layout.item_massage, parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String mag=messages.get(position);
        holder.message.setText(mag);

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
