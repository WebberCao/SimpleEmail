package com.webber.simpleemail.provider;

import com.webber.simpleemail.utils.DButil;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class EmailConstantProvider extends ContentProvider {
	private DButil util;
	@Override
	public boolean onCreate() {
		util=new DButil(getContext());
		return false;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db=util.getWritableDatabase();
		db.delete("email", selection, selectionArgs);
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}
    
	/**
	 * 插入数据
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db=util.getWritableDatabase();
		long id=db.insert("email", null, values);
		getContext().getContentResolver().notifyChange(uri, null);
		return ContentUris.withAppendedId(uri, id);
	}

	
    
	/**
	 * 查询数据
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		   SQLiteDatabase db=util.getReadableDatabase();
		   Cursor c=db.query("email", null, selection, selectionArgs, null, null, null);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db=util.getWritableDatabase();
		db.update("email", values, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return 0;
	}

}
