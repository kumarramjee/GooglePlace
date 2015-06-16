package com.mobimedia.place;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.mobimedia.place.dbhelper.DataBaseHelper;
import com.mobimedia.place.dbhelper.DataBaseSource;
import com.mobimedia.place.helper.Country;

public class MainActivity extends Activity implements OnItemSelectedListener {
	private Spinner spinner;
	private ArrayAdapter<String> arrayadapter;
	private DataBaseHelper dbHeplper;
	private List<Country> getalldata;
	private DataBaseSource dsource;
	List<LatLng> latlng;
	Cursor cur;
	Polygon pol;
	private Country mSelectedCountry;
	private List<String> listItem;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final TextView textenter = (TextView) findViewById(R.id.entername);

		spinner = (Spinner) findViewById(R.id.spinner);

		dbHeplper = new DataBaseHelper(getApplicationContext());
		try {
			dbHeplper.createDatabase();
		} catch (IOException e) {
			e.printStackTrace();
		}
		dsource = new DataBaseSource(this);

		getalldata = dsource.getAllRecord();
		listItem = new ArrayList<String>();
		for (Country country : getalldata) {
			listItem.add(country.country_Name);
		}
		arrayadapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, listItem);
		arrayadapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(arrayadapter);

		textenter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intentt = new Intent(MainActivity.this,
						MapShowActivity.class);
				intentt.putExtra("selected", mSelectedCountry);
				startActivity(intentt);
			}
		});

		spinner.setOnItemSelectedListener(this);
	}

	@Override
	public void onItemSelected(AdapterView<?> adapter, View view, int position,
			long id) {
		mSelectedCountry = getalldata.get(position);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}

}