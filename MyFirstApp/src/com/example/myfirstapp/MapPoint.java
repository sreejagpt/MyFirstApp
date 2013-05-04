package com.example.myfirstapp;
public class MapPoint {

	public float latitude, longitude;
	public int WifiStrength, CellStrength;
	public String CellID, LAC, SSID, MAC, description;
	
	public MapPoint(float lat, float lon, int wfs, int cs, String cellid, String l, String s, String m, String d) {
		latitude = lat;
		longitude = lon;
		WifiStrength = wfs;
		CellStrength = cs;
		CellID = cellid;
		LAC = l;
		SSID = s;
		MAC = m;
		description = d;
	}
	
	public int getWiFiStrength() {
		return this.WifiStrength;
	}
	
	public int getCellStrength() {
		return this.CellStrength;
	}
	
}
