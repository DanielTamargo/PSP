package com.tamargo.ejercicio2;

import javax.crypto.Cipher;
import java.io.*;
import java.net.Socket;
import java.security.KeyPair;

public class HiloServidor2 extends Thread {

    private String nombre;
    private Socket socket;
    private int cliente;
    private KeyPair keyPair = null;
    private Cipher rsaCipher = null;
    ObjectInputStream datoEntrada;
    ObjectOutputStream datoSalida;
    DataInputStream dataIS;
    DataOutputStream dataOS;

    public HiloServidor2(Socket socket, int cliente) {
        this.socket = socket;
        this.cliente = cliente;
        this.nombre = "[Servidor Cliente " + cliente + "] ";
    }

    public HiloServidor2(Socket socket, int cliente, KeyPair keyPair, Cipher rsaCipher) {
        this.socket = socket;
        this.cliente = cliente;
        this.nombre = "[Servidor Cliente " + cliente + "] ";
        this.keyPair = keyPair;
        this.rsaCipher = rsaCipher;
    }

    @Override
    public void run() {
        System.out.println(nombre + "Conexión establecida");
        try {

            try {
                dataIS = new DataInputStream(socket.getInputStream());
                dataOS = new DataOutputStream(socket.getOutputStream());
                System.out.println("Flujos DATA preparados");

                datoEntrada = new ObjectInputStream(socket.getInputStream());
                datoSalida = new ObjectOutputStream(socket.getOutputStream());
                System.out.println("Flujos OBJECT preparados");
            } catch (IOException ignored) { }

            // Enviar clave pública
            System.out.println(nombre + "Enviando clave pública");
            datoSalida.writeObject(keyPair.getPublic());
            System.out.println(nombre + "Clave pública enviada");
            System.out.println();

            // Recibir clave simétrica y descifrarla con clave privada


            // Ya tenemos tunel para enviar mensajes cifrados con la clave simétrica recibida



        } catch (IOException e) {
            System.out.println("[Cliente] el server ha rechazado la conexión");
        }


    }
}
