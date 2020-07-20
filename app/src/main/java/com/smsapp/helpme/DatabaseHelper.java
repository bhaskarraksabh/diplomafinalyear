package com.smsapp.helpme;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * Created by gamelooper on 3/30/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TABLE_NAME = "details";
    public static final String COL0 = "_id";
    public static final String COL1 = "_phoneNumber";
    public static final String COL2 = "_password";
    public static final String COL3 = "_description";
    public static final String COL4 = "_defaultValue";
    private static String DB_NAME = "helpme_db";
    private static int DB_VERSION = 2;


    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        Log.d("Database operations", "Database created..");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        String sql = "CREATE TABLE " +
                TABLE_NAME + " ( " +
                COL0 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL1 + " TEXT, " +
                COL2 + " TEXT, " +
                COL3 + " TEXT, " +
                COL4 + " TEXT)";
        sqLiteDatabase.execSQL(sql);
        Log.d("Database operations", "Table created..");


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);

    }


    /**
     * Insert a new contact into the database
     *
     * @param details
     * @return
     */
    public boolean addDetails(Details details) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL1, details.getPhonenumber());
        contentValues.put(COL2, details.getPassword());
        contentValues.put(COL3, details.getDescription());
        Cursor cursor = getDefaultRecord();
        if (cursor != null && (cursor.getCount() == 0))
            contentValues.put(COL4,
                    "1");
        else
            contentValues.put(COL4,
                    "0");

        long result = db.insert(TABLE_NAME, null, contentValues);

        Log.d("Database operations", "Details inserted.." + result);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getAllDetails() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    public Cursor getRecordById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " +
                COL0 + "=?", new String[]{Integer.toString(id)});
    }


    public Cursor getDefaultRecord() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " +
                COL4 + "=?", new String[]{"1"});
    }

    public int deleteRecord(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.delete(TABLE_NAME,
                COL0 + " = ? ",
                new String[] { Integer.toString(id) });
    }


    public boolean updateDetailsToDefault(int id,String isDefault,String password)
    {

        updateDetails(id,isDefault,password);

        return true;

    }

    public boolean updateDetails(int id, String flag,String password)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        if(null!=password && !password.isEmpty())
        contentValues.put(COL2, password);
        contentValues.put(COL4, flag);
        db.update(TABLE_NAME, contentValues, COL0 + " = ? ", new String[] { Integer.toString(id) } );
        return true;
    }
}
