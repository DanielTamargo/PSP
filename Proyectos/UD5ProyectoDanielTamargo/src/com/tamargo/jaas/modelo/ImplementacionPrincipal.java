package com.tamargo.jaas.modelo;

import java.io.Serializable;
import java.security.Principal;

/**
 * Implementación de la interfaz Principal
 */
public class ImplementacionPrincipal implements Principal, Serializable {

    private final String nombre;
    private final String contrasenya;
    private final int tipo;

    public ImplementacionPrincipal(String nombre, String contrasenya, int tipo) {
        this.nombre = nombre;
        this.contrasenya = contrasenya;
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

    public String getNombre() {
        return nombre;
    }

    public String getContrasenya() {
        return contrasenya;
    }

    public int getTipo() {
        return tipo;
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
