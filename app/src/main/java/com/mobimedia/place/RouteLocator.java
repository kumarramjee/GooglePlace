package com.mobimedia.place;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mobimedia.place.helper.GeocoderTask;

public class RouteLocator extends Activity {
	private GoogleMap map;
	private Context context;
	private LayoutInflater inflator;
	private ImageView iview;
	private GoogleMap googleMap;
	private MarkerOptions markerOptions;
	private LatLng latLng;
	EditText findlocation;
	String location;
	LatLng cameraPos;
	LatLng sourcelatlng;
	LatLng destinatonlatlng;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_locator);
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map1))
				.getMap();
		map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		map.getUiSettings().setZoomControlsEnabled(false);
		findlocation = (EditText) findViewById(R.id.entertexttosearch);

		iview = (ImageView) findViewById(R.id.searchlocation);
		RouteBetweenTwoLocation routelocation = new RouteBetweenTwoLocation(
				this);

		Intent intent = getIntent();
		String sourceplace = intent.getStringExtra("Sourcename");
		String destinationplcae = intent.getStringExtra("Destination");
		Log.i("Route Locator Actvity", " .." + sourceplace + ",,"
				+ destinationplcae);

		sourcelatlng = routelocation.getLocationFromAddress(sourceplace);

		destinatonlatlng = routelocation
				.getLocationFromAddress(destinationplcae);

		Log.i("SourceRoute Location", " Latlng is:" + sourcelatlng);
		Log.i("DestinationRoute Location", " Latlng is:" + destinatonlatlng);

		String stringurloflatlong = routelocation.makeURL(
				sourcelatlng.latitude, sourcelatlng.longitude,
				destinatonlatlng.latitude, destinatonlatlng.longitude);

		Log.i("Rout lOcatore", "Route Locatore in urlForm" + stringurloflatlong);
		connectAsyncTask ctask = new connectAsyncTask(stringurloflatlong);
		ctask.execute();

		iview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				findlocation = (EditText) findViewById(R.id.entertexttosearch);
				location = findlocation.getText().toString();

				Log.i("Search locatio  is", "==" + location);

				Toast.makeText(getApplicationContext(),
						"Searching place is:" + location, Toast.LENGTH_LONG)
						.show();

				if (location != null && !location.equals("")) {
					new GeocoderTask().execute(location);
				}

			}
		});

	}

	public class GeocoderTask extends AsyncTask<String, Void, List<Address>> {

		@Override
		protected List<Address> doInBackground(String... locationName) {
			Geocoder geocoder = new Geocoder(getBaseContext());
			List<Address> addresses = null;

			try {
					addresses = geocoder.getFromLocationName(locationName[0], 3);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return addresses;
		}

		@Override
		protected void onPostExecute(List<Address> addresses) {

			if (addresses == null || addresses.size() == 0) {
				Toast.makeText(getBaseContext(), "No Location found",
						Toast.LENGTH_SHORT).show();
			}

			map.clear();

			for (int i = 0; i < addresses.size(); i++) {

				Address address = (Address) addresses.get(i);

				latLng = new LatLng(address.getLatitude(),
						address.getLongitude());

				String addressText = String.format(
						"%s, %s",
						address.getMaxAddressLineIndex() > 0 ? address
								.getAddressLine(0) : "", address
								.getCountryName());

				markerOptions = new MarkerOptions();
				markerOptions.position(latLng);
				markerOptions.title(addressText);

				map.addMarker(markerOptions);

				if (i == 0)

					cameraPos = latLng;
				com.google.android.gms.maps.model.CameraPosition currentPlace = new com.google.android.gms.maps.model.CameraPosition.Builder()
						.target(cameraPos).zoom((float) 15.6).build();

				map.animateCamera(CameraUpdateFactory
						.newCameraPosition(currentPlace));

			}
		}
	}

	public class connectAsyncTask extends AsyncTask<Void, Void, String> {
		private ProgressDialog progressDialog;
		String url;

		connectAsyncTask(String urlPass) {
			url = urlPass;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressDialog = new ProgressDialog(RouteLocator.this);
			progressDialog.setMessage("Fetching route, Please wait...");
			progressDialog.setIndeterminate(true);
			progressDialog.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			JSONParser jParser = new JSONParser();
			String json = jParser.getJSONFromUrl(url);
			return json;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			progressDialog.hide();
			if (result != null) {
				drawPath(result);
			}
		}
	}

	public void drawPath(String result) {

		try {
			// Tranform the string into a json object
			final JSONObject json = new JSONObject(result);
			JSONArray routeArray = json.getJSONArray("routes");
			JSONObject routes = routeArray.getJSONObject(0);
			JSONObject overviewPolylines = routes
					.getJSONObject("overview_polyline");
			String encodedString = overviewPolylines.getString("points");
			List<LatLng> list = decodePoly(encodedString);

			for (int z = 0; z < list.size() - 1; z++) {
				LatLng src = list.get(z);
				LatLng dest = list.get(z + 1);
				Polyline line = map.addPolyline(new PolylineOptions()
						.add(new LatLng(src.latitude, src.longitude),
								new LatLng(dest.latitude, dest.longitude))
						.width(6).color(Color.GREEN).geodesic(true));
			}

			Intent intent = getIntent();
			String sourceplace = intent.getStringExtra("Sourcename");
			String destinationplcae = intent.getStringExtra("Destination");

			cameraPos = new LatLng(list.get(0).latitude, list.get(0).longitude);
			map.addMarker(new MarkerOptions().position(
					new LatLng(list.get(0).latitude, list.get(0).longitude))
					.title(sourceplace));

			map.addMarker(new MarkerOptions().position(
					new LatLng(list.get(list.size() - 1).latitude, list
							.get(list.size() - 1).longitude)).title(
					destinationplcae));

			com.google.android.gms.maps.model.CameraPosition currentPlace = new com.google.android.gms.maps.model.CameraPosition.Builder()
					.target(list.get(0)).zoom((float) 9.5).build();

			map.animateCamera(CameraUpdateFactory
					.newCameraPosition(currentPlace));

		} catch (JSONException e) {

		}
	}

	private List<LatLng> decodePoly(String encoded) {

		List<LatLng> poly = new ArrayList<LatLng>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;

		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;

			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			LatLng p = new LatLng((((double) lat / 1E5)),
					(((double) lng / 1E5)));
			poly.add(p);
		}

		return poly;
	}

	
	
	
}
