package com.tamargo.datos;

public class InsertarDatosBase {

    public static void main(String[] args) {
        insertarUsuariosBase();
        insertarPreguntasBase();
    }

    public static void insertarUsuariosBase() {
        Usuario usuTest = new Usuario("Test", "Test", 22, "test", "��&��C�");
        Usuario usuAdmin = new Usuario("Admin", "Admin", 22, "admin", "��&��C�", 1);
        Usuario dani = new Usuario("Daniel", "Tamargo", 25, "dani", "��&��C�", 0, 50);
        Usuario irune = new Usuario("Irune", "Méndez", 23, "irune", "��&��C�", 0 , 90);
        Usuario relleno1 = new Usuario("Robert", "Chase", 20, "chase", "��&��C�", 0 , 100);
        Usuario relleno2 = new Usuario("Lucía", "Antolín", 20, "lucia", "��&��C�", 0 , 70);
        Usuario relleno3 = new Usuario("María", "Delgado", 20, "maria", "��&��C�", 0 , 20);
        Usuario relleno4 = new Usuario("Gloria", "Pritchet", 20, "gloria", "��&��C�", 0 , 110);
        Usuario relleno5 = new Usuario("Andrés", "Pomo", 20, "andres", "��&��C�", 0 , 80);
        Usuario relleno6 = new Usuario("Phil", "Dunphy", 20, "phildunphy", "��&��C�", 0 , 130);
        Usuario relleno7 = new Usuario("Allison", "Cameron", 20, "cameron", "��&��C�", 0 , 0);

        EscribirFicheros.addUsuario(usuTest);
        EscribirFicheros.addUsuario(usuAdmin);
        EscribirFicheros.addUsuario(dani);
        EscribirFicheros.addUsuario(irune);
        EscribirFicheros.addUsuario(relleno1);
        EscribirFicheros.addUsuario(relleno2);
        EscribirFicheros.addUsuario(relleno3);
        EscribirFicheros.addUsuario(relleno4);
        EscribirFicheros.addUsuario(relleno5);
        EscribirFicheros.addUsuario(relleno6);
        EscribirFicheros.addUsuario(relleno7);
    }


    public static void insertarPreguntasBase() {
        Pregunta pregunta1 = new Pregunta(
                "¿Qué asignatura impartió Ekaitz Martinez Cordero en el segundo curso del ciclo nocturno Desarrollo de Aplicaciones Multiplataforma?",
                "Acceso a Datos",
                "Desarrollo de Interfaces",
                "Sistemas de Gestión Empresarial",
                "Programación Multimedia y Dispositivos Móviles",
                "DAM",
                3);

        Pregunta pregunta2 = new Pregunta(
                "Si ejecutamos el método .stop() de un hilo, ¿podremos reanudarlo con el método .start()?",
                "No",
                "Sí",
                "Sí, pero sólo si hacemos un .notifyAll() antes",
                "Sí, pero sólo si implementa la interfaz Runnable",
                "Java",
                1);

        Pregunta pregunta3 = new Pregunta(
                "¿Quién puede descifrar mi mensaje si lo he cifrado con mi clave privada?",
                "Cualquiera, si quisiera que no lo hicieran, cifraría con la pública",
                "Solo aquellos que me conozcan bien",
                "Cualquiera que tenga mi clave privada",
                "Cualquiera que tenga mi clave pública",
                "PSP / SI",
                4);

        Pregunta pregunta4 = new Pregunta(
                "Si intentamos hacer el siguiente comando:\n" +
                        "User us = (User) fileIS.readObject();" +
                        "\n¿cuál o cuáles serían las excepciones?",
                "IOException",
                "ClassCastException",
                "IOException + ClassCastException + ClassNotFoundException",
                "ClassNotFoundException",
                "Java",
                3);

        Pregunta pregunta5 = new Pregunta(
                "En Python, ¿podemos guardar un int en una variable donde teníamos guardado un String?",
                "Sí",
                "No",
                "Sí, pero solo si antes hemos igualado la variable a null",
                "Sí, pero al hacerlo no guarda el valor, lo ignora",
                "Python",
                1);

        Pregunta pregunta6 = new Pregunta(
                "Hemos usado el método .acquire(). con...",
                "Los Semáforos",
                "Los Monitores",
                "Los ficheros de texto, dando permiso para escribir",
                "Las conversiones no implícitas de tipos de datos",
                "Java",
                1);

        Pregunta pregunta7 = new Pregunta(
                "Si queremos esperar a que un hilo termine tendremos que...",
                "Calcular el tiempo de uso y esperar con un Thread.sleep()",
                "Ser muy listos",
                "Utilizar el método .join()",
                "No se puede esperar a un hilo, al lanzarlo se separa e independiza",
                "Java",
                3);

        Pregunta pregunta8 = new Pregunta(
                "¿Cuál de estas Bases de Datos es una BBDD embebida?",
                "SQLite",
                "MySQL",
                "Oracle",
                "Access",
                "AD",
                1);

        Pregunta pregunta9 = new Pregunta(
                "El año pasado Ekaitz nos comentó que había un concurso de programación " +
                        "¿Qué lenguaje o lenguajes estaban admitidos en el concurso?",
                "Java",
                "C++",
                "Python",
                "Cualquiera",
                "DAM",
                4);

        Pregunta pregunta10 = new Pregunta(
                "¿Cómo hay que llamar a la variable Exception dentro de los try catch " +
                        "para que el IDE de IntelliJ no te lo señale si no haces nada con dicha variable?",
                "ex",
                "avoid",
                "ignored",
                "doNotUse",
                "IntelliJ",
                3);

        Pregunta pregunta11 = new Pregunta(
                "¿Cuál es la combinación de teclas para, donde tengamos puesto " +
                        "el cursor para escribir, seleccionar el bloque progresivamente?",
                "Ctrl + W",
                "Ctrl + Alt + Supr",
                "Alt + F4",
                "Ctrl + Alt + Y",
                "IntelliJ",
                1);

        Pregunta pregunta12 = new Pregunta(
                "¿Qué asignatura no se imparte en el tercer curso del ciclo nocturno Desarrollo de Aplicaciones Multiplataforma?",
                "Empresa Iniciativa y Emprendedora",
                "Acceso a Datos",
                "Sistemas Informáticos",
                "Programación de Servicios y Procesos",
                "DAM",
                3);


        EscribirFicheros.addPregunta(pregunta1);
        EscribirFicheros.addPregunta(pregunta2);
        EscribirFicheros.addPregunta(pregunta3);
        EscribirFicheros.addPregunta(pregunta4);
        EscribirFicheros.addPregunta(pregunta5);
        EscribirFicheros.addPregunta(pregunta6);
        EscribirFicheros.addPregunta(pregunta7);
        EscribirFicheros.addPregunta(pregunta8);
        EscribirFicheros.addPregunta(pregunta9);
        EscribirFicheros.addPregunta(pregunta10);
        EscribirFicheros.addPregunta(pregunta11);
        EscribirFicheros.addPregunta(pregunta12);


    }

}
