package com.tamargo.datos;

import java.io.*;
import java.util.ArrayList;

public class LeerFicheros {

    /**
     * Recoge los usuarios del fichero usuarios.dat
     */
    public static ArrayList<Usuario> leerUsuarios() {
        ArrayList<Usuario> usuarios = new ArrayList<>();

        try {
            ObjectInputStream objIS = new ObjectInputStream(new FileInputStream(new File("./ficheros/usuarios.dat")));
            try {
                while (true) {
                    usuarios.add((Usuario) objIS.readObject());
                }
            } catch (EOFException ignored) { }
            objIS.close();
            System.out.println("[Fichero] Usuarios leídos: " + usuarios.size());
        } catch (IOException | ClassNotFoundException | ClassCastException ignored) {
            System.out.println("[Fichero] Error al leer los usuarios");
        }

        return usuarios;
    }

    /**
     * Recoge los preguntas del fichero preguntas.dat
     */
    public static ArrayList<Pregunta> leerPreguntas() {
        ArrayList<Pregunta> preguntas = new ArrayList<>();

        try {
            ObjectInputStream objIS = new ObjectInputStream(new FileInputStream(new File("./ficheros/preguntas.dat")));
            try {
                while (true) {
                    preguntas.add((Pregunta) objIS.readObject());
                }
            } catch (EOFException ignored) { }
            objIS.close();
            System.out.println("[Fichero] Preguntas leídas: " + preguntas.size());
        } catch (IOException | ClassNotFoundException | ClassCastException ignored) {
            System.out.println("[Fichero] Error al leer las preguntas");
        }

        return preguntas;
    }

}
