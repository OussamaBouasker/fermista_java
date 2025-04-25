package tn.fermista.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import tn.fermista.models.RapportMedical;
import tn.fermista.services.ServiceRapportMedical;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.sql.SQLException;

public class RapportMedicalPopupController {
    @FXML
    private TextField numField;
    @FXML
    private TextField raceField;
    @FXML
    private TextArea historiqueField;
    @FXML
    private TextArea casMedicalField;
    @FXML
    private TextArea solutionField;

    private ServiceRapportMedical serviceRapportMedical;

    @FXML
    public void initialize() {
        serviceRapportMedical = new ServiceRapportMedical();
    }

    @FXML
    private void handleSave() {
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
            closeWindow();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ajout du rapport", Alert.AlertType.ERROR);
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Le numéro doit être un nombre entier", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) numField.getScene().getWindow();
        stage.close();
    }
} 