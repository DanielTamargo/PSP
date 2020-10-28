package com.tamargo.ejercicio5;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Cliente5 {

    public void ejecutarCliente(int num) {

        boolean bucle = true;

        try {
            MulticastSocket escucha = new MulticastSocket(55557);
            System.out.println("[Cliente " + num + "] conexión realizada");
            System.out.println();
            escucha.joinGroup(InetAddress.getByName("230.0.0.1"));
            System.out.println("- Lista de mensajes:");
            while (bucle) {
                byte[] dato = new byte [1024];
                DatagramPacket dgp = new DatagramPacket(dato, dato.length);
                escucha.receive(dgp);
                byte[] data = dgp.getData();

                String mensaje = new String(data, StandardCharsets.UTF_8).trim();
                if (mensaje.equalsIgnoreCase("fin"))
                    bucle = false;

                System.out.println("[Cliente " + num + "] mensaje recibido: " + mensaje);
            }
            escucha.leaveGroup(InetAddress.getByName("230.0.0.1"));
            escucha.close();
            System.out.println("\n[Cliente " + num + "] desconectando del servidor (sin fallos)");
        } catch (IOException e) {
            System.out.println("\n[Cliente " + num + "] el server ha rechazado la conexión");
        }

    }

}
