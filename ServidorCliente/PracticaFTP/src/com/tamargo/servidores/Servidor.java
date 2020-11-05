package com.tamargo.servidores;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

    public static String nombre = "[Server] ";

    public static void main(String[] args) {

        ServidorFTP serverFTP = new ServidorFTP();
        serverFTP.configurarServidorFTP();

        try {
            ServerSocket serverSocket = new ServerSocket(5600);
            int numCliente = 0;

            System.out.println(nombre + "Esperando a los clientes...");
            while (true) {
                Socket socket = serverSocket.accept();
                numCliente++;

                HiloServidor hilo = new HiloServidor(socket, serverFTP, nombre, numCliente);
                hilo.start();
            }
        } catch (IOException ignored) {
            System.out.println(nombre + "Error al iniciar el servidor (¿el puerto está ocupado?)");
        }

    }

}
