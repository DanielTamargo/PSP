package com.tamargo.servidores;

import com.tamargo.utilidades.ComprobarFichero;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class HiloServidorFTP extends Thread {

    private final Socket cliente;
    private final int numCliente;
    private final String nombre;
    private final int opcion;

    private DataInputStream dataIS;
    private DataOutputStream dataOS;

    private int numFichero = 1;

    private final File carpetaArchivos = new File("./archivos");
    private ArrayList<File> ficheros = new ArrayList<>();

    public HiloServidorFTP(Socket cliente, int numCliente, String nombre, int opcion) {
        this.cliente = cliente;
        this.numCliente = numCliente;
        this.nombre = nombre;
        this.opcion = opcion;

        try {
            dataIS = new DataInputStream(cliente.getInputStream());
            dataOS = new DataOutputStream(cliente.getOutputStream());
        } catch (IOException ignored) { }
    }

    @Override
    public void run() {
        if (opcion == 1) { //Enviar (el cliente descarga un fichero)
            enviarFichero(numFichero);
        } else { //Recibir (el cliente sube un fichero)
            recibirFichero();
        }
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
    public void enviarFichero(int numFichero) {
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

    public void setNumFichero(int numFichero) {
        this.numFichero = numFichero;
    }

    public void setFicheros(ArrayList<File> ficheros) {
        this.ficheros = ficheros;
    }
}
