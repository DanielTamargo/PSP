package com.tamargo.ejercicio3;

import java.util.Random;

public class Persona extends Thread {

    private Museo museo;
    private boolean jubilado;

    public Persona(Museo museo, boolean jubilado) {
        this.museo = museo;
        this.jubilado = jubilado;
    }

    @Override
    public void run() {

        museo.entrar(jubilado);

        int segundosDentro = new Random().nextInt(5) + 1;

        try {
            Thread.sleep(segundosDentro * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        museo.salir();


    }
}
