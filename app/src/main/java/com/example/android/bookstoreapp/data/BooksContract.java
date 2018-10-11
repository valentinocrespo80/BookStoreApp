package com.example.android.bookstoreapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and columns names for the Books database.
 */

public final class BooksContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private BooksContract() {
    }


    /**
     * Name for the entire Content Provider, which is the package name for the app
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.bookstoreapp";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    /**
     * Possible path for (appended to the base content URI for possible URI's)
     */

    public static final String PATH_BOOKS = "books";


    /* Inner class that defines the constant values for the Books database table */
    public static final class BooksEntry implements BaseColumns {

        /** The content URI to access the books data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of items.
         */
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single item.
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_BOOKS;



        /**
         * Name of database table for Books app
         */

        public static final String TABLE_NAME = "books";

        public static final String _ID = BaseColumns._ID;

        public static final String COLUMN_PROD_NAME = "product_name";

        public static final String COLUMN_PRICE = "price";

        public static final String COLUMN_QUANTITY = "quantity";

        public static final String COLUMN_SUPP_NAME = "supplier_name";

        public static final String COLUMN_SUPP_PHO_NUMBER = "supplier_phone_number";


    }
}
