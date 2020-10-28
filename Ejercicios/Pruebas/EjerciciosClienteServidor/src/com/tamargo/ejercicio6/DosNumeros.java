package com.tamargo.ejercicio6;

import java.io.Serializable;

public class DosNumeros implements Serializable {

    private int n1;
    private int n2;
    private int solucion = 0;

    public DosNumeros(int n1, int n2) {
        this.n1 = n1;
        this.n2 = n2;
    }

    @Override
    public String toString() {
        return "DosNumeros{" +
                "n1=" + n1 +
                ", n2=" + n2 +
                ", solucion=" + solucion + '}';
    }

    public void resolver() {
        solucion = n1 * n2;
    }

    public int getSolucion() {
        return solucion;
    }

    public int getN1() {
        return n1;
    }

    public int getN2() {
        return n2;
    }



}
