package com.ydh.bookui.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.ydh.bookui.utilities.Message;
import com.ydh.bookui.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.ydh.bookui.model.Book;
import com.ydh.bookui.utilities.myDbAdapter;
import com.ydh.bookui.utilities.DbBitmapUtility;

public class BookAddEditActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final int REQUEST_IMAGE = 100;

    private RatingBar addRatingBar;
    private TextView tvDate;
    private EditText etTitle, etAuthor, etPage, etReview;
    private ImageView ivCover;
    private byte[] bytesCover;
    private Book book;

    private int requestCode;

    myDbAdapter helper;
    DbBitmapUtility imageDb;

    @BindView(R.id.add_book_img)
    ImageView addBookImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_add);

        initBook();

        ImagePickerActivity.clearCache(this);

        // RatingBar Listener
        addListenerOnratingBar();

        // Get current date
        displayDateTime();

        // DB helper
        helper = new myDbAdapter(this);
        imageDb = new DbBitmapUtility();

    }

    private void loadProfile(String url) {
        Log.d(TAG, "Image cache path: " + url);

        Glide.with(this).load(url)
                .into(addBookImage);
        addBookImage.setColorFilter(ContextCompat.getColor(this, android.R.color.transparent));
    }

    private void loadProfileDefault(Bitmap bitmap, int code) {
        if (code == 1){
            Glide.with(this).load(R.drawable.insert_photo)
                    .transforms(new CenterCrop(),new RoundedCorners(32))
                    .into(addBookImage);
            //addBookImage.setColorFilter(ContextCompat.getColor(this, R.color.light_medium_gray));
        }else {
            Glide.with(this).load(bitmap)
                    .transforms(new CenterCrop(),new RoundedCorners(32))
                    .into(addBookImage);
            addBookImage.setColorFilter(ContextCompat.getColor(this, android.R.color.transparent));
        }
    }

    @OnClick(R.id.add_book_img)
    void onProfileImageClick() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            showImagePickerOptions();
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }

                }).check();
    }

    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(this, new ImagePickerActivity.PickerOPtionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(BookAddEditActivity.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 2); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 3);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 100);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 150);

        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(BookAddEditActivity.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 2); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 3);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 100);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 150);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getParcelableExtra("path");
                try {
                    // You can update this bitmap to your server
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    bytesCover = DbBitmapUtility.getBytes(bitmap);

                    // loading profile image from local cache
                    loadProfile(uri.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(BookAddEditActivity.this);
        builder.setTitle(getString(R.string.dialog_permission_title));
        builder.setMessage(getString(R.string.dialog_permission_message));
        builder.setPositiveButton(getString(R.string.go_to_settings), (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void addListenerOnratingBar(){
        addRatingBar = findViewById(R.id.add_book_ratingBar);

        //if rating value is changed
        addRatingBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    float touchPositionX = event.getX();
                    float width = addRatingBar.getWidth();
                    float starsf = (touchPositionX / width) * 5.0f;
                    //int stars = (int)starsf + 1;
                    addRatingBar.setRating(starsf);

                    Toast.makeText(BookAddEditActivity.this, "test"+addRatingBar.getRating(), Toast.LENGTH_SHORT).show();
                    v.setPressed(false);
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setPressed(true);
                }

                if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                    v.setPressed(false);
                }
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.edit_book_save:
                //Toast.makeText(BookAddEditActivity.this, "Save", Toast.LENGTH_SHORT).show();
                if (requestCode == 1){
                    addBook();
                } else{
                    editBook(book);
                }
                onBackPressed();
                break;
            case R.id.edit_book_cancel:
                //Toast.makeText(BookAddEditActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
                emptyField();
                onBackPressed();
                break;
            case android.R.id.home:

                onBackPressed();
                return true;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.addedit_menu,menu);
        return true;
    }

    // Display Current Date Time
    private void displayDateTime(){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c);

        Date t = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        String formattedTime = sdf.format(t);

        tvDate = findViewById(R.id.add_book_date);
        //tvTime = findViewById(R.id.add_book_time);

        tvDate.setText(formattedDate+" "+formattedTime);
        //tvTime.setText("");

    }

    public void addBook(){
        String b_title = etTitle.getText().toString();
        String b_author = etAuthor.getText().toString();
        int b_page = Integer.parseInt(etPage.getText().toString());
        double b_rate = addRatingBar.getRating();
        String b_review = etReview.getText().toString();
        byte[] b_cover = bytesCover;
        int b_fav = 0;

        if (b_title.isEmpty() || b_author.isEmpty() || b_review.isEmpty()){
            Message.message(getApplicationContext(), "Please fill all blank field");
        } else {
            helper.openDB();
            long id = helper.insertData(b_title,b_author,b_page,b_rate,b_review, b_cover, b_fav);
            helper.closeDB();
            if (id<=0){
                Message.message(getApplicationContext(), "Insertion Unsuccessful");
                emptyField();
            } else {
                Message.message(getApplicationContext(), "Insertion Successful");
                emptyField();
                onBackPressed();
            }
        }
    }

    public void editBook(Book b){
        b.setTitle(etTitle.getText().toString());
        b.setAuthor(etAuthor.getText().toString());
        b.setPages(Integer.parseInt(etPage.getText().toString()));
        b.setRating(addRatingBar.getRating());
        b.setReview(etReview.getText().toString());
        b.setBytesCover(bytesCover);

        if (b.getTitle().isEmpty() || b.getAuthor().isEmpty() || b.getReview().isEmpty()){
            Message.message(getApplicationContext(), "Please fill all blank field");
        } else {
            helper.openDB();
            long id = helper.updateBook(b,b.getId());
            helper.closeDB();
            if (id<=0){
                Message.message(getApplicationContext(), "Updating Unsuccessful");
                //emptyField();
            } else {
                Message.message(getApplicationContext(), "Updating Successful");
                //emptyField();
                book = b;
                onBackPressed();
            }
        }
    }

    public void emptyField(){
        etTitle.setText("");
        etAuthor.setText("");
        addRatingBar.setRating(0);
        etPage.setText("");
        ivCover.setImageResource(R.drawable.container_bg);
    }

    @Override
    public void onBackPressed() {
        if (requestCode != 1){
            Intent intent = new Intent();
            //Intent intent = new Intent(this, BookDetailActivity.class);
            intent.putExtra("id", book.getId());
            intent.putExtra("title",book.getTitle());
            intent.putExtra("author",book.getAuthor());
            intent.putExtra("pages",book.getPages());
            //intent.putExtra("reviews",book.getReviews());
            intent.putExtra("ratingBar",book.getRating());
            intent.putExtra("description",book.getReview());
            intent.putExtra("date", book.getDate());
            intent.putExtra("requestCode",1);
            //intent.putExtra("id","1");
            intent.putExtra("description","sendback description from 2nd activity");
            setResult(RESULT_OK, intent);

        }
        finish();
    }

    public void initBook(){

        etTitle = findViewById(R.id.add_book_title);
        etAuthor = findViewById(R.id.add_book_author);
        etPage = findViewById(R.id.add_book_page);
        etReview = findViewById(R.id.add_book_review);
        addRatingBar = findViewById(R.id.add_book_ratingBar);
        ivCover = findViewById(R.id.add_book_img);

        // Image chooser
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setElevation(0);
        requestCode = getIntent().getExtras().getInt("requestCode");
        if (requestCode ==1){
            actionBar.setTitle("Add Book");
            loadProfileDefault(null,1);

        } else if (requestCode == 2){
            actionBar.setTitle("Edit Book");
            helper = new myDbAdapter(this);

            helper.openDB();
            Cursor cursor = helper.getOneData(getIntent().getExtras().getInt("bookId"));

            if (cursor != null){
                cursor.moveToFirst();

                int id = cursor.getInt(0);
                String b_title = cursor.getString(1);
                String b_author = cursor.getString(2);
                int b_page = cursor.getInt(3);
                double b_rate = cursor.getFloat(4);
                String b_review = cursor.getString(5);
                bytesCover = cursor.getBlob(6);
                String date = cursor.getString(7);
                int favorite = cursor.getInt(8);

                etTitle.setText(b_title);
                etAuthor.setText(b_author);
                etPage.setText(String.valueOf(b_page));
                addRatingBar.setRating((float) b_rate);
                etReview.setText(b_review);

                Bitmap bitmap =  DbBitmapUtility.getImage(bytesCover);

                book = new Book(id,b_title,b_author,b_review,1,b_page, (float) b_rate,bitmap,bytesCover,date,favorite);

                loadProfileDefault(bitmap,2);
            }
            helper.closeDB();
        }
        actionBar.show();
    }

}
