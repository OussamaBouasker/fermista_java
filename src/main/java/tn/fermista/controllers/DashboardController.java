package tn.fermista.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar;
import javafx.stage.Stage;
import java.io.IOException;

public class DashboardController {
    
    @FXML
    private ButtonBar btn_workbench1121; // Le bouton "Suivis médical"
    
    @FXML
    public void initialize() {
        // Ajouter le gestionnaire d'événements au ButtonBar
        btn_workbench1121.setOnMouseClicked(event -> naviguerVersSuiviMedical(new ActionEvent()));
    }
    
    @FXML
    private void naviguerVersSuiviMedical(ActionEvent event) {
        try {
            // Charger la vue choixvachecollier.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/choixvachecollier.fxml"));
            Parent root = loader.load();
            
            // Obtenir la scène actuelle
            Stage stage = (Stage) btn_workbench1121.getScene().getWindow();
            
            // Créer une nouvelle scène avec la vue chargée
            Scene scene = new Scene(root);
            
            // Définir la nouvelle scène sur le stage
            stage.setScene(scene);
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de choixvachecollier.fxml: " + e.getMessage());
        }
    }
} 