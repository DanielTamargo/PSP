package com.tamargo.ejercicio6;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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

                System.out.print("[Server] esperando cliente...");
                Socket socket = serverSocket.accept();
                System.out.println("\r[Server] cliente conectado");
                System.out.println();
                DataOutputStream datoSalida = new DataOutputStream(socket.getOutputStream());
                datoSalida.writeInt(num1);
                datoSalida.writeInt(num2);
                System.out.println("[Server] números enviados: " + num1 + ", " + num2);

                DataInputStream datoEntrada = new DataInputStream(socket.getInputStream());
                int solucion = datoEntrada.readInt();
                System.out.println("[Server] solución recibida: " + solucion);
                System.out.println();

                socket.close();
            }

            serverSocket.close();
            System.out.println("[Server] servidor apagado");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
