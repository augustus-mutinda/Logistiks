package com.paul_karanu.logistiks.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.paul_karanu.logistiks.Data.Models.NewFood;
import com.paul_karanu.logistiks.R;
import com.paul_karanu.logistiks.Utilities.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class NewFoodActivity extends AppCompatActivity implements View.OnClickListener {

    Uri imageUri;

    TextView price, aboutFood, pickUp;
    CollapsingToolbarLayout collapsingToolbarLayout;
    Spinner availabilitySpinner, locationSpinner, pickUpSpinner;

    static NewFood newFood;

    FirebaseUser currentUser;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_food);

        newFood = new NewFood();

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        price = findViewById(R.id.newFoodPrice);
        price.setOnClickListener(this);
        aboutFood = findViewById(R.id.newFoodAbout);
        aboutFood.setOnClickListener(this);
        collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
        availabilitySpinner = findViewById(R.id.newFoodAvailabilitySpinner);
        locationSpinner = findViewById(R.id.newFoodLocationSpinner);
        pickUp  = findViewById(R.id.pickUpTV);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnClickListener(this);

        FloatingActionButton fab = findViewById(R.id.newFoodSaveFAB);
        fab.setOnClickListener(this);

        pickUpSpinner = findViewById(R.id.newFoodDeliveryOptionsSpinner);
        pickUpSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (pickUpSpinner.getSelectedItem().toString()){
                    case "Pick up":
                        pickUp.setText("Pick up at");
                        break;
                    case "Delivery":
                        pickUp.setText("Deliveries around");
                        break;
                    case "Both":
                        pickUp.setText("Pick up or deliveries at/around");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        setField(Constants.FOODNAME);

        collapsingToolbarLayout.setTitle(getResources().getText(R.string.food_name).toString());
        getSupportActionBar().setTitle(getResources().getText(R.string.food_name).toString());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar:
                setField(Constants.FOODNAME);
                break;
            case R.id.newFoodPrice:
                setField(Constants.PRICE);
                break;
            case R.id.newFoodAbout:
                setField(Constants.ABOUTFFOOD);
                break;
            case R.id.newFoodSaveFAB:
                saveFood();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_food, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        setImage();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Constants.IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            imageUri = selectedImage;

            Drawable bg;
            try {
                InputStream inputStream = getContentResolver().openInputStream(selectedImage);
                bg = Drawable.createFromStream(inputStream, selectedImage.toString());
                collapsingToolbarLayout.setBackground(bg);
            } catch (FileNotFoundException e) {

            }

            String fileName = "";
            if (selectedImage.getScheme().equals("file")) {
                fileName = selectedImage.getLastPathSegment();
            } else {
                Cursor cursor = null;
                try {
                    cursor = getContentResolver().query(selectedImage, new String[]{
                            MediaStore.Images.ImageColumns.DISPLAY_NAME
                    }, null, null, null);

                    if (cursor != null && cursor.moveToFirst()) {
                        fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME));
                    }
                } finally {

                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
            if (fileName != null && fileName.indexOf(".") > 0)
                fileName = fileName.substring(0, fileName.lastIndexOf("."));
            newFood.setPicture(fileName);
            Toast.makeText(this, fileName, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Cannot use that image, please pick another", Toast.LENGTH_SHORT).show();
        }
    }

    private void setImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), Constants.IMAGE);
    }

    private void setField(final String field) {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_confirm_password, null);

        final TextView title = dialogView.findViewById(R.id.dialogConfirmPasswordTitle);
        final TextInputLayout inputLayout = dialogView.findViewById(R.id.dialogConfirmPasswordTIL);
        final EditText confirmPasswordEDT = dialogView.findViewById(R.id.dialogConfirmPasswordEDT);
        final Button confirmPasswordButton = dialogView.findViewById(R.id.dialogConfirmPasswordBtn);
        confirmPasswordButton.setText(getText(R.string.save));

        switch (field) {
            case Constants.FOODNAME:
                title.setText(getString(R.string.set_food_name));
                confirmPasswordEDT.setInputType(InputType.TYPE_CLASS_TEXT);
                inputLayout.setHint(getText(R.string.food_name));
                break;
            case Constants.PRICE:
                title.setText(getString(R.string.set_price));
                confirmPasswordEDT.setInputType(InputType.TYPE_CLASS_NUMBER);
                inputLayout.setHint(getText(R.string.price));
                break;
            case Constants.ABOUTFFOOD:
                title.setText(getString(R.string.write_about_the_food));
                confirmPasswordEDT.setInputType(InputType.TYPE_CLASS_TEXT);
                inputLayout.setHint(getText(R.string.about));
                break;
        }

        confirmPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (field) {
                    case Constants.FOODNAME:
                        newFood.setName(confirmPasswordEDT.getText().toString());
                        collapsingToolbarLayout.setTitle(confirmPasswordEDT.getText());
                        getSupportActionBar().setTitle(confirmPasswordEDT.getText());
                        break;
                    case Constants.PRICE:
                        newFood.setPrice(Integer.parseInt(confirmPasswordEDT.getText().toString()));
                        price.setText(confirmPasswordEDT.getText());
                        break;
                    case Constants.ABOUTFFOOD:
                        newFood.setAbout(confirmPasswordEDT.getText().toString());
                        aboutFood.setText(confirmPasswordEDT.getText());
                        break;
                }
                dialog.dismiss();
            }
        });

        dialog.setView(dialogView);
        dialog.show();

    }

    private void saveFood() {
        if (newFood.getName().isEmpty() || newFood.getName().equals("")) {
            setField(Constants.FOODNAME);
            Toast.makeText(this, "Please set a name", Toast.LENGTH_SHORT).show();
            return;
        } else if (newFood.getPrice() == 0) {
            setField(Constants.PRICE);
            Toast.makeText(this, "Please set a price", Toast.LENGTH_SHORT).show();
            return;
        } else if (newFood.getAbout().equals(getResources().getString(R.string.write_about_the_food)) || newFood.getAbout().equals("")) {
            setField(Constants.ABOUTFFOOD);
            Toast.makeText(this, "Please write about your food", Toast.LENGTH_SHORT).show();
            return;
        } else if (newFood.getPicture().equals("")) {
            setImage();
            Toast.makeText(this, "Please provide a picture", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
            newFood.setAvailability(availabilitySpinner.getSelectedItem().toString());
            newFood.setUserID(currentUser.getUid());
            uploadImage();
        }
    }

    private void uploadData() {
        Map<String, Object> newUploadFood = new HashMap<>();
        newUploadFood.put("Name", newFood.getName());
        newUploadFood.put("UserID", newFood.getUserID());
        newUploadFood.put("Price", newFood.getPrice());
        newUploadFood.put("About", newFood.getAbout());
        newUploadFood.put("Availability", newFood.getAvailability());
        newUploadFood.put("Picture", newFood.getPicture());
        newUploadFood.put("Rating", newFood.getRating());
        newUploadFood.put("Delivery", pickUpSpinner.getSelectedItem().toString());
        newUploadFood.put("Popularity", 0);
        firestore.collection("Foods").add(newUploadFood).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                documentReference.update("FoodID", documentReference.getId());
                Toast.makeText(NewFoodActivity.this,  newFood.getName() +" put on menu", Toast.LENGTH_SHORT).show();
                onBackPressed();
                finish();
            }
        });
    }

    private void uploadImage() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        StorageReference ref = storageReference.child("images/" + newFood.getPicture());
        ref.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        uploadData();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(NewFoodActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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