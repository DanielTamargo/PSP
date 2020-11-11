package com.tamargo;

import java.io.*;
import java.util.logging.*;
import java.util.regex.Pattern;

public class Main {

    public static String rutaLog = "./logs/logEjercicio1.log";
    public static Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        ejercicio1();



    }

    public static void ejercicio1() {
        inicializarLog();

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String usuario = "";
        Pattern usuarioPattern = Pattern.compile("[a-z]{3,8}");

        try {
            boolean bucle = true;
            while (bucle) {
                System.out.print("Usuario (entre 3 y 8 caracteres): ");
                usuario = br.readLine();
                if (usuarioPattern.matcher(usuario).matches()) {
                    System.out.println("Usuario correcto. Sesión iniciada.");
                    logger.log(Level.FINE, "Usuario loggeado -> " + usuario);
                    bucle = false;
                } else {
                    System.out.println("Usuario incorrecto, inténtalo de nuevo");
                    logger.log(Level.WARNING, "Nombre de usuario incorrecto -> " + usuario);
                }
            }
        } catch (IOException e) {
            System.out.println("Error al introducir un texto");
            logger.log(Level.SEVERE, "Error al introducir un texto");
        }

        System.out.println();

        String fichero = "";
        Pattern ficheroPattern = Pattern.compile("[a-zA-Z0-9]{3,8}[.][a-zA-Z]{3}");

        try {
            boolean bucle = true;
            while (bucle) {
                System.out.print("Fichero (nombre.txt): ");
                fichero = br.readLine();
                if (ficheroPattern.matcher(fichero).matches()) {
                    logger.log(Level.FINE, "Fichero introducido correctamente -> " + fichero);
                    bucle = false;
                } else {
                    System.out.println("Nombre de fichero incorrecto, inténtalo de nuevo");
                    logger.log(Level.WARNING, "Nombre de fichero incorrecto -> " + fichero);
                }
            }
        } catch (IOException e) {
            System.out.println("Error al introducir un texto");
            logger.log(Level.SEVERE, "Error al introducir un texto");
        }

        File file = new File(fichero);
        if (file.exists()) {
            System.out.println("Contenido del fichero elegido:");
            logger.log(Level.FINE, "El fichero existe -> " + fichero);
            mostrarContenidoFichero(file);
        } else {
            System.out.println("El fichero no existe");
            logger.log(Level.SEVERE, "El fichero no existe -> " + fichero);
        }

        //mostrar log
        System.out.println("\nContenido del log:");
        File log = new File("./logs/logEjercicio1.log");
        mostrarContenidoFichero(log);

    }

    public static void mostrarContenidoFichero(File fichero) {
        try {
            String linea;
            BufferedReader br = new BufferedReader(new FileReader(fichero));
            while ((linea = br.readLine()) != null) {
                if (fichero.getName().contains(".log")) {
                    if (linea.startsWith("WARNING"))
                        System.out.print(CC.RED_BRIGHT);
                    else if (linea.startsWith("SEVERE"))
                        System.out.print(CC.RED);
                    else if (linea.startsWith("FINE"))
                        System.out.print(CC.CYAN_BRIGHT);
                }
                System.out.println(linea);

                System.out.print(CC.RESET);
            }
            br.close();
        } catch (EOFException ignored) {
            System.out.println("\nFin del fichero.");
        } catch (FileNotFoundException e) {
            logger.log(Level.WARNING, "Error al mostrar el contenido del fichero " + fichero.getName());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error al leer el contenido del fichero " + fichero.getName());
        }

    }

    public static void inicializarLog() {
        FileHandler fh;
        try {
            fh = new FileHandler(rutaLog, true);
            logger.setLevel(Level.ALL);
            logger.setUseParentHandlers(false);

            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

            logger.addHandler(fh);

        } catch (IOException e) {
            System.out.println("Error con el log");
        }
    }


}
