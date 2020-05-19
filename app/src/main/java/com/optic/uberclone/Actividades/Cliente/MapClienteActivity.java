package com.optic.uberclone.Actividades.Cliente;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.internal.es;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.DatabaseError;
import com.optic.uberclone.Actividades.Conductor.MapConductorActivity;
import com.optic.uberclone.Actividades.MainActivity;
import com.optic.uberclone.Includes.MyToolbar;
import com.optic.uberclone.Providers.AuthProvider;
import com.optic.uberclone.Providers.GeofireProvider;
import com.optic.uberclone.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapClienteActivity extends AppCompatActivity implements OnMapReadyCallback {


    private  GoogleMap maps;
    private SupportMapFragment mapFragment;
    private  final static int LOCATION_REQUEST_CODE =1;
    private  final static int SETTINGS_REQUEST_CODE =2;

    AuthProvider authProvider;
    private LatLng mLatLng;
    LocationRequest locationRequest;
    FusedLocationProviderClient mFusetLocation;
    GeofireProvider geofireProvider;
    private Marker marker;
    private List<Marker> mConductoresMarkers = new ArrayList<>();

    private boolean ejecutarunavez =true;

    private PlacesClient  placesClient;
    private AutocompleteSupportFragment autocompleteSupportFragment;

    private String mOrigin;
    private LatLng mOriginLatLng;

    private String mDestination;
    private LatLng mDestinationLatLng;

    LocationCallback locationCallback = new LocationCallback()
    {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for(Location location : locationResult.getLocations())
            {
                mLatLng = new LatLng(location.getLatitude(),location.getLongitude());
                if(getApplicationContext() != null)
                {
                    if(marker != null)
                    {
                        marker.remove();
                    }
                    marker = maps.addMarker(new MarkerOptions().position(
                            new LatLng(location.getLatitude(),location.getLongitude())
                            ).title("Tu posicion")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_ubicacion))


                    );

                    maps.moveCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(location.getLatitude(),location.getLongitude()))
                                    .zoom(15f)
                                    .build()
                    ));
                    if(ejecutarunavez)
                    {
                        ejecutarunavez = false;
                        traerConductores();
                    }
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_cliente);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        authProvider = new AuthProvider();
        MyToolbar.show(this,"Cliente",false);
        mFusetLocation = LocationServices.getFusedLocationProviderClient(this);
        geofireProvider = new GeofireProvider();

        if(!Places.isInitialized())
        {
            Places.initialize(getApplicationContext(),getResources().getString(R.string.google_api_key));

            placesClient = Places.createClient(this);
            autocompleteSupportFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.places_autocompleteOrigin);
            autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID,Place.Field.LAT_LNG,Place.Field.NAME));
            autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    mOrigin = place.getName();
                    mOriginLatLng = place.getLatLng();
                    Log.d("PLACE", "Name: " + mOrigin);
                    Log.d("PLACE", "Lat: " + mOriginLatLng.latitude);
                    Log.d("PLACE", "Lng: " + mOriginLatLng.longitude);
                }

                @Override
                public void onError(@NonNull Status status) {

                }
            });
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == LOCATION_REQUEST_CODE)
        {
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                {
                    if(gpsActive()) {
                        mFusetLocation.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }else
                    {
                        showAlertDialog();
                    }
                }
                else{
                    chekLocationPerimions();
                }
            }
            else
            {
                chekLocationPerimions();
            }
        }
    }


    private  void traerConductores()
    {
        geofireProvider.traerConductores(mLatLng).addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                // añadir marcadores de los conductores que se conecten en la aplicacion
                for(Marker marker: mConductoresMarkers)
                {
                    if(marker.getTag() !=null)
                    {
                        if(marker.getTag().equals(key)){
                            return;
                        }
                    }
                }

                LatLng ConcductorLng = new LatLng(location.latitude,location.longitude);
                Marker marker  = maps.addMarker(new MarkerOptions().position(ConcductorLng).title("Conductor disponible").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_vehiculo2)));
                marker.setTag(key);
                mConductoresMarkers.add(marker);
            }

            @Override
            public void onKeyExited(String key) {
                for(Marker marker: mConductoresMarkers)
                {
                    if(marker.getTag() !=null)
                    {
                        if(marker.getTag().equals(key)){
                            marker.remove();
                            mConductoresMarkers.remove(marker);
                            return;
                        }
                    }
                }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                for(Marker marker: mConductoresMarkers)
                {
                    if(marker.getTag() !=null)
                    {
                        if(marker.getTag().equals(key)){
                            marker.setPosition(new LatLng(location.latitude,location.longitude));
                        }
                    }
                }
            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }


    private  void showAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Por favor activa tu ubicacion para continuar").
                setPositiveButton("Configuraciones", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),SETTINGS_REQUEST_CODE);
                    }
                }).create().show();
    }

    private boolean gpsActive(){
        boolean isActive = false;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            isActive = true;
        }
        return  isActive;
    }

    private  void startLocation(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                if(gpsActive()) {
                    mFusetLocation.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                }else
                {
                    showAlertDialog();
                }
            }else
            {
                chekLocationPerimions();
            }
        }else
        {
            if(gpsActive()) {
                mFusetLocation.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            }else
            {
                showAlertDialog();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SETTINGS_REQUEST_CODE && gpsActive())
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                mFusetLocation.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());
            }else{
                showAlertDialog();
            }
    }

    private void chekLocationPerimions()
    {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION))
            {
                new  AlertDialog.Builder(this).setTitle("proporciona los permisos para continuar")
                        .setMessage("Esta aplicacion requiere de los permisos de Ubicacion")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MapClienteActivity.this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
                            }
                        }).create().show();
            }else{

                ActivityCompat.requestPermissions(MapClienteActivity.this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        maps = googleMap;
        maps.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        maps.getUiSettings().setZoomControlsEnabled(true);
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(5);
        startLocation();
    }

    void cierresesion()
    {
        authProvider.CerrarSesion();
        Intent intent = new Intent(MapClienteActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.conductor_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.action_logout)
        {
            cierresesion();
        }
        return super.onOptionsItemSelected(item);
    }
}
