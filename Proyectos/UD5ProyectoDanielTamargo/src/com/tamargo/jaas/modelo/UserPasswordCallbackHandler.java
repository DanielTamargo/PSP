package com.tamargo.jaas.modelo;

import javax.security.auth.callback.*;

/**
 * CallbackHandler encargado de usuarios y contraseñas
 */
public class UserPasswordCallbackHandler implements CallbackHandler {

    private String usuario;
    private char[] contrasenya;

    /**
     * Constructor que recibe el nombre de usuario y contraseña a especificar
     */
    public UserPasswordCallbackHandler(String usuario, char[] contrasenya) {
        this.usuario = usuario;
        this.contrasenya = contrasenya;
    }

    /**
     * Manejar callbacks: NameCallbacks y PasswordCallbacks
     */
    public void handle(Callback[] callbacks) throws UnsupportedCallbackException {
        // Iterar los callbacks
        for (int i = 0; i < callbacks.length; i++) {
            Callback callback = callbacks[i];
            // Manejar callback según su tipo.
            if (callback instanceof NameCallback) {
                NameCallback nameCallback = (NameCallback) callback;
                nameCallback.setName(usuario);
            } else if (callback instanceof PasswordCallback) {
                PasswordCallback passwordCallback = (PasswordCallback) callback;
                passwordCallback.setPassword(contrasenya);
            } else {
                throw new UnsupportedCallbackException(callback, "Tipo de callback no soportado");
            }
        }
    }
}
