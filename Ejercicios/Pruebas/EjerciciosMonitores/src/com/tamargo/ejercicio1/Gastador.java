package com.tamargo.ejercicio1;

public class Gastador extends Thread {

    private Cuenta cuenta;

    public Gastador(Cuenta cuenta) {
        this.cuenta = cuenta;
    }

    @Override
    public void run() {

        cuenta.restar();

    }



}
