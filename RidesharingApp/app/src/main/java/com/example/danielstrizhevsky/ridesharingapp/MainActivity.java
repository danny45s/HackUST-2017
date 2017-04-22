package com.example.danielstrizhevsky.ridesharingapp;

import android.Manifest;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    final int REQUEST_LOCATION = 1;

    GoogleApiClient mGoogleApiClient;
    Location mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }


        final Button sendDataButton = (Button) findViewById(R.id.send_data_button);

        final RequestQueue queue = Volley.newRequestQueue(this);

        sendDataButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String url = "http://ec2-204-236-203-31.compute-1.amazonaws.com:3000/search";
                JSONObject data = new JSONObject();
                JSONObject route = new JSONObject();
                JSONObject preferences = new JSONObject();
                if (mGoogleApiClient.isConnected()) {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            != getPackageManager().PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getParent(),
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                REQUEST_LOCATION);
                    } else {
                        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    }
                }

                try {
                    route.put("startLongitude", mLocation.getLongitude());
                    route.put("startLatitude", mLocation.getLatitude());
                    route.put("endLongitude", 0);  // placeholder
                    route.put("endLatitude", 0);  // placeholder
                    preferences.put("maxDistance", 1000);  // placeholder
                    preferences.put("minNumPeople", 2);  //placeholder
                    data.put("userID", "test");
                    data.put("route", route);
                    data.put("preferences", preferences);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                            }
                        });
                // queue.add(stringRequest);

                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.POST, url, data, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                System.out.println(response);
                                Toast.makeText(getApplicationContext(),
                                        "lat:" + mLocation.getLatitude() + ", long:" + mLocation.getLongitude(),
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                            }
                        });
                queue.add(jsObjRequest);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
