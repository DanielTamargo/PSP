package com.tamargo;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Cliente {

    public static DataInputStream dataIS;
    public static DataOutputStream dataOS;
    public static Socket socket;

    public static void main(String[] args) {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String nombre = "[Cliente] ";
        File carpetaArchivos = new File("./copias");

        try {
            InetAddress direccion = InetAddress.getByName("localhost"); //ip a la que se conectará

            socket = new Socket(direccion, 21);
            //socket.setSoTimeout(4000); // Tiempo para intentar conectar (4s)

            // Flujos entrada y salida datos
            dataIS = new DataInputStream(socket.getInputStream());
            dataOS = new DataOutputStream(socket.getOutputStream());

            boolean bucle = true;
            //while (bucle) {
                // Enviamos opciones hasta tener una opción válida
                boolean error = true;
                String opcionStr = "";
                while (error) {
                    try {
                        System.out.print(nombre + "Mensaje del Servidor:\n" + dataIS.readUTF());
                        opcionStr = br.readLine();
                        dataOS.writeUTF(opcionStr);

                        error = dataIS.readBoolean();
                        if (error)
                            System.out.println(nombre + "Mensaje del Servidor:\n" + dataIS.readUTF());
                        System.out.println();
                    } catch (EOFException ignored) { }
                }

                // Llegaremos aquí cuando el server haya dado un visto bueno a la opción
                int opcion = Integer.parseInt(opcionStr);

                switch (opcion) {
                    case 1 -> {
                        // Descargar archivo (elegir uno y descargarlo)
                        error = true;
                        while (error) {
                            System.out.print(nombre + "Mensaje del Servidor:\n" + dataIS.readUTF());
                            opcionStr = br.readLine();
                            dataOS.writeUTF(opcionStr);

                            error = dataIS.readBoolean();
                            if (error)
                                System.out.println(nombre + "Mensaje del Servidor:\n" + dataIS.readUTF());
                            System.out.println();
                        }

                        descargarFichero(nombre, carpetaArchivos);
                    }
                    case 2 -> {
                        // Subir archivo (subir el fichero por defecto)
                        subirFichero(nombre);
                    }
                    case 3 -> {
                        // Salir
                        bucle = false;
                        System.out.println(nombre + "Mensaje del Servidor:\n" + dataIS.readUTF());
                    }
                }
            //}
            dataIS.close();
            dataOS.close();
            socket.close();
        } catch (IOException ignored) {
            ignored.printStackTrace();
            System.out.println(nombre + "Error al conectar con el Servidor (timeout)");
        }
    }

    public static void subirFichero(String nombre) {
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
            BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());

            // Lo mandamos
            for (int i = 0; i < bufferFichero.length; i++) {
                bos.write(bufferFichero[i]);
            }

            // Cerramos los nuevos flujos de datos
            bis.close();
            bos.close();

            System.out.println(nombre + "Fichero '" + fichero.getName() + "' enviado al Servidor");

        } catch (IOException ignored) { }
    }

    public static void descargarFichero(String nombre, File carpetaArchivos) {
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
            BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
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
