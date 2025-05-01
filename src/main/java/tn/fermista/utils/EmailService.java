package tn.fermista.utils;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import tn.fermista.models.Commande;

public class EmailService {
    
    // Identifiants par défaut pour Gmail
    private static String HOST = "smtp.gmail.com";
    private static String USERNAME = ""; // Sera rempli à partir du fichier de configuration
    private static String PASSWORD = ""; // Sera rempli à partir du fichier de configuration
    private static int PORT = 587;
    private static boolean configLoaded = false;
    
    // Bloc statique pour charger les configurations depuis un fichier
    static {
        try {
            // Essayer de charger les configurations depuis email.properties
            Properties emailProps = new Properties();
            String configPath = "src/main/resources/email.properties";
            try (FileInputStream fis = new FileInputStream(configPath)) {
                emailProps.load(fis);
                
                // Si les propriétés sont définies, les utiliser
                if (emailProps.getProperty("mail.smtp.host") != null) {
                    HOST = emailProps.getProperty("mail.smtp.host");
                }
                if (emailProps.getProperty("mail.smtp.username") != null) {
                    USERNAME = emailProps.getProperty("mail.smtp.username");
                }
                if (emailProps.getProperty("mail.smtp.password") != null) {
                    PASSWORD = emailProps.getProperty("mail.smtp.password");
                }
                if (emailProps.getProperty("mail.smtp.port") != null) {
                    PORT = Integer.parseInt(emailProps.getProperty("mail.smtp.port"));
                }
                
                // Vérifier si les identifiants sont présents
                if (USERNAME != null && !USERNAME.isEmpty() && PASSWORD != null && !PASSWORD.isEmpty()) {
                    configLoaded = true;
                    System.out.println("Configuration email chargée depuis " + configPath);
                } else {
                    System.err.println("Identifiants d'email manquants ou incomplets dans le fichier de configuration");
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Impossible de charger la configuration email: " + e.getMessage());
        }
    }
    
    /**
     * Vérifie si la configuration email est disponible
     * @return true si les identifiants sont configurés, false sinon
     */
    public static boolean isConfigured() {
        return configLoaded && USERNAME != null && !USERNAME.isEmpty() && PASSWORD != null && !PASSWORD.isEmpty();
    }
    
    /**
     * Envoie un email de confirmation de commande 
     * 
     * @param commande La commande confirmée
     * @return true si l'email a été envoyé avec succès, false sinon
     */
    public static boolean envoyerEmailConfirmation(Commande commande) {
        // Vérifier si les identifiants sont configurés
        if (!isConfigured()) {
            System.err.println("L'envoi d'email est désactivé: identifiants non configurés");
            System.err.println("Pour activer l'envoi d'email, configurez vos identifiants dans src/main/resources/email.properties");
            return false;
        }
        
        System.out.println("Tentative d'envoi d'email avec les identifiants suivants:");
        System.out.println("Host: " + HOST);
        System.out.println("Port: " + PORT);
        System.out.println("Username: " + USERNAME);
        System.out.println("Password: " + (PASSWORD != null ? "[masqué]" : "null"));
        
        try {
            // Configurer les propriétés du serveur SMTP
            Properties properties = new Properties();
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.host", HOST);
            properties.put("mail.smtp.port", PORT);
            properties.put("mail.smtp.ssl.trust", HOST);
            properties.put("mail.debug", "true");
            // Augmenter les timeouts pour éviter les erreurs de connexion
            properties.put("mail.smtp.connectiontimeout", "15000");
            properties.put("mail.smtp.timeout", "15000");
            
            // Créer une session avec authentification
            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(USERNAME, PASSWORD);
                }
            });
            
            // Activer le debugging pour voir les détails de l'envoi
            session.setDebug(true);
            
            // Créer le message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USERNAME, "Fermista"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(commande.getEmail()));
            message.setSubject("Confirmation de votre commande #" + commande.getId() + " - Fermista");
            
            // Construire le contenu HTML de l'email
            String htmlContent = buildEmailContent(commande);
            message.setContent(htmlContent, "text/html; charset=utf-8");
            
            // Envoyer le message
            Transport.send(message);
            System.out.println("Email de confirmation envoyé à " + commande.getEmail());
            return true;
            
        } catch (AuthenticationFailedException e) {
            System.err.println("Erreur d'authentification Gmail: " + e.getMessage());
            System.err.println("Veuillez vérifier vos identifiants dans le fichier src/main/resources/email.properties");
            System.err.println("Pour Gmail, assurez-vous d'avoir activé 'l'accès aux applications moins sécurisées' ou d'utiliser un mot de passe d'application");
            
            // Afficher des informations de débogage supplémentaires
            System.err.println("\nDétails techniques pour le support:");
            System.err.println("Exception: " + e.getClass().getName());
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
            
            return false;
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de l'email de confirmation: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Construit le contenu HTML de l'email de confirmation
     */
    private static String buildEmailContent(Commande commande) {
        StringBuilder html = new StringBuilder();
        
        html.append("<!DOCTYPE html>")
            .append("<html><head><meta charset='UTF-8'>")
            .append("<style>")
            .append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }")
            .append(".container { max-width: 600px; margin: 0 auto; padding: 20px; }")
            .append(".header { background-color: #4CAF50; color: white; padding: 10px; text-align: center; }")
            .append(".content { padding: 20px; border: 1px solid #ddd; }")
            .append(".footer { text-align: center; margin-top: 20px; font-size: 12px; color: #777; }")
            .append("table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }")
            .append("th, td { padding: 10px; text-align: left; border-bottom: 1px solid #ddd; }")
            .append("th { background-color: #f2f2f2; }")
            .append(".total { font-weight: bold; text-align: right; }")
            .append("</style></head>")
            .append("<body><div class='container'>")
            .append("<div class='header'><h1>Fermista</h1></div>")
            .append("<div class='content'>")
            .append("<h2>Confirmation de commande #").append(commande.getId()).append("</h2>")
            .append("<p>Cher(e) ").append(commande.getNomClient()).append(",</p>")
            .append("<p>Nous vous remercions pour votre commande chez Fermista. Votre commande a été confirmée et est en cours de traitement.</p>")
            .append("<h3>Détails de la commande</h3>")
            .append("<table>")
            .append("<tr><th>Numéro de commande</th><td>").append(commande.getId()).append("</td></tr>")
            .append("<tr><th>Date</th><td>").append(commande.getDate()).append("</td></tr>")
            .append("<tr><th>Total</th><td>").append(String.format("%.2f DT", commande.getTotal())).append("</td></tr>")
            .append("<tr><th>Adresse de livraison</th><td>").append(commande.getAdresse()).append("</td></tr>")
            .append("</table>")
            .append("<p>Notre équipe prépare votre commande avec le plus grand soin. Vous recevrez un email de confirmation lorsque votre commande sera expédiée.</p>")
            .append("<p>Pour toute question concernant votre commande, n'hésitez pas à nous contacter à <a href='mailto:").append(USERNAME).append("'>").append(USERNAME).append("</a>.</p>")
            .append("<p>Cordialement,<br>L'équipe Fermista</p>")
            .append("</div>")
            .append("<div class='footer'>")
            .append("<p>Cet email a été envoyé à ").append(commande.getEmail()).append("</p>")
            .append("<p>&copy; 2023 Fermista. Tous droits réservés.</p>")
            .append("</div></div></body></html>");
        
        return html.toString();
    }
} 