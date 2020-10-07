package com.tamargo.ejercicio1;

public class Cuenta {

    private int saldo;

    public Cuenta(int saldo) {
        this.saldo = saldo;
    }

    public int getSaldo() {
        return saldo;
    }

    public synchronized int comprobarSaldo() {
        return saldo;
    }

    public synchronized void sumar() {
        while (saldo >= 40) {
            try {
                System.out.println("Ahorrador dice: saldo máximo, esperando para poder ingresar...");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        saldo += 10;
        System.out.println("Ahorrador dice: saldo = " + saldo);
        notifyAll();
    }

    public synchronized void restar() {
        while (saldo <= 0) {
            try {
                System.out.println("Gastador dice: saldo mínimo, esperando para poder gastar...");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        saldo -= 10;
        System.out.println("Gastador dice: saldo = " + saldo);
        notifyAll();

    }

    public void setSaldo(int saldo) {
        this.saldo = saldo;
    }
}
