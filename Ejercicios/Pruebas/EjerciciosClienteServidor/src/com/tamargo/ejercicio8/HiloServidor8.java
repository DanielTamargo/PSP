package com.tamargo.ejercicio8;

import java.io.*;
import java.net.Socket;

public class HiloServidor8 extends Thread {

    private Socket socket;
    private int clientes;

    private int num1 = 0;
    private int num2 = 0;

    public HiloServidor8(Socket socket, int clientes) {
        this.socket = socket;
        this.clientes = clientes;
    }

    @Override
    public void run() {
        try {
            boolean bucle = true;

            DataOutputStream datoSalida = new DataOutputStream(socket.getOutputStream());
            DataInputStream datoEntrada = new DataInputStream(socket.getInputStream());

            System.out.println("[Server] Saludando al Cliente " + clientes);
            datoSalida.writeUTF("¡Hola Cliente " + clientes + "!");

            while (bucle) {
                datoSalida.writeUTF("Por favor, introduce el número 1: ");
                try {
                    String num = datoEntrada.readUTF();
                    System.out.println("[Server] Número recibido: " + num + " (Cliente" + clientes + ")");
                    num1 = Integer.parseInt(num);
                    datoSalida.writeBoolean(false);
                    bucle = false;
                } catch (NumberFormatException ignored) {
                    datoSalida.writeBoolean(true);
                    datoSalida.writeUTF("Error, no has introducido un número entero.");
                }
            }

            bucle = true;
            while (bucle) {
                datoSalida.writeUTF("Por favor, introduce el número 2: ");
                try {
                    String num = datoEntrada.readUTF();
                    System.out.println("[Server] Número recibido: " + num + " (Cliente" + clientes + ")");
                    num2 = Integer.parseInt(num);
                    datoSalida.writeBoolean(false);
                    bucle = false;
                } catch (NumberFormatException ignored) {
                    datoSalida.writeBoolean(true);
                    datoSalida.writeUTF("Error, no has introducido un número entero.");
                }
            }

            bucle = true;
            while (bucle) {
                datoSalida.writeUTF("Ahora introduce la operación aritmética (-, +, *, /) o fin para salir: ");
                String mensajeRecibido = datoEntrada.readUTF();
                System.out.println("[Server] Operación recibida: " + mensajeRecibido + " (Cliente" + clientes + ")");
                if (mensajeRecibido.equalsIgnoreCase("fin")) {
                    System.out.println("[Server] cerrando la conexión con el cliente " + clientes);
                    datoSalida.writeUTF("¡Adiós!");
                    bucle = false;
                } else {
                    if (mensajeRecibido.equalsIgnoreCase("-"))
                        enviarResta(datoSalida);
                    else if (mensajeRecibido.equalsIgnoreCase("+"))
                        enviarSuma(datoSalida);
                    else if (mensajeRecibido.equalsIgnoreCase("*"))
                        enviarMultiplicacion(datoSalida);
                    else if (mensajeRecibido.equalsIgnoreCase("/"))
                        enviarDivision(datoSalida);
                    else
                        datoSalida.writeUTF("¡No te he entendido!");
                }
            }
            socket.close();
        } catch (IOException ignored) { }
    }

    public void enviarResta(DataOutputStream datoSalida) throws IOException {
        int resta = num1 - num2;
        datoSalida.writeUTF(String.valueOf(resta));
    }

    public void enviarSuma(DataOutputStream datoSalida) throws IOException {
        int suma = num1 + num2;
        datoSalida.writeUTF(String.valueOf(suma));
    }

    public void enviarMultiplicacion(DataOutputStream datoSalida) throws IOException {
        int multiplicacion = num1 * num2;
        datoSalida.writeUTF(String.valueOf(multiplicacion));
    }

    public void enviarDivision(DataOutputStream datoSalida) throws IOException {
        if (num2 == 0) {
            datoSalida.writeUTF("¡No se puede dividir entre 0!");
        } else {
            int division = num1 / num2;
            datoSalida.writeUTF(String.valueOf(division));
        }
    }

}
