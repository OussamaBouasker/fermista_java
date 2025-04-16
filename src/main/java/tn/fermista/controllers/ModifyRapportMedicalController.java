package tn.fermista.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.fermista.models.RapportMedical;
import tn.fermista.services.ServiceRapportMedical;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ModifyRapportMedicalController implements Initializable {
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

    private RapportMedical rapportMedical;
    private ServiceRapportMedical serviceRapportMedical;
    private Stage stage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        serviceRapportMedical = new ServiceRapportMedical();
    }

    public void setRapportMedical(RapportMedical rapportMedical) {
        this.rapportMedical = rapportMedical;
        // Remplir les champs avec les données du rapport
        numField.setText(String.valueOf(rapportMedical.getNum()));
        raceField.setText(rapportMedical.getRace());
        historiqueField.setText(rapportMedical.getHistoriqueDeMaladie());
        casMedicalField.setText(rapportMedical.getCasMedical());
        solutionField.setText(rapportMedical.getSolution());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
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

            // Mettre à jour les données du rapport
            rapportMedical.setNum(Integer.parseInt(numField.getText()));
            rapportMedical.setRace(race);
            rapportMedical.setHistoriqueDeMaladie(historiqueField.getText());
            rapportMedical.setCasMedical(casMedicalField.getText());
            rapportMedical.setSolution(solutionField.getText());

            // Mettre à jour dans la base de données
            serviceRapportMedical.update(rapportMedical);

            // Afficher un message de succès
            showAlert("Succès", "Rapport médical modifié avec succès", Alert.AlertType.INFORMATION);

            // Fermer la fenêtre
            stage.close();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la modification du rapport", Alert.AlertType.ERROR);
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Le numéro doit être un nombre entier", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancel() {
        stage.close();
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 