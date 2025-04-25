package tn.fermista.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.fermista.models.RapportMedical;
import tn.fermista.services.ServiceRapportMedical;
import java.sql.SQLException;
import javafx.scene.paint.Color;
import javafx.beans.value.ChangeListener;
import javafx.scene.layout.VBox;

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
    private Label errorLabel;
    @FXML
    private Button saveButton;

    private ServiceRapportMedical serviceRapportMedical;
    private RapportMedical rapportMedical;

    @FXML
    public void initialize() {
        serviceRapportMedical = new ServiceRapportMedical();
        
        // Initialiser le label d'erreur
        errorLabel.setTextFill(Color.RED);
        errorLabel.setVisible(false);
        
        // Ajouter les écouteurs de validation en temps réel
        setupValidationListeners();
    }

    private void setupValidationListeners() {
        // Validation du numéro (nombres uniquement)
        numField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                numField.setText(newValue.replaceAll("[^\\d]", ""));
            }
            validateFields();
        });

        // Validation de la race (lettres et espaces uniquement)
        raceField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-Z\\s]*")) {
                raceField.setText(oldValue);
            }
            validateFields();
        });

        // Validation de la longueur minimale pour les champs texte
        historiqueField.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
        casMedicalField.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
        solutionField.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
    }

    private boolean validateFields() {
        StringBuilder errorMessage = new StringBuilder();
        boolean isValid = true;

        // Validation du numéro
        if (numField.getText().isEmpty()) {
            errorMessage.append("Le numéro est requis\n");
            isValid = false;
        }

        // Validation de la race
        if (raceField.getText().isEmpty()) {
            errorMessage.append("La race est requise\n");
            isValid = false;
        } else if (raceField.getText().length() < 3) {
            errorMessage.append("La race doit contenir au moins 3 caractères\n");
            isValid = false;
        }

        // Validation de l'historique
        if (historiqueField.getText().isEmpty()) {
            errorMessage.append("L'historique est requis\n");
            isValid = false;
        } else if (historiqueField.getText().length() < 10) {
            errorMessage.append("L'historique doit contenir au moins 10 caractères\n");
            isValid = false;
        }

        // Validation du cas médical
        if (casMedicalField.getText().isEmpty()) {
            errorMessage.append("Le cas médical est requis\n");
            isValid = false;
        } else if (casMedicalField.getText().length() < 10) {
            errorMessage.append("Le cas médical doit contenir au moins 10 caractères\n");
            isValid = false;
        }

        // Validation de la solution
        if (solutionField.getText().isEmpty()) {
            errorMessage.append("La solution est requise\n");
            isValid = false;
        } else if (solutionField.getText().length() < 10) {
            errorMessage.append("La solution doit contenir au moins 10 caractères\n");
            isValid = false;
        }

        // Mettre à jour l'interface utilisateur
        errorLabel.setText(errorMessage.toString());
        errorLabel.setVisible(!isValid);
        saveButton.setDisable(!isValid);
        return isValid;
    }

    public void initData(RapportMedical rapportMedical) {
        this.rapportMedical = rapportMedical;
        
        // Remplir les champs avec les données existantes
        numField.setText(String.valueOf(rapportMedical.getNum()));
        raceField.setText(rapportMedical.getRace());
        historiqueField.setText(rapportMedical.getHistoriqueDeMaladie());
        casMedicalField.setText(rapportMedical.getCasMedical());
        solutionField.setText(rapportMedical.getSolution());
        
        // Valider les champs initiaux
        validateFields();
    }

    @FXML
    private void handleSave() {
        try {
            // Validation finale avant la sauvegarde
            if (!validateFields()) {
                return;
            }

            // Mettre à jour les données du rapport
            rapportMedical.setNum(Integer.parseInt(numField.getText()));
            rapportMedical.setRace(raceField.getText());
            rapportMedical.setHistoriqueDeMaladie(historiqueField.getText());
            rapportMedical.setCasMedical(casMedicalField.getText());
            rapportMedical.setSolution(solutionField.getText());

            // Sauvegarder les modifications
            serviceRapportMedical.update(rapportMedical);

            // Afficher un message de succès
            showAlert("Succès", "Rapport médical modifié avec succès", Alert.AlertType.INFORMATION);
            
            // Fermer la fenêtre
            closeWindow();
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la modification du rapport médical", Alert.AlertType.ERROR);
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Le numéro doit être un nombre entier valide", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) numField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        
        // Appliquer le style CSS personnalisé
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
            getClass().getResource("/styles/StyleCalendar.css").toExternalForm()
        );
        dialogPane.getStyleClass().add("custom-alert");
        
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
        }
        
        alert.showAndWait();
    }
} 