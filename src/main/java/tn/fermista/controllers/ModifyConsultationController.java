package tn.fermista.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.fermista.models.Consultation;
import tn.fermista.services.ServiceConsultation;

import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.scene.control.Alert;

public class ModifyConsultationController implements Initializable {
    @FXML
    private TextField nomField;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField heureField;

    @FXML
    private TextField lieuField;

    private Consultation consultation;
    private ServiceConsultation serviceConsultation;
    private Stage stage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        serviceConsultation = new ServiceConsultation();
    }

    public void setConsultation(Consultation consultation) {
        this.consultation = consultation;
        // Remplir les champs avec les données de la consultation
        nomField.setText(consultation.getNom());
        datePicker.setValue(consultation.getDate().toLocalDate());
        heureField.setText(consultation.getHeure().toString());
        lieuField.setText(consultation.getLieu());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleSave() {
        try {
            // Vérifier que tous les champs sont remplis
            if (nomField.getText().isEmpty() || datePicker.getValue() == null ||
                heureField.getText().isEmpty() || lieuField.getText().isEmpty()) {
                showAlert("Erreur", "Veuillez remplir tous les champs", Alert.AlertType.ERROR);
                return;
            }

            // Validation du nom (doit commencer par une majuscule)
            String nom = nomField.getText();
            if (!nom.matches("[A-Z].*")) {
                showAlert("Erreur", "Le nom doit commencer par une majuscule", Alert.AlertType.ERROR);
                return;
            }

            // Validation de l'heure (entre 9:00 et 18:00)
            String heure = heureField.getText();
            try {
                LocalTime time = LocalTime.parse(heure);
                LocalTime startTime = LocalTime.of(9, 0);
                LocalTime endTime = LocalTime.of(18, 0);
                if (time.isBefore(startTime) || time.isAfter(endTime)) {
                    showAlert("Erreur", "L'heure doit être comprise entre 09:00 et 18:00", Alert.AlertType.ERROR);
                    return;
                }
            } catch (Exception e) {
                showAlert("Erreur", "Format d'heure invalide. Utilisez le format HH:mm", Alert.AlertType.ERROR);
                return;
            }

            // Validation du lieu (minimum 5 caractères)
            String lieu = lieuField.getText();
            if (lieu.length() < 5) {
                showAlert("Erreur", "Le lieu doit contenir au moins 5 caractères", Alert.AlertType.ERROR);
                return;
            }

            // Mettre à jour les données de la consultation
            consultation.setNom(nom);
            consultation.setDate(Date.valueOf(datePicker.getValue()));
            consultation.setHeure(Time.valueOf(LocalTime.parse(heure)));
            consultation.setLieu(lieu);

            // Mettre à jour dans la base de données
            serviceConsultation.update(consultation);

            // Afficher un message de succès
            showAlert("Succès", "Consultation modifiée avec succès", Alert.AlertType.INFORMATION);

            // Fermer la fenêtre
            stage.close();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la modification de la consultation", Alert.AlertType.ERROR);
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