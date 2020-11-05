package com.tamargo;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        String nomFichero = "hola";
        try {
            String nombre = nomFichero.substring(0, nomFichero.lastIndexOf('.'));
            String extension = nomFichero.substring(nomFichero.lastIndexOf('.'));
            System.out.println(nombre + "(" + 1 + ")" + extension);
        } catch (IndexOutOfBoundsException ignored) {
            System.out.println(nomFichero + "(" + 1 + ")");
        }
    }
}
