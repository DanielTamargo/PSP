package com.tamargo;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
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
            // Configuramos las propiedades para seleccionar qué certificado definirá la confianza con el servidor
            System.setProperty("javax.net.ssl.keyStore", "AlmacenSSL.jks");
            System.setProperty("javax.net.ssl.keyStorePassword", "12345Abcde");

            SSLServerSocketFactory sfact = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            SSLServerSocket serverSocketSSL = (SSLServerSocket) sfact.createServerSocket(6000);

            int clientes = 0;

            KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
            KeyPair keyPair = keygen.generateKeyPair();

            Cipher rsaCipher = Cipher.getInstance("RSA");
            rsaCipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());

            System.out.println("[Servidor] Esperando a los clientes...");
            while (true) {
                SSLSocket socket = (SSLSocket) serverSocketSSL.accept();
                clientes++;
                HiloServidor hilo = new HiloServidor(socket, clientes, keyPair, rsaCipher);
                hilo.start();
            }
        } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }

    }

}
