package com.tamargo.jaas.acciones;

import java.security.PrivilegedAction;

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
