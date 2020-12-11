package com.sovereign.budgetmanager.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TransactionDatabaseHelper extends SQLiteOpenHelper {
    public static final String TRANSACTION_TABLE = "TRANSACTION_TABLE";
    public static final String COLUMN_TRANSACTION_TITLE = "TRANSACTION_TITLE";
    public static final String COLUMN_TRANSACTION_AMOUNT = "TRANSACTION_AMOUNT";
    public static final String COLUMN_IS_CREDIT = "IS_CREDIT";
    public static final String COLUMN_TRANSACTION_CATS = "TRANSACTION_CATS";
    public static final String COLUMN_TRANSACTION_MODE = "TRANSACTION_MODE";
    public static final String COLUMN_TRANSACTION_DATE = "TRANSACTION_DATE";
    public static final String COLUMN_TRANSACTION_TIME = "TRANSACTION_TIME";
    private Context mContext;

    public TransactionDatabaseHelper(@Nullable Context context) {
        super(context, "TransactionDatabase.db", null, 1);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TRANSACTION_TABLE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_TRANSACTION_TITLE + " TEXT, "
                + COLUMN_TRANSACTION_AMOUNT + " INT, " + COLUMN_IS_CREDIT + " BOOL, " + COLUMN_TRANSACTION_CATS + " INT, " + COLUMN_TRANSACTION_MODE + " INT,"
                + COLUMN_TRANSACTION_DATE + " TEXT, " + COLUMN_TRANSACTION_TIME + " TEXT)";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addTransaction(TransactionModel transactionModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_TRANSACTION_TITLE, transactionModel.getTitle());
        contentValues.put(COLUMN_TRANSACTION_AMOUNT, transactionModel.getAmount());
        contentValues.put(COLUMN_IS_CREDIT, transactionModel.isCredit());
        contentValues.put(COLUMN_TRANSACTION_CATS, transactionModel.getCat());
        contentValues.put(COLUMN_TRANSACTION_MODE, transactionModel.getTransactionMode());
        contentValues.put(COLUMN_TRANSACTION_TIME, transactionModel.getTime());
        contentValues.put(COLUMN_TRANSACTION_DATE, transactionModel.getDate());

        long insert = db.insert(TRANSACTION_TABLE, null, contentValues);
        if (insert == -1){
            return false;
        } else {
            return true;
        }
    }

    public List<TransactionModel> getAll(){
        List<TransactionModel> transactionList = new ArrayList<>();

        String query = "SELECT * FROM " + TRANSACTION_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        if (cursor!=null && cursor.getCount() > 0){
            if (cursor.moveToFirst()){
                //loop through results and create new objects
                do {
                    String title = cursor.getString(1);
                    int amount = cursor.getInt(2);
                    boolean isCredit = cursor.getInt(3) == 1 ? true : false;
                    int cat = cursor.getInt(4);
                    int transactionMode = cursor.getInt(5);
                    String date = cursor.getString(6);
                    String time = cursor.getString(7);

                    TransactionModel transactionModel = new TransactionModel(title, amount, isCredit, cat, transactionMode, date, time);
                    transactionList.add(transactionModel);
                } while (cursor.moveToNext());
            } else {
                //Failed to add.
            }
        }

        cursor.close();
        db.close();
        return transactionList;
    }
}
