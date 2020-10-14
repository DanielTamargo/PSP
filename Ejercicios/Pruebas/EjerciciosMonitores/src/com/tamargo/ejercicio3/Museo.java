package com.tamargo.ejercicio3;

import java.util.Random;

public class Museo {

    private int tumbral = 30;
    private int temperatura = 20;
    private int temperaturaAnterior = 20;
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
        if (temperaturaAnterior < tumbral && temperatura >= tumbral)
            System.out.println("\t\tWARNING: La temperatura ha subido por encima del umbral. Bajando aforo máximo a 5 personas (no se expulsará a los que estén ya dentro).");
        else if (temperaturaAnterior >= tumbral && temperatura < tumbral)
            System.out.println("\t\tINFO: La temperatura se ha regulado y bajado por debajo del umbral. Subiendo aforo máximo a 10 personas.");
        temperaturaAnterior = temperatura;
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

        while (personas >= 10 || (personas >= 5 && temperatura >= tumbral)) {
            try {
                System.out.println("Aforo máximo alcanzado, esperando para entrar...");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        personas++;
        if (jubilado)
            System.out.println("Ha entrado una persona (jubilado). Total: " + personas);
        else
            System.out.println("Ha entrado una persona. Total: " + personas);
        notifyAll();

    }

}
