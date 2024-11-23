package com.example.todolist;

import java.io.Serializable;
import java.util.Date;

public class Task implements Serializable {
    public int id;
    public String name;
    public Date deadline;
    public int duration;
    public String descriptions;

    public Task() {} // Default constructor

    public Task(String name, Date deadline, int duration, String descriptions) {
        this.name = name;
        this.deadline = deadline;
        this.duration = duration;
        this.descriptions = descriptions;
    }

    public Task(int id, String name, Date deadline, int duration, String descriptions) {
        this.id = id;
        this.name = name;
        this.deadline = deadline;
        this.duration = duration;
        this.descriptions = descriptions;
    }


    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Date getDeadline() { return deadline; }
    public void setDeadline(Date deadline) { this.deadline = deadline; }
    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
    public String getDescriptions() { return descriptions; }
    public void setDescriptions(String descriptions) { this.descriptions = descriptions; }
}