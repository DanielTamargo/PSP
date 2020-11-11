package com.tamargo.clientes;

import com.tamargo.utilidades.ComprobarFichero;
import com.tamargo.utilidades.ConsoleColors;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;

import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ClienteFTP {

    private DataInputStream dataIS;
    private DataOutputStream dataOS;
    private Socket socketFTP;

    private final String nombre = "[Cliente FTP] ";
    private final File carpetaDescargas = new File("./archivos/cliente/descargas");

    private int opcion;

    public ClienteFTP(int opcion) {
        this.opcion = opcion;
    }

    public void ejecutarClienteFTP(File fichero) {

        try {
            InetAddress direccion = InetAddress.getByName("localhost"); //ip a la que se conectará

            socketFTP = new Socket(direccion, 21);
            socketFTP.setSoTimeout(4000); // Tiempo para intentar conectar (4s)

            // Flujos entrada y salida datos
            dataIS = new DataInputStream(socketFTP.getInputStream());
            dataOS = new DataOutputStream(socketFTP.getOutputStream());

            if (opcion == 1)
                descargarFichero();
            else
                subirFichero(fichero);

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



    public void subirFichero(File fichero) {
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

    public void descargarFichero() {
        try {
            // Recibimos nombre del fichero y tamaño
            String nombreFichero = dataIS.readUTF();
            int tamanyoFichero = dataIS.readInt();
            String tamanyo;
            if (tamanyoFichero < 1024)
                tamanyo = (float) tamanyoFichero + " bytes";
            else if (tamanyoFichero < (1024 * 1024))
                tamanyo = String.format("%.2f",((float) tamanyoFichero / 1024)) + " Kb";
            else if (tamanyoFichero < (1024 * 1024 * 1024))
                tamanyo = String.format("%.2f",((float) tamanyoFichero / (1024 * 1024))) + " Mb";
            else
                tamanyo = String.format("%.2f",((float) tamanyoFichero / (1024 * 1024 * 1024))) + " Gb";

            System.out.println(nombre + "Recibiendo el fichero " + nombreFichero + " ("
                    + tamanyo + ") del Servidor");

            String nombreFicheroFinal = new ComprobarFichero().nombreFichero(carpetaDescargas, nombreFichero);
            if (!nombreFicheroFinal.equalsIgnoreCase(nombreFichero))
                System.out.println(nombre + "El fichero '" + nombreFichero + "' ya existe, renombrando a '"
                        + nombreFicheroFinal + "'");
            System.out.println(nombre + "Guardando el archivo en: " + carpetaDescargas.getPath() + "\\"
                    + nombreFicheroFinal);

            // Preparamos el medio para recibirlo
            BufferedInputStream bis = new BufferedInputStream(socketFTP.getInputStream());
            byte[] buffer = new byte[tamanyoFichero];

            try {
                long initialMax;
                String unitName;
                long unitSize;

                if (tamanyo.contains("byte")) {
                    initialMax = tamanyoFichero;
                    unitName = " bytes";
                    unitSize = 1L;
                } else if (tamanyo.contains("Kb")) {
                    initialMax = tamanyoFichero / 1024L;
                    unitName = " KBs";
                    unitSize = 1024L;
                } else if (tamanyo.contains("Mb")) {
                    initialMax = tamanyoFichero / (1024L * 1024L);
                    unitName = " MBs";
                    unitSize = 1024L * 1024L;
                } else {
                    initialMax = tamanyoFichero / (1024L * 1024L * 1024L);
                    unitName = " GBs";
                    unitSize = 1024L * 1024L * 1024L;
                }

                System.out.print(ConsoleColors.CYAN_BRIGHT);
                ProgressBar pb = new ProgressBar("Descarga:", initialMax, 20,
                        System.out, ProgressBarStyle.ASCII, unitName, 1L, false,
                        (DecimalFormat)null, ChronoUnit.SECONDS, 0L, Duration.ZERO);
                int n = 1;
                for (int i = 0; i < buffer.length; i++) {
                    buffer[i] = (byte) bis.read(); // Lo leemos y guardamos en un buffer
                    if (unitSize == n) {
                        pb.step();
                        n = 0;
                    }
                    n++;
                }
                pb.close();
                System.out.print("Descarga completada, guardando el fichero en el disco duro...");
                System.out.print(ConsoleColors.RESET);
            } catch (Exception ignored) { }

            // Preparamos el medio para volcarlo en el fichero
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(carpetaDescargas, nombreFicheroFinal)));

            // Volcamos lo leído en el fichero
            bos.write(buffer);

            // Cerramos los nuevos flujos de datos
            bos.flush();
            bis.close();
            bos.close();

            System.out.println("\r" + nombre + "Fichero '" + nombreFicheroFinal + "' descargado del Servidor con éxito");

        } catch (IOException ignored) {
            System.out.println("\r" + nombre + "Error al guardar el fichero");
        }
    }

}
