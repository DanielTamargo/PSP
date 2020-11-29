package com.tamargo.datos;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class EscribirFicheros {

    /**
     * Añade un usuario al fichero usuarios.dat
     */
    public static boolean addUsuario(Usuario usuario) {
        boolean insertado = true;

        try {
            ArrayList<Usuario> usuarios = LeerFicheros.leerUsuarios();
            usuarios.add(usuario);
            ObjectOutputStream objOS = new ObjectOutputStream(new FileOutputStream(new File("./ficheros/usuarios.dat")));
            for (Usuario usu : usuarios) {
                objOS.writeObject(usu);
            }
            objOS.close();
            System.out.println("[Fichero] Usuario '" + usuario.getNombre() + "' guardado con éxito");
        } catch (IOException ignored) {
            System.out.println("[Fichero] Error al guardar un nuevo usuario");
            insertado = false;
        }

        return insertado;
    }

    public static boolean addPregunta(Pregunta pregunta) {
        boolean insertada = true;

        try {
            ArrayList<Pregunta> preguntas = LeerFicheros.leerPreguntas();
            preguntas.add(pregunta);
            ObjectOutputStream objOS = new ObjectOutputStream(new FileOutputStream(new File("./ficheros/preguntas.dat")));
            for (Pregunta preg : preguntas) {
                objOS.writeObject(preg);
            }
            objOS.close();
            System.out.println("[Fichero] Pregunta '" + pregunta.getTitulo() + "' guardada con éxito");
        } catch (IOException ignored) {
            System.out.println("[Fichero] Error al guardar una nueva pregunta");
            insertada = false;
        }

        return insertada;
    }

}
