package tn.fermista.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import java.io.IOException;

public class ModifRendezVous {
    
    @FXML
    private Button btn_workbench1121; // Bouton Suivis médical
    
    @FXML
    private Button btnVache; // Bouton VACHE
    
    @FXML
    private Button btnCollier; // Bouton COLLIER

    @FXML
    public void initialize() {
        // Le contrôleur est initialisé
    }

    @FXML
    private void naviguerVersListeVache() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeVache.fxml"));
            Parent root = loader.load();
            Scene scene = btnVache.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur de navigation");
            alert.setContentText("Impossible d'ouvrir la liste des vaches : " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void naviguerVersListeCollier() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeCollier.fxml"));
            Parent root = loader.load();
            Scene scene = btnCollier.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur de navigation");
            alert.setContentText("Impossible d'ouvrir la liste des colliers : " + e.getMessage());
            alert.showAndWait();
        }
    }
} 