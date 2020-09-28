package com.tamargo;

public class Hilo3 extends Thread {

    int comienzo = 0;

    public Hilo3(int comienzo) {
        this.comienzo = comienzo;
    }

    @Override
    public void run() {

        for (int i = comienzo; i <= 100; i++) {
            System.out.println("Hilo: '" + getName() + "' (prioridad: " + getPriority() + "): " + i);
            if (i == 50) {
                if (comienzo == 0)
                    setPriority(10);
                else
                    setPriority(1);
            }
            i++;
        }
        super.run();
    }
}
