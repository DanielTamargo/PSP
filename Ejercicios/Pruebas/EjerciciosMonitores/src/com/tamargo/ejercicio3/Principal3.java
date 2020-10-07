package com.tamargo.ejercicio3;


import java.util.Random;
import java.util.Scanner;

public class Principal3 {

    public static void main(String[] args) {

        Museo m = new Museo();
        boolean jubilado;
        int r;

        for (int i = 0; i < 15; i++) {
            r = new Random().nextInt(4);
            if (r == 0)
                jubilado = true;
            else
                jubilado = false;
            Persona p = new Persona(m, jubilado);
            p.start();
        }

        Scanner teclado = new Scanner (System.in);
        teclado.nextLine();
        System.exit(0);


    }

}
