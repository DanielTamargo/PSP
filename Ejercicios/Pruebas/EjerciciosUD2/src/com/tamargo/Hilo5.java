package com.tamargo;

public class Hilo5 extends Thread {

    int tiempo;
    String mensaje;

    public Hilo5(int tiempo, String mensaje) {
        this.tiempo = tiempo;
        this.mensaje = mensaje;
    }

    public Hilo5() {
    }

    @Override
    public void run() {

        try {
            sleep(tiempo * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("" + getName() + ": '" + mensaje + "' he esperado: " + tiempo + " segundos.");

        super.run();
    }
}
