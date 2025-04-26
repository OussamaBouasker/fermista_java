package tn.fermista.utils;

import tn.fermista.models.Agriculteur;
import tn.fermista.models.RendezVous;
import tn.fermista.models.Veterinaire;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class EmailService {
    private static final String MAILTRAP_USER = "e4ba15e8f4f848";
    private static final String MAILTRAP_PASSWORD = "bbfebeacb5c69d";
    private static final String MAILTRAP_HOST = "sandbox.smtp.mailtrap.io";
    private static final int MAILTRAP_PORT = 2525;

    public static void sendRendezVousNotification(RendezVous rendezVous) {
        try {
            // Configuration des propriétés SMTP
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", MAILTRAP_HOST);
            props.put("mail.smtp.port", MAILTRAP_PORT);

            // Création de la session
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(MAILTRAP_USER, MAILTRAP_PASSWORD);
                }
            });

            // Création du message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("noreply@fermista.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(rendezVous.getVeterinaire().getEmail()));
            message.setSubject("Nouveau rendez-vous - Fermista");

            // Formatage de la date et l'heure
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            String formattedDate = rendezVous.getDate().toLocalDate().format(dateFormatter);
            String formattedTime = rendezVous.getHeure().toLocalTime().format(timeFormatter);

            // Construction du contenu de l'email
            String content = String.format(
                "Bonjour Dr. %s,\n\n" +
                "Un nouveau rendez-vous a été pris par l'agriculteur %s %s.\n\n" +
                "Détails du rendez-vous :\n" +
                "- Date : %s\n" +
                "- Heure : %s\n" +
                "- Sexe de l'animal : %s\n" +
                "- Cause : %s\n\n" +
                "Veuillez consulter votre liste de rendez-vous pour accepter, refuser ou modifier ce rendez-vous.\n\n" +
                "Cordialement,\n" +
                "L'équipe Fermista",
                rendezVous.getVeterinaire().getLastName(),
                rendezVous.getAgriculteur().getFirstName(),
                rendezVous.getAgriculteur().getLastName(),
                formattedDate,
                formattedTime,
                rendezVous.getSex(),
                rendezVous.getCause()
            );

            message.setText(content);

            // Envoi de l'email
            Transport.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
        }
    }
} 