package tn.fermista.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.fermista.models.RapportMedical;
import tn.fermista.services.ServiceRapportMedical;

import java.sql.SQLException;

public class ModifyRapportMedicalController {
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
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private Stage stage;
    private RapportMedical rapportMedical;
    private final ServiceRapportMedical serviceRapportMedical = new ServiceRapportMedical();

    @FXML
    public void initialize() {
        saveButton.setOnAction(e -> handleSave());
        cancelButton.setOnAction(e -> stage.close());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void initData(RapportMedical rapport) {
        this.rapportMedical = rapport;
        populateFields();
    }

    private void populateFields() {
        if (rapportMedical != null) {
            numField.setText(String.valueOf(rapportMedical.getNum()));
            raceField.setText(rapportMedical.getRace());
            historiqueField.setText(rapportMedical.getHistoriqueDeMaladie());
            casMedicalField.setText(rapportMedical.getCasMedical());
            solutionField.setText(rapportMedical.getSolution());
        }
    }

    @FXML
    private void handleSave() {
        try {
            // Mettre à jour les valeurs du rapport médical
            rapportMedical.setNum(Integer.parseInt(numField.getText()));
            rapportMedical.setRace(raceField.getText());
            rapportMedical.setHistoriqueDeMaladie(historiqueField.getText());
            rapportMedical.setCasMedical(casMedicalField.getText());
            rapportMedical.setSolution(solutionField.getText());

            // Sauvegarder les modifications
            serviceRapportMedical.update(rapportMedical);
            
            // Fermer la fenêtre
            stage.close();
        } catch (SQLException e) {
            e.printStackTrace();
            // Afficher une alerte d'erreur
            showAlert("Erreur", "Erreur lors de la modification du rapport médical");
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Le numéro doit être un nombre valide");
        }
    }

    private void showAlert(String title, String content) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 