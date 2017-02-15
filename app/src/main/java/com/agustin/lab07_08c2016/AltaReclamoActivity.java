package com.agustin.lab07_08c2016;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;

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
        this.ubicacion = (LatLng) extras.get("coordenadas");
        this.setContentView(R.layout.activity_alta_reclamo);

        btnAgregar = (Button) findViewById(R.id.btnReclamar);
        btnCancelar = (Button) findViewById(R.id.btnCancelar);
        txtDescripcion = (EditText) findViewById(R.id.reclamoTexto);
        txtTelefono= (EditText) findViewById(R.id.reclamoTelefono);
        txtMail= (EditText) findViewById(R.id.reclamoMail);

        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Double latitud = ubicacion.latitude;
                Double longitud = ubicacion.longitude;
                String titulo = txtDescripcion.getText().toString();
                String telefono = txtTelefono.getText().toString();
                String email = txtMail.getText().toString();

                Reclamo nuevoReclamo = new Reclamo(latitud, longitud, titulo, telefono ,email);

                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", nuevoReclamo);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Se canceló el reclamo", Toast.LENGTH_LONG).show();

                Intent myIntent = new Intent(AltaReclamoActivity.this, ReclamoActivity.class);
                startActivity(myIntent);
            }
        });
    }


}
