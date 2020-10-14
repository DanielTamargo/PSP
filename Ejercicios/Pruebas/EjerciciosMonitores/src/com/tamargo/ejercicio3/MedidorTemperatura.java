package com.tamargo.ejercicio3;

public class MedidorTemperatura extends Thread {

    private Museo museo;
    private int mediciones = 0;

    public MedidorTemperatura(Museo museo) {
        this.museo = museo;
    }

    @Override
    public void run() {
        while (mediciones < 7) {
            museo.medirTemperatura();
            mediciones++;
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
