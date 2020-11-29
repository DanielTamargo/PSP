package com.tamargo;

import com.tamargo.datos.EscribirFicheros;
import com.tamargo.datos.GuardarLogs;
import com.tamargo.datos.LeerFicheros;
import com.tamargo.datos.Usuario;
import com.tamargo.jaas.modelo.ImplementacionPrincipal;
import com.tamargo.jaas.modelo.UserPasswordCallbackHandler;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;

public class Main {

    public static void main(String[] args) {
        //TODO Lanzar un servidor y un cliente
        // TODO se podría preguntar en un JOptionPane cuántos clientes quiere lanzar

        new Main().pruebaLoginModule();
    }

    public void pruebaLoginModule() {

        LoginContext loginContext = null;
        String usuario = "prueba";
        char[] contrasenya = "-_{}[]¡!88**//__sdf".toCharArray();
        boolean exito = false;

        try {
            loginContext = new LoginContext(
                    "Jugador",
                    new UserPasswordCallbackHandler(usuario, contrasenya)
            );
            loginContext.login();
            exito = true;
        } catch (LoginException ignored) { }

        if (exito) {
            Subject sujeto = loginContext.getSubject();
            Set principales = sujeto.getPrincipals();
            Iterator iterador = principales.iterator();
            while (iterador.hasNext()) {
                ImplementacionPrincipal principal = (ImplementacionPrincipal)iterador.next();
                System.out.println("Usuario Loggeado: " + principal.getName() +
                        "\nContraseña: " + principal.getContrasenya());
            }

        }

    }

    public void pruebaLog() {
        /*
        StackTraceElement[] stackTraceElements = (new Throwable()).getStackTrace();
        GuardarLogs.escribirLog(Level.FINE,
                this.getClass().getPackageName() + "." + this.getClass().getSimpleName(),
                stackTraceElements[0].getMethodName(),
                "Este es un log de prueba");*/

        /*GuardarLogs.escribirLog(
                Level.FINE,
                "Main",
                "pruebaLog",
                "Este es un log de prueba");*/

        GuardarLogs.logger.log(Level.FINE, "Prueba de log");
    }
}
