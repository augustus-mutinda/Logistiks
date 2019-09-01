package com.paul_karanu.logistiks.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.paul_karanu.logistiks.Adapters.OrdersAdapter;
import com.paul_karanu.logistiks.Data.Models.Order;
import com.paul_karanu.logistiks.R;
import com.paul_karanu.logistiks.Utilities.Constants;
import com.paul_karanu.logistiks.Utilities.RecyclerTouchListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class OrdersFragment extends Fragment {

    String PrimaryID, PrimaryImage, PrimaryName, SecondaryID, SecondaryImage, SecondaryName, orderID, foodID;

    RecyclerView recyclerView;
    OrdersAdapter adapter;

    FirebaseStorage storage;
    FirebaseUser user;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    Query query;
    StorageReference storageReference;
    FirestoreRecyclerOptions<Order> options;

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
        query = firestore.collection(PrimaryID + "Orders").orderBy("DateOrdered", Query.Direction.ASCENDING);
        options = new FirestoreRecyclerOptions.Builder<Order>().setQuery(query, Order.class).build();
        adapter = new OrdersAdapter(getContext(), options);

        recyclerView = v.findViewById(R.id.cartRecyclerView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                loadFoodDetail(view, adapter.getItem(position).getFoodID(), adapter.getItem(position).getOrderID());
                setSecondaryID(adapter.getItem(position).getBuyerID());
                setPrimaryID(user.getUid());
                setOrderID(adapter.getItem(position).getOrderID());
                setFoodID(adapter.getItem(position).getFoodID());
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

    private void loadFoodDetail(final View view, final String foodID, final String orderID) {
        DocumentReference docRef = firestore.collection("Foods").document(foodID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    orderPopUp(view, orderID, Objects.requireNonNull(document.get("Delivery")).toString());
                }
            }
        });
    }

    private void orderPopUp(View view, final String orderID, String deliveryType) {
        PopupMenu popup = new PopupMenu(Objects.requireNonNull(getActivity()), view);

        switch (deliveryType) {
            case "Both":
                popup.inflate(R.menu.seller_order_reserve_dispatch);
                break;
            case "Delivery":
                popup.inflate(R.menu.seller_order_dispatch);
                break;
            case "Pick up":
                popup.inflate(R.menu.seller_order_reserve);
                break;
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.sellerOrderReserve:
                        reserveOrder(orderID);
                        break;
                    case R.id.sellerOrderDispatch:
                        dispatchOrder(orderID);
                        break;
                    case R.id.sellerOrderDecline:
                        declineOrder(orderID);
                        break;
                }
                return false;
            }
        });

        popup.show();
    }

    private void declineOrder(String orderID) {
        setChats("Declined");
        removeOrder(orderID);
    }

    private void dispatchOrder(String orderID) {
        setChats("Dispatched");
        removeOrder(orderID);
    }

    private void reserveOrder(String orderID) {
        setChats("Reserved");
        removeOrder(orderID);
    }

    private void removeOrder(String orderID) {
        firestore.collection(PrimaryID + "Orders").document(orderID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Order completed", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Order completion failed!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setChats(String orderAction) {
        DocumentReference documentReference;
        documentReference = firestore.collection("Users").document(SecondaryID);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    setSecondaryImage(document.getString("ProfilePicture"));
                    setSecondaryName(document.getString("FirstName"));
                }
            }
        });
        documentReference = firestore.collection("Users").document(PrimaryID);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    setPrimaryImage(document.getString("ProfilePicture"));
                    setPrimaryName(document.getString("FirstName"));
                }
            }
        });
        checkIfChatExists(orderAction);
    }

    private void checkIfChatExists(final String orderAction) {
        Toast.makeText(getContext(), PrimaryID, Toast.LENGTH_SHORT).show();
        DocumentReference documentReference = firestore.collection(PrimaryID + "Chats").document(SecondaryID);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        updatePrimaryChat(Objects.requireNonNull(document.get("ChatID")).toString(), orderAction);
                    } else {
                        createChat(orderAction);
                    }
                }
            }
        });
    }

    private void createChat(final String orderAction) {
        Map<String, Object> chat = new HashMap<>();
        chat.put("DateCreated", Calendar.getInstance().getTime());
        chat.put("PrimaryID", PrimaryID);
        chat.put("SecondaryID", SecondaryID);
        firestore.collection("Chats").add(chat).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(final DocumentReference documentReference) {
                documentReference.update("ChatID", documentReference.getId());
                createPrimaryChats(documentReference.getId(), orderAction);
                createSecondaryChat(documentReference.getId(), orderAction);
                dumpNewToMessagePool(documentReference.getId(), orderAction);
            }
        });
    }

    private void createPrimaryChats(final String chatID, String orderAction) {
        Map<String, Object> chat = new HashMap<>();
        switch (orderAction) {
            case "Reserved":
                chat.put("LastMessage", "You reserved an order for: " + orderID);
                break;
            case "Dispatched":
                chat.put("LastMessage", "You dispatched an order for: " + orderID);
                break;
            case "Declined":
                chat.put("LastMessage", "You declined order: " + orderID);
                break;
        }
        chat.put("PrimaryID", PrimaryID);
        chat.put("SecondaryID", SecondaryID);
        chat.put("DateCreated", Calendar.getInstance().getTime());
        chat.put("DateModified", Calendar.getInstance().getTime());
        chat.put("MessageState", Constants.MESSAGEUNSENT);
        chat.put("FoodID", foodID);
        chat.put("ChatID", chatID);
        chat.put("Image", SecondaryImage);
        chat.put("SecondaryName", SecondaryName);
        chat.put("PrimaryName", PrimaryName);
        firestore.collection(PrimaryID + "Chats").document(SecondaryID).set(chat).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                firestore.collection(PrimaryID + "Chats").document(SecondaryID).update("MessageState", Constants.MESSAGESENT);
            }
        });
    }

    private void createSecondaryChat(final String chatID, String orderAction) {
        Map<String, Object> chat = new HashMap<>();
        switch (orderAction) {
            case "Reserved":
                chat.put("LastMessage", "Order " + orderID + " reserved");
                break;
            case "Dispatched":
                chat.put("LastMessage", "Order " + orderID + " dispatched");
                break;
            case "Declined":
                chat.put("LastMessage", "Order " + orderID + " declined");
                break;
        }
        chat.put("PrimaryID", SecondaryID);
        chat.put("SecondaryID", PrimaryID);
        chat.put("DateCreated", Calendar.getInstance().getTime());
        chat.put("DateModified", Calendar.getInstance().getTime());
        chat.put("MessageState", Constants.MESSAGEUNSENT);
        chat.put("FoodID", foodID);
        chat.put("ChatID", chatID);
        chat.put("SecondaryName", PrimaryName);
        chat.put("PrimaryName", SecondaryName);
        chat.put("Image", PrimaryImage);
        firestore.collection(SecondaryID + "Chats").document(PrimaryID).set(chat).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                firestore.collection(SecondaryID + "Chats").document(PrimaryID).update("MessageState", Constants.MESSAGESENT);
            }
        });
    }

    private void dumpNewToMessagePool(final String chatID, String orderAction) {
        Map<String, Object> message = new HashMap<>();
        switch (orderAction) {
            case "Reserved":
                message.put("Message", "Order " + orderID + " reserved");
                break;
            case "Dispatched":
                message.put("Message", "Order " + orderID + " dispatched");
                break;
            case "Declined":
                message.put("Message", "Order " + orderID + " declined");
                break;
        }
        message.put("PrimaryID", PrimaryID);
        message.put("SecondaryID", SecondaryID);
        message.put("Image", "");
        message.put("DateCreated", Calendar.getInstance().getTime());
        message.put("MessageState", Constants.MESSAGEUNSENT);
        firestore.collection(chatID).add(message).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(final DocumentReference documentReference) {
                firestore.collection("Chats").document(chatID).update("MessageState", Constants.MESSAGESENT);
            }
        });
    }

    private void updatePrimaryChat(final String chatID, final String orderAction) {
        Map<String, Object> chat = new HashMap<>();
        switch (orderAction) {
            case "Reserved":
                chat.put("LastMessage", "You reserved an order for: " + orderID);
                break;
            case "Dispatched":
                chat.put("LastMessage", "You dispatched an order for: " + orderID);
                break;
            case "Declined":
                chat.put("LastMessage", "You declined order: " + orderID);
                break;
        }
        chat.put("PrimaryID", PrimaryID);
        chat.put("SecondaryID", SecondaryID);
        chat.put("DateCreated", Calendar.getInstance().getTime());
        chat.put("DateModified", Calendar.getInstance().getTime());
        chat.put("MessageState", Constants.MESSAGEUNSENT);
        chat.put("FoodID", foodID);
        chat.put("ChatID", chatID);
        chat.put("Image", SecondaryImage);
        chat.put("SecondaryName", SecondaryName);
        chat.put("PrimaryName", PrimaryName);
        firestore.collection(PrimaryID + "Chats").document(SecondaryID).set(chat).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                updateSecondaryChat(chatID, orderAction);
                firestore.collection(PrimaryID + "Chats").document(SecondaryID).update("MessageState", Constants.MESSAGESENT);
            }
        });
    }

    private void updateSecondaryChat(final String chatID, final String orderAction) {
        Map<String, Object> chat = new HashMap<>();
        switch (orderAction) {
            case "Reserved":
                chat.put("LastMessage", "Order " + orderID + " reserved");
                break;
            case "Dispatched":
                chat.put("LastMessage", "Order " + orderID + " dispatched");
                break;
            case "Declined":
                chat.put("LastMessage", "Order " + orderID + " declined");
                break;
        }
        chat.put("PrimaryID", SecondaryID);
        chat.put("SecondaryID", PrimaryID);
        chat.put("DateCreated", Calendar.getInstance().getTime());
        chat.put("DateModified", Calendar.getInstance().getTime());
        chat.put("MessageState", Constants.MESSAGEUNSENT);
        chat.put("FoodID", foodID);
        chat.put("ChatID", chatID);
        chat.put("SecondaryName", PrimaryName);
        chat.put("PrimaryName", SecondaryName);
        chat.put("Image", PrimaryImage);
        firestore.collection(SecondaryID + "Chats").document(PrimaryID).set(chat).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dumpToMessagePool(chatID, orderAction);
                firestore.collection(SecondaryID + "Chats").document(PrimaryID).update("MessageState", Constants.MESSAGESENT);
            }
        });
    }

    private void dumpToMessagePool(final String chatID, String orderAction) {
        Map<String, Object> message = new HashMap<>();
        switch (orderAction) {
            case "Reserved":
                message.put("Message", "Order " + orderID + " reserved");
                break;
            case "Dispatched":
                message.put("Message", "Order " + orderID + " dispatched");
                break;
            case "Declined":
                message.put("Message", "Order " + orderID + " declined");
                break;
        }
        message.put("PrimaryID", PrimaryID);
        message.put("SecondaryID", SecondaryID);
        message.put("Image", "");
        message.put("DateCreated", Calendar.getInstance().getTime());
        message.put("MessageState", Constants.MESSAGEUNSENT);
        firestore.collection(chatID).add(message).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(final DocumentReference documentReference) {
                firestore.collection("Chats").document(chatID).update("MessageState", Constants.MESSAGESENT);
            }
        });
    }

    public void setPrimaryID(String primaryID) {
        this.PrimaryID = primaryID;
    }

    public void setPrimaryImage(String primaryImage) {
        this.PrimaryImage = primaryImage;
    }

    public void setPrimaryName(String primaryName) {
        this.PrimaryName = primaryName;
    }

    public void setSecondaryID(String secondaryID) {
        this.SecondaryID = secondaryID;
    }

    public void setSecondaryImage(String secondaryImage) {
        this.SecondaryImage = secondaryImage;
    }

    public void setSecondaryName(String secondaryName) {
        this.SecondaryName = secondaryName;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public void setFoodID(String foodID) {
        this.foodID = foodID;
    }
}
