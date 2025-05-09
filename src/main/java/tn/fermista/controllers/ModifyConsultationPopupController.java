package tn.fermista.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.fermista.models.Consultation;
import tn.fermista.services.ServiceConsultation;

import java.sql.Date;
import java.sql.Time;

public class ModifyConsultationPopupController {
    @FXML private TextField nomField;
    @FXML private DatePicker datePicker;
    @FXML private Spinner<Integer> heureSpinner;
    @FXML private Spinner<Integer> minuteSpinner;
    @FXML private TextField lieuField;

    private Consultation consultation;
    private ControlMedicalFront parentController;
    private Stage stage;

    public void setConsultation(Consultation consultation) {
        this.consultation = consultation;
        populateFields();
    }

    public void setParentController(ControlMedicalFront parentController) {
        this.parentController = parentController;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void populateFields() {
        if (consultation != null) {
            nomField.setText(consultation.getNom());
            datePicker.setValue(consultation.getDate().toLocalDate());
            heureSpinner.getValueFactory().setValue(consultation.getHeure().toLocalTime().getHour());
            minuteSpinner.getValueFactory().setValue(consultation.getHeure().toLocalTime().getMinute());
            lieuField.setText(consultation.getLieu());
        }
    }

    @FXML
    private void handleSave() {
        try {
            // Mettre à jour l'objet consultation
            consultation.setNom(nomField.getText());
            consultation.setDate(Date.valueOf(datePicker.getValue()));
            consultation.setHeure(Time.valueOf(String.format("%02d:%02d:00",
                    heureSpinner.getValue(), minuteSpinner.getValue())));
            consultation.setLieu(lieuField.getText());

            // Sauvegarder dans la base de données
            ServiceConsultation serviceConsultation = new ServiceConsultation();
            serviceConsultation.update(consultation);

            // Afficher la confirmation
            showConfirmation();

            // Fermer uniquement la fenêtre de modification
            stage.close();

            // Le rafraîchissement de l'affichage est géré par l'écouteur dans ControlMedicalFront
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la modification de la consultation: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    private void showConfirmation() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Modification réussie");
        alert.setHeaderText(null);
        alert.setContentText("La consultation a été modifiée avec succès.");
        alert.getDialogPane().getStyleClass().add("custom-alert");
        alert.showAndWait();
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
        alert.getDialogPane().getStyleClass().add("custom-alert");
        alert.showAndWait();
    }
}