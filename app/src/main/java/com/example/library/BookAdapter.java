package com.example.library;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class BookAdapter extends ArrayAdapter<BookModel> {
    public BookAdapter(Context context, List<BookModel> books) {
        super(context, 0, books);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        BookModel book = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.book_list_item, parent, false);
        }

        TextView title = convertView.findViewById(R.id.textViewTitle);
        TextView author = convertView.findViewById(R.id.textViewAuthor);
        TextView quantity = convertView.findViewById(R.id.textViewQuantity);

        title.setText("📖 " + book.getTitle());
        author.setText("👤 " + book.getAuthor());
        quantity.setText("📦 Qty: " + book.getQuantity());

        return convertView;
    }
}
