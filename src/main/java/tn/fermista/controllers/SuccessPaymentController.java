package tn.fermista.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Contrôleur pour la vue de confirmation de paiement réussi
 */
public class SuccessPaymentController implements Initializable {

    @FXML private Button backToShopButton;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Aucune initialisation spécifique requise pour l'instant
    }
    
    /**
     * Gère le clic sur le bouton "Retourner à la boutique"
     */
    @FXML
    public void handleBackToShop() {
        try {
            // Récupérer la taille de la fenêtre actuelle
            Stage currentStage = (Stage) backToShopButton.getScene().getWindow();
            double width = currentStage.getWidth();
            double height = currentStage.getHeight();
            boolean isMaximized = currentStage.isMaximized();
            
            // Charger la vue de la boutique
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProductShopView.fxml"));
            Parent root = loader.load();
            
            // Créer une nouvelle scène avec la même taille
            Scene scene = new Scene(root);
            currentStage.setScene(scene);
            
            // Rétablir la taille de la fenêtre
            if (isMaximized) {
                currentStage.setMaximized(true);
            } else {
                currentStage.setWidth(width);
                currentStage.setHeight(height);
            }
            
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de la boutique: " + e.getMessage());
        }
    }
} 