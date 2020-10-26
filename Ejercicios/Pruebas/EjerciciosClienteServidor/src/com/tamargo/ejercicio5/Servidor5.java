package com.tamargo.ejercicio5;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Servidor5 {

    public static void main(String[] args) throws IOException {


        String mensaje;
        boolean bucle = true;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        try {
            MulticastSocket servidor = new MulticastSocket();
            while (bucle) {
                System.out.print("[Server] mensaje a enviar al grupo: ");
                mensaje = br.readLine();

                byte[] dato = mensaje.getBytes();
                DatagramPacket dgp = new DatagramPacket(dato, dato.length, InetAddress.getByName("230.0.0.1"), 55557);
                servidor.send(dgp);
                System.out.println("[Server] mensaje mandado: " + mensaje);
                System.out.println();
                if (mensaje.equalsIgnoreCase("fin")) {
                    bucle = false;
                }
            }

            servidor.close();
            System.out.println("[Server] cerrando servidor");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
