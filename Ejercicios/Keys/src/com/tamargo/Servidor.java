package com.tamargo;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class Servidor {

    public static void main(String[] args) {

        try {
            ServerSocket serverSocket = new ServerSocket(5800);
            int clientes = 0;

            KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
            KeyPair keyPair = keygen.generateKeyPair();

            Cipher rsaCipher = Cipher.getInstance("RSA");
            rsaCipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());

            System.out.println("[Servidor] Esperando a los clientes...");
            while (true) {
                Socket socket = serverSocket.accept();
                clientes++;
                HiloServidor hilo = new HiloServidor(socket, clientes, keyPair, rsaCipher);
                hilo.start();
            }
        } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException ignored) { }

    }

}
