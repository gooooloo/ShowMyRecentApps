package com.qidu.lin.showRecentApps;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper
{
	private static final String DATABASE_NAME = "search_result_cache.db";
	private static final int DATABASE_VERSION = 1;
	private static final String TABLE_NAME = "search_result";

	private static final String COLUMN_1 = "label";
	private static final String COLUMN_2 = "keyword";
	private static final String COLUMN_3 = "matched";

	public DatabaseHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		String string = "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_1 + " TEXT PRIMARY KEY," + COLUMN_2 + " TEXT," + COLUMN_3 + " TEXT"
				+ ");";
		Log.e("@@@", string);
		db.execSQL(string);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.execSQL("DROP TABLE IF EXISTS notes");
		onCreate(db);
	}

	public void insert(String name, String keyword, String number)
	{
		try
		{
			SQLiteDatabase db = getWritableDatabase();

			ContentValues value = new ContentValues();
			value.put(COLUMN_1, name);
			value.put(COLUMN_2, keyword);
			value.put(COLUMN_3, number);
			db.replace(TABLE_NAME, null, value);
			db.close();
		}
		catch (Exception e)
		{
		}
	}

	public String select(String name, String keyword)
	{
		String re = null;
		SQLiteDatabase db = getReadableDatabase();
		String select = COLUMN_1 + "='" + name + "' AND " + COLUMN_2 + "='" + keyword + "'";
		Log.e("@@@", select);
		Cursor c = db.query(TABLE_NAME, new String[] { COLUMN_3 }, select, null, null, null, null);
		if (c.getCount() == 0)
		{

		}
		else
		{
			c.moveToFirst();
			re = c.getString(0);
		}

		c.close();

		db.close();

		return re;
	}
}
