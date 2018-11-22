package com.fpt.customkeyboard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class HintAdapter extends RecyclerView.Adapter<HintAdapter.ViewHolder> {
    private Context mContext;
    private List<String> data;
    private OnItemClickListener listener;

    public HintAdapter() {
        data = new ArrayList<>();
    }

    public void setNewData(List<String> list){
        data.clear();
        if (list != null){
            data.addAll(list);
        }
        notifyDataSetChanged();
    }

    public List<String> getData() {
        return data;
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (mContext == null){
            mContext = viewGroup.getContext();
        }
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_hint,viewGroup,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int i) {
        holder.tv_hint.setText(data.get(i));
        holder.tv_hint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null){
                    listener.onItemClick(HintAdapter.this,v,i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_hint;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_hint = itemView.findViewById(R.id.tv_hint);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(RecyclerView.Adapter adapter, View view, int position);
    }

}
