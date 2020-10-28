package com.tamargo.ejercicio7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class HiloServidor7 extends Thread {

    private Socket socket;
    private int clientes;

    public HiloServidor7(Socket socket, int clientes) {
        this.socket = socket;
        this.clientes = clientes;
    }

    @Override
    public void run() {
        try {
            DataOutputStream datoSalida = new DataOutputStream(socket.getOutputStream());
            DataInputStream datoEntrada = new DataInputStream(socket.getInputStream());
            System.out.println("[Server] Saludando al Cliente " + clientes);
            datoSalida.writeUTF("Hola cliente número " + clientes);
            String mensaje = datoEntrada.readUTF();
            System.out.println("[Server] mensaje del Cliente " + clientes + " '" + mensaje + "'");
            datoSalida.writeUTF(mensaje.toUpperCase());
            socket.close();
        } catch (IOException ignored) { }
    }
}
