package com.moriawe.smultronstallen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder>  {
    private int expandedPosition = -1;
    private List<LocationsProvider.LocationClass> locationsList;
    private Context context;
    private final OnClickListItemListener listener;

    public interface OnClickListItemListener {
        void onItemClick(LocationsProvider.LocationClass item);
        void onSmultronImageClick(LocationsProvider.LocationClass item);
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
        holder.textViewName.setText(locationsList.get(position).getName());
        holder.textViewAddedBy.setText(locationsList.get(position).getAddedBy());
        holder.textViewDate.setText(locationsList.get(position).getDateCreated());

//        holder.imageViewGoToSmultron.setText(locationsList.get(position).getDateCreated());
        holder.imageViewGoToSmultron.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //send selected contact in callback
                listener.onSmultronImageClick(locationsList.get(index));
            }
        });


        holder.textViewLimitedComment.setText(cutStringAddDots(locationsList.get(position).getComment(), 50));
        if (locationsList.get(position).getComment().length() > 50) {
            holder.textViewLimitedComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int clickedPosition = index;
                        listener.onItemClick(locationsList.get(clickedPosition));
                        if (expandedPosition >= 0) {
                            int prev = expandedPosition;
                            notifyItemChanged(prev);
                        }
                        expandedPosition = clickedPosition;
                        notifyItemChanged(expandedPosition);
                    }
                }
            });
        }


        holder.textViewFullComment.setText(locationsList.get(position).getComment());
        if (position == expandedPosition) {
            holder.textViewLimitedComment.setVisibility(View.GONE);
            holder.textViewFullComment.setVisibility(View.VISIBLE);
        } else {
            holder.textViewLimitedComment.setVisibility(View.VISIBLE);
            holder.textViewFullComment.setVisibility(View.GONE);
        }




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
        public TextView textViewAddedBy;
        public TextView textViewDate;
        public ImageView imageViewGoToSmultron;
        public TextView textViewLimitedComment;
        public TextView textViewFullComment;

//        public TextView textViewGeoPoint;

        public ListViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewAddedBy = itemView.findViewById(R.id.textViewAddedBy);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            imageViewGoToSmultron = itemView.findViewById(R.id.goToSmultron);
            textViewLimitedComment = itemView.findViewById(R.id.textViewLimitedComment);
            textViewFullComment = itemView.findViewById(R.id.textViewFullComment);
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

//        @Override
//        public void onClick(View v) {
//            if (listener != null){
//                int clickedPosition = getAdapterPosition();
//                listener.onItemClick(locationsList.get(clickedPosition));
//                if (expandedPosition >= 0) {
//                    int prev = expandedPosition;
//                    notifyItemChanged(prev);
//                }
//                expandedPosition = clickedPosition;
//                notifyItemChanged(expandedPosition);
//            }
//        }


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

    public String cutStringAddDots (String text, Integer max) {
        if (text == null)
            return "";
        if (text.length() <= max)
            return text;
        if (max <= 5)
            return text.substring(0, max);

        return "▼ " + text.substring(0, max - 2) + "...";
    }


}
