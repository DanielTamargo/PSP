package com.tamargo.ejercicio3;

import java.util.Random;

public class Museo {

    private int tumbral = 30;
    private int temperatura = 20;
    private int personas = 0;

    public Museo(int tumbral, int personas) {
        this.tumbral = tumbral;
        this.personas = personas;

    }

    public Museo() {
    }

    public synchronized void medirTemperatura() {
        temperatura = new Random().nextInt(18) + 15;
        System.out.println("\t\tTemperatura actual: " + temperatura);
    }

    public synchronized void salir() {

        while (personas <= 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        personas--;
        System.out.println("\tHa salido una persona. Total: " + personas);
        notifyAll();

    }

    public synchronized void entrar(boolean jubilado) {

        while (personas >= 5 || (personas >= 3 && temperatura >= tumbral)) {
            try {
                System.out.println("Aforo máximo alcanzado, esperando para entrar...");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //if (jubilado) ???

        personas++;
        System.out.println("Ha entrado una persona. Total: " + personas);
        notifyAll();

    }

}
