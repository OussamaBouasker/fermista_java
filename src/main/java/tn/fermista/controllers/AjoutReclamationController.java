package tn.fermista.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import tn.fermista.models.Reclamation;
import tn.fermista.models.User;
import tn.fermista.services.ServiceReclamation;

import java.sql.SQLException;
import java.time.LocalDateTime;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class AjoutReclamationController {

    @FXML
    private TextField titreField;

    @FXML
    private TextArea descriptionArea;

    private ServiceReclamation serviceReclamation = new ServiceReclamation();

    // Suppose que tu as l'utilisateur courant déjà chargé
    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @FXML
    public void initialize() {
        if (currentUser == null) {
            currentUser = tn.fermista.utils.UserSession.getCurrentUser();
        }
    }

    @FXML
    private void handleSubmit() {
        String titre = titreField.getText();
        String description = descriptionArea.getText();

        if (titre.isEmpty() || description.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Champs vides", "Veuillez remplir tous les champs !");
            return;
        }

        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Utilisateur non connecté", "Impossible de soumettre sans utilisateur !");
            return;
        }

        Reclamation reclamation = new Reclamation();
        reclamation.setTitre(titre);
        reclamation.setDescription(description);
        reclamation.setStatus(Reclamation.STATUS_PENDING);
        reclamation.setDateSoumission(LocalDateTime.now());
        reclamation.setUser(currentUser);

        try {
            serviceReclamation.insert(reclamation);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Réclamation soumise avec succès !");
            clearForm();
            navigateToHomePage();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Erreur lors de l'insertion: " + e.getMessage());
        }
    }

    private void navigateToHomePage() {
        try {
            // Simplified loading of HomePage.fxml using static FXMLLoader.load()
            Parent root = FXMLLoader.load(getClass().getResource("/HomePage.fxml"));
            Stage stage = (Stage) titreField.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de navigation", "Impossible de charger la page d'accueil: " + e.getMessage());
        }
    }

    private void clearForm() {
        titreField.clear();
        descriptionArea.clear();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}