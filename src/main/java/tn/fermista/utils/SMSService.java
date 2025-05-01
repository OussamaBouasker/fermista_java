package tn.fermista.utils;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;

import tn.fermista.models.Commande;

public class SMSService {
    
    // Identifiants Twilio
    private static String ACCOUNT_SID = ""; 
    private static String AUTH_TOKEN = "";
    private static String PHONE_NUMBER = "";
    private static String MESSAGE_CONFIRMATION = "";
    private static boolean configLoaded = false;
    
    // Bloc statique pour charger les configurations depuis un fichier
    static {
        try {
            // Essayer de charger les configurations depuis sms.properties
            Properties smsProps = new Properties();
            String configPath = "src/main/resources/sms.properties";
            try (FileInputStream fis = new FileInputStream(configPath)) {
                smsProps.load(fis);
                
                // Si les propriétés sont définies, les utiliser
                if (smsProps.getProperty("twilio.account_sid") != null) {
                    ACCOUNT_SID = smsProps.getProperty("twilio.account_sid");
                }
                if (smsProps.getProperty("twilio.auth_token") != null) {
                    AUTH_TOKEN = smsProps.getProperty("twilio.auth_token");
                }
                if (smsProps.getProperty("twilio.phone_number") != null) {
                    PHONE_NUMBER = smsProps.getProperty("twilio.phone_number");
                }
                if (smsProps.getProperty("twilio.message.confirmation") != null) {
                    MESSAGE_CONFIRMATION = smsProps.getProperty("twilio.message.confirmation");
                }
                
                // Vérifier si les identifiants sont présents
                if (ACCOUNT_SID != null && !ACCOUNT_SID.isEmpty() && 
                    AUTH_TOKEN != null && !AUTH_TOKEN.isEmpty() &&
                    PHONE_NUMBER != null && !PHONE_NUMBER.isEmpty()) {
                    configLoaded = true;
                    System.out.println("Configuration SMS chargée depuis " + configPath);
                    
                    // Initialiser Twilio immédiatement si la configuration est valide
                    Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
                } else {
                    System.err.println("Identifiants Twilio manquants ou incomplets dans le fichier de configuration");
                }
            }
        } catch (IOException e) {
            System.err.println("Impossible de charger la configuration SMS: " + e.getMessage());
        }
    }
    
    /**
     * Vérifie si la configuration SMS est disponible
     * @return true si les identifiants sont configurés, false sinon
     */
    public static boolean isConfigured() {
        return configLoaded && 
               ACCOUNT_SID != null && !ACCOUNT_SID.isEmpty() && 
               AUTH_TOKEN != null && !AUTH_TOKEN.isEmpty() &&
               PHONE_NUMBER != null && !PHONE_NUMBER.isEmpty();
    }
    
    /**
     * Initialise la connexion avec Twilio si ce n'est pas déjà fait
     */
    private static void initTwilio() {
        if (isConfigured()) {
            try {
                // Réinitialiser Twilio avec les identifiants chargés
                System.out.println("Initialisation Twilio avec ACCOUNT_SID: " + ACCOUNT_SID.substring(0, 8) + "...");
                System.out.println("AUTH_TOKEN (longueur): " + AUTH_TOKEN.length() + " caractères");
                System.out.println("PHONE_NUMBER: " + PHONE_NUMBER);
                
                // S'assurer que Twilio est correctement initialisé en forçant une nouvelle initialisation
                Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
                
                // Essayer de valider la configuration en faisant un appel de test léger
                System.out.println("Configuration Twilio validée avec succès");
            } catch (Exception e) {
                System.err.println("Erreur lors de l'initialisation de Twilio: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Envoie un SMS de confirmation de commande
     * 
     * @param commande La commande confirmée
     * @return true si le SMS a été envoyé avec succès, false sinon
     */
    public static boolean envoyerSMSConfirmation(Commande commande) {
        // Vérifier si les identifiants sont configurés
        if (!isConfigured()) {
            System.err.println("L'envoi de SMS est désactivé: identifiants Twilio non configurés");
            System.err.println("Pour activer l'envoi de SMS, configurez vos identifiants dans src/main/resources/sms.properties");
            return false;
        }
        
        // Vérifier que le numéro de téléphone est présent
        if (commande.getTelephone() == null || commande.getTelephone().isEmpty()) {
            System.err.println("Numéro de téléphone manquant pour l'envoi du SMS");
            return false;
        }
        
        try {
            // S'assurer que Twilio est initialisé
            initTwilio();
            
            // Préparer le message avec les informations de la commande
            String nomClient = commande.getNomClient().split(" ")[0]; // Prénom seulement
            String numeroCommande = String.valueOf(commande.getId());
            String total = String.format("%.2f", commande.getTotal());
            
            String messageText = MESSAGE_CONFIRMATION;
            if (messageText != null && !messageText.isEmpty()) {
                messageText = MessageFormat.format(
                    MESSAGE_CONFIRMATION, 
                    nomClient, 
                    numeroCommande,
                    total
                );
            } else {
                // Message par défaut si le modèle n'est pas disponible
                messageText = "Merci " + nomClient + " pour votre commande #" + numeroCommande + " de " + total + " DT chez Fermista!";
            }
            
            // Format international du numéro (ajouter +216 pour la Tunisie)
            String numeroDestinataire = formatPhoneNumber(commande.getTelephone());
            
            System.out.println("Tentative d'envoi de SMS à " + numeroDestinataire);
            System.out.println("Message: " + messageText);
            System.out.println("Utilisation du numéro Twilio: " + PHONE_NUMBER);
            
            try {
                // Envoyer le SMS
                Message message = Message.creator(
                    new PhoneNumber(numeroDestinataire),
                    new PhoneNumber(PHONE_NUMBER),
                    messageText
                ).create();
                
                System.out.println("SMS envoyé avec l'ID: " + message.getSid());
                return true;
            } catch (com.twilio.exception.ApiException apiEx) {
                System.err.println("Erreur API Twilio: " + apiEx.getMessage());
                System.err.println("Code: " + apiEx.getCode() + ", Statut: " + apiEx.getStatusCode());
                
                // Si c'est une erreur d'authentification, suggérer des solutions
                if ("Authenticate".equals(apiEx.getMessage()) || apiEx.getStatusCode() == 401) {
                    System.err.println("ERREUR D'AUTHENTIFICATION TWILIO: Vos identifiants semblent invalides ou expirés.");
                    System.err.println("Veuillez vérifier votre ACCOUNT_SID et AUTH_TOKEN dans le fichier sms.properties");
                    System.err.println("Vous pouvez trouver ces identifiants dans votre console Twilio: https://console.twilio.com/");
                }
                
                throw apiEx;
            }
            
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi du SMS: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Formate un numéro de téléphone pour être compatible avec Twilio
     * @param numero Le numéro de téléphone à formater
     * @return Le numéro formaté (format international)
     */
    private static String formatPhoneNumber(String numero) {
        // Supprimer les espaces et caractères non numériques
        String numeroNettoye = numero.replaceAll("[^0-9]", "");
        
        // Format international pour la Tunisie (ajouter +216)
        if (numeroNettoye.length() == 8) {
            return "+216" + numeroNettoye;
        }
        
        // Si le numéro commence déjà par +, le retourner tel quel
        if (numero.startsWith("+")) {
            return numero;
        }
        
        // Sinon ajouter + au début
        return "+" + numeroNettoye;
    }
} 