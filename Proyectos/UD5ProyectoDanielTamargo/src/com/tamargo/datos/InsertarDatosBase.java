package com.tamargo.datos;

public class InsertarDatosBase {

    public static void main(String[] args) {
        //insertarUsuariosBase();
        insertarPreguntasBase();
    }

    public static void insertarUsuariosBase() {
        Usuario usuTest = new Usuario("Test", "Test", 22, "test", "��&��C�");
        Usuario usuAdmin = new Usuario("Admin", "Admin", 22, "admin", "��&��C�", 1);

        EscribirFicheros.addUsuario(usuTest);
        EscribirFicheros.addUsuario(usuAdmin);
    }


    public static void insertarPreguntasBase() {
        Pregunta pregunta1 = new Pregunta(
                "¿Qué asignatura impartió Ekaitz Martinez Cordero en el segundo curso del ciclo nocturno Desarrollo de Aplicaciones Multiplataforma?",
                "Acceso a Datos",
                "Desarrollo de Interfaces",
                "Sistemas de Gestión Empresarial",
                "Programación Multimedia y Dispositivos Móviles",
                "Qué asignatura impartió",
                3);

        Pregunta pregunta2 = new Pregunta(
                "Si ejecutamos el método .stop() de un hilo, ¿podremos reanudarlo con el método .start()?",
                "No",
                "Sí",
                "Sí, pero sólo si hacemos un .notifyAll() antes",
                "Sí, pero sólo si implementa la interfaz Runnable",
                "PSP - Threads",
                1);

        Pregunta pregunta3 = new Pregunta(
                "¿Quién puede descifrar mi mensaje si lo he cifrado con mi clave privada?",
                "Cualquiera, si quisiera que no lo hicieran, cifraría con la pública",
                "Solo aquellos que me conozcan bien",
                "Cualquiera que tenga mi clave privada",
                "Cualquiera que tenga mi clave pública",
                "PSP / SI - Cifrar/Descifrar",
                4);

        Pregunta pregunta4 = new Pregunta(
                "Si intentamos hacer el siguiente comando:\n" +
                        "User us = (User) fileIS.readObject();" +
                        "\n¿cuál o cuáles serían las excepciones?",
                "IOException",
                "ClassCastException",
                "IOException + ClassCastException + ClassNotFoundException",
                "ClassNotFoundException",
                "Java - Try Catch",
                3);

        EscribirFicheros.addPregunta(pregunta1);
        EscribirFicheros.addPregunta(pregunta2);
        EscribirFicheros.addPregunta(pregunta3);
        EscribirFicheros.addPregunta(pregunta4);
    }

}
