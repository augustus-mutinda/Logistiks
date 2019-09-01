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

import com.bumptech.glide.Glide;
import com.paul_karanu.logistiks.Data.Models.FoodieChat;
import com.paul_karanu.logistiks.R;
import com.paul_karanu.logistiks.Utilities.Constants;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FirestoreChatRVAdapter extends FirestoreRecyclerAdapter<FoodieChat, FirestoreChatRVAdapter.HomeFragmentViewholder> {

    private Context context;
    private StorageReference storageRef;

    public FirestoreChatRVAdapter(Context context, @NonNull FirestoreRecyclerOptions<FoodieChat> options, StorageReference storageReference) {
        super(options);
        this.context = context;
        this.storageRef = storageReference;
    }

    @Override
    protected void onBindViewHolder(@NonNull HomeFragmentViewholder holder, int position, @NonNull FoodieChat foodieChat) {

        Glide.with(context).using(new FirebaseImageLoader())
                .load(loadProfile(foodieChat.getImage()))
                .into(holder.profilePic);

        switch (foodieChat.getMessageState()){
            case Constants.MESSAGEUNSENT:
                holder.statePic.setImageDrawable(context.getDrawable(R.drawable.ic_message_not_sent));
                break;
            case Constants.MESSAGESENT:
                holder.statePic.setImageDrawable(context.getDrawable(R.drawable.ic_message_sent));
                break;
            case Constants.MESSAGEREAD:
                holder.statePic.setImageDrawable(context.getDrawable(R.drawable.ic_message_read));
                break;
        }
        holder.title.setText(foodieChat.getSecondaryName());
        holder.message.setText(foodieChat.getLastMessage());
        holder.time.setText(getDate(foodieChat.getDateModified()));
    }

    @NonNull
    @Override
    public HomeFragmentViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.model_chat, viewGroup, false);

        return new HomeFragmentViewholder(view);
    }

    private StorageReference loadProfile(String image) {
        return storageRef.child("users/" + image);
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
