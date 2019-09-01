package com.paul_karanu.logistiks.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.paul_karanu.logistiks.Adapters.BaseViewPagerAdapter;
import com.paul_karanu.logistiks.Fragments.CartFragment;
import com.paul_karanu.logistiks.Fragments.ChatsFragment;
import com.paul_karanu.logistiks.Fragments.HomeFragment;
import com.paul_karanu.logistiks.R;
import com.paul_karanu.logistiks.Utilities.Constants;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class    BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    FirebaseUser currentUser;
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    FirebaseStorage storage;
    StorageReference storageReference;

    SharedPreferences preferences;

    Intent intent;

    ImageView profilePic, editProfile;
    TextView userTitle, userMail;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        preferences = this.getSharedPreferences("com.cupcake.foodie", MODE_PRIVATE);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        firestore = FirebaseFirestore.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        viewPager = findViewById(R.id.baseViewPager);
        viewPager.setAdapter(new BaseViewPagerAdapter(getSupportFragmentManager(), getFragments(), new String[]{"Home", "Chats", "Cart"}));

        TabLayout tabLayout = findViewById(R.id.baseTablayout);
        tabLayout.setupWithViewPager(viewPager, true);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View navHeader = navigationView.getHeaderView(0);
        profilePic = navHeader.findViewById(R.id.baseNavigationProfilePicture);
        editProfile = navHeader.findViewById(R.id.baseNavigationEditProfile);
        editProfile.setOnClickListener(this);
        userTitle = navHeader.findViewById(R.id.baseNavigationTitle);
        userMail = navHeader.findViewById(R.id.baseNavigationMail);
        userMail.setText(currentUser.getEmail());

        firestore.collection("Users").whereEqualTo("UserID", currentUser.getUid())
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentReference docRef = firestore.collection("Users").document(currentUser.getUid());
                            Source source = Source.CACHE;
                            docRef.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        assert document != null;
                                        Glide.with(getApplicationContext())
                                                .using(new FirebaseImageLoader())
                                                .load(storageReference.child("users/" + document.getString("ProfilePicture")))
                                                .into(profilePic);
                                        userTitle.setText(document.getString("FirstName"));
                                    }
                                }
                            });
                        }
                    }
                });

        if (getIntent().getBooleanExtra("NewUser", false))
            setUpNewUser(true);

        Objects.requireNonNull(getSupportActionBar()).setTitle(getText(R.string.app_name));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_base_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_new_food_picture) {
            startActivity(new Intent(BaseActivity.this, SearchActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            viewPager.setCurrentItem(0, true);
        } else if (id == R.id.nav_chat) {
            viewPager.setCurrentItem(1, true);
        } else if (id == R.id.nav_orders) {
            viewPager.setCurrentItem(2, true);
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(BaseActivity.this, SettingsActivity.class));
        } else if (id == R.id.nav_switch_accounts) {
            preferences.edit().putBoolean(Constants.SELLER, true).apply();
            startActivity(new Intent(this, SellerActivity.class));
            finish();
        } else if (id == R.id.nav_sign_out) {
            mAuth.signOut();
            Toast.makeText(this, "You have been signed out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, SignUpActivity.class));
            clearCache();
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.baseNavigationEditProfile:
                setUpNewUser(false);
                break;
        }
    }

    public void setUpNewUser(boolean isNew) {
        intent = new Intent(BaseActivity.this, ProfileActivity.class);
        intent.putExtra("NewProfile", isNew);
        startActivity(intent);
    }

    private List<Fragment> getFragments() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new HomeFragment());
        fragments.add(new ChatsFragment());
        fragments.add(new CartFragment());
        return fragments;
    }

    public boolean clearCache() {
        try {
            File[] files = getBaseContext().getCacheDir().listFiles();
            for (File file : files) {
                if (!file.delete()) {
                    return false;
                }
            }
            return true;
        } catch (Exception ignored) {
        }
        return false;
    }
}
