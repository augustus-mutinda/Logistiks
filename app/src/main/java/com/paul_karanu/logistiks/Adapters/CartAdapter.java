package com.paul_karanu.logistiks.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.paul_karanu.logistiks.Data.Models.CartItem;
import com.paul_karanu.logistiks.Listeners.CartListener;
import com.paul_karanu.logistiks.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CartAdapter extends FirestoreRecyclerAdapter<CartItem, CartAdapter.CartViewHolder> {

    private Context context;
    private StorageReference storageRef;
    private CartListener listener;

    public CartAdapter(Context context, @NonNull FirestoreRecyclerOptions<CartItem> options, StorageReference storageReference, CartListener listener) {
        super(options);
        this.context = context;
        this.storageRef = storageReference;
        this.listener = listener;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull final CartViewHolder holder, int position, @NonNull CartItem cartItem) {
        Glide.with(context)
                .using(new FirebaseImageLoader())
                .load(loadProfile(cartItem.getPicture()))
                .into(holder.foodPic);
        holder.foodTitle.setText(cartItem.getFoodName());
        holder.foodPrice.setText(cartItem.getPrice());
        holder.time.setText(getDate(cartItem.getDateCreated()));
        switch (cartItem.getDelivery()){
            case "Pick up":
                holder.deliveryTitle.setText("This item is available for pick up only");
                holder.deliverySubTitle.setText("Pick up at "+cartItem.getBuyerLocation());
                break;
            case "Delivery":
                holder.deliveryTitle.setText("This item is available for delivery only");
                holder.deliverySubTitle.setText("Delivery around "+cartItem.getBuyerLocation());
                break;
            case "Both":
                holder.deliveryTitle.setText("This item is available for both pick up and delivery/nChat with seller to agree");
                holder.deliverySubTitle.setText("Delivery/Pick up around "+cartItem.getBuyerLocation());
                break;
        }
        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCancel(holder.getAdapterPosition());
            }
        });
        holder.checkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCheckOut(holder.getAdapterPosition());
            }
        });
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        return new CartViewHolder(LayoutInflater.from(context).inflate(R.layout.model_cart_item, viewGroup, false));
    }

    private StorageReference loadProfile(String image) {
        return storageRef.child("images/" + image);
    }

    class CartViewHolder extends RecyclerView.ViewHolder {

        ImageView foodPic;
        Button checkOut, cancel;
        TextView foodPrice, foodTitle, deliveryTitle, deliverySubTitle, time;

        private CartViewHolder(@NonNull View itemView) {
            super(itemView);
            foodTitle = itemView.findViewById(R.id.modelCartName);
            foodPic = itemView.findViewById(R.id.modelCartImage);
            checkOut = itemView.findViewById(R.id.modelCartCheckOutBtn);
            cancel = itemView.findViewById(R.id.modelCartCancelOutBtn);
            foodPrice = itemView.findViewById(R.id.cartCheckOutSumTotal);
            deliveryTitle = itemView.findViewById(R.id.modelCartDeliveryOptionsTitle);
            deliverySubTitle = itemView.findViewById(R.id.modelCartDeliveryOptionsSubTitle);
            time = itemView.findViewById(R.id.modelCartTime);
        }
    }

    @SuppressLint("SimpleDateFormat")
    private String getDate(Date date){
        return new SimpleDateFormat("HH:mm").format(date);
    }
}
