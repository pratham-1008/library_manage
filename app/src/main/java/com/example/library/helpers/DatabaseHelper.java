package com.example.library.helpers;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "library.db";
    public static final int DATABASE_VERSION = 2;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tables (admins, departments, classes, students, books, issued_books, fines, and paid_fines)
        db.execSQL("CREATE TABLE admins (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "username TEXT UNIQUE," +
                "email TEXT," +
                "password TEXT)");
        db.execSQL("CREATE TABLE books (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT," +
                "author TEXT," +
                "quantity INTEGER)");

        db.execSQL("CREATE TABLE IF NOT EXISTS issued_books (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "student_name TEXT," +
                "book_name TEXT," +
                "issue_date TEXT," +
                "due_date TEXT," +
                "return_date TEXT," +
                "is_returned INTEGER)");

        db.execSQL("CREATE TABLE IF NOT EXISTS students (" +
                "id INTEGER PRIMARY KEY," +
                "name TEXT," +
                "surname TEXT," +
                "department TEXT)");



        db.execSQL("CREATE TABLE IF NOT EXISTS fines (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "student_id INTEGER, " +
                "student_name TEXT, " +
                "book_name TEXT, " +
                "fine_amount REAL, " +
                "is_paid INTEGER DEFAULT 0, " +
                "due_date TEXT" +
                ");");





        db.execSQL("CREATE TABLE paid_fines (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "student_id INTEGER," +             // Added field for student ID
                "student_name TEXT," +
                "book_id INTEGER," +
                "fine_amount REAL," +
                "paid_on TEXT)");


    }
    public boolean registerAdmin(String name, String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if username already exists
        Cursor cursor = db.rawQuery("SELECT id FROM admins WHERE username = ?", new String[]{username});
        if (cursor.moveToFirst()) {
            cursor.close();
            return false; // Username already exists
        }

        cursor.close();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("username", username);
        values.put("email", email);
        values.put("password", password);

        long result = db.insert("admins", null, values);
        return result != -1;
    }
    public boolean loginAdmin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM admins WHERE username = ? AND password = ?", new String[]{username, password});

        boolean success = cursor.moveToFirst();
        cursor.close();
        return success;
    }


    public boolean insertStudent(int id, String firstName, String lastName, String department) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Add the student details to the values object
        values.put("id", id);  // Integer ID
        values.put("name", firstName);
        values.put("surname", lastName);
        values.put("department", department);

        // Insert the student data into the "students" table
        long result = db.insert("students", null, values);

        // If the insertion is successful, the result will not be -1
        return result != -1;
    }
    public List<String> getAllStudentsSortedById() {
        List<String> students = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT id, name, surname, department FROM students ORDER BY id ASC", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String surname = cursor.getString(cursor.getColumnIndexOrThrow("surname"));
                String department = cursor.getString(cursor.getColumnIndexOrThrow("department"));

                String studentInfo = "ID: " + id + "\nName: " + name + " " + surname + "\nDept: " + department;
                students.add(studentInfo);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return students;
    }




    public void updateBookQuantity(int bookId, int quantityChange) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Get the current quantity of the book
        int currentQuantity = getBookQuantity(bookId);

        // Update the quantity by adding the quantityChange
        int newQuantity = currentQuantity + quantityChange;

        // Ensure that quantity is not negative
        if (newQuantity < 0) {
            return; // Avoid updating to a negative quantity
        }

        // Prepare the values to update
        ContentValues values = new ContentValues();
        values.put("quantity", newQuantity);

        // Update the quantity in the books table
        int rowsAffected = db.update("books", values, "id = ?", new String[]{String.valueOf(bookId)});

        if (rowsAffected == 0) {
            // Handle failure (if needed)
        }
    }



    public boolean checkBookAvailability(String bookName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("books", new String[]{"title", "quantity"},
                "title = ?", new String[]{bookName},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") int quantity = cursor.getInt(cursor.getColumnIndex("quantity"));
            cursor.close();
            return quantity > 0; // Book is available if quantity > 0
        }
        return false; // Book not found
    }



    // Check if book is available

    public boolean insertBook(String title, String author, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();  // Get writable database

        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);  // Insert title
        contentValues.put("author", author);  // Insert author
        contentValues.put("quantity", quantity);  // Insert quantity

        // Insert data into the "books" table
        long result = db.insert("books", null, contentValues);
        db.close();  // Close the database connection

        // If the result is -1, the insert failed. Otherwise, it was successful.
        return result != -1;
    }
    // Get book quantity (for updating)
    private int getBookQuantity(int bookId) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Execute query to fetch quantity of the book by its book_id
        Cursor cursor = db.rawQuery("SELECT quantity FROM books WHERE id=?", new String[]{String.valueOf(bookId)});

        // If the cursor is not null and it moves to the first result
        if (cursor != null && cursor.moveToFirst()) {
            // Fetch the quantity from the cursor
            @SuppressLint("Range") int quantity = cursor.getInt(cursor.getColumnIndex("quantity"));

            // Close the cursor to release resources
            cursor.close();

            // Return the retrieved quantity
            return quantity;
        }

        // Return 0 if the book is not found in the database
        return 0;
    }

    public boolean isBookAvailable(int bookId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT quantity FROM books WHERE id=?", new String[]{String.valueOf(bookId)});
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") int quantity = cursor.getInt(cursor.getColumnIndex("quantity"));
            cursor.close();
            return quantity > 0; // Return true if quantity is greater than 0
        }
        return false;
    }
    public int getBookIdByName(String bookName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM books WHERE title = ?", new String[]{bookName});
        if (cursor != null && cursor.moveToFirst()) {
            int bookId = cursor.getInt(0);
            cursor.close();
            return bookId;
        }
        cursor.close();
        return -1;
    }

    public int getStudentIdByName(String studentName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM students WHERE name = ?", new String[]{studentName});
        if (cursor != null && cursor.moveToFirst()) {
            int studentId = cursor.getInt(0);
            cursor.close();
            return studentId;
        }
        cursor.close();
        return -1;
    }


    // Issue book (reduce quantity, record issue details)
    public boolean issueBook(String studentName, String bookName) {
        int bookId = getBookIdByName(bookName);
        if (bookId == -1 || !isBookAvailable(bookId)) {
            return false; // Book not available
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        String issueDate = getCurrentDate();
        String dueDate = getDueDate(7); // 7 days later

        int studentId = getStudentIdByName(studentName);
        if (studentId == -1) {
            return false; // Student not found
        }

        // Add values to be inserted into issued_books table
        values.put("student_name", studentName);
        values.put("book_name", bookName);
        values.put("issue_date", issueDate);
        values.put("due_date", dueDate);
        values.put("return_date", (String) null); // No return date yet
        values.put("is_returned", 0); // Not returned yet

        // Insert into issued_books table
        long insertIssuedBooksResult = db.insert("issued_books", null, values);

        if (insertIssuedBooksResult != -1) {
            updateBookQuantity(bookId, -1); // Decrease quantity by 1
            return true;
        }
        return false;
    }



    // Helper method to get current date in YYYY-MM-DD format
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    private String getDueDate(int daysAfter) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, daysAfter);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }
    public List<Map<String, String>> getIssuedBooksList() {
        List<Map<String, String>> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT student_name, book_name, issue_date, due_date FROM issued_books WHERE is_returned = 0", null);
        if (cursor.moveToFirst()) {
            do {
                Map<String, String> item = new HashMap<>();
                item.put("student_name", cursor.getString(cursor.getColumnIndexOrThrow("student_name")));
                item.put("book_name", cursor.getString(cursor.getColumnIndexOrThrow("book_name")));
                item.put("issue_date", cursor.getString(cursor.getColumnIndexOrThrow("issue_date")));
                item.put("due_date", cursor.getString(cursor.getColumnIndexOrThrow("due_date")));
                list.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
    public void refreshAndHandleFines() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM issued_books WHERE is_returned = 0", null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int issueId = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range") String studentName = cursor.getString(cursor.getColumnIndex("student_name"));
                @SuppressLint("Range") String bookName = cursor.getString(cursor.getColumnIndex("book_name"));
                @SuppressLint("Range") String dueDate = cursor.getString(cursor.getColumnIndex("due_date")); // ✔️ Get due date

                double fine = calculateFine(dueDate);

                if (fine > 0) {
                    int studentId = getStudentIdByName(studentName);

                    // Check if fine already exists (optional safety)
                    Cursor fineCursor = db.rawQuery(
                            "SELECT * FROM fines WHERE student_id=? AND book_name=? AND is_paid=0",
                            new String[]{String.valueOf(studentId), bookName});

                    if (fineCursor.getCount() == 0) {
                        insertFine(studentId, studentName, bookName, (float) fine, dueDate); // ✔️ Pass dueDate here
                        db.delete("issued_books", "id=?", new String[]{String.valueOf(issueId)});
                    }
                    fineCursor.close();
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    private void insertFine(int studentId, String studentName, String bookName, float fineAmount, String dueDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("student_id", studentId);
        values.put("student_name", studentName);
        values.put("book_name", bookName);
        values.put("fine_amount", fineAmount);
        values.put("is_paid", 0);
        values.put("due_date", dueDate); // ✔️ Insert due_date

        db.insert("fines", null, values);
    }










    // Calculate fine for overdue books (7 days after due date)
    public double calculateFine(String dueDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date due = sdf.parse(dueDate);
            Date today = new Date();
            long diffInMillies = today.getTime() - due.getTime();
            long daysLate = diffInMillies / (1000 * 60 * 60 * 24);

            if (daysLate > 0) {
                return daysLate * 5.0; // e.g. ₹5 fine per day
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }
// using when we go to onclick view fine to update fine value
    public void updateAllUnpaidFines() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, due_date FROM fines WHERE is_paid = 0", null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int fineId = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range") String dueDate = cursor.getString(cursor.getColumnIndex("due_date"));

                double newFineAmount = calculateFine(dueDate);

                ContentValues values = new ContentValues();
                values.put("fine_amount", newFineAmount);

                db.update("fines", values, "id = ?", new String[]{String.valueOf(fineId)});
            } while (cursor.moveToNext());
            cursor.close();
        }
    }







    // Method to retrieve unpaid fines
    public ArrayList<Fine> getUnpaidFines() {
        ArrayList<Fine> finesList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, student_id, book_id, fine_amount, is_paid FROM fines WHERE is_paid = 0", null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range") int studentId = cursor.getInt(cursor.getColumnIndex("student_id"));
                @SuppressLint("Range") int bookId = cursor.getInt(cursor.getColumnIndex("book_id"));
                @SuppressLint("Range") float fineAmount = cursor.getFloat(cursor.getColumnIndex("fine_amount"));

                finesList.add(new Fine(id, studentId, bookId, fineAmount));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return finesList;
    }

    // Method to mark fine as paid
    public boolean markFineAsPaid(int fineId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_paid", 1); // Mark as paid
        values.put("paid_on", getCurrentDate()); // Set the paid date

        int rowsAffected = db.update("fines", values, "id=?", new String[]{String.valueOf(fineId)});
        return rowsAffected > 0; // Return true if update was successful
    }
    public Cursor getPaidFines() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT student_id, student_name, fine_amount, paid_on FROM paid_fines ", null);
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle upgrading the database schema
        if (oldVersion < 2) { // Check if the version is less than 2
            // Add the 'department' column to the 'students' table
            db.execSQL("ALTER TABLE students ADD COLUMN department TEXT");
        }
    }

    public Cursor getAllBooks() {
        SQLiteDatabase db = this.getReadableDatabase();
        // Query to get all columns from the "books" table
        return db.rawQuery("SELECT * FROM books", null);
    }


    // Fine model class to represent fine data
    public static class Fine {
        private int id;


        public Fine(int id, int studentId, int bookId, float fineAmount) {
            this.id = id;

        }

        // Getters and setters
        public int getId() {
            return id;
        }


    }
}
