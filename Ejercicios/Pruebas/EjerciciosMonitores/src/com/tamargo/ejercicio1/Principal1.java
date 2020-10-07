package com.tamargo.ejercicio1;

public class Principal1 {

    public static void main(String[] args) throws InterruptedException {

        Cuenta c = new Cuenta(0);

        for (int i = 0; i < 10; i++) {
            Gastador g = new Gastador(c);
            Ahorrador a = new Ahorrador(c);
            g.start();
            a.start();
        }

        Thread.sleep(2000);

        System.out.println("Saldo final de la cuenta: " + c.getSaldo());

    }


}
