package com.tamargo;

public class Hilo5 extends Thread {

    @Override
    public void run() {

        System.out.println("¡Hola Mundo! desde '" + getName() + "'");

        super.run();
    }
}
