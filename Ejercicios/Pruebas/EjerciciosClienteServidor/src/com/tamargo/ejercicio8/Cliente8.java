package com.tamargo.ejercicio8;

import java.io.*;
import java.net.Socket;

public class Cliente8 {

    public static void main(String[] args) {

    }

    public void ejecutarCliente() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        try {
            // Solo se conecta
            Socket socket = new Socket("localhost", 5600);
            DataInputStream datoEntrada = new DataInputStream(socket.getInputStream());
            DataOutputStream datoSalida = new DataOutputStream(socket.getOutputStream());


            System.out.println("El server saluda: " + datoEntrada.readUTF());
            boolean bucle = true;
            while (bucle) {
                System.out.print("El server dice: " + datoEntrada.readUTF());
                String mensaje = br.readLine();
                datoSalida.writeUTF(mensaje);
                bucle = datoEntrada.readBoolean();
                if (bucle) {
                    System.out.println("El server responde: " + datoEntrada.readUTF());
                }
            }

            bucle = true;
            while (bucle) {
                System.out.print("El server dice: " + datoEntrada.readUTF());
                datoSalida.writeUTF(br.readLine());
                bucle = datoEntrada.readBoolean();
                if (bucle) {
                    System.out.println("El server responde: " + datoEntrada.readUTF());
                }
            }

            bucle = true;
            while (bucle) {
                System.out.print("El server dice: " + datoEntrada.readUTF());
                String mensaje = br.readLine();
                datoSalida.writeUTF(mensaje);


                if (mensaje.equalsIgnoreCase("fin")) {
                    bucle = false;
                    System.out.println("El server se despide: " + datoEntrada.readUTF());
                } else
                    System.out.println("El server responde: " + datoEntrada.readUTF());
            }

            socket.close();

        } catch (IOException e) {
            System.out.println("[Cliente] el server ha rechazado la conexión");
        }
    }

}
