package com.tamargo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {

        //ejercicio1y2();

        //ejercicio3();

        //ejercicio4();

        //ejercicio5();

        ejercicio6();

        //ejercicio7();

    }


    public static void ejercicio7() throws InterruptedException {

        System.out.println("Ejercicio 7\n");

        Integer contador = 0;

        System.out.println("Contador inicial: " + contador);

        Hilo6 h1 = new Hilo6(contador);
        h1.start();
        h1.join();
        Hilo6 h2 = new Hilo6(contador);
        h2.start();
        h2.join();
        Hilo6 h3 = new Hilo6(contador);
        h3.start();
        h3.join();
        Hilo6 h4 = new Hilo6(contador);
        h4.start();
        h4.join();

        System.out.println("Contador final: " + contador);

    }

    public static void ejercicio6() throws IOException, InterruptedException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        int tiempo = 1;
        System.out.print("¿Cuántos segundos quieres esperar entre hilos? ");
        tiempo = Integer.parseInt(br.readLine());

        Hilo5 h1 = new Hilo5();
        h1.start();
        Thread.sleep(tiempo * 1000);
        //h1.join(tiempo * 1000);

        Hilo5 h2 = new Hilo5();
        h2.start();
        Thread.sleep(tiempo * 1000);
        //h2.join(tiempo * 1000);

        Hilo5 h3 = new Hilo5();
        h3.start();
        Thread.sleep(tiempo * 1000);
        //h3.join(tiempo * 1000);

        Hilo5 h4 = new Hilo5();
        h4.start();
        Thread.sleep(tiempo * 1000);
        //h4.join(tiempo * 1000);

        Hilo5 h5 = new Hilo5();
        h5.start();
        Thread.sleep(tiempo * 1000);
        //h5.join(tiempo * 1000);

        Hilo5 h6 = new Hilo5();
        h6.start();
        h6.join(tiempo * 1000);

    }

    public static void ejercicio5() throws InterruptedException {

        System.out.println("Ejercicio 5\n");

        System.out.println("Creamos los hilos.");
        Hilo4 hiloPing = new Hilo4(0); // 0 = ping
        Hilo4 hiloPong = new Hilo4(1); // 1 = pong

        System.out.println("Lanzamos los hilos.");
        hiloPing.start();
        hiloPong.start();

        hiloPong.join();
        System.out.println();

    }

    public static void ejercicio4() {

        System.out.println("Ejercicio 4\n");

        System.out.println("Creamos los hilos.");
        Hilo3 hiloPares = new Hilo3(0); // 0 = empieza en 0, es decir, pares
        hiloPares.setName("Pares");
        Hilo3 hiloImpares = new Hilo3(1); // 1 = empieza en 1, es decir, impares
        hiloImpares.setName("Impares");

        System.out.println("Ejecutamos los hilos.");
        hiloPares.start();
        hiloImpares.start();

    }


    public static void ejercicio3() {

        System.out.println("Ejercicio 3\n");

        System.out.println("Creamos hilo 1.");
        Hilo2 hilo1 = new Hilo2();
        hilo1.setName("prueba");

        System.out.println("Creamos hilo 2.");
        Hilo2 hilo2 = new Hilo2();
        hilo2.setName("java");

        System.out.println("Creamos hilo 3.");
        Hilo2 hilo3 = new Hilo2();
        hilo3.setName("Campeon");

        System.out.println("Lanzamos hilo 1.");
        hilo1.start();
        System.out.println("Lanzamos hilo 2.");
        hilo2.start();
        System.out.println("Lanzamos hilo 3.");
        hilo3.start();

    }

    public static void ejercicio1y2() throws IOException {

        System.out.println("Ejercicios 1 y 2\n");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String textoHilo1;
        String textoHilo2;
        String textoHilo3;

        System.out.print("Escribe el texto para el hilo 1: ");
        textoHilo1 = br.readLine();
        System.out.print("Escribe el texto para el hilo 2: ");
        textoHilo2 = br.readLine();
        System.out.print("Escribe el texto para el hilo 3: ");
        textoHilo3 = br.readLine();

        System.out.println("Creamos hilo 1.");
        Hilo1 hilo1 = new Hilo1(textoHilo1);
        System.out.println("Lanzamos hilo 1.");
        hilo1.start();

        System.out.println("Creamos hilo 2.");
        Hilo1 hilo2 = new Hilo1(textoHilo2);
        System.out.println("Lanzamos hilo 2.");
        hilo2.start();

        System.out.println("Creamos hilo 3.");
        Hilo1 hilo3 = new Hilo1(textoHilo3);
        System.out.println("Lanzamos hilo 3.");
        hilo3.start();

    }
}
