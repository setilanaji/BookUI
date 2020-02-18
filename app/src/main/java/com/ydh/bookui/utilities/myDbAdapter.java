package com.ydh.bookui.utilities;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ydh.bookui.model.Book;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class myDbAdapter {
    MyDbHelper myDbHelper;
    SQLiteDatabase db;

    public myDbAdapter(Context context){
        myDbHelper = new MyDbHelper(context);
    }

    public myDbAdapter openDB(){
        try {
            db = myDbHelper.getWritableDatabase();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return this;
    }

    public void closeDB(){
        try{
            myDbHelper.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
    private String getDateTime() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c);

        Date t = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        String formattedTime = sdf.format(t);

        return formattedDate+" "+formattedTime;
    }

    public long insertData(String title, String author, int page, double rate, String review, byte[] image, int fav){
        //SQLiteDatabase sqLiteDatabase = myDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.TITLE, title);
        contentValues.put(myDbHelper.AUTHOR, author);
        contentValues.put(myDbHelper.PAGES, page);
        contentValues.put(myDbHelper.RATE, rate);
        contentValues.put(myDbHelper.REVIEW, review);
        contentValues.put(myDbHelper.IMAGE, image);
        contentValues.put(myDbHelper.DATE, getDateTime());
        contentValues.put(myDbHelper.FAVORITE, fav);
        long id = db.insert(MyDbHelper.TABLE_NAME, null, contentValues);
        return id;

    }

    public Cursor getData(){
        String[] columns = {myDbHelper.UID, myDbHelper.TITLE, myDbHelper.AUTHOR, myDbHelper.PAGES, myDbHelper.RATE, myDbHelper.REVIEW, myDbHelper.IMAGE,myDbHelper.DATE, myDbHelper.FAVORITE};
        Cursor cursor = db.query(myDbHelper.TABLE_NAME, columns,null,null,null,null,null);
        return cursor;
    }

    public Cursor getOneData(int id){
        String query = "SELECT * FROM "+myDbHelper.TABLE_NAME+" WHERE "+myDbHelper.UID+" = "+id;
        Cursor cursor = db.rawQuery(query,null);
        return cursor;
    }

    public long insertFav(int id, int newFav){
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.FAVORITE, newFav);
        String[] whereAgs = {Integer.toString(id)};

        int idret = db.update(myDbHelper.TABLE_NAME, contentValues, myDbHelper.UID+" = ? ",whereAgs);
        return idret;
    }

    public int deleteBook(int id){
        String[] whereArgs = {String.valueOf(id)};
        int count = db.delete(myDbHelper.TABLE_NAME, myDbHelper.UID+" = ?",whereArgs);
        return count;
    }

    public int updateBook(Book book, int id){
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.TITLE, book.getTitle());
        contentValues.put(myDbHelper.AUTHOR, book.getAuthor());
        contentValues.put(myDbHelper.PAGES, book.getPages());
        contentValues.put(myDbHelper.RATE, book.getRating());
        contentValues.put(myDbHelper.REVIEW, book.getReview());
        contentValues.put(myDbHelper.IMAGE, book.getBytesCover());
        contentValues.put(myDbHelper.DATE, getDateTime());
        contentValues.put(myDbHelper.FAVORITE, book.getFavorite());
        String[] whereArgs = {String.valueOf(id)};

        int count = db.update(myDbHelper.TABLE_NAME, contentValues, myDbHelper.UID+" = ?",whereArgs);
        return count;
    }




    static class MyDbHelper extends SQLiteOpenHelper{

        private static final String DATABASE_NAME ="bookDB.db";
        private static final String TABLE_NAME ="book";
        private static final int DATABASE_VERSION = 1;
        private static final String UID = "_id";
        private static final String TITLE = "BookTitle";
        private static final String AUTHOR = "BookAuthor";
        private static final String PAGES = "BookPages";
        private static final String RATE = "BookRate";
        private static final String REVIEW = "BookReview";
        private static final String IMAGE = "Cover";
        private static final String DATE = "Date";
        private static final String FAVORITE = "Favorite";
        private static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+
                " ("+UID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+TITLE+" VARCHAR(255) ,"+AUTHOR+" VARCHAR(225), "+PAGES+" INTEGER, "+RATE+" DOUBLE, "+REVIEW+" VARCHAR(225), "+IMAGE+" BLOB, "+DATE+" VARCHAR(225), "+FAVORITE+" BOOLEAN);";
        private static final String DROP_TABLE = "DROP TABLE IF EXISTS "+TABLE_NAME;
        private Context context;

        public MyDbHelper (Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context=context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(CREATE_TABLE);
            } catch (Exception e) {
                Message.message(context, ""+e);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                Message.message(context, "OnUpgrade");
                db.execSQL(DROP_TABLE);
                onCreate(db);
            }catch (Exception e) {
                Message.message(context, ""+e);
            }

        }
    }
}
