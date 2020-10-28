package com.tamargo.ejercicio6;

import java.io.*;
import java.net.Socket;

public class Cliente6 {

    public static void main(String[] args) {

        try {
            Socket socket = new Socket("localhost", 5600);
            System.out.println("[Cliente] cliente conectado al servidor");
            ObjectInputStream objIS = new ObjectInputStream(socket.getInputStream());
            DosNumeros dn = (DosNumeros) objIS.readObject();
            System.out.println("[Cliente] objeto recibido: " + dn);

            System.out.println();
            System.out.println("[Cliente] números del objeto: " + dn.getN1() + ", " + dn.getN2());

            dn.resolver(); // realiza la solución
            System.out.println("[Cliente] solución resuelta: " + dn.getSolucion());

            ObjectOutputStream objOUT = new ObjectOutputStream(socket.getOutputStream());
            objOUT.writeObject(dn);
            System.out.println("[Cliente] objeto enviado (con la solución): " + dn);

            socket.close();
            System.out.println("[Cliente] cliente desconectado del servidor");

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("[Cliente] error al conectar con el servidor");
        }
    }

}
