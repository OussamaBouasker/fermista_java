package tn.fermista.utils;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LocalizationManager {
    private static LocalizationManager instance;
    private ResourceBundle bundle;
    private Locale currentLocale;
    private static final Logger logger = Logger.getLogger(LocalizationManager.class.getName());

    private LocalizationManager() {
        // Par défaut, on utilise le français
        currentLocale = new Locale("fr");
        try {
            // Essayer d'abord le chemin spécifié
        bundle = ResourceBundle.getBundle("locales/panier", currentLocale);
        } catch (MissingResourceException e) {
            try {
                // Si le premier chemin échoue, essayer le fichier messages.properties à la racine
                bundle = ResourceBundle.getBundle("messages", currentLocale);
                logger.info("Fichier de localisation trouvé à la racine: messages.properties");
            } catch (MissingResourceException e2) {
                logger.warning("Aucun fichier de localisation trouvé. Utilisation des valeurs par défaut.");
                // Ne pas définir bundle si les deux tentatives échouent
            }
        }
    }

    public static LocalizationManager getInstance() {
        if (instance == null) {
            instance = new LocalizationManager();
        }
        return instance;
    }

    public void setLocale(Locale locale) {
        currentLocale = locale;
        try {
        bundle = ResourceBundle.getBundle("locales/panier", currentLocale);
        } catch (MissingResourceException e) {
            try {
                bundle = ResourceBundle.getBundle("messages", currentLocale);
            } catch (MissingResourceException e2) {
                logger.warning("Aucun fichier de localisation trouvé pour la locale " + locale);
                // Conserver l'ancien bundle si le nouveau n'est pas trouvé
            }
        }
    }

    public String getString(String key) {
        try {
            if (bundle != null) {
        return bundle.getString(key);
            } else {
                // Retourner des valeurs par défaut pour les clés connues
                switch (key) {
                    case "panier.retour_boutique":
                        return "Continuer mes achats";
                    case "panier.commander":
                        return "Finaliser ma commande";
                    case "panier.empty":
                        return "Votre panier est vide";
                    default:
                        return key; // Renvoyer la clé si pas de valeur par défaut
                }
            }
        } catch (MissingResourceException e) {
            logger.log(Level.WARNING, "Clé de localisation non trouvée: " + key, e);
            // Retourner des valeurs par défaut pour les clés connues
            switch (key) {
                case "panier.retour_boutique":
                    return "Continuer mes achats";
                case "panier.commander":
                    return "Finaliser ma commande";
                case "panier.empty":
                    return "Votre panier est vide";
                default:
                    return key; // Renvoyer la clé si pas de valeur par défaut
            }
        }
    }

    public Locale getCurrentLocale() {
        return currentLocale;
    }
} 