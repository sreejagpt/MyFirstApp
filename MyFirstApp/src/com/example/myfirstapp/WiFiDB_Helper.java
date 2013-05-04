package com.example.myfirstapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class WiFiDB_Helper extends SQLiteOpenHelper {	/**
	 * Reading from the SQLite database
	 */
	
		//some constant string definitions
		static final String DB_PATH = "";
		static final String DB_NAME = "wifi.sqlite";
		static final int SQLITE_DB_VERSION = 1;
		public static final String TABLE_WIFI = "WiFiAccessPoints";
		public static final String CREATE_WIFI_TABLE = "CREATE TABLE " +
				"WiFiAccessPoints ( _id integer primary key autoincrement, " +
				"MacAddress text NOT NULL, WiFiSSID text NOT NULL, Descript" +
				"ion text);";
		
		
		private SQLiteDatabase myDB;
		//private final Context myContext;

		public WiFiDB_Helper(Context context) {
			super(context, DB_NAME, null, SQLITE_DB_VERSION);
			
		}

		
		

		@Override
		public void onCreate(SQLiteDatabase db) {
			
			db.execSQL(CREATE_WIFI_TABLE); //creating table WiFiAccessPoints
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(WiFiDB_Helper.class.getName(),
			        "Upgrading database from version " + oldVersion + " to "
			            + newVersion + ".");
			    db.execSQL("DROP TABLE IF EXISTS SSID");
			    onCreate(db);
		}

		
		
		/**
		 * Helper function adds new row (SSID, Mac, descr) to the table. If SSID
		 * already exists, the description is updated
		 **/
		public void addRow(String SSID, String MAC, String descr) {
			//If MacAddress already exists, simply update its descr
			myDB = getReadableDatabase();
			String query = "SELECT * FROM WiFiAccessPoints WHERE MacAddress = '" +MAC+"';";
			Cursor c = myDB.rawQuery(query, null);
			
			if (c.getCount() > 0) {
				//update descr
				query = "UPDATE "+TABLE_WIFI+ " SET "+"Description = '"+descr+"'"+
				" WHERE MacAddress = '"+MAC+"' AND WiFiSSID = '"+SSID+"';";
				c = myDB.rawQuery(query,  null);
				myDB.close();
				return;				
			}
			
			myDB = getWritableDatabase();
			ContentValues values = new ContentValues();
			
			values.put("MacAddress", MAC);
			values.put("WiFiSSID", SSID);
			values.put("Description", descr);
			myDB.insert(TABLE_WIFI, null, values);
			myDB.close();
		}
		
		
		
		
	}