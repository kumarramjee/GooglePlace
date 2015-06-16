package com.mobimedia.place;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.mobimedia.place.helper.Country;
import com.mobimedia.place.helper.PolygonCreator;

public class MapShowActivity extends ActionBarActivity implements
		LocationListener, OnClickListener {
	LayoutInflater inflator;
	private GoogleMap map = null;
	private TextView mtextRoute;
	private List<List<LatLng>> countryboundrylatlong;
	private ImageView iview;
	Context context;
	TextView tview;
	boolean isGPSEnabled;
	Location location;
	GoogleMap mGoogleMap;
	Spinner mSprPlaceType;
	LatLng latLng;
	double latitude;
	double longitude;

	String[] mPlaceType = null;
	String[] mPlaceTypeName = null;

	double mLatitude = 0;
	double mLongitude = 0;
	TextView hotel;
	TextView hospital;

	@SuppressWarnings("unchecked")
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maphow);

		PolygonCreator polygencreator = new PolygonCreator();

		tview = (TextView) findViewById(R.id.tv_location);

		iview = (ImageView) findViewById(R.id.currentlocationicon);
		inflator = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		setupActionBar();
		getSupportActionBar().setBackgroundDrawable(null);

		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		map.getUiSettings().setZoomControlsEnabled(false);

		mtextRoute = (TextView) findViewById(R.id.route);

		Country country = (Country) getIntent()
				.getSerializableExtra("selected");
		List<LatLng> boundrylatlong = (List<LatLng>) getIntent()
				.getSerializableExtra(country.boundry_point);

		countryboundrylatlong = polygencreator
				.ParseLatLongFromDataBase(country.boundry_point);

		drawPolygonOnMap(countryboundrylatlong);

		iview.setOnClickListener(this);

		mPlaceType = getResources().getStringArray(R.array.place_type);

		mPlaceTypeName = getResources().getStringArray(R.array.place_type_name);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, mPlaceTypeName);
		mSprPlaceType = (Spinner) findViewById(R.id.spr_place_type);
		mSprPlaceType.setAdapter(adapter);

		Button btnFind;
		btnFind = (Button) findViewById(R.id.btn_find);
		int status = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getBaseContext());
		if (status != ConnectionResult.SUCCESS) {

			int requestCode = 10;
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this,
					requestCode);
			dialog.show();

		} else {
			map = ((MapFragment) getFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
			map.getUiSettings().setZoomControlsEnabled(false);

		}

		btnFind.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
              map.clear();
				int selectedPosition = mSprPlaceType.getSelectedItemPosition();
				String type = mPlaceType[selectedPosition];
				StringBuilder sb = new StringBuilder(
						"https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
				sb.append("location=" + latitude + "," + longitude);
				sb.append("&radius=5000");
				sb.append("&types=" + type);
				sb.append("&sensor=true");
				sb.append("&key=AIzaSyCwF3l7MtnIytu5LIqnfw0fRbQhuhCFaWg");
            	PlacesTask placesTask = new PlacesTask();
				placesTask.execute(sb.toString());

			}
		});

		mtextRoute.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setonclick(v);
			}

			private void setonclick(View v) {
				final Dialog dialog = new Dialog(MapShowActivity.this);
				dialog.setContentView(R.layout.destinationname);
				dialog.setTitle("Destination");

				dialog.getWindow().setBackgroundDrawableResource(
						R.drawable.abc_list_selector_disabled_holo_dark);

				TextView getdirection = (TextView) dialog
						.findViewById(R.id.getdirection);

				final EditText getsource = (EditText) dialog
						.findViewById(R.id.source);

				final CheckBox checkbox = (CheckBox) dialog
						.findViewById(R.id.checkbox);
				checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (checkbox.isChecked()) {
							getsource.setEnabled(false);
							Log.i("CHECK box clicked", "in map show activity");

						}

						else {
							getsource.setEnabled(true);

						}

					}
				});
				getdirection.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) { // TODO Auto-generated method
													// stub
						EditText getsource = (EditText) dialog
								.findViewById(R.id.source);

						String source = getsource.getText().toString();

						EditText getDestination = (EditText) dialog
								.findViewById(R.id.enterdestination);
						String destination = getDestination.getText()
								.toString();

						Intent intent = new Intent(MapShowActivity.this,
								RouteLocator.class);
						intent.putExtra("Sourcename", source);
						intent.putExtra("Destination", destination);
						startActivity(intent);
					}
				});

				dialog.show();
			}
		});

	}

	private String downloadUrl(String strUrl) throws IOException {
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(strUrl);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.connect();
			iStream = urlConnection.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					iStream));
			StringBuffer sb = new StringBuffer();
			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			data = sb.toString();
			br.close();
		} catch (Exception e) {e.printStackTrace();
		} finally {
			iStream.close();
			urlConnection.disconnect();
		}

		return data;
	}

	private void setupActionBar() {
		android.support.v7.app.ActionBar actionBar1 = getSupportActionBar();
		actionBar1.setDisplayShowHomeEnabled(true);
		actionBar1.setDisplayShowCustomEnabled(true);
		actionBar1.setDisplayShowTitleEnabled(true);
		View v = inflator.inflate(R.layout.actionbarforresourceplace, null);

		actionBar1.setCustomView(v);
	}

	public void ShowCurrentLocationOnMap(GoogleMap map) {
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		String provider = locationManager.getBestProvider(criteria, true);
		location = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (location != null) {
			onLocationChanged(location);
		}
		locationManager.requestLocationUpdates(provider, 20000, 0, this);
	}

	private void drawPolygonOnMap(List<List<LatLng>> countryboundrylatlong) {

		LatLng cameraPos = null;
		Country country = (Country) getIntent()
				.getSerializableExtra("selected");

		for (List<LatLng> list : countryboundrylatlong) {
			PolygonOptions rectOptions = new PolygonOptions();
			rectOptions.addAll(list);

			map.addPolygon(rectOptions).setStrokeColor(Color.RED);
			cameraPos = new LatLng(list.get(0).latitude, list.get(0).longitude);
			map.addMarker(new MarkerOptions().position(
					new LatLng(list.get(0).latitude, list.get(0).longitude))
					.title(country.country_Name));

		}
		com.google.android.gms.maps.model.CameraPosition currentPlace = new com.google.android.gms.maps.model.CameraPosition.Builder()
				.target(cameraPos).zoom((float) 5.4).build();

		map.animateCamera(CameraUpdateFactory.newCameraPosition(currentPlace));

	}

	@Override
	public void onLocationChanged(Location location) {
		latitude = location.getLatitude();
		longitude = location.getLongitude();
		latLng = new LatLng(latitude, longitude);

		Geocoder geocoder = new Geocoder(this, Locale.getDefault());
		List<Address> addresses;
		try {
			addresses = geocoder.getFromLocation(latitude, longitude, 1);

			String cityName = addresses.get(0).getAddressLine(0);

			map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
			map.animateCamera(CameraUpdateFactory.zoomTo(15));

			tview.setText("Current place is:" + cityName + ",Latitude:"
					+ latitude + ", Longitude:" + longitude);

			MarkerOptions marker = new MarkerOptions().position(
					new LatLng(latitude, longitude)).title(cityName);
			map.addMarker(marker);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	
	}

	@Override
	public void onProviderEnabled(String provider) {
	
	}

	@Override
	public void onProviderDisabled(String provider) {
	
	}

	@Override
	public void onClick(View v) {
		StartGPS();
	}

	/*----------Method to Check GPS is enable or disable ------------- */
	private Boolean displayGpsStatus() {
		ContentResolver contentResolver = this.getContentResolver();
		boolean gpsStatus = Settings.Secure.isLocationProviderEnabled(
				contentResolver, LocationManager.GPS_PROVIDER);
		if (gpsStatus) {
			return true;

		} else {
			return false;
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	private boolean mFlag = false;

	// Start the GPS
	public void StartGPS() {
		mFlag = displayGpsStatus();
		if (mFlag) {
			ShowCurrentLocationOnMap(map);
		} else {
			Log.d("gps off", "gps off");
			alertbox("Gps Status!!", "Your GPS is: OFF");
		}
	}

	private AlertDialog mAlert;

	/*----------Method to create an AlertBox ------------- */

	protected void alertbox(String title, String mymessage) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Your Device's GPS is Disable")
				.setCancelable(false)
				.setTitle("** Gps Status **")
				.setPositiveButton("Gps On",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Intent myIntent = new Intent(
										Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								startActivity(myIntent);
								dialog.cancel();
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								finish();
								dialog.cancel();
							}
						});
		mAlert = builder.create();
		mAlert.show();
	}

	/** A class, to download Google Places */
	private class PlacesTask extends AsyncTask<String, Integer, String> {

		String data = null;

		@Override
		protected String doInBackground(String... url) {
			try {
				data = downloadUrl(url[0]);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		@Override
		protected void onPostExecute(String result) {
			ParserTask parserTask = new ParserTask();
			parserTask.execute(result);
		}
	}

	/** A class to parse the Google Places in JSON format */
	class ParserTask extends
			AsyncTask<String, Integer, List<HashMap<String, String>>> {

		JSONObject jObject;

		@Override
		protected List<HashMap<String, String>> doInBackground(
				String... jsonData) {

			List<HashMap<String, String>> places = null;
			PlaceJSONParser placeJsonParser = new PlaceJSONParser();

			try {
				jObject = new JSONObject(jsonData[0]);

				/** Getting the parsed data as a List construct */
				places = placeJsonParser.parse(jObject);

			} catch (Exception e) {
				Log.d("Exception", e.toString());
			}
			return places;
		}

		@Override
		protected void onPostExecute(List<HashMap<String, String>> list) {

			for (int i = 0; i < list.size(); i++) {

				MarkerOptions markerOptions = new MarkerOptions();
				HashMap<String, String> hmPlace = list.get(i);
				double lat = Double.parseDouble(hmPlace.get("lat"));
				double lng = Double.parseDouble(hmPlace.get("lng"));
				String name = hmPlace.get("place_name");
				String vicinity = hmPlace.get("vicinity");
    			LatLng latLng = new LatLng(lat, lng);
				markerOptions.position(latLng);
				markerOptions.title(name + " : " + vicinity);
				map.addMarker(markerOptions);
				map.addMarker(markerOptions.position(latLng)).setTitle(
						name + " : " + vicinity);

			}

		}

	}
	
	

	public class DisplayboundryPoint extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return null;
		}
		
	
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}










// ////dfgdfkjgbdkjgbdjfkg new method below

/*
 * public void statusCheck() { final LocationManager manager = (LocationManager)
 * getSystemService(Context.LOCATION_SERVICE);
 * 
 * if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
 * buildAlertMessageNoGps();
 * 
 * }
 * 
 * }
 */

/*
 * private void buildAlertMessageNoGps() { final AlertDialog.Builder builder =
 * new AlertDialog.Builder(this); builder.setMessage(
 * "Your GPS seems to be disabled, do you want to enable it?")
 * .setCancelable(false) .setPositiveButton("Yes", new
 * DialogInterface.OnClickListener() { public void onClick(final DialogInterface
 * dialog, final int id) { startActivity(new Intent(
 * android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)); } })
 * .setNegativeButton("No", new DialogInterface.OnClickListener() { public void
 * onClick(final DialogInterface dialog, final int id) { dialog.cancel(); } });
 * final AlertDialog alert = builder.create(); alert.show();
 * 
 * }
 */

// ////dfgdfkjgbdkjgbdjfkg new method below

/*
 * 
 * public void onLocationChanged(Location location) { mLatitude =
 * location.getLatitude(); mLongitude = location.getLongitude(); LatLng latLng =
 * new LatLng(mLatitude, mLongitude);
 * 
 * map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
 * map.animateCamera(CameraUpdateFactory.zoomTo(12));
 * 
 * }
 */
// ////dfgdfkjgbdkjgbdjfkg new method below

/*
 * mtextRoute.setOnClickListener(new OnClickListener() {
 * 
 * @Override public void onClick(View v) { setonclick(v); }
 * 
 * private void setonclick(View v) { final Dialog dialog = new
 * Dialog(MapShowActivity.this);
 * dialog.setContentView(R.layout.destinationname);
 * dialog.setTitle("Destination");
 * 
 * dialog.getWindow().setBackgroundDrawableResource(
 * R.drawable.abc_list_selector_disabled_holo_dark);
 * 
 * TextView getdirection = (TextView) dialog .findViewById(R.id.getdirection);
 * 
 * final EditText getsource = (EditText) dialog .findViewById(R.id.source);
 * 
 * 
 * 
 * final CheckBox checkbox = (CheckBox) dialog .findViewById(R.id.checkbox);
 * checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() { public
 * void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { if
 * (checkbox.isChecked()) { getsource.setEnabled(false);
 * Log.i("CHECK box clicked", "in map show activity");
 * 
 * 
 * 
 * }
 * 
 * else { getsource.setEnabled(true);
 * 
 * }
 * 
 * } }); getdirection.setOnClickListener(new OnClickListener() {
 * 
 * @Override public void onClick(View v) { // TODO Auto-generated method stub
 * EditText getsource = (EditText) dialog .findViewById(R.id.source);
 * 
 * String source=getsource.getText().toString();
 * 
 * 
 * 
 * EditText getDestination=(EditText)
 * dialog.findViewById(R.id.enterdestination); String
 * destination=getDestination.getText().toString();
 * 
 * Intent intent = new Intent(MapShowActivity.this, RouteLocator.class);
 * intent.putExtra("Sourcename",source); intent.putExtra("Destination",
 * destination); startActivity(intent); } });
 * 
 * dialog.show(); } }); mtextPlace.setOnClickListener(new OnClickListener() {
 * 
 * @Override public void onClick(View v) { // TODO Auto-generated method stub
 * final Dialog dialog = new Dialog(MapShowActivity.this);
 * dialog.setContentView(R.layout.placename); dialog.setTitle("Place");
 * dialog.getWindow().setBackgroundDrawableResource(
 * R.drawable.abc_list_selector_disabled_holo_dark); hotel = (TextView)
 * dialog.findViewById(R.id.hotel); hospital = (TextView)
 * dialog.findViewById(R.id.hospital); TextView college = (TextView)
 * dialog.findViewById(R.id.college); TextView airport = (TextView)
 * dialog.findViewById(R.id.airport); TextView railway = (TextView) dialog
 * .findViewById(R.id.railwaystation); TextView beach = (TextView)
 * dialog.findViewById(R.id.beach); hospital.setOnClickListener(this);
 * hotel.setOnClickListener(this); college.setOnClickListener(this);
 * airport.setOnClickListener(this); railway.setOnClickListener(this);
 * beach.setOnClickListener(this);
 * 
 * dialog.show(); } });
 */

