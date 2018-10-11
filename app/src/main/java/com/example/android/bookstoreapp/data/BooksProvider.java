package com.example.android.bookstoreapp.data;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.bookstoreapp.data.BooksContract.BooksEntry;

import java.security.Provider;

/**
 * {@link ContentProvider} for Pets app.
 */
public class BooksProvider extends ContentProvider {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = BooksProvider.class.getSimpleName();

    /**
     * URI matcher code for the content URI for the books table
     */
    private static final int BOOKS = 100;

    /**
     * URI matcher code for the content URI for a single book in the books table
     */
    private static final int BOOKS_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        sUriMatcher.addURI(BooksContract.CONTENT_AUTHORITY, BooksContract.PATH_BOOKS, BOOKS);
        sUriMatcher.addURI(BooksContract.CONTENT_AUTHORITY, BooksContract.PATH_BOOKS + "/#", BOOKS_ID);


    }

    /*Database Helper object */
    private BooksDbHelper mDbHelper;

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        mDbHelper = new BooksDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // URI matcher can match the the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                cursor = database.query(BooksEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);

                break;
            case BOOKS_ID:
                selection = BooksEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(BooksEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for,
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBooks(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }

    }

    /**
     * Insert a book into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertBooks(Uri uri, ContentValues values) {
        // Check that the name is not null
        String productName = values.getAsString(BooksEntry.COLUMN_PROD_NAME);
        if (productName == null) {
            throw new IllegalArgumentException("Book requires a name");
        }
        Double price = values.getAsDouble(BooksEntry.COLUMN_PRICE);
        if (price == null || price <= 0) {
            throw new IllegalArgumentException("Book requires valid price");
        }
        Integer quantity = values.getAsInteger(BooksEntry.COLUMN_QUANTITY);
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("Please enter a valid quantity");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new book with the given values
        long id = database.insert(BooksEntry.TABLE_NAME, null, values);

        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        // Notify all listeners that the data has changed books content URI.
        // uri: content://com.example.android.bookstoreapp/books
        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateBooks(uri, contentValues, selection, selectionArgs);

            case BOOKS_ID:
                // For the BOOKS_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = BooksEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBooks(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }


    }

    private int updateBooks(Uri uri, ContentValues values, String selection, String[]
            selectionArgs) {
        // If the {@link BooksEntry#COLUMN_PROD_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(BooksEntry.COLUMN_PROD_NAME)) {
            String name = values.getAsString(BooksEntry.COLUMN_PROD_NAME);
            if (name == null || name.isEmpty() )  {
                throw new IllegalArgumentException("Product requires a name");
            }
        }

        // If the {@link BooksEntry#COLUMN_PRICE} key is present,
        // check that the value is valid.
        if (values.containsKey(BooksEntry.COLUMN_PRICE)) {
            Double price = values.getAsDouble(BooksEntry.COLUMN_PRICE);
            if (price == null || price <= 0) {
                throw new IllegalArgumentException("Product requires a valid price");
            }
        }

        // If the {@link BooksEntry#COLUMN_QUANTITY} key is present,
        // check that the quantity value is valid.
        if (values.containsKey(BooksEntry.COLUMN_QUANTITY)) {
            // Check that the quantity is greater than or equal to 0
            Integer quantity = values.getAsInteger(BooksEntry.COLUMN_QUANTITY);
            if (quantity == null || quantity < 0) {
                throw new IllegalArgumentException("Please enter a valid quantity");
            }
        }

        //If the (@link BooksEntry#COLUMN_SUPP_NAME) key is present,
        //Check that the Supplier Name value is valid
        if (values.containsKey(BooksEntry.COLUMN_SUPP_NAME)) {
            //Check that a valid name is entered
            String SupplierName = values.getAsString(BooksEntry.COLUMN_SUPP_NAME);
            if (SupplierName == null || SupplierName.isEmpty()) {
                throw new IllegalArgumentException("App requires a Supplier Name");
            }
        }

        if (values.containsKey(BooksEntry.COLUMN_SUPP_PHO_NUMBER)) {
            //Check that a valid name is entered
            String SupplierPhoneNumber = values.getAsString(BooksEntry.COLUMN_SUPP_PHO_NUMBER);
            if (SupplierPhoneNumber == null || SupplierPhoneNumber.isEmpty()) {
                throw new IllegalArgumentException("App requires a Supplier Phone Number entry");
            }
        }
        // If there are no values to update,then don't update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Returns the number of database rows affected by the update statement
        int rowsUpdated = database.update(BooksEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if(rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Returns a number of rows updated
        return rowsUpdated;

    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(BooksEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOKS_ID:
                // Delete a single row given by the ID in the URI
                selection = BooksEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(BooksEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;

    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BooksEntry.CONTENT_LIST_TYPE;
            case BOOKS_ID:
                return BooksEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }

    }

}
