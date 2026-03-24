package com.example.practiceapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private DBH dbHelper;
    private TextView tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- DARK MODE PERSISTENCE ---
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("isDarkMode", false);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        dbHelper = new DBH(this);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Register.class);
            startActivity(intent);
        });

        // --- UPDATED LOGIN LOGIC ---
        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String userPassword = etPassword.getText().toString().trim();

            if (username.isEmpty() || userPassword.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter both Username and Password", Toast.LENGTH_SHORT).show();
            } else {

                // 1. Ask the Database for the User's Role (returns "ADMIN", "USER", or null)
                // This method also handles the Password Hashing internally.
                String role = dbHelper.getUserRole(username, userPassword);

                if (role != null) {
                    // Login is valid because a role was returned
                    Toast.makeText(MainActivity.this, "Login Successful as " + role, Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(MainActivity.this, Dashboard.class);

                    // 2. Pass the dynamic role found in the database to the Dashboard
                    intent.putExtra("USER_ROLE", role);

                    startActivity(intent);
                    finish();
                } else {
                    // If the database returns null, the credentials were wrong or don't exist
                    Toast.makeText(MainActivity.this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this)
                        .setTitle("Exit Application")
                        .setMessage("Are you sure you want to exit the app?")
                        .setPositiveButton("Exit", (dialog, which) -> finishAffinity())
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        });
    }
}