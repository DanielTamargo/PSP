package com.tamargo;

public class Main {

    public static void main(String[] args) {

        int ejercicio = 5;

        LanzarServidor servidor = new LanzarServidor(ejercicio);
        LanzarCliente cliente = new LanzarCliente(ejercicio);

        servidor.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        cliente.start();

        if (ejercicio == 5) {
            LanzarCliente cliente2 = new LanzarCliente(ejercicio);
            LanzarCliente cliente3 = new LanzarCliente(ejercicio);
            cliente2.start();
            cliente3.start();
        }

    }
}
