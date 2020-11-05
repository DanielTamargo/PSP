package com.tamargo.utilidades;

import java.io.File;
import java.io.IOException;

public class ComprobarFichero {

    /**
     * Constructor vacío puesto que solo queremos utilizar su método synchronized (que este a la vez se apoya en los otros métodos de la clase)
     */
    public ComprobarFichero() { }

    /**
     * Método synchronized que devolverá un nombre único para que el fichero no reemplace a otro
     * @param directorio <- carpeta (ruta)
     * @param nomFichero <- nombre del fichero a comprobar si es único y si no lo es devolver uno que sí lo sea
     * @return devuelve un nombre único
     */
    public synchronized String nombreFichero(File directorio, String nomFichero) {
        String ruta = directorio.getPath() + "/" + nomFichero;
        if (!comprobarNombreUnico(ruta))
            nomFichero = devolverNombreUnico(directorio, nomFichero);

        return nomFichero;
    }

    /**
     * Comprueba que el nombre sea único
     * @param ruta comprueba la ruta entera
     * @return devuelve true si es único y false si no lo es
     */
    public boolean comprobarNombreUnico(String ruta) {
        File f = new File(ruta);
        try {
            return !f.exists();
        } catch (Exception ignored) {
            return false;
        }
    }

    /**
     * Devuelve un nombre único, ya sea bien porque ya lo era directamente o porque ha tenido que generarlo
     * @param directorio <- carpeta donde está el fichero
     * @param nomFichero <- nombre del fichero
     * @return devuelve el nombre del fichero
     */
    public String devolverNombreUnico(File directorio, String nomFichero) {
        String ruta = directorio.getPath() + "/" + nomFichero;
        if (comprobarNombreUnico(ruta))
            return nomFichero;
        else {
            for (int i = 0; i < 9999; i++) {
                String nombre;
                String extension;
                try {
                    nombre = nomFichero.substring(0, nomFichero.lastIndexOf('.'));
                    extension = nomFichero.substring(nomFichero.lastIndexOf('.'));
                } catch (IndexOutOfBoundsException ignored) {
                    return nomFichero;
                }
                ruta = directorio.getPath() + "/" + nombre + "(" + i + ")" + extension;
                if (comprobarNombreUnico(ruta)) {
                        return nombre + "(" + i + ")" + extension;
                }
            }
        }
        return nomFichero;
    }

}
