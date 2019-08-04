package com.charlyge.android.mytavelmantics.Adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.charlyge.android.mytavelmantics.Model.TravelMantics;
import com.charlyge.android.mytavelmantics.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TravelManticsAdapter extends RecyclerView.Adapter<TravelManticsAdapter.TravelViewHolder>{
    private List<TravelMantics> travelManticsList;

    @NonNull
    @Override
    public TravelViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.holiday_deal_list_item,viewGroup,false);
        return new TravelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TravelViewHolder travelViewHolder, int i) {
           TravelMantics travelMantics = travelManticsList.get(travelViewHolder.getAdapterPosition());
             travelViewHolder.tvdealTitle.setText(travelMantics.getDealTitle());
             travelViewHolder.tvprice.setText(travelMantics.getPrice());
             travelViewHolder.tvdealBody.setText(travelMantics.getDealBody());
        Picasso.get().load(travelMantics.getDealImageUrl()).placeholder(R.drawable.ic_loading).centerInside().into(travelViewHolder.dealImageView);


    }

    @Override
    public int getItemCount() {
        if(travelManticsList==null){
            return 0;
        }
        return travelManticsList.size();
    }


    public class TravelViewHolder extends RecyclerView.ViewHolder {
        private TextView tvdealTitle,tvdealBody,tvprice;
        private ImageView dealImageView;

        public TravelViewHolder(View itemView){
            super(itemView);
            tvdealBody = itemView.findViewById(R.id.li_dealBody);
            tvprice = itemView.findViewById(R.id.li_dealprice);
            tvdealTitle = itemView.findViewById(R.id.li_dealTitle);
            dealImageView = itemView.findViewById(R.id.deal_image);
        }
    }

    public void setTravelManticsList(List<TravelMantics> travelManticsList) {
        this.travelManticsList = travelManticsList;
        notifyDataSetChanged();
    }
}
