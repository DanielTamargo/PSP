package com.tamargo.ejercicio2;

import java.util.ArrayList;
import java.util.Random;

public class BufferNumeros {

    private ArrayList<Integer> randoms = new ArrayList<>();

    public BufferNumeros() {
    }

    public BufferNumeros(ArrayList<Integer> randoms) {
        this.randoms = randoms;
    }

    public void addRandom(Integer n) {
        randoms.add(n);
    }

    public ArrayList<Integer> getRandoms() {
        return randoms;
    }

    public void setRandoms(ArrayList<Integer> randoms) {
        this.randoms = randoms;
    }

    public synchronized void consumir(String nombreHilo) {
        while (randoms.size() <= 0) {
            try {
                System.out.println(nombreHilo + ": Buffer vacío, esperando...");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Integer last = randoms.get(randoms.size() - 1);
        System.out.println(nombreHilo + ": " + last);
        randoms.remove(last);
        notifyAll();

    }

    public synchronized void producir(String nombreHilo) {
        while (randoms.size() >= 10) {
            try {
                System.out.println("\t" + nombreHilo + ": Buffer lleno, esperando...");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Integer random = new Random().nextInt(89) + 10;
        System.out.println("\t" + nombreHilo + ": Número " + random + " añadido");
        randoms.add(random);
        notifyAll();

    }


}
