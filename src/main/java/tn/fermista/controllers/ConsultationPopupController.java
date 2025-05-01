package tn.fermista.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.fermista.models.Consultation;
import tn.fermista.models.Vache;
import tn.fermista.models.RapportMedical;
import tn.fermista.services.ServiceConsultation;
import tn.fermista.services.ServiceVache;
import tn.fermista.services.ServiceRapportMedical;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import javafx.collections.FXCollections;

public class ConsultationPopupController {
    @FXML
    private TextField nomField;
    @FXML
    private DatePicker dateField;
    @FXML
    private ComboBox<String> heureField;
    @FXML
    private TextField lieuField;
    @FXML
    private ComboBox<RapportMedical> rapportMedicalComboBox;
    @FXML
    private ComboBox<Vache> vacheComboBox;

    private ServiceConsultation serviceConsultation;
    private ServiceVache serviceVache;
    private ServiceRapportMedical serviceRapportMedical;
    private LocalDate selectedDate;

    public void setSelectedDate(LocalDate date) {
        this.selectedDate = date;
        if (dateField != null) {
            dateField.setValue(date);
        }
    }

    @FXML
    public void initialize() {
        serviceConsultation = new ServiceConsultation();
        serviceVache = new ServiceVache();
        serviceRapportMedical = new ServiceRapportMedical();

        // Initialiser les heures disponibles (de 8h à 17h)
        String[] heures = new String[10];
        for (int i = 8; i < 18; i++) {
            heures[i-8] = String.format("%02d:00", i);
        }
        heureField.setItems(FXCollections.observableArrayList(heures));

        // Charger la liste des vaches
        try {
            List<Vache> vaches = serviceVache.showAll();
            vacheComboBox.setItems(FXCollections.observableArrayList(vaches));
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de charger la liste des vaches", Alert.AlertType.ERROR);
        }

        // Charger la liste des rapports médicaux
        try {
            List<RapportMedical> rapports = serviceRapportMedical.showAll();
            rapportMedicalComboBox.setItems(FXCollections.observableArrayList(rapports));
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de charger la liste des rapports médicaux", Alert.AlertType.ERROR);
        }

        // Si une date a été sélectionnée, l'utiliser
        if (selectedDate != null) {
            dateField.setValue(selectedDate);
        }
    }

    @FXML
    private void handleSave() {
        try {
            // Validation des champs
            if (nomField.getText().isEmpty() || dateField.getValue() == null ||
                heureField.getValue() == null || lieuField.getText().isEmpty() ||
                rapportMedicalComboBox.getValue() == null || vacheComboBox.getValue() == null) {
                showAlert("Erreur", "Veuillez remplir tous les champs et sélectionner un rapport médical et une vache", Alert.AlertType.ERROR);
                return;
            }

            // Créer la consultation
            Consultation consultation = new Consultation();
            consultation.setNom(nomField.getText());
            consultation.setDate(Date.valueOf(dateField.getValue()));
            consultation.setHeure(Time.valueOf(heureField.getValue() + ":00"));
            consultation.setLieu(lieuField.getText());
            consultation.setRapportMedical(rapportMedicalComboBox.getValue());
            consultation.setVache(vacheComboBox.getValue());

            // Sauvegarder dans la base de données
            serviceConsultation.insert(consultation);

            // Message de succès
            showAlert("Succès", "Consultation ajoutée avec succès", Alert.AlertType.INFORMATION);

            // Fermer la fenêtre
            closeWindow();

        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'ajout de la consultation: " + e.getMessage(), Alert.AlertType.ERROR);
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
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }
} 