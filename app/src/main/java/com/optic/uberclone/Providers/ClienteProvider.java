package com.optic.uberclone.Providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.optic.uberclone.Modelos.Cliente;

import java.util.HashMap;
import java.util.Map;

public class ClienteProvider {

    DatabaseReference mDatabase;

    public ClienteProvider() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Usuarios").child("Clientes");
    }

    public Task<Void> crearCliente(Cliente cliente)
    {
        Map<String, Object> map  = new HashMap<>();
        map.put("nombre",cliente.getNombre());
        map.put("correo",cliente.getCorreo());
        map.put("id",cliente.getId());
        map.put("Cedula",cliente.getCedula());
    return  mDatabase.child(cliente.getId()).setValue(map);
        //return  mDatabase.child(cliente.getId()).setValue(map);
    }
}
