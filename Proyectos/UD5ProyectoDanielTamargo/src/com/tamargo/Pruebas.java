package com.tamargo;

import com.tamargo.datos.EscribirFicheros;
import com.tamargo.datos.LeerFicheros;
import com.tamargo.datos.Usuario;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Pruebas {

    public static void main(String[] args) throws UnsupportedEncodingException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException {
        // UTF-8 es el por defecto de los algoritmos, pero mejor asegurar la consistencia
        final String utf8 = "utf-8";
        String contrasenyaCifradora = "ContraseñaSuperSecretaParaEncriptarContraseñas";
        byte[] keyBytes = Arrays.copyOf(contrasenyaCifradora.getBytes(utf8), 24);
        SecretKey claveCifrarContrasenya = new SecretKeySpec(keyBytes, "DESede");

        // El vector debe tener una longitud de 8 bytes
        String vector = "ABCD1234";
        IvParameterSpec iv = new IvParameterSpec(vector.getBytes(utf8));

        // Creamos en encriptador
        Cipher encrypt = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        encrypt.init(Cipher.ENCRYPT_MODE, claveCifrarContrasenya, iv);

        // Preparamos la contraseña encriptada para que el servidor no pueda saber cuál es la contraseña 'pura'
        String message = "-_{}¡!?¿[]";
        byte[] messageBytes = message.getBytes(utf8);
        byte[] encryptedByted = encrypt.doFinal(messageBytes);

        // Encriptar como un mensaje normal y enviar
        System.out.println(new String(messageBytes, utf8));
        System.out.println(new String(encryptedByted, utf8));


    }

}
