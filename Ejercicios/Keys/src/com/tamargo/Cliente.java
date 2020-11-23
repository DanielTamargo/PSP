package com.tamargo;

import javax.crypto.*;
import java.io.*;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

public class Cliente {

    public static void main(String[] args) {
        String nombre = "[Cliente] ";
        try {
            // Solo se conecta
            Socket socket = new Socket("localhost", 5800);
            System.out.println(nombre + "Conexión realizada");

            // Generamos flujos de datos
            DataInputStream dataIS = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOS = new DataOutputStream(socket.getOutputStream());

            ObjectInputStream datoEntrada = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream datoSalida = new ObjectOutputStream(socket.getOutputStream());

            // Recibir clave pública
            System.out.println(nombre + "Recibiendo clave pública");
            PublicKey serverPK = (PublicKey) datoEntrada.readObject();
            System.out.println(nombre + "Clave pública recibida");
            System.out.println();

            // Enviar clave simétrica que generemos cifrándola con la clave recibida
            // Generar claves
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            keygen.init(128); // <- Tamaño clave
            SecretKey claveAES = keygen.generateKey();

            // Generar cifrador/descifrador
            // Primero generamos el cifrador para cifrar la clave simétrica que mandaremos,
            // y luego el cifrador/descifrador para el túnel
            Cipher rsaCipher = Cipher.getInstance("RSA");
            rsaCipher.init(Cipher.ENCRYPT_MODE, serverPK);
            byte[] mandarClave = claveAES.getEncoded();
            byte[] mandarClaveAEScifrada = rsaCipher.doFinal(mandarClave);
            dataOS.write(mandarClaveAEScifrada);

            // Ya está establecido un tunel donde poder enviar mensajes cifrados con la clave simétrica generada
            Cipher aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.ENCRYPT_MODE, claveAES);





        } catch (IOException | NoSuchAlgorithmException |
                NoSuchPaddingException | InvalidKeyException | ClassNotFoundException
                | IllegalBlockSizeException | BadPaddingException e) {
            System.out.println("[Cliente] El cliente ha fallado o el server ha rechazado la conexión. Motivo:\n" + e.getLocalizedMessage());
        }
    }

}
