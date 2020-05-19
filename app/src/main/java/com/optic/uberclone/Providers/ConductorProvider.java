package com.optic.uberclone.Providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.optic.uberclone.Modelos.Cliente;
import com.optic.uberclone.Modelos.Conductor;

import java.util.HashMap;
import java.util.Map;

public class ConductorProvider {

    DatabaseReference mDatabase;

    public ConductorProvider() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Usuarios").child("Conductores");
    }

    public Task<Void> crearConductor(Conductor conductor)
    {

        Map<String, Object> map  = new HashMap<>();
        map.put("nombre",conductor.getNombre());
        map.put("correo",conductor.getCorreo());
        map.put("marca",conductor.getMarca());
        map.put("placa",conductor.getPlaca());
        return  mDatabase.child(conductor.getId()).setValue(map);

    }
}
