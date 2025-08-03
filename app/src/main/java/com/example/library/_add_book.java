package com.example.library;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.library.helpers.DatabaseHelper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class _add_book extends Activity {

    EditText editTitle, editAuthor, editQuantity;
    Button btnAddManual, btnUploadExcel;
    DatabaseHelper dbHelper;

    private static final int PICK_EXCEL_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        editTitle = findViewById(R.id.editTitle);
        editAuthor = findViewById(R.id.editAuthor);
        editQuantity = findViewById(R.id.editQuantity);
        btnAddManual = findViewById(R.id.btnAddManual);
        btnUploadExcel = findViewById(R.id.btnUploadExcel);

        dbHelper = new DatabaseHelper(this);

        btnAddManual.setOnClickListener(v -> {
            String title = editTitle.getText().toString();
            String author = editAuthor.getText().toString();
            String quantityStr = editQuantity.getText().toString();

            if (title.isEmpty() || author.isEmpty() || quantityStr.isEmpty()) {
                Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
                return;
            }

            int quantity = Integer.parseInt(quantityStr);
            boolean success = dbHelper.insertBook(title, author, quantity);
            Toast.makeText(this, success ? "Book Added" : "Failed to Add Book", Toast.LENGTH_SHORT).show();
        });

        btnUploadExcel.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent, PICK_EXCEL_REQUEST);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_EXCEL_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            readExcelFile(fileUri);
        }
    }

    private void readExcelFile(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            int inserted = 0, failed = 0;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String title = parts[0].trim();
                    String author = parts[1].trim();
                    int quantity = Integer.parseInt(parts[2].trim());

                    boolean success = dbHelper.insertBook(title, author, quantity);
                    if (success) inserted++;
                    else failed++;
                }
            }
            reader.close();
            Toast.makeText(this, "Imported: " + inserted + " books", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
