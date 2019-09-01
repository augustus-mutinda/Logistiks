package com.paul_karanu.logistiks.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.paul_karanu.logistiks.Data.Models.Order;
import com.paul_karanu.logistiks.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OrdersAdapter extends FirestoreRecyclerAdapter<Order, OrdersAdapter.HomeFragmentViewholder> {

    private Context context;

    public OrdersAdapter(Context context, @NonNull FirestoreRecyclerOptions<Order> options) {
        super(options);
        this.context = context;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull HomeFragmentViewholder holder, int position, @NonNull Order order) {

        holder.profilePic.setVisibility(View.GONE);
        holder.title.setText("New order for "+order.getFoodName());
        holder.message.setText("From "+order.getBuyerName());
        holder.time.setText(getDate(order.getDateOrdered()));
        holder.statePic.setVisibility(View.GONE);
    }

    @NonNull
    @Override
    public HomeFragmentViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.model_chat, viewGroup, false);
        return new HomeFragmentViewholder(view);
    }

    class HomeFragmentViewholder extends RecyclerView.ViewHolder {

        ImageView profilePic, statePic;
        TextView message;
        TextView title;
        TextView time;
        ConstraintLayout layout;

        private HomeFragmentViewholder(@NonNull View itemView) {
            super(itemView);

            profilePic = itemView.findViewById(R.id.modelChatProfilePicIV);
            statePic = itemView.findViewById(R.id.modelChatStateIV);
            title = itemView.findViewById(R.id.modelChatTitle);
            message = itemView.findViewById(R.id.modelChatSubTitle);
            time = itemView.findViewById(R.id.modelChatTime);
            layout = itemView.findViewById(R.id.modelChat);
        }
    }

    @SuppressLint("SimpleDateFormat")
    private String getDate(Date date){
        return new SimpleDateFormat("HH:mm").format(date);
    }
}
