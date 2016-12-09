package com.shree.mychatadmin.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.shree.mychatadmin.util.DbUtilConstants;


public class DatabaseHelper extends SQLiteOpenHelper{
	
	private static String DATABASE_NAME = "mychat.db";
	private static int DATABASE_VERSION = 1;
	 
	 private static final String CREATE_TABLE_USER_DETAILS = "create table "
		      + DbUtilConstants.USER_DETAILS_TABLE
		      + "(" 
		      + DbUtilConstants._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
		      + DbUtilConstants.USER_NAME  + " TEXT, "
		      + DbUtilConstants.PHONE_NUMBER  + " TEXT, "
			 + DbUtilConstants.EMAIL_ID  + " TEXT, "
		      + DbUtilConstants.USER_TYPE  + " TEXT, "
		      + DbUtilConstants.USER_STATUS  + " INTEGER DEFAULT 0"
		      +");";

	public DatabaseHelper(Context context) {
	    super(context, DATABASE_NAME, null, DATABASE_VERSION);
	  }

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_USER_DETAILS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		db.execSQL("DROP TABLE IF EXISTS " + DbUtilConstants.MOVIE_DETAILS_TABLE);
//	    onCreate(db);
	}

}
