package com.tamargo.ejercicio8;

import com.tamargo.ejercicio7.HiloServidor7;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor8 {

    public static void main(String[] args) {

        try {
            ServerSocket serverSocket = new ServerSocket(5600);
            int clientes = 0;

            System.out.println("Esperando a los clientes...");
            while (true) {
                Socket socket = serverSocket.accept();
                clientes++;
                HiloServidor8 hilo = new HiloServidor8(socket, clientes);
                hilo.start();
            }
        } catch (IOException ignored) { }

    }

}
