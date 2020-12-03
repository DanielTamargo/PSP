package com.tamargo.jaas.modelo;

import com.tamargo.datos.Usuario;

import java.io.Serializable;
import java.security.Principal;

/**
 * Implementación de la interfaz Principal
 */
public class ImplementacionPrincipal implements Principal, Serializable {

    private final Usuario usuario;

    public ImplementacionPrincipal(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ImplementacionPrincipal)) {
            return false;
        }
        ImplementacionPrincipal otro = (ImplementacionPrincipal) obj;
        return usuario.getNick().equals(otro.getName());
    }

    @Override
    public int hashCode() {
        return usuario.getNick().hashCode();
    }

    public String getName() {
        return usuario.getNick();
    }

    public String getNick() {
        return usuario.getNick();
    }

    public String getContrasenya() {
        return usuario.getContrasenya();
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public int getTipo() {
        return usuario.getTipo();
    }

    @Override
    public String toString() {
        return "ImplementacionPrincipal{" +
                "usuario='" + usuario.getNick() + '\'' +
                ", contrasenya='" + usuario.getContrasenya() + '\'' +
                ", tipo='" + usuario.getTipo() + '\'' +
                '}';
    }
}
