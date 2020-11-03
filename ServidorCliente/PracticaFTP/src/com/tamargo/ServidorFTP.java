package com.tamargo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorFTP {

    public static void main(String[] args) {

        String nombre = "[Servidor FTP] "; // <- Identificativo de los mensajes
        int numCliente = 0;
        ServerSocket servidorFTP = null;

        try {
            servidorFTP = new ServerSocket(21);
        } catch (IOException ignored) { }

        if (servidorFTP == null) {
            System.out.println(nombre + "Error al arrancar el server. ¿Puerto ocupado?");
        } else {
            System.out.println(nombre + "Servidor en marcha, esperando clientes...\n");
            while (true) {
                Socket cliente = null;
                try {
                    cliente = servidorFTP.accept();
                    numCliente++;
                    System.out.println(nombre + "¡Nueva conexión! Conectando con Cliente " + numCliente);
                } catch (IOException ignored) { }

                if (cliente != null) {
                    HiloServidorFTP hsFTP = new HiloServidorFTP(cliente, numCliente, nombre);
                    hsFTP.start();
                }
            }
        }

    }

}
