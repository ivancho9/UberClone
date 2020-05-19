package com.optic.uberclone.Providers;

import android.location.Location;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GeofireProvider {

   private DatabaseReference databaseReference;
   private GeoFire geoFire;

   public GeofireProvider()
   {
       databaseReference = FirebaseDatabase.getInstance().getReference().child("Conductores Activos");
       geoFire  = new GeoFire(databaseReference);
   }

   public void GrabarLocalizacion(String conductor, LatLng posicion)
   {
       geoFire.setLocation(conductor,new GeoLocation(posicion.latitude,posicion.longitude));

   }
    public void EliminarLocalizacion(String conductor)
    {
        geoFire.removeLocation(conductor);

    }

    public GeoQuery traerConductores(LatLng latLng)
    {
        GeoQuery  geoQuery = geoFire.queryAtLocation(new GeoLocation(latLng.latitude,latLng.longitude),5);
        geoQuery.removeAllListeners();
        return  geoQuery;
    }
}
