package com.example.myfirstapp;
public class CellPoint {

	public float latitude, longitude;
	public int CellStrength;
	public String CellID, LAC, Provider;
	
	public CellPoint(float lat, float lon, int cs, String cellid, String l, String p) {
		latitude = lat;
		longitude = lon;
		CellStrength = cs;
		CellID = cellid;
		LAC = l;
		Provider = p;
	}
	
	public int getCellStrength() {
		return this.CellStrength;
	}
	

	
}
