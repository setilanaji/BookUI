package com.ydh.bookui.model;

import android.graphics.Bitmap;

import java.util.Date;

public class Book {


    // TODO:  add published year
    private String title,author,review;
    private int id,pages;
    private float rating;
    private int drawableResource,imgUrl; // for testing
    private Bitmap cover;
    private byte[] bytesCover;
    private String date;
    private int favorite;



    public Book(int id, String title, String author, String review, int imgUrl, int pages, float rating, Bitmap cover, byte[] bytesCover, String date, int favorite) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.review = review;
        this.imgUrl = imgUrl;
        this.pages = pages;
        this.rating = rating;
        this.cover = cover;
        this.bytesCover = bytesCover;
        this.date = date;
        this.favorite = favorite;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public int getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(int imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getDrawableResource() {
        return drawableResource;
    }

    public void setDrawableResource(int drawableResource) {
        this.drawableResource = drawableResource;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Bitmap getCover() {
        return cover;
    }

    public void setCover(Bitmap cover) {
        this.cover = cover;
    }

    public byte[] getBytesCover() {
        return bytesCover;
    }

    public void setBytesCover(byte[] bytesCover) {
        this.bytesCover = bytesCover;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getFavorite() {
        return favorite;
    }

    public void setFavorite(int favorite) {
        this.favorite = favorite;
    }
}
