package com.tamargo.ejercicio7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Cliente7 {

    public static void main(String[] args) {

        try {
            MulticastSocket escucha = new MulticastSocket(55557);
            System.out.println("[Cliente] cliente conectado al servidor");
            escucha.joinGroup(InetAddress.getByName("230.0.0.1"));
            byte[] dato = new byte [1024];
            DatagramPacket dgp = new DatagramPacket(dato, dato.length);
            escucha.receive(dgp);
            byte[] data = dgp.getData();
            String n1 = new String(data, StandardCharsets.UTF_8).trim();
            escucha.receive(dgp);
            data = dgp.getData();
            String n2 = new String(data, StandardCharsets.UTF_8).trim();

            System.out.println();
            System.out.println("[Cliente] números recibidos: " + n1 + ", " + n2);
            int sol = Integer.parseInt(n1) * Integer.parseInt(n2);
            System.out.println("[Cliente] solución enviada: " + sol);

            byte[] solucion = String.valueOf(sol).getBytes();
            dgp = new DatagramPacket(solucion, solucion.length, InetAddress.getByName("230.0.0.1"), 55557);
            escucha.send(dgp);

            System.out.println("[Cliente] cliente desconectado del servidor");

        } catch (IOException e) {
            System.out.println("[Cliente] error al conectar con el servidor");
        }
    }

}
