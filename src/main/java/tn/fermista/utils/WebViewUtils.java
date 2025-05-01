package tn.fermista.utils;

import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;

/**
 * Classe utilitaire pour travailler avec JavaFX WebView.
 * Cette classe garantit que tous les modules nécessaires pour WebView sont correctement chargés.
 */
public class WebViewUtils {
    
    /**
     * Vérifie si WebView est correctement initialisé dans le projet.
     * @return true si WebView est disponible, false sinon
     */
    public static boolean isWebViewAvailable() {
        try {
            // Cette ligne échouera si la dépendance javafx-web n'est pas disponible
            Class.forName("javafx.scene.web.WebView");
            // Cette ligne échouera si la dépendance JSObject n'est pas disponible
            Class.forName("netscape.javascript.JSObject");
            return true;
        } catch (ClassNotFoundException e) {
            System.err.println("Les dépendances WebView ne sont pas disponibles: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Exécute un script JavaScript en toute sécurité
     * @param engine WebEngine sur laquelle exécuter le script
     * @param script Script JavaScript à exécuter
     * @return Résultat de l'exécution ou null en cas d'erreur
     */
    public static Object executeScript(WebEngine engine, String script) {
        try {
            return engine.executeScript(script);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'exécution du script JavaScript: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
} 