package com.paul_karanu.logistiks.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.paul_karanu.logistiks.Adapters.TransactionsAdapter;
import com.paul_karanu.logistiks.Data.Models.Transaction;
import com.paul_karanu.logistiks.R;
import com.paul_karanu.logistiks.Utilities.RecyclerTouchListener;

public class TransactionsFragment extends Fragment {

    String PrimaryID;

    RecyclerView recyclerView;
    TransactionsAdapter adapter;

    FirebaseStorage storage;
    FirebaseUser user;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    Query query;
    StorageReference storageReference;
    FirestoreRecyclerOptions<Transaction> options;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = getLayoutInflater().inflate(R.layout.fragment_cart, container, false);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        assert user != null;
        PrimaryID = user.getUid();
        firestore = FirebaseFirestore.getInstance();
        query = firestore.collection("Transactions").whereEqualTo("sellerID", user.getUid()).orderBy("dateCreated", Query.Direction.ASCENDING);
        options = new FirestoreRecyclerOptions.Builder<Transaction>().setQuery(query, Transaction.class).build();
        adapter = new TransactionsAdapter(getContext(), options);

        recyclerView = v.findViewById(R.id.cartRecyclerView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                loadTransaction(adapter.getItem(position));
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

    private void loadTransaction(Transaction transaction) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Order ID: " + transaction.getOrderID() + " for " + transaction.getFoodName());
        if (transaction.isPayWithMpesa()) {
            builder.setMessage("Cost: " + transaction.getValue() + ", paid via MPESA, code: " + transaction.getMpesaCode());
        } else {
            builder.setMessage("Cost: " + transaction.getValue() + ", paid on pickup or delivery");
        }
        builder.setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }
}
