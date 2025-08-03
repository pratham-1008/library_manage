package adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.library.R;

public class PaidFinesAdapter extends BaseAdapter {

    private Context context;
    private Cursor cursor;

    public PaidFinesAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("Range")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_paid_fine, parent, false);
        }

        TextView studentName = convertView.findViewById(R.id.studentName);
        TextView fineAmount = convertView.findViewById(R.id.fineAmount);
        TextView paidOn = convertView.findViewById(R.id.paidOn);

        cursor.moveToPosition(position);

        // Assuming the columns are ordered as follows: student_name, fine_amount, paid_on
        studentName.setText(cursor.getString(cursor.getColumnIndex("student_name")));
        fineAmount.setText("₹" + cursor.getString(cursor.getColumnIndex("fine_amount")));
        paidOn.setText(cursor.getString(cursor.getColumnIndex("paid_on")));

        return convertView;
    }
}
