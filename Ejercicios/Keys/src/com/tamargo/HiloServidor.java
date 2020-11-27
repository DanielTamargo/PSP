package com.tamargo;

import javax.crypto.Cipher;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.Socket;
import java.security.KeyPair;

public class HiloServidor extends Thread {

    private String nombre;
    private SSLSocket socket;
    private int cliente;
    private KeyPair keyPair = null;
    private Cipher rsaCipher = null;
    ObjectInputStream datoEntrada;
    ObjectOutputStream datoSalida;
    DataInputStream dataIS;
    DataOutputStream dataOS;

    public HiloServidor(SSLSocket socket, int cliente) {
        this.socket = socket;
        this.cliente = cliente;
        this.nombre = "[Servidor Cliente " + cliente + "] ";
    }

    public HiloServidor(SSLSocket socket, int cliente, KeyPair keyPair, Cipher rsaCipher) {
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

                datoSalida = new ObjectOutputStream(socket.getOutputStream());
                datoEntrada = new ObjectInputStream(socket.getInputStream());
            } catch (IOException ignored) { }

            // Enviar clave pública
            System.out.println(nombre + "Enviando clave pública");
            datoSalida.writeObject(keyPair.getPublic());
            System.out.println(nombre + "Clave pública enviada");
            System.out.println();

            // Recibir clave simétrica y descifrarla con clave privada
            System.out.println(nombre + "Recibiendo clave simétrica");
            byte[] claveAEScifrada = dataIS.readAllBytes();
            System.out.println(nombre + "Clave simétrica recibida");
            System.out.println();

            // Ya tenemos tunel para enviar mensajes cifrados con la clave simétrica recibida



        } catch (IOException e) {
            System.out.println(nombre + "El server ha rechazado la conexión. Motivo:");
            e.printStackTrace();
        }


    }
}

