package tn.fermista.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.fermista.models.RapportMedical;
import tn.fermista.services.ServiceRapportMedical;

public class ModifyRapportMedicalPopupController {
    @FXML private TextField numField;
    @FXML private TextField raceField;
    @FXML private TextArea historiqueField;
    @FXML private TextArea casMedicalField;
    @FXML private TextArea solutionField;

    private RapportMedical rapportMedical;
    private ControlMedicalFront parentController;
    private Stage stage;

    public void setRapportMedical(RapportMedical rapportMedical) {
        this.rapportMedical = rapportMedical;
        populateFields();
    }

    public void setParentController(ControlMedicalFront parentController) {
        this.parentController = parentController;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
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
            // Mettre à jour l'objet rapport médical
            rapportMedical.setNum(Integer.parseInt(numField.getText()));
            rapportMedical.setRace(raceField.getText());
            rapportMedical.setHistoriqueDeMaladie(historiqueField.getText());
            rapportMedical.setCasMedical(casMedicalField.getText());
            rapportMedical.setSolution(solutionField.getText());

            // Sauvegarder dans la base de données
            ServiceRapportMedical serviceRapportMedical = new ServiceRapportMedical();
            serviceRapportMedical.update(rapportMedical);

            // Afficher la confirmation
            showConfirmation();

            // Fermer uniquement la fenêtre de modification
            stage.close();

            // Le rafraîchissement de l'affichage est géré par l'écouteur dans ControlMedicalFront
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la modification du rapport médical: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    private void showConfirmation() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Modification réussie");
        alert.setHeaderText(null);
        alert.setContentText("Le rapport médical a été modifié avec succès.");
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