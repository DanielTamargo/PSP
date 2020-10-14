package com.tamargo.ejercicio4;

import java.util.Random;

public class Coche implements Runnable {

    private int peso;
    private Puente puente;

    public Coche(int peso, Puente puente) {
        this.peso = peso;
        this.puente = puente;
    }

    @Override
    public void run() {

        puente.accederVehiculo(peso);

        int segundosDentro = new Random().nextInt(5) + 1;

        try {
            Thread.sleep(segundosDentro * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        puente.salirVehiculo(peso);

    }
}
