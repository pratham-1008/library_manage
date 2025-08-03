package com.example.library;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.library.helpers.DatabaseHelper;

public class ViewPaidFinesActivity extends AppCompatActivity {

    private LinearLayout paidFinesContainer;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_paid_fines);

        paidFinesContainer = findViewById(R.id.paidFinesContainer);
        dbHelper = new DatabaseHelper(this);

        Cursor cursor = dbHelper.getPaidFines();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int studentId = cursor.getInt(cursor.getColumnIndexOrThrow("student_id"));
                String studentName = cursor.getString(cursor.getColumnIndexOrThrow("student_name"));
                double fineAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("fine_amount"));
                String paidOn = cursor.getString(cursor.getColumnIndexOrThrow("paid_on"));

                LayoutInflater inflater = LayoutInflater.from(this);
                View itemView = inflater.inflate(R.layout.item_paid_fine, paidFinesContainer, false);

                TextView idView = itemView.findViewById(R.id.studentId);
                TextView nameView = itemView.findViewById(R.id.studentName);
                TextView amountView = itemView.findViewById(R.id.fineAmount);
                TextView paidOnView = itemView.findViewById(R.id.paidOn);

                idView.setText(String.valueOf(studentId));
                nameView.setText(studentName);
                amountView.setText(String.format("₹%.2f", fineAmount));
                paidOnView.setText(paidOn);

                paidFinesContainer.addView(itemView);
            } while (cursor.moveToNext());

            cursor.close(); // ✅ Close the cursor
        } else {
            Toast.makeText(this, "No paid fines available", Toast.LENGTH_SHORT).show();
        }
    }
}
