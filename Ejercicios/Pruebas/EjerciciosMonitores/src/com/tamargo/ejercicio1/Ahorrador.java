package com.tamargo.ejercicio1;

public class Ahorrador extends Thread {

    private Cuenta cuenta;

    public Ahorrador(Cuenta cuenta) {
        this.cuenta = cuenta;
    }

    @Override
    public void run() {
        cuenta.sumar();
    }




}
