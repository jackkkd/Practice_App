package com.example.practiceapp;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Dashboard extends AppCompatActivity {

    private String userRole; // Stores the role globally for this activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        // Catch the role from Login
        userRole = getIntent().getStringExtra("USER_ROLE");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == bottomNav.getSelectedItemId() && getSupportFragmentManager().findFragmentById(R.id.fragment_container) != null) {
                return true;
            }

            if (itemId == R.id.nav_home) selectedFragment = new HomeFragment();
            else if (itemId == R.id.nav_library) selectedFragment = new LibraryFragment();
            else if (itemId == R.id.nav_settings) selectedFragment = new SettingsFragment();

            if (selectedFragment != null) {
                loadFragment(selectedFragment); // Use helper to pass the role
                return true;
            }
            return false;
        });

        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_home);
        }

        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (bottomNav.getSelectedItemId() != R.id.nav_home) {
                    bottomNav.setSelectedItemId(R.id.nav_home);
                } else {
                    new androidx.appcompat.app.AlertDialog.Builder(Dashboard.this)
                            .setTitle("Exit or Log Out")
                            .setMessage("Do you want to log out of your account or exit the application?")
                            .setPositiveButton("Log Out", (dialog, which) -> {
                                android.content.Intent intent = new android.content.Intent(Dashboard.this, MainActivity.class);
                                intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            })
                            .setNegativeButton("Exit App", (dialog, which) -> finishAffinity())
                            .setNeutralButton("Cancel", (dialog, which) -> dialog.dismiss())
                            .show();
                }
            }
        });
    }

    // Helper method to attach the user role to any fragment being loaded
    private void loadFragment(Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putString("ROLE", userRole);
        fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}