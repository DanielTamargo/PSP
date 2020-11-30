package com.tamargo.ventanas;

import com.tamargo.datos.GuardarLogs;
import com.tamargo.varios.JTextFieldLimit;

import javax.crypto.*;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.security.*;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class Cliente {
    private JFrame ventana;
    private JPanel panel;
    private JPanel panelDatos;

    private String nombre = "[Cliente] ";

    private final Dimension dimPanelDatos = new Dimension(600, 500);
    private PublicKey serverPK;


    // LOGIN + REGISTRO
    private JTextField tNick;
    private JPasswordField tContrasenya;

    // LOGIN
    private JButton b_iniciarSesion;
    private JButton b_LoginRegistrarse;

    // REGISTRO
    private JTextField tNombre;
    private JTextField tApellido;
    private JTextField tEdad;
    private JButton b_volver;
    private JButton b_realizarRegistro;

    // VALIDACION NORMAS
    private JTextPane tpNormas;
    private JLabel hashRecibido;
    private JLabel hashRealizado;
    private JLabel confirmacionHash;
    private JButton b_aceptar;




    public Cliente(JFrame ventana) {
        this.ventana = ventana;
        try {
            // Configuramos las propiedades para que ""reciba"" el certificado (realmente accede a él)
            System.setProperty("javax.net.ssl.trustStore", "./certificados/clienteAlmacenSSL");
            System.setProperty("javax.net.ssl.trustStorePassword", "890123");

            // Nos conectamos
            SSLSocketFactory sfact = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket socketSSL = (SSLSocket) sfact.createSocket("localhost", 6000);
            System.out.println(nombre + "Conexión realizada");

            // Generamos flujos de datos
            DataOutputStream dataOS = new DataOutputStream(socketSSL.getOutputStream());
            DataInputStream dataIS = new DataInputStream(socketSSL.getInputStream());
            ObjectInputStream objIS = new ObjectInputStream(socketSSL.getInputStream());
            ObjectOutputStream objOS = new ObjectOutputStream(socketSSL.getOutputStream());

            // Recibimos clave pública
            System.out.println(nombre + "Recibiendo clave pública");
            serverPK = (PublicKey) objIS.readObject();
            System.out.println(nombre + "Clave pública recibida");
            System.out.println();

            // Enviarremos una clave simétrica que generemos cifrándola con la clave pública recibida
            //      Generamos la clave simétrica
            System.out.println(nombre + "Generando clave simétrica");
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            keygen.init(128); // <- Tamaño clave
            SecretKey claveAES = keygen.generateKey();
            System.out.println(nombre + "Clave simétrica generada: " + claveAES.toString());
            System.out.println();

            // Generar cifrador/descifrador
            //      Primero generamos el cifrador para cifrar la clave simétrica que mandaremos,
            //      y luego el cifrador/descifrador para el túnel
            System.out.println(nombre + "Generando Cipher para encriptar/desencriptar");
            Cipher rsaCipher = Cipher.getInstance("RSA");
            rsaCipher.init(Cipher.ENCRYPT_MODE, serverPK);
            System.out.println(nombre + "Cipher generado y configurado para encriptar");
            byte[] mandarClave = claveAES.getEncoded();
            byte[] mandarClaveAEScifrada = rsaCipher.doFinal(mandarClave);
            System.out.println(nombre + "Clave simétrica encriptada con la clave pública del servidor y preparada para enviar");

            //      Enviamos la clave simétrica cifrada con la clave pública recibida del servidor
            objOS.writeObject(mandarClaveAEScifrada);
            System.out.println(nombre + "Clave simétrica encriptada enviada");
            System.out.println();

            // Ya está establecido un tunel donde podemos enviar y recibir mensajes cifrados con la clave simétrica generada
            // Ejemplos de enviar mensaje desencriptado y encriptado
            //System.out.println(nombre + "El servidor dice: " + desencriptarMensaje(claveAES, (byte[]) objIS.readObject()));
            //objOS.writeObject(encriptarMensaje(claveAES, "Sí, gracias por tanta intimidad"));

            // Proceso:
            //             | registro
            // - login | <-|
            // - validar reglas
            // - nueva partida | historial
            //       jugar          ver
            ventanaLogin(claveAES, objOS, objIS);

            this.ventana.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    try {
                        objOS.writeObject(0);
                    } catch (IOException ignored) {}
                    ventana.dispose();
                }
            });


            //todo quitar
            tNick.setText("dani");
            tContrasenya.setText("test");

        } catch (IOException | NoSuchAlgorithmException |
                NoSuchPaddingException | InvalidKeyException | ClassNotFoundException
                | IllegalBlockSizeException | BadPaddingException e) {
            System.out.println("[Cliente] El cliente ha fallado o el server ha rechazado la conexión. Motivo:\n" + e.getLocalizedMessage());
            GuardarLogs.logger.log(Level.SEVERE, "Error con el servicio del cliente. Error: " + e.getLocalizedMessage());
            ventanaErrorConexion();
        }
    }

    public void listenersLogin(SecretKey claveAES, ObjectOutputStream objOS, ObjectInputStream objIS) {
        b_LoginRegistrarse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ventanaRegistro(claveAES, objOS, objIS);
            }
        });
        b_iniciarSesion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (comprobarPatrones(1)) {
                    try {
                        objOS.writeObject(1);
                        objOS.writeObject(encriptarMensaje(claveAES, tNick.getText()));
                        //TODO ENCRIPTAR CONTRASEÑA ANTES DE ENCRIPTARLA Y MANDARLA
                        objOS.writeObject(encriptarMensaje(claveAES, String.valueOf(tContrasenya.getPassword())));
                        if ((boolean) objIS.readObject()) {
                            ventanaValidarNormas(claveAES, objOS, objIS);
                        } else {
                            String titulo = "Credenciales incorrectas";
                            String mensaje = "Usuario y/o contraseña incorrectos";
                            mostrarJOptionPane(titulo, mensaje, 0);
                        }
                    } catch (IOException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException ex) {
                        String titulo = "Error";
                        String mensaje = "Error al intentar enviar los datos.\nMotivo: " + ex.getLocalizedMessage();
                        mostrarJOptionPane(titulo, mensaje, 0);
                    } catch (ClassNotFoundException ex) {
                        String titulo = "Error";
                        String mensaje = "Error al recibir una respuesta del servidor.\nMotivo: " + ex.getLocalizedMessage();
                        mostrarJOptionPane(titulo, mensaje, 0);
                    }
                }
            }
        });
    }

    public void listenersRegistro(SecretKey claveAES, ObjectOutputStream objOS, ObjectInputStream objIS) {
        b_realizarRegistro.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (comprobarPatrones(2)) {
                    try {
                        objOS.writeObject(2);
                        objOS.writeObject(encriptarMensaje(claveAES, tNombre.getText()));
                        objOS.writeObject(encriptarMensaje(claveAES, tApellido.getText()));
                        objOS.writeObject(encriptarMensaje(claveAES, tEdad.getText()));
                        objOS.writeObject(encriptarMensaje(claveAES, tNick.getText()));
                        //TODO ENCRIPTAR CONTRASEÑA ANTES DE ENCRIPTARLA Y MANDARLA
                        objOS.writeObject(encriptarMensaje(claveAES, String.valueOf(tContrasenya.getPassword())));
                        if ((boolean) objIS.readObject()) {
                            //TODO REGISTRO CORRECTO, VOLVER A LA VENTANA LOGIN
                            String titulo = "Registro realizado";
                            String mensaje = "Te has registrado correctamente";
                            mostrarJOptionPane(titulo, mensaje, 1);
                            ventanaLogin(claveAES, objOS, objIS);
                        } else {
                            String titulo = "Datos no válidos";
                            String mensaje = "El nick ya existe, prueba con otro";
                            mostrarJOptionPane(titulo, mensaje, 0);
                        }
                    } catch (IOException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException ex) {
                        String titulo = "Error";
                        String mensaje = "Error al intentar enviar los datos.\nMotivo: " + ex.getLocalizedMessage();
                        mostrarJOptionPane(titulo, mensaje, 0);
                    } catch (ClassNotFoundException ex) {
                        String titulo = "Error";
                        String mensaje = "Error al recibir una respuesta del servidor.\nMotivo: " + ex.getLocalizedMessage();
                        mostrarJOptionPane(titulo, mensaje, 0);
                    }
                }
            }
        });
        b_volver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ventanaLogin(claveAES, objOS, objIS);
            }
        });
    }

    public void ventanaValidarNormas(SecretKey claveAES, ObjectOutputStream objOS, ObjectInputStream objIS) {
        try {
            panelDatos.removeAll();
            panelDatos.repaint();
        } catch (Exception ignored) { }
        panelDatos.setLayout(null);

        ventana.setTitle("Validar Normas");

        String fuenteMYHUI = "MicrosoftYaHeiUI";

        JLabel cabecera = new JLabel("VALIDACIÓN DE LAS NORMAS", SwingConstants.CENTER);
        configurarLabel(cabecera, fuenteMYHUI, Font.BOLD, 22);
        panelDatos.add(cabecera);
        cabecera.setBounds(0, 20, dimPanelDatos.width, 40);

        JPanel linea = new JPanel();
        linea.setBackground(Color.DARK_GRAY);
        panelDatos.add(linea);
        linea.setBounds(0, 55, dimPanelDatos.width, 8);

        JLabel tituloNormas = new JLabel("NORMAS", SwingConstants.CENTER);
        configurarLabel(tituloNormas, fuenteMYHUI, Font.PLAIN, 15);
        panelDatos.add(tituloNormas);
        tituloNormas.setBounds(0, 95, dimPanelDatos.width, 15);
        
        int margenPDL = 80;
        int panelDatosNormasHeight = 150;
        int panelDatosNormasWidth = dimPanelDatos.width - (margenPDL * 2);
        JPanel panelDatosNormas = new JPanel();
        panelDatosNormas.setLayout(null);
        panelDatosNormas.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 3, true));
        panelDatos.add(panelDatosNormas);
        panelDatosNormas.setBounds(margenPDL, 110, panelDatosNormasWidth, panelDatosNormasHeight);

        tpNormas = new JTextPane();
        panelDatosNormas.add(tpNormas);
        int margenTP = 10;
        tpNormas.setBounds(margenTP, margenTP, panelDatosNormasWidth - (margenTP * 2), panelDatosNormasHeight - (margenTP * 2));
        tpNormas.setOpaque(false);
        tpNormas.setEditable(false);
        //tpNormas.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        tpNormas.setFont(new Font(fuenteMYHUI, Font.BOLD, 12));

        int espacioEntreDatos = 45;
        JLabel tituloValidacion = new JLabel("VALIDACIÓN", SwingConstants.CENTER);
        configurarLabel(tituloValidacion, fuenteMYHUI, Font.PLAIN, 15);
        panelDatos.add(tituloValidacion);
        tituloValidacion.setBounds(0, 100 + 50 + panelDatosNormasHeight, dimPanelDatos.width, 15);

        int panelDatosValidacionHeight = 35;
        int panelDatosValidacionWidth = dimPanelDatos.width - (margenPDL * 2);
        JPanel panelDatosValidacion = new JPanel();
        panelDatosValidacion.setLayout(null);
        panelDatosValidacion.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 3, true));
        panelDatos.add(panelDatosValidacion);
        panelDatosValidacion.setBounds(margenPDL, 115 + 50 + panelDatosNormasHeight, panelDatosValidacionWidth, panelDatosValidacionHeight);

        hashRecibido = new JLabel("", SwingConstants.CENTER);
        configurarLabel(hashRecibido, fuenteMYHUI, Font.BOLD, 12);
        panelDatosValidacion.add(hashRecibido);
        hashRecibido.setBounds(0, 10, panelDatosValidacionWidth, 15);

        int widthBoton = 110;
        b_aceptar = new JButton("Adelante");
        configurarButton(b_aceptar, fuenteMYHUI, Font.BOLD, 15);
        panelDatos.add(b_aceptar);
        b_aceptar.setBounds((dimPanelDatos.width / 2) - (widthBoton / 2), 400, widthBoton, 40);

        // Verificar las normas con su firma
        boolean confirmacion;
        try {
            System.out.println(nombre + "Recibiendo las normas y las normas firmadas del servidor");
            String normas = desencriptarMensaje(claveAES, (byte[]) objIS.readObject());
            byte[] firmaNormas = (byte[]) objIS.readObject();
            Signature verRSA = Signature.getInstance("SHA256withRSA");
            verRSA.initVerify(serverPK);
            verRSA.update(normas.getBytes());
            //verRSA.update("a".getBytes()); // <- Forzar fallo en la validación
            confirmacion = verRSA.verify(firmaNormas);

            tpNormas.setText(normas);

            System.out.println(nombre + "Firma recibida:");
            System.out.println(new String(firmaNormas));
            System.out.println(nombre + "La firma es válida: " + (confirmacion ? "Sí": "No"));
            System.out.println();

        } catch (NoSuchAlgorithmException | SignatureException | ClassNotFoundException | BadPaddingException | InvalidKeyException | NoSuchPaddingException | IOException | IllegalBlockSizeException ignored) {
            confirmacion = false;
        }

        String mensaje;
        if (confirmacion) {
            mensaje = "Las normas se han validado correctamente, puedes continuar";
            hashRecibido.setForeground(new Color(30, 148, 53));
        } else {
            b_aceptar.setText("Volver");
            mensaje = "Imposible validar las normas, no podrás jugar sin conexión segura";
            hashRecibido.setForeground(new Color(150, 35, 23));
        }
        hashRecibido.setText(mensaje);

        boolean finalConfirmacion = confirmacion;
        b_aceptar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (finalConfirmacion) {
                    //TODO pasar a la ventana principal donde se puede iniciar una partida o ver clasificación

                } else {
                    ventanaLogin(claveAES, objOS, objIS);
                }
            }
        });
    }

    public void ventanaLogin(SecretKey claveAES, ObjectOutputStream objOS, ObjectInputStream objIS) {
        try {
            panelDatos.removeAll();
            panelDatos.repaint();
        } catch (Exception ignored) { }
        panelDatos.setLayout(null);

        ventana.setTitle("Login");

        String fuenteMYHUI = "MicrosoftYaHeiUI";

        JLabel cabecera = new JLabel("INICIAR SESIÓN", SwingConstants.CENTER);
        configurarLabel(cabecera, fuenteMYHUI, Font.BOLD, 22);
        panelDatos.add(cabecera);
        cabecera.setBounds(0, 20, dimPanelDatos.width, 40);

        JPanel linea = new JPanel();
        linea.setBackground(Color.DARK_GRAY);
        panelDatos.add(linea);
        linea.setBounds(0, 55, dimPanelDatos.width, 8);

        JLabel tituloDatos = new JLabel("DATOS DE ACCESO", SwingConstants.CENTER);
        configurarLabel(tituloDatos, fuenteMYHUI, Font.PLAIN, 15);
        panelDatos.add(tituloDatos);
        tituloDatos.setBounds(0, 170, dimPanelDatos.width, 15);

        int espacioEntreDatos = 45;
        int margenPDL = 100;
        int panelDatosLoginWidth = dimPanelDatos.width - (margenPDL * 2);
        JPanel panelDatosLogin = new JPanel();
        panelDatosLogin.setLayout(null);
        panelDatosLogin.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 3, true));
        panelDatos.add(panelDatosLogin);
        panelDatosLogin.setBounds(margenPDL, 185, panelDatosLoginWidth, 120);

        int plusTField = 11;
        int datosLoginMargenTop = 22;
        JLabel lNombre = new JLabel("Nick", SwingConstants.RIGHT);
        configurarLabel(lNombre, fuenteMYHUI, Font.BOLD, 12);
        panelDatosLogin.add(lNombre);
        lNombre.setBounds(0, datosLoginMargenTop, ((panelDatosLoginWidth / 2) - 90), 40);

        tNick = new JTextField();
        configurarTextField(tNick, fuenteMYHUI, Font.BOLD, 12, 16);
        panelDatosLogin.add(tNick);
        tNick.setBounds(((panelDatosLoginWidth / 2) - 75), datosLoginMargenTop + plusTField, (panelDatosLoginWidth / 2) + 5, 20);

        datosLoginMargenTop += espacioEntreDatos;
        JLabel lContrasenya = new JLabel("Contraseña", SwingConstants.RIGHT);
        configurarLabel(lContrasenya, fuenteMYHUI, Font.BOLD, 12);
        panelDatosLogin.add(lContrasenya);
        lContrasenya.setBounds(0, datosLoginMargenTop, ((panelDatosLoginWidth / 2) - 90), 40);

        tContrasenya = new JPasswordField();
        configurarTextField(tContrasenya, fuenteMYHUI, Font.BOLD, 12, 20);
        panelDatosLogin.add(tContrasenya);
        tContrasenya.setBounds(((panelDatosLoginWidth / 2) - 75), datosLoginMargenTop + plusTField, (panelDatosLoginWidth / 2) + 5, 20);

        b_LoginRegistrarse = new JButton("Registrarse");
        configurarButton(b_LoginRegistrarse, fuenteMYHUI, Font.BOLD, 15);
        panelDatos.add(b_LoginRegistrarse);
        b_LoginRegistrarse.setBounds((dimPanelDatos.width / 2) - 140 - 5, 400, 140, 40);

        b_iniciarSesion = new JButton("Iniciar Sesión");
        configurarButton(b_iniciarSesion, fuenteMYHUI, Font.BOLD, 15);
        panelDatos.add(b_iniciarSesion);
        b_iniciarSesion.setBounds((dimPanelDatos.width / 2) + 5, 400, 140, 40);

        listenersLogin(claveAES, objOS, objIS);
    }

    public void ventanaRegistro(SecretKey claveAES, ObjectOutputStream objOS, ObjectInputStream objIS) {
        try {
            panelDatos.removeAll();
            panelDatos.repaint();
        } catch (Exception ignored) { }
        panelDatos.setLayout(null);

        ventana.setTitle("Registro");

        String fuenteMYHUI = "MicrosoftYaHeiUI";

        JLabel cabecera = new JLabel("REGISTRO", SwingConstants.CENTER);
        configurarLabel(cabecera, fuenteMYHUI, Font.BOLD, 22);
        panelDatos.add(cabecera);
        cabecera.setBounds(0, 20, dimPanelDatos.width, 40);

        JPanel linea = new JPanel();
        linea.setBackground(Color.DARK_GRAY);
        panelDatos.add(linea);
        linea.setBounds(0, 55, dimPanelDatos.width, 8);

        JLabel tituloDatos = new JLabel("INTRODUCE TUS DATOS", SwingConstants.CENTER);
        configurarLabel(tituloDatos, fuenteMYHUI, Font.PLAIN, 15);
        panelDatos.add(tituloDatos);
        tituloDatos.setBounds(0, 90, dimPanelDatos.width, 15);

        int espacioEntreDatos = 45;
        int margenPDL = 100;
        int panelDatosLoginWidth = dimPanelDatos.width - (margenPDL * 2);
        JPanel panelDatosLogin = new JPanel();
        panelDatosLogin.setLayout(null);
        panelDatosLogin.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 3, true));
        panelDatos.add(panelDatosLogin);
        panelDatosLogin.setBounds(margenPDL, 105, panelDatosLoginWidth, 280);

        int plusTField = 11;
        int datosLoginMargenTop = 22;
        JLabel lNombre = new JLabel("Nombre", SwingConstants.RIGHT);
        configurarLabel(lNombre, fuenteMYHUI, Font.BOLD, 12);
        panelDatosLogin.add(lNombre);
        lNombre.setBounds(0, datosLoginMargenTop, ((panelDatosLoginWidth / 2) - 90), 40);

        tNombre = new JTextField();
        configurarTextField(tNombre, fuenteMYHUI, Font.BOLD, 12, 20);
        panelDatosLogin.add(tNombre);
        tNombre.setBounds(((panelDatosLoginWidth / 2) - 75), datosLoginMargenTop + plusTField, (panelDatosLoginWidth / 2) + 5, 20);

        datosLoginMargenTop += espacioEntreDatos;
        JLabel lApellido = new JLabel("Apellido", SwingConstants.RIGHT);
        configurarLabel(lApellido, fuenteMYHUI, Font.BOLD, 12);
        panelDatosLogin.add(lApellido);
        lApellido.setBounds(0, datosLoginMargenTop, ((panelDatosLoginWidth / 2) - 90), 40);

        tApellido = new JTextField();
        configurarTextField(tApellido, fuenteMYHUI, Font.BOLD, 12, 20);
        panelDatosLogin.add(tApellido);
        tApellido.setBounds(((panelDatosLoginWidth / 2) - 75), datosLoginMargenTop + plusTField, (panelDatosLoginWidth / 2) + 5, 20);

        datosLoginMargenTop += espacioEntreDatos;
        JLabel lEdad = new JLabel("Edad", SwingConstants.RIGHT);
        configurarLabel(lEdad, fuenteMYHUI, Font.BOLD, 12);
        panelDatosLogin.add(lEdad);
        lEdad.setBounds(0, datosLoginMargenTop, ((panelDatosLoginWidth / 2) - 90), 40);

        tEdad = new JTextField();
        configurarTextField(tEdad, fuenteMYHUI, Font.BOLD, 12, 3);
        panelDatosLogin.add(tEdad);
        tEdad.setBounds(((panelDatosLoginWidth / 2) - 75), datosLoginMargenTop + plusTField, 50, 20);

        datosLoginMargenTop += espacioEntreDatos;
        JLabel lNick = new JLabel("Nick", SwingConstants.RIGHT);
        configurarLabel(lNick, fuenteMYHUI, Font.BOLD, 12);
        panelDatosLogin.add(lNick);
        lNick.setBounds(0, datosLoginMargenTop, ((panelDatosLoginWidth / 2) - 90), 40);

        tNick = new JTextField();
        configurarTextField(tNick, fuenteMYHUI, Font.BOLD, 12, 16);
        panelDatosLogin.add(tNick);
        tNick.setBounds(((panelDatosLoginWidth / 2) - 75), datosLoginMargenTop + plusTField, (panelDatosLoginWidth / 2) + 5, 20);

        datosLoginMargenTop += espacioEntreDatos;
        JLabel lContrasenya = new JLabel("Contraseña", SwingConstants.RIGHT);
        configurarLabel(lContrasenya, fuenteMYHUI, Font.BOLD, 12);
        panelDatosLogin.add(lContrasenya);
        lContrasenya.setBounds(0, datosLoginMargenTop, ((panelDatosLoginWidth / 2) - 90), 40);

        tContrasenya = new JPasswordField();
        configurarTextField(tContrasenya, fuenteMYHUI, Font.BOLD, 12, 20);
        panelDatosLogin.add(tContrasenya);
        tContrasenya.setBounds(((panelDatosLoginWidth / 2) - 75), datosLoginMargenTop + plusTField, (panelDatosLoginWidth / 2) + 5, 20);

        b_volver = new JButton("Volver");
        configurarButton(b_volver, fuenteMYHUI, Font.BOLD, 15);
        panelDatos.add(b_volver);
        b_volver.setBounds((dimPanelDatos.width / 2) - 140 - 5, 400, 140, 40);

        b_realizarRegistro = new JButton("Registrarse");
        configurarButton(b_realizarRegistro, fuenteMYHUI, Font.BOLD, 15);
        panelDatos.add(b_realizarRegistro);
        b_realizarRegistro.setBounds((dimPanelDatos.width / 2) + 5, 400, 140, 40);

        listenersRegistro(claveAES, objOS, objIS);
    }

    public boolean comprobarPatrones(int tipo) {
        boolean correcto = true;
        String titulo = "Error al recoger datos";
        String mensaje = "";

        Pattern nickPattern = Pattern.compile("[a-zA-Z0-9]{3,16}");
        Pattern contrasenyaPattern = Pattern.compile(".{3,20}");

        String nick = tNick.getText();
        String contrasenya = String.valueOf(tContrasenya.getPassword());

        if (tipo == 2) {
            Pattern nombrePattern = Pattern.compile("[a-zA-Z]{3,20}");
            Pattern apellidoPattern = Pattern.compile("[a-zA-Z]{3,20}");
            Pattern edadPattern = Pattern.compile("[1-9][0-9]{0,2}");
            String nombre = tNombre.getText();
            String apellido = tApellido.getText();
            String edad = tEdad.getText();
            if (!nombrePattern.matcher(nombre).matches()) {
                mensaje += "El patrón del nombre no es correcto.\nDebe contener entre 3 y 20 letras (sin espacios).\n";
                correcto = false;
            }
            if (!apellidoPattern.matcher(apellido).matches()) {
                if (!mensaje.equalsIgnoreCase(""))
                    mensaje += "\n";
                mensaje += "El patrón del apellido no es correcto.\nDebe contener entre 3 y 20 letras (sin espacios).\n";
                correcto = false;
            }
            if (!edadPattern.matcher(edad).matches()) {
                if (!mensaje.equalsIgnoreCase(""))
                    mensaje += "\n";
                mensaje += "El patrón de la edad no es correcto.\nDebe contener entre 1 y 3 números (sin decimales).\n";
                correcto = false;
            }
        }
        if (!nickPattern.matcher(nick).matches()) {
            if (!mensaje.equalsIgnoreCase(""))
                mensaje += "\n";
            mensaje += "El patrón del nick no es correcto.\nDebe contener entre 3 y 16 letras o números (sin espacios).\n";
            correcto = false;
        }
        if (!contrasenyaPattern.matcher(contrasenya).matches() || contrasenya.contains(" ")) {
            if (!mensaje.equalsIgnoreCase(""))
                mensaje += "\n";
            mensaje += "El patrón del contraseña no es correcto.\nDebe contener entre 3 y 20 letras, números o símbolos (sin espacios).\n";
            correcto = false;
        }

        if (!correcto)
            mostrarJOptionPane(titulo, mensaje, 0);
        
        return correcto;
    }

    public void mostrarJOptionPane(String titulo, String mensaje, int tipo) {
        JButton okButton = new JButton("Ok");
        okButton.setFocusPainted(false);
        Object[] options = {okButton};
        final JOptionPane pane = new JOptionPane(mensaje, tipo, JOptionPane.YES_NO_OPTION, null, options);
        JDialog dialog = pane.createDialog(titulo);
        okButton.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }
    public void configurarLabel(JLabel label, String fuente, int tipo, int size) {
        label.setFont(new Font(fuente, tipo, size));
        label.setForeground(Color.DARK_GRAY);
    }
    public void configurarTextField(JTextField tField, String fuente, int tipo, int size, int maxCaracteres) {
        tField.setFont(new Font(fuente, tipo, size));
        tField.setForeground(Color.DARK_GRAY);
        tField.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        tField.setDocument(new JTextFieldLimit(maxCaracteres));
    }
    public void configurarButton(JButton boton, String fuente, int tipo, int size) {
        boton.setFont(new Font(fuente, tipo, size));
        boton.setForeground(Color.DARK_GRAY);
        boton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        boton.setFocusPainted(false);
    }
    public void configurarPasswordField(JPasswordField tField, String fuente, int tipo, int size, int maxCaracteres) {
        tField.setFont(new Font(fuente, tipo, size));
        tField.setForeground(Color.DARK_GRAY);
        tField.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        tField.setDocument(new JTextFieldLimit(maxCaracteres));
    }
    public void ventanaErrorConexion() {
        JButton okButton = new JButton("Entendido");
        okButton.setFocusPainted(false);
        Object[] options = {okButton};
        final JOptionPane pane = new JOptionPane("""
                Error al conectar con el servidor. Posibles motivos:
                1. El servidor no está en marcha / no arranca debidamente
                2. No están generados la claveSSL del Servidor ni/o su certificado
                3. No está correctamente configurada la confianza con el certificado
                
                Comprueba los posibles errores y vuelve a iniciar el cliente.""", JOptionPane.ERROR_MESSAGE, JOptionPane.YES_NO_OPTION, null, options);
        JDialog dialog = pane.createDialog("Imposible conectar");
        okButton.addActionListener(e -> {
            System.exit(0);
        });
        dialog.setVisible(true);
    }

    public String desencriptarMensaje(SecretKey claveAES, byte[] mensaje) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.DECRYPT_MODE, claveAES);
        return new String(aesCipher.doFinal(mensaje));
    }
    public byte[] encriptarMensaje(SecretKey claveAES, String mensaje) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.ENCRYPT_MODE, claveAES);
        return aesCipher.doFinal(mensaje.getBytes());
    }

    public void setVentana(JFrame ventana) {
        this.ventana = ventana;
    }

    public JPanel getPanel() {
        return panel;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Login");
        Cliente vc = new Cliente(frame);
        frame.setContentPane(vc.getPanel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
