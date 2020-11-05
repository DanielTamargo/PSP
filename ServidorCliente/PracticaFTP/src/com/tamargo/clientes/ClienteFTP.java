package com.tamargo.clientes;

import com.tamargo.utilidades.ComprobarFichero;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

public class ClienteFTP {


    private DataInputStream dataIS;
    private DataOutputStream dataOS;
    private Socket socketFTP;

    private final String nombre = "[Cliente FTP] ";
    private final File carpetaArchivos = new File("./copias");

    private int opcion;

    public ClienteFTP(int opcion) {
        this.opcion = opcion;
    }

    public void ejecutarClienteFTP() {

        try {
            InetAddress direccion = InetAddress.getByName("localhost"); //ip a la que se conectará

            socketFTP = new Socket(direccion, 21);
            socketFTP.setSoTimeout(4000); // Tiempo para intentar conectar (4s)

            // Flujos entrada y salida datos
            dataIS = new DataInputStream(socketFTP.getInputStream());
            dataOS = new DataOutputStream(socketFTP.getOutputStream());

            if (opcion == 1)
                descargarFichero(carpetaArchivos);
            else
                subirFichero(); //TODO ¿posible listado para elegir cuál subir?

            dataIS.close();
            dataOS.close();
            socketFTP.close();
        } catch (SocketException ex) {
            if (ex.getLocalizedMessage().contains("reset"))
                System.out.println(nombre + "Error, conexión con el Servidor reiniciada");
            else
                System.out.println(nombre + "Error: " + ex.getLocalizedMessage());
        } catch (IOException ignored) {
            System.out.println(nombre + "Error al conectar con el Servidor (timeout)");
        }
    }



    public void subirFichero() {
        File fichero = new File("./archivoscliente/subir1.txt");
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
            BufferedOutputStream bos = new BufferedOutputStream(socketFTP.getOutputStream());

            // Lo mandamos
            for (byte b : bufferFichero) {
                bos.write(b);
            }

            // Cerramos los nuevos flujos de datos
            bis.close();
            bos.close();

            System.out.println(nombre + "Fichero '" + fichero.getName() + "' enviado al Servidor");

        } catch (IOException ignored) { }
    }

    public void descargarFichero(File carpetaArchivos) {
        try {
            // Recibimos nombre del fichero y tamaño
            String nombreFichero = dataIS.readUTF();
            int tamanyoFichero = dataIS.readInt();
            System.out.println(nombre + "Recibiendo el fichero " + nombreFichero + " ("
                    + tamanyoFichero + ") del Servidor");

            String nombreFicheroFinal = new ComprobarFichero().nombreFichero(carpetaArchivos, nombreFichero);
            if (!nombreFicheroFinal.equalsIgnoreCase(nombreFichero))
                System.out.println(nombre + "El fichero '" + nombreFichero + "' ya existe, renombrando a '"
                        + nombreFicheroFinal + "'");
            System.out.println(nombre + "Guardando el archivo en: " + carpetaArchivos.getPath() + "\\"
                    + nombreFicheroFinal);

            // Preparamos el medio para recibirlo
            BufferedInputStream bis = new BufferedInputStream(socketFTP.getInputStream());
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

            System.out.println(nombre + "Fichero '" + nombreFicheroFinal + "' descargado del Servidor con éxito");

        } catch (IOException ignored) { }
    }

}
