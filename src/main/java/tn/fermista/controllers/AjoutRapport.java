package tn.fermista.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.fermista.models.RapportMedical;
import tn.fermista.services.ServiceRapportMedical;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AjoutRapport implements Initializable {
    @FXML
    private TextField numField;

    @FXML
    private TextField raceField;

    @FXML
    private TextField historiqueField;

    @FXML
    private TextField casMedicalField;

    @FXML
    private TextField solutionField;

    @FXML
    private Button submitRapportBtn;

    private ServiceRapportMedical serviceRapportMedical;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        serviceRapportMedical = new ServiceRapportMedical();
        submitRapportBtn.setOnAction(this::handleSubmit);
    }

    @FXML
    private void handleSubmit(ActionEvent event) {
        try {
            // Vérifier que tous les champs sont remplis
            if (numField.getText().isEmpty() || raceField.getText().isEmpty() ||
                historiqueField.getText().isEmpty() || casMedicalField.getText().isEmpty() ||
                solutionField.getText().isEmpty()) {
                showAlert("Erreur", "Veuillez remplir tous les champs", Alert.AlertType.ERROR);
                return;
            }

            // Validation de la race (féminin ou masculin)
            String race = raceField.getText().toLowerCase();
            if (!race.equals("feminin") && !race.equals("masculin")) {
                showAlert("Erreur", "La race doit être 'feminin' ou 'masculin'", Alert.AlertType.ERROR);
                return;
            }

            // Validation de la longueur minimale (10 caractères)
            if (historiqueField.getText().length() < 10) {
                showAlert("Erreur", "L'historique médical doit contenir au moins 10 caractères", Alert.AlertType.ERROR);
                return;
            }
            if (casMedicalField.getText().length() < 10) {
                showAlert("Erreur", "Le cas médical doit contenir au moins 10 caractères", Alert.AlertType.ERROR);
                return;
            }
            if (solutionField.getText().length() < 10) {
                showAlert("Erreur", "La solution doit contenir au moins 10 caractères", Alert.AlertType.ERROR);
                return;
            }

            // Créer un nouveau rapport médical
            RapportMedical rapportMedical = new RapportMedical();
            rapportMedical.setNum(Integer.parseInt(numField.getText()));
            rapportMedical.setRace(race);
            rapportMedical.setHistoriqueDeMaladie(historiqueField.getText());
            rapportMedical.setCasMedical(casMedicalField.getText());
            rapportMedical.setSolution(solutionField.getText());

            // Ajouter le rapport dans la base de données
            serviceRapportMedical.insert(rapportMedical);

            // Afficher un message de succès
            showAlert("Succès", "Rapport médical ajouté avec succès", Alert.AlertType.INFORMATION);

            // Fermer la fenêtre
            Stage stage = (Stage) submitRapportBtn.getScene().getWindow();
            stage.close();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ajout du rapport", Alert.AlertType.ERROR);
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Le numéro doit être un nombre entier", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void ControlMedicalShow(ActionEvent actionEvent) {
        // Implementation for navigation
    }

    public void DashbordTemplate(ActionEvent actionEvent) {
        // Implementation for navigation
    }
}
