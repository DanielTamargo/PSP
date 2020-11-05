package com.tamargo.servidores;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class HiloServidor extends Thread {

    private Socket socket;
    private ServidorFTP serverFTP;
    private String nombre;
    private int numCliente;

    private DataOutputStream dataOS;
    private DataInputStream dataIS;

    private final File carpetaArchivos = new File("./archivos");
    private ArrayList<File> ficheros = new ArrayList<>();

    public HiloServidor(Socket socket, ServidorFTP serverFTP, String nombre, int numCliente) {
        this.socket = socket;
        this.serverFTP = serverFTP;
        this.nombre = nombre;
        this.numCliente = numCliente;
    }

    @Override
    public void run() {

        try {
            dataOS = new DataOutputStream(socket.getOutputStream());
            dataIS = new DataInputStream(socket.getInputStream());
        } catch (IOException ignored) { }

        if (socket != null && dataIS != null & dataOS != null) {
            String opciones = "Saludos Cliente " + numCliente + ", por favor seleccione una de las siguientes opciones.\n" +
                    "1) Descargar archivo.\n" +
                    "2) Subir archivo.\n" +
                    "3) Salir.\n" +
                    "Opción: ";
            boolean bucle = true;
            try {
                while (bucle) {
                    // Pedimos la opción (descargar archivo, subir archivo o salir)
                    System.out.println(nombre + "Enviando opciones al Cliente " + numCliente);
                    int opcion = opcionValida(opciones, 3);

                    // Procesamos la opción
                    switch (opcion) {
                        case 1 -> { // Descargar archivo (el cliente elige un archivo del listado y se lo enviamos)
                            System.out.println(nombre + "Enviando lista ficheros al Cliente " + numCliente);
                            int numFichero = opcionValida(listadoArchivos().toString(), numArchivos());

                            dataOS.writeBoolean(serverFTP.comprobarServidorFTP());
                            if (serverFTP.comprobarServidorFTP())
                                serverFTP.ejecutarServidorFTP(numCliente, opcion, numFichero, ficheros);
                            else
                                System.out.println(nombre + "Error con el servicio FTP, no se ha podido enviar el fichero");

                        }
                        case 2 -> { // Subir archivo (el cliente sube archivo, aquí lo recibimos y almacenamos en la carpeta archivos)
                            dataOS.writeBoolean(serverFTP.comprobarServidorFTP());
                            if (serverFTP.comprobarServidorFTP())
                                serverFTP.ejecutarServidorFTP(numCliente, opcion, 1, null);
                            else
                                System.out.println(nombre + "Error con el servicio FTP, no se ha podido recibir el fichero");

                        }
                        case 3 -> { // Despedirse y salir del bucle
                            bucle = false;
                            dataOS.writeUTF("¡Adiós Cliente " + numCliente + "!");
                            System.out.println(nombre + "Despidiéndome del Cliente " + numCliente);
                        }
                    }

                    // Si el cliente se desconecta sin elegir la opción 3, nos quedaríamos con el hilo
                    //      en el bucle infinito, acumulando hilos que nunca mueren y saturarían la memoria
                    if (!socket.getKeepAlive() && opcion != 2 && opcion != 1)
                        bucle = false;
                }
            } catch (IOException ignored) {
                System.out.println(nombre + "Error con la conexión con el Cliente " + numCliente);
            }
        } else {
            System.out.println(nombre + "Error al generar los flujos de entrada y salida de datos.\n" +
                    "\tForzando desconexión con el Cliente " + numCliente);
        }
        try {
            dataIS.close();
            dataOS.close();
            if (socket != null)
                socket.close();
            System.out.println(nombre + "Conexión finalizada con el Cliente " + numCliente);
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
