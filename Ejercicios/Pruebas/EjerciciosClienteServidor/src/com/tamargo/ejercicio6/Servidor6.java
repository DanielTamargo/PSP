package com.tamargo.ejercicio6;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class Servidor6 {

    public static void main(String[] args) {

        try {
            ServerSocket serverSocket = new ServerSocket(5600);
            System.out.println("[Server] servidor encendido");

            for (int i = 0; i < 3; i++) {
                int num1 = new Random().nextInt(7) + 1;
                int num2 = new Random().nextInt(7) + 1;
                DosNumeros dn = new DosNumeros(num1, num2);
                System.out.print("[Server] esperando cliente...");
                Socket socket = serverSocket.accept();
                System.out.println("\r[Server] cliente conectado");
                System.out.println();

                ObjectOutputStream objOUT = new ObjectOutputStream(socket.getOutputStream());
                objOUT.writeObject(dn);
                System.out.println("[Server] objeto enviado: " + dn);

                ObjectInputStream objIS = new ObjectInputStream(socket.getInputStream());
                dn = (DosNumeros) objIS.readObject();
                System.out.println("[Server] objeto recibido: " + dn);
                System.out.println("[Server] solución: " + dn.getSolucion());
                System.out.println();

                socket.close();
            }

            serverSocket.close();
            System.out.println("[Server] servidor apagado");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
