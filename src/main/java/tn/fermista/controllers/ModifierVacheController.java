package tn.fermista.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import tn.fermista.models.Vache;
import tn.fermista.services.ServiceVache;

public class ModifierVacheController {
    @FXML
    private TextField nameField;
    @FXML
    private TextField ageField;
    @FXML
    private TextField raceField;
    @FXML
    private TextField etatMedicalField;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Label nameError;
    @FXML
    private Label ageError;
    @FXML
    private Label raceError;
    @FXML
    private Label etatError;

    private final ServiceVache serviceVache = new ServiceVache();
    private Vache vache;
    private FrontVacheCard cardController;
    private Runnable onModificationCallback;

    @FXML
    public void initialize() {
        // Ajouter des écouteurs pour la validation en temps réel
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateName(newValue);
        });

        ageField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                ageField.setText(newValue.replaceAll("[^\\d]", ""));
            }
            validateAge(newValue);
        });

        raceField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateRace(newValue);
        });

        etatMedicalField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateEtatMedical(newValue);
        });
    }

    public void setVache(Vache vache, FrontVacheCard cardController) {
        this.vache = vache;
        this.cardController = cardController;
        
        // Remplir les champs avec les données actuelles
        nameField.setText(vache.getName());
        ageField.setText(String.valueOf(vache.getAge()));
        raceField.setText(vache.getRace());
        etatMedicalField.setText(vache.getEtat_medical());

        // Valider les données initiales
        validateName(vache.getName());
        validateAge(String.valueOf(vache.getAge()));
        validateRace(vache.getRace());
        validateEtatMedical(vache.getEtat_medical());
    }

    private boolean validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            nameError.setText("Le nom est obligatoire");
            nameError.setTextFill(Color.RED);
            return false;
        }
        if (name.length() < 2) {
            nameError.setText("Le nom doit contenir au moins 2 caractères");
            nameError.setTextFill(Color.RED);
            return false;
        }
        if (!name.matches("[a-zA-Z\\s]+")) {
            nameError.setText("Le nom ne doit contenir que des lettres");
            nameError.setTextFill(Color.RED);
            return false;
        }
        nameError.setText("");
        return true;
    }

    private boolean validateAge(String age) {
        if (age == null || age.trim().isEmpty()) {
            ageError.setText("L'âge est obligatoire");
            ageError.setTextFill(Color.RED);
            return false;
        }
        try {
            int ageValue = Integer.parseInt(age);
            if (ageValue < 0 || ageValue > 30) {
                ageError.setText("L'âge doit être entre 0 et 30 ans");
                ageError.setTextFill(Color.RED);
                return false;
            }
            ageError.setText("");
            return true;
        } catch (NumberFormatException e) {
            ageError.setText("L'âge doit être un nombre valide");
            ageError.setTextFill(Color.RED);
            return false;
        }
    }

    private boolean validateRace(String race) {
        if (race == null || race.trim().isEmpty()) {
            raceError.setText("La race est obligatoire");
            raceError.setTextFill(Color.RED);
            return false;
        }
        if (race.length() < 2) {
            raceError.setText("La race doit contenir au moins 2 caractères");
            raceError.setTextFill(Color.RED);
            return false;
        }
        if (!race.matches("[a-zA-Z\\s]+")) {
            raceError.setText("La race ne doit contenir que des lettres");
            raceError.setTextFill(Color.RED);
            return false;
        }
        raceError.setText("");
        return true;
    }

    private boolean validateEtatMedical(String etat) {
        if (etat == null || etat.trim().isEmpty()) {
            etatError.setText("L'état médical est obligatoire");
            etatError.setTextFill(Color.RED);
            return false;
        }
        if (etat.length() < 3) {
            etatError.setText("L'état médical doit contenir au moins 3 caractères");
            etatError.setTextFill(Color.RED);
            return false;
        }
        etatError.setText("");
        return true;
    }

    @FXML
    public void onSaveClick() {
        // Valider tous les champs avant la sauvegarde
        boolean isNameValid = validateName(nameField.getText());
        boolean isAgeValid = validateAge(ageField.getText());
        boolean isRaceValid = validateRace(raceField.getText());
        boolean isEtatValid = validateEtatMedical(etatMedicalField.getText());

        if (!isNameValid || !isAgeValid || !isRaceValid || !isEtatValid) {
            showAlert("Erreur de validation", "Veuillez corriger les erreurs avant de sauvegarder.", Alert.AlertType.ERROR);
            return;
        }

        try {
            // Mettre à jour l'objet vache avec les nouvelles valeurs
            vache.setName(nameField.getText().trim());
            vache.setAge(Integer.parseInt(ageField.getText().trim()));
            vache.setRace(raceField.getText().trim());
            vache.setEtat_medical(etatMedicalField.getText().trim());

            // Sauvegarder dans la base de données
            serviceVache.update(vache);

            // Afficher un message de succès
            showAlert("Succès", "La vache a été modifiée avec succès.", Alert.AlertType.INFORMATION);

            // Mettre à jour la carte si elle existe
            if (cardController != null) {
                cardController.updateCard(vache);
            }

            // Exécuter le callback de modification
            if (onModificationCallback != null) {
                onModificationCallback.run();
            }

            // Fermer la fenêtre
            Stage stage = (Stage) saveButton.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            showAlert("Erreur", "Une erreur est survenue lors de la modification : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void onCancelClick() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void setOnModificationCallback(Runnable callback) {
        this.onModificationCallback = callback;
    }
}
