package com.mobimedia.place.dbhelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper {

	private static String DB_PATH = "/data/data/com.mobimedia.place/databases/";

	public static String TABLE_NAME = "table_BoundryData";
	private static String DATABASE_NAME = "worldmapsearch.db";
	private static int version = 1;
	public static String CountryName = "Country_Name";
	public static String Boundry_Point = "boundry_point";
	SQLiteDatabase myDataBase;
	static Context myContext;

	String query = "CREATE TABLE if not exists " + TABLE_NAME + " ("
			+ "_id INTEGER PRIMARY KEY AUTOINCREMENT," + CountryName + " TEXT,"
			+ Boundry_Point + " TEXT);";

	public DataBaseHelper(Context context) {
		super(context, DATABASE_NAME, null, version);
		this.myContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(query);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
	}

	public void createDatabase() throws IOException {
		boolean dbExist = checkDataBase();

		if (dbExist) {

		} else {
			this.getReadableDatabase();
			try {
				copyDataBase();
			} catch (IOException e) {
				Log.e("DataBasehelper",
						" Successfully  created==" + e.getMessage());
			}
		}
	}

	public static void copyDataBase() throws IOException {
		InputStream myInput = myContext.getAssets().open(DATABASE_NAME);
		String outFileName = DB_PATH + DATABASE_NAME;
		OutputStream myOutput = new FileOutputStream(outFileName);

		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	public static boolean checkDataBase() {
		SQLiteDatabase tempDB = null;
		try {
			String myPath = DB_PATH + DATABASE_NAME;
			tempDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READWRITE);
		} catch (SQLiteException e) {
			Log.e("DataBaseHelper", "Database Checked==" + e.getMessage());
		}
		if (tempDB != null)
			tempDB.close();
		return tempDB != null ? true : false;
	}

	@Override
	public synchronized void close() {
		if (myDataBase != null)
			myDataBase.close();

		super.close();
	}

}
