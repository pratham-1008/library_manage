package com.example.library;

import android.content.Intent; // ✅ Required import
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.library.helpers.DatabaseHelper;

public class signup extends AppCompatActivity {

    EditText nameInput, usernameInput, emailInput, passwordInput;
    Button signupBtn;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        nameInput = findViewById(R.id.nameInput);
        usernameInput = findViewById(R.id.usernameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        signupBtn = findViewById(R.id.signupBtn);
        dbHelper = new DatabaseHelper(this);

        signupBtn.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String username = usernameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (name.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean registered = dbHelper.registerAdmin(name, username, email, password);
            if (registered) {
                Toast.makeText(this, "Registered successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finish(); // optional: prevents coming back to signup page on back press
            } else {
                Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
