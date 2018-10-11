package com.example.android.bookstoreapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;

import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.bookstoreapp.data.BooksContract.BooksEntry;


/**
 * Displays the list Books that were entered and stored in the app.
 */
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int BOOKS_LOADER = 0;

    BooksCursorAdapter mCursorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the books data
        ListView booksListView = (ListView) findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        booksListView.setEmptyView(emptyView);

        //Setup a Adapter to create a list item for each row of book data in the Cursor.
        //There is no book data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new BooksCursorAdapter(this, null);
        booksListView.setAdapter(mCursorAdapter);

        //Setup item click listener
        booksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);

                // Form the content URI that represents the specific book that was clicked on
                Uri currentBookUri = ContentUris.withAppendedId(BooksEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentBookUri);

                // Launch the {@link EditorActivity} to display the data for the current book.
                startActivity(intent);

            }
        });

        //Start the loader
        getLoaderManager().initLoader(BOOKS_LOADER, null, this);

    }

    /**
     * Helper method to insert hardcoded Books data into the database.
     */
    private void insertBook() {

        ContentValues values = new ContentValues();

        values.put(BooksEntry.COLUMN_PROD_NAME, "The Martian");
        values.put(BooksEntry.COLUMN_PRICE, 13.0);
        values.put(BooksEntry.COLUMN_QUANTITY, 3);
        values.put(BooksEntry.COLUMN_SUPP_NAME, "books for you");
        values.put(BooksEntry.COLUMN_SUPP_PHO_NUMBER, "773-211-4500");

        //Insert a new row for the book into the provider using the ContentResolver.
        //Use the @link BooksEntry#CONTENT_URI to indicate that we want to insert
        //into the books database table.
        Uri newUri = getContentResolver().insert(BooksEntry.CONTENT_URI, values);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu options from the menu_main.xml file.
        //This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //User clicked on a menu option in the app bar overflow menu.
        switch (item.getItemId()) {
            //Respond to a click on the "Insert dummy data" menu option.
            case R.id.action_insert_dummy_data:
                insertBook();
                return true;
            //Respond to a click on the "Delete all entries" menu option.
            case R.id.action_delete_all_books:
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //Define a projection that specifies the columns from the table we care about.
        String[] projection = {

                BooksEntry._ID,
                BooksEntry.COLUMN_PROD_NAME,
                BooksEntry.COLUMN_PRICE,
                BooksEntry.COLUMN_QUANTITY,
                BooksEntry.COLUMN_SUPP_NAME,
                BooksEntry.COLUMN_SUPP_PHO_NUMBER,};

        //This Loader will execute the ContentsProvider query method on a background thread.
        return new CursorLoader(this,
                BooksEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //Update {@link BooksCursorAdapter} with this new cursor containing update book data.
        mCursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);

    }
}
