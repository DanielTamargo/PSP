package com.tamargo;

public class Hilo1 extends Thread {

    String texto;

    public Hilo1(String texto) {
        this.texto = texto;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.println("Hilo '" + getName() + "' escribiendo: " + texto);
        }
        super.run();
    }
}
