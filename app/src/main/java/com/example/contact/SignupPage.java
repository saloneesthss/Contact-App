package com.example.contact;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class SignupPage extends AppCompatActivity {

    private TextInputEditText etUsername, etEmail, etPassword;
    private Button btnSignup;
    private TextView tvLogin;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);

        dbHelper = new DBHelper(this);

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignup = findViewById(R.id.btnSignup);
        tvLogin = findViewById(R.id.tvLogin);

        // Already have account → go to login
        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignupPage.this, LoginPage.class));
            finish();
        });

        btnSignup.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Enter full name");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Enter email");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Enter password");
            return;
        }

        boolean success = dbHelper.registerUser(username, email, password);

        if (success) {
            Toast.makeText(this, "Signup successful! Please login", Toast.LENGTH_SHORT).show();
            // Go to login page
            startActivity(new Intent(SignupPage.this, LoginPage.class));
            finish();
        } else {
            Toast.makeText(this, "User already exists!", Toast.LENGTH_SHORT).show();
        }
    }
}
