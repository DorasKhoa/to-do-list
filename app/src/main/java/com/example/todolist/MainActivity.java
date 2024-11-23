package com.example.todolist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Task> taskList = new ArrayList<>();
    private TaskAdapter adapter;
    private ListView listViewTask;
    private TaskDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listViewTask = findViewById(R.id.listviewTask);
        dbHelper = new TaskDbHelper(this); // Khởi tạo dbHelper trước
        loadTasksFromDb();
        adapter = new TaskAdapter(this, taskList);
        listViewTask.setAdapter(adapter);
    }


    private void loadTasksFromDb() {
        taskList.clear();
        try {
            List<Task> tasks = dbHelper.getAllTasks();
            taskList.addAll(tasks);
        } catch (SQLiteException e) {
            Log.e("MainActivity", "Error loading tasks from DB: ", e);
            Toast.makeText(this, "Error loading tasks", Toast.LENGTH_SHORT).show();
        }
    }


    public void onClickAdd(View v) {
        Intent intent = new Intent(this, AddTaskActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            loadTasksFromDb();
            adapter.notifyDataSetChanged();
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            loadTasksFromDb();
            adapter.notifyDataSetChanged();
        }
    }

    public class TaskAdapter extends ArrayAdapter<Task> {
        public TaskAdapter(Context context, ArrayList<Task> tasks) {
            super(context, 0, tasks);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Task task = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.task_item, parent, false);
            }
            TextView tvTaskName = convertView.findViewById(R.id.tvTaskName);
            TextView tvDeadline = convertView.findViewById(R.id.tvDeadline);
            TextView tvDuration = convertView.findViewById(R.id.tvDuration);
            TextView tvDescriptions = convertView.findViewById(R.id.tvDescriptions);

            tvTaskName.setText(task.getName()); // Use getter
            tvDeadline.setText(task.getDeadline() != null ? task.getDeadline().toString().substring(0, 10) : ""); // Use getter
            tvDuration.setText(String.valueOf(task.getDuration())); // Use getter
            tvDescriptions.setText(task.getDescriptions()); // Use getter

            Button btnEdit = convertView.findViewById(R.id.btnEdit);
            Button btnDelete = convertView.findViewById(R.id.btnDelete);

            btnEdit.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), AddTaskActivity.class);
                intent.putExtra("editTask", taskList.get(position));
                intent.putExtra("position", position);
                ((AppCompatActivity) getContext()).startActivityForResult(intent, 2);
            });

            btnDelete.setOnClickListener(v -> {
                dbHelper.deleteTask(task.getId());
                loadTasksFromDb();
                notifyDataSetChanged();
            });

            return convertView;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();// Close the database connection when the activity is destroyed
    }
}