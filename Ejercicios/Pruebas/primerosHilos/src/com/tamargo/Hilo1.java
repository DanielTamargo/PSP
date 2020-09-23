package com.tamargo;

public class Hilo1 extends Thread {

    int num;

    public Hilo1(int num) {
        this.num = num;
    }

    @Override
    public void run() {
        System.out.println("Hilo " + num + ": Hola!");
        super.run();
    }
}
