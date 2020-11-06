package com.tamargo.pruebas;

import com.tamargo.ejercicio8.HiloServidor8;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerPrueba {

    public static void main(String[] args) {

        try {
            ServerSocket serverSocket = new ServerSocket(5600);
            int clientes = 0;

            System.out.println("Esperando a los clientes...");
            Socket socket = serverSocket.accept();
            while (true) {

                clientes++;
                System.out.println(socket.isConnected());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException ignored) { }

    }

}
