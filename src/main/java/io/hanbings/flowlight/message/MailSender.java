package io.hanbings.flowlight.message;

import com.sun.mail.util.MailSSLSocketFactory;
import io.hanbings.flowlight.function.Pair;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.activation.URLDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Properties;
import java.util.function.Consumer;

@Setter
@Getter
@Accessors(fluent = true, chain = true)
public class MailSender {
    String protocol = "smtp";
    String host;
    int port = 465;
    boolean auth;
    String username;
    String password;
    boolean tls;
    boolean debug;
    Consumer<Throwable> error = Throwable::printStackTrace;

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    Session session;
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    Properties properties;

    public MailSender connect() {
        properties = new Properties() {{
            put("mail.transport.protocol", protocol);
            put("mail.smtp.host", host);
            put("mail.smtp.port", port);
            put("mail.smtp.auth", auth);
            put("mail.smtp.starttls.enable", tls);
            put("mail.smtp.debug", debug);
        }};

        if (tls) {
            try {
                MailSSLSocketFactory sslSocketFactory = new MailSSLSocketFactory() {{
                    setTrustAllHosts(true);
                }};

                properties.put("mail.smtp.ssl.enable", true);
                properties.put("mail.smtp.ssl.socketFactory", sslSocketFactory);
            } catch (GeneralSecurityException e) {
                error.accept(e);
            }
        }

        session = auth ? Session.getInstance(properties, new javax.mail.Authenticator() {
            @Override
            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                return new javax.mail.PasswordAuthentication(username, password);
            }
        }) : Session.getInstance(properties);

        return this;
    }

    public void send(Mail mail) {
        Message message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress(mail.from()));
            for (String to : mail.to()) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            }
            message.setSubject(mail.subject());

            if (mail.multipart()) {
                Multipart multipart = new MimeMultipart();

                for (String f : mail.file()) {
                    File file = new File(f);

                    MimeBodyPart filePart = new MimeBodyPart() {{
                        setDataHandler(new DataHandler(new FileDataSource(file)));
                        setFileName(MimeUtility.encodeWord(file.getName()));
                    }};

                    multipart.addBodyPart(filePart);
                }

                for (Pair<String, String> p : mail.resource()) {
                    MimeBodyPart resourcePart = new MimeBodyPart() {{
                        setDataHandler(new DataHandler(new URLDataSource(new URL(p.second()))));
                        setContentID(p.first());
                    }};

                    multipart.addBodyPart(resourcePart);
                }

                MimeBodyPart htmlPart = new MimeBodyPart() {{
                    setContent(mail.content(), "text/html; charset=utf-8");
                }};

                multipart.addBodyPart(htmlPart);
            } else {
                message.setText(mail.content());
            }

            message.saveChanges();
            Transport.send(message, message.getAllRecipients());
        } catch (MessagingException | IOException e) {
            error.accept(e);
        }
    }
}
