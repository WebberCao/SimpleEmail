package com.webber.simpleemail.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DButil extends SQLiteOpenHelper{
	public DButil(Context context) {
		super(context, "emailconstants.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table email(id INTEGER PRIMARY KEY AUTOINCREMENT,mailfrom varchar(20),name varchar(20),address varchar(20))");
		db.execSQL("create table caogaoxiang(id INTEGER PRIMARY KEY AUTOINCREMENT,mailfrom varchar(20),mailto varchar(20),subject varchar(20),content text)");
		db.execSQL("create table attachment(id INTEGER PRIMARY KEY AUTOINCREMENT,filename varchar(20),filepath varchar(100),filesize varchar(20),mailid varchar(20))");
		db.execSQL("create table emailstatus(id INTEGER PRIMARY KEY AUTOINCREMENT,mailfrom varchar(20),messageid varchar(100))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

}
