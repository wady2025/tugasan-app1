package com.teknik.rekodtugasan;

import java.util.ArrayList;
import java.util.List;

public class Task {
    public long id;
    public String customerName;
    public String location;
    public String taskType;
    public String description;
    public String notes;
    public String status;
    public String date;
    public String time;
    public List<String> photos;

    public Task() {
        photos = new ArrayList<>();
    }

    public Task(long id, String customerName, String location, String taskType, 
                String description, String notes, String status, String date, 
                String time, List<String> photos) {
        this.id = id;
        this.customerName = customerName;
        this.location = location;
        this.taskType = taskType;
        this.description = description;
        this.notes = notes;
        this.status = status;
        this.date = date;
        this.time = time;
        this.photos = photos != null ? photos : new ArrayList<>();
    }
}
