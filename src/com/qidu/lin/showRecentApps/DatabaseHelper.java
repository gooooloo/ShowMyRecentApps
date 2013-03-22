package com.qidu.lin.showRecentApps;

import java.security.InvalidParameterException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper
{
	private static final int RESULT_MATCHED = 1;
	private static final int RESULT_UNMATCHED = 0;
	private static final String DATABASE_NAME = "search_result_cache.db";
	private static final int DATABASE_VERSION = 1;
	private static final String TABLE_NAME = "search_result";

	private static final String COLUMN_1 = "label";
	private static final String COLUMN_2 = "keyword";
	private static final String COLUMN_3 = "matched";
	
	static logcator lsGetWritableDatabase = new logcator("getWrit");
	static logcator lcClose = new logcator("close");
	static logcator lcReplace = new logcator("update");
	static logcator lcgetReadableDatabase = new logcator("getredLc");
	static logcator lcQuery = new logcator("query");
	
	static class logcator
	{
		long total = 0;
		long begin = 0;
		private final String name;
		public logcator(String name)
		{
			this.name = name;
		}
		public void begin()
		{
			begin = System.currentTimeMillis();
		}
		public void end()
		{
			total += System.currentTimeMillis() - begin;
			Log.e("@@@",  ""+name+":"+total);
		}
	}

	public DatabaseHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		String string = "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_1 + " TEXT," + COLUMN_2 + " TEXT," + COLUMN_3 + " TINYINT"
				+ ");";
		db.execSQL(string);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.execSQL("DROP TABLE IF EXISTS notes");
		onCreate(db);
	}


	public void insert(String name, String keyword, Boolean result)
	{
		try
		{
			lsGetWritableDatabase.begin();
			SQLiteDatabase db = getWritableDatabase();
			lsGetWritableDatabase.end();

			ContentValues value = new ContentValues();
			value.put(COLUMN_1, name);
			value.put(COLUMN_2, keyword);
			value.put(COLUMN_3, result ? RESULT_MATCHED : RESULT_UNMATCHED);
			
			lcReplace.begin();
			db.replace(TABLE_NAME, null, value);
			lcReplace.end();
			
			lcClose.begin();
			db.close();
			lcClose.end();
		}
		catch (Exception e)
		{
		}
	}

	public Boolean select(String name, String keyword)
	{
		if (keyword == null || keyword.isEmpty())
		{
			throw new InvalidParameterException("keyword should not be null or empty");
		}

		Boolean ret = null;
		lcgetReadableDatabase.begin();
		SQLiteDatabase db = getReadableDatabase();
		lcgetReadableDatabase.end();
		
		lcQuery.begin();
		String select = COLUMN_1 + "='" + name + "' AND " + COLUMN_2 + "='" + keyword + "'";
		Cursor c = db.query(TABLE_NAME, new String[] { COLUMN_3 }, select, null, null, null, null);
		int ccc = -1;
		if (c.getCount() > 0)
		{
			c.moveToFirst();
			ccc = c.getInt(0);
			ret = (ccc == RESULT_MATCHED) ? true : (ccc == RESULT_UNMATCHED) ? false : null;
			ccc = -2;
		}

		c.close();
		lcQuery.end();
		
		lcClose.begin();
		db.close();
		lcClose.end();

		return ret;
	}
}
