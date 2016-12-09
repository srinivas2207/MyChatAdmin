package com.shree.mychatadmin.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;

import com.shree.mychatadmin.MyChatAdminApplication;
import com.shree.mychatadmin.database.DatabaseHelper;
import com.shree.mychatadmin.fragments.UserListFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DatabaseUtil {
	private static DatabaseUtil _dbUtil = null;
	private DatabaseHelper _dataDatabaseHelper = null;
	private static List<UserType> availableUserTypes = null;
	
	public DatabaseUtil() {
		_dataDatabaseHelper = new DatabaseHelper(MyChatAdminApplication.getInstance());
	}
	
	public static DatabaseUtil getInstance() {
		if(_dbUtil == null ) {
			_dbUtil = new DatabaseUtil();
		}
		return _dbUtil;
	}

	public void storeGcmToken(String gcmToken) {
		SharedPreferences prefs = MyChatAdminApplication.getInstance().getSharedPreferences(DbUtilConstants.MY_CHAT_PREFERENCE,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(DbUtilConstants.GCM_TOKEN, gcmToken);
		editor.commit();
	}

	public String getGcmToken() {
		SharedPreferences prefs = MyChatAdminApplication.getInstance().getSharedPreferences(DbUtilConstants.MY_CHAT_PREFERENCE,
				Context.MODE_PRIVATE);
		return prefs.getString(DbUtilConstants.GCM_TOKEN, null);
	}

	public List<UserType> getAvailableUserTypes() {
		return availableUserTypes;
	}

	public void setAvailableUserTypes(List<UserType> availableUserTypes) {
		DatabaseUtil.availableUserTypes =  availableUserTypes;
	}

	public void storeUserDetails(UserDetails userDetails) {
		ContentValues values = new ContentValues();
		values.put(DbUtilConstants.USER_NAME,userDetails.getUserName());
		values.put(DbUtilConstants.PHONE_NUMBER,userDetails.getPhoneNumber());
		values.put(DbUtilConstants.EMAIL_ID,userDetails.getEmaialId());
		values.put(DbUtilConstants.USER_TYPE,userDetails.getUserType());
		values.put(DbUtilConstants.USER_STATUS,userDetails.getUserStatus());

		int count = _getCount(" SELECT "+ DbUtilConstants.PHONE_NUMBER +" FROM "+ DbUtilConstants.USER_DETAILS_TABLE +" WHERE "+ DbUtilConstants.PHONE_NUMBER +" = \""+ userDetails.getPhoneNumber() + "\"  ");

		if (count > 0) {
			_updateRow(DbUtilConstants.USER_DETAILS_TABLE, values, DbUtilConstants.PHONE_NUMBER +" = \"" + userDetails.getPhoneNumber() + "\" ");

		} else {
			_insertRow(DbUtilConstants.USER_DETAILS_TABLE, values);
		}
		values = null;
	}
	
	public void deleteUser(UserDetails userDetails) {
		_deleteRow(DbUtilConstants.USER_DETAILS_TABLE, DbUtilConstants.PHONE_NUMBER+" = "+userDetails.getPhoneNumber());
	}

	public void deleteUsers(int type) {
		_deleteRow(DbUtilConstants.USER_DETAILS_TABLE, DbUtilConstants.USER_STATUS+" = "+type);
		if (type == ApplicationConstants.BLOCKED_USER) {
			_deleteRow(DbUtilConstants.USER_DETAILS_TABLE, DbUtilConstants.USER_STATUS+" = "+ApplicationConstants.BLOCKED_USER_2);
		}
	}


	public List<UserDetails> getUsers(int type) {
		String condition = " where " +DbUtilConstants.USER_STATUS + " = " + type ;
		if (type == ApplicationConstants.BLOCKED_USER) {
			condition += " OR " + DbUtilConstants.USER_STATUS + " = " + ApplicationConstants.BLOCKED_USER_2;
		}

		List<UserDetails> userList = null;
		String sql_query = "Select " +
				DbUtilConstants._ID + "," +
				DbUtilConstants.USER_NAME + "," +
				DbUtilConstants.PHONE_NUMBER + "," +
				DbUtilConstants.EMAIL_ID + "," +
				DbUtilConstants.USER_STATUS + "," +
				DbUtilConstants.USER_TYPE  +
				" from "+ DbUtilConstants.USER_DETAILS_TABLE +
				condition +
				" ORDER BY " + DbUtilConstants._ID + " desc";
		Cursor c = _dataDatabaseHelper.getReadableDatabase().rawQuery(sql_query, null);

		try{

			if ( c != null && c.getCount() > 0 ) {
				userList = new ArrayList<UserDetails>();
				int rowsCount = c.getCount();
				c.moveToFirst();
				for(int i=0;i<rowsCount;i++) {
					UserDetails userDetails = new UserDetails();
					userDetails.setUserName(c.getString(c.getColumnIndex(DbUtilConstants.USER_NAME)));
					userDetails.setPhoneNumber(c.getString(c.getColumnIndex(DbUtilConstants.PHONE_NUMBER)));
					userDetails.setEmaialId(c.getString(c.getColumnIndex(DbUtilConstants.EMAIL_ID)));
					userDetails.setUserStatus(c.getInt(c.getColumnIndex(DbUtilConstants.USER_STATUS)));
					userDetails.setUserType(c.getString(c.getColumnIndex(DbUtilConstants.USER_TYPE)));
					userList.add(userDetails);
					c.moveToNext();
				}
				c.close();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return userList;
	}


	
	public UserDetails getUserDetails(String phoneNumber) {
		UserDetails userDetails= null;

		String sql_query = "Select * from "+ DbUtilConstants.USER_DETAILS_TABLE +" where "+ DbUtilConstants.PHONE_NUMBER +" = "+phoneNumber ;
		
		Cursor c = _dataDatabaseHelper.getReadableDatabase().rawQuery(sql_query, null);
		try{					
			if ( c != null && c.getCount() > 0 ) {			
				c.moveToFirst();
				userDetails= new UserDetails();
				userDetails.setUserName(c.getString(c.getColumnIndex(DbUtilConstants.USER_NAME)));
				userDetails.setPhoneNumber(c.getString(c.getColumnIndex(DbUtilConstants.PHONE_NUMBER)));
				userDetails.setEmaialId(c.getString(c.getColumnIndex(DbUtilConstants.EMAIL_ID)));
				userDetails.setUserStatus(c.getInt(c.getColumnIndex(DbUtilConstants.USER_STATUS)));
				userDetails.setUserType(c.getString(c.getColumnIndex(DbUtilConstants.USER_TYPE)));
				c.close();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	   
		return userDetails;
	}
	

	private int _getCount(String querry) {
		try {
			Cursor c = _dataDatabaseHelper.getReadableDatabase().rawQuery( querry, null);
			return c.getCount();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	private void _updateRow(String tableName, ContentValues values, String whereClause) {
		 _dataDatabaseHelper.getWritableDatabase().update( tableName, values, whereClause, null );
	}
	
	private void _insertRow(String tableName, ContentValues values) {
		 _dataDatabaseHelper.getWritableDatabase().insertOrThrow( tableName, null, values);
	}
	
	private void _deleteRow(String tableName, String whereClause)  {
		_dataDatabaseHelper.getWritableDatabase().delete(tableName, whereClause, null);
	}


}
