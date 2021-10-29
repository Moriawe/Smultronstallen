package com.moriawe.smultronstallen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder>  {

    private ArrayList<ListItem> mExampleList;
    private Context context;
    private final OnClickListItemListener listener;

    public class ListViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewName;
        public TextView textViewDate;
        public TextView textViewImage;
        public TextView textViewGeoPoint;

        public ListViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewImage = itemView.findViewById(R.id.textViewImage);
            textViewGeoPoint = itemView.findViewById(R.id.textViewGeoPoint);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //send selected contact in callback
                    listener.onItemClick(mExampleList.get(getAdapterPosition()));
                }
            });
        }
    }
    public ListAdapter(Context context, ArrayList<ListItem> exampleList, OnClickListItemListener listener) {
        this.context = context;
        this.mExampleList = exampleList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent, false);
        return new ListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ListViewHolder holder, int position) {
        ListItem currentItem = mExampleList.get(position);
        holder.textViewName.setText(currentItem.getTextName());
        holder.textViewDate.setText(currentItem.getTextDate());
        holder.textViewImage.setText(currentItem.getTextImage());
        holder.textViewGeoPoint.setText(currentItem.getTextGeoPoint());
    }
    @Override
    public int getItemCount() {
        return mExampleList.size();
    }
    public void filterList(ArrayList<ListItem> filteredList) {
        mExampleList = filteredList;
        notifyDataSetChanged();
    }

    public interface OnClickListItemListener {
        void onItemClick(ListItem item);
    }



}
