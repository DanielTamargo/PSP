package com.tamargo.datos;

import java.io.Serializable;

public class Usuario implements Serializable {

    private String nombre;
    private String contrasenya;
    private int tipo; // 0 -> jugador, 1 -> admin
    private int puntuacion;

    /**
     * Constructor Jugador
     */
    public Usuario(String nombre, String contrasenya) {
        this.nombre = nombre;
        this.contrasenya = contrasenya;
        this.tipo = 0;
        this.puntuacion = 0;
    }

    /**
     * Constructor para personalizar el tipo (generalmente para poner tipo Admin)
     */
    public Usuario(String nombre, String contrasenya, int tipo) {
        this.nombre = nombre;
        this.contrasenya = contrasenya;
        this.tipo = tipo;
        this.puntuacion = 0;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "nombre='" + nombre + '\'' +
                ", contrasenya='" + contrasenya + '\'' +
                ", tipo=" + tipo +
                ", puntuacion=" + puntuacion +
                '}';
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContrasenya() {
        return contrasenya;
    }

    public void setContrasenya(String contrasenya) {
        this.contrasenya = contrasenya;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }
}
