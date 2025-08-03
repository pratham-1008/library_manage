package com.example.library;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.library.helpers.DatabaseHelper;
import java.util.List;

import adapters.StudentAdapter;

public class ViewStudentsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_student);

        recyclerView = findViewById(R.id.recyclerStudents);
        db = new DatabaseHelper(this);

        List<String> students = db.getAllStudentsSortedById();
        StudentAdapter adapter = new StudentAdapter(students);
        recyclerView.setAdapter(adapter);
    }
}
