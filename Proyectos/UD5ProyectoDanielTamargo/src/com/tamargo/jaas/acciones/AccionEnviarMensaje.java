package com.tamargo.jaas.acciones;

import java.security.PrivilegedAction;

/**
 * Este es un ejemplo de una acción si quisieramos tener acciones posibles a ejecutar
 * En esta acción accederíamos al Sujeto por el Contexto y podríamos comprobar sus privilegios
 *
 * Es similar a cuando trabajábamos con Laravel, cada usuario tenía su rol, y dependiendo de su rol, el listener
 * de una ruta hacía unas cosas u otras
 */
public class AccionEnviarMensaje implements PrivilegedAction {

    public AccionEnviarMensaje() { }

    public Object run() {
        System.out.println("""
                Acción en marcha...
                
                Falta:
                - Comprobar que podemos distinguir cargos con el getPrincipals
                - Comprobar que el socket no se cierra si lo recibimos aquí 
                                       (probar mandando flujos solo, o el socket entero)""");
        return null;
    }

}
