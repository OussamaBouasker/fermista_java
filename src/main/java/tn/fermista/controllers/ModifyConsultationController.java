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
            // Mettre à jour les données de la consultation
            consultation.setNom(nomField.getText());
            consultation.setDate(Date.valueOf(datePicker.getValue()));
            
            // Convertir l'heure du format String vers Time
            String heureStr = heureField.getText();
            LocalTime localTime = LocalTime.parse(heureStr);
            consultation.setHeure(Time.valueOf(localTime));
            
            consultation.setLieu(lieuField.getText());

            // Mettre à jour dans la base de données
            serviceConsultation.update(consultation);

            // Fermer la fenêtre
            stage.close();
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO: Afficher un message d'erreur à l'utilisateur
        }
    }

    @FXML
    private void handleCancel() {
        stage.close();
    }
} 