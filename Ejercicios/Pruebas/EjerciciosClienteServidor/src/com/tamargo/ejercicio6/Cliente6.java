package com.tamargo.ejercicio6;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Cliente6 {

    public static void main(String[] args) {

        try {
            Socket socket = new Socket("localhost", 5600);
            System.out.println("[Cliente] cliente conectado al servidor");
            DataInputStream datoEntrada = new DataInputStream(socket.getInputStream());
            int n1 = datoEntrada.readInt();
            int n2 = datoEntrada.readInt();

            System.out.println();
            System.out.println("[Cliente] números recibidos: " + n1 + ", " + n2);
            int solucion = n1 * n2;
            System.out.println("[Cliente] solución enviada: " + solucion);

            DataOutputStream datoSalida = new DataOutputStream(socket.getOutputStream());
            datoSalida.writeInt(solucion);

            socket.close();
            System.out.println("[Cliente] cliente desconectado del servidor");

        } catch (IOException e) {
            System.out.println("[Cliente] error al conectar con el servidor");
        }
    }

}
