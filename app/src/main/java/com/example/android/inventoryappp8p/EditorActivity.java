package com.example.android.inventoryappp8p;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.android.inventoryappp8p.data.GameContract.GameEntry;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the game data loader
     */
    private static final int EXISTING_GAME_LOADER = 0;

    /**
     * Content URI for the existing game (null if it's a new game)
     */
    private Uri mCurrentGameUri;

    /**
     * EditText field to enter the game's name
     */
    private EditText mNameEditText;

    /**
     * EditText field to enter the game's price
     */
    private EditText mPricedEditText;

    /**
     * EditText field to enter the game's current quantity
     */
    private EditText mQuantityEditText;

    /**
     * EditText field to enter the games's supplier
     */
    private EditText mSupplierEditText;

    /**
     * EditText field to enter the game's supplier
     */
    private EditText mPhoneEditText;

    /**
     * Boolean flag that keeps track of whether the game has been edited (true) or not (false)
     */
    private boolean mGameHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mGameHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mGameHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Button refillButton = findViewById(R.id.restock_item_button);
        Button sellButton = findViewById(R.id.sell_item_button);
        ImageButton callButton = findViewById(R.id.call_button);

        refillButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addToQuantity();
            }
        });

        sellButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                removeFromQuantity();
            }
        });

        callButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                makeCall();
            }
        });

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new game or editing an existing one.
        Intent intent = getIntent();
        mCurrentGameUri = intent.getData();

        if (mCurrentGameUri != null) {
            getLoaderManager().initLoader(EXISTING_GAME_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = findViewById(R.id.edit_game_name);
        mPricedEditText = findViewById(R.id.edit_game_price);
        mQuantityEditText = findViewById(R.id.edit_game_quantity);
        mSupplierEditText = findViewById(R.id.edit_supplier);
        mPhoneEditText = findViewById(R.id.edit_supplier_phone);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mPricedEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierEditText.setOnTouchListener(mTouchListener);
        mPhoneEditText.setOnTouchListener(mTouchListener);
    }

    /**
     * Get user input from editor and save game into database.
     */
    private void saveGame() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPricedEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String supplierString = mSupplierEditText.getText().toString().trim();
        String phoneString = mPhoneEditText.getText().toString().trim();

        // Check if this is supposed to be a new game
        // and check if all the fields in the editor are blank
        if (mCurrentGameUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(supplierString) &&
                TextUtils.isEmpty(phoneString)) {

            // Since no fields were modified, we can return early without creating a new game.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        ContentValues values = new ContentValues();

        if (!TextUtils.isEmpty(nameString)) {
            values.put(GameEntry.COLUMN_GAME_NAME, nameString);
        } else {
            Toast.makeText(this, getString(R.string.update_name),
                    Toast.LENGTH_LONG).show();
            return;
        }

        double price = 0;
        if (!TextUtils.isEmpty(priceString)) {
            price = Double.parseDouble(priceString);
            values.put(GameEntry.COLUMN_GAME_PRICE, price);
        } else {
            Toast.makeText(this, getString(R.string.update_price),
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (!TextUtils.isEmpty(supplierString)) {
            values.put(GameEntry.COLUMN_GAME_SUPPLIER, supplierString);
        } else {
            Toast.makeText(this, getString(R.string.update_supplier),
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (!TextUtils.isEmpty(phoneString)) {
            values.put(GameEntry.COLUMN_GAME_SUPPLIER_PHONE, phoneString);
        } else {
            Toast.makeText(this, getString(R.string.update_phone),
                    Toast.LENGTH_LONG).show();
            return;
        }
        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
            values.put(GameEntry.COLUMN_GAME_QUANTITY, quantity);
        } else {
            Toast.makeText(this, getString(R.string.update_quantity),
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (mCurrentGameUri != null) {

            int rowsAffected = getContentResolver().update(mCurrentGameUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_game_failed),
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_game_successful),
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save game to database
                saveGame();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the game hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mGameHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
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
        // If the game hasn't changed, continue with handling back button press
        if (!mGameHasChanged) {
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
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all game attributes, define a projection that contains
        // all columns from the game table
        String[] projection = {
                GameEntry._ID,
                GameEntry.COLUMN_GAME_NAME,
                GameEntry.COLUMN_GAME_PRICE,
                GameEntry.COLUMN_GAME_QUANTITY,
                GameEntry.COLUMN_GAME_SUPPLIER,
                GameEntry.COLUMN_GAME_SUPPLIER_PHONE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentGameUri,         // Query the content URI for the current game
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of game attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(GameEntry.COLUMN_GAME_NAME);
            int priceColumnIndex = cursor.getColumnIndex(GameEntry.COLUMN_GAME_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(GameEntry.COLUMN_GAME_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(GameEntry.COLUMN_GAME_SUPPLIER);
            int phoneColumnIndex = cursor.getColumnIndex(GameEntry.COLUMN_GAME_SUPPLIER_PHONE);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            double price = cursor.getDouble(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);
            String phone = cursor.getString(phoneColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mPricedEditText.setText(Double.toString(price));
            mQuantityEditText.setText(Integer.toString(quantity));
            mSupplierEditText.setText(supplier);
            mPhoneEditText.setText(phone);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mPricedEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierEditText.setText("");
        mPhoneEditText.setText("");
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
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
                // and continue editing the game.
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
     * Prompt the user to confirm that they want to delete this game.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the game.
                deleteGame();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the game.
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
     * Perform the deletion of the game in the database.
     */
    private void deleteGame() {
        // Only perform the delete if this is an existing game.
        if (mCurrentGameUri != null) {
            // Call the ContentResolver to delete the game at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentGameUri
            // content URI already identifies the game that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentGameUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_game_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_game_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }

    private void addToQuantity() {
        String quantityString = mQuantityEditText.getText().toString();
        int quantityEdit = Integer.parseInt(quantityString);
        quantityEdit++;
        quantityString = String.valueOf(quantityEdit);
        mQuantityEditText.setText(quantityString);
    }

    private void removeFromQuantity() {
        String quantityString = mQuantityEditText.getText().toString();
        int quantityEdit = Integer.parseInt(quantityString);
        quantityEdit = quantityEdit - 1;
        if (quantityEdit < 0) {
            quantityEdit = 0;
            Toast.makeText(this, getString(R.string.quantity_low_limit), Toast.LENGTH_SHORT).show();
        }
        quantityString = String.valueOf(quantityEdit);
        mQuantityEditText.setText(quantityString);
    }

    private void makeCall() {
        String phoneNumberString = mPhoneEditText.getText().toString();
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumberString, null));
        startActivity(intent);
    }
}
