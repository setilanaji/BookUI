package com.ydh.bookui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.ydh.bookui.listeners.BookItemClickListener;
import com.ydh.bookui.listeners.FavoriteClickListener;
import com.ydh.bookui.utilities.Message;
import com.ydh.bookui.R;
import com.ydh.bookui.model.Book;
import com.ydh.bookui.recyclerview.BookAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.ydh.bookui.utilities.myDbAdapter;
import com.ydh.bookui.utilities.DbBitmapUtility;

public class MainActivity extends AppCompatActivity implements BookItemClickListener, NavigationView.OnNavigationItemSelectedListener, FavoriteClickListener {


    private RecyclerView rvBooks;
    private BookAdapter bookAdapter;
    private List<Book> mdata;
    // search
    private EditText searchInput;

    // add
    private FloatingActionButton fabAdd;

    private TextView tvEmpty;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private myDbAdapter helper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        helper = new myDbAdapter(this);

        initViews();
        initmdataBooks();
        setupBookAdapter();

    }

    private void setupBookAdapter() {
        bookAdapter = new BookAdapter(this,mdata,this::onBookClick,this::onFavoriteClick);
        rvBooks.setAdapter(bookAdapter);
    }

    private void initmdataBooks() {
        mdata = new ArrayList<>();

        helper.openDB();

        Cursor cursor = helper.getData();

        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            String title = cursor.getString(1);
            String author = cursor.getString(2);
            int page = cursor.getInt(3);
            float rate = cursor.getFloat(4);
            String review = cursor.getString(5);
            byte[] cover = cursor.getBlob(6);
            String date = cursor.getString(7);
            int favorite = cursor.getInt(8);
            Bitmap bitmap =  DbBitmapUtility.getImage(cover);


            Book book = new Book(id, title, author, review,13, page, rate, bitmap, cover, date, favorite);

            mdata.add(book);
        }

        // sorting from recent date
        Collections.sort(mdata, new Comparator<Book>() {
            @Override
            public int compare(Book o1, Book o2) {
                if (o1.getDate() == null || o2.getDate() == null)
                    return 0;
                return o2.getDate().compareTo(o1.getDate());
            }
        });

        if (!(mdata.size()<1)){
            tvEmpty.setVisibility(View.INVISIBLE);
            rvBooks.setAdapter(bookAdapter);
        }

        helper.closeDB();
    }

    private void initViews(){

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Book Review");
        actionBar.setElevation(0);
        actionBar.show();
        //getSupportActionBar().hide();

        rvBooks = findViewById(R.id.rv_book);
        rvBooks.setLayoutManager(new LinearLayoutManager(this));
        rvBooks.setHasFixedSize(true);

        searchInput = findViewById(R.id.search_input);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                bookAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tvEmpty = findViewById(R.id.txt_emp);

        fabAdd = findViewById(R.id.fab_add);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BookAddEditActivity.class);

                intent.putExtra("id", "1");
                intent.putExtra("description", "send description to 2nd activity");
                intent.putExtra("requestCode",1);
                startActivityForResult(intent,1);
            }
        });


    }


    @Override
    public void onBookClick(Book book, ImageView bookImageView, TextView bookTitle, TextView bookAuthor, TextView bookPageReview, TextView bookRate, RatingBar bookRatingBar, ImageView bookBg, TextView bookDate) {

            Intent intent = new Intent(this, BookDetailActivity.class);
            intent.putExtra("id", book.getId());
            intent.putExtra("title",book.getTitle());
            intent.putExtra("author",book.getAuthor());
            intent.putExtra("pages",book.getPages());
            intent.putExtra("ratingBar",book.getRating());
            intent.putExtra("description",book.getReview());
            intent.putExtra("date",book.getDate());
            intent.putExtra("requestCode",1);
            //intent.putExtra("cover",book.getCover());

            Pair<View, String> p1 = Pair.create((View)bookImageView, "bookImage");
            Pair<View, String> p2 = Pair.create((View) bookTitle, "bookTitle");
            Pair<View, String> p3 = Pair.create((View)bookAuthor, "bookAuthor");
            Pair<View, String> p4 = Pair.create((View)bookPageReview, "bookPageReview");
            Pair<View, String> p5 = Pair.create((View)bookRate, "bookScore");
            Pair<View, String> p6 = Pair.create((View)bookRatingBar, "bookRatingBar");
            Pair<View, String> p7 = Pair.create((View)bookDate, "bookDate");
            // Pair<View, String> p7 = Pair.create((View)bookBg, "bookBg");


            //Animation
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, p1, p2,p3,p4,p5,p6,p7);

            startActivityForResult(intent,1,options.toBundle());

            //Toast.makeText(this,"item clicked"+ book.getTitle(),Toast.LENGTH_LONG).show();

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        return false;

    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.drawer_view,menu);
//        return true;
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            initmdataBooks();
            setupBookAdapter();
        }

    }

    @Override
    public void onFavoriteClick(int bookId, int value) {
        helper.openDB();
        helper.insertFav(bookId,value);
        helper.closeDB();
        //Message.message(this, "stat : "+value);

    }



}

