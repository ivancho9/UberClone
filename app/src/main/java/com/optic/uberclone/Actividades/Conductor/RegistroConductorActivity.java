package com.optic.uberclone.Actividades.Conductor;

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
import com.optic.uberclone.Actividades.Cliente.RegistroActivity;
import com.optic.uberclone.Includes.MyToolbar;
import com.optic.uberclone.Modelos.Cliente;
import com.optic.uberclone.Modelos.Conductor;
import com.optic.uberclone.Providers.AuthProvider;
import com.optic.uberclone.Providers.ClienteProvider;
import com.optic.uberclone.Providers.ConductorProvider;
import com.optic.uberclone.R;

import dmax.dialog.SpotsDialog;

public class RegistroConductorActivity extends AppCompatActivity {



    AuthProvider authProvider;
    ConductorProvider conductorProvider;

    Button botonregistro;
    TextInputEditText textinputcorreo;
    TextInputEditText textinputcontrasenna;
    TextInputEditText textinputnombre;
    TextInputEditText textinputmarca;
    TextInputEditText textinputplaca;

    AlertDialog mdialogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_conductor);
        MyToolbar.show(this,"Registro de conductor",true);
        authProvider = new AuthProvider();
        conductorProvider = new ConductorProvider();
        botonregistro =  findViewById(R.id.btn_registrar);
        textinputcorreo =  findViewById(R.id.textInputCorreo);
        textinputcontrasenna = findViewById(R.id.textInputPassword);
        textinputnombre = findViewById(R.id.textInputNombre);
        textinputmarca = findViewById(R.id.textInputMarca);
        textinputplaca = findViewById(R.id.textInputPlaca);
        mdialogo = new SpotsDialog.Builder().setContext(RegistroConductorActivity.this).setMessage("Espere un momento").build();



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
        final String marca = textinputmarca.getText().toString();
        final String placa = textinputplaca.getText().toString();

        if(!nombre.isEmpty() && !correo.isEmpty() && !contrasenna.isEmpty() && !marca.isEmpty() && !placa.isEmpty())
        {
            if(contrasenna.length() >= 6)
            {
                mdialogo.show();
                registro(nombre,correo,contrasenna,marca,placa);
            }else
            {
                Toast.makeText(RegistroConductorActivity.this, "La contraseña debe ser mayor a 5 digitos",Toast.LENGTH_SHORT).show();
            }

        }
        else
        {
            Toast.makeText(RegistroConductorActivity.this, "Ingrese todos los campos",Toast.LENGTH_SHORT).show();
        }

    }

    void registro(final String nombre, final  String correo, final String contrasenna, final String marca, final String placa)
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
                    Conductor conductor = new Conductor(id,nombre,correo,marca,placa);
                    //grabarUsuario(id,nombre,correo);
                    crear(conductor);
                }else
                {
                    Toast.makeText(RegistroConductorActivity.this, "No se pudo registrar el usuario",Toast.LENGTH_SHORT).show();
                }

            }

        });

    }

    void crear(Conductor conductor)
    {
        conductorProvider.crearConductor(conductor).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                  //  Toast.makeText(RegistroConductorActivity.this, "Registro realizado exitosamente",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegistroConductorActivity.this,MapConductorActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(RegistroConductorActivity.this, "No se pudo crear el conductor",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
