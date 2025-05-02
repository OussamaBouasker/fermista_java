package tn.fermista.utils;

import java.io.IOException;
import java.net.Socket;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.time.format.DateTimeFormatter;
import tn.fermista.models.Reservation;
import java.nio.charset.StandardCharsets;

public class EmailSender2 {
    private static final Logger LOGGER = Logger.getLogger(EmailSender.class.getName());

    // Mailtrap SMTP credentials
    private static final String MAILTRAP_HOST = "sandbox.smtp.mailtrap.io";
    private static final int MAILTRAP_PORT = 2525;
    private static final String MAILTRAP_USERNAME = "b6fe749b4fd202";
    private static final String MAILTRAP_PASSWORD = "7a242d24e6ee74";

    public static void sendReservationStatusEmail(String recipientEmail, String workshopTitle, String status, String meetLink) {
        sendEmail(recipientEmail,
                "Mise à jour de votre reservation - Fermista",
                buildStatusUpdateEmailContent(workshopTitle, status, meetLink));
    }

    public static void sendNewReservationEmail(Reservation reservation) {
        sendEmail(reservation.getEmail(),
                "Confirmation de votre reservation - Fermista",
                buildNewReservationEmailContent(reservation));
    }

    private static void sendEmail(String recipientEmail, String subject, String content) {
        Socket socket = null;
        OutputStream out = null;
        InputStream in = null;

        try {
            // Connect to Mailtrap SMTP server
            socket = new Socket(MAILTRAP_HOST, MAILTRAP_PORT);
            out = socket.getOutputStream();
            in = socket.getInputStream();

            // Read server greeting
            readResponse(in);

            // Send EHLO command
            sendCommand(out, "EHLO fermista.com");
            readResponse(in);

            // Authenticate
            sendCommand(out, "AUTH LOGIN");
            readResponse(in);

            // Send username (Base64 encoded)
            sendCommand(out, Base64.getEncoder().encodeToString(MAILTRAP_USERNAME.getBytes()));
            readResponse(in);

            // Send password (Base64 encoded)
            sendCommand(out, Base64.getEncoder().encodeToString(MAILTRAP_PASSWORD.getBytes()));
            readResponse(in);

            // Set sender
            sendCommand(out, "MAIL FROM:<noreply@fermista.com>");
            readResponse(in);

            // Set recipient
            sendCommand(out, "RCPT TO:<" + recipientEmail + ">");
            readResponse(in);

            // Start data
            sendCommand(out, "DATA");
            readResponse(in);

            // Send email headers and content
            String emailData =
                    "From: Fermista <noreply@fermista.com>\r\n" +
                            "To: " + recipientEmail + "\r\n" +
                            "Subject: " + subject + "\r\n" +
                            "MIME-Version: 1.0\r\n" +
                            "Content-Type: text/html; charset=UTF-8\r\n" +
                            "Content-Transfer-Encoding: base64\r\n" +
                            "\r\n" +
                            Base64.getEncoder().encodeToString(content.getBytes(StandardCharsets.UTF_8)) +
                            "\r\n.\r\n";

            sendCommand(out, emailData);
            readResponse(in);

            // Quit
            sendCommand(out, "QUIT");
            readResponse(in);

            LOGGER.info("Email sent successfully to " + recipientEmail);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error sending email", e);
        } finally {
            try {
                if (out != null) out.close();
                if (in != null) in.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Error closing resources", e);
            }
        }
    }

    private static void sendCommand(OutputStream out, String command) throws IOException {
        out.write((command + "\r\n").getBytes());
        out.flush();
    }

    private static String readResponse(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line).append("\n");
            if (line.charAt(3) == ' ') break;
        }

        return response.toString();
    }

    private static String buildStatusUpdateEmailContent(String workshopTitle, String status, String meetLink) {
        StringBuilder content = new StringBuilder();
        content.append("<!DOCTYPE html>");
        content.append("<html><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'></head>");
        content.append("<body style='font-family: Arial, sans-serif; margin: 0; padding: 0;'>");
        content.append("<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>");
        content.append("<h2 style='color: #2c3e50;'>Mise à jour de votre reservation</h2>");
        content.append("<p>Bonjour,</p>");
        content.append("<p>Le statut de votre reservation pour l'atelier <strong>").append(workshopTitle).append("</strong> a été mis à jour.</p>");
        content.append("<p>Nouveau statut : <strong>").append(status).append("</strong></p>");

        if (meetLink != null && !meetLink.isEmpty()) {
            try {
                // Générer le QR code
                String qrCodeBase64 = QRCodeGenerator.generateQRCodeBase64(meetLink, 120, 120);

                content.append("<div style='text-align: center; margin: 20px 0; padding: 15px; background-color: #ffffff; border-radius: 5px;'>");
                content.append("<h3 style='color: #2c3e50; margin-bottom: 15px;'>Votre QR Code d'accès :</h3>");
                content.append("<div style='display: inline-block; padding: 10px; background-color: white; border-radius: 5px;'>");
                content.append("<img src='data:image/png;base64,").append(qrCodeBase64).append("' ");
                content.append("alt='QR Code' style='display: block; width: 120px; height: 120px; margin: 0 auto;'/>");
                content.append("</div>");
                content.append("<p style='color: #666; margin-top: 15px;'>Scannez ce QR code pour accéder à votre formation</p>");
                content.append("<p style='color: #666; font-size: 12px; margin-top: 5px;'>Ou cliquez sur ce lien : <a href='").append(meetLink).append("'>").append(meetLink).append("</a></p>");
                content.append("</div>");
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Erreur lors de la génération du QR code", e);
                content.append("<p style='color: #666;'>Lien de la reunion : <a href='").append(meetLink).append("'>").append(meetLink).append("</a></p>");
            }
        }

        content.append("<p>Cordialement,<br>L'equipe Fermista</p>");
        content.append("</div></body></html>");

        return content.toString();
    }

    private static String buildNewReservationEmailContent(Reservation reservation) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        StringBuilder content = new StringBuilder();
        content.append("<!DOCTYPE html>");
        content.append("<html><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'></head>");
        content.append("<body style='font-family: Arial, sans-serif; margin: 0; padding: 0;'>");
        content.append("<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>");
        content.append("<h2 style='color: #2c3e50;'>Confirmation de votre reservation</h2>");
        content.append("<p>Cher(e) ").append(reservation.getUser().getFirstName()).append(",</p>");
        content.append("<p>Nous avons bien reçu votre reservation pour l'atelier <strong>")
                .append(reservation.getWorkshop().getTitre())
                .append("</strong>.</p>");

        content.append("<div style='background-color: #f8f9fa; padding: 15px; border-radius: 5px; margin: 20px 0;'>");
        content.append("<h3 style='color: #2c3e50; margin-top: 0;'>Details de votre reservation :</h3>");
        content.append("<ul style='list-style-type: none; padding-left: 0;'>");
        content.append("<li><strong>Date de reservation :</strong> ")
                .append(reservation.getReservationDate().format(formatter))
                .append("</li>");
        content.append("<li><strong>Prix :</strong> ")
                .append(reservation.getPrix())
                .append(" DT</li>");
        content.append("<li><strong>Statut :</strong> <span style='color: #e67e22;'>En attente de confirmation</span></li>");
        content.append("</ul></div>");

        // Ajout du QR code pour les formations autonomes
        if ("Formation Autonome".equals(reservation.getWorkshop().getType())) {
            try {
                String formationLink = QRCodeGenerator.getFormationLink(String.valueOf(reservation.getWorkshop().getId()));
                String qrCodeBase64 = QRCodeGenerator.generateQRCodeBase64(formationLink, 150, 150);

                content.append("<div style='text-align: center; margin: 20px 0; padding: 15px; background-color: #ffffff; border-radius: 5px;'>");
                content.append("<h3 style='color: #2c3e50; margin-bottom: 15px;'>Votre QR Code d'accès :</h3>");
                content.append("<div style='display: inline-block; padding: 10px; background-color: white; border-radius: 5px;'>");
                content.append("<img src='data:image/png;base64,").append(qrCodeBase64).append("' ");
                content.append("alt='QR Code' style='display: block; width: 150px; height: 150px; margin: 0 auto;'/>");
                content.append("</div>");
                content.append("<p style='color: #666; margin-top: 15px;'>Scannez ce QR code pour accéder à votre formation</p>");
                content.append("<p style='color: #666; font-size: 12px; margin-top: 5px;'>Ou cliquez sur ce lien : <a href='").append(formationLink).append("'>").append(formationLink).append("</a></p>");
                content.append("</div>");
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Erreur lors de la génération du QR code", e);
                content.append("<p style='color: #ff0000;'>Désolé, une erreur est survenue lors de la génération du QR code. Veuillez contacter le support.</p>");
            }
        }

        content.append("<p>Notre equipe va traiter votre demande dans les plus brefs delais. ")
                .append("Vous recevrez un email dès que votre reservation sera confirmee.</p>");

        if (reservation.getWorkshop().getType().equals("Atelier Live")) {
            content.append("<p><em>Note : Pour les ateliers live, le lien de la reunion vous sera communique ")
                    .append("une fois votre reservation confirmee.</em></p>");
        }

        content.append("<p style='margin-top: 30px;'>Cordialement,<br>L'equipe Fermista</p>");
        content.append("</div></body></html>");

        return content.toString();
    }
}