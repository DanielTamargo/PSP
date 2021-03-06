package com.tamargo.ejercicio7;

import java.io.IOException;
import java.net.*;


public class Servidor7 {

    public static void main(String[] args) {

        try {
            ServerSocket serverSocket = new ServerSocket(5600);
            int clientes = 0;

            System.out.println("Esperando a los clientes...");
            while (true) {
                Socket socket = serverSocket.accept();
                clientes++;
                HiloServidor7 hilo = new HiloServidor7(socket, clientes);
                hilo.start();
            }
        } catch (IOException ignored) { }
    }

}
