package com.tamargo;

public class HiloRunnable implements Runnable {


    public HiloRunnable() {
    }

    @Override
    public void run() {

        System.out.println("Hilo Runnable '" + Thread.currentThread().getName() + "': Hola");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Hilo Runnable '" + Thread.currentThread().getName() + "': Adiós");
    }
}
