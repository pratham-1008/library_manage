package com.example.library;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.library.helpers.DatabaseHelper;

public class MainActivity extends AppCompatActivity {

    EditText loginUsername, loginPassword;
    Button loginBtn;
    DatabaseHelper dbHelper;
    TextView tt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginUsername = findViewById(R.id.loginUsername);
        loginPassword = findViewById(R.id.loginPassword);
        loginBtn = findViewById(R.id.loginBtn);
        dbHelper = new DatabaseHelper(this);
        tt=findViewById(R.id.tt);

        loginBtn.setOnClickListener(v -> {
            String username = loginUsername.getText().toString().trim();
            String password = loginPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success = dbHelper.loginAdmin(username, password);
            if (success) {
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, AdminDashboardActivity.class));
               // finish(); // optional, so user can't go back to login with back button
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        });
        tt.setOnClickListener(v -> {
            startActivity(new Intent(this, signup.class));
        });
    }
}
