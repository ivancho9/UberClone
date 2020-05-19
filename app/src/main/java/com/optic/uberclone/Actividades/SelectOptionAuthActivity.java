package com.optic.uberclone.Actividades;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.optic.uberclone.Actividades.Cliente.RegistroActivity;
import com.optic.uberclone.Actividades.Conductor.RegistroConductorActivity;
import com.optic.uberclone.R;


public class SelectOptionAuthActivity extends AppCompatActivity {

    Toolbar mtolbar;
    Button login;
    Button Registro;

    SharedPreferences mPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_option_auth);
        mtolbar = findViewById(R.id.toollbar);
        setSupportActionBar(mtolbar);
        getSupportActionBar().setTitle("Seleccionar Opcion");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mPref = getApplicationContext().getSharedPreferences("typeUser",MODE_PRIVATE);
        login = findViewById(R.id.btn_login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Autenticarse();
            }
        });


        Registro = findViewById(R.id.btn_registro);
        Registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Registrarse();
            }
        });
    }

    public  void Autenticarse() {

        Intent intent = new Intent(SelectOptionAuthActivity.this,LoginActivity.class);
        startActivity(intent);
    }

    public  void Registrarse() {
        String tipoUsuario = mPref.getString("User","");
        if(tipoUsuario.equals("Client")){
        Intent intent = new Intent(SelectOptionAuthActivity.this, RegistroActivity.class);
        startActivity(intent);
    }else
        {
            Intent intent = new Intent(SelectOptionAuthActivity.this, RegistroConductorActivity.class);
            startActivity(intent);
        }
    }
}
