package com.tamargo;

import java.io.IOException;

public class LanzarServidor extends Thread {

    private String[] args;

    public LanzarServidor(String[] args) {
        this.args = args;
    }

    @Override
    public void run() {
        try {
            Servidor.main(args);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
