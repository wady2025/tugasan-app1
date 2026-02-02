package com.teknik.rekodtugasan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "tugasan.db";
    private static final int DATABASE_VERSION = 1;

    // Table name
    private static final String TABLE_TASKS = "tasks";

    // Column names
    private static final String COL_ID = "id";
    private static final String COL_CUSTOMER_NAME = "customer_name";
    private static final String COL_LOCATION = "location";
    private static final String COL_TASK_TYPE = "task_type";
    private static final String COL_DESCRIPTION = "description";
    private static final String COL_NOTES = "notes";
    private static final String COL_STATUS = "status";
    private static final String COL_DATE = "date";
    private static final String COL_TIME = "time";
    private static final String COL_PHOTOS = "photos";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_TASKS + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_CUSTOMER_NAME + " TEXT NOT NULL, "
                + COL_LOCATION + " TEXT NOT NULL, "
                + COL_TASK_TYPE + " TEXT NOT NULL, "
                + COL_DESCRIPTION + " TEXT NOT NULL, "
                + COL_NOTES + " TEXT, "
                + COL_STATUS + " TEXT NOT NULL, "
                + COL_DATE + " TEXT NOT NULL, "
                + COL_TIME + " TEXT NOT NULL, "
                + COL_PHOTOS + " TEXT"
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        onCreate(db);
    }

    // Add new task
    public long addTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(COL_CUSTOMER_NAME, task.customerName);
        values.put(COL_LOCATION, task.location);
        values.put(COL_TASK_TYPE, task.taskType);
        values.put(COL_DESCRIPTION, task.description);
        values.put(COL_NOTES, task.notes);
        values.put(COL_STATUS, task.status);
        values.put(COL_DATE, task.date);
        values.put(COL_TIME, task.time);
        values.put(COL_PHOTOS, listToJson(task.photos));

        long id = db.insert(TABLE_TASKS, null, values);
        db.close();
        return id;
    }

    // Get all tasks
    public List<Task> getAllTasks() {
        List<Task> taskList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_TASKS + " ORDER BY " + COL_ID + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Task task = new Task();
                task.id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID));
                task.customerName = cursor.getString(cursor.getColumnIndexOrThrow(COL_CUSTOMER_NAME));
                task.location = cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCATION));
                task.taskType = cursor.getString(cursor.getColumnIndexOrThrow(COL_TASK_TYPE));
                task.description = cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION));
                task.notes = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOTES));
                task.status = cursor.getString(cursor.getColumnIndexOrThrow(COL_STATUS));
                task.date = cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE));
                task.time = cursor.getString(cursor.getColumnIndexOrThrow(COL_TIME));
                
                String photosJson = cursor.getString(cursor.getColumnIndexOrThrow(COL_PHOTOS));
                task.photos = jsonToList(photosJson);

                taskList.add(task);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return taskList;
    }

    // Get single task
    public Task getTask(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TASKS, null, COL_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);

        Task task = null;
        if (cursor != null && cursor.moveToFirst()) {
            task = new Task();
            task.id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID));
            task.customerName = cursor.getString(cursor.getColumnIndexOrThrow(COL_CUSTOMER_NAME));
            task.location = cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCATION));
            task.taskType = cursor.getString(cursor.getColumnIndexOrThrow(COL_TASK_TYPE));
            task.description = cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION));
            task.notes = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOTES));
            task.status = cursor.getString(cursor.getColumnIndexOrThrow(COL_STATUS));
            task.date = cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE));
            task.time = cursor.getString(cursor.getColumnIndexOrThrow(COL_TIME));
            
            String photosJson = cursor.getString(cursor.getColumnIndexOrThrow(COL_PHOTOS));
            task.photos = jsonToList(photosJson);

            cursor.close();
        }

        db.close();
        return task;
    }

    // Update task
    public int updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(COL_CUSTOMER_NAME, task.customerName);
        values.put(COL_LOCATION, task.location);
        values.put(COL_TASK_TYPE, task.taskType);
        values.put(COL_DESCRIPTION, task.description);
        values.put(COL_NOTES, task.notes);
        values.put(COL_STATUS, task.status);
        values.put(COL_DATE, task.date);
        values.put(COL_TIME, task.time);
        values.put(COL_PHOTOS, listToJson(task.photos));

        int rowsAffected = db.update(TABLE_TASKS, values, COL_ID + "=?",
                new String[]{String.valueOf(task.id)});
        db.close();
        return rowsAffected;
    }

    // Delete task
    public void deleteTask(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS, COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    // Get task count
    public int getTaskCount() {
        String countQuery = "SELECT * FROM " + TABLE_TASKS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    // Helper method to convert List to JSON string
    private String listToJson(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "[]";
        }
        JSONArray jsonArray = new JSONArray(list);
        return jsonArray.toString();
    }

    // Helper method to convert JSON string to List
    private List<String> jsonToList(String jsonString) {
        List<String> list = new ArrayList<>();
        if (jsonString == null || jsonString.isEmpty() || jsonString.equals("[]")) {
            return list;
        }
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                list.add(jsonArray.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}
