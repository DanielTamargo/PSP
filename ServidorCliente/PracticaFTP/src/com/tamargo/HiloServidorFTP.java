package com.tamargo;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class HiloServidorFTP extends Thread {

    private final Socket cliente;
    private final int numCliente;
    private final String nombre;

    private DataInputStream dataIS;
    private DataOutputStream dataOS;

    private final File carpetaArchivos = new File("./archivos");
    private ArrayList<File> ficheros = new ArrayList<>();

    public HiloServidorFTP(Socket cliente, int numCliente, String nombre) {
        this.cliente = cliente;
        this.numCliente = numCliente;
        this.nombre = nombre;

        try {
            dataIS = new DataInputStream(cliente.getInputStream());
            dataOS = new DataOutputStream(cliente.getOutputStream());
        } catch (IOException ignored) { }
    }

    @Override
    public void run() {
        if (cliente != null && dataIS != null & dataOS != null) {
            String opciones = "Saludos Cliente " + numCliente + ", por favor seleccione una de las siguientes opciones.\n" +
                    "1) Descargar archivo.\n" +
                    "2) Subir archivo.\n" +
                    "3) Salir.\n" +
                    "Opción: ";
            boolean bucle = true;
            try {
                //while (bucle) {
                    // Pedimos la opción (descargar archivo, subir archivo o salir)
                    System.out.println(nombre + "Enviando opciones al Cliente " + numCliente);
                    int respuesta = opcionValida(opciones, 3);

                    // Procesamos la opción
                    switch (respuesta) {
                        case 1 -> { // Descargar archivo
                            // Mostrar listado y pedir que seleccione uno
                            System.out.println(nombre + "Enviando lista ficheros al Cliente " + numCliente);
                            int numFichero = opcionValida(listadoArchivos().toString(), numArchivos());

                            // Mandar archivo (el cliente lo descarga)
                            mandarArchivo(numFichero);
                        }
                        case 2 -> { // Subir archivo
                            // Recibir archivo (el cliente lo sube)
                            // Almacenarlo en la carpeta archivos
                            recibirFichero();

                        }
                        case 3 -> { // Despedirse y salir del bucle
                            bucle = false;
                            dataOS.writeUTF("¡Adiós Cliente " + numCliente + "!");
                            System.out.println(nombre + "Despidiéndome del Cliente " + numCliente);
                        }
                    }
                //}
            } catch (IOException ignored) {
                // TODO volver a poner el bucle y comprobar si salen errores aquí
            }
        } else {
            System.out.println(nombre + "Error al generar los flujos de entrada y salida de datos.\n" +
                    "\tForzando desconexión con el Cliente " + numCliente);
        }
        try {
            dataIS.close();
            dataOS.close();
            if (cliente != null)
                cliente.close();
            System.out.println(nombre + "Conexión finalizada con el Cliente " + numCliente);
        } catch (IOException ignored) { }
    }


    /**
     * Recibe y almacena un fichero, utiliza la clase ComprobarFichero que comprueba si el nombre del fichero está
     * disponible, y si no lo está, añadirá entre paréntesis el primer número disponible para que el fichero sea único.
     */
    public void recibirFichero() {
        try {
            // Recibimos nombre del fichero y tamaño
            String nombreFichero = dataIS.readUTF();
            int tamanyoFichero = dataIS.readInt();
            System.out.println(nombre + "Recibiendo el fichero " + nombreFichero + " ("
                    + tamanyoFichero + ") del Cliente " + numCliente);

            String nombreFicheroFinal = new ComprobarFichero().nombreFichero(carpetaArchivos, nombreFichero);
            if (!nombreFicheroFinal.equalsIgnoreCase(nombreFichero))
                System.out.println(nombre + "El fichero '" + nombreFichero + "' ya existe, renombrando a '"
                    + nombreFicheroFinal + "'");
            System.out.println(nombre + "Guardando el archivo en: " + carpetaArchivos.getPath() + "\\"
                    + nombreFicheroFinal);

            // Preparamos el medio para recibirlo
            BufferedInputStream bis = new BufferedInputStream(cliente.getInputStream());
            byte[] buffer = new byte[tamanyoFichero];

            // Lo leemos y guardamos en un buffer
            for (int i = 0; i < buffer.length; i++) {
                buffer[i] = (byte) bis.read();
            }

            // Preparamos el medio para volcarlo en el fichero
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(carpetaArchivos, nombreFicheroFinal)));

            // Volcamos lo leído en el fichero
            bos.write(buffer);

            // Cerramos los nuevos flujos de datos
            bos.flush();
            bis.close();
            bos.close();

            System.out.println(nombre + "Fichero '" + nombreFicheroFinal
                    + "' recibido por el Cliente " + numCliente + " con éxito");

        } catch (IOException ignored) { }
    }

    /**
     * Manda un fichero al cliente. Se le mandan nombre, tamaño y contenido del fichero para que el cliente pueda
     * generar una copia del fichero
     * @param numFichero int recibido por el cliente para elegir qué fichero quiere descargar
     */
    public void mandarArchivo(int numFichero) {
        File fichero = ficheros.get(numFichero - 1); // -1 porque el ArrayList comienza desde el 0 y mostramos las opciones desde 1
        int tamanyoFichero = (int) fichero.length();

        try {
            // Mandamos nombre y tamaño fichero
            dataOS.writeUTF(fichero.getName());
            dataOS.writeInt(tamanyoFichero);

            // Preparamos el contenido del fichero para poder mandarlo
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fichero));
            byte[] bufferFichero = new byte[tamanyoFichero];
            bis.read(bufferFichero);

            // Preparamos el medio para mandarlo
            BufferedOutputStream bos = new BufferedOutputStream(cliente.getOutputStream());

            // Lo mandamos
            for (int i = 0; i < bufferFichero.length; i++) {
                bos.write(bufferFichero[i]);
            }

            // Cerramos los nuevos flujos de datos
            bis.close();
            bos.close();

            System.out.println(nombre + "Fichero '" + fichero.getName() + "' enviado al Cliente " + numCliente);

        } catch (IOException ignored) { }
    }


    /**
     * Devuelve un StringBuilder con el listado de los ficheros que contiene la carpeta './archivos';
     * También, el método actualizará el ArrayList que contendrá los ficheros para cuando el cliente elija uno,
     *    podamos acceder a él al momento
     * @return sb <- StringBuilder con el listado
     */
    public StringBuilder listadoArchivos() {
        ficheros = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        int n = 0;
        sb.append("Lista de archivos del servidor:").append("\n");
        if (carpetaArchivos.exists()) {
            for (File file : carpetaArchivos.listFiles()) {
                if (file.isFile()) {
                    ficheros.add(file);
                    n++;
                    sb.append(n).append(") ").append(file.getName()).append(" (").append(file.length()).append(")").append("\n");
                }
            }
        }
        sb.append("Eliges el archivo: ");
        return sb;
    }

    /**
     * Devuelve un int con el número de ficheros que hay dentro de la carpeta './archivos';
     * @return devuelve el número de ficheros
     */
    public int numArchivos() {
        int numArchivos = 0;
        if (carpetaArchivos.exists()) {
            for (File file : carpetaArchivos.listFiles()) {
                if (file.isFile())
                    numArchivos++;
            }
            return numArchivos;
        } else {
            carpetaArchivos.mkdirs();
            return numArchivos();
        }
    }

    /**
     * Da a escoger entre distintas opciones al cliente que deberá mandar un número (aunque lo mandará con writeUTF)
     *    para elegir la opción, si el cliente se equivoca, el server se lo notificará y pedirá de nuevo
     * @param opciones <- opciones a mostrar para que el cliente elija
     * @param limite <- límite que controlará que el cliente no escribe un número mayor a las opciones disponibles
     * @return devuelve la opción que haya escogido una vez sea válida
     */
    public int opcionValida(String opciones, int limite) {
        boolean error = true;
        int respuesta = 0;
        try {
            while (error) {
                dataOS.writeUTF(opciones);
                error = false;
                try {
                    String respuestaRecibida = dataIS.readUTF();
                    System.out.println(nombre + "Respuesta recibida por el Cliente " + numCliente + ": " + respuestaRecibida);
                    respuesta = Integer.parseInt(respuestaRecibida);
                } catch (NumberFormatException ignored) {
                    error = true;
                }
                if (respuesta < 1 || respuesta > limite)
                    error = true;

                if (error) {
                    dataOS.writeBoolean(true);
                    dataOS.writeUTF("Error, debes introducir un número que señale una opción válida.");
                    System.out.println(nombre + "Notificándole el error al Cliente " + numCliente);
                } else {
                    dataOS.writeBoolean(false);
                }
            }
        } catch (IOException ignored) { }
        return respuesta;
    }

}
