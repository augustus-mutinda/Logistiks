package com.paul_karanu.logistiks.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.paul_karanu.logistiks.Data.Models.FoodieFirestore;
import com.paul_karanu.logistiks.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

public class FirestoreHomeRVAdapter extends FirestoreRecyclerAdapter<FoodieFirestore, FirestoreHomeRVAdapter.HomeFragmentViewholder> {

    private Context context;
    private StorageReference storageRef;

    public FirestoreHomeRVAdapter(Context context, @NonNull FirestoreRecyclerOptions<FoodieFirestore> options, StorageReference storageReference) {
        super(options);
        this.context = context;
        this.storageRef = storageReference;
    }

    @Override
    protected void onBindViewHolder(@NonNull HomeFragmentViewholder holder, int position, @NonNull FoodieFirestore model) {
        holder.foodName.setText(model.getName());
        holder.aboutValue.setText(model.getAbout());
        holder.priceValue.setText(String.valueOf(model.getPrice()));
        Glide.with(context)
                .using(new FirebaseImageLoader())
                .load(loadName(model.getPicture()))
                .into(holder.imageHolder);
        switch (model.getRating()) {
            case 0: {
                holder.star1.setVisibility(View.GONE);
                holder.star2.setVisibility(View.GONE);
                holder.star3.setVisibility(View.GONE);
                holder.star4.setVisibility(View.GONE);
                holder.star5.setVisibility(View.GONE);
            }
            break;
            case 1: {
                holder.star1.setVisibility(View.VISIBLE);
                holder.star2.setVisibility(View.GONE);
                holder.star3.setVisibility(View.GONE);
                holder.star4.setVisibility(View.GONE);
                holder.star5.setVisibility(View.GONE);
            }
            break;
            case 2: {
                holder.star1.setVisibility(View.VISIBLE);
                holder.star2.setVisibility(View.VISIBLE);
                holder.star3.setVisibility(View.GONE);
                holder.star4.setVisibility(View.GONE);
                holder.star5.setVisibility(View.GONE);
            }
            break;
            case 3: {
                holder.star1.setVisibility(View.VISIBLE);
                holder.star2.setVisibility(View.VISIBLE);
                holder.star3.setVisibility(View.VISIBLE);
                holder.star4.setVisibility(View.GONE);
                holder.star5.setVisibility(View.GONE);
            }
            break;
            case 4: {
                holder.star1.setVisibility(View.VISIBLE);
                holder.star2.setVisibility(View.VISIBLE);
                holder.star3.setVisibility(View.VISIBLE);
                holder.star4.setVisibility(View.VISIBLE);
                holder.star5.setVisibility(View.GONE);
            }
            break;
            case 5: {
                holder.star1.setVisibility(View.VISIBLE);
                holder.star2.setVisibility(View.VISIBLE);
                holder.star3.setVisibility(View.VISIBLE);
                holder.star4.setVisibility(View.VISIBLE);
                holder.star5.setVisibility(View.VISIBLE);
            }
            break;
        }
    }

    @NonNull
    @Override
    public HomeFragmentViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.model_base_food, viewGroup, false);

        return new HomeFragmentViewholder(view);
    }

    private StorageReference loadName(String fileName) {
        return storageRef.child("images/" + fileName);
    }

    class HomeFragmentViewholder extends RecyclerView.ViewHolder {

        ImageView moreMenu, star1, star2, star3, star4, star5, imageHolder;
        TextView foodName;
        TextView aboutValue;
        TextView priceValue;

        private HomeFragmentViewholder(@NonNull View itemView) {
            super(itemView);

            star1 = itemView.findViewById(R.id.modelBaseFoodStar1);
            star2 = itemView.findViewById(R.id.modelBaseFoodStar2);
            star3 = itemView.findViewById(R.id.modelBaseFoodStar3);
            star4 = itemView.findViewById(R.id.modelBaseFoodStar4);
            star5 = itemView.findViewById(R.id.modelBaseFoodStar5);
            imageHolder = itemView.findViewById(R.id.modelBaseFoodImage);
            moreMenu = itemView.findViewById(R.id.modelBaseFoodMenuImage);
            foodName = itemView.findViewById(R.id.modelBaseFoodTitle);
            aboutValue = itemView.findViewById(R.id.modelBaseFoodAbout);
            priceValue = itemView.findViewById(R.id.modelBaseFoodPrice);
        }
    }
}
