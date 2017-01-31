package edu.siam.siamumap;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.HashMap;

import siamumap.dto.Building;

/**
 * Created by Mob on 27-Sep-15.
 */
public class MapPage extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.InfoWindowAdapter, ConnectionCallbacks, OnConnectionFailedListener, LocationListener {
    AppMethod appMethod = new AppMethod();

    private final String webserviceURL = appMethod.setWebserviceURL();
    private static final String namespace = "http://siamUMapService.org/";
    private static String methodName = "findAllBuilding";
    private static String soapAction = "http://siamUMapService.org/findAllBuilding";

    private ArrayList<Building> buildings = new ArrayList<Building>();

    GoogleMap googleMap;
    SupportMapFragment mapFragment;
    LocationRequest locationRequest;
    GoogleApiClient googleApiClient;
    Location userLocation;
    Marker buildingMarker, userMarker;

    ImageView buildingImage;
    TextView buildingNo, buildingDescription, buildingFloor;

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_page);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        appMethod.checkLocationProvider(this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.siamU_Map);
        mapFragment.getMapAsync(this);

        //TODO check asynctask of getMapAsync and getBuildingData
//        new getAllBuildingData().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        String markerTitle = marker.getTitle();
        LayoutInflater inflater = getLayoutInflater();
        View infoWindow = inflater.inflate(R.layout.map_custom_info_window, null);
        LinearLayout layout = (LinearLayout) infoWindow.findViewById(R.id.customLayout);
        layout.setBackgroundResource(R.drawable.custom_info_window);
        if (markerTitle.equals("คุณอยู่ที่นี่")) {
            return null;
        } else {
            for (int i = 0; i < buildings.size(); i++) {
                buildingImage = (ImageView) infoWindow.findViewById(R.id.buildingImage);
                buildingNo = (TextView) infoWindow.findViewById(R.id.buildingNo);
                buildingDescription = (TextView) infoWindow.findViewById(R.id.buildingDescription);
                buildingFloor = (TextView) infoWindow.findViewById(R.id.buildingFloor);
                if (markerTitle.matches(buildings.get(i).getBuildingDescription())) {
                    if (buildings.get(i).getBuildingPicture() != null) {
                        byte[] decodedString = Base64.decode(buildings.get(i).getBuildingPicture(), Base64.DEFAULT);
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inPurgeable = true;
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, options);
                        buildingImage.setImageBitmap(bitmap);
                    } else {
                        buildingImage.setImageResource(R.drawable.no_picture);
                    }
                    buildingNo.setText("อาคาร " + String.valueOf(buildings.get(i).getBuildingNo()));
                    buildingDescription.setText(buildings.get(i).getBuildingDescription());
                    buildingFloor.setText("จำนวนชั้น " + String.valueOf(buildings.get(i).getBuildingFloor()) + " ชั้น");
                    break;
                }
            }
            return infoWindow;
        }
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        buildGoogleApiClient();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                googleMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            googleMap.setMyLocationEnabled(true);
        }

        LatLng SiamU = new LatLng(13.7190402, 100.4531406);
        CameraPosition target = CameraPosition.builder().target(SiamU).zoom(18).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(target));


        //TODO check asynctask of getMapAsync and getBuildingData
//        new getAllBuildingData().execute();
//        googleMap.setInfoWindowAdapter(this);
    }

    @Override
    public void onConnected(Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(1000);//1000 = 1 sec
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
        new getAllBuildingData().execute();
        googleMap.setInfoWindowAdapter(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        userLocation = location;
        if (userMarker != null) {
            userMarker.remove();
        }
        //create user marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("คุณอยู่ที่นี่");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        userMarker = googleMap.addMarker(markerOptions);
        //go to user location
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(18));
        //stop location update
//        if (googleApiClient != null) {
//            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
//        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    private class getAllBuildingData extends AsyncTask<Void, Integer, Void> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = appMethod.createProgressDialog(MapPage.this);
            progressDialog.setMessage("กำลังเตรียมข้อมูลอาคาร... ");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            SoapObject request = new SoapObject(namespace, methodName);
            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(request);

            HttpTransportSE aht = new HttpTransportSE(webserviceURL);
            aht.debug = true;
            try {
                aht.call(soapAction, soapEnvelope);
                SoapObject response = (SoapObject) soapEnvelope.getResponse();
                int count = response.getPropertyCount();
                for (int i = 0; i < count; i++) {
                    HashMap<String, String> mapping = new HashMap<String, String>();
                    final Building building = new Building();
                    SoapObject responseChild = (SoapObject) response.getProperty(i);
                    building.setBuildingNo(responseChild.getPropertyAsString("buildingNo"));
                    building.setBuildingDescription(responseChild.getPropertyAsString("description"));
                    if (responseChild.hasProperty("picture")) {
                        building.setBuildingPicture(responseChild.getPropertyAsString("picture"));
                    }
                    building.setBuildingFloor(responseChild.getPropertyAsString("floor"));
                    building.setLat(Double.parseDouble(responseChild.getPropertyAsString("latitude")));
                    building.setLng(Double.parseDouble(responseChild.getPropertyAsString("longitude")));
                    buildings.add(building);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void something) {
            progressDialog.dismiss();
            if (googleMap != null) {
                for (int i = 0; i < buildings.size(); i++) {
                    LatLng position = new LatLng(buildings.get(i).getLat(), buildings.get(i).getLng());
                    buildingMarker = googleMap.addMarker(
                            new MarkerOptions()
                                    .position(position)
                                    .title(buildings.get(i).getBuildingDescription())
                    );
                }
            }
        }
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (googleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        googleMap.setMyLocationEnabled(true);
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
}