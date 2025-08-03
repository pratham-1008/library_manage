package com.example.library;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;


import com.example.library.helpers.DatabaseHelper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AddStudentActivity extends Activity {

    EditText etId, etFirstName, etLastName;
    Spinner spinnerDepartment;
    Button btnAddStudent, btnUploadCSV;
    DatabaseHelper dbHelper;
    private static final int PICK_CSV_FILE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        etId = findViewById(R.id.etId);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        spinnerDepartment = findViewById(R.id.spinnerDepartment);

        btnAddStudent = findViewById(R.id.btnAddStudent);
        btnUploadCSV = findViewById(R.id.btnUploadCSV);

        dbHelper = new DatabaseHelper(this);

        // Department options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.departments, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDepartment.setAdapter(adapter);

        btnAddStudent.setOnClickListener(v -> {
            try {
                int id = Integer.parseInt(etId.getText().toString().trim());

                String firstName = etFirstName.getText().toString().trim();
                String lastName = etLastName.getText().toString().trim();
                String department = spinnerDepartment.getSelectedItem().toString();

                boolean inserted = dbHelper.insertStudent(id, firstName, lastName, department);
                if (inserted) {
                    Toast.makeText(this, "Student added successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to add student", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Please enter valid values", Toast.LENGTH_SHORT).show();
            }
        });

        btnUploadCSV.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("text/csv");
            startActivityForResult(intent, PICK_CSV_FILE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_CSV_FILE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            importStudentsFromCSV(uri);
        }
    }

    private void importStudentsFromCSV(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            int success = 0, fail = 0;

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length >= 4) {
                    int id = Integer.parseInt(tokens[0].trim());
                    String firstName = tokens[1].trim();
                    String lastName = tokens[2].trim();
                    String department = tokens[3].trim();

                    boolean inserted = dbHelper.insertStudent(id, firstName, lastName, department);
                    if (inserted) success++;
                    else fail++;
                } else {
                    fail++;
                }
            }

            Toast.makeText(this, "Import finished. Success: " + success + ", Failed: " + fail, Toast.LENGTH_LONG).show();
            reader.close();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to import: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
