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
    public static synchronized boolean addUsuario(Usuario usuario) {
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

    /**
     * Modifica el registro de la puntuación de un usuario en el fichero
     */
    public static synchronized void modificarPuntuacionUsuario(String nick, int puntuacion) {
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
    public static synchronized boolean addPregunta(Pregunta pregunta) {
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



    /*
    Estos dos métodos que están comentados fueron creados con la idea de que varios clientes no pudieran iniciar sesión
    a la vez con un usuario

    Funcionaba bien, pero pensándolo bien, la mayoría de webs y juegos te dejan iniciar sesión donde ya se ha iniciado
    sesión, de hecho, los juegos que no dejan tener 2 conexiones distintas en el mismo usuario, expulsan al usuario que
    estaba conectado con anterioridad (se puede hacer pero ya me estoy quedando sin tiempo y prefiero acabar el
    planning que tengo desde el principio)
     */
    /*
    public static synchronized void conectarUsuario(Usuario usuario) {
        try {
            ArrayList<Usuario> usuarios = LeerFicheros.leerUsuarios();
            ObjectOutputStream objOS = new ObjectOutputStream(new FileOutputStream(new File("./ficheros/usuarios.dat")));
            for (Usuario usu : usuarios) {
                if (usu.getNick().equalsIgnoreCase(usuario.getNick())) {
                    usu.setConectado(true);
                }
                objOS.writeObject(usu);
            }
            objOS.close();

        } catch (IOException ignored) {
            System.out.println("[Fichero] Error al activar el nuevo como conectado -> " + usuario.getNick());
            GuardarLogs.logger.log(Level.SEVERE, "Error al activar el nuevo como conectado -> " + usuario.getNick());
        }
    }

    public static synchronized void desconectarUsuario(Usuario usuario) {
        try {
            ArrayList<Usuario> usuarios = LeerFicheros.leerUsuarios();
            ObjectOutputStream objOS = new ObjectOutputStream(new FileOutputStream(new File("./ficheros/usuarios.dat")));
            for (Usuario usu : usuarios) {
                if (usu.getNick().equalsIgnoreCase(usuario.getNick())) {
                    usu.setConectado(false);
                }
                objOS.writeObject(usu);
            }
            objOS.close();

        } catch (IOException ignored) {
            System.out.println("[Fichero] Error al activar el nuevo como conectado -> " + usuario.getNick());
            GuardarLogs.logger.log(Level.SEVERE, "Error al activar el nuevo como conectado -> " + usuario.getNick());
        }
    }
    */

}
