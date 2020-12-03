package com.tamargo.varios;

import javax.swing.*;
import java.util.Date;

public class Contador extends Thread {

    private final JLabel lContador;
    private boolean bucle = true;

    public Contador(JLabel lContador) {
        this.lContador = lContador;
    }

    @Override
    public void run() {
        long inicio = new Date().getTime();

        while (bucle) {
            float segundos = (float)(new Date().getTime() - inicio) / 1000;
            lContador.setText(String.format("%5.2f", segundos));
        }

        lContador.setText("");
    }

    public void parar() {
        bucle = false;
    }

}
