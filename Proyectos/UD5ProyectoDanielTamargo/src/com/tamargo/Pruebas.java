package com.tamargo;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;

public class Pruebas {

    public static void main(String[] args) {
        String nombre = "[Prueba] ";

        try {
            System.out.println(nombre + "Generando clave simétrica");
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            keygen.init(128); // <- Tamaño clave
            SecretKey claveAES = keygen.generateKey();
            System.out.println(nombre + "Clave simétrica generada: " + claveAES.getAlgorithm());
            System.out.println();
        } catch (NoSuchAlgorithmException ignored) {}

    }

}
