package com.tamargo.jaas.modelo;

import java.io.Serializable;
import java.security.Principal;

/**
 * Implementación de la interfaz Principal
 */
public class ImplementacionPrincipal implements Principal, Serializable {

    private final String nombre;
    private final String contrasenya;
    private final String tipo;

    public ImplementacionPrincipal(String nombre, String contrasenya, String tipo) {
        this.nombre = nombre;
        this.contrasenya = contrasenya;
        if (tipo == null)
            this.tipo = "Invitado";
        else
            this.tipo = tipo;
    }

    @Override
    public boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ImplementacionPrincipal)) {
            return false;
        }
        ImplementacionPrincipal otro = (ImplementacionPrincipal) obj;
        return nombre.equals(otro.getName());
    }

    @Override
    public int hashCode() {
        return nombre.hashCode();
    }

    public String getName() {
        return nombre;
    }

    public String getContrasenya() {
        return contrasenya;
    }

    @Override
    public String toString() {
        return "ImplementacionPrincipal{" +
                "nombre='" + nombre + '\'' +
                ", contrasenya='" + contrasenya + '\'' +
                ", tipo='" + tipo + '\'' +
                '}';
    }
}
