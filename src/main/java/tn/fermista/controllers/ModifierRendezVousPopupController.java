package tn.fermista.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;
import tn.fermista.models.RendezVous;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

public class ModifierRendezVousPopupController {

    @FXML
    private DatePicker datePicker;
    
    @FXML
    private Spinner<Integer> heureSpinner;
    
    @FXML
    private Spinner<Integer> minuteSpinner;
    
    private RendezVous rendezVous;
    private ListRendezVous parentController;
    
    @FXML
    public void initialize() {
        // Configure spinners
        SpinnerValueFactory<Integer> heureFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0);
        SpinnerValueFactory<Integer> minuteFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0);
        
        heureSpinner.setValueFactory(heureFactory);
        minuteSpinner.setValueFactory(minuteFactory);
    }
    
    public void setRendezVous(RendezVous rendezVous, ListRendezVous parentController) {
        this.rendezVous = rendezVous;
        this.parentController = parentController;
        
        // Set current values
        if (rendezVous.getDate() != null) {
            datePicker.setValue(rendezVous.getDate().toLocalDate());
        } else {
            datePicker.setValue(LocalDate.now());
        }
        
        if (rendezVous.getHeure() != null) {
            LocalTime time = rendezVous.getHeure().toLocalTime();
            heureSpinner.getValueFactory().setValue(time.getHour());
            minuteSpinner.getValueFactory().setValue(time.getMinute());
        } else {
            heureSpinner.getValueFactory().setValue(9);
            minuteSpinner.getValueFactory().setValue(0);
        }
    }
    
    @FXML
    private void handleModifier() {
        try {
            // Get values from controls
            LocalDate date = datePicker.getValue();
            int heure = heureSpinner.getValue();
            int minute = minuteSpinner.getValue();
            
            // Validate date (not in the past)
            if (date.isBefore(LocalDate.now())) {
                showAlert("Erreur", "La date ne peut pas être dans le passé.");
                return;
            }
            
            // Create new Date and Time objects
            Date newDate = Date.valueOf(date);
            Time newTime = Time.valueOf(LocalTime.of(heure, minute));
            
            // Update the rendez-vous
            rendezVous.setDate(newDate);
            rendezVous.setHeure(newTime);
            
            // Call the parent controller to update the database
            parentController.updateRendezVous(rendezVous);
            
            // Close the popup
            closePopup();
            
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur est survenue lors de la modification: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleAnnuler() {
        closePopup();
    }
    
    private void closePopup() {
        Stage stage = (Stage) datePicker.getScene().getWindow();
        stage.close();
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 