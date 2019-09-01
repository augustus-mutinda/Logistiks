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

import com.paul_karanu.logistiks.Data.Models.FoodieMessage;
import com.paul_karanu.logistiks.R;
import com.paul_karanu.logistiks.Utilities.Constants;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatMessagesAdapter extends FirestoreRecyclerAdapter<FoodieMessage, ChatMessagesAdapter.HomeFragmentViewholder> {

    private Context context;
    private String primaryUserID;

    public ChatMessagesAdapter(Context context, @NonNull FirestoreRecyclerOptions<FoodieMessage> options, String primaryUserID) {
        super(options);
        this.context = context;
        this.primaryUserID = primaryUserID;
    }

    @Override
    protected void onBindViewHolder(@NonNull HomeFragmentViewholder holder, int position, @NonNull FoodieMessage foodieMessage) {
        if (foodieMessage.getPrimaryID().equals(primaryUserID)){
            holder.recLayout.setVisibility(View.GONE);
            holder.sentLayout.setVisibility(View.VISIBLE);
            holder.sentMessage.setText(foodieMessage.getMessage());
            holder.sentTime.setText(getDate(foodieMessage.getDateCreated()));
            switch (foodieMessage.getMessageState()) {
                case Constants.MESSAGEUNSENT:
                    holder.sentStatePic.setImageDrawable(context.getDrawable(R.drawable.ic_message_not_sent));
                    break;
                case Constants.MESSAGESENT:
                    holder.sentStatePic.setImageDrawable(context.getDrawable(R.drawable.ic_message_sent));
                    break;
                case Constants.MESSAGEREAD:
                    holder.sentStatePic.setImageDrawable(context.getDrawable(R.drawable.ic_message_read));
                    break;
            }
        } else {
            holder.sentLayout.setVisibility(View.GONE);
            holder.recLayout.setVisibility(View.VISIBLE);
            holder.recMessage.setText(foodieMessage.getMessage());
            holder.recTime.setText(getDate(foodieMessage.getDateCreated()));
            switch (foodieMessage.getMessageState()) {
                case Constants.MESSAGEUNSENT:
                    holder.sentStatePic.setImageDrawable(context.getDrawable(R.drawable.ic_message_not_sent));
                    break;
                case Constants.MESSAGESENT:
                    holder.sentStatePic.setImageDrawable(context.getDrawable(R.drawable.ic_message_sent));
                    break;
                case Constants.MESSAGEREAD:
                    holder.sentStatePic.setImageDrawable(context.getDrawable(R.drawable.ic_message_read));
                    break;
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private String getDate(Date date){
        return new SimpleDateFormat("HH:mm").format(date);
    }

    @NonNull
    @Override
    public HomeFragmentViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return  new HomeFragmentViewholder(LayoutInflater.from(context).inflate(R.layout.model_chat_message, viewGroup, false));
    }

    class HomeFragmentViewholder extends RecyclerView.ViewHolder {

        ImageView sentStatePic, recStatePic;
        TextView sentMessage, recMessage;
        TextView sentTime, recTime;
        ConstraintLayout sentLayout, recLayout;

        private HomeFragmentViewholder(@NonNull View itemView) {
            super(itemView);

            sentMessage = itemView.findViewById(R.id.modelSentBody);
            recMessage = itemView.findViewById(R.id.modelReceivedMessageBody);
            sentStatePic = itemView.findViewById(R.id.modelSentState);
            recStatePic = itemView.findViewById(R.id.modelReceivedMessageState);
            sentTime = itemView.findViewById(R.id.modelSentTime);
            recTime = itemView.findViewById(R.id.modelReceivedMessageTime);
            sentLayout = itemView.findViewById(R.id.modelSentMessage);
            recLayout = itemView.findViewById(R.id.modelReceivedMessage);
        }
    }
}
