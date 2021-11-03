package com.moriawe.smultronstallen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder>  {

    private List<LocationsProvider.LocationClass> locationsList;
    private Context context;
    private final OnClickListItemListener listener;

    public interface OnClickListItemListener {
        void onItemClick(LocationsProvider.LocationClass item);
        void onChatItemClick(LocationsProvider.LocationClass item);
        void onAbsenceItemClick(LocationsProvider.LocationClass item);

    }

    public ListAdapter(Context context, List<LocationsProvider.LocationClass> locationsList, OnClickListItemListener onClickListener) {
        this.context = context;
        this.locationsList = locationsList;
        this.listener = onClickListener;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        ListViewHolder viewHolder = new ListViewHolder(view);
        return viewHolder;
//        return new ListViewHolder(itemView);
//        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent, false);
//        return new ListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ListViewHolder holder, int position) {
        final Integer index = position;

        holder.textViewDate.setText(locationsList.get(position).getDateCreated());
        holder.textViewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //send selected contact in callback
                listener.onAbsenceItemClick(locationsList.get(index));
            }
        });

        holder.textViewName.setText(locationsList.get(position).getName());
        holder.textViewName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //send selected contact in callback
                listener.onChatItemClick(locationsList.get(index));
            }
        });
//        ListItem currentItem = locationsList.get(position);
//        holder.textViewName.setText(currentItem.getTextName());
//        holder.textViewDate.setText(currentItem.getTextDate());
//        holder.textViewImage.setText(currentItem.getTextImage());
//        holder.textViewGeoPoint.setText(currentItem.getTextGeoPoint());
    }

    public LocationsProvider.LocationClass getItem(Integer position){
        return locationsList.get(position);
    }

    public class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView textViewName;
        public TextView textViewDate;
        public TextView textViewImage;
//        public TextView textViewGeoPoint;

        public ListViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewImage = itemView.findViewById(R.id.textViewImage);
//            textViewGeoPoint = itemView.findViewById(R.id.textViewGeoPoint);
            itemView.setOnClickListener(this);
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    //send selected contact in callback
//                    listener.onItemClick(locationsList.get(getAdapterPosition()));
//                }
//            });
        }

        @Override
        public void onClick(View v) {
            if (listener != null){
                int clickedPosition = getAdapterPosition();
                listener.onItemClick(locationsList.get(clickedPosition));
            }
        }
    }


    @Override
    public int getItemCount() {
        return locationsList.size();
    }


    public void filterList(List<LocationsProvider.LocationClass> filteredList) {
        locationsList = filteredList;
        notifyDataSetChanged();
    }


}
