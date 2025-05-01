package tn.fermista.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.stage.Stage;

/**
 * Utilitaire pour la navigation entre les vues
 */
public class NavigationUtil {
    
    /**
     * Charge une vue FXML dans la fenêtre existante en mode plein écran
     * 
     * @param stage La fenêtre à utiliser
     * @param resourcePath Le chemin du fichier FXML
     * @return Le contrôleur du FXML chargé
     * @throws Exception Si une erreur se produit lors du chargement
     */
    public static <T> T navigateTo(Stage stage, String resourcePath) throws Exception {
        FXMLLoader loader = new FXMLLoader(NavigationUtil.class.getResource(resourcePath));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        
        // Configurer la scène en plein écran
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
        
        return loader.getController();
    }
    
    /**
     * Charge une vue FXML dans une nouvelle fenêtre en mode plein écran
     * 
     * @param resourcePath Le chemin du fichier FXML
     * @param title Le titre de la fenêtre (optionnel)
     * @return Le contrôleur du FXML chargé
     * @throws Exception Si une erreur se produit lors du chargement
     */
    public static <T> T openInNewWindow(String resourcePath, String title) throws Exception {
        Stage stage = new Stage();
        if (title != null) {
            stage.setTitle(title);
        }
        
        FXMLLoader loader = new FXMLLoader(NavigationUtil.class.getResource(resourcePath));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        
        // Configurer la scène en plein écran
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
        
        return loader.getController();
    }
} 