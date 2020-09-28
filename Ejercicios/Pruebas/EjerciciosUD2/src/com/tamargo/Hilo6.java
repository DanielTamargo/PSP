package com.tamargo;

public class Hilo6 extends Thread {

    Integer contador;

    public Hilo6(Integer contador) {
        this.contador = contador;
    }

    @Override
    public void run() {
        System.out.println("Hilo '" + getName() + "': inicio contador = " + contador);
        for (int i = 0; i < 5000; i++) {
            Main.contador++;
            contador++;
        }
        System.out.println("Hilo '" + getName() + "': fin contador = " + contador + "\n");
        super.run();
    }
}
