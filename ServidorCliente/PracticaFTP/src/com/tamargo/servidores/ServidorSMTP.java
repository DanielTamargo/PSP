package com.tamargo.servidores;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class ServidorSMTP {

    /** El email se envía a una dirección fija -> cambiar esta dirección si quieres recibir el email en tu dirección */
    private final String direccionEnvioEmail = "daniel.tamargo@ikasle.egibide.org";

    private final String nombre = "[Servidor SMTP] ";

    public void enviarEmail(String titulo, String mensaje) {
        String emailServidor = "daniel.tamargo@ikasle.egibide.org";
        String passEmailServidor = "rebelde1L";

        // Configuramos las propiedades de SMTP
        // Documentación extra: https://www.journaldev.com/2532/javamail-example-send-mail-in-java-smtp
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); // Host SMTP
        props.put("mail.smtp.port", "587"); // Puerto TLS
        props.put("mail.smtp.auth", "true"); // Con autentificación
        props.put("mail.smtp.starttls.enable", "true"); // Habilitamos starttls

        // Configuramos la sesión que enviará el mensaje
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailServidor, passEmailServidor);
            }
        });

        try {
            // Configuramos el mensaje
            Message message = prepararMensaje(titulo, mensaje, emailServidor, session);

            // Mandamos el mensaje
            Transport.send(message);
            System.out.println(nombre + "Correo electrónico de confirmación enviado.");

        } catch (NoClassDefFoundError e) {
            System.out.println(nombre + "Error con el servicio SMTP, la definición de la clase Session no existe o no es correcta");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println(nombre + "Error al enviar el correo electrónico de confirmación");
        }

    }

    private Message prepararMensaje(String titulo, String mensaje, String emailServidor, Session session) throws MessagingException {
        Message message = new MimeMessage(session); // Indicamos la sesión desde la cual se enviará el mensaje
        message.addHeader("Content-type", "text/HTML; charset=UTF-8");
        message.addHeader("format", "flowed");
        message.addHeader("Content-Transfer-Encoding", "8bit");
        message.setSubject(titulo); //texto del asunto
        message.setText(mensaje); //texto del mensaje

        // Configuramos a quién mandar el mensaje
        message.setFrom(new InternetAddress(emailServidor));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(direccionEnvioEmail, false));
        return message;
    }


}


