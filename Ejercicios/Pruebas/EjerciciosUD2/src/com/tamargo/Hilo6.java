package com.tamargo;

public class Hilo6 extends Thread {

    Integer contador;

    public Hilo6(Integer contador) {
        this.contador = contador;
    }

    @Override
    public void run() {
        for (int i = 0; i < 5000; i++) {
            contador+= 1;
        }
        super.run();
    }
}
