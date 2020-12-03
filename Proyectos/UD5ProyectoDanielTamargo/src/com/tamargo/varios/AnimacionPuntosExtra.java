package com.tamargo.varios;

import javax.swing.*;

public class AnimacionPuntosExtra extends Thread {

    private final JLabel puntosExtra;
    private boolean bucle = true;

    public AnimacionPuntosExtra(JLabel puntosExtra) {
        this.puntosExtra = puntosExtra;
    }

    @Override
    public void run() {
        int i = 0;
        puntosExtra.setText("¡Puntos Extra!");
        while (i < 20 && bucle) {
            int tiempo;
            if (i < 6)
                tiempo = 250;
            else if (i < 12) {
                tiempo = 100;
            } else {
                tiempo = 20;
            }
            puntosExtra.setVisible(!puntosExtra.isVisible());
            try {
                Thread.sleep(tiempo);
            } catch (InterruptedException e) {
                bucle = false;
                puntosExtra.setVisible(false);
            }

            i++;
        }
        puntosExtra.setText("");
    }

    public void parar() {
        this.bucle = false;
        interrupt();
    }


}
