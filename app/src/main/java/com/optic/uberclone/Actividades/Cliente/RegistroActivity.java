package com.optic.uberclone.Actividades.Cliente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.optic.uberclone.Actividades.Conductor.MapConductorActivity;
import com.optic.uberclone.Actividades.Conductor.RegistroConductorActivity;
import com.optic.uberclone.Modelos.Cliente;
import com.optic.uberclone.Includes.MyToolbar;
import com.optic.uberclone.Providers.AuthProvider;
import com.optic.uberclone.Providers.ClienteProvider;
import com.optic.uberclone.R;

import dmax.dialog.SpotsDialog;

public class RegistroActivity extends AppCompatActivity {



  AuthProvider authProvider;
  ClienteProvider clienteProvider;

    Button botonregistro;
    TextInputEditText textinputcorreo;
    TextInputEditText textinputcontrasenna;
    TextInputEditText textinputnombre;
    TextInputEditText textinputCedula;

    AlertDialog mdialogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        MyToolbar.show(this,"Registrar Usuario",true);
        authProvider = new AuthProvider();
        clienteProvider = new ClienteProvider();
        botonregistro =  findViewById(R.id.btn_registrar);
        textinputcorreo =  findViewById(R.id.textInputCorreo);
        textinputcontrasenna = findViewById(R.id.textInputPassword);
        textinputnombre = findViewById(R.id.textInputNombre);
        textinputCedula = findViewById(R.id.textInputCedula);
        mdialogo = new SpotsDialog.Builder().setContext(RegistroActivity.this).setMessage("Espere un momento").build();



        botonregistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrarUsuario();
            }
        });

    }

    private void registrarUsuario() {

        final String nombre = textinputnombre.getText().toString();
        final String correo = textinputcorreo.getText().toString();
        final String contrasenna = textinputcontrasenna.getText().toString();
        final String cedula = textinputCedula.getText().toString();
     if(!nombre.isEmpty() && !correo.isEmpty() && !contrasenna.isEmpty())
     {
         if(contrasenna.length() >= 6)
         {
             mdialogo.show();
             registro(nombre,correo,contrasenna,cedula);
         }else
             {
                 Toast.makeText(RegistroActivity.this, "La contraseña debe ser mayor a 5 digitos",Toast.LENGTH_SHORT).show();
             }

     }
     else
         {
             Toast.makeText(RegistroActivity.this, "Ingrese todos los campos",Toast.LENGTH_SHORT).show();
         }

    }

    void registro(final String nombre,final  String correo,String contrasenna, final String cedula)
    {
        //mAuth.createUserWithEmailAndPassword(correo,contrasenna).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            authProvider.registrar(correo,contrasenna).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                mdialogo.hide();
                if(task.isSuccessful())
                {
                    //String id = mAuth.getCurrentUser().getUid();
                    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    Cliente client = new Cliente(id,nombre,correo,cedula);
                    //grabarUsuario(id,nombre,correo);
                    crear(client);
                }else
                {
                    Toast.makeText(RegistroActivity.this, "No se pudo registrar el usuario",Toast.LENGTH_SHORT).show();
                }

            }

        });

    }

    void crear(Cliente cliente)
    {
    clienteProvider.crearCliente(cliente).addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            if(task.isSuccessful())
            {
               // Toast.makeText(RegistroActivity.this, "Registro realizado exitosamente",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegistroActivity.this, MapClienteActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                startActivity(intent);
            }
            else
                {
                    Toast.makeText(RegistroActivity.this, "No se pudo crear el cliente",Toast.LENGTH_SHORT).show();
                }
        }
    });

    }
/*
    private void grabarUsuario(String id,String usuario, String mail) {
        String seleccionUsuario = mPref.getString("User","");

        if(seleccionUsuario.equals("Driver"))
        {
            Usuario usu = new Usuario();
            usu.setCorreo(mail);
            usu.setNombre(usuario);
            mDatabase.child("Usuarios").child("Conductores").child(id).setValue(usu).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful())
                    {
                        Toast.makeText(RegistroActivity.this, "Usuario conductor registrado exitosamente",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(RegistroActivity.this, "No se pudo registrar el usuario conductor",Toast.LENGTH_SHORT).show();

                    }

                }
            });
        }
         else if(seleccionUsuario.equals("Client"))
            {
                Usuario usu = new Usuario();
                usu.setCorreo(mail);
                usu.setNombre(usuario);
                mDatabase.child("Usuarios").child("Clientes").child(id).setValue(usu).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful())
                        {
                            Toast.makeText(RegistroActivity.this, "Usuario cliente registrado exitosamente",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(RegistroActivity.this, "No se pudo registrar el usuario cliente",Toast.LENGTH_SHORT).show();

                        }

                    }
                });
        }

    }*/
}
