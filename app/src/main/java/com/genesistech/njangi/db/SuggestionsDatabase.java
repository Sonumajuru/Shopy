package com.genesistech.njangi.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SuggestionsDatabase {

    public static final String DB_SUGGESTION = "SUGGESTION_DB";
    public final static String TABLE_SUGGESTION = "SUGGESTION_TB";
    public final static String FIELD_ID = "_id";
    public final static String FIELD_SUGGESTION = "suggestion";

    private final SQLiteDatabase db;

    public SuggestionsDatabase(Context context) {
        Helper helper = new Helper(context, DB_SUGGESTION, null, 1);
        db = helper.getWritableDatabase();
    }

    public long insertSuggestion(String text)
    {
        ContentValues values = new ContentValues();
        values.put(FIELD_SUGGESTION, text);
        return db.insert(TABLE_SUGGESTION, null, values);
    }

    public Cursor getSuggestions(String text)
    {
        return db.query(TABLE_SUGGESTION, new String[] {FIELD_ID, FIELD_SUGGESTION},
                FIELD_SUGGESTION +" LIKE '"+ text +"%'", null, null, null, FIELD_ID);
    }

    private static class Helper extends SQLiteOpenHelper
    {

        public Helper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                      int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE "+TABLE_SUGGESTION+" ("+
                    FIELD_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+FIELD_SUGGESTION+" TEXT UNIQUE ON CONFLICT REPLACE);");
            Log.d("SUGGESTION", "DB CREATED");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}