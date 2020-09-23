package com.tamargo;

public class Hilo3 extends Thread {

    int comienzo = 0;

    public Hilo3(int comienzo) {
        this.comienzo = comienzo;
    }

    @Override
    public void run() {

        for (int i = comienzo; i < 100; i++) {
            System.out.println("Hilo: '" + getName() + "': " + i);
            i++;
        }
        super.run();
    }
}
