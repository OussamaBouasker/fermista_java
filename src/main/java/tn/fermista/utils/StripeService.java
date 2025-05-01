package tn.fermista.utils;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import tn.fermista.models.Commande;
import tn.fermista.models.Panier;

public class StripeService {
    
    // Clés API Stripe
    private static String API_KEY = "";
    private static String PUBLISHABLE_KEY = "";
    private static String CURRENCY = "usd"; // Devise en minuscules par défaut
    private static boolean configLoaded = false;
    
    // Bloc statique pour charger les configurations depuis un fichier
    static {
        try {
            // Essayer de charger les configurations depuis stripe.properties
            Properties stripeProps = new Properties();
            String configPath = "src/main/resources/stripe.properties";
            try (FileInputStream fis = new FileInputStream(configPath)) {
                stripeProps.load(fis);
                
                // Si les propriétés sont définies, les utiliser
                if (stripeProps.getProperty("stripe.api.key") != null) {
                    API_KEY = stripeProps.getProperty("stripe.api.key");
                }
                if (stripeProps.getProperty("stripe.publishable.key") != null) {
                    PUBLISHABLE_KEY = stripeProps.getProperty("stripe.publishable.key");
                }
                if (stripeProps.getProperty("stripe.currency") != null) {
                    // S'assurer que la devise est en minuscules pour Stripe
                    CURRENCY = stripeProps.getProperty("stripe.currency").toLowerCase().trim();
                }
                
                // Vérifier si les identifiants sont présents
                if (API_KEY != null && !API_KEY.isEmpty() && 
                    PUBLISHABLE_KEY != null && !PUBLISHABLE_KEY.isEmpty()) {
                    configLoaded = true;
                    System.out.println("Configuration Stripe chargée depuis " + configPath);
                    System.out.println("Devise configurée: " + CURRENCY);
                    
                    // Initialiser Stripe avec la clé API
                    Stripe.apiKey = API_KEY;
                } else {
                    System.err.println("Clés API Stripe manquantes ou incomplètes dans le fichier de configuration");
                }
            }
        } catch (IOException e) {
            System.err.println("Impossible de charger la configuration Stripe: " + e.getMessage());
        }
    }
    
    /**
     * Vérifie si la configuration Stripe est disponible
     * @return true si les identifiants sont configurés, false sinon
     */
    public static boolean isConfigured() {
        return configLoaded && 
               API_KEY != null && !API_KEY.isEmpty() && 
               PUBLISHABLE_KEY != null && !PUBLISHABLE_KEY.isEmpty();
    }
    
    /**
     * Retourne la clé publique Stripe pour l'affichage dans l'interface
     * @return La clé publique
     */
    public static String getPublishableKey() {
        return PUBLISHABLE_KEY;
    }
    
    /**
     * Crée une intention de paiement pour une commande
     * 
     * @param commande La commande à payer
     * @return L'identifiant du client de paiement
     * @throws StripeException En cas d'erreur avec l'API Stripe
     */
    public static PaymentIntent createPaymentIntent(Commande commande) throws StripeException {
        if (!isConfigured()) {
            throw new IllegalStateException("La configuration Stripe n'est pas disponible");
        }
        
        // Convertir le total en centimes (Stripe attend le montant en centimes)
        long montantCentimes = Math.round(commande.getTotal() * 100);
        
        // Créer un Map<String, String> car putAllMetadata attend ce type
        Map<String, String> metadata = new HashMap<>();
        metadata.put("order_id", String.valueOf(commande.getId()));
        metadata.put("customer_name", commande.getNomClient());
        metadata.put("customer_email", commande.getEmail());
        
        // Créer les paramètres pour l'intention de paiement
        PaymentIntentCreateParams params = 
            PaymentIntentCreateParams.builder()
                .setAmount(montantCentimes)
                .setCurrency(CURRENCY)
                .setDescription("Commande #" + commande.getId() + " - Fermista")
                .putAllMetadata(metadata)
                .setAutomaticPaymentMethods(
                    PaymentIntentCreateParams.AutomaticPaymentMethods
                        .builder()
                        .setEnabled(true)
                        .build()
                )
                .build();
        
        // Créer l'intention de paiement
        PaymentIntent paymentIntent = PaymentIntent.create(params);
        
        System.out.println("Intent de paiement créé: " + paymentIntent.getId());
        return paymentIntent;
    }
    
    /**
     * Confirme un paiement (utilisé après que l'utilisateur a entré ses informations de carte)
     * 
     * @param paymentIntentId L'identifiant de l'intention de paiement
     * @return L'intention de paiement mise à jour
     * @throws StripeException En cas d'erreur avec l'API Stripe
     */
    public static PaymentIntent confirmPayment(String paymentIntentId) throws StripeException {
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        
        if (paymentIntent.getStatus().equals("requires_payment_method")) {
            Map<String, Object> params = new HashMap<>();
            params.put("payment_method", "pm_card_visa"); // Utiliser une carte de test Stripe
            
            paymentIntent = paymentIntent.confirm(params);
        }
        
        return paymentIntent;
    }
} 