package com.tamargo;

public class Hilo2 extends Thread {
    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            System.out.println("Hilo de nombre " + getName() + " contador " + (i + 1));
        }
        super.run();
    }
}
