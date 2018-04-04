package com.demo_google_map.pulkit.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.demo_google_map.pulkit.R;
import com.demo_google_map.pulkit.nearplaces_path.NearByPlacesActivity;
import com.demo_google_map.pulkit.path.MapsActivity;

public class MainActivity extends AppCompatActivity {

    Intent intent;
    TextView tv_layers, tv_gps_enabled, tv_polyline, tv_get_single_user_location, custom_search_location, text_path,
            text_near_by_places, text_shortest_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_layers = (TextView) findViewById(R.id.layers);
        tv_gps_enabled = (TextView) findViewById(R.id.gps_enabled);
        tv_get_single_user_location = (TextView) findViewById(R.id.tv_get_single_user_location);
        custom_search_location = (TextView) findViewById(R.id.custom_search_location);
        tv_polyline = (TextView) findViewById(R.id.text_polyline);
        text_near_by_places = (TextView) findViewById(R.id.text_near_by_places);
        text_path = (TextView) findViewById(R.id.text_path);

        tv_layers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MainActivity.this, DemoLayers.class);
                startActivity(intent);
            }
        });

        tv_gps_enabled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MainActivity.this, DemoGPSEnabled.class);

                startActivity(intent);
            }
        });

        tv_get_single_user_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MainActivity.this, GetSingleUserLocation.class);

                startActivity(intent);
            }
        });

        custom_search_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MainActivity.this, DemoSearchLocation.class);

                startActivity(intent);
            }
        });

        tv_polyline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MainActivity.this, DemoPolyline.class);

                startActivity(intent);
            }
        });

        text_near_by_places.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MainActivity.this, NearByPlacesActivity.class);
                startActivity(intent);
            }
        });

        text_path.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });

    }
}
