package com.tamargo.servidores;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

//TODO Crear un servidor normal que pueda hablar en bucle con un cliente e instancie
//     un servidor FTP para usarlo a la hora de tradear archivos
//     también añadir un servidor SMTP que realice un envío del email cuando se deba


public class ServidorFTP {

    private ServerSocket servidorFTP = null;
    private final String nombre = "[Servidor FTP] "; // <- Identificativo de los mensajes

    public void configurarServidorFTP() {
        try {
            servidorFTP = new ServerSocket(21);
            System.out.println(nombre + "Servicio FTP ON!");
        } catch (IOException ignored) { }
    }

    public boolean comprobarServidorFTP() {
        //boolean serverON = servidorFTP != null;
        //System.out.println("Servidor FTP sigue conectado:" + serverON);
        return servidorFTP != null;
    }

    public void ejecutarServidorFTP(int numCliente, int opcion, int numFichero, ArrayList<File> ficheros) {
        // El server normal es el que está en bucle y delega en este método al cliente que requiera servicios FTP
        Socket cliente = null;
        try {
            cliente = servidorFTP.accept();
        } catch (IOException ignored) { }

        if (cliente != null) {
            HiloServidorFTP hsFTP = new HiloServidorFTP(cliente, numCliente, nombre, opcion);
            if (opcion == 1) {
                hsFTP.setFicheros(ficheros);
                hsFTP.setNumFichero(numFichero);
            }
            hsFTP.start();
        } else {
            System.out.println(nombre + "Error al conectar con el Cliente " + numCliente);
        }
    }

    public String getNombre() {
        return nombre;
    }
}
