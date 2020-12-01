package com.tamargo.servicio;

import com.tamargo.datos.EscribirFicheros;
import com.tamargo.datos.GuardarLogs;
import com.tamargo.datos.Usuario;
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
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;

public class HiloServidor extends Thread {
    private final String nombre;

    private final SSLSocket socketSSL;
    private final KeyPair parejaClaves;

    private DataInputStream dataIS;
    private DataOutputStream dataOS;
    private ObjectInputStream objIS;
    private ObjectOutputStream objOS;

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
                                System.out.println(nombre + "Enviando firma normas encriptada:");
                                System.out.println(new String(firmaNormas));

                                objOS.writeObject(encriptarMensaje(claveAES, normas));
                                objOS.writeObject(firmaNormas);
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
