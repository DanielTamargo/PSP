package com.tamargo.ejercicio2;

public class Principal2 {

    public static void main(String[] args) {

        BufferNumeros bn = new BufferNumeros();

        for (int i = 0; i < 4; i++) {
            Consumidor c = new Consumidor(bn);
            c.start();
        }

        Productor p = new Productor(bn);
        p.start();



    }

}
