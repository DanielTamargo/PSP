package com.tamargo;

public class Actividad1 {
    public static void main(String[] args) throws InterruptedException {

        System.out.println("Comienza el main.");
        act1hiloNormal();
        //act1hiloRunnable();
        System.out.println("Finaliza el main.");

    }

    public static void act1hiloNormal() throws InterruptedException {

        /*
        new HiloAct1().start(); //hilo 0
        new HiloAct1().start(); //hilo 1
        new HiloAct1().start(); //hilo 2
        new HiloAct1().start(); //hilo 3
        */

        HiloAct1 hilo = new HiloAct1();
        hilo.setName("Hilo 1");
        System.out.println("Hilo 1 en estado: " + hilo.getState()); //hilo en estado NEW
        hilo.start();
        System.out.println("Hilo 1 en estado: " + hilo.getState()); //hilo en estado RUNNABLE
        hilo.join();
        System.out.println("Hilo 1 en estado: " + hilo.getState()); //hilo en estado TERMINATED
    }

    public static void act1hiloRunnable() {

        //HiloAct1Runnable hilo = new HiloAct1Runnable();
        //Thread hiloConvertido = new Thread(hilo);
        //hiloConvertido.start();

        new Thread(new HiloAct1Runnable(), "Hilo 1").start(); //hilo 0
        new Thread(new HiloAct1Runnable(), "Hilo 2").start(); //hilo 1
        new Thread(new HiloAct1Runnable(), "Hilo 3").start(); //hilo 2
        new Thread(new HiloAct1Runnable(), "Hilo 4").start(); //hilo 3
    }

}
