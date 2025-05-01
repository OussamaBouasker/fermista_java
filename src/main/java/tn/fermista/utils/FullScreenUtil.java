package tn.fermista.utils;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Utilitaire pour gérer l'affichage en plein écran des différentes vues
 */
public class FullScreenUtil {
    
    // Dimensions standard pour toutes les vues de l'application (basées sur DashboardTemplate.fxml)
    public static final double STD_WIDTH = 930.0;
    public static final double STD_HEIGHT = 680.0;
    
    /**
     * Configure la fenêtre en plein écran
     * @param scene La scène à configurer
     */
    public static void setFullScreen(Scene scene) {
        if (scene != null) {
            // Si la scène a déjà une fenêtre, appliquer le plein écran immédiatement
            Stage stage = (Stage) scene.getWindow();
            if (stage != null) {
                stage.setMaximized(true);
            } else {
                // Si la scène n'a pas encore de fenêtre, attendre qu'elle soit visible
                scene.windowProperty().addListener((obs, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        Platform.runLater(() -> {
                            ((Stage) newWindow).setMaximized(true);
                        });
                    }
                });
            }
        }
    }
    
    /**
     * Configure la fenêtre en plein écran directement via le Stage
     * @param stage Le Stage à configurer
     */
    public static void setFullScreen(Stage stage) {
        if (stage != null) {
            Platform.runLater(() -> {
                stage.setMaximized(true);
            });
        }
    }
    
    /**
     * Applique les dimensions standard à une scène
     * Cela garantit que la scène aura toujours la même taille de base
     * @param scene La scène à redimensionner
     */
    public static void applyStandardDimensions(Scene scene) {
        if (scene != null) {
            // Définir les dimensions standard
            scene.getWindow().setWidth(STD_WIDTH);
            scene.getWindow().setHeight(STD_HEIGHT);
        }
    }
    
    /**
     * Crée une scène aux dimensions standard
     * @param root Le nœud racine de la scène
     * @return Une scène aux dimensions standard
     */
    public static Scene createStandardScene(javafx.scene.Parent root) {
        return new Scene(root, STD_WIDTH, STD_HEIGHT);
    }
} 