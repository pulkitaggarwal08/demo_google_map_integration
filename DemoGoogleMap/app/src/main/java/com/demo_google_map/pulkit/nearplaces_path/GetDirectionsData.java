package com.demo_google_map.pulkit.nearplaces_path;

import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.io.IOException;

/**
 * Created by pulkit on 21/8/17.
 */

public class GetDirectionsData extends AsyncTask<Object, String, String> {

    GoogleMap mMap;
    String googleDirectionsData;
    String url;
    String duration, distance;
    LatLng latLng;

    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];
        latLng = (LatLng) objects[2];

        DownloadURL downloadURL = new DownloadURL();
        try {
            googleDirectionsData = downloadURL.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googleDirectionsData;
    }

    //------------------Simple with method-----------------------
/*    @Override
    protected void onPostExecute(String result) {
//        super.onPostExecute(s);

        HashMap<String, String> directionsList = null;
        DataParser parser = new DataParser();
        directionsList = parser.parseDirections(result);

        duration = directionsList.get("duration");
        distance = directionsList.get("distance");

        mMap.clear();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng)
                .draggable(true)
                .title("Duration= " + duration)
                .snippet("Distance= " + distance);

        mMap.addMarker(markerOptions);

    }*/

    //------------------Through API URl-------------------------


    @Override
    protected void onPostExecute(String result) {
//        super.onPostExecute(s);

        String[] directionsList;
        DataParser parser = new DataParser();
        directionsList = parser.parseDirections(result);
        displayDirections(directionsList);

    }

    private void displayDirections(String[] directionsList) {

        int count = directionsList.length;

        for (int i = 0; i < count; i++) {
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.color(Color.RED)
                    .width(10)
                    .addAll(PolyUtil.decode(directionsList[i]));

            mMap.addPolyline(polylineOptions);
        }

    }
}

