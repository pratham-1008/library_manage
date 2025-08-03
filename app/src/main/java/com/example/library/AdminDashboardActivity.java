package com.example.library;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.library.helpers.DatabaseHelper;
import com.example.library.helpers.issued_books;

public class AdminDashboardActivity extends AppCompatActivity {

    private Button btnAddStudent, btnAddBook, btnViewBooks, btnViewFines, btnViewIssuedBooks, btnIssueBook, btnViewPaidFines,btnViewStudent ;
            ImageButton btnRefresh;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Initialize buttons
        btnAddStudent = findViewById(R.id.btnAddStudent);
        btnAddBook = findViewById(R.id.btnAddBook);
        btnViewBooks = findViewById(R.id.btnViewBooks);
        btnViewFines = findViewById(R.id.btnViewFines);
        btnViewIssuedBooks = findViewById(R.id.btnViewIssuedBooks);
        btnIssueBook = findViewById(R.id.btnIssueBook);
        btnViewPaidFines = findViewById(R.id.btnViewPaidFines);
        btnRefresh=findViewById(R.id.refresh);
        btnViewStudent=findViewById(R.id.btnViewStudent);
        DatabaseHelper db = new DatabaseHelper(this);

        // Set up button listeners
        btnAddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Add Student activity
                startActivity(new Intent(AdminDashboardActivity.this, com.example.library.AddStudentActivity.class));
            }
        });

        btnAddBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Add Book activity
                startActivity(new Intent(AdminDashboardActivity.this, _add_book.class));
            }
        });
        btnRefresh.setOnClickListener(v -> {

            db.refreshAndHandleFines();
            Toast.makeText(this, "Overdue books moved to fines.", Toast.LENGTH_SHORT).show();
        });


        btnViewBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the View Books activity
                startActivity(new Intent(AdminDashboardActivity.this, all_books.class));
            }
        });

        btnViewFines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.updateAllUnpaidFines(); // ✔️ Recalculate fine before opening fines activity
                startActivity(new Intent(AdminDashboardActivity.this, viewfines.class));
            }
        });


        btnViewIssuedBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the View Issued Books activity
                startActivity(new Intent(AdminDashboardActivity.this, issued_books.class));
            }
        });

        btnIssueBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Issue Book activity
                startActivity(new Intent(AdminDashboardActivity.this, IssueBookActivity.class));
            }
        });

        btnViewPaidFines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the View Paid Fines activity
                startActivity(new Intent(AdminDashboardActivity.this, ViewPaidFinesActivity.class));
            }

        });
        btnViewStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboardActivity.this, ViewStudentsActivity.class));
            }
        });


    }
}
