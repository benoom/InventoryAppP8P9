package com.example.android.inventoryappp8p;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventoryappp8p.data.GameContract.GameEntry;

/**
 * {@link GameCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of game data as its data source. This adapter knows
 * how to create list items for each row of game data in the {@link Cursor}.
 */
public class GameCursorAdapter extends CursorAdapter {
    private Cursor mCursor;

    /**
     * Constructs a new {@link GameCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public GameCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);

    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the game data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current game can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        // Initialize gameSoldBtn and cursor variables
        Button gameSoldBtn;
        this.mCursor = cursor;

        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = view.findViewById(R.id.name);
        TextView priceTextView = view.findViewById(R.id.price);
        TextView quantityTextView = view.findViewById(R.id.quantity);

        // Find the columns of game attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(GameEntry.COLUMN_GAME_NAME);
        int priceColumnIndex = cursor.getColumnIndex(GameEntry.COLUMN_GAME_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(GameEntry.COLUMN_GAME_QUANTITY);

        // Read the game attributes from the Cursor for the current game
        String gameName = cursor.getString(nameColumnIndex);
        String gamePrice = cursor.getString(priceColumnIndex);
        String gameQuantity = cursor.getString(quantityColumnIndex);

        // Update the TextViews with the attributes for the current game
        nameTextView.setText(gameName);
        priceTextView.setText(gamePrice);
        quantityTextView.setText(gameQuantity);

        gameSoldBtn = view.findViewById(R.id.game_sold_button);
        gameSoldBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int columnIdIndex = mCursor.getColumnIndex(GameEntry._ID);
                int quantityIndex = mCursor.getColumnIndex(GameEntry.COLUMN_GAME_QUANTITY);
                String column = mCursor.getString(columnIdIndex);
                String currentQuantity = mCursor.getString(quantityIndex);
                CatalogActivity catalogActivity = (CatalogActivity) context;
                catalogActivity.decreaseCount(Integer.valueOf(column), Integer.valueOf(currentQuantity));
            }
        });
    }
}

