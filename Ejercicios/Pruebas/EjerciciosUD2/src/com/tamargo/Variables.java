package com.tamargo;

public class Variables {

    int contador = 0;

    public Variables(int contador) {
        this.contador = contador;
    }

    public Variables() {
    }

    public synchronized void suma() {
        contador++;
        System.out.println("Contador: " + contador);
    }

    public int getContador() {
        return contador;
    }

    public void setContador(int contador) {
        this.contador = contador;
    }
}
