package com.agustin.lab07_08c2016;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by martdominguez on 10/11/2016.
 */

public class Reclamo implements Serializable{
    private Double latitud;
    private Double longitud;
    private String titulo;
    private String telefono;
    private String email;
    private String imagenPath;

    public Reclamo(){

    }

    public Reclamo(Double lat,Double lng, String titulo, String telefono, String email) {
        this.latitud=lat;
        this.longitud=lng;
        this.titulo = titulo;
        this.telefono = telefono;
        this.email = email;
    }

    public LatLng coordenadaUbicacion() {
        return new LatLng(this.latitud,this.longitud);
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImagenPath() {
        return imagenPath;
    }

    public void setImagenPath(String imagenPath) {
        this.imagenPath = imagenPath;
    }

}
