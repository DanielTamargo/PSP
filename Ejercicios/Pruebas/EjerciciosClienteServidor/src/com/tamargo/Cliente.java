package com.tamargo;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class Cliente {

    public static void lanzarCliente(int ejercicio) {
        try {
            switch (ejercicio) {
                case 1 -> cliente1();
                case 2 -> cliente2();
                case 3 -> cliente3();
                case 4 -> cliente4();
                case 5 -> cliente5();
            }
        } catch (IOException ignored) {}
    }

    public static void main(String[] args) throws IOException {

    }


    public static void cliente1() throws IOException {
        //No hace nada en este ejercicio
    }

    public static void cliente2() throws IOException {

        try {
            // Solo se conecta
            Socket socket = new Socket("localhost", 5600);

            //DataOutputStream datoSalida = new DataOutputStream(cliente.getOutputStream());
            //datoSalida.writeUTF("Mensaje");

            DataInputStream datoEntrada = new DataInputStream(socket.getInputStream());
            try {
                System.out.println("\tCliente: " + datoEntrada.readUTF());
            } catch (EOFException e) {
                System.out.println("\tCliente: respuesta errónea del servidor");
            }
            socket.close();
        } catch (IOException e) {
            System.out.println("\tCliente: el server ha rechazado la conexión");
        }

    }

    public static void cliente3() {
        boolean bucle = true;

        try {
            while (bucle) {
                // Solo se conecta
                Socket socket = new Socket("localhost", 5600);

                DataOutputStream datoSalida = new DataOutputStream(socket.getOutputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                System.out.print("\tCliente: mensaje a enviar: ");
                String mensaje = br.readLine();
                datoSalida.writeUTF(mensaje);
                if (mensaje.equalsIgnoreCase("fin"))
                    bucle = false;
                else {
                    DataInputStream datoEntrada = new DataInputStream(socket.getInputStream());
                    System.out.println("\tCliente: mensaje del servidor '" + datoEntrada.readUTF() + "'");
                }
                socket.close();
            }
        } catch (IOException e) {
            System.out.println("\tCliente: el server ha rechazado la conexión");
        }

    }

    public static void cliente4() {
        boolean bucle = true;

        try {
            while (bucle) {
                // Solo se conecta
                Socket socket = new Socket("localhost", 5600);

                DataOutputStream datoSalida = new DataOutputStream(socket.getOutputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                System.out.print("\tCliente: escribe fin para acabar: ");
                String mensaje = br.readLine();
                datoSalida.writeUTF(mensaje);
                if (mensaje.equalsIgnoreCase("fin"))
                    bucle = false;
                else {
                    DataInputStream datoEntrada = new DataInputStream(socket.getInputStream());
                    System.out.println("\tCliente: hora:      " + datoEntrada.readUTF());
                    System.out.println("\tCliente: puerto:    " + datoEntrada.readInt());
                    System.out.println("\tCliente: dirección: " + datoEntrada.readUTF());
                    System.out.println();
                }

                socket.close();
            }
        } catch (IOException e) {
            System.out.println("\tCliente: el server ha rechazado la conexión");
        }
    }

    public static void cliente5() {
        boolean bucle = true;
        String nombreHilo = Thread.currentThread().getName();

        try {
            MulticastSocket escucha = new MulticastSocket(55557);
            escucha.joinGroup(InetAddress.getByName("230.0.0.1"));
            while (bucle) {
                byte[] dato = new byte [1024];
                DatagramPacket dgp = new DatagramPacket(dato, dato.length);
                escucha.receive(dgp);
                byte[] data = dgp.getData();
                String mensaje = new String(data, StandardCharsets.UTF_8);
                if (mensaje.equalsIgnoreCase("fin"))
                    bucle = false;
                else
                    System.out.println("\t\t\t\tCliente " + nombreHilo + ": mensaje recibido: " + mensaje);
            }
            escucha.close();
        } catch (IOException e) {
            System.out.println("\t\t\t\tCliente " + nombreHilo + ": el server ha rechazado la conexión");
        }
    }

}
