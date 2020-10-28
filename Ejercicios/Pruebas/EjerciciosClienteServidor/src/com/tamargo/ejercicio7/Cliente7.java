package com.tamargo.ejercicio7;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Cliente7 {

    public static void main(String[] args) {

    }

    public void ejecutarCliente() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        try {
            // Solo se conecta
            Socket socket = new Socket("localhost", 5600);

            DataInputStream datoEntrada = new DataInputStream(socket.getInputStream());
            System.out.println("[Cliente] mensaje del servidor: " + datoEntrada.readUTF());

            DataOutputStream datoSalida = new DataOutputStream(socket.getOutputStream());

            System.out.print("[Cliente] mensaje a enviar: ");
            String mensaje = br.readLine();
            datoSalida.writeUTF(mensaje);

            System.out.println("[Cliente] mensaje del servidor: '" + datoEntrada.readUTF() + "'");

            socket.close();

        } catch (IOException e) {
            System.out.println("[Cliente] el server ha rechazado la conexión");
        }
    }

}
