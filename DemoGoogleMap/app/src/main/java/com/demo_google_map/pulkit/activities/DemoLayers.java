package com.demo_google_map.pulkit.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import com.demo_google_map.pulkit.R;
import com.demo_google_map.pulkit.utils.PermissionUtils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NONE;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_SATELLITE;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_TERRAIN;

public class DemoLayers extends AppCompatActivity implements
        OnMapReadyCallback, AdapterView.OnItemSelectedListener
        , ActivityCompat.OnRequestPermissionsResultCallback

{
    private GoogleMap mMap;
    private CheckBox mTrafficCheckbox;
    private CheckBox mMyLocationCheckbox;
    private CheckBox mBuildingsCheckbox;
    private CheckBox mIndoorCheckbox;
    private Spinner mSpinner;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mShowPermissionDeniedDialog = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layers);

        mSpinner = (Spinner) findViewById(R.id.layers_spinner);
        mTrafficCheckbox = (CheckBox) findViewById(R.id.traffic);
        mMyLocationCheckbox = (CheckBox) findViewById(R.id.my_location);
        mBuildingsCheckbox = (CheckBox) findViewById(R.id.buildings);
        mIndoorCheckbox = (CheckBox) findViewById(R.id.indoor);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.layers_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(this);


        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        onStartAppPermission();

    }

    public void onStartAppPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // Uncheck the box until the layer has been enabled and request missing permission.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE, Manifest.permission.ACCESS_FINE_LOCATION, false);
            mMap.setMyLocationEnabled(false);
        }
    }

    public void onTrafficToggled(View view) {
        updateTraffic();
    }

    public void onMyLocationToggled(View view) {
        updateMyLocation();
    }

    public void onBuildingsToggled(View view) {
        updateBuildings();
    }

    public void onIndoorToggled(View view) {
        updateIndoor();
    }

    private void updateTraffic() {
        if (!checkReady()) {
            return;
        }
        mMap.setTrafficEnabled(mTrafficCheckbox.isChecked());
    }

    private void updateMyLocation() {
        if (!checkReady()) {
            return;
        }
        checkPermission();
    }

    private void updateBuildings() {
        if (!checkReady()) {
            return;
        }
        mMap.setBuildingsEnabled(mBuildingsCheckbox.isChecked());
    }

    private void updateIndoor() {
        if (!checkReady()) {
            return;
        }
        mMap.setIndoorEnabled(mIndoorCheckbox.isChecked());
    }

    private boolean checkReady() {
        if (mMap == null) {
            Toast.makeText(this, R.string.map_not_ready, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void checkPermission() {
        if (!mMyLocationCheckbox.isChecked()) {
            mMap.setMyLocationEnabled(false);
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // Uncheck the box until the layer has been enabled and request missing permission.
            mMyLocationCheckbox.setChecked(false);
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE, Manifest.permission.ACCESS_FINE_LOCATION, false);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {

        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, results, Manifest.permission.ACCESS_FINE_LOCATION)) {
            mMap.setMyLocationEnabled(true);
            mMyLocationCheckbox.setChecked(true);
        } else {
            mShowPermissionDeniedDialog = true;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        updateMapType();
    }

    private void updateMapType() {
        // No toast because this can also be called by the Android framework in onResume() at which
        // point mMap may not be ready yet.
        if (mMap == null) {
            return;
        }

        String layerName = ((String) mSpinner.getSelectedItem());
        if (layerName.equals(getString(R.string.normal))) {
            mMap.setMapType(MAP_TYPE_NORMAL);
        } else if (layerName.equals(getString(R.string.hybrid))) {
            mMap.setMapType(MAP_TYPE_HYBRID);
        } else if (layerName.equals(getString(R.string.satellite))) {
            mMap.setMapType(MAP_TYPE_SATELLITE);
        } else if (layerName.equals(getString(R.string.terrain))) {
            mMap.setMapType(MAP_TYPE_TERRAIN);
        } else if (layerName.equals(getString(R.string.none_map))) {
            mMap.setMapType(MAP_TYPE_NONE);
        } else {
            Log.i("LDA", "Error setting layer with name " + layerName);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

}
