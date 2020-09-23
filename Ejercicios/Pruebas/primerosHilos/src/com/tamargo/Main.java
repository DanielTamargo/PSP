package com.tamargo;


import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) {

        // Vamos a hacer hilos
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        int i = 1;
        new Hilo1(i).start();
        i++;
        new Hilo1(i).start();
        i++;
        new Hilo1(i).start();
        i++;
        new Hilo1(i).start();
        i++;
        new Hilo1(i).start();
        i++;
        new Hilo1(i).start();
        i++;
        new Hilo1(i).start();
        i++;
        new Hilo1(i).start();
        i++;
        new Hilo1(i).start();
        i++;
        new Hilo1(i).start();
        



    }
}
