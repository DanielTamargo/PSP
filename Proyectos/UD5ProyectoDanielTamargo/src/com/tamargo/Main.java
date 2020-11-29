package com.tamargo;

import com.tamargo.datos.EscribirFicheros;
import com.tamargo.datos.GuardarLogs;
import com.tamargo.datos.LeerFicheros;
import com.tamargo.datos.Usuario;

import java.util.ArrayList;
import java.util.logging.Level;

public class Main {

    public static void main(String[] args) {
        //TODO Lanzar un servidor y un cliente
        // TODO se podría preguntar en un JOptionPane cuántos clientes quiere lanzar

        new Main().pruebaLog();
    }


    public void pruebaLog() {
        /*
        StackTraceElement[] stackTraceElements = (new Throwable()).getStackTrace();
        GuardarLogs.escribirLog(Level.FINE,
                this.getClass().getPackageName() + "." + this.getClass().getSimpleName(),
                stackTraceElements[0].getMethodName(),
                "Este es un log de prueba");*/
        GuardarLogs.escribirLog(
                Level.FINE,
                "Main",
                "pruebaLog",
                "Este es un log de prueba");
    }
}
