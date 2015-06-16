package com.mobimedia.place.helper;

import java.util.ArrayList;

import java.util.List;

import com.google.android.gms.maps.model.LatLng;

public class PolygonCreator {
	public List<List<LatLng>> ParseLatLongFromDataBase(String polygoiPoints) {

		List<List<LatLng>> listLatLong = new ArrayList<List<LatLng>>();

		StringBuffer sb = new StringBuffer(polygoiPoints);

		if (polygoiPoints.startsWith("POLYGON")) {
			String polygonPoints = sb.substring("POLYGON((".length(),
					polygoiPoints.length() - 2);

			listLatLong.add(filterLatlong(polygonPoints));

		} else if (polygoiPoints.startsWith("MULTIPOLYGON")) {
			StringBuilder  builder = new StringBuilder(polygoiPoints);
			 builder.delete(0, "MULTIPOLYGON".length() + 3);
				builder.delete(builder.length() - 3, builder.length());
				String cordinates = new String(builder);

				String points[] = cordinates.split(",\\(\\(");
				int index = 0;
				for (String string : points){
					points[index++] = string.replace("))", "");
					listLatLong.add(filterLatlong(points[index -1] ));
				}
				
		}

		return listLatLong;
	}

	private List<LatLng> filterLatlong(String polygonPoints) {
		List<LatLng> mlistitem = new ArrayList<>();
		String latLongpoints[] = polygonPoints.split(",");
		for (int i = 0; i < latLongpoints.length; i++) {
			String singlelatlongvalue[] = latLongpoints[i].split(" ");
			LatLng latilonglist = new LatLng(
					Double.parseDouble(singlelatlongvalue[1]),
					Double.parseDouble(singlelatlongvalue[0]));
			mlistitem.add(latilonglist);
		}
		return mlistitem;
	}
}
