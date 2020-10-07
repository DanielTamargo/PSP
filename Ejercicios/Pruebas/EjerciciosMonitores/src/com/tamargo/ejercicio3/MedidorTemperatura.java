package com.tamargo.ejercicio3;

public class MedidorTemperatura extends Thread {

    private Museo museo;

    public MedidorTemperatura(Museo museo) {
        this.museo = museo;
    }

    @Override
    public void run() {
        while (true) {
            museo.medirTemperatura();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
