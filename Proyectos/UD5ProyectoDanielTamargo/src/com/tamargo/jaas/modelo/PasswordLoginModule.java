package com.tamargo.jaas.modelo;

import com.tamargo.datos.EscribirFicheros;
import com.tamargo.datos.GuardarLogs;
import com.tamargo.datos.LeerFicheros;
import com.tamargo.datos.Usuario;

import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Level;

/**
 * LoginModule que coteja el usuario y la contraseña con la BBDD y genera un nuevo usuario
 * en caso de no existir
 */
public class PasswordLoginModule implements LoginModule {

    private Subject sujeto;
    private CallbackHandler callbackHandler;

    private boolean loginExito = false;
    private boolean commitExito = false;

    private String usuario;
    private char[] contrasenya;

    private Principal principal;

    /**
     * Inicializar el LoginModule
     */
    @Override
    public void initialize(Subject sujeto, CallbackHandler callbackHandler,
                           Map estadoCompartido, Map opciones) {
        this.sujeto = sujeto;
        this.callbackHandler = callbackHandler;
        this.loginExito = false;
        this.commitExito = false;
        this.usuario = null;
        clearPassword();
    }

    /**
     * Login
     * - comprobamos que el usuario existe
     *  + si existe, comprobamos que su contraseña coincide
     *  + si no existe, creamos un nuevo jugador guardando los datos y dando por válido el acceso
     */
    public boolean login() throws LoginException {
        if (callbackHandler == null) {
            throw new LoginException("CallbackHandler no definido");
        }

        // Crear dos callbacks: uno para usuario y el otro para password.
        Callback[] callbacks = new Callback[2];
        callbacks[0] = new NameCallback("Usuario");
        callbacks[1] = new PasswordCallback("Password", false);

        try {
            // Llamar al callbackhandler para rellenar informacion
            callbackHandler.handle(callbacks);
            usuario = ((NameCallback) callbacks[0]).getName();
            char[] tempPassword = ((PasswordCallback) callbacks[1]).getPassword();
            contrasenya = new char[tempPassword.length];
            System.arraycopy(tempPassword, 0, contrasenya, 0, tempPassword.length);

            // Borrar password en el callback
            ((PasswordCallback) callbacks[1]).clearPassword();
        } catch (IOException ioe) {
            GuardarLogs.logger.log(Level.SEVERE, "Error al manejar las credenciales recibidas. Ha habido un error al trabajar con los datos");
            return false;
            //throw new LoginException(ioe.toString());
        } catch (UnsupportedCallbackException uce) {
            GuardarLogs.logger.log(Level.SEVERE, "Error al manejar las credenciales recibidas. El CallbackHandler utilizado no está soportado");
            return false;
            //throw new LoginException(uce.toString());
        }

        ArrayList<Usuario> usuarios = LeerFicheros.leerUsuarios();
        String contrasenyaCotejar = new String(contrasenya);

        boolean usuarioExiste = false;

        // Validar usuario y password
        for (Usuario usu: usuarios) {
            if (usu.getNombre().equalsIgnoreCase(usuario)) {
                usuarioExiste = true;
                if (usu.getContrasenya().equals(contrasenyaCotejar)) {
                    GuardarLogs.logger.log(Level.FINE, "Inicio de sesión exitoso");
                    System.out.println("[PasswordLoginModule] Inicio de sesión correcto");
                    loginExito = true;
                    return true;
                } else {
                    usuario = null;
                    clearPassword();
                    GuardarLogs.logger.log(Level.WARNING, "Intento de inicio de sesión fallido. Contraseña incorrecta.");
                    System.out.println("[PasswordLoginModule] Contraseña incorrecta");
                    loginExito = false;
                    //TODO JOPTION PANE INFORMATIVO
                    return false;
                }
            }
        }

        EscribirFicheros.addUsuario(new Usuario(usuario, contrasenyaCotejar));
        loginExito = true;
        GuardarLogs.logger.log(Level.FINE, "Nuevo usuario registrado en la base de datos: " + usuario);
        System.out.println("[PasswordLoginModule] Se ha registrado al usuario '" + usuario + "' en la base de datos");
        return true;
    }

    /**
     *	Llamar si el login tiene éxito
     */
    public boolean commit() throws LoginException {
        // Si el login falló pero por algún motivo hemos llegado al commit, abortamos
        if (!loginExito) {
            return false;
        }

        // Login con éxito: crear Principal y añadirlo al Subject
        principal = new ImplementacionPrincipal(usuario, new String(contrasenya), null);
        sujeto.getPrincipals().add(principal);

        // Borrar usuario y password.
        usuario = null;
        clearPassword();
        commitExito = true;
        return true;
    }

    /**
     * Llamar si el login falla
     */
    public boolean abort() throws LoginException {
        // Si login falla, devolver false
        if (!loginExito) {
            return false;
        } else {
            if (!commitExito) {
                // Nuestro login tuvo éxito pero otros fallaron
                loginExito = false;
                usuario = null;
                clearPassword();
                principal = null;
            } else {
                // Nosotros hicimos commit pero alguien falló
                logout();
            }
        }
        return true;
    }

    /**
     * Logout
     */
    public boolean logout() throws LoginException {
        // Borrar principal del usuario
        sujeto.getPrincipals().remove(principal);
        loginExito = false;
        commitExito = false;
        usuario = null;
        clearPassword();
        principal = null;
        return true;
    }

    /**
     * Limpiar la contrasenya
     */
    private void clearPassword() {
        if (contrasenya == null) {
            return;
        }
        Arrays.fill(contrasenya, ' ');
        contrasenya = null;
    }
}
