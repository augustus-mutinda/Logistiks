package com.paul_karanu.logistiks.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.paul_karanu.logistiks.Data.Models.FoodieFirestore;
import com.paul_karanu.logistiks.R;

import java.util.Objects;

public class SellerHomeFragment extends Fragment {

    String primaryID;

    RecyclerView recyclerView;

    FirebaseUser user;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    FirestoreRecyclerAdapter<FoodieFirestore, HomeFragmentViewholder> adapter;
    FirestoreRecyclerOptions<FoodieFirestore> options;
    StorageReference storageRef;
    FirebaseStorage storage;
    Query query;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = getLayoutInflater().inflate(R.layout.fragment_home, container, false);

        recyclerView = v.findViewById(R.id.fragmentHomeRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        assert user != null;
        primaryID = user.getUid();
        firestore = FirebaseFirestore.getInstance();
        query = firestore.collection("Foods").whereEqualTo("UserID", primaryID).orderBy("Name", Query.Direction.ASCENDING);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        options = new FirestoreRecyclerOptions.Builder<FoodieFirestore>().setQuery(query, FoodieFirestore.class).build();
        adapter = new FirestoreRecyclerAdapter<FoodieFirestore, HomeFragmentViewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull HomeFragmentViewholder holder, int position, @NonNull FoodieFirestore model) {
                holder.setName(model.getName());
                holder.setAbout(model.getAbout());
                holder.setPrice(String.valueOf(model.getPrice()));
                holder.setPicture(loadName(model.getPicture()));
            }

            @NonNull
            @Override
            public HomeFragmentViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new HomeFragmentViewholder(LayoutInflater.from(getContext()).inflate(R.layout.model_base_food, viewGroup, false));
            }
        };
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    private StorageReference loadName(String fileName) {
        return storageRef.child("images/" + fileName);
    }

    private class HomeFragmentViewholder extends RecyclerView.ViewHolder {

        View view;

        private HomeFragmentViewholder(@NonNull View itemView) {
            super(itemView);

            this.view = itemView;
        }

        void setName(String name) {
            TextView foodName = view.findViewById(R.id.modelBaseFoodTitle);
            foodName.setText(name);
        }

        void setAbout(String about) {
            TextView aboutValue = itemView.findViewById(R.id.modelBaseFoodAbout);
            aboutValue.setText(about);
        }

        void setPrice(String price) {
            TextView priceValue = itemView.findViewById(R.id.modelBaseFoodPrice);
            priceValue.setText(price);
        }

        void setPicture(StorageReference storageReference) {
            ImageView imageHolder = itemView.findViewById(R.id.modelBaseFoodImage);
            Glide.with(Objects.requireNonNull(getContext())).using(new FirebaseImageLoader()).load(storageReference).into(imageHolder);
        }
    }
}
