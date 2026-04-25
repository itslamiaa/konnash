package com.example.konnashapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.ViewHolder> {

    private List<Entry> entryList;

    public EntryAdapter(List<Entry> entryList) {
        this.entryList = entryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_entry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Entry entry = entryList.get(position);
        holder.tvTitle.setText(entry.getTitle());

        String amountText;
        if (entry.isExpense()) {
            amountText = "- " + String.format("%.2f", entry.getAmount());
            holder.tvAmount.setTextColor(holder.itemView.getContext().getColor(android.R.color.holo_red_dark));
        } else {
            amountText = "+ " + String.format("%.2f", entry.getAmount());
            holder.tvAmount.setTextColor(holder.itemView.getContext().getColor(android.R.color.holo_green_dark));
        }

        holder.tvAmount.setText(amountText);
    }

    @Override
    public int getItemCount() {
        return entryList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvAmount;

        ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvAmount = itemView.findViewById(R.id.tv_amount);
        }
    }
}