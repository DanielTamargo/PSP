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
import java.nio.ByteBuffer;
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

    private LoginContext loginContext = null;
    private String nickJugador = "nickDelJugadorLoggeado";

    public HiloServidor(int numCliente, SSLSocket socketSSL, KeyPair parejaClaves) {
        this.socketSSL = socketSSL;
        this.parejaClaves = parejaClaves;

        this.nombre = "[Servidor Cliente " + numCliente + "] ";
    }
    
    /**
     * Inicio del hilo, arrancará cuando un cliente se conecte al servidor, este hilo se encargará de la comunicación
     * con dicho cliente, así, otro cliente podrá conectarse al servidor y ser atendido por otro hilo, permitiendo la
     * capacidad multicliente del servidor
     *
     * Al conectarse, el servidor le mandará la clave pública al cliente (sin encriptar), el cliente la recibirá y generará
     * una clave simétrica, la cual encriptará con la clave pública que ha recibido y nos la enviará. Desencriptaremos esa
     * clave simétrica que ha generado utilizando nuestra clave privada, y a partir de entonces ambos tendremos dicha clave
     * simétrica para poder comunicarnos de manera rápida, segura y eficaz
     *
     * Según el cliente vaya seleccionando opciones en la aplicación, iremos recibiendo un int para determinar cómo
     * nos comunicaremos
     *
     * (nota 1: el cliente generará una segunda clave simétrica, la cual será siempre igual, para así encriptar su
     *      * contraseña y nadie a parte del propio cliente podrá saber cuál es dicha contraseña que haya introducido para
     *      * registrarse)
     *
     * Las opciones son:
     * 1 = iniciar sesión, recibiremos el nick y contraseña y realizaremos el proceso de login a través del JAAS implementado
     * notificaremos si el login es válido o no con un boolean
     * (nota 2: si es un login válido, le mandaremos al usuario su nick para poder mostrar colores personalizados en el top
     * puntuaciones, pero sólo recibirá su nick, así evitamos mover información confidencial o de posible interés ajeno)
     * si el login es válido le mandaremos las normas del juego en puro + las normas firmadas para que el cliente las coteje
     * si las comprueba correctamente, podrá pasar a la ventana del juego
     *
     * 2 = registro, recibiremos los datos del usuario a registrar y comprobaremos que el nick indicado no esté en uso
     * no tendremos que comprobar los datos puesto que el cliente tiene establecido unos patrones para afianzar el buen
     * uso de los datos
     * notificaremos si rel registro es válido o no con un boolean
     *
     * 3 = si el usuario consiguió iniciar sesión, podrá iniciar una nueva partida, a través de esta opción pasarémos al método
     * buclePartida() comentado más adelante
     *
     * (nota 3: si hay errores o notificaciones importantes o de interés, se almacenarán en el log correspondiente a su fecha,
     * el server solo almacenará hasta 5 logs, por lo que el sexto día de uso consecutivo borrará el log más antiguo)
     */
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
                            loginContext = null;

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
                            } catch (LoginException ignored) {
                                loginContext = null;
                            }

                            objOS.writeObject(exito);

                            if (exito) {
                                int tipoUsuario = 0;

                                if (loginContext != null) {
                                    Subject sujeto = loginContext.getSubject();
                                    Set<Principal> principales = sujeto.getPrincipals();
                                    for (Principal principale : principales) {
                                        ImplementacionPrincipal principal = (ImplementacionPrincipal) principale;
                                        tipoUsuario = principal.getTipo();
                                    }
                                }

                                objOS.writeObject(tipoUsuario);

                                if (tipoUsuario != 1) { // JUGADOR
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
                                } else { // ADMIN
                                    System.out.println(this.nombre + "Mandándole los logs al administrador loggeado -> " + nickJugador);
                                    GuardarLogs.logger.log(Level.FINE, "Mandando logs al administrador loggeado -> " + nickJugador);
                                    ArrayList<ArrayList<String>> todosLosLogs = LeerFicheros.contenidoTodosLosLogs();

                                    // MANDAMOS EL TOTAL DE LOGS LEIDOS Y LUEGO EL CONTENIDO
                                    objOS.writeObject(todosLosLogs.size());
                                    if (todosLosLogs.size() > 0) {
                                        Collections.reverse(todosLosLogs);
                                        for (ArrayList<String> datosLog : todosLosLogs) {
                                            objOS.writeObject(encriptarArrayListString(claveAES, datosLog));
                                        }
                                        System.out.println(this.nombre + "Datos de los logs enviados con éxito");
                                    }
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
                            System.out.println(this.nombre + "Comenzando una nueva partida con " + nickJugador);
                            GuardarLogs.logger.log(Level.FINE, "Comenzando una nueva partida con el usuario " + nickJugador);
                            buclePartida(claveAES, objOS, objIS);
                        }
                    }
                }
            } catch (EOFException ignored) { }
            catch (SignatureException e) {
                System.out.println(nombre + "Error al firmar las normas. Error: " + e.getLocalizedMessage());
                GuardarLogs.logger.log(Level.INFO, "Error al firmar las normas. Error: " + e.getLocalizedMessage());
            }

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

    /**
     * Bucle de la partida, donde se irán mostrando las preguntas y cotejando las respuestas y puntuaciones
     *
     * Respuestas que recibe el servidor:
     * Si el server lee un 1, significará que el cliente ha respondido, y si lee un 2, significará
     * que el usuario ha abandonado.
     *
     * Respuestas que puede dar el servidor:
     * 0 = el cliente ha acertado y pasa a la siguiente pregunta
     * 1 = el cliente ha acertado pero ya ha acertado todas las preguntas y ha realizado un "pleno"
     * 2 = el cliente ha fallado y no ha conseguido superar su puntuación máxima
     * 3 = el cliente ha fallado pero sí ha conseguido superar su puntuación máxima
     * -1 = el cliente ha abandonado y no ha superado su puntuación máxima
     * -2 = el cliente ha abandonado pero ha conseguido superar su puntuación máxima
     */
    public void buclePartida(SecretKey claveAES, ObjectOutputStream objOS, ObjectInputStream objIS) throws IOException, ClassNotFoundException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        ArrayList<Pregunta> preguntas = LeerFicheros.leerPreguntas();

        if (preguntas.size() > 0) {
            Collections.shuffle(preguntas); // Las barajamos para que salgan en distinto orden
            int pos = 0;
            int puntuacion = 0;
            objOS.writeObject(true); // Existen preguntas, lo notificamos
            System.out.println(nombre + "Enviando la primera pregunta: " + preguntas.get(pos).getTitulo());
            enviarPregunta(datosPreguntaYRespuestas(preguntas.get(pos), puntuacion), claveAES, objOS);
            boolean bucle = true;
            boolean extra;
            int opcion;
            while (bucle) {
                opcion = (Integer) objIS.readObject();
                switch (opcion) {
                    case 1 -> { // RESPONDE
                        String textoRespuesta = desencriptarMensaje(claveAES, (byte[]) objIS.readObject());
                        int tiempo = desencriptarInt(claveAES, (byte[]) objIS.readObject());
                        boolean acierto = preguntas.get(pos).esCorrecta(textoRespuesta);
                        if (acierto) {
                            if (tiempo < 4) {
                                puntuacion += 30;
                                extra = true;
                            } else {
                                puntuacion += 10;
                                extra = false;
                            }
                            pos++;
                            if (pos < preguntas.size()) { // NUEVA PREGUNTA
                                objOS.writeObject(0);
                                objOS.writeObject(extra);
                                System.out.println(nombre + "¡Ha acertado!");
                                enviarPregunta(datosPreguntaYRespuestas(preguntas.get(pos), puntuacion), claveAES, objOS);
                                System.out.println(nombre + "Enviando otra pregunta: " + preguntas.get(pos).getTitulo());
                            } else { // HAS ACERTADO TODAS Y NO QUEDAN MÁS
                                System.out.println(nombre + "Ha acertado todas las preguntas");
                                boolean maxPuntuacion = cotejarPuntuaciones(puntuacion);
                                objOS.writeObject(1);
                                if (maxPuntuacion)
                                    objOS.writeObject(encriptarMensaje(claveAES, String.valueOf(puntuacion + "max")));
                                else
                                    objOS.writeObject(encriptarMensaje(claveAES, String.valueOf(puntuacion)));
                                objOS.writeObject(extra);
                                bucle = false;
                            }
                        } else {
                            if (!cotejarPuntuaciones(puntuacion)) { // HAS FALLADO SIN SUPERAR TU PUNTUACIÓN
                                System.out.println(nombre + "Ha fallado y no ha superado su puntuación máxima");
                                objOS.writeObject(2);
                            } else { // HAS FALLADO PERO HAS SUPERADO TU PUNTUACIÓN
                                System.out.println(nombre + "Ha fallado pero ha logrado superar su puntuación máxima");
                                objOS.writeObject(3);
                            }
                            objOS.writeObject(encriptarMensaje(claveAES, String.valueOf(puntuacion)));
                            bucle = false;
                        }
                    }
                    case 2 -> { // ABANDONA
                        if (!cotejarPuntuaciones(puntuacion)) {
                            System.out.println(nombre + "Ha abandonado sin lograr superar su puntuación máxima");
                            objOS.writeObject(-1);
                        } else {
                            System.out.println(nombre + "Ha abandonado pero ha logrado superar su puntuación máxima");
                            objOS.writeObject(-2);
                        }
                        objOS.writeObject(encriptarMensaje(claveAES, String.valueOf(puntuacion)));
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
     *
     * Utilizará getPrincipals() sacando el sujeto que realizó el login (JAAS)
     * Por si acaso hubiera un problema con el loginContext (que no me ha ocurrido aún, pero más
     * vale prevenir que curar)
     */
    public boolean cotejarPuntuaciones(int puntuacion) {
        boolean mayorPuntuacion = false;
        if (loginContext != null) {
            Subject sujeto = loginContext.getSubject();
            Set<Principal> principales = sujeto.getPrincipals();
            for (Principal principale : principales) {
                ImplementacionPrincipal principal = (ImplementacionPrincipal) principale;
                if (principal.getUsuario().getPuntuacion() < puntuacion) {
                    EscribirFicheros.modificarPuntuacionUsuario(principal.getUsuario().getNick(), puntuacion);
                    principal.getUsuario().setPuntuacion(puntuacion);
                    mayorPuntuacion = true;
                    break;
                }
            }
        } else {
            ArrayList<Usuario> usuarios = LeerFicheros.leerUsuarios();
            for (Usuario usu: usuarios) {
                if (usu.getNick().equalsIgnoreCase(nickJugador)) {
                    if (usu.getPuntuacion() < puntuacion) {
                        EscribirFicheros.modificarPuntuacionUsuario(nickJugador, puntuacion);
                        mayorPuntuacion = true;
                    }
                }
            }
        }
        return mayorPuntuacion;
    }

    /**
     * Método que enviará al cliente el ArrayList de Strings que le indiquemos
     */
    public void enviarPregunta(ArrayList<String> datos, SecretKey claveAES, ObjectOutputStream objOS) throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        objOS.writeObject(encriptarArrayListString(claveAES, datos));
    }

    /**
     * Método utilizado para cargar y devolver un ArrayList de Strings que contendrá información de una
     * pregunta (el título, las 4 respuestas y el tipo) y la puntuación del jugador para mostrarlo en el cliente
     */
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

    /**
     * Método utilizado para cargar y devolver un ArrayList de Strings que
     * contendrá el top 10 de puntuaciones + el top y la puntuación del jugador loggeado
     */
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

    /**
     * Método para, a través del método topPuntuaciones(), cargar un ArrayList de Strings la información del top de
     * puntuaciones y mandárselo al cliente
     */
    public void enviarTopPuntuaciones(SecretKey claveAES, ObjectOutputStream objOS) {
        System.out.println(nombre + "Enviando el TOP Puntuaciones...");
        try {
            objOS.writeObject(encriptarArrayListString(claveAES, topPuntuaciones()));
        } catch (IOException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            System.out.println(nombre + "Error al enviar el top de puntuaciones, la conexión fracasará. Error: " + e.getLocalizedMessage());
            GuardarLogs.logger.log(Level.INFO, "Error al enviar el top de puntuaciones, la conexión fracasará. Error: " + e.getLocalizedMessage());
        }
    }

    /**
     * Utilizaremos este método para pasar de un int a un array de bytes para luego mandarlo encriptado
     */
    public byte[] intToByteArray(int data) {
        byte[] result = new byte[4];
        result[0] = (byte) ((data & 0xFF000000) >> 24);
        result[1] = (byte) ((data & 0x00FF0000) >> 16);
        result[2] = (byte) ((data & 0x0000FF00) >> 8);
        result[3] = (byte) ((data & 0x000000FF));
        return result;
    }

    /**
     * Método para encriptar un ArrayList de Strings
     */
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

    /**
     * Método para encriptar un String
     */
    public byte[] encriptarMensaje(SecretKey claveAES, String mensaje) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.ENCRYPT_MODE, claveAES);
        return aesCipher.doFinal(mensaje.getBytes());
    }

    /**
     * Método para desencriptar un int
     */
    public int desencriptarInt(SecretKey claveAES, byte[] mensaje) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.DECRYPT_MODE, claveAES);
        return ByteBuffer.wrap(aesCipher.doFinal(mensaje)).getInt();
    }

    /**
     * Método para desencriptar un String
     */
    public String desencriptarMensaje(SecretKey claveAES, byte[] mensaje) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.DECRYPT_MODE, claveAES);
        return new String(aesCipher.doFinal(mensaje));
    }

}
