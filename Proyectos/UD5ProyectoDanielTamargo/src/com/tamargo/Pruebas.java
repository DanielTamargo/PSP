package com.tamargo;

import com.tamargo.datos.EscribirFicheros;
import com.tamargo.datos.LeerFicheros;
import com.tamargo.datos.Pregunta;
import com.tamargo.datos.Usuario;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Pruebas {

    public static void main(String[] args) throws UnsupportedEncodingException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException, InterruptedException {

        long timeMilli = new Date().getTime();
        Thread.sleep(1001);
        long timeMilli2 = new Date().getTime();

        System.out.println(timeMilli2 - timeMilli);

    }

    public static String saltoLineaBoton(String texto) {
        String devolver = "";
        if (texto.length() > 20) {
            while (texto.length() > 20) {
                for (int i = 20; i > 0; i++) {
                    if (texto.charAt(i) == ' ') {
                        devolver += texto.substring(0, i);
                        texto = texto.substring(i + 1);
                        break;
                    }
                }
                devolver += "\n";
            }
            if (texto.length() > 0)
                devolver += texto;
        } else {
            devolver = texto;
        }
        return devolver;
    }

}
