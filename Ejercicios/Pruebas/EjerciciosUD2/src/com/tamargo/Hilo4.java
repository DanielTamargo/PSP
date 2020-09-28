package com.tamargo;

public class Hilo4 extends Thread {

    int tipo = 0;

    public Hilo4(int tipo) {
        this.tipo = tipo;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            if (tipo == 0)
                System.out.print("ping ");
            else
                System.out.print("pong ");

            try {
                sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        super.run();
    }
}
