package com.example.library;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.library.helpers.DatabaseHelper;

public class IssueBookActivity extends AppCompatActivity {

    private EditText studentIdEditText, studentNameEditText, bookNameEditText;
    private Button issueBookButton;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_book);

        dbHelper = new DatabaseHelper(this);

        studentIdEditText = findViewById(R.id.student_id);
        studentNameEditText = findViewById(R.id.student_name);
        bookNameEditText = findViewById(R.id.book_name);
        issueBookButton = findViewById(R.id.issue_book_button);

        issueBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                issueBook();
            }
        });
    }

    private void issueBook() {
        String studentIdStr = studentIdEditText.getText().toString().trim();
        String studentName = studentNameEditText.getText().toString().trim();
        String bookName = bookNameEditText.getText().toString().trim();

        if (studentName.isEmpty() || bookName.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if book exists and is available
        if (dbHelper.checkBookAvailability(bookName)) {
            // Issue the book
            boolean success = dbHelper.issueBook(studentName, bookName);
            if (success) {
                Toast.makeText(this, "Book issued successfully.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to issue the book.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Book is not available.", Toast.LENGTH_SHORT).show();
        }
    }
}
