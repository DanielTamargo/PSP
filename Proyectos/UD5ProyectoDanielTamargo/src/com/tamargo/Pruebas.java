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
import java.util.*;

public class Pruebas {

    public static void main(String[] args) throws UnsupportedEncodingException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException {
        Usuario usuario1 = new Usuario("Irune", "Mendez", 12, "irune", "test", 1, 150);
        EscribirFicheros.addUsuario(usuario1);

        ArrayList<Usuario> usuarios = LeerFicheros.leerUsuarios();
        HashMap<Usuario, Integer> listaSinOrdenar = new HashMap<>();

        for (Usuario usuario: usuarios) {
            listaSinOrdenar.put(usuario, usuario.getPuntuacion());
        }

        LinkedHashMap<Usuario, Integer> listaOrdenada = new LinkedHashMap<>();
        listaSinOrdenar.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> listaOrdenada.put(x.getKey(), x.getValue()));

        for (Usuario value : listaOrdenada.keySet()) {

        }


    }

}
