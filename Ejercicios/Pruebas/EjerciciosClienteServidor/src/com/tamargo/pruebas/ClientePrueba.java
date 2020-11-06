package com.tamargo.pruebas;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientePrueba {

    public static void main(String[] args) {

        try {
            Socket socket = new Socket("localhost", 5600);

            Scanner teclado = new Scanner(System.in);

            teclado.nextLine();

            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
