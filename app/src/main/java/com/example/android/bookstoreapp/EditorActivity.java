package com.example.android.bookstoreapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.app.AlertDialog;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.bookstoreapp.data.BooksContract.BooksEntry;


/**
 * Allows user to input and edit product information for Books Table.
 */

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_BOOKS_LOADER = 0;

    /**
     * Content URI for the existing book (null if it's a new book)
     */
    private Uri mCurrentBooksUri;

    private EditText mProductName;

    private EditText mPrice;

    private EditText mQuantity;

    private EditText mSupplierName;

    private EditText mSupplierPhoneNumber;

    /**
     * Keeps track of whether a book has been edited (true) or not(false)
     */
    private boolean mBookHasChanged = false;

    /**
     * Listens for any user touches on a View, and change the mBookHasChanged
     * boolean to true
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mBookHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new book or editing an existing one.
        Intent intent = getIntent();
        mCurrentBooksUri = intent.getData();

        if (mCurrentBooksUri == null) {
            // This is a new book, so change the app bar to say "Add a Book"
            setTitle(getString(R.string.editor_activity_title_new_book));
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing book, so change app bar to say "Edit Book"
            setTitle(getString(R.string.editor_activity_title_edit_book));
            // Initialize a loader to read the book data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_BOOKS_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mProductName = (EditText) findViewById(R.id.edit_product_name);
        mPrice = (EditText) findViewById(R.id.edit_price);
        mQuantity = (EditText) findViewById(R.id.edit_quantity);
        mSupplierName = (EditText) findViewById(R.id.edit_supplier_name);
        mSupplierPhoneNumber = (EditText) findViewById(R.id.edit_supp_phone_number);

        // Determines if any of the input fields have been touched or modified
        mProductName.setOnTouchListener(mTouchListener);
        mPrice.setOnTouchListener(mTouchListener);
        mQuantity.setOnTouchListener(mTouchListener);
        mSupplierName.setOnTouchListener(mTouchListener);
        mSupplierPhoneNumber.setOnTouchListener(mTouchListener);

    }

    /**
     * Get user input from editor and update new Book into database
     */
    private void saveBook() {
        String prodNameString = mProductName.getText().toString().trim();
        String priceString = mPrice.getText().toString().trim();
        final String quantityString = mQuantity.getText().toString().trim();
        String suppNameString = mSupplierName.getText().toString().trim();
        String phoneNumberString = mSupplierPhoneNumber.getText().toString().trim();

        // Check if this is supposed to be a new book
        // and check if all the fields in the editor are blank
        if (mCurrentBooksUri == null && TextUtils.isEmpty(prodNameString)
                && TextUtils.isEmpty(priceString) && TextUtils.isEmpty(quantityString)
                && TextUtils.isEmpty(suppNameString) && TextUtils.isEmpty(phoneNumberString)) {
            return;
        }

        //Create ContentValues object where column names are the keys,
        // and Books info from the Editor is the values.
        ContentValues values = new ContentValues();
        values.put(BooksEntry.COLUMN_PROD_NAME, prodNameString);
        values.put(BooksEntry.COLUMN_PRICE, priceString);
        // If the quantity is not provided by the user, don't try to parse the string into an
        // integer value. Use 0 by default.
        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }
        values.put(BooksEntry.COLUMN_QUANTITY, quantity);
        values.put(BooksEntry.COLUMN_SUPP_NAME, suppNameString);
        values.put(BooksEntry.COLUMN_SUPP_PHO_NUMBER, phoneNumberString);

        // Determine if this is a new book or existing book by checking if mCurrentBooksUri
        // is null or not
        if (mCurrentBooksUri == null) {

            // This is a New book, so insert a new book into the provider,
            // returning the content URI for a new book.
            Uri newUri = getContentResolver().insert(BooksEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful
            if (newUri == null) {

                Toast.makeText(this, getString(R.string.editor_insert_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, getString(R.string.editor_insert_book_successful),
                        Toast.LENGTH_SHORT).show();
            }

        } else {
            // This is a Existing book, so update the book with content URI: mCurrentBooksUri
            // and pass in new ContentValues.
            int rowsAffected = getContentResolver().update(mCurrentBooksUri, values, null,
                    null);
            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Update was successful and we can display a toast
                Toast.makeText(this, getString(R.string.editor_update_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new book, hide the "Delete" menu item.
        if (mCurrentBooksUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                //Save book to database
                saveBook();
                //Exit Activity
                finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                //If the book hasn't changed, continue with navigating up to the parent
                // activity which is the Main Activity.
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                // Setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the book hasn't changed, continue with handling back button press
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //Define a projection that contains all columns from the book table
        String[] projection = {BooksEntry._ID,
                BooksEntry.COLUMN_PROD_NAME,
                BooksEntry.COLUMN_PRICE,
                BooksEntry.COLUMN_QUANTITY,
                BooksEntry.COLUMN_SUPP_NAME,
                BooksEntry.COLUMN_SUPP_PHO_NUMBER};

        //This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,
                mCurrentBooksUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        //Finish early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Read data from the first row in the cursor
        if (cursor.moveToFirst()) {
            // Find the columns of item attributes that we're interested in
            int productNameColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_PROD_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_SUPP_NAME);
            int supplierPhoneNumberColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_SUPP_PHO_NUMBER);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(productNameColumnIndex);
            Double price = cursor.getDouble(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String quantityString = Integer.toString(quantity);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierPhoneNumber = cursor.getString(supplierPhoneNumberColumnIndex);

            //convert price to string so it can be displayed
            String priceString = Double.toString(price);

            // Update the views on the screen with the values from the database
            mProductName.setText(name);
            mPrice.setText(priceString);
            mQuantity.setText(quantityString);
            mSupplierName.setText(supplierName);
            mSupplierPhoneNumber.setText(supplierPhoneNumber);

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mProductName.setText("");
        mPrice.setText("");
        mQuantity.setText("");
        mSupplierName.setText("");
        mSupplierPhoneNumber.setText("");
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue to leave the Editor Activity
     *
     * @param discardButtonClickListener is the click listener
     *                                   for what to do when user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this book.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the book.
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Delete the book from the database
     */
    private void deleteBook() {
        // Only perform the delete if this is an existing book.
        if (mCurrentBooksUri != null) {
            // Call the ContentResolver to delete the book at the given content URI.
            int rowsDeleted = getContentResolver().delete(mCurrentBooksUri, null, null);
            // Show a toast message depending on whether or not the delete was successful
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_book_successful),
                        Toast.LENGTH_SHORT).show();
            }

        }
        // Close the activity
        finish();
    }
}
