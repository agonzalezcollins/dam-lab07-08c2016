package com.agustin.lab07_08c2016;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.drive.internal.StringListResponse;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AltaReclamoActivity extends AppCompatActivity {

    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final String TAG = "AltaReclamoActivity";

    private Button btnCancelar;
    private Button btnAgregar;
    private Button btnImagen;
    private EditText txtDescripcion;
    private EditText txtMail;
    private EditText txtTelefono;
    private LatLng ubicacion;
    private ImageView imageViewFoto;
    private File imageCaptureFile;
    private String pathImagen = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        this.ubicacion = (LatLng) extras.get("coordenadas");
        this.setContentView(R.layout.activity_alta_reclamo);

        btnAgregar = (Button) findViewById(R.id.btnReclamar);
        btnCancelar = (Button) findViewById(R.id.btnCancelar);
        btnImagen = (Button) findViewById(R.id.btnImagen);
        txtDescripcion = (EditText) findViewById(R.id.reclamoTexto);
        txtTelefono= (EditText) findViewById(R.id.reclamoTelefono);
        txtMail= (EditText) findViewById(R.id.reclamoMail);
        imageViewFoto = (ImageView) findViewById(R.id.imageViewFoto);


        btnImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Double latitud = ubicacion.latitude;
                Double longitud = ubicacion.longitude;
                String titulo = txtDescripcion.getText().toString();
                String telefono = txtTelefono.getText().toString();
                String email = txtMail.getText().toString();

                Reclamo nuevoReclamo = new Reclamo(latitud, longitud, titulo, telefono ,email);

                if(pathImagen!=null){
                    nuevoReclamo.setImagenPath(pathImagen);
                    Toast.makeText(getApplicationContext(),"Imagen Guardada - Path: "+pathImagen, Toast.LENGTH_SHORT).show();
                }

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

    /**
     *
     */
    private void dispatchTakePictureIntent() {
        askForPermissionCamara();
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            this.imageCaptureFile = null;
            try {
                this.imageCaptureFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(getApplicationContext() , "Error de Camara", Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (this.imageCaptureFile != null) {
                Uri photoUri = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        this.imageCaptureFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {

            if(this.imageCaptureFile.exists()){
                Bitmap imageBitmap = BitmapFactory.decodeFile(this.imageCaptureFile.getAbsolutePath());
                imageViewFoto.setImageBitmap(imageBitmap);
            }
            //Bundle extras = data.getExtras();
            //Bitmap imageBitmap = (Bitmap) extras.get("data");
            //imageViewFoto.setImageBitmap(imageBitmap);

        }
    }

    private void askForPermissionCamara(){
        PackageManager packageManager = this.getPackageManager();
        final boolean deviceHasCameraFlag = packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA);

        if( !deviceHasCameraFlag ){
            Log.e(TAG, "Dispositivo no tiene camara");
            Toast.makeText(getApplicationContext() , "Device has no camera", Toast.LENGTH_SHORT).show();
            this.btnImagen.setEnabled(false); //Deshabilito boton de camara
        }
        else {
            Log.v(TAG, "Dispositivo tiene camara " + deviceHasCameraFlag);
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        pathImagen = image.getAbsolutePath();
        return image;
    }


}
