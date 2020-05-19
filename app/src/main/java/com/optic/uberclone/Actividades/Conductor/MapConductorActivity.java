package com.optic.uberclone.Actividades.Conductor;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.optic.uberclone.Actividades.Cliente.MapClienteActivity;
import com.optic.uberclone.Actividades.MainActivity;
import com.optic.uberclone.Includes.MyToolbar;
import com.optic.uberclone.Providers.AuthProvider;
import com.optic.uberclone.Providers.GeofireProvider;
import com.optic.uberclone.R;

public class MapConductorActivity extends AppCompatActivity implements OnMapReadyCallback {

    private  GoogleMap maps;
    private SupportMapFragment mapFragment;
    private GeofireProvider geofireProvider;
    private  final static int LOCATION_REQUEST_CODE =1;
    private  final static int SETTINGS_REQUEST_CODE =2;

    private LatLng mLatLng;

    private Marker marker;

    AuthProvider authProvider;

    LocationRequest locationRequest;
    FusedLocationProviderClient mFusetLocation;

    private Button buttonConectar;
    private  boolean isconnet = false;


    LocationCallback locationCallback = new LocationCallback()
    {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for(Location location : locationResult.getLocations())
            {
                mLatLng = new LatLng(location.getLatitude(),location.getLongitude());
                if(marker != null)
                {
                    marker.remove();
                }
                marker = maps.addMarker(new MarkerOptions().position(
                        new LatLng(location.getLatitude(),location.getLongitude())
                        ).title("Tu posicion")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_vehiculo2))


                );
                if(getApplicationContext() != null)
                {
                    maps.moveCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                            .target(new LatLng(location.getLatitude(),location.getLongitude()))
                            .zoom(15f)
                            .build()
                    ));
                    
                    actualizarUbicacion();
                    
                }
            }
        }
    };








    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == LOCATION_REQUEST_CODE)
        {
           if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
           {
               if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
               {
                   if(gpsActive()) {
                       mFusetLocation.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                       maps.setMyLocationEnabled(true);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_conductor);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        geofireProvider = new GeofireProvider();
        authProvider = new AuthProvider();
        MyToolbar.show(this,"Conductor",false);
        mFusetLocation = LocationServices.getFusedLocationProviderClient(this);
        buttonConectar = findViewById(R.id.btnconectar);
        buttonConectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isconnet) {
                    disconnet();
                }
                else {
                    startLocation();
                }
            }
        });



    }
    private void actualizarUbicacion() {
        if(authProvider.existeSession() && mLatLng !=null){
            geofireProvider.GrabarLocalizacion(authProvider.retornarId(), mLatLng);
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

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SETTINGS_REQUEST_CODE && gpsActive())
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                mFusetLocation.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());
                maps.setMyLocationEnabled(true);
            }else{
                showAlertDialog();
            }
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
//locationManager.getProvider()

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            isActive = true;
        }
        return  isActive;
    }

    private void disconnet() {

        if(mFusetLocation !=null)
        {
            buttonConectar.setText("Conectarse");
            isconnet =false;
            mFusetLocation.removeLocationUpdates(locationCallback);
            if(authProvider.existeSession()){
                geofireProvider.EliminarLocalizacion(authProvider.retornarId());
            }

        }else
        {
            Toast.makeText(this, "NO SE PUEDE DESCONECtar", Toast.LENGTH_SHORT).show();
        }
    }
    private  void startLocation(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                if(gpsActive()) {
                    buttonConectar.setText("Desconectarse");
                    isconnet =true;
                    mFusetLocation.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    maps.setMyLocationEnabled(true);
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
                maps.setMyLocationEnabled(true);
            }else
            {
                showAlertDialog();
            }
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
                                ActivityCompat.requestPermissions(MapConductorActivity.this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
                            }
                        }).create().show();
            }else{

                ActivityCompat.requestPermissions(MapConductorActivity.this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
            }
        }
    }
    void cierresesion()
    {
        disconnet();
        authProvider.CerrarSesion();
        Intent intent = new Intent(MapConductorActivity.this, MainActivity.class);
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
