package com.tamargo;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        File carpetaArchivos = new File(".\\archivos/hola");

        if (carpetaArchivos.exists()) {
            int numArchivos = carpetaArchivos.listFiles().length;
            System.out.println(numArchivos);
        }

        System.out.println(carpetaArchivos.getPath());
    }
}
