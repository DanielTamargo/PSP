package com.tamargo;

public class HiloAct1 extends Thread {
    @Override
    public void run() {
        for (int i = 0; i < 15; i++) {
            System.out.println("Mostramos el número: '" + (i + 1) + "' del hilo '" + getName() + "'");
        }
        super.run();
    }
}
