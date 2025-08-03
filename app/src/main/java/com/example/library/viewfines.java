package com.example.library;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.library.helpers.DatabaseHelper;

public class viewfines extends AppCompatActivity {

    private LinearLayout finesLinearLayout;
    private Button updateButton;
    private SQLiteDatabase db;
    DatabaseHelper dbHelper = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewfines);

        finesLinearLayout = findViewById(R.id.linearLayoutFines);
        updateButton = findViewById(R.id.buttonUpdate);

        // Open the database for reading and writing
        db = dbHelper.getWritableDatabase();

        loadAndDisplayFines();

        updateButton.setOnClickListener(v -> markCheckedFinesAsPaid());
    }

    private void loadAndDisplayFines() {
        // Open the database for reading
        SQLiteDatabase dbForReading = dbHelper.getReadableDatabase();

        Cursor cursor = dbForReading.rawQuery(
                "SELECT id, student_id, student_name, book_name, fine_amount, is_paid FROM fines WHERE is_paid = 0", null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int fineId = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range") String studentId = cursor.getString(cursor.getColumnIndex("student_id"));
                @SuppressLint("Range") String studentName = cursor.getString(cursor.getColumnIndex("student_name"));
                @SuppressLint("Range") String bookName = cursor.getString(cursor.getColumnIndex("book_name"));
                @SuppressLint("Range") String fineAmount = "₹" + cursor.getDouble(cursor.getColumnIndex("fine_amount"));

                addFineItem(fineId, studentName, studentId, bookName, fineAmount, false);
            } while (cursor.moveToNext());
        }

        cursor.close();
        dbForReading.close(); // Close after reading
    }

    private void addFineItem(int fineId, String studentName, String studentId, String bookTitle, String fineAmount, boolean isPaid) {
        View fineItemView = LayoutInflater.from(this).inflate(R.layout.item_fine, null);

        TextView studentNameTextView = fineItemView.findViewById(R.id.textViewStudentName);
        TextView studentIdTextView = fineItemView.findViewById(R.id.textViewStudentId);
        TextView bookTitleTextView = fineItemView.findViewById(R.id.textViewBookTitle);
        TextView fineAmountTextView = fineItemView.findViewById(R.id.textViewFineAmount);
        CheckBox isPaidCheckBox = fineItemView.findViewById(R.id.checkBoxIsPaid);

        // Set values
        studentNameTextView.setText("Student Name: " + studentName);
        studentIdTextView.setText("Student ID: " + studentId);
        bookTitleTextView.setText("Book: " + bookTitle);
        fineAmountTextView.setText("Fine: " + fineAmount);
        isPaidCheckBox.setChecked(isPaid);

        // Store the fine ID as a tag
        isPaidCheckBox.setTag(fineId);

        finesLinearLayout.addView(fineItemView);
    }

    private void markCheckedFinesAsPaid() {
        // Use a writable database for updating
        SQLiteDatabase dbForWriting = dbHelper.getWritableDatabase();

        int childCount = finesLinearLayout.getChildCount();

        for (int i = childCount - 1; i >= 0; i--) {
            View fineItemView = finesLinearLayout.getChildAt(i);
            CheckBox isPaidCheckBox = fineItemView.findViewById(R.id.checkBoxIsPaid);

            if (isPaidCheckBox.isChecked()) {
                int fineId = (int) isPaidCheckBox.getTag();

                // Fetch fine details from 'fines' table, excluding the 'book_id' column
                Cursor cursor = dbForWriting.rawQuery("SELECT student_id, student_name, fine_amount FROM fines WHERE id=?",
                        new String[]{String.valueOf(fineId)});

                if (cursor.moveToFirst()) {
                    int studentId = cursor.getInt(cursor.getColumnIndexOrThrow("student_id"));
                    String studentName = cursor.getString(cursor.getColumnIndexOrThrow("student_name"));
                    double fineAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("fine_amount"));

                    // Insert into paid_fines table (no 'book_id' column)
                    ContentValues paidValues = new ContentValues();
                    paidValues.put("student_id", studentId);
                    paidValues.put("student_name", studentName);
                    paidValues.put("fine_amount", fineAmount); // Removed 'book_id' insertion
                    String paidOnDate = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
                    paidValues.put("paid_on", paidOnDate);

                    long insertId = dbForWriting.insert("paid_fines", null, paidValues);

                    if (insertId != -1) {
                        // DELETE from fines table
                        int deletedRows = dbForWriting.delete("fines", "id=?", new String[]{String.valueOf(fineId)});
                        if (deletedRows > 0) {
                            finesLinearLayout.removeViewAt(i);
                            Toast.makeText(this, "Fine paid and removed from record.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Error deleting fine.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Error adding to paid fines.", Toast.LENGTH_SHORT).show();
                    }
                }
                cursor.close();
            }
        }

        dbForWriting.close(); // Close after writing
    }

}
