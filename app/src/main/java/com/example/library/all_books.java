package com.example.library;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.library.helpers.DatabaseHelper;

import java.util.ArrayList;

public class all_books extends AppCompatActivity {

        private ListView listViewBooks;
        private DatabaseHelper dbHelper;
        private Cursor cursor;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_all_books);

                listViewBooks = findViewById(R.id.listViewBooks);
                dbHelper = new DatabaseHelper(this);

                cursor = dbHelper.getAllBooks();
                ArrayList<BookModel> bookList = new ArrayList<>();

                if (cursor != null && cursor.moveToFirst()) {
                        do {
                                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                                String author = cursor.getString(cursor.getColumnIndexOrThrow("author"));
                                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));

                                bookList.add(new BookModel(title, author, quantity));
                        } while (cursor.moveToNext());
                }

                BookAdapter adapter = new BookAdapter(this, bookList);
                listViewBooks.setAdapter(adapter);
        }

        @Override
        protected void onDestroy() {
                super.onDestroy();
                if (cursor != null) cursor.close();
                if (dbHelper != null) dbHelper.close();
        }
}
