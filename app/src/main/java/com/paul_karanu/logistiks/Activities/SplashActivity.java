package com.paul_karanu.logistiks.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.paul_karanu.logistiks.R;
import com.paul_karanu.logistiks.Utilities.Constants;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    ImageView logo;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        logo = findViewById(R.id.splashLogo);
        context = this;

        FirebaseApp.initializeApp(SplashActivity.this);
        new CheckRegistration().execute();
    }

    @SuppressLint("StaticFieldLeak")
    public class CheckRegistration extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                startActivity(new Intent(SplashActivity.this, SignUpActivity.class));
                finish();
            } else {
                if (SplashActivity.this.getSharedPreferences("com.cupcake.foodie", MODE_PRIVATE).getBoolean(Constants.SELLER, false)) {
                    startActivity(new Intent(SplashActivity.this, SellerActivity.class));
                } else
                    startActivity(new Intent(SplashActivity.this, BaseActivity.class));
                finish();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            logo.startAnimation(AnimationUtils.loadAnimation(context, R.anim.faded));
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            logo.clearAnimation();
            super.onPostExecute(aVoid);
        }
    }
}
