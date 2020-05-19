package com.optic.uberclone.Modelos;

public class Conductor {

    String id;
    String nombre;
    String correo;
    String marca;
    String placa;

    public Conductor(String id, String nombre, String correo, String marca, String placa) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.marca = marca;
        this.placa = placa;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }
}
