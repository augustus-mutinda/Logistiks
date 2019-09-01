package com.paul_karanu.logistiks.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.paul_karanu.logistiks.Data.Profile.Profile;
import com.paul_karanu.logistiks.R;
import com.paul_karanu.logistiks.Utilities.Constants;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    Profile newProfile = new Profile();

    CircleImageView profile;
    TextView surname, otherName, location, subLocation, email, phoneNumber;
    Button actionButton;
    Uri userUri;

    FirebaseAuth auth;
    FirebaseUser user;
    StorageReference storageReference;
    FirebaseFirestore firestore;
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        firestore = FirebaseFirestore.getInstance();

        profile = findViewById(R.id.profilePictureIV);
        profile.setOnClickListener(this);
        surname = findViewById(R.id.profileSurnameTV);
        surname.setOnClickListener(this);
        otherName = findViewById(R.id.profileOtherNameTV);
        otherName.setOnClickListener(this);
        location = findViewById(R.id.profileLocation);
        location.setOnClickListener(this);
        subLocation = findViewById(R.id.profileSubLocation);
        subLocation.setOnClickListener(this);
        email = findViewById(R.id.profileEmailTV);
        phoneNumber = findViewById(R.id.profilePhoneTV);
        phoneNumber.setOnClickListener(this);
        actionButton = findViewById(R.id.profileSave);
        actionButton.setOnClickListener(this);

        firestore.collection("Users").whereEqualTo("UserID", user.getUid())
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            populateFields();
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profilePictureIV:
                setPicture();
                break;
            case R.id.profileSurnameTV:
                setField(Constants.SURNAME);
                break;
            case R.id.profileOtherNameTV:
                setField(Constants.OTHERNAME);
                break;
            case R.id.profileLocation:
                setField(Constants.LOCATION);
                break;
            case R.id.profileSubLocation:
                setField(Constants.SUBLOCATION);
                break;
            case R.id.profilePhoneTV:
                setField(Constants.PHONENUMBER);
                break;
            case R.id.profileSave:
                validateProfile();
                Toast.makeText(this, "Action", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void setPicture(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Picture"), Constants.IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == Constants.IMAGE && resultCode == RESULT_OK && data != null) {
            userUri = data.getData();

            Glide.with(this).load(userUri).into(profile);

            String fileName = "";
            if (Objects.equals(userUri.getScheme(), "file")) {
                fileName = userUri.getLastPathSegment();
            } else {
                try (Cursor cursor = getContentResolver().query(userUri, new String[]{
                        MediaStore.Images.ImageColumns.DISPLAY_NAME
                }, null, null, null)) {

                    if (cursor != null && cursor.moveToFirst()) {
                        fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME));
                    }
                }
            }
            if (fileName != null && fileName.indexOf(".") > 0)
                fileName = fileName.substring(0, fileName.lastIndexOf("."));
            newProfile.setProfilePicture(fileName);
            Toast.makeText(this, fileName, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Cannot use that image, please pick another", Toast.LENGTH_SHORT).show();
        }
    }

    private void populateFields(){
        DocumentReference docRef = firestore.collection("Users").document(user.getUid());

        Source source = Source.CACHE;

        docRef.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    assert document != null;
                    Glide.with(getApplicationContext())
                            .using(new FirebaseImageLoader())
                            .load(storageReference.child("users/"+document.getString("ProfilePicture")))
                            .into(profile);
                    surname.setText(document.getString("FirstName"));
                    otherName.setText(document.getString("SecondName"));
                    location.setText(document.getString("Location"));
                    subLocation.setText(document.getString("SecondaryLocation"));
                    phoneNumber.setText(document.getString("PhoneNumber"));
                    email.setText(document.getString("Email"));
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void setField(final String field) {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        @SuppressLint("InflateParams") View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_confirm_password, null);

        final TextView title = dialogView.findViewById(R.id.dialogConfirmPasswordTitle);
        final TextInputLayout inputLayout = dialogView.findViewById(R.id.dialogConfirmPasswordTIL);
        final EditText editText = dialogView.findViewById(R.id.dialogConfirmPasswordEDT);
        final Button actionButton = dialogView.findViewById(R.id.dialogConfirmPasswordBtn);
        actionButton.setText(getText(R.string.save));

        switch (field) {
            case Constants.SURNAME:
                title.setText("Set first name");
                editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                inputLayout.setHint("First name");
                break;
            case Constants.OTHERNAME:
                title.setText("Set other name");
                editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                inputLayout.setHint("Other name");
                break;
            case Constants.LOCATION:
                title.setText("Set primary location");
                editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                inputLayout.setHint("Location");
                break;
            case Constants.SUBLOCATION:
                title.setText("Set secondary location");
                editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                inputLayout.setHint("Alternative location");
                break;
            case Constants.PHONENUMBER:
                title.setText("Enter your number");
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                inputLayout.setHint("Phone number");
                break;
        }

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (field) {
                    case Constants.SURNAME:
                        newProfile.setFirstName(editText.getText().toString());
                        surname.setText(newProfile.getFirstName());
                        break;
                    case Constants.OTHERNAME:
                        newProfile.setSecondName(editText.getText().toString());
                        otherName.setText(newProfile.getSecondName());
                        break;
                    case Constants.LOCATION:
                        newProfile.setLocation(editText.getText().toString());
                        location.setText(newProfile.getLocation());
                        break;
                    case Constants.SUBLOCATION:
                        newProfile.setSecondaryLocation(editText.getText().toString());
                        subLocation.setText(newProfile.getSecondaryLocation());
                        break;
                    case Constants.PHONENUMBER:
                        newProfile.setPhoneNumber(editText.getText().toString());
                        phoneNumber.setText(newProfile.getPhoneNumber());
                        break;
                }
                dialog.dismiss();
            }
        });

        dialog.setView(dialogView);
        dialog.show();

    }

    private void validateProfile(){

        if (newProfile.getFirstName().equals("")){
            Toast.makeText(this, "Please set a name", Toast.LENGTH_SHORT).show();
            setField(Constants.SURNAME);
            return;
        } else if (newProfile.getSecondName().equals("")){
            Toast.makeText(this, "Please set a second name", Toast.LENGTH_SHORT).show();
            setField(Constants.OTHERNAME);
            return;
        } else if (newProfile.getLocation().equals("")){
            Toast.makeText(this, "Please set a location", Toast.LENGTH_SHORT).show();
            setField(Constants.OTHERNAME);
            return;
        } else if (newProfile.getSecondaryLocation().equals("")){
            Toast.makeText(this, "Please set a secondary location", Toast.LENGTH_SHORT).show();
            setField(Constants.OTHERNAME);
            return;
        } else if (newProfile.getPhoneNumber().equals("") || newProfile.getPhoneNumber().length() <10){
            Toast.makeText(this, "Please set a phone number", Toast.LENGTH_SHORT).show();
            setField(Constants.OTHERNAME);
            return;
        } else if (newProfile.getProfilePicture().equals("")){
            Toast.makeText(this, "Please set a profile picture", Toast.LENGTH_SHORT).show();
            setPicture();
            return;
        }

        newProfile.setEmail(user.getEmail());
        newProfile.setUserID(user.getUid());
        newProfile.setDateCreated(Calendar.getInstance().getTime());

        saveOnlineProfile();
    }

    private void saveOnlineProfile(){
        uploadImage();
        Map<String, Object> uploadProfile = new HashMap<>();
        uploadProfile.put("UserID", newProfile.getUserID());
        uploadProfile.put("FirstName", newProfile.getFirstName());
        uploadProfile.put("SecondName", newProfile.getSecondName());
        uploadProfile.put("Location", newProfile.getLocation());
        uploadProfile.put("SecondaryLocation", newProfile.getSecondaryLocation());
        uploadProfile.put("Email", newProfile.getEmail());
        uploadProfile.put("PhoneNumber", newProfile.getPhoneNumber());
        uploadProfile.put("DateCreated", newProfile.getDateCreated());
        uploadProfile.put("ProfilePicture", newProfile.getProfilePicture());
        firestore.collection("Users").document(user.getUid()).set(uploadProfile).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ProfileActivity.this, "Profile set online", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this, "Profile not set Online", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void uploadImage() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        StorageReference ref = storageReference.child("users/" + newProfile.getProfilePicture());
        ref.putFile(userUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        Toast.makeText(ProfileActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ProfileActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int) progress + "%");
                    }
                });
    }
}
