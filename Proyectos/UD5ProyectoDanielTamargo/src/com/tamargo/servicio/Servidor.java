package com.tamargo.servicio;

import com.tamargo.datos.GuardarLogs;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;

public class Servidor {

    public static void main(String[] args) {
        lanzarServidor();
    }

    /**
     * Método que lanzará el Servidor y ejecutará los siguiente pasos:
     * - Configura las propiedades para seleccionar qué certificado definirá la confianza con el servidor
     * - Gracias a dichas propiedades, genera un SSLServerSocket que aceptará conexiones posteriormente
     * - Genera un par de claves (pub/priv) RSA
     * - Comienza un bucle infinito donde irá aceptando conexiones
     * - Cada conexión aceptada se delegará en un hilo del servidor
     */
    public static void lanzarServidor() {
        String nombre = "[Servidor] ";

        try {
            // Configuramos las propiedades para seleccionar qué certificado definirá la confianza con el servidor
            System.setProperty("javax.net.ssl.keyStore", "./certificados/servidorAlmacenSSL.jks");
            System.setProperty("javax.net.ssl.keyStorePassword", "12345Abcde");

            SSLServerSocketFactory sfact = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            SSLServerSocket serverSocketSSL = (SSLServerSocket) sfact.createServerSocket(6000);

            int clientes = 0;

            // Preparamos la pareja de claves pública y privada
            KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
            KeyPair keyPair = keygen.generateKeyPair();

            System.out.println(nombre +"Esperando a los clientes...");
            while (true) {
                SSLSocket socket = (SSLSocket) serverSocketSSL.accept();
                clientes++;
                HiloServidor hilo = new HiloServidor(clientes, socket, keyPair);
                hilo.start();
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            System.out.println(nombre + "Error al inicializar el servidor.\nMotivo del error: " + e.getLocalizedMessage());
            GuardarLogs.logger.log(Level.SEVERE, "Error al inicializar el servidor. Error: " + e.getLocalizedMessage());
        }
    }

}
