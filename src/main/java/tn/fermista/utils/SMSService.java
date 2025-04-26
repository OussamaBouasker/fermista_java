package tn.fermista.utils;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import tn.fermista.models.RendezVous;

import java.time.format.DateTimeFormatter;

public class SMSService {
    // Remplacez ces valeurs par vos identifiants Twilio
    private static final String ACCOUNT_SID = "ACe54fd14a5a126cd641203f3baa63b514";
    private static final String AUTH_TOKEN = "005b6d36b067d4324eb5140042c3805d";
    private static final String TWILIO_PHONE_NUMBER = "+14846625835";
 // Format international sans espaces

    static {
        // Initialisation de Twilio
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public static void sendRendezVousStatusUpdate(RendezVous rendezVous, String oldStatus) {
        try {
            // Vérification du numéro de téléphone
            String phoneNumber = rendezVous.getAgriculteur().getNumber();
            if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
                System.err.println("Le numéro de téléphone de l'agriculteur est manquant");
                return;
            }

            // Formatage du numéro de téléphone
            phoneNumber = formatPhoneNumber(phoneNumber);
            if (phoneNumber == null) {
                System.err.println("Format de numéro de téléphone invalide");
                return;
            }

            // Formatage de la date et l'heure
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            String formattedDate = rendezVous.getDate().toLocalDate().format(dateFormatter);
            String formattedTime = rendezVous.getHeure().toLocalTime().format(timeFormatter);

            // Construction du message
            String messageContent = String.format(
                "Fermista - Mise à jour de votre rendez-vous :\n" +
                "Date : %s\n" +
                "Heure : %s\n" +
                "Statut : %s\n" +
                "Vétérinaire : Dr. %s",
                formattedDate,
                formattedTime,
                getStatusMessage(rendezVous.getStatus()),
                rendezVous.getVeterinaire().getLastName()
            );

            // Envoi du SMS
            Message message = Message.creator(
                new PhoneNumber(phoneNumber),
                new PhoneNumber(TWILIO_PHONE_NUMBER),
                messageContent
            ).create();

            System.out.println("SMS envoyé avec succès. SID: " + message.getSid());

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'envoi du SMS : " + e.getMessage());
        }
    }

    private static String formatPhoneNumber(String phoneNumber) {
        // Supprimer tous les caractères non numériques
        String cleaned = phoneNumber.replaceAll("[^0-9+]", "");

        // Vérifier si le numéro commence par +
        if (!cleaned.startsWith("+")) {
            // Si le numéro commence par 0, le remplacer par +216 (code pays Tunisie)
            if (cleaned.startsWith("0")) {
                cleaned = "+216" + cleaned.substring(1);
            } else {
                // Sinon, ajouter le préfixe international
                cleaned = "+216" + cleaned;
            }
        }

        // Vérifier la longueur minimale (10 chiffres + le +)
        if (cleaned.length() < 11) {
            return null;
        }

        return cleaned;
    }

    private static String getStatusMessage(String status) {
        switch (status.toLowerCase()) {
            case "accepté":
                return "Votre rendez-vous a été accepté";
            case "refusé":
                return "Votre rendez-vous a été refusé";
            case "modifié":
                return "Votre rendez-vous a été modifié";
            default:
                return "Le statut de votre rendez-vous a été mis à jour";
        }
    }

    public static void sendUrgentRendezVousConfirmation(RendezVous rendezVous) {
        try {
            // Vérification du numéro de téléphone
            String phoneNumber = rendezVous.getAgriculteur().getNumber();
            if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
                System.err.println("Le numéro de téléphone de l'agriculteur est manquant");
                return;
            }

            // Formatage du numéro de téléphone
            phoneNumber = formatPhoneNumber(phoneNumber);
            if (phoneNumber == null) {
                System.err.println("Format de numéro de téléphone invalide");
                return;
            }

            // Formatage de la date et l'heure
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            String formattedDate = rendezVous.getDate().toLocalDate().format(dateFormatter);
            String formattedTime = rendezVous.getHeure().toLocalTime().format(timeFormatter);

            // Construction du message
            String messageContent = String.format(
                "Fermista - Rendez-vous URGENT confirmé :\n" +
                "Votre rendez-vous urgent a été automatiquement accepté.\n" +
                "Date : %s\n" +
                "Heure : %s\n" +
                "Vétérinaire : Dr. %s\n" +
                "Cause : %s",
                formattedDate,
                formattedTime,
                rendezVous.getVeterinaire().getLastName(),
                rendezVous.getCause()
            );

            // Envoi du SMS
            Message message = Message.creator(
                new PhoneNumber(phoneNumber),
                new PhoneNumber(TWILIO_PHONE_NUMBER),
                messageContent
            ).create();

            System.out.println("SMS de confirmation urgent envoyé avec succès. SID: " + message.getSid());

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'envoi du SMS de confirmation urgent : " + e.getMessage());
        }
    }

    public static void sendRendezVousDeplacementNotification(RendezVous rendezVous) {
        try {
            // Vérification du numéro de téléphone
            String phoneNumber = rendezVous.getAgriculteur().getNumber();
            if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
                System.err.println("Le numéro de téléphone de l'agriculteur est manquant");
                return;
            }

            // Formatage du numéro de téléphone
            phoneNumber = formatPhoneNumber(phoneNumber);
            if (phoneNumber == null) {
                System.err.println("Format de numéro de téléphone invalide");
                return;
            }

            // Formatage de la date et l'heure
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            String formattedDate = rendezVous.getDate().toLocalDate().format(dateFormatter);
            String formattedTime = rendezVous.getHeure().toLocalTime().format(timeFormatter);

            // Construction du message
            String messageContent = String.format(
                "Fermista - Modification de votre rendez-vous :\n" +
                "Votre rendez-vous a été décalé en raison d'un cas urgent.\n" +
                "Nouvelle date : %s\n" +
                "Nouvelle heure : %s\n" +
                "Vétérinaire : Dr. %s\n" +
                "Nous nous excusons pour ce changement.",
                formattedDate,
                formattedTime,
                rendezVous.getVeterinaire().getLastName()
            );

            // Envoi du SMS
            Message message = Message.creator(
                new PhoneNumber(phoneNumber),
                new PhoneNumber(TWILIO_PHONE_NUMBER),
                messageContent
            ).create();

            System.out.println("SMS de déplacement envoyé avec succès. SID: " + message.getSid());

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'envoi du SMS de déplacement : " + e.getMessage());
        }
    }
}