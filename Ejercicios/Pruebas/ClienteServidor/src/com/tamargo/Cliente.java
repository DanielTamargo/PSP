package com.tamargo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Cliente {

    public static void main(String[] args) throws IOException {

        Socket cliente = new Socket("localhost", 5600);

        DataOutputStream datoSalida = new DataOutputStream(cliente.getOutputStream());
        datoSalida.writeUTF("Petición al servidor");

        cliente.close();
    }

    public void lanzarCliente() throws IOException {
        Socket cliente = new Socket("localhost", 5600);

        DataOutputStream datoSalida = new DataOutputStream(cliente.getOutputStream());
        datoSalida.writeUTF("Petición al servidor");

        cliente.close();
    }


}
