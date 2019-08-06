package com.crucialtech.travelassistant;

import android.content.Context;
import android.content.Intent;
import android.os.Trace;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class DealsAdapter extends RecyclerView.Adapter<DealsAdapter.DealViewHolder> {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;
    ArrayList<TravelDeal> mTravelDeals;


    public DealsAdapter(){

        //UtilityClass.openFirebaseReference("traveldeals");
        mFirebaseDatabase = UtilityClass.mFirebaseDatabase;
        mDatabaseReference = UtilityClass.mDatabaseReference;
        mTravelDeals = UtilityClass.mTravelDeals;
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                TravelDeal travelDeal = dataSnapshot.getValue(TravelDeal.class);
                if (travelDeal != null) {
                    travelDeal.setId(dataSnapshot.getKey());
                }
                notifyItemInserted(mTravelDeals.size() - 1);
                mTravelDeals.add(travelDeal);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabaseReference.addChildEventListener(mChildEventListener);
    }

    @NonNull
    @Override
    public DealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.deals_layout,parent,false);
        return new DealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DealViewHolder holder, int position) {
        TravelDeal deal = mTravelDeals.get(position);
        holder.bind(deal);
    }

    @Override
    public int getItemCount() {
        return mTravelDeals.size();
    }

    public class DealViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_name,tv_price,tv_description;
        ImageView dealImage;
        public DealViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_deal_name);
            tv_description = itemView.findViewById(R.id.tv_deal_description);
            tv_price = itemView.findViewById(R.id.tv_deal_price);
            itemView.setOnClickListener(this);
        }

        public void bind(TravelDeal deal){
            tv_name.setText(deal.getTitle());
            tv_description.setText(deal.getDescription());
            tv_price.setText(deal.getPrice());
        }

        @Override
        public void onClick(View v) {
            int postion = getAdapterPosition();
            TravelDeal dealselected = mTravelDeals.get(postion);
            Intent intent = new Intent(v.getContext(),AdminActivity.class);
            intent.putExtra("Deal",dealselected);
            v.getContext().startActivity(intent);

        }
    }
}
