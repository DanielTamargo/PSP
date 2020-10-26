package com.tamargo.ejercicio7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class Servidor7 {

    public static void main(String[] args) {

        try {
            MulticastSocket serverSocket = new MulticastSocket(55557);
            System.out.println("[Server] servidor encendido");

            int num1 = new Random().nextInt(7) + 1;
            int num2 = new Random().nextInt(7) + 1;

            System.out.println();
            byte[] numero1 = String.valueOf(num1).getBytes();
            byte[] numero2 = String.valueOf(num2).getBytes();
            DatagramPacket dgp = new DatagramPacket(numero1, numero1.length, InetAddress.getByName("230.0.0.1"), 55557);
            serverSocket.send(dgp);
            dgp = new DatagramPacket(numero2, numero2.length, InetAddress.getByName("230.0.0.1"), 55557);
            serverSocket.send(dgp);

            System.out.println("[Server] números enviados: " + num1 + ", " + num2);
            MulticastSocket escucha = new MulticastSocket(55557);
            escucha.joinGroup(InetAddress.getByName("230.0.0.1"));

            byte[] dato = new byte [1024];
            dgp = new DatagramPacket(dato, dato.length);
            escucha.receive(dgp);
            byte[] data = dgp.getData();
            String solucion = new String(data, StandardCharsets.UTF_8).trim();

            System.out.println("[Server] solución recibida: " + solucion);
            System.out.println();

            serverSocket.close();
            System.out.println("[Server] servidor apagado");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
