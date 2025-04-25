package tn.fermista.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.fermista.models.Consultation;
import tn.fermista.models.RapportMedical;
import tn.fermista.models.Vache;
import tn.fermista.services.ServiceConsultation;
import tn.fermista.services.ServiceRapportMedical;
import tn.fermista.services.ServiceVache;

import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ComboBox;

public class ModifyConsultationController implements Initializable {
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
    private Consultation consultation;
    private Stage stage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        serviceConsultation = new ServiceConsultation();
        serviceVache = new ServiceVache();
        serviceRapportMedical = new ServiceRapportMedical();

        // Initialiser les heures disponibles
        String[] heures = new String[10];
        for (int i = 8; i < 18; i++) {
            heures[i-8] = String.format("%02d:00", i);
        }
        heureField.getItems().addAll(heures);

        // Charger les listes
        try {
            rapportMedicalComboBox.getItems().addAll(serviceRapportMedical.showAll());
            vacheComboBox.getItems().addAll(serviceVache.showAll());
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors du chargement des données", Alert.AlertType.ERROR);
        }
    }

    public void setConsultation(Consultation consultation) {
        this.consultation = consultation;
        // Remplir les champs avec les données de la consultation
        nomField.setText(consultation.getNom());
        dateField.setValue(consultation.getDate().toLocalDate());
        heureField.setValue(consultation.getHeure().toString().substring(0, 5));
        lieuField.setText(consultation.getLieu());
        rapportMedicalComboBox.setValue(consultation.getRapportMedical());
        vacheComboBox.setValue(consultation.getVache());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleSave() {
        try {
            // Vérifier que tous les champs sont remplis
            if (nomField.getText().isEmpty() || dateField.getValue() == null ||
                heureField.getValue() == null || lieuField.getText().isEmpty()) {
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
            String heure = heureField.getValue();
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
            consultation.setDate(Date.valueOf(dateField.getValue()));
            consultation.setHeure(Time.valueOf(heure + ":00"));
            consultation.setLieu(lieu);
            consultation.setRapportMedical(rapportMedicalComboBox.getValue());
            consultation.setVache(vacheComboBox.getValue());

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
        
        // Ajout de style CSS
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
            getClass().getResource("/styles/alert.css").toExternalForm()
        );
        dialogPane.getStyleClass().add("custom-alert");
        
        // Personnalisation des icônes selon le type d'alerte
        switch (type) {
            case ERROR:
                dialogPane.getStyleClass().add("error-alert");
                break;
            case INFORMATION:
                dialogPane.getStyleClass().add("info-alert");
                break;
            case WARNING:
                dialogPane.getStyleClass().add("warning-alert");
                break;
            case CONFIRMATION:
                dialogPane.getStyleClass().add("confirm-alert");
                break;
        }
        
        alert.showAndWait();
    }

    public void initData(Consultation consultation) {
        this.consultation = consultation;
        
        // Remplir les champs avec les données de la consultation
        nomField.setText(consultation.getNom());
        dateField.setValue(consultation.getDate().toLocalDate());
        heureField.setValue(consultation.getHeure().toString().substring(0, 5));
        lieuField.setText(consultation.getLieu());
        
        // Sélectionner le rapport médical et la vache dans les ComboBox
        if (consultation.getRapportMedical() != null) {
            rapportMedicalComboBox.setValue(consultation.getRapportMedical());
        }
        
        if (consultation.getVache() != null) {
            vacheComboBox.setValue(consultation.getVache());
        }
    }
} 