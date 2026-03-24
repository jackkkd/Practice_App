package com.example.practiceapp;

public class BookModel {
    private int id;
    private String title;
    private String author;
    private int coverImageResourceId;
    private String description;
    private String datePublished;
    private String genre;
    private String purpose;
    private int viewCount; // NEW

    // Updated Constructor
    public BookModel(int id, String title, String author, int coverImageResourceId, String description, String datePublished, String genre, String purpose, int viewCount) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.coverImageResourceId = coverImageResourceId;
        this.description = description;
        this.datePublished = datePublished;
        this.genre = genre;
        this.purpose = purpose;
        this.viewCount = viewCount;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getCoverImageResourceId() { return coverImageResourceId; }
    public String getDescription() { return description; }
    public String getDatePublished() { return datePublished; }
    public String getGenre() { return genre; }
    public String getPurpose() { return purpose; }

    // NEW getters and setters for views
    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }
}