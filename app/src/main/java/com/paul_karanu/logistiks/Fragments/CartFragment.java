package com.paul_karanu.logistiks.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.paul_karanu.logistiks.Adapters.CartAdapter;
import com.paul_karanu.logistiks.Data.Models.CartItem;
import com.paul_karanu.logistiks.Data.Models.Transaction;
import com.paul_karanu.logistiks.Listeners.CartListener;
import com.paul_karanu.logistiks.R;
import com.paul_karanu.logistiks.Utilities.Constants;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CartFragment extends Fragment implements CartListener {

    String PrimaryName, PrimaryID, PrimaryImage, SecondaryImage, SecondaryName, SellerID, FoodName;

    RecyclerView recyclerView;
    CartAdapter adapter;

    FirebaseStorage storage;
    FirebaseUser user;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    Query query;
    StorageReference storageReference;
    FirestoreRecyclerOptions<CartItem> options;

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
        query = firestore.collection(user.getUid() + "Cart").orderBy("DateCreated", Query.Direction.ASCENDING);
        options = new FirestoreRecyclerOptions.Builder<CartItem>().setQuery(query, CartItem.class).build();
        adapter = new CartAdapter(getContext(), options, storageReference, this);

        recyclerView = v.findViewById(R.id.cartRecyclerView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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

    @Override
    public void onCancel(int position) {
        removeCartItem(adapter.getItem(position).getCartID());
    }

    @Override
    public void onCheckOut(int position) {
        FoodName = adapter.getItem(position).getFoodName();
        promptPay(adapter.getItem(position).getFoodID(), adapter.getItem(position).getSellerID(), adapter.getItem(position).getCartID());
    }

    private void promptPay(final String foodID, final String sellerID, final String cartID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Please choose a payment option");
        builder.setCancelable(false);
        builder.setMessage("1) Pay on Delivery or Pick up(PDP)\n2) Pay with MPESA\n3) Proceed with MPESA code(PwMC)");
        builder.setNegativeButton("PDP", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                payOnDeliveryOrPickUp(foodID, sellerID, cartID);
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
                payWithMpesaCode(foodID, sellerID, cartID);
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void payOnDeliveryOrPickUp(final String FoodID, final String SellerID, final String cartID) {
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
                        removeCartItem(cartID);
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

    private void payWithMpesaCode(final String FoodID, final String SellerID, final String cartID) {
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
                                    removeCartItem(cartID);
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
        placeOrder(foodID, sellerID);
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
                        //checkIfChatExists(orderID, foodID);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Order placing failed... Please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkIfChatExists(final String orderID, final String foodID) {
        Toast.makeText(getContext(), PrimaryID, Toast.LENGTH_SHORT).show();
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
        chat.put("LastMessage", "You have a new order for" + FoodName);
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
        PrimaryName = name;
    }

    private void setSecondaryName(String name) {
        PrimaryName = name;
    }

    private void setPrimaryImage(String name) {
        PrimaryName = name;
    }

    private void setSecondaryImage(String name) {
        PrimaryName = name;
    }

    private void removeCartItem(String cartID) {
        firestore.collection(PrimaryID + "Cart").document(cartID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Cart removed...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "cart delete failed...", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
