package com.example.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "todolist.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "tasks";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DEADLINE = "deadline";
    private static final String COLUMN_DURATION = "duration";
    private static final String COLUMN_DESCRIPTION = "description";

    public TaskDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME + " TEXT," +
                COLUMN_DEADLINE + " TEXT," +
                COLUMN_DURATION + " INTEGER," +
                COLUMN_DESCRIPTION + " TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, task.getName());  // Use getter
        values.put(COLUMN_DEADLINE, formatDate(task.getDeadline()));  // Use getter
        values.put(COLUMN_DURATION, task.getDuration());  // Use getter
        values.put(COLUMN_DESCRIPTION, task.getDescriptions());  // Use getter
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                try {
                    tasks.add(new Task(
                            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                            parseDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DEADLINE))),
                            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DURATION)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                    ));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return tasks;
    }

    public void updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, task.getName());
        values.put(COLUMN_DEADLINE, formatDate(task.getDeadline()));
        values.put(COLUMN_DURATION, task.getDuration());
        values.put(COLUMN_DESCRIPTION, task.getDescriptions());
        // Corrected line: use _id instead of id
        db.update(TABLE_NAME, values, "_id=?", new String[]{String.valueOf(task.getId())});
        db.close();
    }

    public void deleteTask(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "_id=?", new String[]{String.valueOf(id)}); // Use "_id"
        db.close();
    }

    private String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(date);
    }

    private Date parseDate(String dateString) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.parse(dateString);
    }
}