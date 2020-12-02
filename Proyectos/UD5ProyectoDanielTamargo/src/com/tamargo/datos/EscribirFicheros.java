package com.tamargo.datos;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.logging.Level;

public class EscribirFicheros {

    /**
     * Añade un usuario al fichero usuarios.dat
     */
    public static boolean addUsuario(Usuario usuario) {
        boolean insertado = true;

        try {
            ArrayList<Usuario> usuarios = LeerFicheros.leerUsuarios();
            ObjectOutputStream objOS = new ObjectOutputStream(new FileOutputStream(new File("./ficheros/usuarios.dat")));
            boolean nickExiste = false;
            for (Usuario usu : usuarios) {
                if (usu.getNick().equalsIgnoreCase(usuario.getNick()))
                    nickExiste = true;
                objOS.writeObject(usu);
            }
            if (!nickExiste) {
                objOS.writeObject(usuario);
                System.out.println("[Fichero] Usuario con el nick '" + usuario.getNick() + "' guardado con éxito");
                GuardarLogs.logger.log(Level.FINE, "Nuevo usuario registrado -> " + usuario.getNick());
            } else {
                insertado = false;
                System.out.println("[Fichero] Ya existe un usuario guardado con el nick '" + usuario.getNick() + "'");
                GuardarLogs.logger.log(Level.WARNING, "No se pudo registrar, ya existe un usuario con el nick -> " + usuario.getNick());
            }
            objOS.close();
        } catch (IOException ignored) {
            System.out.println("[Fichero] Error al registrar un nuevo usuario. Usuario: " + usuario.getNick());
            GuardarLogs.logger.log(Level.SEVERE, "Error al registrar un nuevo usuario -> " + usuario.getNick());
            insertado = false;
        }

        return insertado;
    }

    public static void modificarPuntuacionUsuario(String nick, int puntuacion) {
        try {
            boolean nickExiste = false;
            ArrayList<Usuario> usuarios = LeerFicheros.leerUsuarios();
            ObjectOutputStream objOS = new ObjectOutputStream(new FileOutputStream(new File("./ficheros/usuarios.dat")));
            for (Usuario usu : usuarios) {
                if (usu.getNick().equalsIgnoreCase(nick)) {
                    usu.setPuntuacion(puntuacion);
                    nickExiste = true;
                }
                objOS.writeObject(usu);
            }
            objOS.close();

            if (nickExiste) {
                System.out.println("[Fichero] Puntuación del usuario editado con éxito. Usuario: " + nick);
                GuardarLogs.logger.log(Level.FINE, "Error al editar la puntuación del usuario -> " + nick);
            }
        } catch (IOException ignored) {
            System.out.println("[Fichero] Error al editar un nuevo usuario");
            GuardarLogs.logger.log(Level.SEVERE, "Error al editar un nuevo usuario -> " + nick);
        }
    }

    /**
     * Añade una pregunta al fichero preguntas.dat
     */
    public static boolean addPregunta(Pregunta pregunta) {
        boolean insertada = true;

        try {
            ArrayList<Pregunta> preguntas = LeerFicheros.leerPreguntas();
            boolean existe = false;
            ObjectOutputStream objOS = new ObjectOutputStream(new FileOutputStream(new File("./ficheros/preguntas.dat")));
            for (Pregunta preg : preguntas) {
                if (preg.getTitulo().equalsIgnoreCase(pregunta.getTitulo()))
                    existe = true;
                objOS.writeObject(preg);
            }
            if (!existe) {
                objOS.writeObject(pregunta);
                System.out.println("[Fichero] Pregunta '" + pregunta.getTitulo() + "' guardada con éxito");
                GuardarLogs.logger.log(Level.FINE, "Pregunta guardada con éxito. Pregunta: " + pregunta.getTitulo());
            } else {
                System.out.println("[Fichero] Ya existe la pregunta '" + pregunta.getTitulo() + "'");
                GuardarLogs.logger.log(Level.WARNING, "Ya existe la pregunta: " + pregunta.getTitulo());
            }
            objOS.close();

        } catch (IOException ignored) {
            System.out.println("[Fichero] Error al guardar una nueva pregunta");
            GuardarLogs.logger.log(Level.SEVERE, "Error al guardar la nueva pregunta -> " + pregunta.getTitulo());
            insertada = false;
        }

        return insertada;
    }

}
