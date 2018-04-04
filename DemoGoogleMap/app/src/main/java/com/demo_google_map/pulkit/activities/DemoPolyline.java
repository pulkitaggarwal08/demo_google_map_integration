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
import android.util.Log;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class DemoPolyline extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
        , LocationListener {

    private GoogleApiClient mgoogleApiClient;
    private GoogleMap mgoogleMap;
    private LocationRequest mlocationRequest;
    private Location mlocation;
    private EditText editText_location1, editText_location2;
    private String search_location1, search_location2;

    private Marker marker1;
    private Marker marker2;
    private Polyline drawLine;
    double self_latitude;
    double self_longitude;


    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_polyline);

        editText_location1 = (EditText) findViewById(R.id.editText_location1);
        editText_location2 = (EditText) findViewById(R.id.editText_location2);

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

                    Geocoder geocoder = new Geocoder(DemoPolyline.this);
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

        self_latitude = location.getLatitude();
        self_longitude = location.getLongitude();

        editText_location1.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });

        editText_location2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });

    }

    private void performSearch() {
        editText_location1.clearFocus();
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(editText_location1.getWindowToken(), 0);

        search_location1 = editText_location1.getText().toString();
        search_location2 = editText_location2.getText().toString();

        if (search_location1.isEmpty()) {

            LatLng latLng = new LatLng(self_latitude, self_longitude);

//            goTOLocationZoom(first_latitude, first_longitude, 9.5f);
//            setfirstMarker("", self_latitude, self_longitude);
//            navigateLocation(self_latitude, self_longitude);

            if (search_location1.isEmpty()) {

                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                try {

                    List<Address> addressList = geocoder.getFromLocation(self_latitude, self_longitude, 1);
                    Address address = addressList.get(0);

                    String locality = addressList.get(0).getLocality();
                    String subLocality = addressList.get(0).getSubLocality();
                    String adminArea = addressList.get(0).getAdminArea();
                    String countryName = addressList.get(0).getCountryName();

                    Toast.makeText(this, locality, Toast.LENGTH_SHORT).show();

                    double second_latitude = address.getLatitude();
                    double second_longitude = address.getLongitude();

                    Log.i("second_latitude", String.valueOf(second_latitude));
                    Log.i("second_longitude", String.valueOf(second_longitude));

//                    goTOLocationZoom(first_latitude, first_longitude, 9.5f);
                    setfirstMarker(locality + ", " + subLocality + ", " + adminArea, self_latitude, self_longitude);
                    navigateLocation(locality + ", " + subLocality + ", " + adminArea, self_latitude, self_longitude);


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } else if (!search_location1.equalsIgnoreCase("")) {
            Geocoder geocoder = new Geocoder(this);
            try {

                List<Address> addressList = geocoder.getFromLocationName(search_location1, 10);
                Address address = addressList.get(0);

                String locality = address.getLocality();
                String subLocality = address.getSubLocality();
                String adminArea = address.getAdminArea();
                String countryName = address.getCountryName();

                Toast.makeText(this, locality, Toast.LENGTH_SHORT).show();

                double first_latitude = address.getLatitude();
                double first_longitude = address.getLongitude();

                Log.i("first_latitude", String.valueOf(first_latitude));
                Log.i("first_longitude", String.valueOf(first_longitude));

//                goTOLocationZoom(first_latitude, first_longitude, 9.5f);
                setfirstMarker(locality, first_latitude, first_longitude);
                navigateLocation(locality + ", " + subLocality + ", " + adminArea, first_latitude, first_longitude);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        if (!search_location2.equalsIgnoreCase("")) {
            Geocoder geocoder = new Geocoder(this);
            try {

                List<Address> addressList = geocoder.getFromLocationName(search_location2, 10);
                Address address = addressList.get(0);

                String locality = address.getLocality();
                Toast.makeText(this, locality, Toast.LENGTH_SHORT).show();

                double second_latitude = address.getLatitude();
                double second_longitude = address.getLongitude();

                Log.i("second_latitude", String.valueOf(second_latitude));
                Log.i("second_longitude", String.valueOf(second_longitude));

                goTOLocationZoom(second_latitude, second_longitude, 9.5f);
                setSecondMarker(locality, second_latitude, second_longitude);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void navigateLocation(String locality, double latitude, double longitude) {

        LatLng latLng = new LatLng(latitude, longitude);

        mgoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

        // Flat markers will rotate when the map is rotated,
        // and change perspective when the map is tilted.
        mgoogleMap.addMarker(new MarkerOptions()
                .title(locality)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car))
                .position(latLng)
                .flat(true)
                .rotation(245));

        CameraPosition cameraPosition = CameraPosition.builder()
                .target(latLng)
                .zoom(15)
                .bearing(90)
                .build();

        // Animate the change in camera view over 2 seconds
        mgoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);

    }

    private void setfirstMarker(String locality, double latitude, double longitude) {

        /*Inbuilt Marker Options for marker*/
        MarkerOptions markerOptions = new MarkerOptions()
                .title(locality)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .position(new LatLng(latitude, longitude))
                .snippet("I am here");

        if (marker1 == null) {
            marker1 = mgoogleMap.addMarker(markerOptions);
        } else if (marker2 == null) {
            marker2 = mgoogleMap.addMarker(markerOptions);
            drawPolyLine();
        } else {
            removeLocation();
            marker1 = mgoogleMap.addMarker(markerOptions);
        }
    }

    private void setSecondMarker(String locality, double latitude, double longitude) {

        /*Inbuilt Marker Options for marker*/
        MarkerOptions markerOptions = new MarkerOptions()
                .title(locality)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(new LatLng(latitude, longitude))
                .snippet("I am here");

        if (marker1 == null) {
            marker1 = mgoogleMap.addMarker(markerOptions);
        } else if (marker2 == null) {
            marker2 = mgoogleMap.addMarker(markerOptions);
            drawPolyLine();
        } else {
            removeLocation();
            marker1 = mgoogleMap.addMarker(markerOptions);
        }

    }

    private void drawPolyLine() {

        PolylineOptions polylineOptions = new PolylineOptions()
                .add(marker1.getPosition())
                .add(marker2.getPosition())
                .color(Color.BLUE)
                .width(10);

        drawLine = mgoogleMap.addPolyline(polylineOptions);

    }

    private void removeLocation() {

        marker1.remove();
        marker1 = null;
        marker2.remove();
        marker2 = null;
        drawLine.remove();
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
