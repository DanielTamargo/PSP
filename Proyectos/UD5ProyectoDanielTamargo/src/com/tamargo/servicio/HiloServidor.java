package com.tamargo.servicio;

import com.tamargo.datos.*;
import com.tamargo.jaas.modelo.ImplementacionPrincipal;
import com.tamargo.jaas.modelo.UserPasswordCallbackHandler;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLSocket;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.io.*;
import java.security.*;
import java.util.*;
import java.util.logging.Level;

public class HiloServidor extends Thread {
    private final String nombre;

    private final SSLSocket socketSSL;
    private final KeyPair parejaClaves;

    private DataInputStream dataIS;
    private DataOutputStream dataOS;
    private ObjectInputStream objIS;
    private ObjectOutputStream objOS;

    private String nickJugador = "nickDelJugadorLoggeado";

    public HiloServidor(int numCliente, SSLSocket socketSSL, KeyPair parejaClaves) {
        this.socketSSL = socketSSL;
        this.parejaClaves = parejaClaves;

        this.nombre = "[Servidor Cliente " + numCliente + "] ";
    }


    @Override
    public void run() {
        System.out.println(nombre + "Conexión establecida");
        try {
            // Preparar los flujos de datos
            try {
                dataOS = new DataOutputStream(socketSSL.getOutputStream());
                dataIS = new DataInputStream(socketSSL.getInputStream());
                objOS = new ObjectOutputStream(socketSSL.getOutputStream());
                objIS = new ObjectInputStream(socketSSL.getInputStream());
                System.out.println(nombre + "Flujos de datos preparados");
            } catch (IOException ignored) { }

            // Enviar clave pública
            objOS.writeObject(parejaClaves.getPublic());
            System.out.println(nombre + "Clave pública enviada");

            // Recibir clave simétrica y descifrarla con clave privada
            byte[] claveAEScifrada = (byte[]) objIS.readObject();
            System.out.println(nombre + "Clave simétrica cifrada recibida");

            Cipher rsaCipher = Cipher.getInstance("RSA");
            rsaCipher.init(Cipher.DECRYPT_MODE, parejaClaves.getPrivate());

            byte[] bytesClaveAES = rsaCipher.doFinal(claveAEScifrada);
            SecretKey claveAES = new SecretKeySpec(bytesClaveAES, 0, bytesClaveAES.length, "AES");
            System.out.println(nombre + "Clave simétrica descifrada y lista para el uso");

            // Ya tenemos tunel para enviar mensajes cifrados con la clave simétrica recibida
            // Ejemplos de enviar mensaje encriptado y desencriptado
            //objOS.writeObject(encriptarMensaje(claveAES, "Ya tenemos el tunel encriptado por la clave AES que me mandaste"));
            //System.out.println(nombre + "El cliente dice: " + desencriptarMensaje(claveAES, (byte[]) objIS.readObject()));


            // Bucle comunicativo
            // TODO aquí el hilo del server esperará recibir opciones
            try {
                int n = 1;
                Integer respuesta = -1;
                while (respuesta != 0) {
                    respuesta = (Integer) objIS.readObject();
                    switch (respuesta) {
                        case 1 -> { // INICIO DE SESIÓN
                            String nick = desencriptarMensaje(claveAES, (byte[]) objIS.readObject());
                            String contrasenya = desencriptarMensaje(claveAES, (byte[]) objIS.readObject());
                            System.out.println(nombre + "Login -> Credenciales recibidas: " + nick + ", " + contrasenya);
                            LoginContext loginContext = null;

                            char[] password = contrasenya.toCharArray();
                            boolean exito = false;

                            try {
                                loginContext = new LoginContext(
                                        "Jugador",
                                        new UserPasswordCallbackHandler(nick, password)
                                );
                                loginContext.login();
                                exito = true;
                                nickJugador = nick;
                            } catch (LoginException ignored) { }

                            objOS.writeObject(exito);

                            if (exito) {
                                Signature sigRSA = Signature.getInstance("SHA256withRSA");
                                sigRSA.initSign(parejaClaves.getPrivate());
                                String normas = """
                                        1- Al empezar una partida comenzarás una serie de rondas
                                        2- Cada ronda recibirás una pregunta y 4 respuestas
                                        3- Solo una respuesta será correcta
                                        4- Cada respuesta acertada sumará un punto
                                        5- Seguirás respondiendo hasta fallar una pregunta, abandonar o terminarlas todas
                                        6- Se te guardará la puntuación más alta que alcances
                                        7- Puedes ver la lista de puntuaciones para ver tu clasificación""";
                                sigRSA.update(normas.getBytes());
                                byte[] firmaNormas = sigRSA.sign();
                                System.out.println(nombre + "Enviando normas firmadas:");
                                System.out.println(new String(firmaNormas));

                                objOS.writeObject(encriptarMensaje(claveAES, normas));
                                objOS.writeObject(firmaNormas);

                                boolean confirmacion;
                                confirmacion = (boolean) objIS.readObject();
                                if (confirmacion) {
                                    enviarTopPuntuaciones(claveAES, objOS);
                                }
                            }
                        }
                        case 2 -> { // REGISTRO
                            String nombre = desencriptarMensaje(claveAES, (byte[]) objIS.readObject());
                            String apellido = desencriptarMensaje(claveAES, (byte[]) objIS.readObject());
                            String edadStr = desencriptarMensaje(claveAES, (byte[]) objIS.readObject());
                            int edad = 0;
                            try {
                                edad = Integer.parseInt(edadStr);
                            } catch (NumberFormatException ignored) {}
                            String nick = desencriptarMensaje(claveAES, (byte[]) objIS.readObject());
                            String contrasenya = desencriptarMensaje(claveAES, (byte[]) objIS.readObject());
                            System.out.println(this.nombre + "Registro -> Datos recibidos: "  + nombre + ", " + apellido + ", " + edad + ", " + nick + ", " + contrasenya);
                            Usuario usuario = new Usuario(nombre, apellido, edad, nick, contrasenya);
                            objOS.writeObject(EscribirFicheros.addUsuario(usuario));
                        }
                        case 3 -> { // PARTIDA
                            buclePartida(claveAES, objOS, objIS);
                        }
                    }
                }
            } catch (EOFException ignored) { }
            catch (SignatureException e) {
                System.out.println(nombre + "Error al firmar las normas. Error: " + e.getLocalizedMessage());
                GuardarLogs.logger.log(Level.INFO, "Error al firmar las normas. Error: " + e.getLocalizedMessage());
            }

            // TODO solo mostrar desconexión con éxito si valida reglas y todo va bi en, si no las valida mostrar
            // "El cliente no ha aceptado las reglas, finalizando conexión"
            System.out.println(nombre + "Cliente desconectado con éxito. ¡Adiós!");
            objIS.close();
            objOS.close();
        } catch (IOException e) {
            System.out.println(nombre + "El cliente se ha desconectado o ha rechazado la conexión. Error: " + e.getLocalizedMessage());
            GuardarLogs.logger.log(Level.INFO, "El cliente se ha desconectado o ha rechazado la conexión. Error: " + e.getLocalizedMessage());
        } catch (NoSuchAlgorithmException e) {
            System.out.println(nombre + "Error al generar la clave simétrica o alguno de los cifradores: " + e.getLocalizedMessage());
            GuardarLogs.logger.log(Level.INFO, "Error al generar la clave simétrica o alguno de los cifradores: " + e.getLocalizedMessage());
        } catch (ClassNotFoundException e) {
            System.out.println(nombre + "Error al intentar utilizar una clase que no existe: " + e.getLocalizedMessage());
            GuardarLogs.logger.log(Level.INFO, "Error al intentar utilizar una clase que no existe: " + e.getLocalizedMessage());
        } catch (NoSuchPaddingException e) {
            System.out.println(nombre + "Error al intentar configurar los cifradores: " + e.getLocalizedMessage());
            GuardarLogs.logger.log(Level.INFO, "Error al intentar configurar los cifradores: " + e.getLocalizedMessage());
        } catch (InvalidKeyException e) {
            System.out.println(nombre + "Error al utilizar una clave no válida para configurar los cifradores: " + e.getLocalizedMessage());
            GuardarLogs.logger.log(Level.INFO, "Error al utilizar una clave no válida para configurar los cifradores: " + e.getLocalizedMessage());
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            System.out.println(nombre + "Error al utilizar el cifrador para encriptar/desencriptar: " + e.getLocalizedMessage());
            GuardarLogs.logger.log(Level.INFO, "Error al utilizar el cifrador para encriptar/desencriptar: " + e.getLocalizedMessage());
        }

    }

    public void buclePartida(SecretKey claveAES, ObjectOutputStream objOS, ObjectInputStream objIS) throws IOException, ClassNotFoundException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        ArrayList<Pregunta> preguntas = LeerFicheros.leerPreguntas();

        if (preguntas.size() > 0) {
            Collections.shuffle(preguntas); // Las barajamos para que salgan en distinto orden
            int pos = 0;
            int puntuacion = 0;
            objOS.writeObject(true); // Existen preguntas, lo notificamos
            enviarPregunta(datosPreguntaYRespuestas(preguntas.get(pos), puntuacion), claveAES, objOS);
            boolean bucle = true;
            int opcion = -1;
            while (bucle) {
                opcion = (Integer) objIS.readObject();
                switch (opcion) {
                    case 1 -> {
                        // TODO RECIBIR TEXTO RESPUESTA
                        //  SI ACIERTA, SIGUIENTE PREGUNTA
                        //  SI FALLA, COTEJAR PUNTUACIONES Y RESPONDER
                        String textoRespuesta = desencriptarMensaje(claveAES, (byte[]) objIS.readObject());
                        boolean acierto = preguntas.get(pos).esCorrecta(textoRespuesta);
                        if (acierto) {
                            puntuacion += 10;
                            pos++;
                            if (pos < preguntas.size()) { // NUEVA PREGUNTA
                                objOS.writeObject(0);
                                enviarPregunta(datosPreguntaYRespuestas(preguntas.get(pos), puntuacion), claveAES, objOS);
                            } else { // HAS ACERTADO TODAS Y NO QUEDAN MÁS
                                cotejarPuntuaciones(puntuacion);
                                objOS.writeObject(1);
                                bucle = false;
                            }
                        } else {
                            if (!cotejarPuntuaciones(puntuacion)) { // HAS FALLADO SIN SUPERAR TU PUNTUACIÓN
                                objOS.writeObject(2);
                            } else { // HAS FALLADO PERO HAS SUPERADO TU PUNTUACIÓN
                                objOS.writeObject(3);
                            }
                            bucle = false;
                        }
                    }
                    case 2 -> {
                        // TODO ABANDONO, COTEJAR PUNTUACIONES Y RESPONDER
                        if (!cotejarPuntuaciones(puntuacion)) {
                            objOS.writeObject(-1);
                        } else {
                            objOS.writeObject(-2);
                        }
                        bucle = false;
                    }
                }
            }

            enviarTopPuntuaciones(claveAES, objOS);
        } else {
            objOS.writeObject(false); // No existen preguntas, lo notificamos
        }
    }

    /**
     * Devuelve true si la puntuación es mayor que la que ya tenía el usuario
     */
    public boolean cotejarPuntuaciones(int puntuacion) {
        boolean mayorPuntuacion = false;
        ArrayList<Usuario> usuarios = LeerFicheros.leerUsuarios();
        for (Usuario usu: usuarios) {
            if (usu.getNick().equalsIgnoreCase(nickJugador)) {
                if (usu.getPuntuacion() < puntuacion) {
                    EscribirFicheros.modificarPuntuacionUsuario(nickJugador, puntuacion);
                    mayorPuntuacion = true;
                }
            }
        }
        return mayorPuntuacion;
    }

    public void enviarPregunta(ArrayList<String> datos, SecretKey claveAES, ObjectOutputStream objOS) throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        objOS.writeObject(encriptarArrayListString(claveAES, datos));
    }

    public ArrayList<String> datosPreguntaYRespuestas(Pregunta pregunta, int puntuacion) {
        ArrayList<String> datos = new ArrayList<>();

        // Cogemos el título
        datos.add(pregunta.getTitulo());

        // Cogemos las respuestas y las barajamos
        ArrayList<String> respuestas = new ArrayList<>();
        respuestas.add(pregunta.getOpcion1());
        respuestas.add(pregunta.getOpcion2());
        respuestas.add(pregunta.getOpcion3());
        respuestas.add(pregunta.getOpcion4());
        Collections.shuffle(respuestas);

        // Las añadimos
        datos.addAll(respuestas);

        // Cogemos el tipo
        datos.add("Tipo: " + pregunta.getTipo());
        datos.add("Tu puntuación: " + puntuacion);

        return datos;
    }

    public ArrayList<String> topPuntuaciones() {
        ArrayList<String> lineasTopPuntuaciones = new ArrayList<>();

        ArrayList<Usuario> usuarios = LeerFicheros.leerUsuarios();
        HashMap<Usuario, Integer> listaSinOrdenar = new HashMap<>();

        for (Usuario usuario: usuarios) {
            listaSinOrdenar.put(usuario, usuario.getPuntuacion());
        }

        LinkedHashMap<Usuario, Integer> listaOrdenada = new LinkedHashMap<>();
        listaSinOrdenar.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> listaOrdenada.put(x.getKey(), x.getValue()));

        int n = 0;
        String tuPuntuacion = "";
        for (Usuario value : listaOrdenada.keySet()) {
            if (n < 10) {
                lineasTopPuntuaciones.add(String.format("%-18s%3d\n", value.getNick(), value.getPuntuacion()).replace(' ', '.'));
            }
            if (value.getNick().equalsIgnoreCase(nickJugador)) {
                tuPuntuacion = "Tu puntuación: " + value.getPuntuacion() + "\n" +
                        "Eres el top " + (n + 1);
            }
            n++;
        }

        for (int i = 0; i < (10 - n); i++) {
            lineasTopPuntuaciones.add("\n");
        }
        for (int i = 0; i < 3; i++) {
            lineasTopPuntuaciones.add("\n");
        }

        lineasTopPuntuaciones.add(tuPuntuacion);
        return lineasTopPuntuaciones;
    }

    public void enviarTopPuntuaciones(SecretKey claveAES, ObjectOutputStream objOS) {
        System.out.println(nombre + "Enviando el TOP Puntuaciones...");
        try {
            objOS.writeObject(encriptarArrayListString(claveAES, topPuntuaciones()));
        } catch (IOException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            System.out.println(nombre + "Error al enviar el top de puntuaciones, la conexión fracasará. Error: " + e.getLocalizedMessage());
            GuardarLogs.logger.log(Level.INFO, "Error al enviar el top de puntuaciones, la conexión fracasará. Error: " + e.getLocalizedMessage());
        }
    }

    public byte[] encriptarArrayListString(SecretKey claveAES, ArrayList<String> arrayList) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);
        for (String element : arrayList) {
            out.writeUTF(element);
        }

        byte[] bytes = baos.toByteArray();
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.ENCRYPT_MODE, claveAES);
        return aesCipher.doFinal(bytes);
    }

    public byte[] encriptarMensaje(SecretKey claveAES, String mensaje) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.ENCRYPT_MODE, claveAES);
        return aesCipher.doFinal(mensaje.getBytes());
    }

    public String desencriptarMensaje(SecretKey claveAES, byte[] mensaje) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.DECRYPT_MODE, claveAES);
        return new String(aesCipher.doFinal(mensaje));
    }

}
