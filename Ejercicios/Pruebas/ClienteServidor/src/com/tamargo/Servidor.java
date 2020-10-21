package com.tamargo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(5600);
        System.out.println("Server Socket configurado\n");

        System.out.println("Esperando peticiones...");
        Socket socket = serverSocket.accept();
        System.out.println("Petición confirmada.\n");

        DataInputStream datoEntrada = new DataInputStream(socket.getInputStream());
        DataOutputStream datoSalida = new DataOutputStream(socket.getOutputStream());
        //System.out.println("Flujo de datos (entrada y salida) generado\n");

        String respuesta = datoEntrada.readUTF();
        System.out.println("Mensaje recibido: " + respuesta);

        socket.close();
        serverSocket.close();
    }

    public void lanzarServidor() throws IOException {
        ServerSocket serverSocket = new ServerSocket(5600);
        System.out.println("Server Socket configurado\n");

        System.out.println("Esperando peticiones...");
        Socket socket = serverSocket.accept();
        System.out.println("Petición confirmada.\n");

        DataInputStream datoEntrada = new DataInputStream(socket.getInputStream());
        DataOutputStream datoSalida = new DataOutputStream(socket.getOutputStream());
        //System.out.println("Flujo de datos (entrada y salida) generado\n");

        String respuesta = datoEntrada.readUTF();
        System.out.println("Mensaje recibido: " + respuesta);

        socket.close();
        serverSocket.close();
    }

}
