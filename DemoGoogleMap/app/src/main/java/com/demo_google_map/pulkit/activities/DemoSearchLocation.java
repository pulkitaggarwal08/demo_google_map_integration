package com.demo_google_map.pulkit.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.demo_google_map.pulkit.R;
import com.demo_google_map.pulkit.utils.PermissionUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class DemoSearchLocation extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
        , LocationListener {

    private GoogleApiClient mgoogleApiClient;
    private GoogleMap mgoogleMap;
    private LocationRequest mlocationRequest;
    private Location mlocation;
    private Marker marker;
    private EditText editText_location;
    private String search_location;
    private Circle circle;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_location);

        editText_location = (EditText) findViewById(R.id.editText_location);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mgoogleMap = googleMap;
        onStartAppPermission();

        if (mgoogleMap != null) {

            mgoogleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {

                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {

                    Geocoder geocoder = new Geocoder(DemoSearchLocation.this);
                    LatLng latLng = marker.getPosition();

                    double latitude = latLng.latitude;
                    double longitude = latLng.longitude;
                    List<Address> addressList = null;
                    try {
                        addressList = geocoder.getFromLocation(latitude, longitude, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address add = addressList.get(0);
                    marker.setTitle(add.getLocality());
                    marker.showInfoWindow();

                }
            });


            mgoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {

                    View view = getLayoutInflater().inflate(R.layout.marker_custom_info_window, null);

                    TextView tv_locality = (TextView) view.findViewById(R.id.tv_locality);
                    TextView tv_latitude = (TextView) view.findViewById(R.id.tv_latitude);
                    TextView tv_longitude = (TextView) view.findViewById(R.id.tv_longitude);
                    TextView tv_snippet = (TextView) view.findViewById(R.id.tv_snippet);

                    LatLng latLng = marker.getPosition();
                    tv_locality.setText(marker.getTitle());
                    tv_latitude.setText("Latitude:" + latLng.latitude);
                    tv_longitude.setText("Longitude:" + latLng.longitude);
                    tv_snippet.setText(marker.getSnippet());

                    return view;
                }
            });
        }

    }

    private void onStartAppPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            mgoogleMap.setMyLocationEnabled(true);
        } else {
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE, Manifest.permission.ACCESS_FINE_LOCATION, false);
            buildGoogleApiClient();
            mgoogleMap.setMyLocationEnabled(false);
        }

    }

    private synchronized void buildGoogleApiClient() {
        mgoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mgoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mlocationRequest = new LocationRequest();
        mlocationRequest.setInterval(1000);
        mlocationRequest.setFastestInterval(1000);
        mlocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        /*Read this below, why we use fused api*/
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mgoogleApiClient, mlocationRequest, this);
        }

    }

    @Override
    public void onLocationChanged(Location location) {

        mlocation = location;

        editText_location.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });

        /*Place Current Location*//*
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);

        *//*Marker Options for marker*//*
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        marker = mgoogleMap.addMarker(markerOptions);

        *//*move map camera*//*
        mgoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mgoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        *//*stop location update*//*
        if (mgoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mgoogleApiClient, this);
        }*/

    }

    private void performSearch() {
        editText_location.clearFocus();
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(editText_location.getWindowToken(), 0);

        search_location = editText_location.getText().toString();

        if (!search_location.equalsIgnoreCase("")) {
            Geocoder geocoder = new Geocoder(this);
            try {

                List<Address> addressList = geocoder.getFromLocationName(search_location, 10);
                Address address = addressList.get(0);

                String locality = address.getLocality();

                Toast.makeText(this, locality, Toast.LENGTH_SHORT).show();

                double latitude = address.getLatitude();
                double longitude = address.getLongitude();

                goTOLocationZoom(latitude, longitude, 9.5f);

                setMarker(locality, latitude, longitude);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    private void setMarker(String locality, double latitude, double longitude) {

        if (marker != null) {
            removeLocation();
        }

        /*Inbuilt Marker Options for marker*/
//        MarkerOptions markerOptions = new MarkerOptions()
//                .title(locality)
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
//                .position(new LatLng(latitude, longitude))
//                .snippet("I am here");
//        marker = mgoogleMap.addMarker(markerOptions);


        /*Custom Marker Icon*/
//        MarkerOptions markerOptions = new MarkerOptions()
//                .title(locality)
//                .draggable(true)
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car))
//                .position(new LatLng(latitude, longitude))
//                .snippet("I am here");
//        marker = mgoogleMap.addMarker(markerOptions);

                /*Custom Marker Icon*/
        MarkerOptions markerOptions = new MarkerOptions()
                .title(locality)
                .draggable(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(new LatLng(latitude, longitude))
                .snippet("I am here");
        marker = mgoogleMap.addMarker(markerOptions);

        circle = drawCircle(new LatLng(latitude, longitude));

    }

    private Circle drawCircle(LatLng latLng) {

        CircleOptions circleOptions = new CircleOptions()
                .center(latLng)
                .radius(600)
                .fillColor(0x70A4C2FD)
                .strokeColor(Color.BLUE)
                .strokeWidth(1);

        return mgoogleMap.addCircle(circleOptions);
    }

    private void removeLocation() {

        marker.remove();
        marker = null;
        circle.remove();
        circle = null;

    }

    private void goTOLocationZoom(double latitude, double longitude, float zoom) {

        LatLng latLng = new LatLng(latitude, longitude);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
//        mgoogleMap.moveCamera(cameraUpdate);
        mgoogleMap.animateCamera(cameraUpdate);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }
        if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
            mgoogleMap.setMyLocationEnabled(true);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (mgoogleApiClient == null) {
                buildGoogleApiClient();
            }
            mgoogleMap.setMyLocationEnabled(true);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}
