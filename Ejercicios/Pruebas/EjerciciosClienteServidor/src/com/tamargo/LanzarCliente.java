package com.tamargo;

import java.io.IOException;

public class LanzarCliente extends Thread {

    private int ejercicio;

    public LanzarCliente(int ejercicio) {
        this.ejercicio = ejercicio;
    }

    @Override
    public void run() {
        Cliente.lanzarCliente(ejercicio);
    }

}
