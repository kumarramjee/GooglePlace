package com.mobimedia.place;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

public class RouteBetweenTwoLocation
{
	GoogleMap map;
Context context;
	
	public RouteBetweenTwoLocation(Context context)
	{
		this.context=context;
	}
	
	public LatLng getLocationFromAddress(String strAddress) {

	    Geocoder coder = new Geocoder(context);
	    List<Address> address;
	    LatLng p1 = null;

	    try {
	        address = coder.getFromLocationName(strAddress, 5);
	        if (address == null) {
	            return null;
	        }
	        Address location = address.get(0);
	        location.getLatitude();
	        location.getLongitude();

	        p1 = new LatLng(location.getLatitude(), location.getLongitude() );

	    } catch (Exception ex) {

	        ex.printStackTrace();
	    }

	    return p1;
	}
	
	
	public String makeURL (double sourcelat, double sourcelog, double destlat, double destlog ){
        StringBuilder urlString = new StringBuilder();
        urlString.append("http://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString
                .append(Double.toString( sourcelog));
        urlString.append("&destination=");// to
        urlString
                .append(Double.toString( destlat));
        urlString.append(",");
        urlString.append(Double.toString( destlog));
        urlString.append("&sensor=false&mode=driving&alternatives=true");
        return urlString.toString();
 }
	
	
	
	
	
	
	public void drawPath(String  result) {

	    try {
	            //Tranform the string into a json object
	           final JSONObject json = new JSONObject(result);
	           JSONArray routeArray = json.getJSONArray("routes");
	           JSONObject routes = routeArray.getJSONObject(0);
	           JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
	           String encodedString = overviewPolylines.getString("points");
	           List<LatLng> list = decodePoly(encodedString);

	           for(int z = 0; z<list.size()-1;z++){
	                LatLng src= list.get(z);
	                LatLng dest= list.get(z+1);
	                Polyline line = map.addPolyline(new PolylineOptions()
	                .add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude,   dest.longitude))
	                .width(2)
	                .color(Color.BLUE).geodesic(true));
	            }

	    } 
	    catch (JSONException e) {

	    }
	}

	public  List<LatLng> decodePoly(String encoded) {

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

	        LatLng p = new LatLng( (((double) lat / 1E5)),
	                 (((double) lng / 1E5) ));
	        poly.add(p);
	    }

	    return poly;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//This function will make the url that we will send to get Direction API response. 
	
	
	/*
	
	public class JSONParser {

	    InputStream is = null;
	     JSONObject jObj = null;
	    String json = "";
	    // constructor
	    public JSONParser() {
	    }
	    public String getJSONFromUrl(String url) {

	        // Making HTTP request
	        try {
	            // defaultHttpClient
	            DefaultHttpClient httpClient = new DefaultHttpClient();
	            HttpPost httpPost = new HttpPost(url);

	            HttpResponse httpResponse = httpClient.execute(httpPost);
	            HttpEntity httpEntity = httpResponse.getEntity();
	            is = httpEntity.getContent();           

	        } catch (UnsupportedEncodingException e) {
	            e.printStackTrace();
	        } catch (ClientProtocolException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        try {
	            BufferedReader reader = new BufferedReader(new InputStreamReader(
	                    is, "iso-8859-1"), 8);
	            StringBuilder sb = new StringBuilder();
	            Strinprivate List<LatLng> decodePoly(String encoded) {

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

	                    LatLng p = new LatLng( (((double) lat / 1E5)),
	                             (((double) lng / 1E5) ));
	                    poly.add(p);
	                }

	                return poly;
	            }g line = null;
	            while ((line = reader.readLine()) != null) {
	                sb.append(line + "\n");
	            }

	            json = sb.toString();
	            is.close();
	        } catch (Exception e) {
	            Log.e("Buffer Error", "Error converting result " + e.toString());
	        }
	        return json;

	    }
	}
	
	
	
	
	
	public void drawPath(String  result) {

	    try {
	            //Tranform the string into a json object
	           final JSONObject json = new JSONObject(result);
	           JSONArray routeArray = json.getJSONArray("routes");
	           JSONObject routes = routeArray.getJSONObject(0);
	           JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
	           String encodedString = overviewPolylines.getString("points");
	           List<LatLng> list = decodePoly(encodedString);

	           for(int z = 0; z<list.size()-1;z++){
	                LatLng src= list.get(z);
	                LatLng dest= list.get(z+1);
	                Polyline line = map.addPolyline(new PolylineOptions()
	                .add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude,   dest.longitude))
	                .width(2)
	                .color(Color.BLUE).geodesic(true));
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

	        	        LatLng p = new LatLng( (((double) lat / 1E5)),
	        	                 (((double) lng / 1E5) ));
	        	        poly.add(p);
	        	    }

	        	    return poly;
	        	}
	    } 
	    catch (JSONException e) {

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
	        result = 0;private List<LatLng> decodePoly(String encoded) {

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

	                LatLng p = new LatLng( (((double) lat / 1E5)),
	                         (((double) lng / 1E5) ));
	                poly.add(p);
	            }

	            return poly;
	        }
	        do {
	            b = encoded.charAt(index++) - 63;
	            result |= (b & 0x1f) << shift;
	            shift += 5;
	        } while (b >= 0x20);
	        int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	        lng += dlng;

	        LatLng p = new LatLng( (((double) lat / 1E5)),
	                 (((double) lng / 1E5) ));
	        poly.add(p);
	    }

	    return poly;
	}*/
	
}
