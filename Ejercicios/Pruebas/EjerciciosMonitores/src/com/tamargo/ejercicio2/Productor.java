package com.tamargo.ejercicio2;

import java.util.Random;

public class Productor extends Thread {

    private BufferNumeros bn;

    public Productor(BufferNumeros bn) {
        this.bn = bn;
    }

    @Override
    public void run() {
        while (true)
            bn.producir(getName());
    }




}
