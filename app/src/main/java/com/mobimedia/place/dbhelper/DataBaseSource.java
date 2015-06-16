package com.mobimedia.place.dbhelper;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.mobimedia.place.helper.Country;

public class DataBaseSource {
	private SQLiteDatabase database = null;
	private Cursor cursor;
	DataBaseHelper dbhelper;
	Polygon pol;
	String[] latlonglist;
	private String TABLE_NAME = "table_BoundryData";
	public static String CountryName = "Country_Name";
	public static String Boundry_Point = "boundry_point";
	List<LatLng> finasallatlnglist;

	public DataBaseSource(Context context) {
		dbhelper = new DataBaseHelper(context);
	}

	public List<Country> getAllRecord() {
		List<Country> CountryList = new ArrayList<Country>();

		String selectQuery = "SELECT  * FROM " + TABLE_NAME;
		database = dbhelper.getReadableDatabase();
		cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				Country cobj = new Country();
				cobj.id = cursor.getInt(0);
				cobj.country_Name = cursor.getString(1);
				cobj.boundry_point = cursor.getString(2);
				CountryList.add(cobj);
			} while (cursor.moveToNext());
		}
		database.close();
		return CountryList;
	}

	public Cursor getCountryNameAndPoint(String countryname,
			SQLiteDatabase database) {

		Log.i("DAtabaseSource", ":" + countryname);
		return database.rawQuery("SELECT " + Boundry_Point + " FROM "
				+ TABLE_NAME + " WHERE " + CountryName + " = " + "'"
				+ countryname + "'", latlonglist);
	}

	@SuppressLint("UseValueOf")
	public List<LatLng> checkForPolygonOrMulti(String polygoiPoints) {

		ArrayList<LatLng> finallistofLatlng = new ArrayList<LatLng>();
		ArrayList<LatLng> mlistitem = new ArrayList<LatLng>();

		StringBuffer sb = new StringBuffer(polygoiPoints);
		if (polygoiPoints.startsWith("POLYGON")) {

			String polygonPoints = sb.substring("POLYGON((".length(),
					polygoiPoints.length() - 2);
			String latLongpoints[] = polygonPoints.split(",");

			for (int i = 0; i < latLongpoints.length; i++) {
				String singlelatlongvalue[] = latLongpoints[i].split(" ");
				LatLng latilonglist = new LatLng(
						Double.parseDouble(singlelatlongvalue[0]),
						Double.parseDouble(singlelatlongvalue[1]));
				mlistitem.add(latilonglist);
			}
		} else if (polygoiPoints.startsWith("MULTIPOLYGON")) {
			sb.substring("MULTIPOLYGON((".length(), polygoiPoints.length() - 3);

		}
		finallistofLatlng.addAll(mlistitem);

		return finasallatlnglist;
	}
}
