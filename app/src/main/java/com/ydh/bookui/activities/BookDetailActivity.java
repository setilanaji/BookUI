package com.ydh.bookui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Menu;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.ydh.bookui.utilities.DbBitmapUtility;
import com.ydh.bookui.R;
import com.ydh.bookui.utilities.myDbAdapter;

public class BookDetailActivity extends AppCompatActivity {

    private ImageView bookImage;
    private TextView bookTitle, bookAuthor, bookPageReview, bookScore, bookDate;
    private RatingBar bookRatingBar;
    private EditText bookReview;

    private myDbAdapter helper;
    private int bookId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Detail Book");
        actionBar.setElevation(0);
        actionBar.show();

        initBook();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.book_edit:
                //Toast.makeText(BookDetailActivity.this, "Edit", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(BookDetailActivity.this, BookAddEditActivity.class);
                intent.putExtra("requestCode",2);
                intent.putExtra("bookId",bookId);
                startActivityForResult(intent,2);
                break;
            case R.id.book_delete:
                //Toast.makeText(BookDetailActivity.this, "Delete", Toast.LENGTH_SHORT).show();
                showWarningConfirmation(this);
                break;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.book_menu,menu);
        return true;
    }



    public void showWarningConfirmation(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Review");
        builder.setMessage(context.getString(R.string.lbl_delete_book));

        builder.setPositiveButton("YES", (dialog, which) -> {
            deleteBook(bookId);
            dialog.dismiss();
            setResult(RESULT_OK);
            finish();
        });

        builder.setNegativeButton("NO", (dialog, which) -> dialog.dismiss());

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void deleteBook(int id){
        helper.openDB();
        helper.deleteBook(id);
        helper.closeDB();
    }

    public void initBook(){

        bookId = getIntent().getExtras().getInt("id");
        String bookTitleStr = getIntent().getExtras().getString("title");
        String bookAuthorStr = "by "+getIntent().getExtras().getString("author");
        int bookPageVal = getIntent().getExtras().getInt("pages");
        //int bookReviewVal = getIntent().getExtras().getInt("reviews");
        String bookPageReviewStr = ""+ bookPageVal+ " pages";
        Float bookRatingVal = getIntent().getExtras().getFloat("ratingBar");
        String bookScoreStr = bookRatingVal+"";
        String bookDateStr = getIntent().getExtras().getString("date");

        helper = new myDbAdapter(this);

        helper.openDB();

        bookImage = findViewById(R.id.detail_book_img);
        bookReview = findViewById(R.id.detail_book_review);


        Cursor cursor = helper.getOneData(bookId);

        if (cursor != null){
            cursor.moveToFirst();
            String bookReviewStr = cursor.getString(5);
            byte[] cover = cursor.getBlob(6);
            Bitmap bitmap =  DbBitmapUtility.getImage(cover);
            Glide.with(getApplicationContext())
                    .load(bitmap)
                    .transforms(new CenterCrop(),new RoundedCorners(32))
                    .into(bookImage);
            bookReview.setText(bookReviewStr);
        }

        helper.closeDB();

        bookAuthor = findViewById(R.id.detail_book_author);
        bookTitle = findViewById(R.id.detail_book_title);
        bookRatingBar = findViewById(R.id.detail_book_ratingBar);
        bookPageReview = findViewById(R.id.detail_book_pagesreviews);
        bookScore = findViewById(R.id.detail_book_score);
        bookDate = findViewById(R.id.detail_book_date);

        bookAuthor.setText(bookAuthorStr);
        bookTitle.setText(bookTitleStr);
        bookRatingBar.setRating(bookRatingVal);
        bookPageReview.setText(bookPageReviewStr);
        bookScore.setText(bookScoreStr);
        bookDate.setText(bookDateStr);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            bookRatingBar.setRating(data.getExtras().getFloat("ratingBar"));
            bookAuthor.setText("by "+data.getExtras().getString("author"));
            bookTitle.setText(data.getExtras().getString("title"));
            bookPageReview.setText(data.getExtras().getString("reviews"));
            bookScore.setText(data.getExtras().getFloat("ratingBar")+"");
            bookDate.setText(data.getExtras().getString("date")+"");

            bookId = data.getExtras().getInt("id");

            helper.openDB();
            Cursor cursor = helper.getOneData(bookId);

            if (cursor != null){
                cursor.moveToFirst();
                //String bookReviewStr = cursor.getString(5);
                byte[] cover = cursor.getBlob(6);
                Bitmap bitmap =  DbBitmapUtility.getImage(cover);
                Glide.with(getApplicationContext())
                        .load(bitmap)
                        .transforms(new CenterCrop(),new RoundedCorners(32))
                        .into(bookImage);
                //bookReview.setText(bookReviewStr);
            }

            helper.closeDB();
            //initBook();
        }

    }



}
