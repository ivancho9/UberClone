package com.optic.uberclone.Actividades;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.optic.uberclone.Actividades.Cliente.MapClienteActivity;
import com.optic.uberclone.Actividades.Cliente.RegistroActivity;
import com.optic.uberclone.Actividades.Conductor.MapConductorActivity;
import com.optic.uberclone.Actividades.Conductor.RegistroConductorActivity;
import com.optic.uberclone.Includes.MyToolbar;
import com.optic.uberclone.R;

import dmax.dialog.SpotsDialog;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText textinpuntmail;
    TextInputEditText textinpuntcontrasenna;
    Button Logueo;


    AlertDialog mdialogo;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        MyToolbar.show(this,"Login",true);
        textinpuntmail = findViewById(R.id.textInputCorreo);
        textinpuntcontrasenna = findViewById(R.id.textInputPassword);
        Logueo = findViewById(R.id.btn_login);
        mPref = getApplicationContext().getSharedPreferences("typeUser",MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mdialogo = new SpotsDialog.Builder().setContext(LoginActivity.this).setMessage("Espere un momento").build();
        Logueo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

    }

    private void login() {
        String correo = textinpuntmail.getText().toString();
        String clave = textinpuntcontrasenna.getText().toString();

        if(!correo.isEmpty() && !clave.isEmpty())
        {
            if(clave.length() >= 6)
            {
                mdialogo.show();
                mAuth.signInWithEmailAndPassword(correo,clave).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            String Usuario = mPref.getString("User","");
                            if(Usuario.equals("Client"))
                            {
                                Intent intent = new Intent(LoginActivity.this, MapClienteActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                                startActivity(intent);
                            }else
                                {
                                    Intent intent = new Intent(LoginActivity.this, MapConductorActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                                    startActivity(intent);
                                }

                        }else
                            {
                                Toast.makeText(LoginActivity.this, "La contraseña o el correo son incorrectos",Toast.LENGTH_SHORT).show();
                            }
                        mdialogo.dismiss();
                    }
                });
            }else
                {
                    Toast.makeText(LoginActivity.this, "La contraseña tiene que tener minimo 6 caracteres",Toast.LENGTH_SHORT).show();
                }

        }else
            {
                Toast.makeText(LoginActivity.this, "La contraseño o el mail estan vacios",Toast.LENGTH_SHORT).show();


            }

    }
}
