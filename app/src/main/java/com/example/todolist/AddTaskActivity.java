package com.example.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddTaskActivity extends AppCompatActivity {

    private TaskDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        dbHelper = new TaskDbHelper(this);
        DatePicker dp = findViewById(R.id.dpDeadline);
        Calendar c = Calendar.getInstance();
        dp.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), null);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("editTask")) {
            Task task = (Task) intent.getSerializableExtra("editTask");
            if (task != null) { // Kiểm tra null ở đây
                EditText etTaskName = findViewById(R.id.etTaskName);
                EditText etDescription = findViewById(R.id.etmDescription);
                EditText etDuration = findViewById(R.id.etDuration);
                DatePicker dpDeadline = findViewById(R.id.dpDeadline);

                etTaskName.setText(task.name);
                etDescription.setText(task.descriptions);
                etDuration.setText(String.valueOf(task.duration));

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(task.deadline);
                dpDeadline.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), null);
            } else {
                Toast.makeText(this, "Error loading task data", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onClickAddTask(View v) {
        try {
            String name = ((EditText) findViewById(R.id.etTaskName)).getText().toString().trim();
            String descriptions = ((EditText) findViewById(R.id.etmDescription)).getText().toString().trim();
            int duration;
            try {
                duration = Integer.parseInt(((EditText) findViewById(R.id.etDuration)).getText().toString().trim());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid duration format", Toast.LENGTH_SHORT).show();
                return;
            }

            DatePicker dp = findViewById(R.id.dpDeadline);
            String dateText = dp.getDayOfMonth() + "/" + (dp.getMonth() + 1) + "/" + dp.getYear();
            Date deadline;
            try {
                deadline = new SimpleDateFormat("dd/MM/yyyy").parse(dateText);
            } catch (ParseException e) {
                Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
                return;
            }

            if (name.isEmpty() || descriptions.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = getIntent();
            Task t;
            if (intent != null && intent.hasExtra("editTask")) {
                int id = ((Task) intent.getSerializableExtra("editTask")).getId();
                t = new Task(id, name, deadline, duration, descriptions);
                dbHelper.updateTask(t);

            } else {
                t = new Task(name, deadline, duration, descriptions);
                dbHelper.addTask(t);
            }
            setResult(RESULT_OK);
            finish();
        } catch (Exception e) {
            Log.e("AddTaskActivity", "Error adding/updating task: ", e);
            Toast.makeText(this, "Error adding/updating task. Check logs for details.", Toast.LENGTH_LONG).show();
        } finally {
            dbHelper.close();
        }
    }
}