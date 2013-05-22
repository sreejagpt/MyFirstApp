package com.example.myfirstapp;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CellDB_Helper extends SQLiteOpenHelper{
	
	//some constant string definitions
	static final String DB_PATH = "";
	static final String DB_NAME = "cellInfo.sqlite";
	static final int SQLITE_DB_VERSION = 7;
	public static final String TABLE_CELL = "CellIDLAC";
	public static final String CREATE_CELL_TABLE = "CREATE TABLE " +
			"CellIDLAC ( _id integer primary key autoincrement, " +
			"CellID text, LAC text, Provider text, CStrength integer, latitude real, longitude real);";
	
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
		    db.execSQL("DROP TABLE CellIDLAC");
		    onCreate(db);
		
	}
	
	/**
	 * Helper function adds new row (CellID, LAC) to the table. 
	 **/
	public void addRow(String CellID, String LAC, String provider, int cellStrength, float lat, float lon) {
		
		myDB = getWritableDatabase();
		ContentValues values = new ContentValues();
		
		values.put("CellID", CellID);
		values.put("LAC", LAC);
		values.put("Provider", provider);
		values.put("CStrength", cellStrength);
		values.put("latitude", lat);
		values.put("longitude", lon);
		myDB.insert(TABLE_CELL, null, values);
		myDB.close();
	}


	public ArrayList<CellPoint> getLatLong() {
		ArrayList<CellPoint> ret = new ArrayList<CellPoint>();
		myDB = getReadableDatabase();
		String query = "SELECT DISTINCT latitude, longitude, CStrength, CellID," +
				"LAC, Provider FROM CellIDLAC;";
		Cursor c = myDB.rawQuery(query, null);
		
		if (c != null) {
			if(c.moveToFirst()) {
			
				do{
					Float lat = c.getFloat(c.getColumnIndex("latitude"));
					Float lon = c.getFloat(c.getColumnIndex("longitude"));
					Integer CellStrength = c.getInt(c.getColumnIndex("CStrength"));
					String cellid = c.getString(c.getColumnIndex("CellID"));
					String LAC = c.getString(c.getColumnIndex("LAC"));
					String prov = c.getString(c.getColumnIndex("Provider"));
					CellPoint cp = new CellPoint(lat, lon, CellStrength, cellid, LAC, prov);
					ret.add(cp);
				} while(c.moveToNext());	
			}
		}
		c.close();
		
		myDB.close();
		return ret;
	}

}
