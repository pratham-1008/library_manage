package adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.library.R;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {

    private List<String> studentList;

    public StudentAdapter(List<String> studentList) {
        this.studentList = studentList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtId, txtName, txtDept;

        public ViewHolder(View itemView) {
            super(itemView);
            txtId = itemView.findViewById(R.id.txtId);
            txtName = itemView.findViewById(R.id.txtName);
            txtDept = itemView.findViewById(R.id.txtDept);
        }
    }

    @Override
    public StudentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.student_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StudentAdapter.ViewHolder holder, int position) {
        String[] parts = studentList.get(position).split("\n");
        holder.txtId.setText(parts[0]);      // ID: ...
        holder.txtName.setText(parts[1]);    // Name: ...
        holder.txtDept.setText(parts[2]);    // Department: ...
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }
}
