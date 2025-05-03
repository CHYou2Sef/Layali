package com.example.layali;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import android.app.Application;

import com.example.layali.activities.LoginActivity;
import com.google.firebase.FirebaseApp;
import com.example.layali.databinding.ActivityMainBinding;
import com.example.layali.fragments.home.HomeFragment;
import com.example.layali.fragments.cart.CartFragment;
import com.example.layali.fragments.history.HistoryFragment;
import com.example.layali.fragments.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//import com.example.layali.activities.databinding.ActivityMainBinding;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;
    private NavController navController;
    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);

        mAuth = FirebaseAuth.getInstance();

        try {
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
        } catch (Exception e) {
            Log.e(TAG, "Error inflating layout", e);
            setContentView(R.layout.activity_main);
            bottomNavigationView = findViewById(R.id.bottom_nav);
        }

        if (getIntent().getBooleanExtra("openCart", false)) {
            // ✅ ouvrir directement CartFragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new CartFragment())
                    .commit();
        }

        if (getIntent().getBooleanExtra("openHistory", false)) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HistoryFragment())
                    .commit();
        }


        setupNavigation();
    }

    private void setupNavigation() {
        // Get the NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);

        if (navHostFragment == null) {
            Log.e(TAG, "NavHostFragment is null");
            return;
        }

        navController = navHostFragment.getNavController();

        // Top-level destinations
        Set<Integer> topLevelDestinations = new HashSet<>();
        topLevelDestinations.add(R.id.nav_home);
        topLevelDestinations.add(R.id.nav_cart);
        topLevelDestinations.add(R.id.nav_history);
        topLevelDestinations.add(R.id.nav_profile);

        // Configure AppBarConfiguration
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(topLevelDestinations)
                .build();

        // Set up the ActionBar with NavController
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // Connect the BottomNavigationView with the NavController
        bottomNavigationView = binding.bottomNav;
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        // Add navigation listener for debugging
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getLabel() == "ProfileFragment") {
                if (isUserLoggedIn()) {
                    navController.navigate(R.id.nav_profile);
                }
                else {
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                }
            }
            Log.d(TAG, "Navigating to: " + destination.getLabel()+" : "+destination.getId());
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    private boolean isUserLoggedIn() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        return currentUser != null;
    }
}