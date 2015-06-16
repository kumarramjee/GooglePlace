package com.mobimedia.place.helper;

import java.io.Serializable;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;


public class Country implements Serializable {

	public int id;
	public String country_Name;
	public String boundry_point;

	public List<LatLng> boundryLatlng;;
	
}
