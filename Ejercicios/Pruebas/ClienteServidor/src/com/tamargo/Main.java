package com.tamargo;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        LanzarServidor servidor = new LanzarServidor(args);
        LanzarCliente cliente = new LanzarCliente(args);

        servidor.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        cliente.start();

    }
}
