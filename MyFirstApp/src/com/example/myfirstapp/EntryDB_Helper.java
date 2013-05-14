package com.example.myfirstapp;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class EntryDB_Helper extends SQLiteOpenHelper{
	
	//some constant string definitions
			static final String DB_PATH = "";
			static final String DB_NAME = "entry.sqlite";
			static final int SQLITE_DB_VERSION = 5;
			public static final String TABLE_ENTRY = "Entries";
			public static final String CREATE_ENTRY_TABLE = "CREATE TABLE IF NOT EXISTS " +
					"Entries ( _id integer primary key autoincrement, " +
					"TimeStamp text NOT NULL, CellId " +
					"text, LAC text, MacAddress text NOT NULL, WiFiSSID text NOT NULL, Description text, latitude real " +
					", longitude real , WiFiStrength integer" +
					" NOT NULL, CellStrength integer, FOREIGN " +
					"KEY(CellId) REFERENCES CellIDLAC(CellID)" +
					" ON UPDATE CASCADE ON DELETE CASCADE, FOREIGN KEY(MacAddress)" +
					" REFERENCES WiFiAccessPoints(MacAddress) ON UPDATE CASCADE" +
					" ON DELETE CASCADE);";
			//_id, TimeStamp, CellId, LAC, MacAddress, WiFiSSID, Description, latitude, longitude, WiFiStrength, CellStrength
			//Time is of format YYYY-MM-DD HH:MM:SS.SSS
			private SQLiteDatabase myDB;
			//private final Context myContext;

			public EntryDB_Helper(Context context) {
				super(context, DB_NAME, null, SQLITE_DB_VERSION);
				
			}

			@Override
			public void onCreate(SQLiteDatabase db) {
				db.execSQL(CREATE_ENTRY_TABLE); //creating table Entries
				
			}

			@Override
			public void onUpgrade(SQLiteDatabase db, int oldVersion,
					int newVersion) {
				Log.w(EntryDB_Helper.class.getName(),
				        "Upgrading database from version " + oldVersion + " to "
				            + newVersion + ".");
				    db.execSQL("DROP TABLE Entries");
				    onCreate(db);
				
			}
			
			/**
			 * Helper function adds new row (timestamp, CellID, MACAddress, 
			 * latitude, longitude, WiFiStrength, cellStrength ) to the table.
			 * 
			 **/
			public void addRow(String timestamp, String CellID, String MAC, float lat, float lon, int WStrength, int cStrength, String SSID, String descr, String LAC) {
				
				myDB = getWritableDatabase();
				ContentValues values = new ContentValues();
				
				values.put("TimeStamp", timestamp);
				values.put("MacAddress", MAC);
				values.put("LAC", LAC);
				values.put("Description", descr);
				values.put("WiFiSSID", SSID);
				values.put("CellId", CellID);
				values.put("latitude", lat);
				values.put("longitude", lon);
				values.put("WiFiStrength", WStrength);
				values.put("CellStrength", cStrength);
				myDB.insert(TABLE_ENTRY, null, values);
				myDB.close();
			}
			
			public ArrayList<MapPoint> getLatLong() {
				ArrayList<MapPoint> ret = new ArrayList<MapPoint>();
				myDB = getReadableDatabase();
				String query = "SELECT DISTINCT latitude, longitude, WiFiStrength, CellStrength, MacAddress, CellId," +
						"LAC, WiFiSSID, Description FROM Entries;";
				System.out.println("getLatLong1");
				Cursor c = myDB.rawQuery(query, null);
				System.out.println("getLatLong2");

				if (c != null) {
					if(c.moveToFirst()) {
					
						do{
							Float lat = c.getFloat(c.getColumnIndex("latitude"));
							Float lon = c.getFloat(c.getColumnIndex("longitude"));
							Integer wfs = c.getInt(c.getColumnIndex("WiFiStrength"));
							Integer CellStrength = c.getInt(c.getColumnIndex("CellStrength"));
							String MAC = c.getString(c.getColumnIndex("MacAddress"));
							String cellid = c.getString(c.getColumnIndex("CellId"));
							String LAC = c.getString(c.getColumnIndex("LAC"));
							String SSID = c.getString(c.getColumnIndex("WiFiSSID"));
							String description = c.getString(c.getColumnIndex("Description"));
							MapPoint mp = new MapPoint(lat, lon, wfs, CellStrength, cellid, LAC, SSID, MAC, description);
							ret.add(mp);
						} while(c.moveToNext());	
					}
				}
				c.close();
				
				myDB.close();
				System.out.println("VALUE:: "+ret.get(0).SSID);
				return ret;
				
			}

}
