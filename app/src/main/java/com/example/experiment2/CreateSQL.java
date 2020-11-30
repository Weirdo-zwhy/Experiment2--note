package com.example.experiment2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class CreateSQL extends SQLiteOpenHelper {
    public static final String CREATE_DIARY = "create table diary ("
            +"id integer primary key autoincrement,"
            +"author text,"
            +"title text,"
            +"body text,"
            +"time integer)";
    private Context mContext;
    public CreateSQL(Context context, String name, SQLiteDatabase.CursorFactory factory , int version){
        super(context,name,factory,version);
        mContext = context;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DIARY);
        Toast.makeText(mContext,"Create succeeded",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion>oldVersion) {
            db.execSQL("ALTER TABLE diary ADD photoURL text;");
        }
    }
}
