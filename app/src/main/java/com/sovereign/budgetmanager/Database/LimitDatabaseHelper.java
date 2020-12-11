package com.sovereign.budgetmanager.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LimitDatabaseHelper extends SQLiteOpenHelper {

    private static final String LIMITS_TABLE = "LIMITS_TABLE";
    private static final String COLUMN_LIMIT = "COL_LIMIT";
    private static final String COLUMN_LIMIT_CAT = "LIMIT_CAT";
    private static final String COLUMN_LIMIT_TYPE = "LIMIT_TYPE";
    private Context mContext;


    public LimitDatabaseHelper(@Nullable Context context) {
        super(context, "LimitsDatabase.db", null, 1);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + LIMITS_TABLE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_LIMIT + " INT, " + COLUMN_LIMIT_CAT + " INT, " + COLUMN_LIMIT_TYPE + " INT)";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Boolean addLimit(LimitModel limitModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_LIMIT, limitModel.getLimit());
        contentValues.put(COLUMN_LIMIT_CAT, limitModel.getCat());
        contentValues.put(COLUMN_LIMIT_TYPE, limitModel.getType());

        long insert = db.insert(LIMITS_TABLE, null, contentValues);
        if (insert == -1){
            return false;
        } else {
            return true;
        }

    }

    public List<LimitModel> getAll(){
        String query = "SELECT * FROM " + LIMITS_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();

        List<LimitModel> limitModelList = new ArrayList<>();

        Cursor cursor = db.rawQuery(query, null);

        if (cursor!=null && cursor.getCount() > 0){
            if (cursor.moveToFirst()){
                //loop through results and create new objects
                do {

                    int limit = cursor.getInt(1);
                    int cat = cursor.getInt(2);
                    int type = cursor.getInt(3);


                    LimitModel limitModel = new LimitModel(limit, cat, type);
                    limitModelList.add(limitModel);
                } while (cursor.moveToNext());
            } else {
                //Failed to add.
            }
        }

        cursor.close();
        db.close();
        return limitModelList;
    }
}
