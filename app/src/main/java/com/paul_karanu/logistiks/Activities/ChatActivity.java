package com.paul_karanu.logistiks.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.paul_karanu.logistiks.Adapters.ChatMessagesAdapter;
import com.paul_karanu.logistiks.Data.Models.ChatMessage;
import com.paul_karanu.logistiks.Data.Models.FoodieMessage;
import com.paul_karanu.logistiks.R;
import com.paul_karanu.logistiks.Utilities.Constants;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    ChatMessagesAdapter adapter;

    String PrimaryID;
    String PrimaryName;
    String PrimaryImage;
    String SecondaryID;
    String SecondaryName;
    String SecondaryImage;
    String currentMessage;
    String ChatID;
    String FoodID;
    boolean newChat = true;

    FirebaseFirestore firestore;
    FirestoreRecyclerOptions<FoodieMessage> options;
    StorageReference storageRef;
    FirebaseStorage storage;
    FirebaseUser user;
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    Query query;

    TextView title;
    ImageView moreMenu, star1, star2, star3, star4, star5, imageHolder;
    EditText messageEditText;
    Button sendButton;
    TextView foodName;
    TextView aboutValue;
    TextView priceValue;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        SecondaryID = getIntent().getStringExtra("SecondaryID");
        FoodID = getIntent().getStringExtra("FoodID");
        ChatID = getIntent().getStringExtra("ChatID");

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        assert user != null;
        PrimaryID = user.getUid();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        title = findViewById(R.id.chatChatTitle);
        star1 = findViewById(R.id.modelBaseFoodStar1);
        star2 = findViewById(R.id.modelBaseFoodStar2);
        star3 = findViewById(R.id.modelBaseFoodStar3);
        star4 = findViewById(R.id.modelBaseFoodStar4);
        star5 = findViewById(R.id.modelBaseFoodStar5);
        imageHolder = findViewById(R.id.modelBaseFoodImage);
        moreMenu = findViewById(R.id.modelBaseFoodMenuImage);
        foodName = findViewById(R.id.modelBaseFoodTitle);
        aboutValue = findViewById(R.id.modelBaseFoodAbout);
        priceValue = findViewById(R.id.modelBaseFoodPrice);
        recyclerView = findViewById(R.id.chatsRecyclerView);
        messageEditText = findViewById(R.id.messageEDT);
        sendButton = findViewById(R.id.chatSendBtn);
        sendButton.setOnClickListener(this);

        setFoodDetails();
        checkIfChatExists();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chatSendBtn:
                currentMessage = messageEditText.getText().toString();
                if (newChat)
                    createChat();
                else
                    updatePrimaryChat(ChatID);
                hideKeyBoard();
                break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    private void setFoodDetails() {
        DocumentReference docRef = firestore.collection("Foods").document(FoodID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    star1.setVisibility(View.GONE);
                    star2.setVisibility(View.GONE);
                    star3.setVisibility(View.GONE);
                    star4.setVisibility(View.GONE);
                    star5.setVisibility(View.GONE);
                    Glide.with(ChatActivity.this).using(new FirebaseImageLoader())
                            .load(loadName(Objects.requireNonNull(document.get("Picture")).toString()))
                            .into(imageHolder);
                    moreMenu.setImageDrawable(getResources().getDrawable(R.drawable.ic_cancel));
                    foodName.setText(Objects.requireNonNull(document.get("Name")).toString());
                    aboutValue.setText(Objects.requireNonNull(document.get("About")).toString());
                    priceValue.setText(Objects.requireNonNull(document.get("Price")).toString());
                }
            }
        });

        DocumentReference documentReference = firestore.collection("Users").document(SecondaryID);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    title.setText(document.getString("FirstName"));
                    setSecondaryName(document.getString("FirstName"));
                    setSecondaryImage(document.getString("ProfilePicture"));
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
                    setPrimaryName(document.getString("FirstName"));
                    setPrimaryImage(document.getString("ProfilePicture"));
                }
            }
        });
    }

    private void setPrimaryName(String name) {
        this.PrimaryName = name;
    }

    private void setPrimaryImage(String name) {
        this.PrimaryImage = name;
    }

    private void setSecondaryImage(String name) {
        this.SecondaryImage = name;
    }

    private void setSecondaryName(String name) {
        this.SecondaryName = name;
    }

    private void loadChat(String ChatID) {
        query = firestore.collection(ChatID).orderBy("DateCreated", Query.Direction.DESCENDING);
        options = new FirestoreRecyclerOptions.Builder<FoodieMessage>().setQuery(query, FoodieMessage.class).build();
        adapter = new ChatMessagesAdapter(this, options, PrimaryID);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL, true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter.startListening();
        this.ChatID = ChatID;
        recyclerView.scrollToPosition(0);
    }

    private void checkIfChatExists() {
        DocumentReference documentReference = firestore.collection(PrimaryID + "Chats").document(SecondaryID);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        loadChat(Objects.requireNonNull(document.get("ChatID")).toString());
                        newChat = false;
                    }
                } else {
                    newChat = true;
                    Log.d("FOODIE", "get failed with ", task.getException());
                }
            }
        });
    }

    private void createChat() {
        Map<String, Object> chat = new HashMap<>();
        chat.put("DateCreated", Calendar.getInstance().getTime());
        chat.put("PrimaryID", PrimaryID);
        chat.put("SecondaryID", SecondaryID);
        firestore.collection("Chats").add(chat).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(final DocumentReference documentReference) {
                documentReference.update("ChatID", documentReference.getId());
                createPrimaryChats(documentReference.getId());
                createSecondaryChat(documentReference.getId());
                dumpNewToMessagePool(documentReference.getId());
            }
        });
    }

    private void createPrimaryChats(final String chatID) {
        Map<String, Object> chat = new HashMap<>();
        ChatMessage message = new ChatMessage();

        message.setChatID(chatID);
        message.setDateCreated(Calendar.getInstance().getTime());
        message.setDateCreated(Calendar.getInstance().getTime());
        message.setFoodID(FoodID);
        message.setImage("");
        message.setLastMessage(currentMessage);
        message.setMessageState(Constants.MESSAGEUNSENT);
        message.setPrimaryID(PrimaryID);
        message.setSecondaryID(SecondaryID);
        message.setPrimaryName(PrimaryName);
        message.setSecondaryName(SecondaryName);

        databaseReference.child(PrimaryID + "Chats").child("SecondaryID").setValue(message);
        chat.put("PrimaryID", PrimaryID);
        chat.put("SecondaryID", SecondaryID);
        chat.put("LastMessage", currentMessage);
        chat.put("DateCreated", Calendar.getInstance().getTime());
        chat.put("DateModified", Calendar.getInstance().getTime());
        chat.put("MessageState", Constants.MESSAGEUNSENT);
        chat.put("FoodID", FoodID);
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

    private void createSecondaryChat(final String chatID) {
        Map<String, Object> chat = new HashMap<>();
        chat.put("PrimaryID", SecondaryID);
        chat.put("SecondaryID", PrimaryID);
        chat.put("LastMessage", currentMessage);
        chat.put("DateCreated", Calendar.getInstance().getTime());
        chat.put("DateModified", Calendar.getInstance().getTime());
        chat.put("MessageState", Constants.MESSAGEUNSENT);
        chat.put("FoodID", FoodID);
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

    private void dumpNewToMessagePool(final String chatID) {
        Map<String, Object> message = new HashMap<>();
        message.put("PrimaryID", PrimaryID);
        message.put("SecondaryID", SecondaryID);
        message.put("Message", currentMessage);
        message.put("Image", "");
        message.put("DateCreated", Calendar.getInstance().getTime());
        message.put("MessageState", Constants.MESSAGEUNSENT);
        firestore.collection(chatID).add(message).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(final DocumentReference documentReference) {
                Toast.makeText(ChatActivity.this, "Message sent", Toast.LENGTH_SHORT).show();
                firestore.collection("Chats").document(chatID).update("MessageState", Constants.MESSAGESENT);
                newChat = false;
                loadChat(chatID);
            }
        });
    }

    private void updatePrimaryChat(final String chatID) {
        Map<String, Object> chat = new HashMap<>();
        chat.put("PrimaryID", PrimaryID);
        chat.put("SecondaryID", SecondaryID);
        chat.put("LastMessage", currentMessage);
        chat.put("DateCreated", Calendar.getInstance().getTime());
        chat.put("DateModified", Calendar.getInstance().getTime());
        chat.put("MessageState", Constants.MESSAGEUNSENT);
        chat.put("FoodID", FoodID);
        chat.put("ChatID", chatID);
        chat.put("SecondaryName", SecondaryName);
        chat.put("PrimaryName", PrimaryName);
        chat.put("Image", SecondaryImage);
        firestore.collection(PrimaryID + "Chats").document(SecondaryID).set(chat).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                updateSecondaryChat(chatID);
                firestore.collection(PrimaryID + "Chats").document(SecondaryID).update("MessageState", Constants.MESSAGESENT);
                hideKeyBoard();
            }
        });
    }


    private void updateSecondaryChat(final String chatID) {
        Map<String, Object> chat = new HashMap<>();
        chat.put("PrimaryID", SecondaryID);
        chat.put("SecondaryID", PrimaryID);
        chat.put("LastMessage", currentMessage);
        chat.put("DateCreated", Calendar.getInstance().getTime());
        chat.put("DateModified", Calendar.getInstance().getTime());
        chat.put("MessageState", Constants.MESSAGEUNSENT);
        chat.put("FoodID", FoodID);
        chat.put("ChatID", chatID);
        chat.put("SecondaryName", PrimaryName);
        chat.put("Image", PrimaryImage);
        chat.put("PrimaryName", SecondaryName);
        firestore.collection(SecondaryID + "Chats").document(PrimaryID).set(chat).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dumpToMessagePool(chatID);
                firestore.collection(SecondaryID + "Chats").document(PrimaryID).update("MessageState", Constants.MESSAGESENT);
            }
        });
    }

    private void dumpToMessagePool(final String chatID) {
        Map<String, Object> message = new HashMap<>();
        message.put("PrimaryID", PrimaryID);
        message.put("SecondaryID", SecondaryID);
        message.put("Message", currentMessage);
        message.put("Image", "");
        message.put("DateCreated", Calendar.getInstance().getTime());
        message.put("MessageState", Constants.MESSAGEUNSENT);
        firestore.collection(chatID).add(message).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(final DocumentReference documentReference) {
                Toast.makeText(ChatActivity.this, "Message sent", Toast.LENGTH_SHORT).show();
                firestore.collection("Chats").document(chatID).update("MessageState", Constants.MESSAGESENT);
            }
        });
        recyclerView.scrollToPosition(0);
    }

    private StorageReference loadName(String fileName) {
        return storageRef.child("images/" + fileName);
    }

    private void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(messageEditText.getWindowToken(), 0);
        messageEditText.getText().clear();
        messageEditText.setHint("Type your message here");
    }
}
