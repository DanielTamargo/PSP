package com.tamargo;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class Servidor {

    public static void lanzarServidor(int ejercicio) {
        try {
            switch (ejercicio) {
                case 1 -> servidor1();
                case 2 -> servidor2();
                case 3 -> servidor3();
                case 4 -> servidor4();
                case 5 -> servidor5();
            }
        } catch (IOException ignored) {}
    }

    public static void main(String[] args) throws IOException {

    }

    public static void servidor1() throws IOException {
        InetAddress[] direcciones = InetAddress.getAllByName("www.google.es");
        for (int i = 0; i < direcciones.length; i++) {
            System.out.println("Dirección tal cual:                " + direcciones[i]);
            System.out.println("Dirección .getAddress():           " + Arrays.toString(direcciones[i].getAddress()));
            System.out.println("Dirección .getHostName():          " + direcciones[i].getHostName());
            System.out.println("Dirección .getCanonicalHostName(): " + direcciones[i].getCanonicalHostName());
            System.out.println("Dirección .getHostAddress():       " + direcciones[i].getHostAddress());
        }
    }

    public static void servidor2() throws IOException {
        ServerSocket serverSocket = new ServerSocket(5600);
        int clientes = 0;

        System.out.println("Esperando a los clientes...");
        while (clientes < 3) {
            Socket socket = serverSocket.accept();
            DataOutputStream datoSalida = new DataOutputStream(socket.getOutputStream());
            clientes++;
            datoSalida.writeUTF("Hola cliente " + clientes);
            socket.close();
        }
        serverSocket.close();
    }

    public static void servidor3() throws IOException {
        ServerSocket serverSocket = new ServerSocket(5600);
        boolean bucle = true;
        while (bucle) {
            Socket socket = serverSocket.accept();
            DataOutputStream datoSalida = new DataOutputStream(socket.getOutputStream());
            DataInputStream datoEntrada = new DataInputStream(socket.getInputStream());

            String mensaje = datoEntrada.readUTF();
            if (mensaje.equalsIgnoreCase("fin"))
                bucle = false;
            else {
                System.out.println("Server: mensaje recibido '" + mensaje + "'");
                datoSalida.writeUTF(mensaje.toUpperCase());
            }
            socket.close();
        }
        serverSocket.close();
    }

    public static void servidor4() throws IOException {
        int puerto = 5600;
        byte[] msgARecibir = new byte[1024];
        byte[] msg = new byte[1024];
        DatagramSocket serverSocket = new DatagramSocket(puerto);
        System.out.println("[Server] servidor conectado");
        System.out.println();
        boolean bucle = true;
        while (bucle) {
            DatagramPacket recibido = new DatagramPacket(msgARecibir, msgARecibir.length);
            serverSocket.receive(recibido);

            String mensaje = new String(recibido.getData());

            System.out.println("[Server] Mensaje recibido: " + mensaje);
            System.out.println("[Server] Procedente de:    " + recibido.getSocketAddress());
            System.out.println("[Server] Procedente de:    " + recibido.getAddress());
            System.out.println("[Server] Puerto:           " + recibido.getPort());

            if (mensaje.equalsIgnoreCase("fin"))
                bucle = false;
            else {
                LocalDateTime hora = LocalDateTime.now();
                DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                String mensajeEnviar = "Hora del servidor: " + hora.format(formato);
                msg = mensajeEnviar.getBytes();

                DatagramPacket paquete = new DatagramPacket(msg, msg.length, recibido.getAddress(), recibido.getPort());

                serverSocket.send(paquete);

            }

        }
        serverSocket.close();
        System.out.println();
        System.out.println("[Server] desconectando servidor");
    }

    public static void servidor5() throws IOException {
        MulticastSocket servidor = new MulticastSocket();
        String mensaje;
        boolean bucle = true;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        while (bucle) {
            System.out.print("Servidor: mensaje a enviar al grupo: ");
            mensaje = br.readLine();
            System.out.println();

            byte[] dato = mensaje.getBytes();
            DatagramPacket dgp = new DatagramPacket(dato, dato.length, InetAddress.getByName("230.0.0.1"), 55557);
            servidor.send(dgp);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println();
            if (mensaje.equalsIgnoreCase("fin")) {
                bucle = false;
            }
        }

        servidor.close();

    }

}
