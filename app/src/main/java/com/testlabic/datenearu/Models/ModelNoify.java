package com.testlabic.datenearu.Models;

public class ModelNoify {
    public String title;
    public String description;

    public ModelNoify(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public ModelNoify() {
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}