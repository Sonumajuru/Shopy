package com.njangi.shop.adapter;

import android.content.Context;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;
import com.njangi.shop.db.SuggestionsDatabase;

public class SuggestionAdapter extends SimpleCursorAdapter
{
    public SuggestionAdapter(Context context, int layout, Cursor c,
                             String[] from, int[] to) {
        super(context, layout, c, from, to);
    }

    public SuggestionAdapter(Context context, int layout, Cursor c,
                             String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }
    @Override
    public CharSequence convertToString(Cursor cursor) {
        int indexColumnSuggestion = cursor.getColumnIndex(SuggestionsDatabase.FIELD_SUGGESTION);
        return cursor.getString(indexColumnSuggestion);
    }
}