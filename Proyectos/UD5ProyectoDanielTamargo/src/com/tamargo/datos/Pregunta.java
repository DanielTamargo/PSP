package com.tamargo.datos;

import java.io.Serializable;

public class Pregunta implements Serializable {

    private String titulo;
    private String opcion1;
    private String opcion2;
    private String opcion3;
    private String opcion4;
    private String tipo; // Gaming, Historia, Programación...
    private int opcionCorrecta;

    /**
     * Constructor donde se definirán todos los atributos, incluído seleccionar cuál es la opción correcta
     */
    public Pregunta(String titulo, String opcion1, String opcion2, String opcion3, String opcion4, String tipo, int opcionCorrecta) {
        this.titulo = titulo;
        this.opcion1 = opcion1;
        this.opcion2 = opcion2;
        this.opcion3 = opcion3;
        this.opcion4 = opcion4;
        this.tipo = tipo;
        this.opcionCorrecta = opcionCorrecta;
    }

    /**
     * Constructor donde por defecto la opción correcta será la primera opción introducida
     */
    public Pregunta(String titulo, String opcion1, String opcion2, String opcion3, String opcion4, String tipo) {
        this.titulo = titulo;
        this.opcion1 = opcion1;
        this.opcion2 = opcion2;
        this.opcion3 = opcion3;
        this.opcion4 = opcion4;
        this.tipo = tipo;
        this.opcionCorrecta = 1;
    }

    /**
     * No recibe directamente un int con la opción elegida porque la idea es hacer que el orden en el que aparecen
     * las opciones sea aleatorio, haciendo que si una pregunta se repite al volver a jugar no sea tan fácil acertarla
     *
     * Este método recibirá el String de la opción elegida y lo cotejará con el texto de la opción correcta
     * para ver si es la opción correcta
     */
    public boolean esCorrecta(String opcionElegida) {
        String textoOpcionCorrecta;
        if (opcionCorrecta == 1)
            textoOpcionCorrecta = opcion1;
        else if (opcionCorrecta == 2)
            textoOpcionCorrecta = opcion2;
        else if (opcionCorrecta == 3)
            textoOpcionCorrecta = opcion3;
        else
            textoOpcionCorrecta = opcion4;

        return opcionElegida.equalsIgnoreCase(textoOpcionCorrecta);
    }

    @Override
    public String toString() {
        return "Pregunta{" +
                "titulo='" + titulo + '\'' +
                ", opcion1='" + opcion1 + '\'' +
                ", opcion2='" + opcion2 + '\'' +
                ", opcion3='" + opcion3 + '\'' +
                ", opcion4='" + opcion4 + '\'' +
                ", tipo='" + tipo + '\'' +
                ", opcionCorrecta=" + opcionCorrecta +
                '}';
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getOpcion1() {
        return opcion1;
    }

    public void setOpcion1(String opcion1) {
        this.opcion1 = opcion1;
    }

    public String getOpcion2() {
        return opcion2;
    }

    public void setOpcion2(String opcion2) {
        this.opcion2 = opcion2;
    }

    public String getOpcion3() {
        return opcion3;
    }

    public void setOpcion3(String opcion3) {
        this.opcion3 = opcion3;
    }

    public String getOpcion4() {
        return opcion4;
    }

    public void setOpcion4(String opcion4) {
        this.opcion4 = opcion4;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getOpcionCorrecta() {
        return opcionCorrecta;
    }

    public void setOpcionCorrecta(int opcionCorrecta) {
        this.opcionCorrecta = opcionCorrecta;
    }
}
