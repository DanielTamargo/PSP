package com.tamargo.clientes;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Objects;

public class Cliente {

    public static void main(String[] args) {

        String nombre = "[Cliente] ";
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        DataOutputStream dataOS;
        DataInputStream dataIS;

        try {
            Socket socket = new Socket("localhost", 5600);

            // Flujos entrada y salida datos
            dataIS = new DataInputStream(socket.getInputStream());
            dataOS = new DataOutputStream(socket.getOutputStream());

            boolean bucle = true;
            while (bucle) {

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
                    } catch (EOFException ignored) {
                        esperar(250);
                        System.out.println(nombre + "Servidor no responde, esperando...");
                    }
                }

                // Llegaremos aquí cuando el server haya dado un visto bueno a la opción
                int opcion = Integer.parseInt(opcionStr);

                switch (opcion) {
                    case 1 -> {
                        error = true;
                        while (error) { // Elegir correctamente un fichero
                            System.out.print(nombre + "Mensaje del Servidor:\n" + dataIS.readUTF());
                            opcionStr = br.readLine();
                            dataOS.writeUTF(opcionStr);

                            error = dataIS.readBoolean();
                            if (error)
                                System.out.println(nombre + "Mensaje del Servidor:\n" + dataIS.readUTF());
                            System.out.println();
                        }

                        if (dataIS.readBoolean()) {
                            ClienteFTP cliFTP = new ClienteFTP(opcion);
                            cliFTP.ejecutarClienteFTP(null);
                        } else
                            System.out.println(nombre + "Error al conectar con el servicio FTP del servidor");
                    }
                    case 2 -> {
                        if (dataIS.readBoolean()) {
                            ClienteFTP cliFTP = new ClienteFTP(opcion);
                            cliFTP.ejecutarClienteFTP(elegirFichero());
                        } else
                            System.out.println(nombre + "Error al conectar con el servicio FTP del servidor");
                    }
                    case 3 -> {
                        bucle = false;
                        System.out.println(nombre + "Mensaje del Servidor:\n" + dataIS.readUTF());
                    }
                }
                System.out.println();
            }
            dataIS.close();
            dataOS.close();
            socket.close();
        } catch (
                SocketException ex) {
            if (ex.getLocalizedMessage().contains("reset"))
                System.out.println(nombre + "Error, conexión con el Servidor reiniciada y/o perdida");
            else
                System.out.println(nombre + "Error: " + ex.getLocalizedMessage());
        } catch (
                IOException ignored) {
            System.out.println(nombre + "Error al conectar con el Servidor (timeout)");
        }

    }

    public static File elegirFichero() {
        File fichero;

        File carpetaSubidas = new File("./archivos/cliente/subir");
        ArrayList<File> ficheros = new ArrayList<>();
        for (File f: Objects.requireNonNull(carpetaSubidas.listFiles())) {
            if (f.isFile())
                ficheros.add(f);
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int opcion = 0;
        boolean bucle = true;
        while (bucle) {
            System.out.println("Lista de archivos a subir:");
            for (int i = 0; i < ficheros.size(); i++) {
                File f = ficheros.get(i);
                int tamanyoFichero = (int)f.length();
                String tamanyo;
                if (tamanyoFichero < 1024)
                    tamanyo = (float) tamanyoFichero + " bytes";
                else if (tamanyoFichero < (1024 * 1024))
                    tamanyo = String.format("%.2f",((float) tamanyoFichero / 1024)) + " Kb";
                else if (tamanyoFichero < (1024 * 1024 * 1024))
                    tamanyo = String.format("%.2f",((float) tamanyoFichero / (1024 * 1024))) + " Mb";
                else
                    tamanyo = String.format("%.2f",((float) tamanyoFichero / (1024 * 1024 * 1024))) + " Gb";
                System.out.println((i + 1) + ") " + f.getName() + " (" + tamanyo + ")");
            }
            System.out.print("Opción: ");
            try {
                opcion = Integer.parseInt(br.readLine()) - 1;

                if (opcion >= 0 && opcion < ficheros.size())
                    bucle = false;
            } catch (NumberFormatException | IOException ignored) { }

            if (bucle)
                System.out.println("Error. Introduce una opción válida.\n");
        }

        fichero = ficheros.get(opcion);
        return fichero;
    }

    public static void esperar(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
