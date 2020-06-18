package com.Project.project.Utilities;

/**
 * Containing information about a question in questionnaire.
 */

public class Question {
    //Question fields.
    private int id;
    private String name;
    private int rating;
    private Integer image;

    // Constructor.
    public Question(int id, String name, int rating, Integer image) {
        this.id = id;
        this.name = name;
        this.rating = rating;
        this.image = image;
    }

    // Getters and Setters.
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getImage() {
        return image;
    }

    public void setImage(int id) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
