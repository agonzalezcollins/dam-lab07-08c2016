package com.agustin.lab07_08c2016;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;
import com.agustin.lab07_08c2016.Reclamo;

public class AltaReclamoActivity extends AppCompatActivity {

    private Button btnCancelar;
    private Button btnAgregar;
    private EditText txtDescripcion;
    private EditText txtMail;
    private EditText txtTelefono;
    private LatLng ubicacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        ubicacion = (LatLng) extras.get("coordenadas");
        setContentView(R.layout.activity_alta_reclamo);
        btnAgregar = (Button) findViewById(R.id.btnReclamar);
        btnCancelar = (Button) findViewById(R.id.btnCancelar);
        txtDescripcion = (EditText) findViewById(R.id.reclamoTexto);
        txtTelefono= (EditText) findViewById(R.id.reclamoTelefono);
        txtMail= (EditText) findViewById(R.id.reclamoMail);
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    // COMPLETAR
            }
        });
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    // COMPLETAR
            }
        });
    }


}
