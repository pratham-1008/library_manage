package com.example.library.helpers;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.library.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class issued_books extends AppCompatActivity {
    ListView issuedBooksList;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issued_books);

        issuedBooksList = findViewById(R.id.issuedBooksList);
        db = new DatabaseHelper(this);

        List<Map<String, String>> issuedList = db.getIssuedBooksList();

        List<String> displayList = new ArrayList<>();
        for (Map<String, String> item : issuedList) {
            String line = "Student: " + item.get("student_name") +
                    "\nBook: " + item.get("book_name") +
                    "\nIssued: " + item.get("issue_date") +
                    "\nDue: " + item.get("due_date");
            displayList.add(line);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
        issuedBooksList.setAdapter(adapter);
    }
}
