package com.agustin.lab07_08c2016;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;
import android.Manifest;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class ReclamoActivity extends AppCompatActivity implements OnMapReadyCallback, OnMapLongClickListener, LocationListener {

    private GoogleMap myMap;
    private static final Integer CODIGO_RESULTADO_ALTA_RECLAMO = 999;
    private static final Integer ZOOM_GOOGLEMAPS_INICIAL = 14;
    private static final Integer ZOOM_GOOGLEMAPS_CERCANO = 16;
    private static final Integer ZOOM_GOOGLEMAPS_ALTO = 18;
    private List<Reclamo> reclamos;
    private Marker markerSeleccionado;
    private Location ubicacionActual;

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reclamo);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        reclamos = new ArrayList<>();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        this.askForLocationPermission();
    }

    private void askForLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // Permiso Dinamico
            // Chequear Permisos
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

            if (permissionCheck != PackageManager.PERMISSION_GRANTED) { //No tengo el permiso

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    //Explicar el Permiso
                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(ReclamoActivity.this,R.style.myDialog));
                    builder.setTitle("Permisos de Localizacion!");
                    builder.setMessage("Puedo acceder al permiso de localizacion?");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            ActivityCompat.requestPermissions(ReclamoActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    ReclamoActivity.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                        }
                    });
                    builder.create();
                    builder.show();

                } else { // Si no puedo pedir el permiso
                    ActivityCompat.requestPermissions(ReclamoActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            ReclamoActivity.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                }
            }
            // Tengo el permiso
        }
        // Permiso Estatico no se puede cambiar.
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        switch (requestCode) {
            case ReclamoActivity.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (myMap != null){
                        iniciarMapa();
                    }
                } else {
                    // No tengo el permiso
                    Toast.makeText(this.getApplicationContext(), "Se requieren permisos GPS para funcionar", Toast.LENGTH_LONG).show();
                    finish();
                }
                return;
            }
            // Otros permisos (case)
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.myMap = googleMap;
        this.iniciarMapa();
    }

    @Override
    public void onMapLongClick (LatLng point){
        Intent myIntent = new Intent(ReclamoActivity.this, AltaReclamoActivity.class);
        myIntent.putExtra("coordenadas", point);
        startActivityForResult(myIntent, ReclamoActivity.CODIGO_RESULTADO_ALTA_RECLAMO);
    }


    /**
     * IniciarMapa
     */
    private void iniciarMapa() {
        try {  // Habilitar Funciones
            this.myMap.setMyLocationEnabled(true);
            this.myMap.setOnMapLongClickListener(this);
            this.myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL); // Asigno una vista al mapa
            this.acercarToLocalizacion();

            this.myMap.setOnInfoWindowClickListener(
                new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Log.v("Marker seleccionado",marker.getTitle());
                        markerSeleccionado = marker;
                        mostrarDialogo();
                    }

                }
            );

        }catch (SecurityException exception){
            Toast.makeText(getApplicationContext(), "No posee permisos GPS", Toast.LENGTH_SHORT).show();
            Log.v("SecurityException", exception.getMessage());
        }
    }

    /**
     * Localizacion segun LocationManager
     * Otra Forma: https://developer.android.com/training/location/retrieve-current.html#GetLocation
     */
    private void acercarToLocalizacion () {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        this.askForEnableLocalizacion(locationManager);

        if(this.ubicacionActual != null){
            LatLng ubicacionActualLatLng = new LatLng (this.ubicacionActual.getLatitude(),this.ubicacionActual.getLongitude());
            Log.v("Ubicacion Actual",ubicacionActualLatLng.toString());
            this.myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ubicacionActualLatLng, ReclamoActivity.ZOOM_GOOGLEMAPS_ALTO));
            return;
        }
        else{
            try {
                Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(lastLocation == null){ // GPS Apagado | No existe localizacion
                    Criteria criteria = new Criteria();
                    criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                    String provider = locationManager.getBestProvider(criteria, true);
                    lastLocation = locationManager.getLastKnownLocation(provider);
                }

                if(lastLocation != null){ // Encontre localizacion
                    LatLng ubicacionAproximada = new LatLng (lastLocation.getLatitude(),lastLocation.getLongitude());
                    Log.v("Ubicacion Aproximada",ubicacionAproximada.toString());
                    this.myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ubicacionAproximada, ReclamoActivity.ZOOM_GOOGLEMAPS_INICIAL));

                }

            }catch (SecurityException exception){
                Toast.makeText(getApplicationContext(), "No posee permisos GPS", Toast.LENGTH_SHORT).show();
                Log.v("SecurityException", exception.getMessage());
            }
        }
    }


    /**
     * Preguntar si se puede habilitar localizacion
     */
    private void askForEnableLocalizacion(LocationManager locationManager){
        boolean isGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(!isGPS){ // Si esta inhabilitado el GPS, pruebo habilitarlo con el usuario
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(ReclamoActivity.this, R.style.myDialog));
            builder.setTitle("Habilitar Localización");
            builder.setMessage("¿Permite habilitar localizacion?");
            builder.setPositiveButton(android.R.string.ok,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                            dialog.dismiss();
                        }
                    });
            builder.setNegativeButton(android.R.string.cancel,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.create();
            builder.show();
        }
    }

    /**
     * BuscarCercanos
     *
     * @param kmCerca
     */
    private void buscarCercanos(Integer kmCerca) {
        List<Reclamo> reclamosCercanos = new ArrayList<>();
        Toast.makeText(this, " Mostrar a " + kmCerca + " KMs", Toast.LENGTH_LONG).show();
        LatLng puntoOrigen= (LatLng) this.markerSeleccionado.getPosition();
        float[] distancia = new float[1];
        for(Reclamo reclamo : this.reclamos){
            LatLng puntoDestino= (LatLng) reclamo.coordenadaUbicacion();
            //Metodo Estatico para calcular distancia, sino usar instanciaLocalizacion.distanceTo().
            Location.distanceBetween(puntoOrigen.latitude, puntoOrigen.longitude, puntoDestino.latitude, puntoDestino.longitude, distancia);
            Log.v("distancia","Calculo: "+distancia[0]+" m ,  Pedida: "+kmCerca.floatValue()*1000+" m");
            if(distancia[0] <= kmCerca.floatValue()*1000){
                reclamosCercanos.add(reclamo);
            }
        }

        // TODO: Marcar reclamosCercanos
        PolylineOptions polylineOptions = new PolylineOptions().geodesic(true);

        for(Reclamo reclamo : reclamosCercanos){
            polylineOptions.add(reclamo.coordenadaUbicacion());
        }
        myMap.addPolyline(polylineOptions);
    }

    /**
     * onActivityResult:
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Reclamo nuevoReclamo = (Reclamo) extras.get("result");
            this.reclamos.add(nuevoReclamo);

            this.myMap.addMarker(new MarkerOptions().position(nuevoReclamo.coordenadaUbicacion())
                    .title("Reclamo de "+nuevoReclamo.getEmail())
                    .snippet(nuevoReclamo.getTitulo())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        }
    }

    /**
     * mostrarDialogo:
     */
    private void mostrarDialogo() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        builder.setView(R.layout.alert_distancia_busqueda);
        builder.setPositiveButton("Buscar reclamos", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Dialog myDialog = (Dialog) dialog;
                EditText myEditText = (EditText) myDialog.findViewById(R.id.distanciaReclamo);
                try{
                    Integer respuesta = Integer.parseInt(myEditText.getText().toString());
                    Log.d(":::Reclamo", "Reclamos a distancia...." + respuesta);
                    buscarCercanos(respuesta);
                }catch (NumberFormatException exception){
                    Toast.makeText(ReclamoActivity.this, "Ingresar un numero", Toast.LENGTH_SHORT).show();
                    Log.v("NumberFormatException",exception.getMessage());
                }
            }
        });
        AlertDialog myAlertDialog = builder.create();
        myAlertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Reclamo Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        this.acercarToLocalizacion();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());

    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    @Override
    public void onLocationChanged(Location location) {
        this.ubicacionActual = location;
        this.acercarToLocalizacion();

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        this.askForEnableLocalizacion(locationManager);
    }
}
