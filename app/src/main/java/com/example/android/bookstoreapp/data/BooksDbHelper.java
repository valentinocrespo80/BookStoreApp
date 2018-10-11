package com.example.android.bookstoreapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.bookstoreapp.data.BooksContract.BooksEntry;


public class BooksDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = BooksDbHelper.class.getSimpleName();

    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "books.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a ne instance of BooksDbHelper.
     *
     * @param context of the app
     */
    public BooksDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    /**
     * This called when the database is called for the first time.
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_BOOKS_TABLE = "CREATE TABLE " + BooksEntry.TABLE_NAME + "("
                + BooksEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BooksEntry.COLUMN_PROD_NAME + " TEXT NOT NULL,"
                + BooksEntry.COLUMN_PRICE + " REAL NOT NULL DEFAULT 0.00,"
                + BooksEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0,"
                + BooksEntry.COLUMN_SUPP_NAME + " TEXT NOT NULL,"
                + BooksEntry.COLUMN_SUPP_PHO_NUMBER + " TEXT NOT NULL);";


        db.execSQL(SQL_CREATE_BOOKS_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
