package com.hoangdv.framework.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {
	public static final String DATABASE_NAME = "sampledatabase.sqlite";
	public static final int DATABASE_VERSION = 1;
	public static final String TABLE_SAMPLE = "tbl_sample";
	public static final String COLUMN_SAMPLE_ID = "id";
	public static final String COLUMN_SAMPLE_VALUE = "value";
	public static final String CREATE_TABLE_SAMPLE = "CREATE TABLE "
			+ TABLE_SAMPLE + "(" + COLUMN_SAMPLE_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_SAMPLE_VALUE
			+ " TEXT)";

	public DBOpenHelper(Context ctx) {
		super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		// TODO Auto-generated method stub
		database.execSQL(CREATE_TABLE_SAMPLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int arg1, int arg2) {
		// TODO Auto-generated method stub
		database.execSQL("DROP TABLE IF EXISTS "+TABLE_SAMPLE);
		onCreate(database);
	}
	
	public Cursor execQuery(String sql){
		SQLiteDatabase database = this.getReadableDatabase();
		Cursor cursor = null;
		cursor = database.rawQuery(sql, null);
		return cursor;
	}

}
