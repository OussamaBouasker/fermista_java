package tn.fermista.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.stage.Modality;
import tn.fermista.models.Collier;
import tn.fermista.services.ServiceCollier;

import java.util.Optional;

public class FrontCollierCard {
    @FXML
    private Label referenceLabel;
    @FXML
    private Label tailleLabel;
    @FXML
    private Label temperatureLabel;
    @FXML
    private Label agitationLabel;
    @FXML
    private Label vacheLabel;
    @FXML
    private VBox card;

    private Collier collier;
    private final ServiceCollier serviceCollier = new ServiceCollier();
    private Runnable onModificationCallback;

    public void setOnModificationCallback(Runnable callback) {
        this.onModificationCallback = callback;
    }

    public void setCollier(Collier collier) {
        this.collier = collier;
        updateCardDisplay();
    }

    private void updateCardDisplay() {
        referenceLabel.setText("Réf: " + collier.getReference());
        tailleLabel.setText(collier.getTaille());
        temperatureLabel.setText(String.format("%.1f°C", collier.getValeurTemperature()));
        agitationLabel.setText(String.format("%.1f", collier.getValeurAgitation()));
        vacheLabel.setText(collier.getVache() != null ? collier.getVache().getName() : "Non assigné");
    }

    @FXML
    public void handleModifier() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/ModifierCollierFront.fxml"));
            javafx.scene.control.DialogPane dialogPane = loader.load();
            ModifierCollierFrontController controller = loader.getController();
            controller.setCollier(collier);

            javafx.scene.control.Dialog<javafx.scene.control.ButtonType> dialog = new javafx.scene.control.Dialog<>();
            dialog.setTitle("Modifier le collier");
            dialog.setDialogPane(dialogPane);
            dialog.initModality(Modality.APPLICATION_MODAL);

            java.util.Optional<javafx.scene.control.ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
                // Mettre à jour les valeurs du collier
                collier.setReference(controller.getReference());
                collier.setTaille(controller.getTaille());
                collier.setValeurTemperature(Double.parseDouble(controller.getTemperature()));
                collier.setValeurAgitation(Double.parseDouble(controller.getAgitation()));
                collier.setVache(controller.getSelectedVache());
                // Sauvegarder les modifications
                serviceCollier.update(collier);
                // Mettre à jour l'affichage
                updateCardDisplay();
                // Notifier le parent
                if (onModificationCallback != null) {
                    onModificationCallback.run();
                }
                // Afficher un message de succès
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText(null);
                alert.setContentText("Le collier a été modifié avec succès.");
                alert.showAndWait();
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors de l'ouverture de la fenêtre de modification : " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void handleDelete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer le collier");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer le collier " + collier.getReference() + " ?");
        alert.initModality(Modality.APPLICATION_MODAL);

        ((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setText("Supprimer");
        ((Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("Annuler");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                serviceCollier.delete(collier);
                card.setVisible(false);
                card.setManaged(false);

                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Succès");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Le collier " + collier.getReference() + " a été supprimé avec succès.");
                successAlert.show();

                if (onModificationCallback != null) {
                    onModificationCallback.run();
                }
            } catch (Exception e) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Erreur");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Une erreur est survenue lors de la suppression : " + e.getMessage());
                errorAlert.show();
            }
        }
    }

    @FXML
    public void handleStatus() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Status du Collier");
        alert.setHeaderText(collier.getReference());
        alert.setContentText(String.format(
            "Référence: %s\nTaille: %s\nVache: %s\nTempérature: %.1f°C\nAgitation: %.1f",
            collier.getReference(),
            collier.getTaille(),
            collier.getVache() != null ? collier.getVache().getName() : "Non assigné",
            collier.getValeurTemperature(),
            collier.getValeurAgitation()
        ));
        
        alert.getDialogPane().setStyle("-fx-background-color: white;");
        ((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setText("Fermer");
        
        alert.showAndWait();
    }
} 