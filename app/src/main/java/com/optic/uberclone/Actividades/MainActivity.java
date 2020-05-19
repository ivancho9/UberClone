package com.optic.uberclone.Actividades;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.optic.uberclone.Actividades.Cliente.MapClienteActivity;
import com.optic.uberclone.Actividades.Conductor.MapConductorActivity;
import com.optic.uberclone.R;

public class MainActivity extends AppCompatActivity {

    Button soycondunctor;
    Button soycliente;

    SharedPreferences mPref;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPref = getApplicationContext().getSharedPreferences("typeUser",MODE_PRIVATE);
        editor = mPref.edit();
        editor.apply();
        soycliente = findViewById(R.id.btn_cliente);
        soycondunctor = findViewById(R.id.btn_conductor);

        soycliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("User","Client");
                editor.apply();
                SeleccionarAut();
            }
        });

        soycondunctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("User","Driver");
                editor.apply();
                SeleccionarAut();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        if(FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            String Usuario = mPref.getString("User","");
            if(Usuario.equals("Client"))
            {
                Intent intent = new Intent(MainActivity.this, MapClienteActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                startActivity(intent);
            }else
            {
                Intent intent = new Intent(MainActivity.this, MapConductorActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                startActivity(intent);
            }
        }

    }

    private void SeleccionarAut() {
        Intent intent = new Intent(MainActivity.this, SelectOptionAuthActivity.class);
        startActivity(intent);
    }
}
