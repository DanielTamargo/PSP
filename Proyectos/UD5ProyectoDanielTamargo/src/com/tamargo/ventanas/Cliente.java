package com.tamargo.ventanas;

import com.tamargo.datos.GuardarLogs;
import com.tamargo.varios.JTextFieldLimit;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.security.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class Cliente {

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // VARIABLES

    // PRINCIPALES
    private final JFrame ventana;
    private String nickJugador = "nickdeljugador";
    private JPanel panel;
    private JPanel panelDatos;

    private final String nombre = "[Cliente] ";
    private JLabel cabecera;

    private final Dimension dimPanelDatos = new Dimension(600, 500);
    private final Dimension dimPanelPartida = new Dimension(360, 400);
    private final Dimension dimPanelPuntuaciones = new Dimension(210, 340);
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
    private JButton b_aceptar;

    // MENU
    private JPanel panelPartida;
    private JPanel panelPuntuaciones;

    private JButton b_nuevaPartida;
    private JButton b_actualizarPuntuaciones;
    private JButton b_cerrarSesion;

    // PARTIDA
    private JTextPane tpPregunta;
    private JButton b_respuesta1;
    private JButton b_respuesta2;
    private JButton b_respuesta3;
    private JButton b_respuesta4;
    private JLabel puntuacionPartida;
    private JLabel tipoPreguntaPartida;
    private JButton b_abandonar;
    private ArrayList<String> textoRespuestas = new ArrayList<>();
    private ArrayList<String> datosPregunta = new ArrayList<>();

    // PUNTUACIONES
    private JTextPane tpPuntuaciones;
    private ArrayList<String> topPuntuaciones = new ArrayList<>();

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    public Cliente(JFrame ventana) {
        this.ventana = ventana;
        //iniciarCliente();

        ventanaAdmin(null, null, null);
    }

    // LANZAR VENTANA
    public void iniciarCliente() {
        try {
            // Configuramos las propiedades para que confié en el certificado que hemos ""recibido"" (realmente está en local)
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

            // Recibimos la clave pública del servidor
            System.out.println(nombre + "Recibiendo clave pública");
            serverPK = (PublicKey) objIS.readObject();
            System.out.println(nombre + "Clave pública recibida");
            System.out.println();

            // Enviaremos una clave simétrica que generemos cifrándola con la clave pública recibida
            //      Generamos la clave simétrica
            System.out.println(nombre + "Generando clave simétrica");
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            keygen.init(128); // <- Tamaño clave
            SecretKey claveAES = keygen.generateKey();
            System.out.println(nombre + "Clave simétrica generada: " + claveAES.toString());
            System.out.println();

            //      Creamos cifrador/descifrador
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
                    } catch (IOException ignored) { }
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
            ventanaErrorConexion();
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // LISTENERS
    // LOGIN
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

                        byte[] contrasenyaEncriptada = encriptarContrasenya();
                        //System.out.println(new String(contrasenyaEncriptada));

                        // Encriptar como un mensaje normal y enviar
                        byte[] contrasenyaEncriptadaEncriptada = encriptarMensajeBytes(claveAES, contrasenyaEncriptada);
                        objOS.writeObject(contrasenyaEncriptadaEncriptada);

                        if ((boolean) objIS.readObject()) {
                            nickJugador = tNick.getText();
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
                    } catch (InvalidAlgorithmParameterException ex) {
                        String titulo = "Error";
                        String mensaje = "Error al preparar el encriptador de la contraseña.\nMotivo: " + ex.getLocalizedMessage();
                        mostrarJOptionPane(titulo, mensaje, 0);
                    }
                }
            }
        });
    }
    // REGISTRO
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
                        byte[] contrasenyaEncriptada = encriptarContrasenya();
                        byte[] contrasenyaEncriptadaEncriptada = encriptarMensajeBytes(claveAES, contrasenyaEncriptada);
                        objOS.writeObject(contrasenyaEncriptadaEncriptada);
                        if ((boolean) objIS.readObject()) {
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
                    } catch (InvalidAlgorithmParameterException ex) {
                        String titulo = "Error";
                        String mensaje = "Error al preparar el encriptador de la contraseña.\nMotivo: " + ex.getLocalizedMessage();
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
    // VALIDACIÓN
    public void listenersValidacion(Boolean confirmacion, SecretKey claveAES, ObjectOutputStream objOS, ObjectInputStream objIS) {
        boolean finalConfirmacion = confirmacion;
        b_aceptar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (finalConfirmacion) {
                    ventanaMenuPrincipal(claveAES, objOS, objIS);
                } else {
                    ventanaLogin(claveAES, objOS, objIS);
                }
            }
        });
    }
    // MENÚ PRINCIPAL
    public void listenersMenuPrincipal(SecretKey claveAES, ObjectOutputStream objOS, ObjectInputStream objIS) {
        b_cerrarSesion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nickJugador = "nickdeljugador";
                ventanaLogin(claveAES, objOS, objIS);
            }
        });
    }
    // PANEL PARTIDA - VERSIÓN MENÚ
    public void listenersPanelPartidaMenu(SecretKey claveAES, ObjectOutputStream objOS, ObjectInputStream objIS) {
        b_nuevaPartida.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                b_cerrarSesion.setEnabled(false);
                try {
                    objOS.writeObject(3); // 3 = nueva partida
                    boolean existenPreguntas = (boolean) objIS.readObject();
                    if (existenPreguntas) {
                        ventanaPanelPartidaJugando(claveAES, objOS, objIS);
                        datosPregunta = desencriptarArrayListString(claveAES, (byte[]) objIS.readObject());
                        volcarDatosPregunta();
                    } else {
                        mostrarJOptionPane("Oops!", "Parece que el servidor no tiene preguntas registradas\n" +
                                "en la BBDD ahora mismo. ¡Prueba a jugar más tarde!", 2);
                    }
                } catch (IOException | ClassNotFoundException | NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException ignored) { }


            }
        });
    }
    // PANEL PARTIDA - VERSIÓN IN GAME
    public void listenersPanelPartidaInGame(SecretKey claveAES, ObjectOutputStream objOS, ObjectInputStream objIS) {
        b_abandonar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                b_cerrarSesion.setEnabled(true);
                inGameEnviarRespuestaAlServidor(2, "", claveAES, objOS, objIS);
                ventanaPanelPartidaMenu(claveAES, objOS, objIS);
                ventanaPanelPuntuaciones(claveAES, objOS, objIS);
            }
        });
        b_respuesta1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String texto = "";
                try {
                    texto = textoRespuestas.get(0);
                } catch (NullPointerException | IndexOutOfBoundsException ignored) {
                    mostrarJOptionPane("Error", "Error al responder al servidor", 0);
                    ventanaPanelPartidaMenu(claveAES, objOS, objIS);
                }

                inGameEnviarRespuestaAlServidor(1, texto, claveAES, objOS, objIS);
            }
        });
        b_respuesta2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String texto = "";
                try {
                    texto = textoRespuestas.get(1);
                } catch (NullPointerException | IndexOutOfBoundsException ignored) {
                    mostrarJOptionPane("Error", "Error al responder al servidor", 0);
                    ventanaPanelPartidaMenu(claveAES, objOS, objIS);
                }

                inGameEnviarRespuestaAlServidor(1, texto, claveAES, objOS, objIS);
            }
        });
        b_respuesta3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String texto = "";
                try {
                    texto = textoRespuestas.get(2);
                } catch (NullPointerException | IndexOutOfBoundsException ignored) {
                    mostrarJOptionPane("Error", "Error al responder al servidor", 0);
                    ventanaPanelPartidaMenu(claveAES, objOS, objIS);
                }

                inGameEnviarRespuestaAlServidor(1, texto, claveAES, objOS, objIS);
            }
        });
        b_respuesta4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String texto = "";
                try {
                    texto = textoRespuestas.get(3);
                } catch (NullPointerException | IndexOutOfBoundsException ignored) {
                    mostrarJOptionPane("Error", "Error al responder al servidor", 0);
                    ventanaPanelPartidaMenu(claveAES, objOS, objIS);
                }

                inGameEnviarRespuestaAlServidor(1, texto, claveAES, objOS, objIS);
            }
        });
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // VENTANAS
    public void ventanaAdmin(SecretKey claveAES, ObjectOutputStream objOS, ObjectInputStream objIS) {
        try {
            panelDatos.removeAll();
            panelDatos.repaint();
        } catch (Exception ignored) { }
        panelDatos.setLayout(null);

        ventana.setTitle("Administrador");
        String fuenteMYHUI = "MicrosoftYaHeiUI";

        cabecera = new JLabel("COMPROBAR LOGS", SwingConstants.CENTER);
        configurarLabel(cabecera, fuenteMYHUI, Font.BOLD, 22);
        panelDatos.add(cabecera);
        cabecera.setBounds(0, 20, dimPanelDatos.width, 40);

        JPanel linea = new JPanel();
        linea.setBackground(Color.DARK_GRAY);
        panelDatos.add(linea);
        linea.setBounds(0, 55, dimPanelDatos.width, 8);

        int margenPDL = 10;
        int posYDatosLogs = 120;
        int panelDatosNormasHeight = dimPanelDatos.height - posYDatosLogs - 10;
        int panelDatosNormasWidth = dimPanelDatos.width - (margenPDL * 2);
        JPanel panelDatosLogs = new JPanel();
        panelDatosLogs.setLayout(null);
        panelDatosLogs.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 3, true));
        panelDatos.add(panelDatosLogs);
        panelDatosLogs.setBounds(margenPDL, posYDatosLogs, panelDatosNormasWidth, panelDatosNormasHeight);

        JTextPane tpNormas = new JTextPane();
        int margenTP = 10;
        tpNormas.setBounds(margenTP, margenTP, panelDatosNormasWidth - (margenTP * 2), panelDatosNormasHeight - (margenTP * 2));
        tpNormas.setOpaque(false);
        tpNormas.setEditable(false);
        //tpNormas.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        tpNormas.setFont(new Font(fuenteMYHUI, Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(tpNormas);
        panelDatosLogs.add(scrollPane);
        scrollPane.setBounds(0, 0, panelDatosNormasWidth, panelDatosNormasHeight);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

        posYDatosLogs -= 50;
        int widthBoton = 100;
        int heightBoton = 40;
        int distanciaEntreBotones = 10;

        JButton b_fine = new JButton("Fine");
        configurarButton(b_fine, fuenteMYHUI, Font.BOLD, 14);
        panelDatos.add(b_fine);
        b_fine.setBounds(margenPDL, posYDatosLogs, widthBoton, heightBoton);

        JButton b_warning = new JButton("Warning");
        configurarButton(b_warning, fuenteMYHUI, Font.BOLD, 14);
        panelDatos.add(b_warning);
        b_warning.setBounds(margenPDL + distanciaEntreBotones + widthBoton, posYDatosLogs, widthBoton, heightBoton);

        JButton b_severe = new JButton("Severe");
        configurarButton(b_severe, fuenteMYHUI, Font.BOLD, 14);
        panelDatos.add(b_severe);
        b_severe.setBounds(margenPDL + (distanciaEntreBotones + widthBoton) * 2, posYDatosLogs, widthBoton, heightBoton);

        JButton b_cerrarSesion = new JButton("Cerrar Sesión");
        configurarButton(b_cerrarSesion, fuenteMYHUI, Font.BOLD, 14);
        panelDatos.add(b_cerrarSesion);
        b_cerrarSesion.setBounds(440, posYDatosLogs, 150, heightBoton);




    }
    public void ventanaPanelPuntuaciones(SecretKey claveAES, ObjectOutputStream objOS, ObjectInputStream objIS) {
        try {
            panelPuntuaciones.removeAll();
            panelPuntuaciones.repaint();
        } catch (Exception ignored) { }
        panelPuntuaciones.setLayout(null);

        String fuenteMYHUI = "MicrosoftYaHeiUI";

        JLabel cabeceraPuntuaciones = new JLabel("PUNTUACIONES", SwingConstants.CENTER);
        configurarLabel(cabeceraPuntuaciones, fuenteMYHUI, Font.BOLD, 14);
        panelPuntuaciones.add(cabeceraPuntuaciones);
        cabeceraPuntuaciones.setBounds(0, 10, dimPanelPuntuaciones.width, 16);

        JPanel linea = new JPanel();
        linea.setBackground(Color.DARK_GRAY);
        panelPuntuaciones.add(linea);
        linea.setBounds(0, 35, dimPanelPuntuaciones.width, 4);

        tpPuntuaciones = new JTextPane();
        panelPuntuaciones.add(tpPuntuaciones);
        int margenX = 8;
        int margenY = 50;
        tpPuntuaciones.setBounds(margenX, margenY, dimPanelPuntuaciones.width - (margenX * 2), dimPanelPuntuaciones.height - (margenY + 10));
        tpPuntuaciones.setOpaque(false);
        tpPuntuaciones.setEditable(false);
        //tpNormas.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        tpPuntuaciones.setFont(new Font("Unispace", Font.BOLD, 14));

        volcarDatosTextPane(tpPuntuaciones, topPuntuaciones, 2);
    }
    public void ventanaPanelPartidaJugando(SecretKey claveAES, ObjectOutputStream objOS, ObjectInputStream objIS) {
        try {
            panelPartida.removeAll();
            panelPartida.repaint();
        } catch (Exception ignored) { }
        panelPartida.setLayout(null);

        ventana.setTitle("Partida");
        String fuenteMYHUI = "MicrosoftYaHeiUI";

        cabecera.setText("JUGANDO PARTIDA");

        JLabel cabeceraPreg = new JLabel("PREGUNTA", SwingConstants.CENTER);
        configurarLabel(cabeceraPreg, fuenteMYHUI, Font.BOLD, 12);
        panelPartida.add(cabeceraPreg);
        cabeceraPreg.setBounds(0, 20, dimPanelPartida.width, 20);
        cabeceraPreg.setForeground(Color.GRAY);

        int margenX = 10;
        tpPregunta =  new JTextPane();
        panelPartida.add(tpPregunta);
        tpPregunta.setBounds(margenX, 40, dimPanelPartida.width - (margenX * 2), 80);
        tpPregunta.setOpaque(false);
        tpPregunta.setEditable(false);
        tpPregunta.setFont(new Font(fuenteMYHUI, Font.BOLD, 14));
        tpPregunta.setForeground(Color.DARK_GRAY);
        StyledDocument doc = tpPregunta.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);

        int posYboton = 130;
        int espaciadoEntreBotones = 10;
        int heightBoton = 80;
        int widthBoton = (dimPanelPartida.width - ((margenX * 2) + espaciadoEntreBotones)) / 2 ;

        b_respuesta1 = new JButton("<html>Respuesta1<br>Linea 2</html>");
        configurarButton(b_respuesta1, fuenteMYHUI, Font.BOLD, 12);
        panelPartida.add(b_respuesta1);
        b_respuesta1.setBounds(margenX, posYboton, widthBoton, heightBoton);

        b_respuesta2 = new JButton("<html>Respuesta2<br>Linea 2</html>");
        configurarButton(b_respuesta2, fuenteMYHUI, Font.BOLD, 12);
        panelPartida.add(b_respuesta2);
        b_respuesta2.setBounds(margenX + widthBoton + espaciadoEntreBotones, posYboton, widthBoton, heightBoton);

        b_respuesta3 = new JButton("<html>Respuesta3<br>Linea 2</html>");
        configurarButton(b_respuesta3, fuenteMYHUI, Font.BOLD, 12);
        panelPartida.add(b_respuesta3);
        b_respuesta3.setBounds(margenX, posYboton + heightBoton + espaciadoEntreBotones, widthBoton, heightBoton);

        b_respuesta4 = new JButton("<html>Respuesta4<br>Linea 2</html>");
        configurarButton(b_respuesta4, fuenteMYHUI, Font.BOLD, 12);
        panelPartida.add(b_respuesta4);
        b_respuesta4.setBounds(margenX + widthBoton + espaciadoEntreBotones,
                posYboton + heightBoton + espaciadoEntreBotones,
                widthBoton, heightBoton);


        puntuacionPartida = new JLabel("Puntos: 0");
        configurarLabel(puntuacionPartida, fuenteMYHUI, Font.BOLD, 12);
        panelPartida.add(puntuacionPartida);
        puntuacionPartida.setBounds(margenX, dimPanelPartida.height - 40, 250, 20);

        tipoPreguntaPartida = new JLabel("Tipo pregunta: Gaming");
        configurarLabel(tipoPreguntaPartida, fuenteMYHUI, Font.BOLD, 12);
        panelPartida.add(tipoPreguntaPartida);
        tipoPreguntaPartida.setBounds(margenX, dimPanelPartida.height - 60, 250, 20);

        int widthAbandonar = 120;
        b_abandonar = new JButton("Abandonar");
        configurarButton(b_abandonar, fuenteMYHUI, Font.BOLD, 14);
        panelPartida.add(b_abandonar);
        b_abandonar.setBounds((dimPanelPartida.width - margenX) - widthAbandonar, dimPanelPartida.height - 60,
                widthAbandonar, 40);


        String ejemploPreg = "Pregunta de Ejemplo\nAquí se cargarán las\nfuturas preguntas que recibiremos";
        tpPregunta.setText(ejemploPreg);

        listenersPanelPartidaInGame(claveAES, objOS, objIS);
    }
    public void ventanaPanelPartidaMenu(SecretKey claveAES, ObjectOutputStream objOS, ObjectInputStream objIS) {
        try {
            panelPartida.removeAll();
            panelPartida.repaint();
        } catch (Exception ignored) { }
        panelPartida.setLayout(null);

        cabecera.setText("MENÚ");

        ventana.setTitle("Menú");
        String fuenteMYHUI = "MicrosoftYaHeiUI";

        // TODO cargar gif para empezar nueba partida??
        //  newlabel
        //  labelseticon
        //  panel.addlabel
        //  labelsetbounds

        b_nuevaPartida = new JButton("Nueva Partida");
        configurarButton(b_nuevaPartida, fuenteMYHUI, Font.BOLD, 15);
        panelPartida.add(b_nuevaPartida);
        int npHeight = 40;
        int npWidth = 120;
        b_nuevaPartida.setBounds(((dimPanelPartida.width / 2) - (npWidth / 2)),
                ((dimPanelPartida.height / 2) - (npHeight / 2)), npWidth, npHeight);

        listenersPanelPartidaMenu(claveAES, objOS, objIS);
    }
    public void ventanaMenuPrincipal(SecretKey claveAES, ObjectOutputStream objOS, ObjectInputStream objIS) {
        try {
            panelDatos.removeAll();
            panelDatos.repaint();
        } catch (Exception ignored) { }
        panelDatos.setLayout(null);

        ventana.setTitle("Menú");
        String fuenteMYHUI = "MicrosoftYaHeiUI";

        cabecera = new JLabel("MENÚ", SwingConstants.CENTER);
        configurarLabel(cabecera, fuenteMYHUI, Font.BOLD, 22);
        panelDatos.add(cabecera);
        cabecera.setBounds(0, 20, dimPanelDatos.width, 40);

        JPanel linea = new JPanel();
        linea.setBackground(Color.DARK_GRAY);
        panelDatos.add(linea);
        linea.setBounds(0, 55, dimPanelDatos.width, 8);

        int margenIzq = 10;

        panelPartida = new JPanel();
        panelPartida.setLayout(null);
        panelPartida.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 3, true));
        panelDatos.add(panelPartida);
        panelPartida.setBounds(margenIzq, 80, dimPanelPartida.width, dimPanelPartida.height);

        panelPuntuaciones = new JPanel();
        panelPuntuaciones.setLayout(null);
        panelPuntuaciones.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 3, true));
        panelDatos.add(panelPuntuaciones);
        panelPuntuaciones.setBounds(margenIzq + margenIzq + dimPanelPartida.width, 80, dimPanelPuntuaciones.width, dimPanelPuntuaciones.height);

        b_cerrarSesion = new JButton("Cerrar Sesión");
        configurarButton(b_cerrarSesion, fuenteMYHUI, Font.BOLD, 15);
        panelDatos.add(b_cerrarSesion);
        b_cerrarSesion.setBounds(margenIzq + margenIzq + dimPanelPartida.width, 80 + dimPanelPuntuaciones.height + 20,
                dimPanelDatos.width - (margenIzq + margenIzq + dimPanelPartida.width + 10), 40);

        try {
            topPuntuaciones = desencriptarArrayListString(claveAES, (byte[]) objIS.readObject());
        } catch (IOException | ClassNotFoundException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException ignored) { }

        listenersMenuPrincipal(claveAES, objOS, objIS);

        ventanaPanelPartidaMenu(claveAES, objOS, objIS);
        ventanaPanelPuntuaciones(claveAES, objOS, objIS);
    }
    public void ventanaValidarNormas(SecretKey claveAES, ObjectOutputStream objOS, ObjectInputStream objIS) {
        try {
            panelDatos.removeAll();
            panelDatos.repaint();
        } catch (Exception ignored) { }
        panelDatos.setLayout(null);

        ventana.setTitle("Validar Normas");
        String fuenteMYHUI = "MicrosoftYaHeiUI";

        cabecera = new JLabel("VALIDACIÓN DE LAS NORMAS", SwingConstants.CENTER);
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

        JTextPane tpNormas = new JTextPane();
        panelDatosNormas.add(tpNormas);
        int margenTP = 10;
        tpNormas.setBounds(margenTP, margenTP, panelDatosNormasWidth - (margenTP * 2), panelDatosNormasHeight - (margenTP * 2));
        tpNormas.setOpaque(false);
        tpNormas.setEditable(false);
        //tpNormas.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        tpNormas.setFont(new Font(fuenteMYHUI, Font.BOLD, 12));

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

        JLabel hashRecibido = new JLabel("", SwingConstants.CENTER);
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

        try {
            objOS.writeObject(confirmacion);
        } catch (IOException ignored) { }

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

        listenersValidacion(confirmacion, claveAES, objOS, objIS);
    }
    public void ventanaLogin(SecretKey claveAES, ObjectOutputStream objOS, ObjectInputStream objIS) {
        try {
            panelDatos.removeAll();
            panelDatos.repaint();
        } catch (Exception ignored) { }
        panelDatos.setLayout(null);

        ventana.setTitle("Login");
        String fuenteMYHUI = "MicrosoftYaHeiUI";

        cabecera = new JLabel("INICIAR SESIÓN", SwingConstants.CENTER);
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

        cabecera = new JLabel("REGISTRO", SwingConstants.CENTER);
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
    public void ventanaErrorConexion() {
        JButton salir = new JButton("Salir");
        JButton volverIntentar = new JButton("Volver a Intentar");
        salir.setFocusPainted(false);
        Object[] options = {salir, volverIntentar};
        final JOptionPane pane = new JOptionPane("""
                Error al conectar con el servidor. Posibles motivos:
                1. El servidor no está en marcha / no arranca debidamente
                2. No están generados la claveSSL del Servidor o su certificado
                3. No está correctamente configurada la confianza con el certificado
                
                Comprueba los posibles errores y vuelve a iniciar el cliente.""", JOptionPane.ERROR_MESSAGE, JOptionPane.YES_NO_OPTION, null, options);
        JDialog dialog = pane.createDialog("Imposible conectar");
        salir.addActionListener(e -> System.exit(0));
        volverIntentar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
                iniciarCliente();
            }
        });
        dialog.setVisible(true);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // MÉTODOS ENCRIPTACIÓN
    public ArrayList<String> desencriptarArrayListString(SecretKey claveAES, byte[] mensaje) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        ArrayList<String> datos = new ArrayList<>();
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.DECRYPT_MODE, claveAES);
        byte[] bytes = aesCipher.doFinal(mensaje);

        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        DataInputStream in = new DataInputStream(bais);
        while (in.available() > 0) {
            String element = in.readUTF();
            datos.add(element);
        }

        return datos;
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
    public byte[] encriptarMensajeBytes(SecretKey claveAES, byte[] mensaje) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.ENCRYPT_MODE, claveAES);
        return aesCipher.doFinal(mensaje);
    }
    public byte[] encriptarContrasenya() throws UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        // ENCRIPTAMOS LA CONTRASEÑA PARA QUE EL SERVIDOR NO PUEDA SABER CUÁL ES (SEGURIDAD DEL CLIENTE)
        // Vamos a crear una clave DES que SIEMPRE será la misma para que SIEMPRE dé el mismo resultado
        // UTF-8 es el por defecto de los algoritmos, pero mejor asegurar la consistencia
        final String utf8 = "utf-8";
        String contrasenyaCifradora = "ContraseñaSuperSecretaParaEncriptarContraseñas";
        byte[] keyBytes = Arrays.copyOf(contrasenyaCifradora.getBytes(utf8), 24);
        SecretKey claveCifrarContrasenya = new SecretKeySpec(keyBytes, "DESede");

        // El vector debe tener una longitud de 8 bytes
        String vector = "ABCD1234";
        IvParameterSpec iv = new IvParameterSpec(vector.getBytes(utf8));

        // Creamos en encriptador
        Cipher encrypt = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        encrypt.init(Cipher.ENCRYPT_MODE, claveCifrarContrasenya, iv);

        // Preparamos la contraseña encriptada para que el servidor no pueda saber cuál es la contraseña 'pura'
        byte[] bytesContrasenya = String.valueOf(tContrasenya.getPassword()).getBytes(utf8);
        return encrypt.doFinal(bytesContrasenya);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // MÉTODOS VARIOS

    public void inGameEnviarRespuestaAlServidor(int tipo, String textoRespuesta, SecretKey claveAES, ObjectOutputStream objOS, ObjectInputStream objIS) {
        int respuesta = -1;
        try {
            // RESPONDER
            objOS.writeObject(tipo); // tipo = 1 -> comprobarRespuesta, tipo = 2 -> abandonar
            if (tipo == 1) { // Si hemos mandado el tipo 1, está esperando a recibir la respuesta para comprobar si es válida
                objOS.writeObject(encriptarMensaje(claveAES, textoRespuesta));
            }
            // AGUARDAR RESPUESTA
            respuesta = (Integer) objIS.readObject();
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException
                | NoSuchPaddingException | IllegalBlockSizeException | ClassNotFoundException ignored) { }

        switch (respuesta) {
            case -2 -> { // ABANDONO CON NUEVA MÁXIMA PUNTUACIÓN
                mostrarJOptionPane("Máx Puntuación", "Una pena que hayas abandonado :( pero aún así...\n¡Enhorabuena! ¡Has logrado una nueva máxima puntuación!", 1);
                ventanaMenuPrincipal(claveAES, objOS, objIS); // EL SERVER TIENE QUE VOLVER A MANDAR EL TOP PUNTUACIONES
            }
            case -1 -> {
                mostrarJOptionPane("Abandono", "Una pena que hayas abandonado :(\n¡Vuelve a jugar cuando quieras!", 1);
                ventanaMenuPrincipal(claveAES, objOS, objIS); // EL SERVER TIENE QUE VOLVER A MANDAR EL TOP PUNTUACIONES
            }
            case 0 -> { // SIGUIENTE PREGUNTA
                try {
                    datosPregunta = desencriptarArrayListString(claveAES, (byte[]) objIS.readObject());
                    volcarDatosPregunta();
                } catch (IOException | ClassNotFoundException | NoSuchAlgorithmException | InvalidKeyException |
                        NoSuchPaddingException | BadPaddingException |
                        IllegalBlockSizeException ignored) { }
            }
            case 1 -> { // HAS ACERTADO TODAS LAS PREGUNTAS FIN DE LA PARTIDA
                mostrarJOptionPane("Pleno", "¡Pleno! Has acertado todas las preguntas que hay en el servidor\n¡Impresionante!", 1);
                ventanaMenuPrincipal(claveAES, objOS, objIS); // EL SERVER TIENE QUE VOLVER A MANDAR EL TOP PUNTUACIONES
            }
            case 2 -> { // PREGUNTA FALLADA, FIN DE LA PARTIDA
                mostrarJOptionPane("Fallaste", "Fin de la partida, ¡inténtalo de nuevo a ver si consigues \nsuperar tu máxima puntuación!", 1);
                ventanaMenuPrincipal(claveAES, objOS, objIS); // EL SERVER TIENE QUE VOLVER A MANDAR EL TOP PUNTUACIONES
            }
            case 3 -> { // PREGUNTA FALLADA, FIN DE LA PARTIDA CON NUEVA MÁXIMA PUNTUACIÓN
                mostrarJOptionPane("Fallaste", "Fin de la partida, ¡has logrado tu máxima puntuación!", 1);
                ventanaMenuPrincipal(claveAES, objOS, objIS); // EL SERVER TIENE QUE VOLVER A MANDAR EL TOP PUNTUACIONES
            }
            default -> { // CUALQUIER RESPUESTA NO CONTEMPLADA O ENTENDIDA CONTARÁ COMO ABANDONO SIN NOTIFICACIÓN
                ventanaMenuPrincipal(claveAES, objOS, objIS); // EL SERVER TIENE QUE VOLVER A MANDAR EL TOP PUNTUACIONES
            }
        }
    }

    public void volcarDatosPregunta() {
        try {
            textoRespuestas = new ArrayList<>();

            tpPregunta.setText(datosPregunta.get(0));
            textoRespuestas.add(datosPregunta.get(1));
            textoRespuestas.add(datosPregunta.get(2));
            textoRespuestas.add(datosPregunta.get(3));
            textoRespuestas.add(datosPregunta.get(4));

            b_respuesta1.setText("<html><head><style>p{text-align: center;}</style></head><body><p>" + saltoLineaBoton(textoRespuestas.get(0)) + "</p></body></html>".replaceAll("\n", "<br>"));
            b_respuesta2.setText("<html><head><style>p{text-align: center;}</style></head><body><p>" + saltoLineaBoton(textoRespuestas.get(1)) + "</p></body></html>".replaceAll("\n", "<br>"));
            b_respuesta3.setText("<html><head><style>p{text-align: center;}</style></head><body><p>" + saltoLineaBoton(textoRespuestas.get(2)) + "</p></body></html>".replaceAll("\n", "<br>"));
            b_respuesta4.setText("<html><head><style>p{text-align: center;}</style></head><body><p>" + saltoLineaBoton(textoRespuestas.get(3)) + "</p></body></html>".replaceAll("\n", "<br>"));

            tipoPreguntaPartida.setText(datosPregunta.get(5));
            puntuacionPartida.setText(datosPregunta.get(6));
        } catch (NullPointerException | IndexOutOfBoundsException ignored) { }
    }
    public String saltoLineaBoton(String texto) {
        String devolver = "";
        int numCaracteres = 22;
        boolean saltoLinea = true;
        String ultimaPlabra = texto.substring(texto.lastIndexOf(' ') + 1);

        if (texto.length() > numCaracteres) {
            try {
                while (texto.length() > numCaracteres) {
                    for (int i = numCaracteres; i > 0; i--) {
                        if (texto.charAt(i) == ' ') {
                            devolver += texto.substring(0, i);
                            texto = texto.substring(i + 1);
                            break;
                        }
                        if (i == 1) {
                            devolver = texto;
                            saltoLinea = false;
                            break;
                        }
                    }
                    if (saltoLinea)
                        devolver += "\n";
                }
            } catch (StringIndexOutOfBoundsException ignored) {}
            if (texto.length() > 0)
                devolver += texto;
        } else {
            devolver += texto;
        }

        return devolver;
    }
    public void volcarDatosTextPane(JTextPane textPane, ArrayList<String> listaStrings, int tipo) {
        textPane.setText("");

        StyledDocument doc = textPane.getStyledDocument();

        Style style = textPane.addStyle("PlaylistStyle", null);
        StyleConstants.setForeground(style, Color.DARK_GRAY);

        String nums = "1234567890";

        try {
            boolean resetearColor = false;
            for (String str : listaStrings) {
                if ((tipo == 2 && str.contains(nickJugador)) || (tipo == 2 && str.contains("Tu puntuación"))
                        || (tipo == 1 && nums.contains(str.substring(0, 1)))) {
                    StyleConstants.setForeground(style, Color.BLUE);
                    resetearColor = true;
                }

                doc.insertString(doc.getLength(), str, style);

                if (resetearColor) {
                    StyleConstants.setForeground(style, Color.DARK_GRAY);
                    resetearColor = false;
                }
            }
        } catch (BadLocationException ignored) {}
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

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // GETTERS Y SETTERS
    public JPanel getPanel() {
        return panel;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // MAIN
    public static void main(String[] args) {
        JFrame frame = new JFrame("Login");
        Cliente vc = new Cliente(frame);
        frame.setContentPane(vc.getPanel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}
