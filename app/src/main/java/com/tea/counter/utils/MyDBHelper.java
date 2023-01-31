package com.tea.counter.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.tea.counter.model.ItemModel;

import java.util.ArrayList;

public class MyDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "itemManger";
    private static final String TABLE_ITEM_DATA = "contacts";
    private static final String KEY_ID = "id";
    private static final String KEY_ITEM_NAME = "itemname";
    private static final String KEY_ITEM_PRICE = "phone_number";

    public MyDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_ITEM_DATA + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_ITEM_NAME + " TEXT," + KEY_ITEM_PRICE + " TEXT" + ")";
        Log.e("CREATE_CONTACTS_TABLE", CREATE_CONTACTS_TABLE);
        db.execSQL(CREATE_CONTACTS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEM_DATA);
        // Create tables again
        onCreate(db);
    }

    public void addContact(ItemModel itemModel) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ITEM_NAME, itemModel.getItemName()); // Contact Name
        values.put(KEY_ITEM_PRICE, itemModel.getPrice()); // Contact Phone
        db.insert(TABLE_ITEM_DATA, null, values);
//        db.close();
    }

    public ArrayList<ItemModel> fetchContact() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT  * FROM " + TABLE_ITEM_DATA, null);
        ArrayList<ItemModel> arrContacts = new ArrayList<>();
        while (cursor.moveToNext()) {
            ItemModel model = new ItemModel(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
            arrContacts.add(model);
        }
        return arrContacts;
    }


}
