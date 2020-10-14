package com.tamargo.ejercicio4;

import java.util.Random;
import java.util.Scanner;

public class Principal4 {

    public static void main(String[] args) {

        Puente p = new Puente();
        int peso;

        for (int i = 0; i < 20; i++) {
            peso = new Random().nextInt(6) + 1 + new Random().nextInt(751) + 251;
            Coche coche = new Coche(peso, p);
            Thread hiloCoche = new Thread(coche);
            hiloCoche.start();
        }

        // Salimos al apretar ENTER
        Scanner teclado = new Scanner (System.in);
        teclado.nextLine();
        System.exit(0);

    }


}
