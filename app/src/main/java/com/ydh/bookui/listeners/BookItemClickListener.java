package com.ydh.bookui.listeners;

import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ydh.bookui.model.Book;

public interface BookItemClickListener {
    void onBookClick(Book book, ImageView bookImageView, TextView bookTitle, TextView bookAuthor, TextView bookPageReview, TextView bookRate, RatingBar bookRatingBar, ImageView bookBg, TextView bookDate);
}
