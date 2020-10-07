package com.tamargo.ejercicio2;

public class Consumidor extends Thread {

    private BufferNumeros bn;

    public Consumidor(BufferNumeros bn) {
        this.bn = bn;
    }

    @Override
    public void run() {
        while (true)
            bn.consumir(getName());
    }




}
