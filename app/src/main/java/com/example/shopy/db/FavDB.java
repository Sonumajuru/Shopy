package com.example.shopy.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class FavDB extends SQLiteOpenHelper {

    private static int DB_VERSION = 1;
    private static String DATABASE_NAME = "NJangiDB";
    private static final String TABLE_NAME = "favoriteTable";
    public static String KEY_ID = "id";
    public static String ITEM_TITLE = "itemTitle";
    public static String ITEM_PRICE =  "itemPrice";
    public static String ITEM_RATING =  "itemRating";
    public static String ITEM_CURRENCY =  "itemCurrency";
    public static String ITEM_DESCRIPTION =  "itemDescription";
    public static String ITEM_CATEGORY =  "itemCategory";
    public static String ITEM_IMAGE = "itemImage";
    public static String ITEM_UUID = "itemUuid";
    public static String FAVORITE_STATUS = "fStatus";
    // dont forget write this spaces
    private static String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + KEY_ID + " TEXT," + ITEM_TITLE + " TEXT," + ITEM_PRICE
            + " TEXT," + ITEM_CURRENCY + " TEXT," + ITEM_RATING
            + " TEXT," + ITEM_UUID + " TEXT," + ITEM_IMAGE + " TEXT," + ITEM_CATEGORY
            + " TEXT," + ITEM_DESCRIPTION + " TEXT," + FAVORITE_STATUS+" TEXT)";

    public FavDB(Context context) { super(context,DATABASE_NAME,null,DB_VERSION);}

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    // create empty table
    public void insertEmpty() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        // enter your value
        for (int x = 1; x < 11; x++) {
            cv.put(KEY_ID, x);
            cv.put(FAVORITE_STATUS, "0");

            db.insert(TABLE_NAME,null, cv);
        }
    }

    // insert data into database
    public void insertIntoTheDatabase(String item_title, String desc, String item_image, String id,
                                      String fav_status, double item_price,
                                      double item_rating, String currency, String uuid, String category) {
        SQLiteDatabase db;
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ITEM_TITLE, item_title);
        cv.put(ITEM_IMAGE, item_image);
        cv.put(ITEM_PRICE, item_price);
        cv.put(ITEM_RATING, item_rating);
        cv.put(ITEM_CURRENCY, currency);
        cv.put(ITEM_DESCRIPTION, desc);
        cv.put(ITEM_CATEGORY, category);
        cv.put(ITEM_UUID, uuid);
        cv.put(KEY_ID, id);
        cv.put(FAVORITE_STATUS, fav_status);
        db.insert(TABLE_NAME,null, cv);
        Log.d("FavDB Status", item_title + ", favstatus - "+fav_status+" - . " + cv);
    }

    // read all data
    public Cursor read_all_data(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select * from " + TABLE_NAME + " where " + KEY_ID+"="+id+"";
        return db.rawQuery(sql,null,null);
    }

    // remove line from database
    public void remove_fav(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "UPDATE " + TABLE_NAME + " SET  "+ FAVORITE_STATUS+" ='0' WHERE "+KEY_ID+"="+id+"";
        db.execSQL(sql);
        Log.d("remove", id);

    }

    // select all favorite list

    public Cursor select_all_favorite_list() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM "+TABLE_NAME+" WHERE "+FAVORITE_STATUS+" ='1'";
        return db.rawQuery(sql,null,null);
    }
}