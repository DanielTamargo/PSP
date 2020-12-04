package com.tamargo.datos;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Level;

public class LeerFicheros {

    /**
     * Recoge los usuarios del fichero usuarios.dat
     */
    public static synchronized ArrayList<Usuario> leerUsuarios() {
        ArrayList<Usuario> usuarios = new ArrayList<>();

        try {
            File f = new File("./ficheros/usuarios.dat");
            if (f.exists()) {
                ObjectInputStream objIS = new ObjectInputStream(new FileInputStream(f));
                try {
                    while (true) {
                        usuarios.add((Usuario) objIS.readObject());
                    }
                } catch (EOFException ignored) {
                }
                objIS.close();
                //System.out.println("[Fichero] Usuarios leídos: " + usuarios.size());
            } else {
                System.out.println("[Fichero] No existen usuarios");
            }
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            System.out.println("[Fichero] Error al leer los usuarios");
            GuardarLogs.logger.log(Level.SEVERE, "Error al leer los usuarios. Error: " + e.getLocalizedMessage());
        }

        return usuarios;
    }

    /**
     * Recoge las preguntas del fichero preguntas.dat
     */
    public static synchronized ArrayList<Pregunta> leerPreguntas() {
        ArrayList<Pregunta> preguntas = new ArrayList<>();

        try {
            File f = new File("./ficheros/preguntas.dat");
            if (f.exists()) {
                ObjectInputStream objIS = new ObjectInputStream(new FileInputStream(f));
                try {
                    while (true) {
                        preguntas.add((Pregunta) objIS.readObject());
                    }
                } catch (EOFException ignored) {
                }
                objIS.close();
                //System.out.println("[Fichero] Preguntas leídas: " + preguntas.size());
            } else {
                System.out.println("[Fichero] No existen preguntas");
            }
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            System.out.println("[Fichero] Error al leer las preguntas");
            GuardarLogs.logger.log(Level.SEVERE, "Error al leer las preguntas. Error: " + e.getLocalizedMessage());
        }

        return preguntas;
    }

    /**
     * Genera un ArrayList que contendrá numerosos ArrayLists que cada uno de ellos contendrá el contenido de un log
     */
    public static synchronized ArrayList<ArrayList<String>> contenidoTodosLosLogs() {
        ArrayList<ArrayList<String>> contenidoTodosLogs = new ArrayList<>();

        for (File f: Objects.requireNonNull(new File("./logs").listFiles())) {
            if (!f.getName().contains("lck")) {
                ArrayList<String> contenidoUnLog = new ArrayList<>();
                contenidoUnLog.add(f.getName());
                try {
                    BufferedReader br = new BufferedReader(new FileReader(f));
                    String linea;
                    while ((linea = br.readLine()) != null) {
                        contenidoUnLog.add(linea);
                    }
                } catch (IOException ignored) {}
                contenidoTodosLogs.add(contenidoUnLog);
            }
        }

        return contenidoTodosLogs;
    }

}
