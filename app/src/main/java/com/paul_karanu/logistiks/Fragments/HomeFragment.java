package com.paul_karanu.logistiks.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.storage.images.FirebaseImageLoader;
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
import com.paul_karanu.logistiks.Activities.ChatActivity;
import com.paul_karanu.logistiks.Activities.SellerActivity;
import com.paul_karanu.logistiks.Adapters.FirestoreHomeRVAdapter;
import com.paul_karanu.logistiks.Data.Models.CartItem;
import com.paul_karanu.logistiks.Data.Models.FoodieFirestore;
import com.paul_karanu.logistiks.Data.Models.Transaction;
import com.paul_karanu.logistiks.R;
import com.paul_karanu.logistiks.Utilities.Constants;
import com.paul_karanu.logistiks.Utilities.RecyclerTouchListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment implements View.OnClickListener {

    String PrimaryID;
    String SellerID;
    String FoodName, FoodImage;
    String PrimaryImage, SecondaryImage, PrimaryName, SecondaryName;

    RecyclerView recyclerView;

    FirestoreHomeRVAdapter adapter;
    CartItem item = new CartItem();

    SharedPreferences preferences;

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore firestore;
    FirestoreRecyclerOptions<FoodieFirestore> options;
    StorageReference storageRef;
    FirebaseStorage storage;
    Query query;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = getLayoutInflater().inflate(R.layout.fragment_home, container, false);

        preferences = Objects.requireNonNull(getActivity()).getSharedPreferences("com.cupcake.foodie", MODE_PRIVATE);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        assert user != null;
        PrimaryID = user.getUid();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        query = firestore.collection("Foods")
                .orderBy("Popularity", Query.Direction.DESCENDING);
        options = new FirestoreRecyclerOptions.Builder<FoodieFirestore>().setQuery(query, FoodieFirestore.class).build();
        adapter = new FirestoreHomeRVAdapter(getContext(), options, storageRef);

        recyclerView = v.findViewById(R.id.fragmentHomeRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                String foodID = adapter.getItem(position).getFoodID();
                String foodSellerID = adapter.getItem(position).getUserID();
                SellerID = foodSellerID;
                FoodName = adapter.getItem(position).getName();
                if (foodSellerID.equals(PrimaryID)) {
                    loadSellerOverflowMenu(view, foodID, foodSellerID);
                } else
                    loadOverflowMenu(view, foodID, foodSellerID);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return v;
    }

    @Override
    public void onClick(View v) {

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

    private void loadSellerOverflowMenu(View view, final String FoodID, final String FoodSellerID) {
        PopupMenu popup = new PopupMenu(Objects.requireNonNull(getActivity()), view);

        popup.inflate(R.menu.base_seller_menu);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu1:
                        loadFoodDialog(FoodID, FoodSellerID);
                        break;
                    case R.id.menu2:
                        preferences.edit().putBoolean(Constants.SELLER, true).apply();
                        startActivity(new Intent(getContext(), SellerActivity.class));
                        Objects.requireNonNull(getActivity()).finish();
                        break;
                }
                return false;
            }
        });

        popup.show();
    }

    private void loadOverflowMenu(View view, final String FoodID, final String FoodSellerID) {
        PopupMenu popup = new PopupMenu(Objects.requireNonNull(getActivity()), view);

        popup.inflate(R.menu.base_buyer_menu);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.baseBuyerMenuPreviewMeal:
                        loadFoodDialog(FoodID, FoodSellerID);
                        break;
                    case R.id.baseBuyerMenuTalkToSeller:
                        Intent intent = new Intent(getContext(), ChatActivity.class);
                        intent.putExtra("FoodID", FoodID);
                        intent.putExtra("SecondaryID", FoodSellerID);
                        startActivity(intent);
                        break;
                    case R.id.baseBuyerMenuAddToCart:
                        loadCartFood(FoodID, FoodSellerID);
                        break;
                    case R.id.baseBuyerMenuPlaceOrder:
                        promptPay(FoodID, FoodSellerID);
                        break;
                }
                return false;
            }
        });

        popup.show();
    }

    private void promptPay(final String foodID, final String sellerID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Please choose a payment option");
        builder.setCancelable(false);
        builder.setMessage("1) Pay on Delivery or Pick up(PDP)\n2) Pay with MPESA\n3) Proceed with MPESA code(PwMC)");
        builder.setNegativeButton("PDP", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                payOnDeliveryOrPickUp(foodID, sellerID);
                dialog.dismiss();
            }
        }).setNeutralButton("Pay with MPESA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                payWithMpesa();
                dialog.dismiss();
            }
        }).setPositiveButton("PwMC", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                payWithMpesaCode(foodID, sellerID);
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void payOnDeliveryOrPickUp(final String FoodID, final String SellerID) {
        getChatDetails(FoodID, SellerID);
        final Transaction transaction = new Transaction();
        transaction.setBuyerProfile(user.getUid());
        transaction.setDateCreated(Calendar.getInstance().getTime());
        transaction.setFoodID(FoodID);
        transaction.setOrderState(Constants.ORDERREGISTERED);
        transaction.setPayWithMpesa(false);
        transaction.setSellerID(SellerID);
        transaction.setBuyerID(user.getUid());
        firestore.collection("Foods").document(FoodID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot snapshot = task.getResult();
                transaction.setFoodName(snapshot.get("Name").toString());
                transaction.setValue(snapshot.get("Price").toString());

                firestore.collection("Transactions").add(transaction).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        DocumentReference reference = task.getResult();
                        reference.update("transactionID", reference.getId());
                        Toast.makeText(getContext(), "Transaction posted", Toast.LENGTH_SHORT).show();
                        getChatDetails(FoodID, SellerID);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to initiate a transaction, check your connection", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Failed to initiate a transaction, check your connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void payWithMpesa() {
        Toast.makeText(getActivity(), "Please chat with the seller and establish payment mode", Toast.LENGTH_SHORT).show();
    }

    private void payWithMpesaCode(final String FoodID, final String SellerID) {
        final AlertDialog mDialog = new AlertDialog.Builder(getContext()).create();
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_confirm_mpesa, null);

        final TextInputEditText code = dialogView.findViewById(R.id.dialogPasswordEDT);
        final TextInputEditText confirmCode = dialogView.findViewById(R.id.dialogConfirmPasswordEDT);
        final Button actionButton = dialogView.findViewById(R.id.dialogConfirmPasswordBtn);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!code.getText().toString().equals(confirmCode.getText().toString())) {
                    Toast.makeText(getContext(), "Codes do not match...", Toast.LENGTH_SHORT).show();
                } else if (code.getText().length() != 10) {
                    Toast.makeText(getContext(), "Codes must be 10 characters", Toast.LENGTH_SHORT).show();
                } else {
                    getChatDetails(FoodID, SellerID);
                    final Transaction transaction = new Transaction();
                    transaction.setBuyerProfile(user.getUid());
                    transaction.setDateCreated(Calendar.getInstance().getTime());
                    transaction.setFoodID(FoodID);
                    transaction.setOrderState(Constants.ORDERREGISTERED);
                    transaction.setPayWithMpesa(false);
                    transaction.setSellerID(SellerID);
                    transaction.setBuyerID(user.getUid());
                    firestore.collection("Foods").document(FoodID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot snapshot = task.getResult();
                            transaction.setFoodName(snapshot.get("Name").toString());
                            transaction.setValue(snapshot.get("Price").toString());

                            firestore.collection("Transactions").add(transaction).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    DocumentReference reference = task.getResult();
                                    reference.update("transactionID", reference.getId());
                                    Toast.makeText(getContext(), "Transaction posted", Toast.LENGTH_SHORT).show();
                                    getChatDetails(FoodID, SellerID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "Failed to initiate a transaction, check your connection", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Failed to initiate a transaction, check your connection", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        mDialog.setView(dialogView);
        mDialog.show();
    }

    @SuppressLint("InflateParams")
    private void loadFoodDialog(final String FoodID, final String SellerID) {
        final AlertDialog dialog = new AlertDialog.Builder(getContext()).create();
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_load_food, null);

        final TextView title = dialogView.findViewById(R.id.dialogFoodTitle);
        final CircleImageView logo = dialogView.findViewById(R.id.dialogFoodImage);
        final ImageView cancel = dialogView.findViewById(R.id.dialogFoodCancel);
        final TextView about = dialogView.findViewById(R.id.dialogFoodAbout);
        final TextView price = dialogView.findViewById(R.id.dialogFoodPrice);
        final TextView deliveries = dialogView.findViewById(R.id.dialogFoodDeliveries);
        final Button cart = dialogView.findViewById(R.id.dialogFoodAddCart);
        final Button order = dialogView.findViewById(R.id.dialogFoodPlaceOrder);
        final LinearLayout layout = dialogView.findViewById(R.id.dialogFoodActionBtns);
        if (SellerID.equals(PrimaryID))
            layout.setVisibility(View.GONE);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadCartFood(FoodID, SellerID);
                dialog.dismiss();
            }
        });
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptPay(FoodID, SellerID);
                dialog.dismiss();
            }
        });
        DocumentReference docRef = firestore.collection("Foods").document(FoodID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    title.setText(Objects.requireNonNull(document.get("Name")).toString());
                    Glide
                            .with(getActivity())
                            .using(new FirebaseImageLoader())
                            .load(loadProfile(Objects.requireNonNull(document.get("Picture")).toString()))
                            .into(logo);
                    about.setText(Objects.requireNonNull(document.get("About")).toString());
                    price.setText(Objects.requireNonNull(document.get("Price")).toString());
                    switch (Objects.requireNonNull(document.getString("Delivery"))) {
                        case "Delivery":
                            deliveries.setText("Available for deliveries only");
                            break;
                        case "Pick up":
                            deliveries.setText("Available for pick up only");
                            break;
                        case "Both":
                            deliveries.setText("Available for deliveries and pick up");
                            break;
                    }
                }
            }
        });
        //dialog.dismiss();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setView(dialogView);
        dialog.show();
    }

    private void loadCartFood(final String FoodID, final String SellerID) {
        item.setSellerID(SellerID);
        item.setBuyerID(PrimaryID);
        DocumentReference docRef = firestore.collection("Foods").document(FoodID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    item.setFoodID(FoodID);
                    item.setFoodName(Objects.requireNonNull(document.get("Name")).toString());
                    item.setPrice(Objects.requireNonNull(document.get("Price")).toString());
                    item.setDelivery(Objects.requireNonNull(document.get("Delivery")).toString());
                    item.setPicture(Objects.requireNonNull(document.get("Picture")).toString());
                    loadCartBuyer(FoodID, SellerID);
                }
            }
        });
    }

    private void loadCartBuyer(final String FoodID, final String SellerID) {
        DocumentReference docRef = firestore.collection("Users").document(PrimaryID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    item.setBuyerLocation(Objects.requireNonNull(document.get("Location")).toString());
                    loadCartSeller(FoodID, SellerID);
                }
            }
        });
    }

    private void loadCartSeller(final String FoodID, final String SellerID) {
        DocumentReference docRef = firestore.collection("Users").document(SellerID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    item.setSellerLocation(Objects.requireNonNull(document.get("Location")).toString());
                    item.setSellerID(SellerID);
                    loadCart(FoodID);
                }
            }
        });
    }

    private void loadCart(String FoodID) {
        Map<String, Object> cart = new HashMap<>();
        cart.put("FoodName", item.getFoodName());
        cart.put("FoodID", FoodID);
        cart.put("SellerID", item.getSellerID());
        cart.put("BuyerID", item.getBuyerID());
        cart.put("DateCreated", Calendar.getInstance().getTime());
        cart.put("Price", item.getPrice());
        cart.put("Delivery", item.getDelivery());
        cart.put("BuyerLocation", item.getBuyerLocation());
        cart.put("SellerLocation", item.getSellerLocation());
        cart.put("Picture", item.getPicture());
        firestore.collection(PrimaryID + "Cart").add(cart)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getContext(), "Added to cart...", Toast.LENGTH_SHORT).show();
                        documentReference.update("CartID", documentReference.getId());
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "!Not added... try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private StorageReference loadProfile(String image) {
        return storageRef.child("images/" + image);
    }

    private void getChatDetails(final String foodID, String sellerID) {
        DocumentReference documentReference;
        documentReference = firestore.collection("Users").document(sellerID);
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
        documentReference = firestore.collection("Foods").document(foodID);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    setFoodImage(document.getString("Picture"));
                    setFoodName(document.getString("Name"));
                    placeOrder(foodID, SellerID);
                }
            }
        });
    }

    private void placeOrder(final String FoodID, final String SellerID) {
        Map<String, Object> order = new HashMap<>();
        order.put("BuyerID", PrimaryID);
        order.put("BuyerName", PrimaryName);
        order.put("SellerID", SellerID);
        order.put("FoodID", FoodID);
        order.put("FoodName", FoodName);
        order.put("DateOrdered", Calendar.getInstance().getTime());
        order.put("OrderStatus", Constants.ORDERREGISTERED);
        firestore.collection("Orders").add(order)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        documentReference.update("OrderID", documentReference.getId());
                        placeSellerOrder(FoodID, SellerID, documentReference.getId());
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Order placing failed... Please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void placeSellerOrder(final String foodID, String sellerID, final String orderID) {
        final Map<String, Object> order = new HashMap<>();
        order.put("BuyerID", PrimaryID);
        order.put("BuyerName", PrimaryName);
        order.put("SellerID", SellerID);
        order.put("FoodID", foodID);
        order.put("FoodName", FoodName);
        order.put("DateOrdered", Calendar.getInstance().getTime());
        order.put("OrderStatus", Constants.ORDERREGISTERED);
        firestore.collection(sellerID + "Orders").add(order)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        documentReference.update("OrderID", documentReference.getId());
                        Toast.makeText(getContext(), "Order placed", Toast.LENGTH_SHORT).show();
                        checkIfChatExists(orderID, foodID);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Order placing failed... Please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkIfChatExists(final String orderID, final String foodID) {
        DocumentReference documentReference = firestore.collection(PrimaryID + "Chats").document(SellerID);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        updatePrimaryChat(Objects.requireNonNull(document.get("ChatID")).toString(), foodID, orderID);
                    } else {
                        createChat(orderID, foodID);
                    }
                }
            }
        });
    }

    private void createChat(final String orderID, final String foodID) {
        Map<String, Object> chat = new HashMap<>();
        chat.put("DateCreated", Calendar.getInstance().getTime());
        chat.put("PrimaryID", PrimaryID);
        chat.put("SecondaryID", SellerID);
        firestore.collection("Chats").add(chat).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(final DocumentReference documentReference) {
                documentReference.update("ChatID", documentReference.getId());
                createPrimaryChats(documentReference.getId(), foodID);
                createSecondaryChat(documentReference.getId(), foodID);
                dumpNewToMessagePool(documentReference.getId(), orderID);
            }
        });
    }

    private void createPrimaryChats(final String chatID, String foodID) {
        Map<String, Object> chat = new HashMap<>();
        chat.put("PrimaryID", PrimaryID);
        chat.put("SecondaryID", SellerID);
        chat.put("LastMessage", "You placed an order for " + FoodName);
        chat.put("DateCreated", Calendar.getInstance().getTime());
        chat.put("DateModified", Calendar.getInstance().getTime());
        chat.put("MessageState", Constants.MESSAGEUNSENT);
        chat.put("FoodID", foodID);
        chat.put("ChatID", chatID);
        chat.put("Image", SecondaryImage);
        chat.put("SecondaryName", SecondaryName);
        chat.put("PrimaryName", PrimaryName);
        firestore.collection(PrimaryID + "Chats").document(SellerID).set(chat).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                firestore.collection(PrimaryID + "Chats").document(SellerID).update("MessageState", Constants.MESSAGESENT);
            }
        });
    }

    private void createSecondaryChat(final String chatID, String foodID) {
        Map<String, Object> chat = new HashMap<>();
        chat.put("PrimaryID", SellerID);
        chat.put("SecondaryID", PrimaryID);
        chat.put("LastMessage", "You have a new order for " + FoodName);
        chat.put("DateCreated", Calendar.getInstance().getTime());
        chat.put("DateModified", Calendar.getInstance().getTime());
        chat.put("MessageState", Constants.MESSAGEUNSENT);
        chat.put("FoodID", foodID);
        chat.put("ChatID", chatID);
        chat.put("SecondaryName", PrimaryName);
        chat.put("PrimaryName", SecondaryName);
        chat.put("Image", PrimaryImage);
        firestore.collection(SellerID + "Chats").document(PrimaryID).set(chat).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                firestore.collection(SellerID + "Chats").document(PrimaryID).update("MessageState", Constants.MESSAGESENT);
            }
        });
    }

    private void dumpNewToMessagePool(final String chatID, String orderID) {
        Map<String, Object> message = new HashMap<>();
        message.put("PrimaryID", PrimaryID);
        message.put("SecondaryID", SellerID);
        message.put("Message", "New order, orderID: " + orderID);
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

    private void updatePrimaryChat(final String chatID, final String foodID, final String orderID) {
        Map<String, Object> chat = new HashMap<>();
        chat.put("PrimaryID", PrimaryID);
        chat.put("SecondaryID", SellerID);
        chat.put("LastMessage", "You placed an order for " + FoodName);
        chat.put("DateCreated", Calendar.getInstance().getTime());
        chat.put("DateModified", Calendar.getInstance().getTime());
        chat.put("MessageState", Constants.MESSAGEUNSENT);
        chat.put("FoodID", foodID);
        chat.put("ChatID", chatID);
        chat.put("Image", SecondaryImage);
        chat.put("SecondaryName", SecondaryName);
        chat.put("PrimaryName", PrimaryName);
        firestore.collection(PrimaryID + "Chats").document(SellerID).set(chat).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                updateSecondaryChat(chatID, foodID, orderID);
                firestore.collection(PrimaryID + "Chats").document(SellerID).update("MessageState", Constants.MESSAGESENT);
            }
        });
    }

    private void updateSecondaryChat(final String chatID, String foodID, final String orderID) {
        Map<String, Object> chat = new HashMap<>();
        chat.put("PrimaryID", SellerID);
        chat.put("SecondaryID", PrimaryID);
        chat.put("LastMessage", "You have a new order for " + FoodName);
        chat.put("DateCreated", Calendar.getInstance().getTime());
        chat.put("DateModified", Calendar.getInstance().getTime());
        chat.put("MessageState", Constants.MESSAGEUNSENT);
        chat.put("FoodID", foodID);
        chat.put("ChatID", chatID);
        chat.put("SecondaryName", PrimaryName);
        chat.put("PrimaryName", SecondaryName);
        chat.put("Image", PrimaryImage);
        firestore.collection(SellerID + "Chats").document(PrimaryID).set(chat).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dumpToMessagePool(chatID, orderID);
                firestore.collection(SellerID + "Chats").document(PrimaryID).update("MessageState", Constants.MESSAGESENT);
            }
        });
    }

    private void dumpToMessagePool(final String chatID, String orderID) {
        Map<String, Object> message = new HashMap<>();
        message.put("PrimaryID", PrimaryID);
        message.put("SecondaryID", SellerID);
        message.put("Message", "New order, orderID: " + orderID);
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

    private void setPrimaryName(String name) {
        this.PrimaryName = name;
    }

    private void setSecondaryName(String name) {
        this.SecondaryName = name;
    }

    private void setPrimaryImage(String name) {
        this.PrimaryImage = name;
    }

    private void setSecondaryImage(String name) {
        this.SecondaryImage = name;
    }

    public void setFoodName(String foodName) {
        this.FoodName = foodName;
    }

    public void setFoodImage(String foodImage) {
        this.FoodName = foodImage;
    }
}