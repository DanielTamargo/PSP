package com.tamargo.servicio;

import com.tamargo.datos.GuardarLogs;

import javax.crypto.*;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.logging.Level;

public class Cliente {

    public static void main(String[] args) {
        String nombre = "[Cliente] ";
        try {
            // Configuramos las propiedades para que ""reciba"" el certificado (realmente accede a él)
            System.setProperty("javax.net.ssl.trustStore", "./certificados/clienteAlmacenSSL");
            System.setProperty("javax.net.ssl.trustStorePassword", "890123");

            // Nos conectamos
            SSLSocketFactory sfact = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket socketSSL = (SSLSocket) sfact.createSocket("localhost", 6000);
            System.out.println(nombre + "Conexión realizada");

            // Generamos flujos de datos
            ObjectInputStream objIS = new ObjectInputStream(socketSSL.getInputStream());
            ObjectOutputStream objOS = new ObjectOutputStream(socketSSL.getOutputStream());

            // Recibimos clave pública
            System.out.println(nombre + "Recibiendo clave pública");
            PublicKey serverPK = (PublicKey) objIS.readObject();
            System.out.println(nombre + "Clave pública recibida");
            System.out.println();

            // Enviarremos una clave simétrica que generemos cifrándola con la clave pública recibida
            //          Generamos la clave simétrica
            System.out.println(nombre + "Generando clave simétrica");
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            keygen.init(128); // <- Tamaño clave
            SecretKey claveAES = keygen.generateKey();
            System.out.println(nombre + "Clave simétrica generada: " + claveAES.toString());
            System.out.println();

            // Generar cifrador/descifrador
            //      Primero generamos el cifrador para cifrar la clave simétrica que mandaremos,
            //      y luego el cifrador/descifrador para el túnel
            System.out.println(nombre + "Generando Cipher para encriptar/desencriptar");
            Cipher rsaCipher = Cipher.getInstance("RSA");
            rsaCipher.init(Cipher.ENCRYPT_MODE, serverPK);
            System.out.println(nombre + "Cipher generado y configurado para encriptar");
            byte[] mandarClave = claveAES.getEncoded();
            byte[] mandarClaveAEScifrada = rsaCipher.doFinal(mandarClave);
            System.out.println(nombre + "Clave simétrica encriptada con la clave pública del servidor y preparada para enviar");

            //      Enviamos la clave simétrica cifrada con la clave pública recibida del servidor
            objOS.writeObject(mandarClaveAEScifrada);
            System.out.println(nombre + "Clave simétrica encriptada enviada");
            System.out.println();

            // Ya está establecido un tunel donde podemos enviar y recibir mensajes cifrados con la clave simétrica generada
            // Ejemplos de enviar mensaje desencriptado y encriptado
            //System.out.println(nombre + "El servidor dice: " + desencriptarMensaje(claveAES, (byte[]) objIS.readObject()));
            //objOS.writeObject(encriptarMensaje(claveAES, "Sí, gracias por tanta intimidad"));

            // LISTENERS y comprobar que no se cierra el socket en el constructor de la ventana


            System.out.println();
            System.out.println(nombre + "Desconectando del servidor. ¡Adiós!");
            objIS.close();
            objOS.close();
        } catch (IOException | NoSuchAlgorithmException |
                NoSuchPaddingException | InvalidKeyException | ClassNotFoundException
                | IllegalBlockSizeException | BadPaddingException e) {
            System.out.println("[Cliente] El cliente ha fallado o el server ha rechazado la conexión. Motivo:\n" + e.getLocalizedMessage());
            GuardarLogs.logger.log(Level.SEVERE, "Error con el servicio del cliente. Error: " + e.getLocalizedMessage());
        }
    }

    public static String desencriptarMensaje(SecretKey claveAES, byte[] mensaje) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.DECRYPT_MODE, claveAES);
        return new String(aesCipher.doFinal(mensaje));
    }

    public static byte[] encriptarMensaje(SecretKey claveAES, String mensaje) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.ENCRYPT_MODE, claveAES);
        return aesCipher.doFinal(mensaje.getBytes());
    }

}
