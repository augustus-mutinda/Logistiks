package com.paul_karanu.logistiks.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.paul_karanu.logistiks.Activities.ChatActivity;
import com.paul_karanu.logistiks.Adapters.FirestoreChatRVAdapter;
import com.paul_karanu.logistiks.Data.Models.FoodieChat;
import com.paul_karanu.logistiks.R;
import com.paul_karanu.logistiks.Utilities.RecyclerTouchListener;

public class ChatsFragment extends Fragment {

    RecyclerView recyclerView;
    FirestoreChatRVAdapter adapter;

    FirebaseStorage storage;
    FirebaseUser user;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    Query query;
    StorageReference storageReference;
    FirestoreRecyclerOptions<FoodieChat> options;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = getLayoutInflater().inflate(R.layout.fragment_chats, container, false);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        query = firestore.collection(user.getUid() + "Chats").orderBy("DateCreated", Query.Direction.ASCENDING);
        options = new FirestoreRecyclerOptions.Builder<FoodieChat>().setQuery(query, FoodieChat.class).build();
        adapter = new FirestoreChatRVAdapter(getContext(), options, storageReference);

        recyclerView = v.findViewById(R.id.chatsRV);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra("FoodID", adapter.getItem(position).getFoodID());
                intent.putExtra("SecondaryID", adapter.getItem(position).getSecondaryID());
                intent.putExtra("PrimaryName", adapter.getItem(position).getPrimaryName());
                intent.putExtra("ChatID", adapter.getItem(position).getChatID());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

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
}
