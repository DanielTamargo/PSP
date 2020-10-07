package com.tamargo.ejercicio3;

import java.util.concurrent.Semaphore;

public class FabricanteCuerpos extends Thread {

    private Semaphore s;

    public FabricanteCuerpos(Semaphore s) {
        this.s = s;
    }

    public FabricanteCuerpos() {

    }



}
