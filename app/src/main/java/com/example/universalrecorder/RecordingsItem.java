package com.example.universalrecorder;

public class RecordingsItem {
    String name;
    String date;
    String size;
    String duration;

    public RecordingsItem(String name, String date, String size, String length) {
        this.name = name;
        this.date = date;
        this.size = size;
        this.duration = length;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getLength() {
        return duration;
    }

    public void setLength(String duration) {
        this.duration = duration;
    }
}
