package com.paul_karanu.logistiks.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.paul_karanu.logistiks.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ProviderQueryResult;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    Button nextButton;
    EditText mainPasswordEDT, emailEDT;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        nextButton = findViewById(R.id.signInBtn);
        nextButton.setOnClickListener(this);

        mainPasswordEDT = findViewById(R.id.signInPasswordEDT);
        emailEDT = findViewById(R.id.signInEmailEDT);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.signInBtn:
                checkFields();
                break;
        }
    }

    private void checkFields() {
        if (mainPasswordEDT.getText().toString().length() < 6 || mainPasswordEDT.getText().toString().length() > 24){
            Toast.makeText(this, "Please use a password 6-24 characters long", Toast.LENGTH_SHORT).show();
        } else if (emailEDT.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
        } else if (mainPasswordEDT.getText().toString().isEmpty()){
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
        } else {
            checkIfUserExists(emailEDT.getText().toString(), mainPasswordEDT.getText().toString());
        }
    }

    private void checkIfUserExists(final String email, final String password){
        mAuth.fetchProvidersForEmail(email).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                boolean isRegistered = !Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getProviders()).isEmpty();
                if (!isRegistered)
                    signUserUp(email, password);
                else
                    signUserIn(email, password);
            }
        });
    }

    @SuppressLint("InflateParams")
    private void signUserUp(final String email, final String password){
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_confirm_password, null);

        final EditText confirmPasswordEDT = dialogView.findViewById(R.id.dialogConfirmPasswordEDT);
        final Button confirmPasswordButton = dialogView.findViewById(R.id.dialogConfirmPasswordBtn);
        confirmPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password.equals(confirmPasswordEDT.getText().toString())) {
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                startActivity(new Intent(SignUpActivity.this, BaseActivity.class));
                                finish();
                            } else {
                                Toast.makeText(SignUpActivity.this, "Sign up failed, try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(SignUpActivity.this, "Passwords do not match, try again", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });

        dialog.setView(dialogView);
        dialog.show();

    }

    private void signUserIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    startActivity(new Intent(SignUpActivity.this, BaseActivity.class));
                    finish();
                } else {
                    Toast.makeText(SignUpActivity.this, "Sign in failed, Please check your credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
