package com.teknik.rekodtugasan;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 101;
    
    private ListView taskListView;
    private FloatingActionButton fabAddTask;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private DatabaseHelper dbHelper;
    private List<String> currentPhotos;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize
        dbHelper = new DatabaseHelper(this);
        taskList = new ArrayList<>();
        currentPhotos = new ArrayList<>();

        // Setup views
        taskListView = findViewById(R.id.taskListView);
        fabAddTask = findViewById(R.id.fabAddTask);

        // Load tasks
        loadTasks();

        // Setup adapter
        taskAdapter = new TaskAdapter(this, taskList);
        taskListView.setAdapter(taskAdapter);

        // Add task button
        fabAddTask.setOnClickListener(v -> showAddTaskDialog());
    }

    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_task, null);
        builder.setView(dialogView);

        EditText etCustomerName = dialogView.findViewById(R.id.etCustomerName);
        EditText etLocation = dialogView.findViewById(R.id.etLocation);
        EditText etDescription = dialogView.findViewById(R.id.etDescription);
        EditText etNotes = dialogView.findViewById(R.id.etNotes);
        Spinner spinnerTaskType = dialogView.findViewById(R.id.spinnerTaskType);
        Spinner spinnerStatus = dialogView.findViewById(R.id.spinnerStatus);
        Button btnTakePhoto = dialogView.findViewById(R.id.btnTakePhoto);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        TextView tvPhotoCount = dialogView.findViewById(R.id.tvPhotoCount);

        // Setup spinners
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this,
                R.array.task_types, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTaskType.setAdapter(typeAdapter);

        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(this,
                R.array.task_status, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        currentPhotos.clear();
        updatePhotoCount(tvPhotoCount);

        AlertDialog dialog = builder.create();

        btnTakePhoto.setOnClickListener(v -> {
            if (checkCameraPermission()) {
                openCamera();
            } else {
                requestCameraPermission();
            }
        });

        btnSave.setOnClickListener(v -> {
            String customerName = etCustomerName.getText().toString().trim();
            String location = etLocation.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String notes = etNotes.getText().toString().trim();
            String taskType = spinnerTaskType.getSelectedItem().toString();
            String status = spinnerStatus.getSelectedItem().toString();

            if (customerName.isEmpty() || location.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Sila isi semua medan wajib", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create task object
            Task task = new Task();
            task.customerName = customerName;
            task.location = location;
            task.description = description;
            task.notes = notes;
            task.taskType = taskType;
            task.status = status;
            task.date = getCurrentDate();
            task.time = getCurrentTime();
            task.photos = new ArrayList<>(currentPhotos);

            // Save to database
            long id = dbHelper.addTask(task);
            if (id > 0) {
                task.id = id;
                taskList.add(0, task);
                taskAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Tugasan berjaya disimpan!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Gagal menyimpan tugasan", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) 
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, 
                new String[]{Manifest.permission.CAMERA}, 
                CAMERA_PERMISSION_CODE);
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Tugasan_" + System.currentTimeMillis());
        values.put(MediaStore.Images.Media.DESCRIPTION, "Gambar tugasan");
        
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                String encodedImage = encodeImageToBase64(bitmap);
                currentPhotos.add(encodedImage);
                
                // Update photo count in dialog if visible
                View dialogView = findViewById(R.id.tvPhotoCount);
                if (dialogView != null) {
                    updatePhotoCount((TextView) dialogView);
                }
                
                Toast.makeText(this, "Gambar berjaya ditambah!", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Gagal memuat gambar", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void updatePhotoCount(TextView tvPhotoCount) {
        if (tvPhotoCount != null) {
            tvPhotoCount.setText(currentPhotos.size() + " gambar ditambah");
        }
    }

    private void loadTasks() {
        taskList.clear();
        taskList.addAll(dbHelper.getAllTasks());
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, 
                                         @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Kebenaran kamera diperlukan", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Task Adapter
    private class TaskAdapter extends ArrayAdapter<Task> {
        
        public TaskAdapter(Activity context, List<Task> tasks) {
            super(context, 0, tasks);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            Task task = getItem(position);
            
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.item_task, parent, false);
            }

            TextView tvCustomerName = convertView.findViewById(R.id.tvCustomerName);
            TextView tvTaskType = convertView.findViewById(R.id.tvTaskType);
            TextView tvLocation = convertView.findViewById(R.id.tvLocation);
            TextView tvDate = convertView.findViewById(R.id.tvDate);
            TextView tvStatus = convertView.findViewById(R.id.tvStatus);
            TextView tvDescription = convertView.findViewById(R.id.tvDescription);
            TextView tvPhotoCount = convertView.findViewById(R.id.tvPhotoCount);
            Button btnDelete = convertView.findViewById(R.id.btnDelete);
            Button btnView = convertView.findViewById(R.id.btnView);

            if (task != null) {
                tvCustomerName.setText("🔧 " + task.customerName);
                tvTaskType.setText("Jenis: " + task.taskType);
                tvLocation.setText("📍 " + task.location);
                tvDate.setText("📅 " + task.date + " • " + task.time);
                tvStatus.setText(task.status);
                tvDescription.setText(task.description);
                tvPhotoCount.setText("📷 " + task.photos.size() + " gambar");

                // Set status color
                int statusColor;
                switch (task.status) {
                    case "Selesai":
                        statusColor = getResources().getColor(android.R.color.holo_green_dark);
                        break;
                    case "Dalam Proses":
                        statusColor = getResources().getColor(android.R.color.holo_orange_dark);
                        break;
                    default:
                        statusColor = getResources().getColor(android.R.color.holo_red_dark);
                        break;
                }
                tvStatus.setTextColor(statusColor);

                btnDelete.setOnClickListener(v -> {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Padam Tugasan")
                            .setMessage("Adakah anda pasti mahu memadam tugasan ini?")
                            .setPositiveButton("Padam", (dialog, which) -> {
                                dbHelper.deleteTask(task.id);
                                taskList.remove(position);
                                notifyDataSetChanged();
                                Toast.makeText(getContext(), "Tugasan dipadam", Toast.LENGTH_SHORT).show();
                            })
                            .setNegativeButton("Batal", null)
                            .show();
                });

                btnView.setOnClickListener(v -> showTaskDetails(task));
            }

            return convertView;
        }
    }

    private void showTaskDetails(Task task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_task_details, null);
        builder.setView(dialogView);

        TextView tvCustomer = dialogView.findViewById(R.id.tvDetailCustomer);
        TextView tvLocation = dialogView.findViewById(R.id.tvDetailLocation);
        TextView tvType = dialogView.findViewById(R.id.tvDetailType);
        TextView tvDate = dialogView.findViewById(R.id.tvDetailDate);
        TextView tvStatus = dialogView.findViewById(R.id.tvDetailStatus);
        TextView tvDescription = dialogView.findViewById(R.id.tvDetailDescription);
        TextView tvNotes = dialogView.findViewById(R.id.tvDetailNotes);
        ListView lvPhotos = dialogView.findViewById(R.id.lvPhotos);
        Button btnClose = dialogView.findViewById(R.id.btnClose);

        tvCustomer.setText("Pelanggan: " + task.customerName);
        tvLocation.setText("Lokasi: " + task.location);
        tvType.setText("Jenis: " + task.taskType);
        tvDate.setText("Tarikh: " + task.date + " " + task.time);
        tvStatus.setText("Status: " + task.status);
        tvDescription.setText("Keterangan:\n" + task.description);
        
        if (task.notes != null && !task.notes.isEmpty()) {
            tvNotes.setText("Nota:\n" + task.notes);
            tvNotes.setVisibility(View.VISIBLE);
        } else {
            tvNotes.setVisibility(View.GONE);
        }

        // Display photos
        if (task.photos != null && !task.photos.isEmpty()) {
            PhotoAdapter photoAdapter = new PhotoAdapter(this, task.photos);
            lvPhotos.setAdapter(photoAdapter);
        }

        AlertDialog dialog = builder.create();
        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    // Photo Adapter
    private class PhotoAdapter extends ArrayAdapter<String> {
        
        public PhotoAdapter(Activity context, List<String> photos) {
            super(context, 0, photos);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            String photoBase64 = getItem(position);
            
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.item_photo, parent, false);
            }

            ImageView imageView = convertView.findViewById(R.id.imageView);

            if (photoBase64 != null) {
                byte[] decodedString = Base64.decode(photoBase64, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imageView.setImageBitmap(bitmap);
            }

            return convertView;
        }
    }
}
