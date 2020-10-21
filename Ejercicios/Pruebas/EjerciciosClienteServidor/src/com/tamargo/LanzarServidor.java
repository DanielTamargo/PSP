package com.tamargo;

import java.io.IOException;

public class LanzarServidor extends Thread {

    private int ejercicio;

    public LanzarServidor(int ejercicio) {
        this.ejercicio = ejercicio;
    }

    @Override
    public void run() {
        Servidor.lanzarServidor(ejercicio);
    }
}
