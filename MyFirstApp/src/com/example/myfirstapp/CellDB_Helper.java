package com.example.myfirstapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CellDB_Helper extends SQLiteOpenHelper{
	
	//some constant string definitions
	static final String DB_PATH = "";
	static final String DB_NAME = "cellInfo.sqlite";
	static final int SQLITE_DB_VERSION = 1;
	public static final String TABLE_CELL = "CellIDLAC";
	public static final String CREATE_CELL_TABLE = "CREATE TABLE " +
			"CellIDLAC ( _id integer primary key autoincrement, " +
			"CellID text, LAC text);";
	
	private SQLiteDatabase myDB; //my own database instance
	
	
	public CellDB_Helper(Context context) {
		super(context, DB_NAME, null, SQLITE_DB_VERSION);
		
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_CELL_TABLE); //creating table CellIDLAC if it doesn't exist
		
	}


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(CellDB_Helper.class.getName(),
		        "Upgrading database from version " + oldVersion + " to "
		            + newVersion + ".");
		    db.execSQL("DROP TABLE IF EXISTS CellID");
		    onCreate(db);
		
	}
	
	/**
	 * Helper function adds new row (CellID, LAC) to the table. 
	 **/
	public void addRow(String CellID, String LAC) {
		
		myDB = getWritableDatabase();
		ContentValues values = new ContentValues();
		
		values.put("CellID", CellID);
		values.put("LAC", LAC);
	
		myDB.insert(TABLE_CELL, null, values);
		myDB.close();
	}

}
